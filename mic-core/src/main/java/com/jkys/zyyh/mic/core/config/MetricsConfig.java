package com.jkys.zyyh.mic.core.config;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Metrics配置
 * @author ks 2019-05-07.
 */
@Configuration
public class MetricsConfig {

    @Value("${metrics.period:30}")
    private Long METRICS_PERIOD;

    @Bean
    public MetricRegistry metricRegistry(){
        return new MetricRegistry();
    }

    @Bean
    public Slf4jReporter slf4jReporter(@Autowired MetricRegistry metricRegistry) {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(LoggerFactory.getLogger("monitorLog"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(METRICS_PERIOD, TimeUnit.SECONDS);
        return reporter;
    }

}
