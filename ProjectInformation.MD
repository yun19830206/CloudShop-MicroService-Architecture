## 项目构建情况介绍
  以常见电商微服务为模型，本工程是自建微服务系统核心架构，利用Zookeeper做服务治理，用Hystrix做服务容错保护。
  工程中业务代码没有编写，但是详细的框架代码都已经全部编写完成，况且有非常详细的注释与说明，并做框架做了各个层面的验证(有验证页面)


###  系统模块说明  
  - cs_pojo：系统公用模型Model子工程
  - cs_core：核心系统子工程,完成服务注册、服务发现、服务调用容灾处理、各种核心功能提供
  - cs_gateway：系统统一对外Api接口调用工程
  - cs_order：订单业务系统
  - cs_product：商品业务系统
  - cs_user：会员业务系统


###  子工程启动项目地址(服务启动必须先启动Zookeeper服务，并且在微服务中填写正确微服务地址)
  - cs_gateway： http://localhost:9003/frameTest.html
  - cs_user：    http://localhost:9000/index
  - cs_product： http://localhost:9001/index
  - cs_order：   http://localhost:9002/index
  
  
###  模块引用关系说明
  见<cloudshop-businessModel.vsd>里面的对应章节关系图


###  此分布式系统构建方法说明----服务注册--服务发现--服务调用--服务隔离  这四者的核心关系见visio图


###  此框架待验证的点
  - 服务注册：如order服务做成集成的了，在不断的服务调用过程中，先后启动1-3个product(再停用1个服务)，要能看到服务注册的结果变化。 
  - 服务依赖隔离：如order集群3个引用(再停用1个服务)，分时段调用超时接口、异常接口、时好时坏接口，看是否能不影响业务主线程：
  具体测试验证页面见：http://IP:Port/frameTest.html; 里面此框架详细的验证方法和理论，读者可以根据这些线索去理解接口、方法、思路

  

###  框架验证页面：http://IP:Port/frameTest.html; 具体验证的点有
    一：查看服务注册结果，通过ServiceCall的方式获得的服务注册信息(ServiceDiscovery.serviceCacheBuilder()方式)
    一：查看服务注册结果，查看服务注册结果(实时Zookeeper方式查看所有服务)
    二: 监控服务器资源：包括堆内存信息、线程信息、各种GC信息
    三: 轮训查看所有ServiceCall调用监控结果(GateWay为发起方)(每一次远程服务调用都会产生一个服务调用的结果)
    四: 调试服务调用ServiceCall返回类型：正常，失败，超时，异常接口(注意看ServiceCall调用监控结果)(直接调用与Hystrix隔离调用两种)。注意这里的调试时重点难点


###  Hystrix使用的验证包括：服务隔离、失败(异常、超时)服务降级(执行fallBack)、熔断器打开、线程池满
    一：服务隔离看《cloudshop-businessModel.vsd.Hystrix服务验证图》一页
    二：失败(一个请求失败执行fallBack正常好验证)、异常(一个请求失败执行fallBack正常好验证)都比较好验证，也看一部分的图
    三：熔断器打开(的标志是后续请求不执行run()方法,而直接执行fallBack()方法)看《cloudshop-businessModel.vsd.Hystrix服务验证图》一页
    四：线程池满(的标志是后续请求不执行run()方法,而直接执行fallBack()方法)看《cloudshop-businessModel.vsd.Hystrix服务验证图》一页(有几项没有搞清楚)


###  Hystrix的hystrix-dashboard查看方式
    一：必须要配置Hystrix Dashboard的Servlet,在Gateway项目web.xml中配置HystrixMetricsStreamServlet
    二：解压启动tomcat压缩包(Hystrix查看Dashboard的官方项目)(apache-tomcat-8.0.36_hystrix-dashboard-7979.rar)，输入网址:http://localhost:7979/
    三：确保整个项目都已经启动正常运行了，并且调用了很多次HystrixCommand(多调用几种服务，成功、失败都多尝试几次)
    四：打开Dashboard地址:http://localhost:7979/，然后依次填入"Stream Type: Hystrix","App path",Add Stream, Monitor Streams 就能看到官方监控结果，见《cloudshop-businessModel.vsd.Hystrix dashboard》:监控地址输入:http://localhost:9003/hystrix.stream
    

###  验证直接远程调用方式(NormalRestTemplateRPCServiceCallerImpl)与Hystrix服务隔离调用方式(HystrixRPCServiceCallerImpl)，会把服务拖垮的情况
    PS：注意不能一启动服务的时候就做并发测试，需要先进行一些正常成功服务的调用一段时间才可以，因为先要有成功率的的基础数据。
    1：Gateway项目，NIOmaxThreads=500,port=9003,启动参数:-server -Xms128M -Xmx256M
    2：Order项目，NIOmaxThreads=500,port=9002,启动参数:-server -Xms128M -Xmx256M
    3: Product项目，NIOmaxThreads=500,port=9001,启动参数:-server -Xms128M -Xmx256M
    4: Hystrix是如何保护应用的，详见《Hystrix是如何保护应用验证.docx》文档
