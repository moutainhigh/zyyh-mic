package com.jkys.zyyh.mic.server.common;

import com.jkys.zyyh.mic.common.common.ApiException;
import com.jkys.zyyh.mic.common.common.ExceptionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * <Description> <br>
 * 统一异常处理
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
@Slf4j
@ControllerAdvice
@Component
public class ExceptionAdvice {

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public ApiResult handleSQLException(SQLException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("SQLException :",e);
        ApiResult apiResult = new ApiResult();
        apiResult.setMessage(e.toString());
        apiResult.setCode(ExceptionConstant.SQL_EXCEPTION);
        apiResult.setSuccess(false);
        return apiResult;
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ApiResult handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("RuntimeException :",e);
        ApiResult apiResult = new ApiResult();
        apiResult.setMessage(e.toString());
        apiResult.setCode(ExceptionConstant.RUNTIME_EXCEPTION);
        apiResult.setSuccess(false);
        return apiResult;
    }

    @ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public ApiResult handleBizException(ApiException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("ApiException :",e);
        ApiResult apiResult = new ApiResult();
        apiResult.setMessage(e.getMessage());
        apiResult.setCode(e.getCode());
        apiResult.setSuccess(false);
        return apiResult;
    }
}
