# polaris-toolkit 在 JDK 17 运行时的不兼容写法排查报告

> 创建日期：2026-07-22
> 最后更新：2026-07-22（§1.1 JAXB 修复、§3.1a reflectasm 删除、§3.1 cglib 升级并纠正 asm 论述）
> 排查场景：**JDK 8 编译（产物 Java 8 字节码）+ 在 JDK 17（及更高版本）运行**
> 关联文档：`.plan/java17-jdk-support-plan.md`（编译层计划，聚焦 `polaris-core-apt`）
> 状态：排查完成，待评审（§1.1、§3.1、§3.1a 已处理）

---

## 〇、背景与总体结论

本仓库现以 **JDK 8**（`maven-toolchains-plugin` 锁定 JDK 8）编译，产出 Java 8 字节码（major version 52），需保证产物能在 **JDK 17** 运行。本次排查覆盖两个层面：

1. **运行时代码层**：反射访问 JDK 内部 API、动态类定义、脚本引擎、JAXB、安全管理器等。
2. **构建/依赖层**：编译器配置、第三方库版本与 JDK 17 的兼容性。

### 总体结论

- 项目已有一份编译层计划 `.plan/java17-jdk-support-plan.md`，且其中**改动 1（`BaseProcessor.getSupportedSourceVersion()` 返回 `SourceVersion.latestSupported()`）已落地**（`polaris-core-apt/.../BaseProcessor.java:65-68`，全仓已无 `@SupportedSourceVersion` 注解残留）。
- `polaris-core` 的 ASM 增强路径（`AsmReflects` / `AccessClassLoader`）已做较完整的 **JDK 9+ 适配**，主路径不依赖 `--add-opens`。
- **§1.1 JAXB 运行时实现缺失 -- 已修复（2026-07-22）**：`polaris-extension/pom.xml` 已补 `org.glassfish.jaxb:jaxb-runtime:2.3.3`（`optional=true`），与既有 `jakarta.xml.bind-api:2.3.3` 配对，API 包名同为 `javax.xml.bind.*`，与 `Jaxb.java` import 一致，JDK 17 下 JAXB 功能可用。
- **§3.1a reflectasm 死依赖 -- 已删除（2026-07-22）**：排查确认全仓无任何代码引用 `com.esotericsoftware.reflectasm`（`polaris-core/asm/reflect` 下的 Access 系列为项目自有实现），已从三处 pom 删除该依赖声明。
- **§3.1 cglib -- 已修复（2026-07-22）**：仅测试代码（`CglibTest`）使用，已从 3.2.5 升级到 3.3.0 并将作用域由 `optional=true` 改为 `scope=test`。**认知修正（核实 cglib-3.3.0.jar 后）**：cglib 3.x **早已不做 ASM relocate**（那是 2.x 做法），3.3.0 用**外部** `org.ow2.asm:asm`（jar 内无 asm 类，`Enhancer.class` 引用 `org/objectweb/asm/*`）。cglib pom（cglib-parent）声明的 asm 7.1 仅为编译期期望版本；项目根 pom `dependencyManagement` 把 asm 统一锁为 9.4，Maven 调解覆盖传递依赖的 7.1，故 test classpath 实际为 asm 9.4（支持 class file 61）。升级 3.3.0 的真实意义是 **asm API 二进制兼容性**（3.2.5 按 asm 6.x API 编译，在 asm 9.4 上可能 NoSuchMethodError；3.3.0 按 asm 7.1 API 编译，更接近 asm 9.x，且被 Spring 5/6 广泛用于 asm 9.x 环境）。
- **仍需处理 1 处**：`JavaScriptEvaluator` 依赖已被移除的 Nashorn（JDK 15+ 移除）。
- 其余反射 JDK 内部 API 的写法（`Field.modifiers`、`com.sun` 内部 DocumentBuilder、`ClassLoader.defineClass`）均已带降级处理，维持现状即可。

---

## 一、会导致 JDK 17 运行时功能不可用的写法（建议处理）

### 1.1 JAXB 仅声明 API，缺运行时实现 ✅ 已修复（2026-07-22）

- **位置**：
  - `polaris-extra/polaris-extension/src/main/java/io/polaris/xml/Jaxb.java:27,50` -- 直接 `JAXBContext.newInstance(...)`，异常处 `throw new RuntimeException(e)`，无降级。
  - `polaris-extra/polaris-extension/pom.xml:195-206`。
- **原问题**：JAXB（`javax.xml.bind.*`）自 **JDK 11 起从 JDK 中移除**（JEP 320）。原配置仅引入 `jakarta.xml.bind:jakarta.xml.bind-api:2.3.3`（仅 API，不含实现），JDK 17 下 `JAXBContext.newInstance(...)` 会抛：
  ```
  JAXBException: Implementation of JAXB-API has not been found on module path or classpath
  ```
- **修复**：在 `polaris-extension/pom.xml` 补运行时实现依赖：
  ```xml
  <dependency>
      <groupId>org.glassfish.jaxb</groupId>
      <artifactId>jaxb-runtime</artifactId>
      <version>2.3.3</version>
      <optional>true</optional>
  </dependency>
  ```
- **验证要点**：
  - `jaxb-runtime:2.3.3` 与既有 `jakarta.xml.bind-api:2.3.3` 为官方配对版本，API 包名同为 `javax.xml.bind.*`，与 `Jaxb.java` 的 `import javax.xml.bind.*` 完全一致。
  - `optional=true`：不强制传递给下游消费方；需要 JAXB 功能时由实现类 SPI（`META-INF/services/javax.xml.bind.JAXBContextFactory`）自动加载。
  - JDK 17 下 `JAXBContext.newInstance(...)` 可正常找到实现，不再抛异常。
- **状态**：已修复，关闭此项。

### 1.2 JavaScript 脚本引擎依赖已移除的 Nashorn ⚠️ 高（功能降级）

- **位置**：
  - `polaris-core/src/main/java/io/polaris/core/script/JavaScriptEvaluator.java:13` -- 引擎名常量 `"javascript"`。
  - `polaris-core/src/main/java/io/polaris/core/script/AbstractStandardEvaluator.java:36` -- `manager.getEngineByName("javascript")`。
  - `polaris-core/src/main/java/io/polaris/core/json/DefaultJsonSerializer.java:206` -- 也依赖 `ScriptEvaluators.getEvaluator("javascript")`。
- **原因**：Nashorn（JS 引擎）自 **JDK 15 起被移除**（JEP 372）。JDK 17 下 `getEngineByName("javascript")` 返回 `null`。
- **现状降级行为**：
  - 构造期 `AbstractStandardEvaluator.java:48` 打印 `log.error("脚本引擎不支持:{}", engineName)`。
  - `eval` 时 `AbstractStandardEvaluator.java:76` 抛 `ScriptEvalException("脚本引擎不支持:...")`。
- **影响**：JDK 17 下 JS 表达式求值功能不可用（含 `DefaultJsonSerializer` 中依赖 JS 求值器的路径）。`GroovyEvaluator` 同理依赖 `groovy-jsr223`（由 `withGroovy` 模块提供），JDK 17 下若 groovy 引擎在类路径上则可用。
- **建议**：需消费方引入独立 JS 引擎，例如：
  - `org.openjdk.nashorn:nashorn-core`（Nashorn 独立版，兼容旧代码），或
  - GraalVM `org.graalvm.js:js-scriptengine` + `org.graalvm.js:js`（功能更强，但 API 有差异）。
  - 建议在 `JavaScriptEvaluator` 类注释中注明 JDK 17 的依赖要求。

---

## 二、已做降级适配，但存在功能受限 / 边界降级

### 2.1 ASM 动态类定义的反射回退路径 ✅ 基本可用

- **位置**：
  - `polaris-core/src/main/java/io/polaris/core/asm/internal/AsmReflects.java:269-298`（核心 `defineClass`）。
  - `polaris-core/src/main/java/io/polaris/core/asm/generator/AbstractClassGenerator.java:451`（调用方，传 `targetClass`）。
  - `polaris-core/src/main/java/io/polaris/core/asm/reflect/AccessClassLoader.java:170-188`（访问类加载器定义）。
- **适配情况**：
  - JDK 9+ 优先走 `MethodHandles.privateLookupIn(targetClass, lookup).defineClass(bytes)`（**无需 `--add-opens`**），将类定义到目标类型的加载器/模块，可访问 protected/package 成员。
  - 回退路径 1：反射 `ClassLoader.defineClass`（JDK 17 强封装下 `setAccessible` 失败，需 `--add-opens java.base/java.lang=ALL-UNNAMED`）。
  - 回退路径 2：`sun.misc.Unsafe#defineClass`（需 `--add-opens java.base/sun.misc=ALL-UNNAMED`）。
  - `AccessClassLoader` 最终还有 protected `defineClass`（本地加载器，JDK 17 可用）兜底。
- **边界风险**：当目标类型位于**未 open 的命名模块**（如 JDK 内部 `java.*` 类、模块化用户代码）时，`privateLookupIn` 失败 -> 回退到本地定义 -> 生成的访问类访问目标 package-private 成员会被模块系统拒绝。对**普通应用类（classpath、非模块化）无影响**。
- **结论**：维持现状。如需对 JDK 内部类生成访问器，加 `--add-opens`。

### 2.2 `Reflects.removeFinalModifier` 反射 `Field.modifiers` ✅ 已降级

- **位置**：`polaris-core/src/main/java/io/polaris/core/reflect/Reflects.java:1223-1233`。
- **写法**：`Field.class.getDeclaredField("modifiers")` -> `setAccessible(true)` -> `setInt(field, ...)`。
- **原因**：JDK 16+ 强封装下，对 `java.lang.reflect.Field` 的私有 `modifiers` 字段反射会抛 `InaccessibleObjectException`。
- **现状**：已 `catch (RuntimeException e)` 返回 `false`，并在 `Reflects.java:1214-1219` 注释说明需 `--add-opens java.base/java.lang.reflect=ALL-UNNAMED` 才能恢复去除 final 的能力。功能受限但不崩溃。

### 2.3 `Xml` 默认 DocumentBuilderFactory 指向 JDK 内部类 ✅ 已回退

- **位置**：
  - `polaris-core/src/main/java/io/polaris/core/xml/Xml.java:85` -- 默认值 `"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"`（JDK 内部非导出包）。
  - `Xml.java:558-575`（`createDocumentBuilderFactory`）-- 已 `catch (Throwable ignored)` 回退到 `DocumentBuilderFactory.newInstance()`。
- **结论**：JDK 17 下首次按类名查找失败一次后自动用 JDK 默认实现，可用。

### 2.4 `MemoryClassLoader` 对 `URLClassLoader` 的类型判定（行为差异，非致命）

- **位置**：`polaris-core/src/main/java/io/polaris/core/compiler/MemoryClassLoader.java:195`。
- **写法**：`if (loader instanceof URLClassLoader && !ClassLoader.getSystemClassLoader().equals(loader))`。
- **原因**：JDK 9+ 系统类加载器（`AppClassLoader`）不再继承 `URLClassLoader`，该分支在 JDK 17 下对系统类加载器恒为 `false`，无法从父加载器收集 URL。
- **现状**：有 `java.class.path`（`MemoryClassLoader.java:232`）兜底，动态编译 classpath 仍能拼出，属行为差异非致命。
- **关联**：`MemoryCompiler.java:56` 的 `ToolProvider.getSystemJavaCompiler()` 在 JDK 9+ 仍可用（**无需 tools.jar**）。✅

### 2.5 `JavaAccessControllers` / `SecurityManager` 用法 ✅ 已规避

- **位置**：`polaris-core/src/main/java/io/polaris/core/lang/JavaAccessControllers.java:18,23`。
- **写法**：`System.getSecurityManager() != null ? AccessController.doPrivileged(...) : action.get/call()`。
- **原因**：`SecurityManager` 自 JDK 17 起废弃（JEP 411），`AccessController.doPrivileged` 已废弃。
- **现状**：JDK 17 下 `getSecurityManager()` 恒为 `null`，直接走 `action.get()/call()`，等价于普通调用，无副作用。注释已标记便于后续清理。全仓其余 `SecurityManager` 命中（`JdkProxies.java:88`、`AggregateResourceBundles.java:104`）均为注释说明，代码已规避。✅

---

## 三、依赖版本与 JDK 17 运行时的潜在冲突

### 3.1 cglib 旧版本与项目 ASM 9.4 的 API 兼容 ✅ 已修复（2026-07-22）

- **位置**：
  - `polaris-extra/polaris-extension/pom.xml:73-78` -- 原 `cglib:cglib:3.2.5`（2017 年），`<optional>true</optional>`。
  - 唯一使用点：`polaris-extra/polaris-extension/src/test/java/io/polaris/bytecode/cglib/CglibTest.java`（import `net.sf.cglib.proxy.Enhancer/MethodInterceptor/MethodProxy`，做线程池代理拦截演示）。
- **排查结论**：
  - cglib **非死依赖**（有测试代码使用），不能像 reflectasm 那样直接删除。
  - `polaris-core/asm/proxy/Enhancer`、`aop/AsmProxyFactory` 等是**项目自有实现**（包名 `io.polaris.core.asm.proxy`，import `org.objectweb.asm.*`），与 cglib 无关，命名属巧合。
  - **认知修正（解包 `cglib-3.3.0.jar` 核实后）**：cglib 3.x **早已不做 ASM relocate**（那是 cglib 2.x 的做法）。3.3.0 jar 内**既无** `net/sf/cglib/asm/**`**也无** `org/objectweb/asm/**`，`Enhancer.class` 字节码引用的是 `org/objectweb/asm/ClassVisitor`/`Label`/`Type` -- 即 cglib 运行时用**外部** `org.ow2.asm:asm`，不内嵌。
  - **关于 asm 7.1（核实 `cglib-parent-3.3.0.pom` 确声明 `<asm.version>7.1</asm.version>`）**：7.1 仅为 cglib 的**编译期期望版本**，非运行期必然版本。项目根 pom `dependencyManagement` 把 asm 统一锁为 `${dependency.asm.version}`=9.4（`pom.xml:417-419`），Maven 依赖调解决定：`dependencyManagement` 版本**覆盖**一切传递依赖声明的版本。故 cglib 3.3.0（test）传递来的 asm:7.1 会被强制解析为 **asm:9.4**。test classpath 实际 asm=9.4（支持 class file 61 / JDK 17），**7.1 不会成为运行版本**，不存在"asm 7.1 不支持 class 61"的问题。
  - **升级 3.3.0 的真实意义**：是 **ASM API 二进制兼容性**，而非 class file 支持（后者由项目统一 asm 9.4 保证、与 cglib 版本无关）。3.2.5 按 asm 6.x API 编译，在 asm 9.4 上可能 `NoSuchMethodError`；3.3.0 按 asm 7.1 API 编译，更接近 asm 9.x，且被 Spring Framework 5.3/6.0 广泛用于 asm 9.x 环境，实践可靠。
- **修复**：在 `polaris-extension/pom.xml` 调整 cglib 依赖：
  ```xml
  <dependency>
      <groupId>cglib</groupId>
      <artifactId>cglib</artifactId>
      <version>3.3.0</version>
      <scope>test</scope>
  </dependency>
  ```
  - 版本 `3.2.5` -> `3.3.0`（cglib 最后版本，2022 年，asm 7.1 API，运行时随项目 asm 9.4）。
  - `<optional>true</optional>` -> `<scope>test</scope>`：事实上仅测试代码使用，改 test scope 更准确（主代码编译期不再可见 cglib，避免误用）。
  - **无需**对 cglib 显式 exclude/指定 asm 版本：项目 `dependencyManagement` 已统一 asm 9.4，自动覆盖 cglib 期望的 7.1。
- **残留风险（小）**：cglib 3.3.0（asm 7.1 API）跑在 asm 9.4 上属"老 API 调用新 asm"，ASM 向后兼容良好，但理论上有个别方法签名变化风险。最彻底确认方式是在 **JDK 17 下实跑 `CglibTest`**（当前 toolchains 锁 JDK 8，需临时指定 JDK 17 跑 surefire）。若代理 `ThreadPoolExecutor` 触发强封装，surefire `argLine` 加 `--add-opens java.base/java.util.concurrent=ALL-UNNAMED`。
- **状态**：已修复，关闭此项。

### 3.1a reflectasm 死依赖 ✅ 已删除（2026-07-22）

- **原问题**：原 `pom.xml`、`polaris-extra/pom.xml`、`polaris-extension/pom.xml` 三处声明 `com.esotericsoftware:reflectasm:1.11.9`（`optional=true`），基于 ASM 6.x，与项目统一 ASM 9.4 存在潜在 API 错配。
- **排查结论**：全仓（含测试）**无任何 `com.esotericsoftware` / `reflectasm` 的 import 或引用**。`polaris-core/asm/reflect` 下的 `MethodAccess` / `FieldAccess` / `ConstructorAccess` / `PublicConstructorAccess` / `AccessClassLoader` 均为**项目自有实现**（包名 `io.polaris.core.asm.reflect`，import `org.objectweb.asm.*` 即项目统一 ASM 9.4），仅在 javadoc `@see` / `package-info.java` 中标注"参考 reflectasm 项目"。即 reflectasm 是一个**完全未被使用的死依赖**。
- **修复**：删除三处 pom 中的 reflectasm 声明：
  - 根 `pom.xml`：删除 `<dependency.reflectasm.version>` 版本属性 + dependencyManagement 中的依赖块。
  - `polaris-extra/pom.xml`：删除 `<dependency.reflectasm.version>` 版本属性 + dependencyManagement 中的依赖块。
  - `polaris-extra/polaris-extension/pom.xml`：删除实际依赖块（原 `optional=true`）。
- **验证**：删除后全仓 grep `reflectasm` / `esotericsoftware` 仅余代码 javadoc 中的"参考 reflectasm 项目"来源链接（属设计来源标注，不构成依赖，保留）。
- **状态**：已删除，关闭此项。

### 3.2 字节码/代理库版本核对（多数 OK）

| 库 | 版本 | 位置 | JDK 17 兼容性 |
|---|---|---|---|
| ASM (org.ow2.asm) | 9.4 | `pom.xml:97` | ✅ OK。9.0+ 即支持 class file 61（JDK 17） |
| javassist | 3.29.2-GA | `pom.xml:98` | ✅ OK。3.28+ 支持 JDK 17 |
| byte-buddy / byte-buddy-agent | 1.12.23 | `pom.xml:96` | ✅ OK。1.12.x 支持 JDK 17 |
| cglib | ~~3.2.5~~ -> 3.3.0 | `polaris-extension/pom.xml:73` | ✅ 已升级（2026-07-22，见 §3.1；用外部 asm，随项目 9.4；scope 改 test） |
| reflectasm | ~~1.11.9~~ | 原 `pom.xml:99` | ✅ 已删除（死依赖，2026-07-22，见 §3.1a） |

### 3.3 其他库版本核对

| 库 | 版本 | 位置 | JDK 17 兼容性 |
|---|---|---|---|
| lombok | 1.18.32 | `pom.xml:77` | ✅ OK |
| lombok-maven-plugin | 1.18.20.0 | `pom.xml:740` | ✅ OK（delombok 用项目 lombok 1.18.32 运行） |
| mybatis | 3.5.7 | `pom.xml:113` | ✅ OK |
| mybatis-spring | 2.0.6 / 2.1.4 | `polaris-mybatis/pom.xml:39` 等 | ✅ OK |
| slf4j | 1.7.36 | `pom.xml:85` | ✅ 可运行（已 EOL，建议未来迁 2.x） |
| log4j2 | 2.25.3 | `pom.xml:84` | ✅ OK |
| disruptor | 3.4.4 | `pom.xml:87` | ✅ OK |
| JUnit 5 (Jupiter) | 5.9.1 | `pom.xml:88` | ✅ OK |
| JUnit 4 | 4.13.2 | `pom.xml:89` | ✅ OK |
| mockito | 4.11.0 | `pom.xml:93` | ✅ OK（4.x 用 byte-buddy 1.12.x） |
| h2 | 2.2.220 | `pom.xml:90` | ✅ OK |
| hsqldb | 2.7.1 / 2.5.0 | `pom.xml:91` 等 | ✅ OK（2.5.0 较旧） |

### 3.4 测试/Demo 侧库与 JDK 17 不兼容 ⚠️ 测试作用域

| 库 | 版本 | 位置 | 问题 |
|---|---|---|---|
| cobertura-maven-plugin | 2.7 | `pom.xml:1108` | 内置旧版 ASM（4.x/5.x），不支持 class file 61，`-P sut` 激活后 JDK 17 覆盖率检测失败。建议换 JaCoCo |
| powermock | 2.0.7 | `pom.xml:92` | JDK 16+ 强封装下反射受限 |
| derby | 10.14.2.0 | `polaris-core/pom.xml:151` | 10.15+ 才支持 JDK 11+ |
| spring-boot | 2.3.10 / 2.4.2 | `pom.xml:81`、`polaris-test/polaris-boot-demo/pom.xml:17` | 仅 `withDemo` profile 生效，JDK 17 需 2.5+ |
| findbugs-maven-plugin | 3.0.5 | `pom.xml:1182` | 不支持 JDK 17，但 `findbugs.skip=true` 默认跳过 |

> 以上仅影响测试 / demo，不影响库本体在 JDK 17 下的运行。

---

## 四、构建/编译层（当前 JDK 8 toolchain 编译，对 JDK 17 运行无直接影响）

当前根 pom 用 `<source>8</source><target>8</target>`（`pom.xml:62-63`，`<release>8` 被注释）+ `maven-toolchains-plugin` 锁 JDK 8（`pom.xml:1284-1302`），即**仍在 JDK 8 下编译**。这部分对"JDK 17 运行"无直接影响。相关隐患已在 `.plan/java17-jdk-support-plan.md` 规划：

- **改动 1（APT `SourceVersion`）**：✅ **已落地**。`BaseProcessor.java:65-68` override `getSupportedSourceVersion()` 返回 `SourceVersion.latestSupported()`；全仓已无 `@SupportedSourceVersion` 注解残留。
- **改动 2（清理 `cn.pivoto.jdk:tools` 死依赖）**：`polaris-core-apt/pom.xml` 已无该依赖（仅 `jsr305`）；根 pom `jdk8` profile 中的 `tools:1.8` 仅在 `<jdk>(1.8,8]</jdk>` 激活，JDK 17 不引入。
- **改动 3（`<source>/<target>` -> `<release>8`）**：待实施。当前仍是 source/target 8（JDK 8 编译下唯一正确选择；若改 JDK 17 编译应切 `<release>8`）。

### 其他构建层事实

- `maven-enforcer` `requireJavaVersion`：version=`${maven.compiler.source}`="8"，`<fail>false</fail>`（`pom.xml:1053-1073`），不阻断 JDK 17 构建。
- **全仓无 `module-info.java`**（非模块化工程）；`polaris-core` shade 配置有意排除 `module-info.class`。
- **全仓无 `--add-exports` / `--add-opens` 配置**：`.mvn/maven.config`（`-U --fail-at-end -e`）、`.mvn/jvm.config`（内存 + headless）、surefire `argLine` 均无。
- `javax.*` vs `jakarta.*` 坐标：
  - `polaris-web` 的 `javax.servlet/jsp/jstl/el` 坐标均为 `provided`，JDK 17 下 `javax` 包名仍可用；若目标容器是 Jakarta EE 9+（`jakarta.*` 包名）需迁移。
  - `jakarta.*` 坐标（`jakarta.xml.bind-api`、`jakarta.validation-api`、`jakarta.el`、`jakarta.inject`）均用 2.x 版本（API 包名仍为 `javax.*`），JDK 17 兼容。

---

## 五、建议优先级

1. **§1.2 Nashorn 移除**：JDK 17 下 JS 求值功能不可用的真问题，需文档化或补 optional 的独立 JS 引擎依赖（Nashorn standalone / GraalVM JS）。
2. **§2.1-2.5** 已做适配，维持现状即可；如需恢复完整能力（去除 final、Unsafe 类定义等），在启动参数加对应 `--add-opens`。
3. **§3.4** 按需在 JDK 17 跑测试时处理（`cobertura` -> JaCoCo、`derby` 升级等）。
4. **§4 编译层**：按 `.plan/java17-jdk-support-plan.md` 继续推进改动 3（`<release>8`）。

> §1.1 JAXB（补 `jaxb-runtime:2.3.3`）、§3.1a reflectasm（删除死依赖）、§3.1 cglib（升级 3.3.0 + 改 test scope）均已于 2026-07-22 处理，从待办中移除。当前仅余 §1.2 Nashorn 一项"高"优先级待处理。

---

## 六、风险速查表

| # | 位置 | 写法 | JDK 17 影响 | 严重度 | 状态 |
|---|---|---|---|---|---|
| 1 | `Jaxb.java:27,50` + `polaris-extension/pom.xml:195` | JAXB 仅 API 无实现 | 功能不可用 | 高 | ✅ 已修复（补 `jaxb-runtime:2.3.3`） |
| 2 | `JavaScriptEvaluator.java:13`、`AbstractStandardEvaluator.java:36` | 依赖已移除的 Nashorn | JS 求值不可用 | 高 | 待处理 |
| 3 | `cglib 3.2.5` | asm 6.x API 在 asm 9.4 上可能 NoSuchMethodError | 测试运行失败 | 中 | ✅ 已修复（升级 `3.3.0` + scope=test；asm 实际由 dependencyManagement 锁 9.4） |
| 3a | ~~`reflectasm 1.11.9`~~ | （死依赖，无代码引用） | 无（未使用） | 中 | ✅ 已删除（2026-07-22） |
| 4 | `AsmReflects.java:269` 等 | 动态类定义反射回退 | 边界降级 | 低 | 已适配 |
| 5 | `Reflects.java:1223` | 反射 `Field.modifiers` | 去除 final 失效 | 低 | 已降级 |
| 6 | `Xml.java:85` | 默认 DocumentBuilder 指向内部类 | 首次失败后回退 | 低 | 已回退 |
| 7 | `MemoryClassLoader.java:195` | instanceof URLClassLoader | classpath 收集行为差异 | 低 | 有兜底 |
| 8 | `cobertura 2.7` 等 | 测试侧旧库 | 仅影响测试 | 低 | 按需处理 |
