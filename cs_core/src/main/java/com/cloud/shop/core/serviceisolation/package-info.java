/**
 * 高效\分布式\线程池 服务依赖隔离框架hystrix练习Demo:<br/>
 * 练习URL：http://hot66hot.iteye.com/blog/2155036;  http://ningandjiao.iteye.com/blog/2171185<br/>
 *
 * 学习和例子中要能体现:
 *      1：线程池隔离模式/信号量隔离模式， 默认是线程池隔离模式
 *      2：超时时间
 *      3：信号量模式下，最大并发请求限流，默认值10
 *      4：熔断器阈值，默认20
 *      5：熔断器关闭时间，默认5秒
 *
 * HystrixCommand的执行流程大概如下：
     1:每次调用创建一个新的HystrixCommand,把依赖调用封装在run()方法中.
     2:执行execute()/queue做同步或异步调用.
     3:判断熔断器(circuit-breaker)是否打开,如果打开跳到步骤8,进行降级策略,如果关闭进入步骤.
     4:判断线程池/队列/信号量是否跑满，如果跑满进入降级步骤8,否则继续后续步骤.
     5:调用HystrixCommand的run方法.运行依赖逻辑
     5a:依赖逻辑调用超时,进入步骤8.
     6:判断逻辑是否调用成功
     6a:返回成功调用结果
     6b:调用出错，进入步骤8.
     7:计算熔断器状态,所有的运行状态(成功, 失败, 拒绝,超时)上报给熔断器，用于统计从而判断熔断器状态.
     8:getFallback()降级逻辑.
     以下四种情况将触发getFallback调用：
     (1):run()方法抛出非HystrixBadRequestException异常。
     (2):run()方法调用超时
     (3):熔断器开启拦截调用
     (4):线程池/队列/信号量是否跑满
     8a:没有实现getFallback的Command将直接抛出异常
     8b:fallback降级逻辑调用成功直接返回
     8c:降级逻辑调用失败抛出异常
     9:返回执行成功结果
 * Created by ChengYun on 2017/8/7 Vesion 1.0
 */
/**
 * 重点练习了Hystrix的如下功能与特性：
 *     1:同步调用方式 String result = helloWorldCommand.execute();
 *     2:异步调用 Future<String> future = helloWorldCommand.queue();  result = future.get(100, TimeUnit.MILLISECONDS);
 *     3:注册观察者事件拦截(线程执行完成之后调用此方法) Observable<String> observe = new HelloWorldHystrixCommand("World").observe();  observe.subscribe(new Action1<String>() {
 *     4:使用Fallback() 提供降级策略 HelloWorldHystrixCommand extends HystrixCommand<String> {}.protected String getFallback(){}
 *     5:依赖命名:CommandKey,在Command的构造函数中，设置.andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld")));NOTE: 每个CommandKey代表一个依赖抽象,相同的依赖要使用相同的CommandKey名称。依赖隔离的根本就是对相同CommandKey的依赖做隔离.
 *     6:依赖分组:CommandGroup. 命令分组用于对依赖操作分组,便于统计,汇总等.
 *     7:线程池/信号:ThreadPoolKey
 *     8:请求缓存 Request-Cache,见RequestCacheCommand.java
 *     9:信号量隔离:SEMAPHORE(不太懂使用场景,通过例子的返回结果看,是调用线程远方的线程和调用方式同一个线程).隔离本地代码或可快速返回远程调用(如memcached,redis)可以直接使用信号量隔离,降低线程隔离开销.
 *     10:fallback降级逻辑命令嵌套(直接看网页中的例子吧，不是很明白使用业务场景)
 *     11:显示调用fallback逻辑,用于特殊业务处理(直接看网页中的例子吧，不是很明白使用业务场景).NOTE:显示调用降级适用于特殊需求的场景,fallback用于业务处理，fallback不再承担降级职责，建议慎重使用，会造成监控统计换乱等问题.
 */
package com.cloud.shop.core.serviceisolation;