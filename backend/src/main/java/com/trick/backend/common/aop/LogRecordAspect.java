package com.trick.backend.common.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trick.backend.common.utils.ThreadLocalUtil;
import com.trick.backend.mapper.OperationLogMapper;
import com.trick.backend.model.pojo.OperationLog;
import com.trick.backend.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LogRecordAspect {

    // SpEL表达式解析器
    private final SpelExpressionParser parser = new SpelExpressionParser();
    // 用于获取方法参数名
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OperationLogService operationLogService;

    // 定义切点，拦截所有带 @LogRecord 注解的方法
    @Pointcut("@annotation(com.trick.backend.common.aop.LogRecord)")
    public void logRecordPointcut() {
    }

    // 使用环绕通知，可以控制方法执行前后，并处理异常
    @Around("logRecordPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            // 方法成功执行后记录日志
            handleLog(joinPoint, null, startTime);
            return result;
        } catch (Throwable e) {
            // 方法抛出异常后记录日志
            handleLog(joinPoint, e, startTime);
            throw e;
        }
    }

    private void handleLog(JoinPoint joinPoint, Throwable e, long startTime) {
        OperationLog opLog = new OperationLog();

        // 获取HTTP请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogRecord logRecord = method.getAnnotation(LogRecord.class);

        // 1. 设置注解上定义的基础信息
        opLog.setOperationModule(logRecord.module());
        opLog.setOperationType(logRecord.type());

        // 2. 解析SpEL表达式，生成动态的日志描述
        String description = parseSpel(logRecord.description(), method, joinPoint.getArgs());
        opLog.setOperationDesc(description);

        // 3. 设置请求相关信息
        if (request != null) {
            opLog.setRequestUri(request.getRequestURI());
            opLog.setRequestMethod(request.getMethod());
            opLog.setIpAddress(request.getRemoteAddr());
        }

        // 4. 设置操作人信息 (从ThreadLocal中获取)
        Map<String, Object> userInfo = ThreadLocalUtil.getContext();
        if (userInfo != null) {
            opLog.setOperatorId(((Integer) userInfo.get("id")));
            opLog.setOperatorName((String) userInfo.get("username"));
        }

        // 5. 设置请求参数
        try {
            opLog.setRequestParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (JsonProcessingException ex) {
            opLog.setRequestParams("参数序列化失败");
        }

        // 6. 设置执行状态和耗时
        long executionTime = System.currentTimeMillis() - startTime;
        opLog.setExecutionTime((int) executionTime);
        opLog.setCreateTime(LocalDateTime.now());

        if (e != null) {
            opLog.setStatus(1); // 1-失败
            opLog.setErrorMsg(e.getMessage());
        } else {
            opLog.setStatus(0); // 0-成功
        }

        // 7. 异步保存日志
        operationLogService.addOperationLog(opLog);
    }

    /**
     * 解析SpEL表达式
     *
     * @param spel   表达式字符串
     * @param method 方法
     * @param args   方法参数
     * @return 解析后的字符串
     */
    private String parseSpel(String spel, Method method, Object[] args) {
        // 获取方法参数名
        String[] paramNames = discoverer.getParameterNames(method);
        // 创建SpEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, String.class);
    }
}
