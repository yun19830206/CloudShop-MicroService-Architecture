package com.cloud.shop.core.initbean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化Bean工作(尤其是那些不愿意通过XML配置方式启动的Bean)
 * Created by ChengYun on 2017/8/22 Version 1.0
 */
@Configuration
public class InitBeanToSpringContext {

    private final static Logger logger = LoggerFactory.getLogger(InitBeanToSpringContext.class);


    /**
     * 初始化Spring提供的Http访问句ResTemplate
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(90000);   //一般应用不会配置这么长时间，这里配置20秒，是为了调试查看效果。
        requestFactory.setConnectTimeout(90000);


        // 添加转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
//        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());  加了之后会使我Controller目前默认返回的JSON变成XML


        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        logger.info("Success Create Spring RestTemplate Bean");
        return restTemplate ;
    }
}
