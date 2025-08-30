package com.trick.backend.common.exception;

import com.trick.backend.common.result.Result;
import com.trick.backend.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage(), e);
        return Result.error("系统运行时异常，请联系管理员");
    }

    /**
     * 资源未找到异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("静态资源未找到：{}", e.getResourcePath());
        return Result.error(ResultCode.NOT_FOUND.getCode(), "资源不存在：" + e.getResourcePath());
    }

    /**
     * 全局默认异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("未知异常：{}", e.getMessage(), e);
        return Result.error("服务器异常，请稍后再试");
    }

    /**
     * 唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("唯一键冲突：{}", e.getMessage());

        String message = DuplicateKeyEnum.getMessageFromException(e.getMessage());
        return Result.error(message);
    }

}
