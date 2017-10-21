package com.cloud.shop.core.strategy.impl;

import com.cloud.shop.core.strategy.ISelector;
import com.cloud.shop.core.utils.CSCollectionUtils;
import com.cloud.shop.core.utils.CSIpUtils;
import com.cloud.shop.core.utils.CSStringUtils;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对Zookeeper里面ServerName服务实例的List，选择本地优先、Randon的数据返回(实际项目中使用较多)
 * Created by ChengYun on 2017/8/29 Version 1.0
 */
public class LocalRandonSelectorImpl implements ISelector{

    private AtomicInteger index = new AtomicInteger(0);

    /**
     * 针对Zookeeper里面ServerName服务实例的List，选择本地优先、Randon的数据返回
     * @param all T的结合
     * @param <T> 泛型返回结果
     * @return T
     */
    @Override
    public <T> T getInstance(List<T> all) {
        if(null == all || CSCollectionUtils.collectionIsEmpty(all)){
            return null;
        }

        //优先匹配服务
        if(all.get(0) instanceof ServiceInstance<?>){
            String localIPAddress = CSIpUtils.getLocalIpAddress();
            for(T instance : all){
                ServiceInstance<?> serviceInstance = (ServiceInstance<?>) instance;
                if(CSStringUtils.equals(localIPAddress,serviceInstance.getAddress())){
                    return instance;
                }
            }
        }

        //本地没有匹配上，用随机数返回
        int indexNumber = Math.abs(index.getAndIncrement());
        return all.get(indexNumber % all.size());
    }
}
