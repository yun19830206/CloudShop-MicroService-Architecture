package com.cloud.shop.gateway.web.monitor;

import com.cloud.shop.common.ApiResponse;
import com.cloud.shop.core.monitor.servicerpcmonitor.IServicePRCMonitor;
import com.cloud.shop.core.monitor.webresmonitor.WebResourceMonitor;
import com.cloud.shop.core.monitor.webresmonitor.pojo.WebResourceModel;
import com.cloud.shop.core.rpccall.IRPCServiceCall;
import com.cloud.shop.core.rpccall.impl.HystrixRPCServiceCallerImpl;
import com.cloud.shop.core.servicegovern.pojo.InstanceDetails;
import com.cloud.shop.core.servicegovern.servicediscover.IServiceDiscover;
import com.cloud.shop.core.utils.CSRestTemplateUtils;
import com.cloud.shop.core.utils.CSServiceGovernUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本类可以监控服务治理的相关结果<br/>
 *  host/csmonitor/service/showAllCachedServices: 监控服务治理的相关结果(通过CacheService方式来获得)
 *  host/csmonitor/service/showAllZookeeperServices: 监控服务治理的相关结果(通过实时查看Zookeeper的方式来获得)
 *  host/csmonitor/service/getOneServiceList?serviceName=order.serviceRandom: 手动获得某个ServiceName在Zookeeper服务注册中的结果
 * Created by ChengYun on 2017/8/6 Version 1.0
 */
@Controller
@RequestMapping("/csmonitor/service/")
public class ServiceGovern {

    private final Logger logger = LoggerFactory.getLogger(ServiceGovern.class);

    @Value("${service.contextName}")
    private String serviceName;

    /** 服务发现接口 */
    private final IServiceDiscover serviceDiscover ;
    /** 服务器资源监控类型 */
    private final WebResourceMonitor webResourceMonitor ;
    /** http调用工具类 */
    private final RestTemplate restTemplate;
    /** 服务调用监控类 */
    private final IServicePRCMonitor servicePRCMonitor;


    /** 这种注入的方式，是Spring官方所推荐的方式 */
    @Autowired
    public ServiceGovern(IServiceDiscover serviceDiscover, WebResourceMonitor webResourceMonitor,
                         RestTemplate restTemplate, IServicePRCMonitor servicePRCMonitor) {
        this.serviceDiscover = serviceDiscover;
        this.webResourceMonitor = webResourceMonitor;
        this.restTemplate = restTemplate ;
        this.servicePRCMonitor = servicePRCMonitor ;
    }

    /**
     * 监控服务治理的相关结果(通过CacheService方式来获得)
     */
    @RequestMapping("/showAllCachedServices")
    @ResponseBody
    public ApiResponse showAllCachedServices() {

        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE)
                .message("监控服务治理的相关结果(通过CacheService方式来获得)")
                .data(serviceDiscover.getAllServiceFromCachedServiceMap()).build();
    }

    /**
     * 监控服务治理的相关结果(通过实时查看Zookeeper的方式来获得)
     */
    @RequestMapping("/showAllZookeeperServices")
    @ResponseBody
    public ApiResponse showAllZookeeperServices() {

        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE)
                .message("监控服务治理的相关结果(通过实时查看Zookeeper的方式来获得)")
                .data(serviceDiscover.getAllServiceFromZoookeeper()).build();
    }

    /**
     * 手动获得某个ServiceName在Zookeeper服务注册中的结果
     */
    @RequestMapping("/getOneServiceList")
    @ResponseBody
    public ApiResponse getOneServiceList(String serviceName) throws Exception {

        List<ServiceInstance<InstanceDetails>> serviceList = null ;
        try{
            serviceList = serviceDiscover.getInstancesByName(serviceName);
        }catch (Exception e){
            logger.error("get serviceName["+serviceName+"] serviceInstance from zookeeper error",e);
            e.printStackTrace();
        }

        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE)
                .message("手动获得某个ServiceName在Zookeeper服务注册中的结果")
                .data(serviceList).build();
    }

    /**
     * 监控服务治理的相关结果(通过实时查看Zookeeper的方式来获得：简短结果：ServiceName\URl)
     */
    @RequestMapping("/getAllSimpleServiceListFromZookeeper")
    @ResponseBody
    public ApiResponse getAllSimpleServiceListFromZookeeper() {
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE)
                .message("监控服务治理的相关结果(通过实时查看Zookeeper的方式来获得)")
                .data(CSServiceGovernUtils.getSimpleServiceList(serviceDiscover.getAllServiceFromZoookeeper())).build();
    }

    /**
     * 测试接口，创建一个大对象，用于监控Web应用的内存占用情况
     */
    @RequestMapping("/createBigdataObject")
    @ResponseBody
    public ApiResponse createBigdataObject() {
        byte[] bytes = new byte[400000000];
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE)
                .message("测试接口，创建一个大对象，用于监控Web应用的内存占用情况")
                .data("测试接口，创建一个大对象，用于监控Web应用的内存占用情况").build();
    }

    /**
     * 测试接口，创建一个大对象，用于监控Web应用的内存占用情况
     */
    @RequestMapping("/getWebResource")
    @ResponseBody
    public ApiResponse getWebResource(String serviceName) {
        WebResourceModel[] webResourceModels = new WebResourceModel[5] ;
        //所有不满足条件的，返回本服务的
        if(null == serviceName || null == this.serviceName || this.serviceName.equals(serviceName)){
            webResourceModels = webResourceMonitor.getWebResourceModels().toArray(webResourceModels);
        }else{
            String webName="",webSeq="A" ;
            if(serviceName.endsWith("A")){
                webName = serviceName.replace("A","");
                webSeq = "A";
            }else if(serviceName.endsWith("B")){
                webName = serviceName.replace("B","");
                webSeq = "B";
            }
            List<String> orderMonitorServices = CSServiceGovernUtils.getSimpleServiceList(serviceDiscover.getAllServiceFromZoookeeper()).get(webName);
            //orderMonitorServices = 1, A返回值，B不返回值
            if(null != orderMonitorServices && orderMonitorServices.size() == 1 && "A".equals(webSeq)){
                webResourceModels = CSRestTemplateUtils.get(restTemplate,orderMonitorServices.get(0),null,webResourceModels.getClass());
            }else if (null != orderMonitorServices && orderMonitorServices.size() >1 ){
                //orderMonitorServices > 1, A返回值，B也返回值
                if("A".equals(webSeq)){
                    webResourceModels = CSRestTemplateUtils.get(restTemplate,orderMonitorServices.get(0),null,webResourceModels.getClass());
                }else{
                    webResourceModels = CSRestTemplateUtils.get(restTemplate,orderMonitorServices.get(1),null,webResourceModels.getClass());
                }
            }
        }
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("服务器占用资源情况").data(getFormatedResourceDisplayStringList(webResourceModels)).build();
    }

    /**
     * 查看本应用所有远端ServiceCall的结果
     */
    @RequestMapping("/getServiceCallMonitorResult")
    @ResponseBody
    public ApiResponse getServiceCallMonitorResult() {
        Map monitorResult = servicePRCMonitor.getMonitorResult(30);
        return new ApiResponse.ApiResponseBuilder().code(ApiResponse.DEFAULT_CODE).message("查看本应用所有远端ServiceCall的结果").data(monitorResult).build();
    }

    /**
     * 获得简短格式的服务器资源占用情况
     * @param webResourceModels
     * @return
     */
    private List<String> getFormatedResourceDisplayStringList(WebResourceModel[] webResourceModels){
        List<String> simpleWebResourceModelList = new ArrayList<>();
        if(webResourceModels != null && webResourceModels.length>0){
            for(WebResourceModel webResourceModel : webResourceModels){
                if(null != webResourceModel && null != webResourceModel.getHeapMax()){
                    StringBuffer oneWebResource = new StringBuffer();
                    oneWebResource.append("堆总数/空闲:").append(webResourceModel.getHeapMax()).append("/").append(webResourceModel.getHeapFree()).append("; ");
                    oneWebResource.append("线程数/守护:").append(webResourceModel.getThreadCount()).append("/").append(webResourceModel.getThreadDemoCount()).append("; ");
                    oneWebResource.append("MonitorGC次数/耗时:").append(webResourceModel.getMonitorGCCount()).append("/").append(webResourceModel.getMonitorTimes()).append("; ");
                    oneWebResource.append("FullGC次数/耗时:").append(webResourceModel.getFullGCCount()).append("/").append(webResourceModel.getFullGCTimes()).append("; ");
                    simpleWebResourceModelList.add(oneWebResource.toString());
                }
            }
        }
        return  simpleWebResourceModelList;
    }


}
