package com.cloud.shop.common;

import org.springframework.util.StringUtils;


/**
 * API 响应格式
	{
	  "code": 200,
	  "message": "创建成功！",
	  "data": JSONObject,
	  "dataCount": 1，
      "pageNumber": 3,
      "pageSize" : 20
	}
 * Created by  chengyun on 2016/7/9.
 */
public class ApiResponse {

    public static String DEFAULT_MSG = "操作成功";
    /** 返回码: 业务请求正常成功 */
    public static int DEFAULT_CODE = 200;
    /** 返回码: 业务逻辑处理失败 */
    public static int BUSINESS_ERROR_CODE = 300;
    /** 返回码: 服务器内部错误异常 */
    public static int SERVICE_ERROR_CODE = 500;

    //code返回客服端的代码和HTTP的code一直；message返回客服端的字符信息；data返回客服端的数据；dataCount数据总数(共123个任务单，当前只返回10个)
    private int code;           //required
    private String message;     //required
    private Object data;        //optional
    private int dataCount;      //optional
    
    private int pageNumber;		//分页相关：当前第几页,optional
    private int pageSize;		//分页相关：每页展示多少条数据,optional

    public ApiResponse() {
        this(DEFAULT_CODE, DEFAULT_MSG, null,0);
    }

    public ApiResponse(int code, String message, Object data, int dataCount) {
        this.code = code;
        if (!StringUtils.isEmpty(message)) {
            this.message = message;
        }
        this.data = data;
        this.setDataCount(dataCount);
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    
	public int getDataCount() {
		return dataCount;
	}
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}


	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "ApiResponse [code=" + code + ", message=" + message + ", data=" + data + ", dataCount=" + dataCount
				+ ", pageNumber=" + pageNumber + ", pageSize=" + pageSize  + "]";
	}



	/**
     * 构造者模式。
     */
    public static class ApiResponseBuilder {
        ApiResponse apiResponse;

        public ApiResponseBuilder() {
            apiResponse = new ApiResponse();
        }

        /**
         * 设置错误码。默认200
         * @param code 错误码
         * @return ApiResponseBuilder
         */
        public ApiResponseBuilder code(int code) {
            apiResponse.code = code;
            return this;
        }

        /**
         * 设置消息。默认[操作成功]
         * @param message 错误消息
         * @return ApiResponseBuilder
         */
        public ApiResponseBuilder message(String message) {
            apiResponse.message = message;
            return this;
        }

        /**
         * 设置响应的具体内容
         * @param data 响应的具体内容
         * @return 内容
         */
        public ApiResponseBuilder data(Object data) {
            apiResponse.data = data;
            return this;
        }
        
        /**
         * 设置响应的具体内容
         * @param count 响应的具体内容
         * @return 内容
         */
        public ApiResponseBuilder dataCount(int count) {
            apiResponse.dataCount = count;
            return this;
        }
        
        /**
         * 设置计算好的分页数据
         * @return 内容
         */
        public ApiResponseBuilder page(int pageNumber, int pageSize) {
            apiResponse.pageNumber = pageNumber ;
            apiResponse.pageSize = pageSize ;
            return this;
        }

        /**
         * 构造响应
         * @return 响应
         */
        public ApiResponse build() {
            //参数校验, 并且设置默认值
            if (this.apiResponse.code <= 0) {
                this.apiResponse.code = DEFAULT_CODE;
            }
            if (StringUtils.isEmpty(apiResponse.message)) {
                this.apiResponse.message = DEFAULT_MSG;
            }
            return apiResponse;
        }
    }


}
