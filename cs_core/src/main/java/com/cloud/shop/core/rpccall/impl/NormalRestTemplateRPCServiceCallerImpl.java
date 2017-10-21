package com.cloud.shop.core.rpccall.impl;

import com.cloud.shop.core.monitor.servicerpcmonitor.IServicePRCMonitor;
import com.cloud.shop.core.rpccall.IRPCServiceCall;
import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.cloud.shop.core.servicegovern.servicediscover.IServiceDiscover;
import com.cloud.shop.core.strategy.ISelector;
import com.cloud.shop.core.utils.CSRestTemplateUtils;
import com.cloud.shop.enums.EnumRPCCallResult;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 远程服务调用实现类：普通RestTemplate调用方式(没有服务隔离功能)，本类的主要目的是复现没有服务隔离时候，把其他其他服务拖垮的问题<br/>
 *  1:不提供异步调用方式，如果是异步调用属于事件型通知，通过MQ方式实现。
 *  2:不提供get\post的区别，由服务注册、发现里面的服务Get/Post属性自动调用，没有属性默认用Post方式
 * Created by ChengYun on 2017/8/19 Version 1.0
 */
@Component
public class NormalRestTemplateRPCServiceCallerImpl implements IRPCServiceCall {
    private final static Logger log = LoggerFactory.getLogger(NormalRestTemplateRPCServiceCallerImpl.class);

    /** 用于真正RPC调用的工具类 */
    @Autowired
    private RestTemplate restTemplate;

    /** 在Zookeeper中服务发现客户端工具端 */
    @Autowired
    private IServiceDiscover serviceDiscover ;

    /** 监控RPC流量的结果类 */
    @Autowired
    private IServicePRCMonitor servicePRCMonitor;

    /** 针对同一个服务的多个实例，获得一个合适的实例使用 */
    @Autowired
    private ISelector selector;

    /**
     * 同步服务调用, 支持服务不成功默认值
     * @param serviceName  服务方法
     * @param request      请求消息体. 可以为空
     * @param responseType 响应类型
     * @return 响应对象 T
     */
    public <T> T call(String serviceName, Object request, Class<T> responseType, T fallbackRsp) {

        long callStartTime = System.currentTimeMillis();
        //1：通过serviceDiscover拿到serviceName的服务实例InstanceDetails
        ServiceInstance<InstanceDetails> serviceInstance = null ;
        InstanceDetails instanceDetails = null ;
        try {
            List<ServiceInstance<InstanceDetails>> allServiceInstance = serviceDiscover.getInstancesByName(serviceName);
            serviceInstance = selector.getInstance(allServiceInstance);
        } catch (Exception e) {
            log.error("Normal RestTemplate Call find service["+serviceName+"] from IServiceDiscover Exception",e);
        }
        if(null != serviceInstance && null != serviceInstance.getPayload()){
            instanceDetails = serviceInstance.getPayload();
        }else{
            //没有找到服务，则不用发起服务调用，直接记录服务调用失败监控，返回null
            log.info("can not get "+ serviceName + " result from Normal RestTemplate .");
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    null, EnumRPCCallResult.Error_UrlNull);
            return null;
        }

        //2：根据发起服务调用(到此为止，说明serviceInstance 和 instanceDetails都不为空)
        T t = null;
        try {
            t = getOrPost(instanceDetails,request,responseType);
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    instanceDetails.getRequestUrl(),EnumRPCCallResult.Sucess);
        } catch (Exception e) {
            log.error("Normal RestTemplate Call exceute service["+serviceName+"] exception ",e);
            EnumRPCCallResult callResult = EnumRPCCallResult.Error_RestTemplate_Execute_Exception;
            callResult.setResultTypeSubDesc(e.getMessage());
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    instanceDetails.getRequestUrl(),callResult);
        }
        return t;
    }

    /**
     * 根据服务实例instanceDetails, 参数request，获得Http请求的返回值responseType
     * @param instanceDetails
     * @param request
     * @param responseType
     * @return
     */
    private <T> T getOrPost(InstanceDetails instanceDetails, Object request, Class<T> responseType) {
        T t = null ;
        if(null == instanceDetails || null == instanceDetails.getRequestUrl()){
            return t;
        }
        if(RequestMethod.GET.equals(instanceDetails.getRequestMethod())){
            t = CSRestTemplateUtils.get(restTemplate,instanceDetails.getRequestUrl(),request,responseType);
        }else{
            t = CSRestTemplateUtils.post(restTemplate,instanceDetails.getRequestUrl(),request,responseType);
        }
        return t;
    }

}
