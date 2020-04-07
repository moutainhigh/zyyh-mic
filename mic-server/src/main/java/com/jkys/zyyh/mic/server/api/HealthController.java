package com.jkys.zyyh.mic.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
@RestController
public class HealthController {

    /**
     * 健康检查
     * @return
     */
    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "SUCCESS";
    }
}
