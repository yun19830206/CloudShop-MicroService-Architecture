package com.cloud.shop.order;

import java.io.Serializable;

/**
 * Gateway项目模拟微服务调用Order服务，Order服务返回结果Model(较简单,实际项目肯定是按实际业务定义的)
 * Created by ChengYun on 2017/9/17 Version 1.0
 */
public class OrderSimulationResponse implements Serializable {

    private static final long serialVersionUID = 4484496266996135071L;

    /** 接口调用结果描述 */
    private String resultMessage ;
    /** 接口调用结果Object */
    private Object resultObject ;

    public OrderSimulationResponse(String resultMessage, Object resultObject){
        this.resultMessage = resultMessage ;
        this.resultObject = resultObject ;
    }

    /** 必须要有默认无参构造函数，不能序列化、反序列化不成功 */
    public OrderSimulationResponse(){
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

    @Override
    public String toString() {
        return "OrderSimulationResponse{" +
                "resultMessage='" + resultMessage + '\'' +
                ", resultObject=" + resultObject +
                '}';
    }
}
