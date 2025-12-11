package com.training.admin.exception;

import com.training.admin.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各类异常，返回友好的错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 是否为开发环境
     * 可在 application.yml 中配置: app.dev-mode=true
     */
    @Value("${app.dev-mode:false}")
    private boolean devMode;
    
    /**
     * 业务异常处理
     * 直接返回业务错误信息，无需记录堆栈
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数校验异常处理
     * 处理 @Valid 和 @Validated 注解的校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", errorMessage);
        return Result.error(400, errorMessage);
    }
    
    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", errorMessage);
        return Result.error(400, errorMessage);
    }
    
    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        
        // 开发环境返回详细信息，便于调试
        if (devMode) {
            return Result.error(400, e.getMessage());
        }
        
        // 生产环境返回友好提示
        return Result.error(400, "参数错误，请检查输入");
    }
    
    /**
     * 系统异常处理
     * 记录详细的错误堆栈，便于排查问题
     * 根据环境返回不同的错误信息
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 记录详细的错误日志，包含完整堆栈信息
        log.error("系统异常", e);
        
        // 开发环境：返回详细错误信息，便于调试
        if (devMode) {
            String detailMessage = e.getMessage();
            // 如果错误信息过长，截取前 500 字符
            if (detailMessage != null && detailMessage.length() > 500) {
                detailMessage = detailMessage.substring(0, 500) + "...";
            }
            return Result.error(500, "系统异常：" + detailMessage);
        }
        
        // 生产环境：返回友好提示，隐藏技术细节
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}

