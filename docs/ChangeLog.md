## 版本变更说明

### 3.2.13
- 添加ExtendedBeanMap类
- 优化Sql构造方法，支持非目标实体类型传参的BeanMap

### 3.2.12
- 添加Guids中工具方法

### 3.2.11
- 添加LazyRef类
- 删除MapperEntity注解不用的属性

### 3.2.10
- 添加滑动窗口通用实现类SlidingWindow

### 3.2.9
- 添加批量数据处理通用实现类BatchDataCollector

### 3.2.8
- Sql构建器对count子查询添加别名，防止如mysql库报错：`Every derived table must have its own alias`
- 代码生成器支持配置列扩展属性以应用于模板

### 3.2.7
- 设置jackson默认时区

### 3.2.6
- 添加GuidNodeStrategyProvider机制

### 3.2.5
- 优化Mybatis扩展插件相关功能，添加DynamicUseGeneratedKeys以支持插入语句实现中使用GeneratedKeys

### 3.2.4
- 优化生成器类型处理
- 更新构建脚本

### 3.2.3
- 优化生成器类型处理
- 优化版本号解析方法

### 3.2.2
- 修复StringCases工具类bug

### 3.2.1
- 更新构建打包配置，修复lombok使用问题

### 3.2.0
- 拆分插件工程
- 更新groupId命名

### 3.1.43
- 添加 EntityMergeMapper

### 3.1.42
- 优化一些函数式接口命名
- 优化代码生成器模板及相关方法

### 3.1.41
- 优化Jdbc异常处理，添加Queries工具方法

### 3.1.40
- 优化mybatis扩展SQL功能，修复部分key取值问题

### 3.1.39
- 升级日志版本
- 添加日期时间工具方法
- 调整nacos、zookeeper工具结构

### 3.1.38
- 优化ASM字节码操作工具库
- 添加生成式动态代理实现工具库
- 添加aop工具库
- 优化异常类、常量类等命名

### 3.1.37
- 修复Maven插件对native2ascii的处理问题

### 3.1.36
- 添加插件模块properties-maven-plugin
- 调整插件模块命名

### 3.1.35
- 修复java9及上以环境中模块化权限错误
- 修改类生成所用的异常类以防类加载器找不到类
- MetaObject排除特定类的Bean解析
- 调整部分工程依赖

### 3.1.34
- 优化DBV工具类
- 调整工具类函数接口命名
- 优化注释处理器工具类

### 3.1.33
- 优化Dates工具类，修改包名，添加工具方法
- 优化Strings工具类，添加工具方法

### 3.1.32
- 优化Jdbc工具类，添加工具方法
- 调整Jdbc注解命名
- 修复其他缺陷

### 3.1.31
- Jdbc工具类添加绑定参数类型处理逻辑

### 3.1.30
- 优化Guid、Dates
- 应用环境/属性读写工具
- 并发与线程池处理工具
- ZK工具类

### 3.1.29
- 添加Fastjson工具类

### 3.1.28
- 优化类型转换异常处理
- 添加BeanMap异常处理
- 添加OS、Jacksons工具方法
- 优化Beans方法
- 集群模式工具

### 3.1.27
- 优化Copier方法签名与类结构

### 3.1.26
- 优化Map工具类
- 优化Bean属性复制与反射访问工具
- 添加函数接口
- 压测优化BeanMap、MetaObject、Asm相关工具类

### 3.1.25
- 增强Copier工具，增加key匹配模式参数

### 3.1.24
- 修复LocalNode获取IP节点错误

### 3.1.23
- 移除DecoderException类
- 优化部分工具类的命名与注释文字
- 修改SQL引用表达式解析方法
- 添加Mybatis的LanguageDriver实现TableRefResolvableDriver
- 优化Mybatis的LanguageDriver实现ProviderSqlSourceDriver
- 其他代码优化与测试

### 3.1.22
- 添加工具函数接口`Callable*`、`Executable*`
- 调整日志工具，增加方法，优化输出
- 优化Jdbc工具方法与注解处理
- 优化Mybatis扩展方法，修改Mapper实体声明注解
- 添加对SQL绑定取值时的异常处理
- 添加列别名前缀后缀注解处理，区分查询与更新语句的主键条件，查询时去除乐观锁字段条件
- 其他代码优化与测试

### 3.1.21
- 优化脚本引擎处理，调整类与方法签名
- 优化注解属性工具类`AnnotationAttributes`，添加工具方法
- 优化元属性工具类`MetaObject`，尽可能支持运行期属性读写
- 修改测试日志配置
- 修改`SqlNode`换行符，默认为空格以保持SQL日志在同行
- 优化`TextNode`，添加空值判断
- 增加实体表与字段的引用表达式的解析
- 增强Jdbc工具方法，添加接口执行器代理工具，添加多种sql构建与配置注解
- 原始SQL尽量支持引用表达式解析，顶层对象添加TableAccessible实现
- 优化注解处理器，添加`InsertStatement`相关方法
- Mybatis扩展增加自定义`LanguageDriver`实现以支持非`Map`类型参数
- 其他代码优化与测试

### 3.1.20
- 添加`polaris-json`子模块，提供工具类`Jsons`
- 优化字符串工具类`Strings`，添加工具方法，调整部分方法命名与参数类型
- 优化断言工具类`Assertions`、`Arguments`，添加工具方法
- 优化SQL工具类，添加对`&{tableAlias.tableField}`引用范式的处理，添加绑定变量取值的缓存支持
- 其他代码优化与测试

### 3.1.19
- 优化Sql构建条件处理
- 调整部分代码注释内容
- 其他代码优化与测试

### 3.1.18
- 添加参数断言工具类`Arguments`，区别于`Assertions`，统一抛出`IllegalArgumentException`异常
- 调整字符串工具类`Strings`部分方法命名
- 其他代码优化与测试

### 3.1.17
- 优化日志工具类方法中对无参日志内容的处理
- 其他代码优化与测试

### 3.1.16
- 添加Sql相关注解与其处理工具方法
- 优化Mybatis扩展处理，添加对SQL相关注解的支持
- 增强Jdbc注解处理器，添加字段的`groupBy`、`orderBy`、`having`工具方法
- 优化日志工具类，调整命名，添加缓存
- 其他代码优化与测试

### 3.1.15
- 修改密文处理工具类，封装统一异常类`CryptoRuntimeException`
- 添加日期格式化类`DateFormats`
- 优化代码生成器模板
- 其他代码优化与测试

### 3.1.14
- 修复JDBC工具类中对`in`子查询的处理问题
- 添加JDBC工具类对查询列别名的前缀与后缀支持
- 其他代码优化与测试

### 3.1.13
- 修复`SelectSegment`函数列别名处理问题
- 优化工具类`ServiceLoader`对`ServiceProperty`属性注解的读取
- 其他代码优化与测试

### 3.1.12
- 优化代码生成器模板
- 优化字符串处理工具类`Strings`，添加工具方法
- 其他代码优化与测试

### 3.1.11
- 优化日志类`StdoutLogger`，调整扩展方法
- 其他代码优化与测试

### 3.1.10
- 优化代码生成器模板
- 优化日志工具类，添加工具类`ILoggers`，优化日志组件判断方式与内容输出方法
- 其他代码优化与测试

### 3.1.9
- 优化代码生成器模板
- 优化反射处理工具类`Reflects`
- 优化断言工具类`Assertions`
- 调整`ConverterRegistry`，添加公开构造器
- 优化注解处理工具`Annotations`，提供原生与合并式的两类注解处理方法
- 其他代码优化与测试

### 3.1.8
- 优化类型转换工具类，修改实现，添加`Converters`工具
- 优化Jdbc相关工具类，添加集合操作支持
- 修改JUnit测试注解
- 添加Bean处理工具类`MetaObject`
- 其他代码优化与测试

### 3.1.7
- 优化代码生成器模板
- 添加工具类`StopWatch`
- 其他代码优化与测试

### 3.1.6
- 优化代码生成器模板
- 其他代码优化与测试

### 3.1.5
- 优化代码生成器模板与工具类
- 优化`BeanMap`工具类
- 其他代码优化与测试

### 3.1.4
- 优化代码生成器模板
- 其他代码优化与测试

### 3.1.3
- 优化代码生成器模板
- 优化Jdbc相关工具类
- 其他代码优化与测试

### 3.1.2
- 优化代码生成器模板
- 修复`MergeStatement`SQL语法错误
- 其他代码优化与测试

### 3.1.1
- 代码生成器优化
- 开发注解处理工具类
- 其他代码优化与测试

### 3.1.0
- 基于优化后的工程结构发布基础版本

### 3.0.1
- 优化工程结构，分离bom工程，开发工具库独立版本

### 1.x、2.x
- 已弃用，工具库历史过渡版本