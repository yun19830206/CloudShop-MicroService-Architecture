package com.cloud.shop.core.serviceisolation;

import com.cloud.shop.core.monitor.servicerpcmonitor.IServicePRCMonitor;
import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.cloud.shop.core.servicegovern.servicediscover.IServiceDiscover;
import com.cloud.shop.core.strategy.ISelector;
import com.cloud.shop.core.utils.CSRestTemplateUtils;
import com.cloud.shop.enums.EnumRPCCallResult;
import com.netflix.hystrix.*;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 基于Hytrix做的服务隔离，用于IServiceCaller做RPC的依赖隔离(RPC全部是post方式、暂不提供get方式)
 * Created by ChengYun on 2017/8/7 Version 1.0
 */
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
 * Created by ChengYun on 2017/3/15 Vesion 1.0
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
public class HystrixRPCCommand<T> extends HystrixCommand<T> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** RPC的请求参数Object */
    private final Object request ;
    /** 返回结果类型 */
    private final Class<T> responseType ;
    /** 用于RPC服务调用的RestTemplate，由Spring提供 */
    private final RestTemplate restTemplate ;
    /** 待调用的远程服务唯一名称，也就是已经注册到Zookeeper当中服务唯一标识，如果：order.serviceRandom */
    private final String serviceName ;

    /** 用于服务调用失败的时候，返回的默认值，有set方法 */
    private T fallBack ;

    /** 服务发现实现类 */
    private IServiceDiscover serviceDiscover;
    /** 针对同一个服务的多个实例，获得一个合适的实例使用 */
    private ISelector selector;
    /** 服务调用监控，用于实时查看服务调用的结果 */
    private IServicePRCMonitor servicePRCMonitor ;

    /** 非共享变量，可以使用。用于记录每次命令的开始时间。 */
    long callStartTime = 0L ;
    /** 本次Hystrix请求的RequestUrl */
    String requestUrl = "";

    /**
     * 初始化带服务隔离的HytrixRPCCommand
     * @param serviceName 服务名称
     * @param request     请求Object
     * @param responseType  返回值类型
     * @param fallbackRsp  服务调用失败时，返回值类型
     * @param restTemplate  Spring的RestTemplate
     * @param serviceDiscover   在zookeeper中的服务发现工具类
     * @param servicePRCMonitor  服务调用统计类
     */
    public HystrixRPCCommand(String serviceName, Object request, Class<T> responseType, T fallbackRsp,
                             RestTemplate restTemplate, IServiceDiscover serviceDiscover, IServicePRCMonitor servicePRCMonitor, ISelector selector) {

        //设置分组key，分组key可以用在报表、监控中，默认的线程池隔离也基于分组key(order.get: group组就是order, commandKey就是get)
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName.split("\\.", 2)[0]))
                        //指定命令key
                        .andCommandKey(HystrixCommandKey.Factory.asKey(serviceName))
                        //配置线程池相关属性，队列大小和线程数(默认5个线程，队列10)
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(5).withCoreSize(20))   //线程池是同组Command公用的，可以设置稍大一些
                        //配置CommandProperty相关属性
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true)  //断路器 默认开启
                                        .withCircuitBreakerRequestVolumeThreshold(20)              //意思10s内请求数超过20个时熔断器开始生效(默认20个)
                                        .withCircuitBreakerErrorThresholdPercentage(70)            //上面配置生效后，当错误比例>70%时开始熔断默认50%)
                                        .withExecutionIsolationThreadTimeoutInMilliseconds(1000)   //配置依赖超时时间,1000毫秒(默认1秒)
                                        .withExecutionIsolationThreadInterruptOnTimeout(true)      //使用线程隔离时，是否对命令执行超时的线程调用中断（Thread.interrupt()）操作.默认:true(在使用过程中发现并没有中断执行线程)
                                //.withMetricsRollingPercentileBucketSize()
                        )
                        //指定线程池key，取代默认的分组key
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(serviceName.split("\\.", 2)[0]))
                /**
                 //线程池隔离模式，不写默认是线程池隔离模式
                 HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
                 //信号量隔离模式
                 HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE);
                 //熔断器阈值，默认20个、50%(意思10s内请求数超过20个时熔断器开始生效,后面当错误比例>50%时开始熔断(也就是直接执行getFallback())
                 HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(20);
                 HystrixCommandProperties.Setter().withCircuitBreakerErrorThresholdPercentage(50);
                 //熔断器关闭时间，默认5秒
                 HystrixCommandProperties.Setter().withCircuitBreakerSleepWindowInMilliseconds(5000);
                 */

        );

        this.serviceName= serviceName ;
        this.request = request;
        this.responseType = responseType;
        this.fallBack = fallbackRsp;

        this.restTemplate = restTemplate;
        this.serviceDiscover = serviceDiscover ;
        this.servicePRCMonitor = servicePRCMonitor ;
        this.selector = selector ;
        log.debug("Init HystrixRPCCommand Success, serviceName="+serviceName);  //这一个log后面要注释掉，因为调用太频繁。
    }

    /**
     * Hystrix执行业务的实际代码，hytrixRPCCommand.queue()和hytrixRPCCommand.execute()执行时调用的方法
     * @return T
     * @throws Exception
     */
    protected T run() throws Exception {
        log.error("HystrixRPCCommand.run()调用开始，时间:"+System.currentTimeMillis()+";  线程:"+Thread.currentThread().getName());  //为了调试方式修改为log.error，正常使用log.debug
        callStartTime = System.currentTimeMillis();
        if(null == serviceName || null == restTemplate || null == serviceDiscover || null == servicePRCMonitor){
            log.info("HystrixRPCCommand run fault, because parameters have null,serviceName={"+serviceName+"}, restTemplate={"+restTemplate+"}, serviceDiscover={"+serviceDiscover+"}, servicePRCMonitor={"+servicePRCMonitor+"}");
            return null ;
        }

        //1：通过serviceDiscover拿到serviceName的服务实例InstanceDetails
        ServiceInstance<InstanceDetails> serviceInstance = null ;
        InstanceDetails instanceDetails = null ;
        try {
            List<ServiceInstance<InstanceDetails>> allServiceInstance = serviceDiscover.getInstancesByName(serviceName);
            serviceInstance = selector.getInstance(allServiceInstance);
        } catch (Exception e) {
            log.error("HystrixRPCCommand find service["+serviceName+"] from IServiceDiscover Exception",e);
        }
        if(null != serviceInstance && null != serviceInstance.getPayload()){
            instanceDetails = serviceInstance.getPayload();
        }else{
            //没有找到服务，则不用发起服务调用，直接记录服务调用失败监控，返回null
            log.info("can not get "+ serviceName + " result from HystrixRPCCommand.run().");
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    null, EnumRPCCallResult.Error_UrlNull);
            return null;
        }

        //2：根据发起服务调用(到此为止，说明serviceInstance 和 instanceDetails都不为空)
        T t = null;
        requestUrl = instanceDetails.getRequestUrl() ;
        try {
            t = getOrPost(instanceDetails,request,responseType);
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    instanceDetails.getRequestUrl(),EnumRPCCallResult.Sucess);
            //TODO 这里有一个小问题:当上面的调用异常的时候，hystrix的超时隔离器是自动工作返回默认结果出去(超时隔离没有增加任何addAudit，而原本调用的线程会返回结果正常的，只是时间慢了好多，而这个时候给了正确的addAudit，与实际情况不符。(可以用耗时来补偿机制)
        } catch (Exception e) {
            log.error("HystrixRPCCommand exceute service["+serviceName+"] exception ",e);
            //TODO 验证，如果出现异常(如Http 500异常，个人感觉应该再次抛出异常，以便熔断器/Fallback正常工作，不然异常情况永远不会发生啊(除非超时))
//            if(e instanceof SomeBuisnessException){
                throw e;  //这里可以根据业务实际情况，抛出业务定义的Exception(非HystrixBadRequestException异常)
//            }

            /** TODO 这里没搞懂，hystrix的默认超时时间是多少？
             *    答案："配置依赖超时时间 默认1秒"这个属性. 已经通过参数验证OK了(RestTemplate超时25秒、远端服务超时18秒、Hystrix依赖超时3秒,不打断点的情况下3秒出结果，且getFallback()方法被调用)
             */

            /** TODO 这里没搞懂，hystrix怎么判断熔断器是否开启？
             *    答案：目前没有找到特定的标识或者方法可以知道开启断路器的状态。但是可以肯定的是只要断路器开启了，后面的调用getFallback()方法被调用。目前看断路器开启的情况是下面介绍的熔断器阈值开启的时候
             */

            /** TODO 熔断器阈值，10秒阈值20个,之后超过50%错误率开始启动短路器默认5秒(启动短路器默认5秒的动作就是执行getFallback())。可以用如下方法验证
             *    答案：ThreadPoolPropertiesDefaults配置withCoreSize(50)withMaxQueueSize(70)(大一些可以预防线程队列不够而启动熔断机制),
             *    然后客户端启动30个线程(每个线程一开始调用一次超时接口,3秒后再次请求此接口),可以看到刚开始30个请求都是执行run()方法的,由于返回结果都是超时代表错误率超过50%,
             *    所有后面的30个请求短路的。短路默认5秒之后再次执行run()方法.
             *    **断路器开启或者关闭的条件(官方说法)：
                    1、  当满足一定的阀值的时候（默认10秒内超过20个请求次数）
                    2、  当失败率达到一定的时候（默认10秒内超过50%的请求失败）
                    3、  到达以上阀值，断路器将会开启
                    4、  当开启的时候，所有请求都不会进行转发
                    5、  一段时间之后（默认是5秒），这个时候断路器是半开状态，会让其中一个请求进行转发。如果成功，断路器会关闭，若失败，继续开启。重复4和5。
            */

            /** TODO getFallback()被调用降级逻辑. 以下四种情况将触发getFallback调用：
                 (1):run()方法抛出非HystrixBadRequestException异常。
                 (2):run()方法调用超时
                 (3):熔断器开启拦截调用
                 (4):线程池/队列/信号量是否跑满
             */
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
    private T getOrPost(InstanceDetails instanceDetails, Object request, Class<T> responseType) {
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

    /**
     * 执行降级业务(策略),当run方法执行失败时，调用此默认方法
     * @return T
     */
    @Override
    protected T getFallback() {
        /** TODO getFallback()被调用降级逻辑. 以下四种情况将触发getFallback调用：
             (1):run()方法抛出非HystrixBadRequestException异常。
             (2):run()方法调用超时
             (3):熔断器开启拦截调用
             (4):线程池/队列/信号量是否跑满
         */
        log.error("HystrixRPCCommand.getFallback()调用开始，时间:"+System.currentTimeMillis()+";  线程:"+Thread.currentThread().getName());

        if(null != this.fallBack){
            log.info("HystrixRPCCommand getFallback happen for service["+this.serviceName+"]");
            servicePRCMonitor.addAudit(callStartTime,(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                    requestUrl,EnumRPCCallResult.Hystrix_Fallback_Sucess);
            return this.fallBack;
        }

        EnumRPCCallResult callResult ;
        if(super.isCircuitBreakerOpen()){
            callResult = EnumRPCCallResult.Error_Hystrix_CircuitBreak_Open ;
        }else if(super.isFailedExecution()){
            callResult = EnumRPCCallResult.Error_Hystrix_Execute_Exception ;
        }else if(super.isResponseTimedOut()){
            callResult = EnumRPCCallResult.Error_Hystrix_Request_Timeout ;
        }else{
            callResult = EnumRPCCallResult.Error_Hystrix_Thread_Full ;
        }
        servicePRCMonitor.addAudit(callStartTime,callStartTime==0?0:(int)(System.currentTimeMillis()-callStartTime),serviceName,Thread.currentThread().getStackTrace(),
                requestUrl,callResult);

        log.info("HystrixRPCCommand getFallback happen for service["+this.serviceName+"]. not default fallBack return, throw Exception.");
        throw new RuntimeException("can not get "+ serviceName + " result from getFallback().");
    }


}
