package com.example.apilogger.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * API日志记录拦截器
 * 
 * @author 示例开发者
 * @since 1.0.0
 */
public class ApiLoggerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggerInterceptor.class);
    
    private final ApiLoggerProperties properties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher;
    
    private static final String STOPWATCH_ATTRIBUTE = "API_LOGGER_STOPWATCH";
    private static final String REQUEST_BODY_ATTRIBUTE = "API_LOGGER_REQUEST_BODY";

    public ApiLoggerInterceptor(ApiLoggerProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!properties.isEnabled() || !shouldLog(request)) {
            return true;
        }

        // 开始计时
        if (properties.isIncludeExecutionTime()) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(STOPWATCH_ATTRIBUTE, stopWatch);
        }

        // 缓存请求体
        if (properties.isIncludeRequestBody()) {
            String requestBody = getRequestBody(request);
            request.setAttribute(REQUEST_BODY_ATTRIBUTE, requestBody);
        }

        // 记录请求日志
        logRequest(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!properties.isEnabled() || !shouldLog(request)) {
            return;
        }

        // 停止计时
        StopWatch stopWatch = (StopWatch) request.getAttribute(STOPWATCH_ATTRIBUTE);
        if (stopWatch != null) {
            stopWatch.stop();
        }

        // 记录响应日志
        logResponse(request, response, stopWatch, ex);
    }

    /**
     * 判断是否需要记录日志
     */
    private boolean shouldLog(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // 检查排除模式
        for (String pattern : properties.getExcludePatterns()) {
            if (pathMatcher.match(pattern, uri)) {
                return false;
            }
        }
        
        // 检查包含模式
        if (!properties.getIncludePatterns().isEmpty()) {
            boolean matched = false;
            for (String pattern : properties.getIncludePatterns()) {
                if (pathMatcher.match(pattern, uri)) {
                    matched = true;
                    break;
                }
            }
            return matched;
        }
        
        return true;
    }

    /**
     * 记录请求日志
     */
    private void logRequest(HttpServletRequest request) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "REQUEST");
            logData.put("timestamp", new Date());
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("remoteAddr", getClientIpAddress(request));
            
            // 记录请求参数
            if (properties.isIncludeRequestParams()) {
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (!parameterMap.isEmpty()) {
                    Map<String, Object> params = new LinkedHashMap<>();
                    parameterMap.forEach((key, values) -> {
                        if (values.length == 1) {
                            params.put(key, values[0]);
                        } else {
                            params.put(key, Arrays.asList(values));
                        }
                    });
                    logData.put("parameters", params);
                }
            }
            
            // 记录请求头
            if (properties.isIncludeRequestHeaders()) {
                Map<String, String> headers = new LinkedHashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    if (!shouldExcludeHeader(headerName)) {
                        headers.put(headerName, request.getHeader(headerName));
                    }
                }
                if (!headers.isEmpty()) {
                    logData.put("headers", headers);
                }
            }
            
            // 记录请求体
            if (properties.isIncludeRequestBody()) {
                String requestBody = (String) request.getAttribute(REQUEST_BODY_ATTRIBUTE);
                if (StringUtils.isNotBlank(requestBody)) {
                    logData.put("body", truncateContent(requestBody, properties.getMaxRequestBodySize()));
                }
            }
            
            writeLog(logData);
            
        } catch (Exception e) {
            logger.error("记录请求日志失败", e);
        }
    }

    /**
     * 记录响应日志
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, StopWatch stopWatch, Exception ex) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("type", "RESPONSE");
            logData.put("timestamp", new Date());
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("status", response.getStatus());
            
            // 记录执行时间
            if (properties.isIncludeExecutionTime() && stopWatch != null) {
                logData.put("executionTime", stopWatch.getTotalTimeMillis() + "ms");
            }
            
            // 记录响应头
            if (properties.isIncludeResponseHeaders()) {
                Map<String, String> headers = new LinkedHashMap<>();
                for (String headerName : response.getHeaderNames()) {
                    if (!shouldExcludeHeader(headerName)) {
                        headers.put(headerName, response.getHeader(headerName));
                    }
                }
                if (!headers.isEmpty()) {
                    logData.put("headers", headers);
                }
            }
            
            // 记录异常信息
            if (ex != null) {
                logData.put("exception", ex.getClass().getSimpleName());
                logData.put("exceptionMessage", ex.getMessage());
            }
            
            writeLog(logData);
            
        } catch (Exception e) {
            logger.error("记录响应日志失败", e);
        }
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.warn("读取请求体失败", e);
        }
        return sb.toString();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 判断是否需要排除某个请求头
     */
    private boolean shouldExcludeHeader(String headerName) {
        return properties.getExcludeHeaders().stream()
                .anyMatch(excludeHeader -> excludeHeader.equalsIgnoreCase(headerName));
    }

    /**
     * 截断内容
     */
    private String truncateContent(String content, int maxSize) {
        if (content == null) {
            return null;
        }
        
        if (content.length() <= maxSize) {
            return content;
        }
        
        return content.substring(0, maxSize) + "... [截断]";
    }

    /**
     * 写入日志
     */
    private void writeLog(Map<String, Object> logData) {
        try {
            String logMessage;
            if (properties.getFormat() == ApiLoggerProperties.LogFormat.JSON) {
                logMessage = objectMapper.writeValueAsString(logData);
            } else {
                logMessage = formatPlainText(logData);
            }
            
            // 根据配置的级别写入日志
            switch (properties.getLevel()) {
                case TRACE:
                    logger.trace(logMessage);
                    break;
                case DEBUG:
                    logger.debug(logMessage);
                    break;
                case INFO:
                    logger.info(logMessage);
                    break;
                case WARN:
                    logger.warn(logMessage);
                    break;
                case ERROR:
                    logger.error(logMessage);
                    break;
                default:
                    logger.info(logMessage);
            }
            
        } catch (Exception e) {
            logger.error("写入日志失败", e);
        }
    }

    /**
     * 格式化纯文本日志
     */
    private String formatPlainText(Map<String, Object> logData) {
        StringBuilder sb = new StringBuilder();
        sb.append("API日志 - ");
        
        logData.forEach((key, value) -> {
            sb.append(key).append("=").append(value).append(" ");
        });
        
        return sb.toString().trim();
    }
} 