package com.cloud.shop.dictionary;

/**
 * 公共静态常量、字典值管理类(目前放在一个类中，如果业务使用较多，按照一个模块一个管理类进行)
 * Created by ChengYun on 2017/8/26 Version 1.0
 */
public final class PublicStaticDictionary {

    /** 返回结果字典管理 */
    public static class PublicResultMessage{
        /** 成功 */
        public static final String RESULT_SUCESS_STRING = "sucess" ;
        /** 失败 */
        public static final String RESULT_ERROR_STRING = "error" ;
    }
}
