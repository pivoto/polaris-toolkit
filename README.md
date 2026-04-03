# polaris-toolkit

一个纯净的Java工具类库，提供一套标准SDK库与三方依赖的扩展库，力求功能丰富，不断优化和借鉴吸收各类优秀开源实现。

- [版本历史](docs/ChangeLog.md)

## 目录结构

- .mvn 存放Maven配置文件
- .bin 脚本目录
- docs 文档目录
- pom.xml 根 pom 文件
- README.md 根描述文件

## 模块结构

- polaris-core 常用核心工具库，只存在Slf4j日志、JSR标准依赖，无更多的三方依赖，以保待纯净
- polaris-extra 扩展工具库，针对特定用途或功能的扩展工具，有具体的三方库依赖
  - polaris-validation 验证器工具库，封装和增强 jsr303 规范的验证器，提供更丰富的验证规则。
  - polaris-json JSON工具库，引入`jackson`等依赖库，封装和增强JSON操作工具
  - polaris-crypto 加解密工具库，引入`bouncycastle`等依赖库，封装和增强加解密操作工具
  - polaris-mybatis  MyBatis工具库，封装和增强`mybatis`自动Mapper、拦截器等功能。
  - polaris-extension 其他常用三方工具框架的封装与扩展库，简化使用，包括：apache-commons、spring-core、guava等集成封装，大部分依赖以可选形式引入，由应用方管理具体依赖关系。
- polaris-builder 代码生成器工具

## 代码规范

编码时须遵守基本的技术研发规范，框架内部额外约定：
- 各模块间尽量减少强依赖关系
- 模块内的包名要体现模块名，示例：`{域名}.{框架名}.{模块名}.**`
- 常量包为`*.consts`，常量类名以`*s`等结尾，声明为接口
- 异常包名为`*.err`，异常类名以`Exception`结尾
- 接口命名时，除了兼容历史版本或已开放给业务应用继承使用，不添加`I`前缀，尤其是框架内部使用
- 枚举类命名时，不添加`Enum`后缀
- 枚举成员名与全局常量命名时, 使用大写字母与下划线分隔形式
- 注解处理器依赖的类或方法，使用`@AnnotationProcessing`标注
- 稳定发布而不轻易变更的类或方法，使用`@Published`标注
- 框架内部使用的类或方法，使用`@Internal`标注
- 实现向后兼容性的类或方法，使用`@Compatible`标注
- 因考虑向后兼容而保留的的类或方法，不再进一步维护，会在适当时机会进行删除时，使用`@Legacy`标注
- 实验性质的类或方法，使用`@Experimental`标注
- 尚未完成但因构建依赖或部分投产等原因而提交的类或方法，使用`@Uncompleted`标注
- 需要增加额外警示的类或方法，使用`@Warning`标注

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

##  Profile配置
- dev: 开发环境
- sut: 单元测试环境
- sit: 集成测试环境
- uat: 用户验收测试环境
- pre: 预生产环境
- pro: 生产环境


## GIT 操作步骤规范

### 功能分支更新与推送

假定功能分支名为`fea_xxx`，操作步骤如下：

1. 切换到`fea_xxx`分支
2. 确定`fea_xxx`分支是基于最近一次的`master`分支演进的，如不是，则执行`git rebase master`操作以变基到最新的`master`分支
3. 执行`git fetch origin fea_xxx`、`git rebase origin/fea_xxx`操作以更新到远程最新（或`git pull --rebase origin fea_xxx`）
4. 执行`git push origin fea_xxx`操作推送到远程。如果出现冲突被拒绝，则回到第3步重新处理。

> 注：注意每次推送前先执行更新操作，以`rebase`方式解决所有冲突，再推送远程。尤其在多人协作开发时必须确保操作正确，不要无脑使用IDE默认状态的更新方法（即`git pull --no-rebase`)

### 功能分支合并到主线分支(`dev|sit|uat|pre|master`)

假定功能分支名为`fea_xxx`，主线分支名为`dev`，操作步骤如下：

1. 切换到`fea_xxx`分支
2. 确定`fea_xxx`分支是基于最近一次的`master`分支演进的，如不是，则执行`git rebase master`操作以变基到最新的`master`分支
3. 基于`fea_xxx`分支创建`dev_fea_xxx`临时分支，作为合并到主线分支前的预处理临时分支
4. 切换到`dev`分支
5. 执行`git fetch origin dev`、`git reset --keep origin/dev`操作以更新到远程最新。
6. 确认此主线分支是否基于最近一次的`master`分支演进，如不是，则执行`git merge --no-ff master`操作以更新状态
7. 切换到`dev_fea_xxx`分支
8. 执行`git rebase dev`将`dev_fea_xxx`变基到`dev`分支
9. 切换到`dev`分支
10. 执行`git merge --no-ff dev_fea_xxx`操作合并功能分支
11. 执行`git push origin dev`操作推送到远程。如果出现冲突被拒绝，则回到第4步重新处理。
12. 删除分支`dev_fea_xxx`

> 注：如果目标主线分支是`sit|uat|pre|master`等，则预处理分支名修改为`{sit|uat|pre|master}_fea_xxx`，其他操作步骤雷同。

### 生产主线分支(`master`)完成部署上线后的操作

生产分支完成部署上线后，需要即时将各环境主线分支更新到最新状态，操作步骤如下：

1. 切换到`master`分支
2. 执行`git fetch origin master`、`git reset --keep origin/master`操作以更新到远程最新
3. 切换到`dev`分支
4. 执行`git fetch origin dev`、`git reset --keep origin/dev`操作以更新到远程最新
5. 执行`git merge --no-ff master`操作合并`master`分支
6. 执行`git push origin dev`操作推送到远程。如果出现冲突被拒绝，则回到第3步重新处理。
7. 在`sit|uat|pre`等主线分支上执行第3~6步，与`dev`分支操作雷同。

> 注：如果未即时更新某环境分支，请在下一次部署此环境分支前，对其进行上述更新操作。


## GIT 提交信息规范

>- 在`push`到远程仓库前，请先通过`fetch+rebase`操作保证同分支的线性迭代，不要产生无意义的`merge`信息
>- 在功能功能`merge`到主分支前，请先在功能分支通过`reabse`或`merge`（建议rebase）操作获取到主分支最新代码，以保证合并操作无任何冲突

提交信息格式最好遵循以下规范：

- 提交信息格式：

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
   【功能优化】opt/optimize: 强调对已有功能特性的优化改进（设计、实现、规范等）
   【缺陷修复】fix/bugfix: 问题修改（修复缺陷）
   【代码重构】refactor: 对代码整体或局部进行重构优化
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


body: 信息内容（可选）
-- 变更原因
-- 解决方案
-- 影响范围


footer: 脚注（可选）
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
  #     【功能优化】opt/optimize: 强调对已有功能特性的优化改进（设计、实现、规范等）
  #     【缺陷修复】fix/bugfix: 问题修改（修复缺陷）
  #     【代码重构】refactor: 对代码整体或局部进行重构优化
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
  # body: 信息内容（可选）
  # -- 变更原因
  # -- 解决方案
  # -- 影响范围
  #
  #
  # footer: 脚注（可选）
  # -- 关联信息
  # -- 其他
  ```

  - 在全局git配置中设置模板文件路径，如`git config commit.template ~/.gitmessage`
  - 以后在提交时，直接输入`git commit`，会弹出提交信息输入框，输入信息后，按回车提交。
  - 在使用IDE时，通常此模板配置同样有效，也可在IDE中单独设置提交信息模板（某些IDE需要插件支持）。

