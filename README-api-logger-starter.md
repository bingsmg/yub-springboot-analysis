# API Logger Spring Boot Starter

> 一个符合 Spring Boot 第三方 starter 命名规范的自动配置库，用于自动记录 HTTP 请求和响应信息

## 📋 项目概述

本项目演示了如何按照 **Spring Boot 第三方 starter 正确命名规范** 创建一个功能完整的自定义 starter。它提供了 API 请求日志记录功能，支持丰富的配置选项和多种日志格式。

## 🎯 核心特性

- ✅ **符合规范**：严格遵循 Spring Boot 第三方 starter 命名规范
- 🚀 **开箱即用**：零配置启动，自动装配
- 📝 **功能丰富**：记录请求头、响应头、请求体、响应体、执行时间
- 🎯 **智能过滤**：支持 URL 模式匹配和请求头过滤
- 📊 **多种格式**：JSON 和纯文本日志格式
- ⚡ **性能监控**：自动记录接口执行时间
- 🔒 **安全考虑**：默认排除敏感信息
- 🎛️ **高度可配置**：所有功能均可通过配置开关控制

## 📁 项目结构

```
├── api-logger-spring-boot-starter/          # 主 starter 模块
│   └── pom.xml                              # 依赖聚合，无 Java 代码
├── api-logger-spring-boot-autoconfigure/    # 自动配置模块
│   ├── pom.xml                              # 核心依赖定义
│   ├── src/main/java/
│   │   └── com/example/apilogger/autoconfigure/
│   │       ├── ApiLoggerProperties.java     # 配置属性类
│   │       ├── ApiLoggerInterceptor.java    # 核心拦截器实现
│   │       └── ApiLoggerAutoConfiguration.java # 自动配置类
│   └── src/main/resources/META-INF/
│       ├── spring.factories                 # 自动配置声明
│       └── spring-configuration-metadata.json # IDE 配置提示
├── api-logger-demo/                         # 演示应用
│   ├── pom.xml                              
│   └── src/main/java/
│       └── com/example/demo/
│           ├── ApiLoggerDemoApplication.java
│           ├── entity/User.java
│           ├── repository/UserRepository.java
│           ├── controller/UserController.java
│           └── config/DataInitializer.java
└── analysis/                                # 文档
    ├── 1.env prepare.md                     # 环境准备
    ├── 2.spring-starters.md                # Starter 基础概念
    └── Spring-Boot-Starter完整开发指南.md    # 📘 完整开发指南
```

## 🏗️ 命名规范对比

### ✅ 正确的第三方命名规范（本项目使用）

| 组件 | 命名 | 说明 |
|------|------|------|
| **Starter 模块** | `api-logger-spring-boot-starter` | 第三方 starter 正确格式 |
| **自动配置模块** | `api-logger-spring-boot-autoconfigure` | 对应的自动配置模块 |
| **GroupId** | `com.example` | 使用第三方域名 |
| **包名** | `com.example.apilogger.autoconfigure` | 避免使用 Spring 官方包名 |
| **配置前缀** | `api-logger` | 避免使用 `spring` 前缀 |

### ❌ 错误的命名方式（避免使用）

| 组件 | 错误示例 | 问题说明 |
|------|----------|----------|
| **Starter 模块** | ~~`spring-boot-starter-api-logger`~~ | 这是官方格式，仅供 Spring Boot 团队使用 |
| **包名** | ~~`org.springframework.boot.autoconfigure`~~ | 官方包名，第三方不能使用 |
| **配置前缀** | ~~`spring.api-logger`~~ | `spring` 前缀保留给官方使用 |

## 🚀 快速开始

### 1. 构建项目

```bash
# 构建自动配置模块
cd api-logger-spring-boot-autoconfigure
mvn clean install

# 构建 starter 模块
cd ../api-logger-spring-boot-starter
mvn clean install
```

### 2. 运行演示应用

```bash
# 运行演示应用
cd ../api-logger-demo
mvn spring-boot:run
```

### 3. 测试功能

```bash
# 获取用户列表
curl http://localhost:8080/api/users

# 创建新用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户","email":"test@example.com","age":25}'

# 搜索用户
curl "http://localhost:8080/api/users/search?name=张"

# 模拟异常（测试异常日志）
curl http://localhost:8080/api/users/error
```

## 📖 配置说明

### 基础配置

```yaml
api-logger:
  enabled: true                    # 启用/禁用日志记录
  level: INFO                      # 日志级别
  format: JSON                     # 日志格式：JSON | PLAIN_TEXT
```

### 内容控制

```yaml
api-logger:
  include-request-headers: true    # 记录请求头
  include-response-headers: true   # 记录响应头
  include-request-body: true       # 记录请求体
  include-response-body: false     # 记录响应体（通常关闭以减少日志量）
  include-request-params: true     # 记录请求参数
  include-execution-time: true     # 记录执行时间
  max-request-body-size: 10240     # 请求体大小限制（字节）
  max-response-body-size: 10240    # 响应体大小限制（字节）
```

### 过滤规则

```yaml
api-logger:
  exclude-patterns:               # 排除的 URL 模式
    - "/actuator/**"
    - "/static/**"
    - "/favicon.ico"
  include-patterns:               # 包含的 URL 模式（可选）
    - "/api/**"
  exclude-headers:                # 排除的请求头
    - "Authorization"
    - "Cookie"
    - "X-Auth-Token"
```

## 📝 日志示例

### JSON 格式输出

```json
{
  "type": "REQUEST",
  "timestamp": "2024-01-15T10:30:45.123",
  "method": "POST",
  "uri": "/api/users",
  "remoteAddr": "127.0.0.1",
  "parameters": {},
  "headers": {
    "Content-Type": "application/json",
    "User-Agent": "curl/7.68.0"
  },
  "body": "{\"name\":\"测试用户\",\"email\":\"test@example.com\",\"age\":25}"
}
```

### 纯文本格式输出

```
API日志 - type=REQUEST timestamp=Mon Jan 15 10:30:45 CST 2024 method=POST uri=/api/users remoteAddr=127.0.0.1
```

## 📚 完整文档

**🔗 详细的开发指南请参考：[Spring Boot Starter 完整开发指南](analysis/3.Spring-Boot-Starter完整开发指南)**

该文档包含：

- 📖 **Spring Boot Starter 基础概念** - 深入理解 starter 机制
- 🏷️ **命名规范详解** - 官方 vs 第三方命名规范
- 🏗️ **项目架构设计** - 标准架构模式和模块职责
- 📝 **完整实现流程** - 从零开始的详细步骤
- 🛠️ **实战案例分析** - API Logger Starter 技术实现
- 📖 **使用指南** - 配置选项和使用示例
- 💡 **最佳实践** - 生产环境配置和常见陷阱
- 🔧 **故障排除** - 常见问题和解决方案

## 🔧 技术实现要点

### 1. 条件装配

使用 Spring Boot 的条件注解实现智能装配：

```java
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, ApiLoggerInterceptor.class})
@ConditionalOnProperty(name = "api-logger.enabled", matchIfMissing = true)
```

### 2. 配置属性绑定

```java
@ConfigurationProperties(prefix = "api-logger")
public class ApiLoggerProperties {
    // 配置属性定义
}
```

### 3. 自动配置注册

在 `META-INF/spring.factories` 中声明：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.apilogger.autoconfigure.ApiLoggerAutoConfiguration
```

### 4. IDE 智能提示

通过 `META-INF/spring-configuration-metadata.json` 提供配置提示。

## 📚 学习价值

本项目完整展示了：

1. **正确的命名规范**：严格遵循 Spring Boot 第三方 starter 命名约定
2. **标准的项目结构**：starter + autoconfigure 分离架构
3. **完整的自动配置**：条件装配、配置绑定、元数据提供
4. **生产级实现**：异常处理、性能优化、安全考虑
5. **详细的文档**：完整的使用指南和最佳实践

## 🎓 扩展学习

- 查看 `analysis/Spring-Boot-Starter完整开发指南.md` 了解详细开发流程
- 参考演示应用了解集成使用方式
- 研究源码了解 Spring Boot 自动配置机制

## 📞 总结

这个项目不仅提供了一个实用的 API 日志记录功能，更重要的是展示了如何正确地创建符合 Spring Boot 规范的第三方 starter。它是学习 Spring Boot 自动配置机制和 starter 开发的绝佳案例。

---

**注意**：本项目专门纠正了命名规范问题，展示了第三方开发者应该如何正确命名和组织 Spring Boot starter 项目。 