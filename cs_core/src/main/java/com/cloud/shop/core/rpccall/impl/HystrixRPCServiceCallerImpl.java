package com.cloud.shop.core.rpccall.impl;

import com.cloud.shop.core.monitor.servicerpcmonitor.IServicePRCMonitor;
import com.cloud.shop.core.rpccall.IRPCServiceCall;
import com.cloud.shop.core.servicegovern.servicediscover.IServiceDiscover;
import com.cloud.shop.core.serviceisolation.HystrixRPCCommand;
import com.cloud.shop.core.strategy.ISelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * 远程服务调用实现类：支持服务隔离方式Hystrix<br/>
 *  1:不提供异步调用方式，如果是异步调用属于事件型通知，通过MQ方式实现。
 *  2:不提供get\post的区别，由服务注册、发现里面的服务Get/Post属性自动调用，没有属性默认用Post方式
 * Created by ChengYun on 2017/8/19 Version 1.0
 */
@Component
public class HystrixRPCServiceCallerImpl implements IRPCServiceCall {
    private final static Logger logger = LoggerFactory.getLogger(HystrixRPCServiceCallerImpl.class);

    /** 用于真正RPC调用的工具类 */
    @Autowired
    private RestTemplate restTemplate;

    /** 监控RPC流量的结果类 */
    @Autowired
    private IServicePRCMonitor servicePRCMonitor;

    /** 在Zookeeper中服务发现客户端工具端 */
    @Autowired
    private IServiceDiscover serviceDiscover ;

    /** 针对同一个服务的多个实例，获得一个合适的实例使用 */
    @Autowired
    private ISelector selector;

    /**
     * 同步服务调用, 支持服务不成功默认值
     * @param serviceName  服务方法
     * @param request      请求消息体. 可以为空
     * @param responseType 响应类型
     * @param fallbackRsp  降级响应
     * @return 响应对象 T
     */
    @Override
    public <T> T call(String serviceName, Object request, Class<T> responseType, T fallbackRsp) {
        HystrixRPCCommand<T> hytrixRPCCommand = new HystrixRPCCommand<T>(serviceName, request, responseType, fallbackRsp, restTemplate, serviceDiscover, servicePRCMonitor, selector);
        try {
            return hytrixRPCCommand.queue().get();
        } catch (Exception e) {
//            logger.error("HystrixRPCServiceCallerImpl Call Service[" + serviceName + "] exception,", e);
            return fallbackRsp ;
        }
    }

    /**
     * 本bean实例化好之后，初始化各个属性
     */
    @PostConstruct
    public void initProperty(){
        //目前没有什么需要初始化的，并且把RestTemplete的常用方法practice中做详细描述
    }
}
