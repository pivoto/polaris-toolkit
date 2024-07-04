# polaris-toolkit

一个纯净的Java工具类库，提供一套标准SDK库与三方依赖的扩展库，力求功能丰富，不断优化和借鉴吸收各类优秀开源实现。

- polaris-core

  常用核心工具库，只存在Slf4j日志、JSR标准依赖，无更多的三方依赖，以保待纯净

- polaris-extra-*

  扩展工具库，针对特定用途或功能的扩展工具，有具体的三方库依赖

- polaris-builder

  代码生成器工具

## 依赖引入

### maven

- 只依赖核心库
```xml
<dependency>
  <groupId>cn.fossc.polaris.toolkit</groupId>
  <artifactId>polaris-core</artifactId>
  <version>${polaris-version}</version>
</dependency>
```
- 依赖通用库
```xml
<dependency>
  <groupId>cn.fossc.polaris.toolkit</groupId>
  <artifactId>polaris-all</artifactId>
  <type>pom</type>
  <version>${polaris-version}</version>
</dependency>
```
