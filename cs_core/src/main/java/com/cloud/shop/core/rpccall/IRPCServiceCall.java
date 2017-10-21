package com.cloud.shop.core.rpccall;

import java.util.concurrent.ExecutionException;

/**
 * 提供远程服务调用的接口<br/>
 *  1:不提供异步调用方式，如果是异步调用属于事件型通知，通过MQ方式实现。
 *  2:不提供get\post的区别，由服务注册、发现里面的服务Get/Post属性自动调用，没有属性默认用Post方式
 * Created by ChengYun on 2017/8/19 Version 1.0
 */
public interface IRPCServiceCall {

    /**
     * 同步服务调用, 支持服务不成功默认值
     * @param serviceName  服务方法
     * @param request      请求消息体. 可以为空
     * @param responseType 响应类型
     * @param fallbackRsp  降级响应
     * @return 响应对象 Future .
     */
    <T> T call(String serviceName, Object request, Class<T> responseType, T fallbackRsp) throws ExecutionException, InterruptedException;
}
