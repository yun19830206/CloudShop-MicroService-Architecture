/**
 * 请求地址静态值
 * Created by ChengYun on 2017/10/8 Version 1.0
 */
public class DictRequestUrl {

    /** Order服务直接访问：正常 */
    public static String ORDER_NORMAL_REQUEST_SUCCESS = "http://localhost:9003/api/gateway/debugServiceCall?callType=rest&interfaceType=normal";
    /** Order服务直接访问：失败 */
    public static String ORDER_NORMAL_REQUEST_FAIL = "http://localhost:9003/api/gateway/debugServiceCall?callType=rest&interfaceType=fail";
    /** Order服务直接访问：超时 */
    public static String ORDER_NORMAL_REQUEST_TIMEOUT = "http://localhost:9003/api/gateway/debugServiceCall?callType=rest&interfaceType=timeout";
    /** Order服务直接访问：异常 */
    public static String ORDER_NORMAL_REQUEST_EXCEPTION = "http://localhost:9003/api/gateway/debugServiceCall?callType=rest&interfaceType=exception";

    /** Order服务Hystrix访问：正常 */
    public static String ORDER_HYSTRIX_REQUEST_SUCCESS = "http://localhost:9003/api/gateway/debugServiceCall?callType=hystrix&interfaceType=normal";
    /** Order服务Hystrix访问：失败 */
    public static String ORDER_HYSTRIX_REQUEST_FAIL = "http://localhost:9003/api/gateway/debugServiceCall?callType=hystrix&interfaceType=fail";
    /** Order服务Hystrix访问：超时 */
    public static String ORDER_HYSTRIX_REQUEST_TIMEOUT = "http://localhost:9003/api/gateway/debugServiceCall?callType=hystrix&interfaceType=timeout";
    /** Order服务Hystrix访问：异常 */
    public static String ORDER_HYSTRIX_REQUEST_EXCEPTION = "http://localhost:9003/api/gateway/debugServiceCall?callType=hystrix&interfaceType=exception";

    /** Product服务Hystrix访问：正常 */
    public static String PRODUCT_HYSTRIX_REQUEST_SUCCESS = "http://localhost:9003/api/gateway/getProductInfo?productId=123";
}
