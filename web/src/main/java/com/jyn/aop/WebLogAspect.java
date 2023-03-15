package com.jyn.aop;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一日志处理切面
 *
 * @author 10263
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    @Pointcut("execution(public * com.jyn.web.controller.*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Object[] parameterValues = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        WebLog webLog = new WebLog();
        webLog.setParameter(JSONObject.toJSONString(assembleParameter(parameterNames, parameterValues)));
        webLog.setHeader(JSONObject.toJSONString(getHeaders(request.getHeaderNames(), request)));
        webLog.setUri(request.getRequestURI());
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(Operation.class)) {
            Operation log = method.getAnnotation(Operation.class);
            webLog.setDescription(log.description());
        }
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            webLog.setSpendTime(endTime - startTime);
            log.error("请求失败 = {}, exception = {}", webLog, e);
            throw new RuntimeException();
        }
        long endTime = System.currentTimeMillis();
        webLog.setSpendTime(endTime - startTime);
        webLog.setResult(JSONObject.toJSONString(result));
        log.info("请求成功 = {}", JSONObject.toJSONString(webLog));
        return result;
    }

    /**
     * 参数处理
     *
     * @param parameterNames
     * @param parameterValues
     * @return
     */
    private Map<String, Object> assembleParameter(String[] parameterNames, Object[] parameterValues) {
        Map<String, Object> parameterNameAndValues = new HashMap<>(16);
        for (int i = 0; i < parameterNames.length; i++) {
            parameterNameAndValues.put(parameterNames[i], parameterValues[i]);
        }
        return parameterNameAndValues;
    }

    /**
     * 请求头信息
     *
     * @param headerNames
     * @param request
     * @return
     */
    private Map<String, Object> getHeaders(Enumeration<String> headerNames, HttpServletRequest request) {
        Map<String, Object> parameterNameAndValues = new HashMap<>(16);
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            parameterNameAndValues.put(key, value);
        }
        return parameterNameAndValues;
    }
}

