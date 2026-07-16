# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 语言约定（必须遵守）

**所有对话、代码注释、说明文字、提交信息、文档等一律只使用中文。**

- 与用户的对话、回复一律使用中文。
- 新增或修改代码时，注释一律使用中文（保留原有英文注释的历史代码除外，但一旦改动到该处即应改为中文）。
- 变更说明、PR 描述、文档、日志输出文案等说明性文字一律使用中文。
- 代码中的标识符（类名、方法名、变量名、包名等）仍按现有命名规范使用英文，不强行翻译；但其上的注释与说明用中文。
- 命令、配置键、注解名、Maven 坐标等保持原样（它们是机器可读的标识，不属于"说明文字"）。

## 项目概述

`polaris-toolkit` 是一个纯净的 Java 工具类库，是多模块 Maven 工程，基础包名为 `io.polaris.*`。`polaris-core` 保持依赖纯净（仅依赖 slf4j + JSR 注解）；更丰富的能力位于 `polaris-extra/*`，独立的代码生成器位于 `polaris-builder`。

## 构建与测试

本仓库使用 Maven 包装器（Git Bash 下用 `./mvnw`，cmd 下用 `mvnw.cmd`）。`.mvn/maven.config` 会始终追加 `-U --fail-at-end -e`，`.mvn/jvm.config` 设置构建 JVM 堆内存——这些会作用于每一次包装器调用。

`.bin/` 下有一层构建脚本封装了 Maven：

- `.bin/main.sh` —— 核心脚本。默认 goal 为 `clean install` 并带 `-DskipTests`。参数：`-g install|deploy`（目标）、`-p <profile>`、`-central`（对应 `dist,dist-central` profile）、`-jcfc`（对应 `withDoc,dist-jcfc`）、`-e`（输出错误信息）、`-x`（调试输出）、`-c true|false`（颜色开关）。
- `.bin/install.sh` —— `main.sh -g install` 的快捷方式。
- `.bin/version.sh` —— 通过 `versions:set`/`commit` 升级版本，并重写根 pom 中的 `<project.polaris-toolkit.revision>`。运行前需先修改脚本中的 `VER=`。
- `.bin/show-active-profles.sh` —— 执行 `mvn help:active-profiles`。
- `.bin/deploy-central*.sh` / `deploy-jcfc*.sh` —— 发布到 central / jcfc / me 仓库（通过 `change-central*.sh` 脚本切换目标仓库）。

运行测试需要显式开启——默认 `skipTests=true`（`dev` profile）。使用 `sut` profile 运行单元测试（同时会启用 cobertura 覆盖率）：

```bash
./mvnw -P sut clean test                      # 跨整个 reactor 运行单元测试
./mvnw -pl polaris-core -P sut clean test      # 单模块
```

运行单个测试类时，需同时覆盖分组过滤器（默认排除 `slow,heavy,local`）与 `skipTests`：

```bash
./mvnw -pl polaris-core -P sut test -Dtest=StringsTest -Dgroups= -DexcludedGroups=
```

JUnit 5（Jupiter）与 Vintage 引擎均在类路径上。Surefire 匹配 `*Test`/`*Tests`/`*TestCase`；Failsafe 匹配 `*IT`/`*ITCase`（集成测试）。测试通过 `io.polaris.core.junit` 中定义的 JUnit5 `@Tag` 注解分类（`@Fast`、`@Heavy`、`@Slow`、`@Light`、`@Local`、`@Important`）。默认分组：`important | fast | light`；排除：`local,slow,heavy`。直接运行带标签的测试时，用 `-Dgroups= -DexcludedGroups=` 清空它们。

## Maven Profile

环境 profile（通过 `project.activeProfile` 驱动资源过滤）：`dev`（默认）、`sut`（单元测试）、`sit`、`uat`、`pre`、`pro`。

重要的结构性 profile：

- `withDemo` —— **在 IntelliJ 中自动激活**（由 `idea.version` 属性触发）。它会加入 `polaris-test` 模块（示例 / `polaris-core-test`、`polaris-boot-demo`）。在纯命令行构建时这些模块不在 reactor 中——不要假定它们参与构建。
- `withGroovy` —— 模块存在 `src/main/groovy` 时自动激活。
- `withDoc` —— 构建聚合 javadoc jar。
- `dist` —— 打包源码 + javadoc 用于发布。
- `dist-central` / `dist-jcfc` / `dist-me` —— 发布到 central / jcfc / me Maven 仓库。
- `jdk8` —— 在 JDK ≤ 8 下提供 `cn.pivoto.jdk:tools`（即 JDK 8 的 `tools.jar`）。

## 架构

### 模块分层与 core-apt -> core 的 shade 关系

依赖方向：`polaris-logging`（pom，slf4j 桥接）-> `polaris-core-apt` -> `polaris-core` -> `polaris-extra/*` 与 `polaris-builder`。`polaris-all` 是聚合 pom，引入所有已发布制品。

`polaris-core-apt` 才是**所有注解与注解处理器的归宿**，而不是 `polaris-core`：

- 稳定性 / API 契约占解位于 `io.polaris.core.annotation`：`@Published`、`@Internal`、`@Compatible`、`@Compatibles`、`@Legacy`、`@Experimental`、`@Uncompleted`、`@Warning`、`@AnnotationProcessing`、`@ByteCodeProcessing`、`@Alternative`。
- 代码生成注解：`@Access`（链式 bean 访问器）与 JDBC 系列 `@Table` / `@Column` / `@ColumnProperty` / `@ColumnProperties` / `@Id` / `@Expression`。
- 处理器 `AccessProcessor` 与 `JdbcAnnotationProcessor` 继承自 `io.polaris.core.annotation.processing.BaseProcessor`，通过 `polaris-core-apt` 的 `META-INF/services/javax.annotation.processing.Processor` 注册（注意：该 SPI 文件位于 `polaris-core/src/main/resources/META-INF/services`，在 shade 时合并进来）。
- 一份 vendored 的 squareup `javapoet` 位于 `io.polaris.core.javapoet`（原始包 `com.squareup.javapoet`）；处理器用它生成代码。

随后 `polaris-core` 使用 `maven-shade-plugin` 将 `polaris-core-apt`、ASM、javapoet、jsr305（以及 `jakarta.inject`）内联进 `polaris-core` jar，并将 `com.squareup.javapoet` 重定位为 `io.polaris.dependency.com.squareup.javapoet`，将 `org.objectweb.asm` 重定位为 `io.polaris.dependency.org.objectweb.asm`。这正是让 `polaris-core` 在运行时保持"纯净"、却仍能携带注解处理与字节码能力的机制。修改注解或处理器代码时，应编辑 `polaris-core-apt`，而非 `polaris-core`。

### JDBC 注解处理

`JdbcAnnotationProcessor` 会对每个带 `@Table` 的实体运行，并为每个实体生成两个伴生类（后缀可在 `@Table` 上配置）：

- `<Entity>Meta`（`metaSuffix`，默认 `Meta`）—— 列/表达式元数据。
- `<Entity>Sql`（`sqlSuffix`，默认 `Sql`，由 `sqlGenerated` 控制）—— SQL 构建辅助。

它通过 `AptAnnotationAttributes` / `AptAnnotations.getMergedAnnotation(...)` 跨类型层次合并注解（参见近期对 `AptAnnotationAttributes` 的类型转换支持相关工作）。

### SPI 框架

`io.polaris.core.spi.SpiLoader` 是对 `java.util.ServiceLoader` 的自定义、更丰富的替代（有意不使用日志组件，以避免与日志 SPI 实现形成循环依赖）。它支持命名 provider（`@SpiName`）、排序（`@SpiOrder`）、条件激活（`io.polaris.core.spi.condition.SpiConditions`）、wrapper/装饰器 provider，以及可插拔的加载策略（`SpiDirectoryLoadingStrategy`、`SpiPackageLoadingStrategy`）。实现注册在 `META-INF/services/io.polaris.core.*` 下——已有的扩展点包括 `CacheManagerFactory`、`MemCacheFactory`、`MetadataReaderFactory`、`AsyncThreadPoolFactory`、`JsonSerializer`、`Evaluator` 以及 SPI 内部自身。工具包的扩展点请使用 `SpiLoader`/`SpiLoaders`（及其有状态变体），而非 `ServiceLoader`。

### polaris-builder（运行时代码生成器）

`polaris-builder` 是一个**独立的运行时代码生成器**（不是注解处理器）。入口 `io.polaris.builder.Main` 接收 `--database <jdbcCfg>`、`--metadata <xmlData>`、`--code <codegen>`，并执行 `io.polaris.builder.code.Codes.generate(...)`。它读取数据库元数据（XSD schema + JDBC），并用 `polaris-builder-resource/src/main/resources/vm/` 中的 Velocity 模板渲染输出（示例输入在 `.../sample/`）。`polaris-builder-assembly` 将其打包为可运行发行版（`mainClass` = `${start-class}`）。此外还有一个数据库版本/变更日志运行器位于 `io.polaris.builder.changer`（`ChangerRunner`）。

## 代码规范

以下为项目特有规范（源自 `README.md`），通过代码评审约束：

- **包命名**：模块包遵循 `io.polaris.<module>.**`。模块间尽量减少耦合。
- **常量**：放在 `*.consts` 包中，类名以 `s` 结尾（如 `CharConsts`、`StdConsts`、`StdKeys`），声明为接口。枚举成员与全局常量使用 `UPPER_SNAKE_CASE`（大写字母与下划线分隔）。
- **异常**：放在 `*.err` 包中，类名以 `Exception` 结尾。
- **命名**：接口不加 `I` 前缀（兼容历史版本或已开放给业务继承的除外）；枚举不加 `Enum` 后缀。
- **稳定性注解**（位于 `polaris-core-apt`，`io.polaris.core.annotation`）：`@Published`（稳定发布，勿轻易破坏）、`@Internal`（框架内部使用）、`@Compatible`（向后兼容）、`@Legacy`（计划删除）、`@Experimental`（实验性）、`@Uncompleted`（未完成）、`@Warning`（额外警示）、`@AnnotationProcessing`（标注 APT 依赖的类/方法）。新增代码应按此标注。
- **Lombok**：全工程使用。每个模块都有 `lombok.config`，设置 `config.stopBubbling=true`、`toString.callSuper=CALL`、`equalsAndHashCode.callSuper=CALL`、`accessors.capitalization=BEANSPEC`、`nonNull.exceptionType=IllegalArgumentException`；`val`/`var` 标记为 `WARNING`。不要添加根级 `lombok.config`。
- **格式**（`.editorconfig`）：使用 tab，`indent_size`/`tab_width` 为 2，LF 换行，UTF-8。YAML/JSON/Markdown 用 2 空格缩进。改动时匹配周围代码风格。

## Git 规范

- 提交信息格式为 `【<type>】<subject>`
  - 常用中文类型标签：
    - `【功能新增】`: 增加特性（开发新功能、新需求）
    - `【功能变更】`: 变更或扩展增强已有功能特性
    - `【功能优化】`: 强调对已有功能特性的优化改进（设计、实现、规范等）
    - `【缺陷修复】`: 问题修改（修复缺陷）
    - `【代码重构】`: 对代码整体或局部进行重构优化
    - `【性能优化】`: 强调性能优化
    - `【文档更新】`: 文档修改
    - `【配置变更】`: 配置文件变更（工程构建、开发或运行配置）
    - `【代码测试】`: 测试代码增加或修改
    - `【代码规范】`: 格式修改（代码格式化、空白字符、标点符号、空行等）
    - `【项目构建】`: 构建变更（构建脚本、持续集成、辅助工具等）
    - `【依赖更新】`: 新增或更新依赖库版本
    - `【版本发布】`: 版本发布
    - `【版本快照】`: 版本快照
    - `【变更回退】`: 变更回退
    - `【其他杂项】`: 不影响源代码文件的其他杂项
- 仅使用 rebase 工作流：推送前用 `fetch` + `rebase` 更新；不要使用 `git pull --no-rebase`（避免无意义 merge 提交）。

## 文档

`docs/` 是一个 GitBook 工程（`book.json`、`SUMMARY.md`），以中文撰写。`docs/ChangeLog.md` 为版本变更说明。已发布文档按模块划分（核心库 / 扩展库）。
