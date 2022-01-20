checkstyle 执行过程可以参考其 ant 任务执行方法：`com.puppycrawl.tools.checkstyle.ant.CheckstyleAntTask.execute`

几个关键方法：
- `com.puppycrawl.tools.checkstyle.PackageNamesLoader.getPackageNames`
    获取`classpath*:checkstyle_packages.xml`配置的包名，用于查找模块类定义
- `com.puppycrawl.tools.checkstyle.PackageObjectFactory.createModule`
