package com.cloud.shop.core.servicegovern.serviceregister;


import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 服务注册接口
 * Created by ChengYun on 2017/6/1 Vesion 1.0
 */
public interface IRegisterService {

    /**
     * 注册一个服务
     * @param context         tomcat 的 context, 例如 users
     * @param listenAddress   tomcat的监听地址，例如：127.0.0.1:8081
     * @param controllerPath  待注册服务的Controller类的RequestMapping的路径, 可能为空
     * @param controllerName  待注册服务的类的名称
     * @param methodPath      待注册服务的Controller类方法的RequestMapping的路径, 一般不可能为空
     * @param methodName      待注册服务的类的方法名称
     * @param microServiceDesc 方法上的 {@link MicroServiceDesc} 注解内容
     * @param httpType         RequestMapping注解上面的Http 类型方法上的 {@link RequestMapping} 注解内容
     */
    void register(String context, String listenAddress,
                  String controllerPath, String controllerName,
                  String methodPath, String methodName,
                  MicroServiceDesc microServiceDesc,
                  RequestMethod httpType);
}