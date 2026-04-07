package com.uav.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.audit.entity.AuditLog;
import com.uav.audit.service.AuditLogService;
import com.uav.audit.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    @Pointcut("@annotation(com.uav.audit.aspect.AuditRecord) && !execution(* com.uav.audit.service..*.*(..))")
    public void auditLogPointcut() {
    }

    @Around("auditLogPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        AuditRecord annotation = signature.getMethod().getAnnotation(AuditRecord.class);

        // 获取当前用户信息
        Long userId = null;
        String username = "anonymous";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long) {
                userId = (Long) authentication.getPrincipal();
                username = authentication.getName();
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败: {}", e.getMessage());
        }

        // 记录请求参数
        String requestParams = "";
        try {
            requestParams = objectMapper.writeValueAsString(point.getArgs());
        } catch (Exception e) {
            requestParams = Arrays.toString(point.getArgs());
        }

        // 执行方法
        Object result = null;
        int responseStatus = 200;
        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            responseStatus = 500;
            throw e;
        } finally {
            // 记录审计日志（异步）
            long duration = System.currentTimeMillis() - startTime;
            try {
                saveAuditLog(userId, username, annotation, request, requestParams, responseStatus, duration);
            } catch (Exception e) {
                log.error("保存审计日志失败: {}", e.getMessage());
            }
        }
    }

    private void saveAuditLog(Long userId, String username, AuditRecord annotation,
                              HttpServletRequest request, String requestParams,
                              int responseStatus, long duration) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setUsername(username);
        auditLog.setOperationType(annotation.operationType());
        auditLog.setOperationDesc(annotation.description());
        auditLog.setOperationObject(annotation.operationObject());
        auditLog.setRequestMethod(request != null ? request.getMethod() : "UNKNOWN");
        auditLog.setRequestUrl(request != null ? request.getRequestURI() : "UNKNOWN");
        auditLog.setRequestParams(truncateStr(requestParams, 2000));
        auditLog.setResponseStatus(responseStatus);
        auditLog.setLogTime(LocalDateTime.now());

        if (request != null) {
            auditLog.setIpAddress(getClientIp(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        // 保存到数据库
        try {
            auditLogService.saveLog(auditLog);
            log.info("审计日志已保存: 用户={}, 操作={}, 对象={}, IP={}, 状态={}, 耗时={}ms",
                    username, annotation.operationType(), annotation.operationObject(),
                    auditLog.getIpAddress(), responseStatus, duration);
        } catch (Exception e) {
            log.error("保存审计日志失败: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String truncateStr(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
