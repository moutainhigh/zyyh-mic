package com.jkys.zyyh.mic.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author twj
 * 2020/1/19 17:07
 */
@Slf4j
@Component
@PropertySource({"classpath:mic.properties"})
@Data
public class MicConfig {

    @Value("${hospitalId}")
    private Long hospitalId;
}
