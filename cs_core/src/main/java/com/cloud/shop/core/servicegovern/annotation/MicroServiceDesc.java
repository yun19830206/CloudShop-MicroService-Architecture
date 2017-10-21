package com.cloud.shop.core.servicegovern.annotation;

import java.lang.annotation.*;

/**
 * 微服务架构中，服务注册、发现中的服务描述自定义注解。
 * 所有的服务Controller类都需要添加这个标签. 可选的
 * Created by ChengYun on 2017/6/4 Vesion 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface MicroServiceDesc {

     /**
      * 需要注册与发现的服务的名称。
      * 必填，作用于Controller的方法RequestMapping上面，要求同一个子系统中，此值不能相同.
      * 如Controller的方法上：
      *     @RequestMapping(value = "/get", method = RequestMethod.POST)
      *     @ResponseBody
      *     @MicroServiceDesc(serviceName="user.get")
      *     public List<Object> get(@RequestBody ResourcesRequestBody request) {
     */
     String serviceName();

    /**
     * 是否对请求进行降级， 默认false
     * @return  是否降级
     */
    boolean degrade() default  false;

}
