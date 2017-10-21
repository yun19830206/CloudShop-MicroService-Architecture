package com.cloud.shop.core.servicegovern;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 创建CuratorFramework客户端
 * Created by ChengYun on 2017/6/1 Vesion 1.0
 */
@Component
public class CuratorFrameworkFactoryBean{
  private final static Logger logger = LoggerFactory.getLogger(CuratorFrameworkFactoryBean.class);

  /** Curator客户端 */
  private CuratorFramework curator;

  /** 从Spring配置文件中获取Zookeeper的连接串 */
  @Value("${zookeeper.address:127.0.0.1:2181}")
  private String connectString;

  /** Zookeeper的session会话时间 */
  private int sessionTimeout;

  /** Zookeeper的链接重试机制 */
  private RetryPolicy retryPolicy;

  /** Zokkeeper数据的根节点名称 */
  @Value("${zookeeper.namespace:CloudShop}")
  private String namespace;

  /** 构建Curator工厂 */
  @PostConstruct
  public void initCuratorFrameworkFactory() throws Exception {

    if(connectString  == null){
      return;
    }

    org.apache.curator.framework.CuratorFrameworkFactory.Builder builder = org.apache.curator.framework.CuratorFrameworkFactory.builder();
    builder.connectString(connectString);
    if (retryPolicy == null) {
      retryPolicy = new ExponentialBackoffRetry(1000, 3);
    }
    builder.retryPolicy(retryPolicy);

    if (sessionTimeout > 0) {
      builder.sessionTimeoutMs(sessionTimeout);
    }

    if (namespace != null) {
      builder.namespace(namespace);
    }

    curator = builder.build();
    curator.start();
    logger.info("Zookeeper Curator start client success. connected to {}", connectString);
  }

  @PreDestroy
  public void destroy() throws Exception {
    curator.close();
    logger.info("Zookeeper Curator destory ...");
  }

  public CuratorFramework getCuratorFramework() throws Exception{
    if(null == curator ){
      this.initCuratorFrameworkFactory();
    }
    return curator;
  }

  public String getConnectString() {
    return connectString;
  }

  public void setConnectString(String connectString) {
    this.connectString = connectString;
  }

  public Integer getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(Integer sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }
  
  public void setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

}