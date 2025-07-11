package com.example.apilogger.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * API日志记录配置属性
 * 
 * @author 示例开发者
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "api-logger")
public class ApiLoggerProperties {

    /**
     * 是否启用API日志记录
     */
    private boolean enabled = true;

    /**
     * 日志记录级别
     */
    private LogLevel level = LogLevel.INFO;

    /**
     * 是否记录请求头
     */
    private boolean includeRequestHeaders = true;

    /**
     * 是否记录响应头
     */
    private boolean includeResponseHeaders = true;

    /**
     * 是否记录请求体
     */
    private boolean includeRequestBody = true;

    /**
     * 是否记录响应体
     */
    private boolean includeResponseBody = true;

    /**
     * 是否记录请求参数
     */
    private boolean includeRequestParams = true;

    /**
     * 是否记录执行时间
     */
    private boolean includeExecutionTime = true;

    /**
     * 最大请求体大小（字节），超过此大小的请求体将被截断
     */
    private int maxRequestBodySize = 10240; // 10KB

    /**
     * 最大响应体大小（字节），超过此大小的响应体将被截断
     */
    private int maxResponseBodySize = 10240; // 10KB

    /**
     * 需要忽略的URL路径模式
     */
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * 需要包含的URL路径模式（为空表示包含所有）
     */
    private List<String> includePatterns = new ArrayList<>();

    /**
     * 需要忽略的请求头名称
     */
    private List<String> excludeHeaders = new ArrayList<>();

    /**
     * 日志记录格式
     */
    private LogFormat format = LogFormat.JSON;

    /**
     * 日志记录级别枚举
     */
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    /**
     * 日志记录格式枚举
     */
    public enum LogFormat {
        JSON, PLAIN_TEXT
    }

    // 默认构造函数
    public ApiLoggerProperties() {
        // 默认忽略静态资源和健康检查接口
        excludePatterns.add("/actuator/**");
        excludePatterns.add("/static/**");
        excludePatterns.add("/css/**");
        excludePatterns.add("/js/**");
        excludePatterns.add("/images/**");
        excludePatterns.add("/favicon.ico");
        
        // 默认忽略敏感请求头
        excludeHeaders.add("Authorization");
        excludeHeaders.add("Cookie");
        excludeHeaders.add("X-Auth-Token");
    }

    // Getter和Setter方法
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public boolean isIncludeRequestHeaders() {
        return includeRequestHeaders;
    }

    public void setIncludeRequestHeaders(boolean includeRequestHeaders) {
        this.includeRequestHeaders = includeRequestHeaders;
    }

    public boolean isIncludeResponseHeaders() {
        return includeResponseHeaders;
    }

    public void setIncludeResponseHeaders(boolean includeResponseHeaders) {
        this.includeResponseHeaders = includeResponseHeaders;
    }

    public boolean isIncludeRequestBody() {
        return includeRequestBody;
    }

    public void setIncludeRequestBody(boolean includeRequestBody) {
        this.includeRequestBody = includeRequestBody;
    }

    public boolean isIncludeResponseBody() {
        return includeResponseBody;
    }

    public void setIncludeResponseBody(boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public boolean isIncludeRequestParams() {
        return includeRequestParams;
    }

    public void setIncludeRequestParams(boolean includeRequestParams) {
        this.includeRequestParams = includeRequestParams;
    }

    public boolean isIncludeExecutionTime() {
        return includeExecutionTime;
    }

    public void setIncludeExecutionTime(boolean includeExecutionTime) {
        this.includeExecutionTime = includeExecutionTime;
    }

    public int getMaxRequestBodySize() {
        return maxRequestBodySize;
    }

    public void setMaxRequestBodySize(int maxRequestBodySize) {
        this.maxRequestBodySize = maxRequestBodySize;
    }

    public int getMaxResponseBodySize() {
        return maxResponseBodySize;
    }

    public void setMaxResponseBodySize(int maxResponseBodySize) {
        this.maxResponseBodySize = maxResponseBodySize;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }

    public List<String> getExcludeHeaders() {
        return excludeHeaders;
    }

    public void setExcludeHeaders(List<String> excludeHeaders) {
        this.excludeHeaders = excludeHeaders;
    }

    public LogFormat getFormat() {
        return format;
    }

    public void setFormat(LogFormat format) {
        this.format = format;
    }
} 