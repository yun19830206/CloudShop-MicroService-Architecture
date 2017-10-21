package com.cloud.shop.dictionary;

/**
 * 请求地址静态值
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class DictRequestUrl {

    /** Order服务直接访问：正常 */
    public static String ORDER_NORMAL_REQUEST_SUCCESS = "http://192.168.1.108:9003/api/gateway/restNormalCall";
    /** Order服务直接访问：失败 */
    public static String ORDER_NORMAL_REQUEST_FAIL = "http://192.168.1.108:9003/api/gateway/restRandomFail";
    /** Order服务直接访问：超时 */
    public static String ORDER_NORMAL_REQUEST_TIMEOUT = "http://192.168.1.108:9003/api/gateway/restRandomTimeout";
    /** Order服务直接访问：异常 */
    public static String ORDER_NORMAL_REQUEST_EXCEPTION = "http://192.168.1.108:9003/api/gateway/restRandomException";
    /** Order服务直接访问：获得用户订单 */
    public static String ORDER_NORMAL_SUCCESS_GET_ORDER_BYUSERID = "http://192.168.1.108:9003/api/gateway/getOrderByUserIdNormal";
    /** Order服务直接访问：删除用户订单 */
    public static String ORDER_NORMAL_SUCCESS_DEL_ORDER = "http://192.168.1.108:9003/api/gateway/delOrderNormal";

    /** Order服务Hystrix访问：正常 */
    public static String ORDER_HYSTRIX_REQUEST_SUCCESS = "http://192.168.1.108:9003/api/gateway/hystirxNormalCall";
    /** Order服务Hystrix访问：失败 */
    public static String ORDER_HYSTRIX_REQUEST_FAIL = "http://192.168.1.108:9003/api/gateway/hystrixRandomFail";
    /** Order服务Hystrix访问：超时 */
    public static String ORDER_HYSTRIX_REQUEST_TIMEOUT = "http://192.168.1.108:9003/api/gateway/hystrixRandomTimeout";
    /** Order服务Hystrix访问：异常 */
    public static String ORDER_HYSTRIX_REQUEST_EXCEPTION = "http://192.168.1.108:9003/api/gateway/hystrixRandomException";
    /** Order服务Hystrix访问：获得用户订单 */
    public static String ORDER_HYSTRIX_SUCCESS_GET_ORDER_BYUSERID = "http://192.168.1.108:9003/api/gateway/getOrderByUserIdHystrix";
    /** Order服务Hystrix访问：删除用户订单 */
    public static String ORDER_HYSTRIX_SUCCESS_DEL_ORDER = "http://192.168.1.108:9003/api/gateway/delOrderHystrix";

    /** Product服务直接访问：正常 */
    public static String PRODUCT_NORMAL_REQUEST_SUCCESS = "http://192.168.1.108:9003/api/gateway/getProductInfoNormal";
    /** Product服务Hystrix访问：正常 */
    public static String PRODUCT_HYSTRIX_REQUEST_SUCCESS = "http://192.168.1.108:9003/api/gateway/getProductInfoHystrix";
    /** Product服务Hystrix访问：正常返回，耗时5秒 */
    public static String PRODUCT_HYSTRIX_REQUEST_SUCCESS_5S = "http://192.168.1.108:9003/api/gateway/serviceUsed5Second";
    /** Product服务直接访问：正常 */
    public static String PRODUCT_NORMAL_REQUEST_GET_PRDUCT_BYORDERID = "http://192.168.1.108:9003/api/gateway/getProductByOrderIdNormal";
    /** Product服务Hystrix访问：正常 */
    public static String PRODUCT_HYSTRIX_REQUEST_GET_PRDUCT_BYORDERID = "http://192.168.1.108:9003/api/gateway/getProductByOrderIdHystrix";


}
