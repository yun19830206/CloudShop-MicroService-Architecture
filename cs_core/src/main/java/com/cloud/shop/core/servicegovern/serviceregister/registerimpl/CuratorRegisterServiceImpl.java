package com.cloud.shop.core.servicegovern.serviceregister.registerimpl;

import com.cloud.shop.core.servicegovern.CuratorFrameworkFactoryBean;
import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.cloud.shop.core.servicegovern.serviceregister.IRegisterService;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;

/**
 * 提供服务注册方法的实现类
 * Created by ChengYun on 2017/6/4 Version 1.0
 */
@Component
public class CuratorRegisterServiceImpl implements IRegisterService {

    private final static Logger logger = LoggerFactory.getLogger(CuratorRegisterServiceImpl.class);

    /** 从Spring配置文件中获取Curator服务注册的根目录,一般是在:/CloudShop/MicroService */
    @Value("${zookeeper.serviceRootPath:/MicroService}")
    private String registerServiceRootPath ;

    /** CuratorFrameworkFactory工厂Bean */
    private final CuratorFrameworkFactoryBean curatorFrameworkFactoryBean ;

    /** 服务发现动作对象 */
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;

    /** Spring推荐的自动注入单类成员变量类的方式 */
    @Autowired
    public CuratorRegisterServiceImpl(CuratorFrameworkFactoryBean curatorFrameworkFactoryBean) throws Exception{
        this.curatorFrameworkFactoryBean = curatorFrameworkFactoryBean;
    }

    /** 构建Curator服务注册实现类 (不能在构造函数中调用，不然会有很多属性拿不到) */
    @PostConstruct
    private void initCuratorRegisterServiceImpl() throws Exception{

        //创建一个服务发现动作对象
        JsonInstanceSerializer<InstanceDetails> jsonInstanceSerializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(curatorFrameworkFactoryBean.getCuratorFramework())
                .serializer(jsonInstanceSerializer)
                .basePath(registerServiceRootPath).build();
        serviceDiscovery.start();
        logger.info("Zookeeper Curator ServiceDiscovery(in register) Start Success");
    }

    /**
     * 注册一个服务
     * @param context         tomcat 的 context, 例如 users
     * @param listenAddress   tomcat的监听地址，例如：127.0.0.1:8081
     * @param controllerPath  待注册服务的Controller类的RequestMapping的路径, 可能为空
     * @param controllerName  待注册服务的类的名称
     * @param methodPath      待注册服务的Controller类方法的RequestMapping的路径, 一般不可能为空
     * @param methodName      待注册服务的类的方法名称
     * @param microServiceDesc 方法上的 {@link MicroServiceDesc} 注解内容
     */
    public void register(String context, String listenAddress, String controllerPath, String controllerName, String methodPath, String methodName, MicroServiceDesc microServiceDesc, RequestMethod httpType) {
        boolean degrade = microServiceDesc != null && microServiceDesc.degrade();

        if(null != microServiceDesc){
            InstanceDetails instanceDetails = new InstanceDetails.InstanceDetailsBuilder(listenAddress,context)
                    .controllerName(controllerName).controllerPath(controllerPath)
                    .methodName(methodName).methodPath(methodPath)
                    .serviceName(microServiceDesc.serviceName())
                    .degrade(degrade).requestMethod(httpType).build();

            //创建一个服务对象：指定服务的 地址，端口，名称
            try {
                ServiceInstance<InstanceDetails> serviceInstance = ServiceInstance.<InstanceDetails>builder()
                        .name(instanceDetails.getServiceName())
                        .address(instanceDetails.getLinstenAddress().split(":")[0])
                        .port(Integer.parseInt(instanceDetails.getLinstenAddress().split(":")[1]))
                        .payload(instanceDetails)
                        .build();
                this.serviceDiscovery.registerService(serviceInstance);
            }catch (Exception e){
                logger.error("Curator register Service error. node[controllerName="+controllerName+", methodName="+methodName+"].", e);
            }
        }else{
            logger.error("MicroServiceDesc is null, Can't not create zookeeper node[controllerName="+controllerName+", methodName="+methodName+"].");
        }
    }
}
