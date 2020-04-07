package com.jkys.zyyh.mic.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * HttpPoolProperties类.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-14 17:47
 */
@Component
@ConfigurationProperties(prefix = "http-pool")
@Data
public class HttpPoolProperties {
    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;
    private Integer socketTimeout;
    private Integer validateAfterInactivity;
}
