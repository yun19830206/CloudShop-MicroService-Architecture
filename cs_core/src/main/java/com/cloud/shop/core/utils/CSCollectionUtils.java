package com.cloud.shop.core.utils;

import org.springframework.util.CollectionUtils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

/**
 * 集合工具类
 * Created by ChengYun on 2017/6/9 Vesion 1.0
 */
public class CSCollectionUtils {

    private CSCollectionUtils(){
        /** 不允许New 自己 */
    }

    /**
     * 判断集合是否为空
     * @return
     */
    public static boolean collectionIsEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }

}
