package com.cloud.shop.core.servicegovern.serviceregister;

import com.cloud.shop.core.servicegovern.annotation.MicroServiceDesc;
import com.cloud.shop.core.servicegovern.serviceregister.registerimpl.CuratorRegisterServiceImpl;
import com.cloud.shop.core.utils.CSIpUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 完成微服务中对扫描到的服务进行注册的动作
 * Created by ChengYun on 2017/6/8 Vesion 1.0
 */
@Component
public class CSServiceRegisterHolder implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(CSServiceRegisterHolder.class);

    @Autowired
    private CuratorRegisterServiceImpl curatorRegisterService ;

    /** 存放本应用中的所有Controller */
    private Map<String,Object> allServiceBeanMap = Maps.newHashMap();

    @Value("${service.contextName}")
    private String serviceContext ;

    /** 本服务的应用地址：IP地址:端口Port */
    private String serviceAddress ;

    /** 存放微服务中服务的代号和端口 */
    private final static Map<String,Integer> serviceContextPort = new HashMap();
    static {
        serviceContextPort.put("CSUser",9000);
        serviceContextPort.put("CSProduct",9001);
        serviceContextPort.put("CSOrder",9002);
        serviceContextPort.put("CSGateway",9003);
    }

    /** 根据Spring的ApplicationContext，获得所有Controller类 */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        allServiceBeanMap.putAll(applicationContext.getBeansWithAnnotation(Controller.class));
        logger.info("CSServiceRegisterHolder init success. allServiceBeanMap.size()=" + allServiceBeanMap.size());
    }

    /** 本类初始化好之后，注册所有的服务 */
    @PostConstruct
    public void registerAllServices(){

        //1:获得本应用的地址，便于生产服务注册的数据
        this.serviceAddress = CSIpUtils.getLocalIpAddress() + ":" + serviceContextPort.get(serviceContext);

        //2:遍历本服务的所有Controller，然后获得所有@MicroServiceDesc的注解数据，生成服务注册的接口
        for(Object controller : allServiceBeanMap.values()){
            //获得Controller上的@RequestMapping("/user")的值,便于后面获得整个服务接口的RequestUrl
            RequestMapping controllerMappingAnno = AnnotationUtils.findAnnotation(controller.getClass(),RequestMapping.class);
            String controllerMappingUrl = getRequestMappingUrl(controllerMappingAnno);

            //遍历Controller的每个方法，获取方法的RequestMapping 与 MicroServiceDesc注解的值(两者必须都有)，进行注册
            Method[] controllerMethods = ReflectionUtils.getAllDeclaredMethods(controller.getClass());
            for(Method method : controllerMethods){
                RequestMapping controllerMethodMappingAnno = method.getAnnotation(RequestMapping.class);
                if(null != controllerMethodMappingAnno){
                    RequestMethod httpType = this.getRequestType(controllerMethodMappingAnno);
                    String controllerMethodRequestUrl = getRequestMappingUrl(controllerMethodMappingAnno);
                    MicroServiceDesc serviceDescAnno = method.getAnnotation(MicroServiceDesc.class);
                    if(null != serviceDescAnno){
                        this.curatorRegisterService.register(serviceContext,serviceAddress,
                                controllerMappingUrl,controller.getClass().getSimpleName(),
                                controllerMethodRequestUrl,method.getName(),serviceDescAnno,httpType);
                    }
                }
            }
        }
    }

    /**
     * 根据Spring RequestMapping注解，获得本请求的http类型，get、post等
     * @param controllerMethodMappingAnno
     * @return
     */
    private RequestMethod getRequestType(RequestMapping controllerMethodMappingAnno) {
        //默认使用Post请求方式
        RequestMethod requestMethod = RequestMethod.POST;
        RequestMethod[] requestMethods = controllerMethodMappingAnno.method();
        if(null == controllerMethodMappingAnno || null == requestMethods || requestMethods.length<1){
            return requestMethod ;
        }

        if(requestMethods.length == 1){
            return requestMethods[0];
        }
        return requestMethod;
    }


    /** 获得RequestMapping注解的URL信息 */
    private String getRequestMappingUrl(RequestMapping controllerRequestMappingAnno) {

        if(null == controllerRequestMappingAnno){
            return null ;
        }

        String[] requestPath = controllerRequestMappingAnno.path();
        if(null != requestPath && requestPath.length>0){
            return requestPath[0];
        }

        String[] requestValue = controllerRequestMappingAnno.value();
        if(null != requestValue && requestValue.length>0){
            return requestValue[0];
        }

        return null ;
    }


}
