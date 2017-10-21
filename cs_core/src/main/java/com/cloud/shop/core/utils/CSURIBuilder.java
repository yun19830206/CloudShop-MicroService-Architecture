package com.cloud.shop.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * 构造URL地址. 用法举例
 * <pre>
 *      CSURIBuilder build =  new CSURIBuilder("http://192.168.4.103:8686");
 *      build.addPath("/home/");
 *      build.addPath("index");
 *      build.addPath("/qq").addParameter("number", "6565118");
 *      Assert.assertEquals("http://192.168.4.103/home/index/qq?number=6565118", build.build().toString());
 * </pre>
 * Created by ChengYun on 2017/6/10 Vesion 1.0
 */
public class CSURIBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CSURIBuilder.class);

    private  org.apache.http.client.utils.URIBuilder builder;

    public CSURIBuilder(String baseUrl)  {
        try {
            builder = new org.apache.http.client.utils.URIBuilder(baseUrl);
        } catch (URISyntaxException e) {
            logger.error("url is not legal format", e);
        }
    }

    public CSURIBuilder addPath(String subPath) {
        if (null == subPath  || subPath.isEmpty() || "/".equals(subPath)) {
            return this;
        }

        if(null != builder) {
            builder.setPath(this.appendSegmentToPath(builder.getPath(), subPath));
        }
        return this;
    }

    public CSURIBuilder addParameter(String key, String value) {
        if(builder != null) {
            builder.addParameter(key,value);
        }
        return this;
    }

    private String appendSegmentToPath(String path, String segment) {
        if (null == path|| path.isEmpty()) {
            path = "/";
        }

        if (path.charAt(path.length() - 1) == '/' || segment.startsWith("/")) {
            return path + segment;
        }

        return path + "/" + segment;
    }

    public String build(){
        try {
            if(null != builder) {
                return this.builder.build().toString();
            }
        } catch (URISyntaxException e) {
            logger.error("url is not legal format ", e);
        }
        return null;
    }
}