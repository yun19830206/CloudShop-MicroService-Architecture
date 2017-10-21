package com.cloud.shop.order.web;

import com.cloud.shop.core.monitor.webresmonitor.WebResourceMonitor;
import com.cloud.shop.core.monitor.webresmonitor.pojo.WebResourceModel;
import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用于监控本服务的资源占用情况
 * Created by ChengYun on 2017/9/16 Version 1.0
 */
@Controller
@RequestMapping("/order")
public class WebMonitorController {

    private final WebResourceMonitor webResourceMonitor ;

    /** 这种注入的方式，是Spring官方所推荐的方式 */
    @Autowired
    public WebMonitorController(WebResourceMonitor webResourceMonitor) {
        this.webResourceMonitor = webResourceMonitor;
    }

    @MicroServiceDesc(serviceName="order.resMonitor")
    @RequestMapping(value = "/resMonitor",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public WebResourceModel[] resMonitor(String orderId) {

        WebResourceModel[] webResourceModels = new WebResourceModel[5] ;
        webResourceModels = webResourceMonitor.getWebResourceModels().toArray(webResourceModels);

        return webResourceModels;
    }
}
