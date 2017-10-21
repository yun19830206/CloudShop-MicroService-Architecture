package com.cloud.shop.core.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * IP地址工具类
 * Created by ChengYun on 2017/6/9 Vesion 1.0
 */
public class CSIpUtils {

    private static String localIpAddress ;

    private CSIpUtils(){
        /** 不允许New 自己 */
    }

    /** 获得本机Ip地址 */
    public static String getLocalIpAddress(){
        if(null == localIpAddress){
            localIpAddress = getLocalIpAddressPrivate();
        }
        return localIpAddress;
    }

    /** 获得本机Ip地址:具体实现方法 */
    private static String getLocalIpAddressPrivate() {
        DatagramSocket sock = null;
        String localIP = "127.0.0.1";
        try {
            SocketAddress socketAddress = new InetSocketAddress( InetAddress.getByName("1.2.3.4"), 1);
            sock = new DatagramSocket();
            sock.connect(socketAddress);
            localIP = sock.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            sock.disconnect();
            sock.close();
            sock = null;
        }
        return localIP;
    }
}
