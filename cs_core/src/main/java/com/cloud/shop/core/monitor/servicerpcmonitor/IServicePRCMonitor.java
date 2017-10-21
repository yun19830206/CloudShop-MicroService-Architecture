package com.cloud.shop.core.monitor.servicerpcmonitor;

import com.cloud.shop.common.RPCCallResult;
import com.cloud.shop.enums.EnumRPCCallResult;

import java.util.Map;

/**
 * 模拟监控RPC流量的结果类(实际情况会把监控数据异步到数据库当中，以便于单独模块实时查看)
 * Created by ChengYun on 2017/8/7 Version 1.0
 */
public interface IServicePRCMonitor {

    /**
     * 每次服务调用结束，保存调用结果
     * @param callResult 服务调用接口
     */
    void addAudit(RPCCallResult callResult);

    /**
     * 每次服务调用结束，保存调用结果(可以直接用参数简单构建)
     * @param callStartTime 服务调用开始时间
     * @param userdTime  服务调用耗时
     * @param serviceName  服务名称
     * @param stackTrace  服务调用堆栈
     * @param serviceCallFullUrl 服务调用时的完整URL
     * @param callResult  服务调用结果状态值，取值：EnumRPCCallResult
     */
    void addAudit(long callStartTime, int userdTime, String serviceName, StackTraceElement[] stackTrace, String serviceCallFullUrl, EnumRPCCallResult callResult);

    /**
     * 实时返回本服务微服务调用接口(一般而言要单独写一个Model来表达，我们这里就直接用Map标识)
     * @param serviceCallResultCount
     * @return Map.callTotalCount=int; Map.callSucessCount=int; Map.callFaultCount=int; Map.callResultList=List<RPCCallResult>;
     */
    Map<String,Object> getMonitorResult(int serviceCallResultCount);
}
