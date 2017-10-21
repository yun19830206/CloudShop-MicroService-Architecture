package com.cloud.shop.core.servicegovern.servicediscover.discoverimpl;

import com.cloud.shop.core.servicegovern.CuratorFrameworkFactoryBean;
import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.cloud.shop.core.servicegovern.servicediscover.IServiceDiscover;
import com.cloud.shop.core.utils.CSCollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 服务发现接口实现类
 * Created by ChengYun on 2017/7/31 Version 1.0
 */
@Component
public class CuratorServiceDiscoverImpl implements IServiceDiscover {

    private final Logger logger = LoggerFactory.getLogger(CuratorServiceDiscoverImpl.class);

    /** 创建带名称的线程工厂 */
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("Curator-ServiceCache-Thread-%d").build();
    /** 用于独立在Zookeeper中发现服务的线程池管理 */
    private ExecutorService cachedServiceExecutors ;

    /** CuratorFrameworkFactory工厂Bean，用于提供核心的CuratorFramework */
    private final CuratorFrameworkFactoryBean curatorFrameworkFactoryBean ;

    /** curator用户服务发现的工具类 */
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    /** 服务发现结果的缓存结果：<serviceName,ServiceCache<服务>> */
    private Map<String,ServiceCache<InstanceDetails>> cachedServiceMap = Maps.newHashMap();
    /** 对于所有已经开启的缓存服务发现工具，都需要在引用结束是关闭掉 */
    private List<Closeable> needCloseCacheServiceList = Lists.newArrayList();

    /** 共有成员变量，用于控制在高并发的时候，获得Zookeeper的CacheService串行 */
    private final Object lock;

    /** 从Spring配置文件中获取Curator服务注册的根目录,一般是在:/CloudShop/MicroService */
    @Value("${zookeeper.serviceRootPath:/MicroService}")
    private String registerServiceRootPath ;

    /** Spring推荐的自动注入单类成员变量类的方式 */
    @Autowired
    public CuratorServiceDiscoverImpl(CuratorFrameworkFactoryBean curatorFrameworkFactoryBean) throws Exception{
        this.curatorFrameworkFactoryBean = curatorFrameworkFactoryBean;
        this.lock = new Object();
    }

    /** 初始化 服务发现实现类(不能在构造函数中调用，不然会有很多属性拿不到) */
    @PostConstruct
    private void initCuratorServiceDiscover() throws Exception{
        //初始化是有成员变量
        this.cachedServiceExecutors = Executors.newFixedThreadPool(30, namedThreadFactory);
        JsonInstanceSerializer<InstanceDetails> jsonInstanceSerializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(curatorFrameworkFactoryBean.getCuratorFramework())
                .basePath(registerServiceRootPath)
                .serializer(jsonInstanceSerializer)
                .build();
        serviceDiscovery.start();
        logger.info("Zookeeper Curator ServiceDiscovery(in discover) Start Success");
    }



    /**
     * 根据服务名称，获取服务
     * @param serviceName 服务的名称
     * @return 服务实例
     * @throws Exception 异常
     */
    public List<ServiceInstance<InstanceDetails>> getInstancesByName(String serviceName) throws Exception {

        //从本地缓存中获得上次获得List<ServiceInstance<InstanceDetail>>,如果存在就使用，不存在就通过Curator在Zookeeper中查询一下
        ServiceCache<InstanceDetails> cacheService = cachedServiceMap.get(serviceName);
        if(null == cacheService){
            synchronized (lock){
                cacheService = cachedServiceMap.get(serviceName);
                if(null == cacheService){
                    cacheService = serviceDiscovery.serviceCacheBuilder().name(serviceName)
                            .executorService(this.cachedServiceExecutors).build();
                    cacheService.start();
                    needCloseCacheServiceList.add(cacheService);
                    cachedServiceMap.put(serviceName,cacheService);
                }
            }
        }
        return cacheService.getInstances();
    }

    /**
     * 监控从CachedServiceMap获得所有服务实例,与getAllServiceFromZoookeeper()的区别在前者是Query过的服务，后者是实时查看所有服务<br/>
     * 重点验证已经被缓存的CachedServiceMap中的服务集群机器有变动，获得服务是否有变动(20170806验证过，是准实时变动的)<br/>
     * @return Map 从CachedServiceMap获得所有服务实例
     */
    public Map<String, List<ServiceInstance<InstanceDetails>>> getAllServiceFromCachedServiceMap() {
        Map<String, List<ServiceInstance<InstanceDetails>>> allCachedServiceMap = Maps.newHashMap();
        for(Map.Entry<String,ServiceCache<InstanceDetails>> _oneEntry:cachedServiceMap.entrySet()){
            allCachedServiceMap.put(_oneEntry.getKey(),_oneEntry.getValue().getInstances());
        }
        return allCachedServiceMap;
    }

    /**
     * 监控从Zookeeper实时获得所有服务实例,getAllServiceFromCachedServiceMap()的区别在前者是实时查看所有服务，后者是Cached过的服务<br/>
     * 重点验证已经被缓存的CachedServiceMap中的服务集群机器有变动，获得服务是否有变动(20170806验证过，是准实时变动的)<br/>
     * @return Map 从Zookeeper实时获得所有服务实例
     */
    public Map<String, List<ServiceInstance<InstanceDetails>>> getAllServiceFromZoookeeper() {
        Map<String, List<ServiceInstance<InstanceDetails>>> allServiceInZookeeper = Maps.newHashMap();

        //获得所有注册过的服务名称
        Collection<String> serviceNames = null ;
        try {
            serviceNames = serviceDiscovery.queryForNames();
        } catch (Exception e) {
            //e.printStackTrace();  通过如下的方式，可以打印堆栈异常的
            logger.error("serviceDiscovery.queryForNames() exception：", e);
        }
        if(CSCollectionUtils.collectionIsEmpty(serviceNames)){
            logger.error("can not find any services from zookeeper:" + curatorFrameworkFactoryBean.getConnectString());
            return allServiceInZookeeper;
        }

        for(String serviceName : serviceNames){
            Collection<ServiceInstance<InstanceDetails>> serviceInstanceCollection;
            try {
                serviceInstanceCollection = serviceDiscovery.queryForInstances(serviceName);
                if(CSCollectionUtils.collectionIsEmpty(serviceInstanceCollection)){
                    logger.debug("can`t find any service instance by name[" + serviceName + "].");
                }else{
                    allServiceInZookeeper.put(serviceName,(List)serviceInstanceCollection);
                }

            } catch (Exception e) {
                logger.warn("query instances for name[" + serviceName + "] have exception：", e.getMessage());
                e.printStackTrace();
            }
        }

        return allServiceInZookeeper ;
    }


    /** 在本工具消亡前，关闭已经打开的CacheService连接 */
    @PreDestroy
    public synchronized void close(){
        for(Closeable closeable : needCloseCacheServiceList){
            CloseableUtils.closeQuietly(closeable);
        }
    }
}
