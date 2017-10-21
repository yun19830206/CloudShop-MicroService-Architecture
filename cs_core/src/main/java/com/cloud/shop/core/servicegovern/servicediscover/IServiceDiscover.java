package com.cloud.shop.core.servicegovern.servicediscover;

import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * 服务发现接口
 * Created by ChengYun on 2017/7/31 Vesion 1.0
 */
public interface IServiceDiscover {

    /**
     * 根据服务名称，获取服务
     * @param serviceName 服务的名称
     * @return 服务实例
     * @throws Exception 异常
     */
    List<ServiceInstance<InstanceDetails>> getInstancesByName(String serviceName) throws Exception;

    /**
     * 监控从CachedServiceMap获得所有服务实例,与getAllServiceFromZoookeeper()的区别在前者是Query过的服务，后者是实时查看所有服务<br/>
     * 重点验证已经被缓存的CachedServiceMap中的服务集群机器有变动，获得服务是否有变动<br/>
     * @return Map 从CachedServiceMap获得所有服务实例
     */
    Map<String,List<ServiceInstance<InstanceDetails>>> getAllServiceFromCachedServiceMap();

    /**
     * 监控从Zookeeper实时获得所有服务实例,getAllServiceFromCachedServiceMap()的区别在前者是实时查看所有服务，后者是Cached过的服务<br/>
     * 重点验证已经被缓存的CachedServiceMap中的服务集群机器有变动，获得服务是否有变动<br/>
     * @return Map 从Zookeeper实时获得所有服务实例
     */
    Map<String,List<ServiceInstance<InstanceDetails>>> getAllServiceFromZoookeeper();

}
