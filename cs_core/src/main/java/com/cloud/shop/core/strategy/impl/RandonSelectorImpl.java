package com.cloud.shop.core.strategy.impl;

import com.cloud.shop.core.strategy.ISelector;
import com.cloud.shop.core.utils.CSCollectionUtils;
import com.cloud.shop.core.utils.CSIpUtils;
import com.cloud.shop.core.utils.CSStringUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对Zookeeper里面ServerName服务实例的List，随机选择器(模拟测试时使用，不然都调用本地服务了)
 * Created by ChengYun on 2017/9/17 Version 1.0
 */
@Component
public class RandonSelectorImpl implements ISelector{

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

        //本地没有匹配上，用随机数返回
        int indexNumber = Math.abs(index.getAndIncrement());
        return all.get(indexNumber % all.size());
    }
}
