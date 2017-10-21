package com.cloud.shop.core.monitor.servicerpcmonitor.monitorImpl;

import com.cloud.shop.common.RPCCallResult;
import com.cloud.shop.core.monitor.servicerpcmonitor.IServicePRCMonitor;
import com.cloud.shop.dictionary.PublicStaticDictionary;
import com.cloud.shop.enums.EnumRPCCallResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控RPC流量(总量、成功、失败)(正常事情是异步MQ将RPC结果到数据库当中)<br/>
 * 为了演示，只记录本次服务启动的RPC总流量，成功量，失败量，以及最后100RPC详情
 * Created by ChengYun on 2017/8/19 Version 1.0
 */
@Component
public class ServiceRPCMonitorImpl implements IServicePRCMonitor {
    private final static Logger logger = LoggerFactory.getLogger(ServiceRPCMonitorImpl.class);

    /** 监控RPC流量--总量 */
    private AtomicLong callTotalCount = new AtomicLong();
    /** 监控RPC流量--成功 */
    private AtomicLong callSucessCount = new AtomicLong();
    /** 监控RPC流量--失败 */
    private AtomicLong callFaultCount = new AtomicLong();
    /** 为了演示，只记录本次服务启动的RPC总流量，成功量，失败量，以及最后100RPC详情(超过200时，去除100个) */
    private List<RPCCallResult> rpcCallResultList = Lists.newCopyOnWriteArrayList();

    /**
     * 每次服务调用结束，保存调用结果
     * @param callResult 服务调用接口
     */
    @Override
    public void addAudit(RPCCallResult callResult) {
        if(null == callResult){
            return;
        }
        this.callTotalCount.incrementAndGet();
        if(PublicStaticDictionary.PublicResultMessage.RESULT_SUCESS_STRING.equals(callResult.getServiceCallResult().getResultType())){
            this.callSucessCount.incrementAndGet();
        }else{
            this.callFaultCount.incrementAndGet();
        }
        try {
            synchronized (rpcCallResultList){   //这里加了同步锁，原本以为CopyOnWriteArrayList是线程OK的，最终发现不OK。这里只是模拟，在实际项目中都是用Queue队列的方式异步记录的
                rpcCallResultList.add(callResult);
            }

        } catch (Exception e) {
            logger.error("Add RPCCallResult[" + callResult + "] to CopyOnWriteArrayList exception", e);
        }

        //为了演示，只记录最后100RPC详情(超过200时，去除100个)
        if(rpcCallResultList.size()>200){
            synchronized (rpcCallResultList){
                for(int i=0; i<=100; i++){
                    rpcCallResultList.remove(i) ;
                }
            }
        }
    }

    @Override
    public void addAudit(long callStartTime, int userdTime, String serviceName, StackTraceElement[] stackTrace, String serviceCallFullUrl, EnumRPCCallResult callResult) {
        RPCCallResult rpcCallResult = new RPCCallResult.RPCCallResultBuiler().callTimestamp(callStartTime)
                .callUsedTime(userdTime).serviceName(serviceName).serviceCallStack(getStackTraceString(stackTrace))
                .serviceCallFullUrl(serviceCallFullUrl).serviceCallResult(callResult).builder();
        this.addAudit(rpcCallResult);
    }

    /**
     * 实时返回本服务微服务调用接口(一般而言要单独写一个Model来表达，我们这里就直接用Map标识)
     * @param serviceCallResultCount
     * @return Map.callTotalCount=int; Map.callSucessCount=int; Map.callFaultCount=int; Map.callResultList=List<RPCCallResult>;
     */
    @Override
    public Map<String, Object> getMonitorResult(int serviceCallResultCount) {
        Map<String,Object> maponitorResult = Maps.newHashMap();
        maponitorResult.put("callTotalCount",callTotalCount+"");
        maponitorResult.put("callSucessCount",callSucessCount+"");
        maponitorResult.put("callFaultCount",callFaultCount+"");

        List<String> callResultList = Lists.newArrayList();
        int startIndex=0, endIndex=0;
        if(serviceCallResultCount<10 || serviceCallResultCount>30){
            serviceCallResultCount =30 ;
        }
        if(serviceCallResultCount > rpcCallResultList.size()){
            serviceCallResultCount = rpcCallResultList.size();
            startIndex = serviceCallResultCount ;
            endIndex = 0 ;
        }else{
            startIndex = rpcCallResultList.size();
            endIndex = startIndex -30 ;
        }
        RPCCallResult oneRPCCallResult ;
        for(int i = startIndex-1; i>=endIndex&&rpcCallResultList.size()>0; i--){
            oneRPCCallResult =  rpcCallResultList.get(i);
            if(null != oneRPCCallResult){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("服务开始:").append(oneRPCCallResult.getCallTimestamp()).append("; 耗时:").append(oneRPCCallResult.getCallUsedTime()).append("MS");
                stringBuilder.append("; 名称:").append(oneRPCCallResult.getServiceName()).append("; URL:").append(oneRPCCallResult.getServiceCallFullUrl());
                stringBuilder.append("; 状态:").append(oneRPCCallResult.getServiceCallResult().getResultType()).append("; 状态描述:").append(oneRPCCallResult.getServiceCallResult().getResultTypeSubDesc());
                stringBuilder.append("; 调用堆栈:").append(oneRPCCallResult.getServiceCallStack());
                callResultList.add(stringBuilder.toString());
            }
        }
        maponitorResult.put("callResultList",callResultList);
        return maponitorResult;
    }

    /**
     * 根据当前调用线程的堆栈，转换成最近的10个线程堆栈信息
     * @param stackTrace 线程堆栈信息，第一个默认是(java.lang.Thread.getStackTrace)不需要
     * @return String
     */
    private String getStackTraceString(StackTraceElement[] stackTrace) {
        if(null == stackTrace || stackTrace.length<2){
            return null ;
        }
        StringBuffer stackTrackString = new StringBuffer();
        stackTrackString.append(getClassName(stackTrace[1])).append(".").append(stackTrace[1].getMethodName()).append("():").append(stackTrace[1].getLineNumber());
        for(int i=2; i<stackTrace.length; i++){
            if(i>20){   //一般线上最多20层就可以了，建议5个
                break;
            }else {
                stackTrackString.append("<--").append(getClassName(stackTrace[i])).append(".").append(stackTrace[i].getMethodName()).append("():").append(stackTrace[i].getLineNumber());
            }
        }
        return stackTrackString.toString();
    }

    /** 获得全路径ClassName中的类短名 */
    private String getClassName(StackTraceElement stackTraceElement) {
        if(null == stackTraceElement || null == stackTraceElement.getClassName()){
            return "";
        }
        String className = stackTraceElement.getClassName();
        return className.substring(className.lastIndexOf(".")+1);
    }

    public AtomicLong getCallTotalCount() {
        return callTotalCount;
    }
    public AtomicLong getCallSucessCount() {
        return callSucessCount;
    }
    public AtomicLong getCallFaultCount() {
        return callFaultCount;
    }
    public List<RPCCallResult> getRpcCallResultList() {
        return rpcCallResultList;
    }




    /** Main方法测试一下效果 */
    public static void main(String[] args) {
        ServiceRPCMonitorImpl serviceRPCMonitor = new ServiceRPCMonitorImpl();
        serviceRPCMonitor.doBusinessStackOne();
        System.out.println(Thread.currentThread().getStackTrace());
        RPCCallResult rpcCallResult = new RPCCallResult.RPCCallResultBuiler().callTimestamp(System.currentTimeMillis()).callUsedTime(50)
                .serviceName("Main Test Caller").serviceCallStack(serviceRPCMonitor.getStackTraceString(Thread.currentThread().getStackTrace())).serviceCallFullUrl("url").serviceCallResult(EnumRPCCallResult.Sucess).builder();
        serviceRPCMonitor.addAudit(rpcCallResult);
        System.out.println(serviceRPCMonitor.getCallFaultCount().get());
        System.out.println(serviceRPCMonitor.getRpcCallResultList().size());
        System.out.println(serviceRPCMonitor.getRpcCallResultList().size());
    }
    public void doBusinessStackOne(){
        int i =0 ;
        doBusinessStackTwo();
    }
    private String doBusinessStackTwo() {
        Map<String, String> map = Maps.newHashMap();
        Thread.currentThread().getStackTrace();
        //int i = 1/0 ;
        return "OK" ;
    }


}
