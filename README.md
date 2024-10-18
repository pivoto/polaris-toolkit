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


## GIT 提交信息规范

提交信息格式最好遵循以下规范：

- 简单的提交信息格式：` <type> <subject>`
- 复杂的提交信息格式：
```
<type> <subject>

<body>

<footer>
```

- 格式说明：
```
head: <type> <subject>
-- type: 提交类型
   【功能新增】fea/feature: 增加特性（开发新功能、新需求）
   【功能扩展】fea/feature: 扩展或增强已有功能特性
   【缺陷修复】fix/bugfix: 问题修改（修复缺陷）
   【代码优化】opt/optimize: 代码优化或重构（优化功能实现代码）
   【性能优化】perf/performance: 强调性能优化
   【文档更新】doc: 文档修改
   【配置变更】conf: 配置文件变更（工程构建、开发或运行配置）
   【代码测试】test: 测试代码增加或修改
   【代码规范】style: 格式修改（代码格式化、空白字符、标点符号、空行等）
   【项目构建】build: 构建变更（构建脚本、持续集成、辅助工具等）
   【依赖更新】dependency: 新增或更新依赖库版本
   【版本发布】release: 版本发布
   【版本快照】snapshot: 版本快照
   【变更回退】revert: 变更回退
   【其他杂项】chore: 不影响源代码文件的其他杂项

-- subject: 信息概要


body: 信息内容
-- 变更原因
-- 解决方案
-- 影响范围


footer: 脚注
-- 关联信息
-- 其他
```

- 使用方法：
  - 可编写全局提交信息模板，如`~/.gitmessage`，内容如下
  ```
  这里写提交信息，以下是格式说明

  # head: <type> <subject>
  # -- type: 提交类型
  #     【功能新增】fea/feature: 增加特性（开发新功能、新需求）
  #     【功能扩展】fea/feature: 扩展或增强已有功能特性
  #     【缺陷修复】fix/bugfix: 问题修改（修复缺陷）
  #     【代码优化】opt/optimize: 代码优化或重构（优化功能实现代码）
  #     【性能优化】perf/performance: 强调性能优化
  #     【文档更新】doc: 文档修改
  #     【配置变更】conf: 配置文件变更（工程构建、开发或运行配置）
  #     【代码测试】test: 测试代码增加或修改
  #     【代码规范】style: 格式修改（代码格式化、空白字符、标点符号、空行等）
  #     【项目构建】build: 构建变更（构建脚本、持续集成、辅助工具等）
  #     【依赖更新】dependency: 新增或更新依赖库版本
  #     【版本发布】release: 版本发布
  #     【版本快照】snapshot: 版本快照
  #     【变更回退】revert: 变更回退
  #     【其他杂项】chore: 不影响源代码文件的其他杂项
  #
  # -- subject: 信息概要
  #
  #
  # body: 信息内容
  # -- 变更原因
  # -- 解决方案
  # -- 影响范围
  #
  #
  # footer: 脚注
  # -- 关联信息
  # -- 其他
  ```
  - 在全局git配置中设置模板文件路径，如`git config commit.template ~/.gitmessage`
  - 以后在提交时，直接输入`git commit`，会弹出提交信息输入框，输入信息后，按回车提交。
  - 在使用IDE时，通常此模板配置同样有效，也可在IDE中单独设置提交信息模板（某些IDE需要插件支持）。

