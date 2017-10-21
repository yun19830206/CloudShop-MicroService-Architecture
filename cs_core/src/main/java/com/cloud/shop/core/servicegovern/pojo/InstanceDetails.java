package com.cloud.shop.core.servicegovern.pojo;


import com.cloud.shop.core.utils.CSURIBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 单个服务实例Bean，代表分布式架构下，某个具体服务的描述信息.如：获得会员详情服务<br/>
 * 采用建造者设计模式构建<br/>
 * Created by ChengYun on 2017/1/15 Vesion 1.0
*/
public class InstanceDetails {

    /** Tomcat服务 监听的地址, 例如：127.0.0.0:8081  */
    private final String linstenAddress;

    /** tomcat 的 context, 例如 users */
    private final String context;

    /**  待注册服务的类的名称  */
    private final String controllerName;

	/**  待注册服务的Controller类的RequestMapping的路径, 可能为空  */
    private final String controllerPath;

	/**  待注册服务的类的方法名称  */
    private final String methodName;

	/**  待注册服务的Controller类方法的RequestMapping的路径, 一般不可能为空  */
    private final String methodPath;

	/**  待注册的微服务的名称  */
    private final String serviceName;

    /** 服务请求的URL. 如：http://192.168.1.102:9002/order/get */
    private final String requestUrl;

    /** 是否降级 */
    private final boolean degrade ;

	/** 服务的请求类型：Http Get、Post */
    private final RequestMethod requestMethod;

    /** 建造者模式里面的私有构造器 */
	private InstanceDetails(InstanceDetailsBuilder instanceDetailsBuilder){
		this.linstenAddress = instanceDetailsBuilder.linstenAddress ;
		this.context = instanceDetailsBuilder.context ;
		this.controllerName = instanceDetailsBuilder.controllerName ;
		this.controllerPath = instanceDetailsBuilder.controllerPath ;
		this.methodName = instanceDetailsBuilder.methodName ;
		this.methodPath = instanceDetailsBuilder.methodPath ;
		this.serviceName = instanceDetailsBuilder.serviceName ;
		this.requestUrl = instanceDetailsBuilder.requestUrl ;
		this.degrade = instanceDetailsBuilder.degrade ;
		this.requestMethod = instanceDetailsBuilder.requestMethod ;
	}

	/**
	 * 貌似必须要有此无参构造函数，不然curator来实例化节点服务实例的时候，不能反向序列化出来
	 */
	public InstanceDetails() {
		this.linstenAddress = null ;
		this.context = null ;
		this.controllerName = null ;
		this.controllerPath = null ;
		this.methodName = null ;
		this.methodPath = null ;
		this.serviceName = null ;
		this.requestUrl = null ;
		this.degrade = false ;
		this.requestMethod = null ;
	}

	/** InstanceDetails构造器 */
	public static class InstanceDetailsBuilder{

		private static final String schema = "http://";

		/** Tomcat服务 监听的地址, 例如：127.0.0.0:8081  */
		private final String linstenAddress;

		/** tomcat 的 context, 例如 users */
		private final String context;

		/**  待注册服务的类的名称  */
		private String controllerName;

		/**  待注册服务的Controller类的RequestMapping的路径, 可能为空  */
		private String controllerPath;

		/**  待注册服务的类的方法名称  */
		private String methodName;

		/**  待注册服务的Controller类方法的RequestMapping的路径, 一般不可能为空  */
		private String methodPath;

		/**  待注册的微服务的名称  */
		private String serviceName;

		/** 服务请求的URL. 如：http://172.16.10.158:8087/user/user/getDetail/4684221 */
		private String requestUrl;

		/** 是否降级 */
		@JsonIgnore
		private boolean degrade ;

		/** 服务的请求类型：Http Get、Post */
		private RequestMethod requestMethod;

		public InstanceDetailsBuilder(String linstenAddress, String context){
			this.linstenAddress = linstenAddress;
			this.context = context;
		}

		public InstanceDetailsBuilder controllerName(String controllerName){
			this.controllerName = controllerName ;
			return this ;
		}

		public InstanceDetailsBuilder controllerPath(String controllerPath){
			this.controllerPath = controllerPath ;
			return this ;
		}

		public InstanceDetailsBuilder methodName(String methodName){
			this.methodName = methodName ;
			return this ;
		}

		public InstanceDetailsBuilder methodPath(String methodPath){
			this.methodPath = methodPath ;
			return this ;
		}

		public InstanceDetailsBuilder serviceName(String serviceName){
			this.serviceName = serviceName ;
			return this ;
		}

		public InstanceDetailsBuilder degrade(boolean degrade){
			this.degrade = degrade ;
			return this ;
		}

		public InstanceDetailsBuilder requestMethod(RequestMethod requestMethod){
			this.requestMethod = requestMethod ;
			return this ;
		}

		public InstanceDetails build(){
			this.requestUrl = buildRequestFullUrlPath();
			InstanceDetails instanceDetails = new InstanceDetails(this);
			return instanceDetails;
		}

		private String buildRequestFullUrlPath() {
			CSURIBuilder builder = new CSURIBuilder(schema + linstenAddress);
			//builder.addPath(context);
			builder.addPath(controllerPath);
			builder.addPath(methodPath);
			return builder.build();
		}
	}

	public String getLinstenAddress() {
		return linstenAddress;
	}

	public String getContext() {
		return context;
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getControllerPath() {
		return controllerPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodPath() {
		return methodPath;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	@JsonIgnore
	public boolean isDegrade() {
		return degrade;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	@Override
	public String toString() {
		return "InstanceDetails{" +
				"linstenAddress='" + linstenAddress + '\'' +
				", context='" + context + '\'' +
				", controllerName='" + controllerName + '\'' +
				", controllerPath='" + controllerPath + '\'' +
				", methodName='" + methodName + '\'' +
				", methodPath='" + methodPath + '\'' +
				", serviceName='" + serviceName + '\'' +
				", requestUrl='" + requestUrl + '\'' +
				", degrade='" + degrade + '\'' +
				", requestMethod='" + requestMethod + '\'' +
				'}';
	}
}
