package com.cloud.shop.enums;

import com.cloud.shop.dictionary.PublicStaticDictionary;

/**
 * Created by ChengYun on 2017/9/3 Version 1.0
 */
public enum EnumRPCCallResult {

    Sucess(200, PublicStaticDictionary.PublicResultMessage.RESULT_SUCESS_STRING, PublicStaticDictionary.PublicResultMessage.RESULT_SUCESS_STRING),
    Hystrix_Fallback_Sucess(201, PublicStaticDictionary.PublicResultMessage.RESULT_SUCESS_STRING, "Hystrix Fallback Sucess"),
    Error_UrlNull(600, PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "service not found from zookeeper"),
    Error_RestTemplate_Execute_Exception(601, PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "Exceute RestTemplate exception"),
    Error_Hystrix_Execute_Exception(602, PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "Execute RestTemplateexception"),
    Error_Hystrix_Request_Timeout(603,PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "Exceute Hystrix Request Timeout"),
    Error_Hystrix_Thread_Full(604,PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "Hystrix Thread Full"),
    Error_Hystrix_CircuitBreak_Open(605,PublicStaticDictionary.PublicResultMessage.RESULT_ERROR_STRING, "Hystrix CircuitBreak Open"),;

    /** 字典唯一Id */
    int id ;
    /** 返回结果类型，目前只有 成功，失败 */
    String resultType ;
    /** 返回结果子类型描述，失败的原因可能有多种 */
    String resultTypeSubDesc ;

    EnumRPCCallResult(int id, String resultType, String resultTypeSubDesc){
        this.id = id;
        this.resultType = resultType;
        this.resultTypeSubDesc = resultTypeSubDesc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultTypeSubDesc() {
        return resultTypeSubDesc;
    }

    public void setResultTypeSubDesc(String resultTypeSubDesc) {
        this.resultTypeSubDesc = resultTypeSubDesc;
    }

    @Override
    public String toString() {
        return "EnumRPCCallResult{" +
                "id=" + id +
                ", resultType='" + resultType + '\'' +
                ", resultTypeSubDesc='" + resultTypeSubDesc + '\'' +
                '}';
    }
}
