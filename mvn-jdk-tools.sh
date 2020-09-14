# mvn install:help  -Dgoal=install-file -Ddetail=true
# mvn deploy:help  -Dgoal=deploy-file -Ddetail=true
# mvn deploy:deploy-file -Dfile=/d/devel/java/jdk/lib/tools.jar  -DartifactId=tools -DgroupId=cn.pivoto.jdk -Dpackaging=jar -Dversion=1.8  -DgeneratePom=true -DrepositoryId=oss -Durl=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
#mvn install:install-file -Dfile=/d/devel/java/jdk/lib/tools.jar  -DartifactId=tools -DgroupId=cn.pivoto.jdk -Dpackaging=jar -Dversion=1.8  -DgeneratePom=true

# 手动上传时需要手动生成签名文件、源码包、文档包，否则nexus会验证失败，以下几个文件缺一不可，待处理
# *.jar
# *.pom
# *-javadoc.jar
# *-sources.jar
# *.jar.asc
# *.pom.asc
# *-javadoc.jar.asc
# *-sources.jar.asc

mkdir -p .local/
cd .local/
cp /d/devel/java/jdk/lib/tools.jar ./tools-1.8.jar

echo '<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<modelVersion>4.0.0</modelVersion>
	<groupId>cn.pivoto.jdk</groupId>
	<artifactId>tools</artifactId>
	<packaging>jar</packaging>
	<version>1.8</version>
	<name>tools</name>
	<url>https://www.pivoto.cn</url>
	<description>pivoto</description>
	<organization>
		<name>pivoto</name>
		<url>https://www.pivoto.cn</url>
	</organization>

	<developers>
		<developer>
			<name>Qt</name>
			<email>kylinmania@163.com</email>
			<url>https://www.pivoto.cn/</url>
			<roles>
				<role>software engineer</role>
			</roles>
			<timezone>8</timezone>
		</developer>
	</developers>

	<licenses>
		<license>
			<name>Apache License, Version 2.0</name>
			<url>http://www.apache.org/licenses/LICENSE-2.0</url>
		</license>
	</licenses>

	<ciManagement>
		<system>continuum</system>
		<url>http://www.pivoto.cn</url>
		<notifiers>
			<notifier>
				<type>mail</type>
				<sendOnError>true</sendOnError>
				<sendOnFailure>true</sendOnFailure>
				<sendOnSuccess>true</sendOnSuccess>
				<sendOnWarning>false</sendOnWarning>
				<configuration>
					<address>kylinmania@163.com</address>
				</configuration>
			</notifier>
		</notifiers>
	</ciManagement>

	<issueManagement>
		<system>issue</system>
		<url>http://www.pivoto.cn</url>
	</issueManagement>
	<scm>
		<connection>scm:git:git@github.com:pivoto/polaris.git</connection>
		<developerConnection>scm:git:git@github.com:pivoto/polaris.git</developerConnection>
		<url>git@github.com:pivoto/polaris.git</url>
	</scm>
</project>
' > tools-1.8.pom
jar cvf tools-1.8-sources.jar tools-1.8.pom
jar cvf tools-1.8-javadoc.jar tools-1.8.pom
gpg --armor --detach-sign tools-1.8.jar
gpg --armor --detach-sign tools-1.8.pom
gpg --armor --detach-sign tools-1.8-sources.jar
gpg --armor --detach-sign tools-1.8-javadoc.jar

mvn deploy:deploy-file -Dfile=tools-1.8.jar  -DpomFile=tools-1.8.pom -Dclassifiers=sources,javadoc,,,sources,javadoc   -Dtypes=jar,jar,pom.asc,jar.asc,jar.asc,jar.asc  -Dfiles=tools-1.8-sources.jar,tools-1.8-javadoc.jar,tools-1.8.pom.asc,tools-1.8.jar.asc,tools-1.8-sources.jar.asc,tools-1.8-javadoc.jar.asc -DrepositoryId=oss -Durl=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/

#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-list
#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-close  -DstagingRepositoryId=cnpivoto-1019
#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-release  -DstagingRepositoryId=cnpivoto-1019
