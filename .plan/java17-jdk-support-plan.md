# polaris-toolkit 对 Java 17（及以下）支持改进方案与步骤

> 创建日期：2026-07-16
> 目标模块：`polaris-core-apt`（及其构建配置）
> 状态：待实施

---

## 一、目标与范围

- **目标**：工具包能在 **JDK 8/11/17** 下被消费与运行，**APT 处理器**在这些版本下正常工作、不报警告。
- **已定策略**：用 **JDK 17 构建**、`<release>8</release>` 产出 **Java 8 字节码**（向后完全兼容，最小改动）；**暂不引入 record 支持**。
- **重点模块**：`polaris-core-apt`。

## 二、现状调研结论

### 2.1 必须改动的项

| # | 问题点 | 现状位置 | 影响 |
|---|--------|----------|------|
| 1 | APT 声明 `@SupportedSourceVersion(SourceVersion.RELEASE_8)` | `AccessProcessor`、`JdbcAnnotationProcessor` | JDK 9+ 编译输出 `Supported source version 'RELEASE_8' ... less than -source` 警告 |
| 2 | `cn.pivoto.jdk:tools 1.8`（tools.jar）依赖 | `polaris-core-apt/pom.xml`（provided） | 运行时已无 `com.sun.tools.javac.*` 使用（`BaseProcessor` 中 JavacTrees/TreeMaker 全注释），死代码；JDK 9+ 已移除 tools.jar |
| 3 | `maven-compiler-plugin` 用 `<source>/<target>` | 根 pom `pluginManagement` | 应改为 `<release>8</release>`，用目标版本 bootclasspath 校验 API，更安全 |

### 2.2 保持不变（已确认，说明理由）

| 项 | 现状 | 保持理由 |
|---|------|----------|
| `ElementScanner8` | `BaseScanner` + 三处使用 | `--release 8` 下无法引用 `ElementScanner9+`（编译期符号不存在）；`ElementScanner8` 在 JDK 17 运行时仍可用；不访问 record/module 符合"不支持 record"预期 |
| vendored javapoet 1.13.0 | `TypeSpec.Kind` 无 RECORD | 本次不引入 record 支持，保持；未来需支持 record 时再升级 vendored 版本并补 `RECORD` |
| ASM 9.4 | 支持到 class file 64（JDK 20） | JDK 17（version 61）无压力，无需升 |
| Lombok 1.18.32 | 已支持 17/21 | 无需动 |
| jsr305（`javax.annotation.Nonnull/Nullable`，约 140 处） | 普通 jar，17 下可用 | 不阻塞本次升级，作为后续独立清理项；不要误迁到 `jakarta.annotation`（语义不同且会被 shade） |
| 根 pom `jdk8` profile | JDK ≤ 8 激活，提供 tools | 仅用于遗留 JDK 8 构建场景；JDK 17 下不激活，无影响 |

### 2.3 需验证的潜在风险

1. **lombok-maven-plugin 1.18.20.0**：delombok 插件**内置 lombok 1.18.20**，对 JDK 17 支持不完整（1.18.22+ 才正式支持 17）。若 17 下 delombok 阶段报错，升级插件到 `1.18.34.0`+（其内置 lombok 跟随）。**本次最可能踩的坑。**
2. **`AsmReflects` 的 `sun.misc.Unsafe` 反射**：已有 `catch(Throwable)` 降级，17 下若被强封装限制会自动回退到非 Unsafe 路径，不崩。如需启用 Unsafe 路径，追加 `--add-opens java.base/sun.misc=ALL-UNNAMED`。
3. **maven-enforcer `requireJavaVersion`**：用 `${maven.compiler.source}`（1.8）作下限且 `<fail>false</false>`，17 下通过，无需改。

## 三、核心改动详述（必做，共 3 项）

### 改动 1：APT 的 `SourceVersion` 声明 —— 消除 JDK 9+ 警告【重点】

**改法**：
- 移除 `AccessProcessor`、`JdbcAnnotationProcessor` 上的 `@SupportedSourceVersion` 注解及对应 import；
- 在 `BaseProcessor` 中 override：

```java
@Override
public SourceVersion getSupportedSourceVersion() {
    // 运行时自动取当前 JDK 支持的最高版本：JDK 8 返回 RELEASE_8，JDK 17 返回 RELEASE_17
    return SourceVersion.latestSupported();
}
```

> ⚠️ **关键**：`AbstractProcessor` 默认 `getSupportedSourceVersion()` 在没有 `@SupportedSourceVersion` 注解时会降级返回 `RELEASE_6`，反而更糟。所以**移除注解的同时必须 override**，二者必须一起做。
>
> `SourceVersion.latestSupported()` 自 Java 6 即有，`--release 8` 下可正常编译。

**涉及文件**：
- `polaris-core-apt/src/main/java/io/polaris/core/annotation/processing/BaseProcessor.java`
- `polaris-core-apt/src/main/java/io/polaris/core/annotation/processing/AccessProcessor.java`
- `polaris-core-apt/src/main/java/io/polaris/core/jdbc/annotation/processing/JdbcAnnotationProcessor.java`

### 改动 2：清理 `cn.pivoto.jdk:tools`（tools.jar）死依赖

**改法**：从 `polaris-core-apt/pom.xml` 删除以下依赖：

```xml
<dependency>
    <groupId>cn.pivoto.jdk</groupId>
    <artifactId>tools</artifactId>
    <version>1.8</version>
    <scope>provided</scope>
</dependency>
```

> 根 pom 的 `jdk8` profile 保留，仅用于遗留 JDK 8 构建场景。

### 改动 3：`maven-compiler-plugin` 由 `<source>/<target>` 改为 `<release>8</release>`

**改法**：在根 pom `pluginManagement` 的 `maven-compiler-plugin` 配置中，用 `<release>8</release>` 替换 `<source>${maven.compiler.source}</source>` / `<target>${maven.compiler.target}</target>` 两行（二者互斥，需移除原 source/target）。

**好处**：`--release` 用目标版本的 bootclasspath 校验 API，能阻止误用 9+ API（如 `List.of`）写出在 8 下 `NoSuchMethodError` 的代码，比 source/target 更安全。

> 注意：**保留 `maven.compiler.source` 属性定义**（值仍为 `1.8`），因其被以下处引用：
> - `maven-enforcer` 的 `requireJavaVersion`（根 pom 第 1062 行）
> - shade manifest 的 `X-Compiler-Source`（`polaris-core/pom.xml` 第 274 行）
> - `withGroovyEclipse` profile 的 compiler 配置（根 pom 第 1833 行）

## 四、实施步骤（有序）

1. 建 feature 分支（按本项目 git 规范，rebase 工作流）。
2. 改 `BaseProcessor.java`：新增 `getSupportedSourceVersion()` override；移除 `AccessProcessor`、`JdbcAnnotationProcessor` 的 `@SupportedSourceVersion` 注解及 import。
3. 删 `polaris-core-apt/pom.xml` 的 `cn.pivoto.jdk:tools` 依赖。
4. 改根 pom `pluginManagement` 的 `maven-compiler-plugin`：`<release>8</release>` 替换 `<source>/<target>`；保留 `maven.compiler.source` 属性。
5. JDK 17 下全量构建 + 测试：`./mvnw -P sut clean test`。
6. **验证产物字节码为 8**：`javap -verbose polaris-core/target/classes/io/polaris/core/...class | grep major`，应为 `major version: 52`。
7. **验证 APT 无 warning 且正常生成**：构建 `polaris-core` 时无 `Supported source version ... less than -source` 警告；`*Meta`/`*Sql` 及 Access 伴生类正常生成（可在 `polaris-core-test` 的 `@Table`/`@Access` demo 上验证）。
8. （按需）若第 5 步 delombok 报错，升 `lombok-maven-plugin` 版本后重试。
9. 回归：分别在 **JDK 8** 与 **JDK 17** 两个消费环境跑一次 `polaris-test` 下游 demo，确认兼容。

## 五、验证清单

- [ ] JDK 17 下 `./mvnw -P sut clean test` 全绿。
- [ ] 产物 `.class` 的 `major version: 52`（Java 8）。
- [ ] 构建 `polaris-core` 时无 `Supported source version ... less than -source` 警告。
- [ ] APT 生成的 `*Meta`/`*Sql`/Access 伴生类内容与升级前一致（diff 为空）。
- [ ] JDK 8 消费方仍能正常编译运行（回归）。

## 六、风险与回退

- 改动面小且集中（`BaseProcessor` 1 处 + 2 个处理器注解 + 2 个 pom），回退成本低。
- 最大不确定性在 **delombok 插件**（风险点 1）；若升级插件后仍有问题，可临时在该模块关闭 delombok（注释 `lombok-maven-plugin` 的 `generate-sources` execution）绕过，不影响 APT 主流程。
- `--release 8` 若暴露出对 9+ API 的隐式依赖（之前 source/target 8 在 17 下会"放行"），属隐藏问题暴露，需逐处修复或下移到 `polaris-extra`（这些依赖本就不该出现在 `polaris-core`）。

## 七、不在本次范围（后续可选）

- 引入 record 实体支持（需升级 vendored javapoet、`ElementScanner` 改 9+/16+、`TypeSpec` 支持 `RECORD` kind）。
- jsr305 → 现代 null 检查注解的迁移清理。
