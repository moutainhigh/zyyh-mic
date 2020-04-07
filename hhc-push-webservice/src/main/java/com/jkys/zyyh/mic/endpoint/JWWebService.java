package com.jkys.zyyh.mic.endpoint;

import com.jkys.zyyh.mic.service.JWMessageService;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.xml.ws.Endpoint;

/**
 * @ClassName JWWebService
 * @Description
 * @Author Gabriel
 * @Date 2020/4/3 10:01
 * @Version V1.0
 */
@Configuration
public class JWWebService {

    @Resource
    private JWMessageService jwMessageService;

    @Bean
    public ServletRegistrationBean cxfDispatcherServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/services/*");
    }

    @Bean(name = SpringBus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), jwMessageService);
        endpoint.publish("/message");

        return endpoint;
    }
}