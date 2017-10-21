package com.cloud.shop.core.monitor.restcallmonitor;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicLong;

/** 
* 拦截用户所有请求，用于统计
* @author cheng.yun
* @version 2016年9月4日 下午7:11:06
*/

public class InterfaceCountInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware{
	static Logger log = Logger.getLogger(InterfaceCountInterceptor.class);

	private ApplicationContext applicationContext ;
	private AtomicLong callCount = new AtomicLong(0);

	/** 可以进行编码、安全控制等处理. 这里用于统计接口调用次数 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		InterfaceCallMonitor interfaceCallMonitor = applicationContext.getBean(InterfaceCallMonitor.class);
		interfaceCallMonitor.interfaceCallAddOne();
		log.error("=====Call Count="+callCount.incrementAndGet());
		return true ;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/** 可以根据ex是否为null判断是否发生了异常，进行日志记录 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

	/** 有机会修改ModelAndView */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
}
