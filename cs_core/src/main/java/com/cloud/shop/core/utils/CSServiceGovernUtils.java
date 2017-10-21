package com.cloud.shop.core.utils;

import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * Curator链接的Zookeeper中的服务治理工具类
 * Created by ChengYun on 2017/9/9 Version 1.0
 */
public class CSServiceGovernUtils {

    private CSServiceGovernUtils(){
        /** 工具类不允许New 自己 */
    }

    /**
     * 获得入参 服务注册详细结果 的简短注册结果
     * @return
     */
    public static Map<String,List<String>> getSimpleServiceList(Map<String,List<ServiceInstance<InstanceDetails>>> allDetailServiceList){
        Map<String,List<String>> simpleServiceList = Maps.newHashMap();
        if(null == allDetailServiceList || allDetailServiceList.size()<1){
            return simpleServiceList ;
        }
        for(Map.Entry<String,List<ServiceInstance<InstanceDetails>>> entry : allDetailServiceList.entrySet()){
            List<String> serviceNameList = Lists.newArrayList();
            List<ServiceInstance<InstanceDetails>> serviceList = entry.getValue();
            if(null != serviceList && serviceList.size()>0){
                for(ServiceInstance<InstanceDetails> serviceInstance : serviceList){
                    serviceNameList.add(serviceInstance.getPayload().getRequestUrl());
                }
            }
            simpleServiceList.put(entry.getKey(),serviceNameList);
        }
        return simpleServiceList;
    }


}
