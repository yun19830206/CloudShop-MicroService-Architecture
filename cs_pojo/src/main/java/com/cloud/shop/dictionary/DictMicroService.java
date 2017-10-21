package com.cloud.shop.dictionary;

/**
 * 各个应用提供的微服务的字典值
 * Created by ChengYun on 2017/9/17 Version 1.0
 */
public class DictMicroService {

    /**
     * Order提供的微服务列表
     */
    public static class OrderService{
        /** 业务调用相关接口标识 */
        public static final String ORDER_GET = "order.get";
        public static final String ORDER_GETORDERBYUSERID = "order.getOrderByUserId";
        public static final String ORDER_DELORDER = "order.delOrder";

        /** 模拟接口调用相关服务 */
        public static final String ORDER_SERVICE_RANDOM_FAIL = "order.serviceRandomFail";
        public static final String ORDER_SERVICE_RANDOM_TIMEOUT = "order.serviceRandomTimeout";
        public static final String ORDER_SERVICE_RANDOM_EXCEPTION = "order.serviceRandomException";

        public static final String ORDER_SERVICE_FAIL = "order.serviceFail";
        public static final String ORDER_SERVICE_TIMEOUT = "order.serviceTimeout";
        public static final String ORDER_SERVICE_EXCEPTION = "order.serviceException";
    }

    /**
     * Order提供的微服务列表
     */
    public static class ProductService{
        /** 商品服务 */
        public static final String PRODUCT_SERVICE_GET = "product.get";
        public static final String PRODUCT_SERVICE_GETPRODUCTBYORDERID = "product.getProductByOrderId";
    }
}
