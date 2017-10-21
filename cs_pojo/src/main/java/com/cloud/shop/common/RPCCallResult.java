package com.cloud.shop.common;

import com.cloud.shop.enums.EnumRPCCallResult;

/**
 * RPC远程服务调用结果
 * Created by ChengYun on 2017/8/26 Version 1.0
 */
public class RPCCallResult {

    /** RPC调用时间 */
    private final long callTimestamp ;

    /** RPC调用耗时 */
    private final int callUsedTime ;

    /** RPC调用服务名称 */
    private final String serviceName ;

    /** 服务调用者的Full Stack */
    private final String serviceCallStack ;

    /** 服务调用Full URL */
    private final String serviceCallFullUrl ;

    /** 服务调用结果状态 */
    private final EnumRPCCallResult serviceCallResult ;

    /** 建造者模式里面的私有构造器 */
    private RPCCallResult(RPCCallResultBuiler rpcCallResultBuiler){
        this.callTimestamp = rpcCallResultBuiler.callTimestamp ;
        this.callUsedTime = rpcCallResultBuiler.callUsedTime ;
        this.serviceName = rpcCallResultBuiler.serviceName ;
        this.serviceCallStack = rpcCallResultBuiler.serviceCallStack ;
        this.serviceCallFullUrl = rpcCallResultBuiler.serviceCallFullUrl ;
        this.serviceCallResult = rpcCallResultBuiler.serviceCallResult ;
    }

    /** RPCCallResult构造器 */
    public static class RPCCallResultBuiler{
        /** RPC调用时间 */
        private long callTimestamp ;

        /** RPC调用耗时 */
        private int callUsedTime ;

        /** RPC调用服务名称 */
        private String serviceName ;

        /** 服务调用者的Full Stack */
        private String serviceCallStack ;

        /** 服务调用Full URL */
        private String serviceCallFullUrl ;

        /** 服务调用结果状态 */
        private EnumRPCCallResult serviceCallResult ;

        public RPCCallResultBuiler callTimestamp(long callTimestamp){
            this.callTimestamp = callTimestamp ;
            return this ;
        }
        public RPCCallResultBuiler callUsedTime(int callUsedTime){
            this.callUsedTime = callUsedTime ;
            return this ;
        }
        public RPCCallResultBuiler serviceName(String serviceName){
            this.serviceName = serviceName ;
            return this ;
        }
        public RPCCallResultBuiler serviceCallStack(String serviceCallStack){
            this.serviceCallStack = serviceCallStack ;
            return this ;
        }
        public RPCCallResultBuiler serviceCallFullUrl(String serviceCallFullUrl){
            this.serviceCallFullUrl = serviceCallFullUrl ;
            return this ;
        }
        public RPCCallResultBuiler serviceCallResult(EnumRPCCallResult serviceCallResult){
            this.serviceCallResult = serviceCallResult ;
            return this ;
        }

        public RPCCallResult builder(){
            RPCCallResult rpcCallResult = new RPCCallResult(this);
            return rpcCallResult ;
        }
    }

    public long getCallTimestamp() {
        return callTimestamp;
    }

    public int getCallUsedTime() {
        return callUsedTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceCallStack() {
        return serviceCallStack;
    }

    public String getServiceCallFullUrl() {
        return serviceCallFullUrl;
    }

    public EnumRPCCallResult getServiceCallResult() {
        return serviceCallResult;
    }

    @Override
    public String toString() {
        return "RPCCallResult{" +
                ", callUsedTime=" + callUsedTime +
                ", serviceName='" + serviceName + '\'' +
                ", serviceCallFullUrl='" + serviceCallFullUrl + '\'' +
                ", serviceCallResult='" + serviceCallResult.getResultType() + '\'' +
                ", serviceCallResultDesc='" + serviceCallResult.getResultTypeSubDesc() + '\'' +
                '}';
    }
}
