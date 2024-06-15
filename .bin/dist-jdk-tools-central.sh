#!/bin/bash
version=1.8
#version=1.8.0_212

mkdir -p .local/
cd .local/

cp /d/devel/java/jdk/lib/tools.jar tools-${version}.jar

echo '<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<modelVersion>4.0.0</modelVersion>
	<groupId>cn.fossc.jdk</groupId>
	<artifactId>tools</artifactId>
	<packaging>jar</packaging>
	<version>'${version}'</version>
	<name>tools</name>
	<url>https://www.fossc.cn</url>
	<description>fossc</description>
	<organization>
		<name>fossc</name>
		<url>https://www.fossc.cn</url>
	</organization>

	<developers>
		<developer>
			<name>Qt</name>
			<email>kylinmania@163.com</email>
			<url>https://www.fossc.cn/</url>
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
		<url>http://www.fossc.cn</url>
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
		<url>http://www.fossc.cn</url>
	</issueManagement>
	<scm>
		<connection>scm:git:git@gitee.com:fossc/polaris.git</connection>
		<developerConnection>scm:git:git@gitee.com:fossc/polaris.git</developerConnection>
		<url>git@gitee.com:fossc/polaris.git</url>
	</scm>
</project>
' > tools-${version}.pom
jar cvf tools-${version}-sources.jar tools-${version}.pom
jar cvf tools-${version}-javadoc.jar tools-${version}.pom
gpg --armor --detach-sign tools-${version}.jar
gpg --armor --detach-sign tools-${version}.pom
gpg --armor --detach-sign tools-${version}-sources.jar
gpg --armor --detach-sign tools-${version}-javadoc.jar
md5sum tools-${version}.jar | awk '{print $1}' > tools-${version}.jar.md5
md5sum tools-${version}.pom | awk '{print $1}' > tools-${version}.pom.md5
md5sum tools-${version}-sources.jar | awk '{print $1}' > tools-${version}-sources.jar.md5
md5sum tools-${version}-javadoc.jar | awk '{print $1}' > tools-${version}-javadoc.jar.md5
sha1sum tools-${version}.jar | awk '{print $1}' > tools-${version}.jar.sha1
sha1sum tools-${version}.pom | awk '{print $1}' > tools-${version}.pom.sha1
sha1sum tools-${version}-sources.jar | awk '{print $1}' > tools-${version}-sources.jar.sha1
sha1sum tools-${version}-javadoc.jar | awk '{print $1}' > tools-${version}-javadoc.jar.sha1

mkdir -p ~/.m2/repository/cn/fossc/jdk/tools/${version}/
cp -f *.jar ~/.m2/repository/cn/fossc/jdk/tools/${version}/
cp -f *.pom ~/.m2/repository/cn/fossc/jdk/tools/${version}/
cp -f *.asc ~/.m2/repository/cn/fossc/jdk/tools/${version}/
cp -f *.md5 ~/.m2/repository/cn/fossc/jdk/tools/${version}/
cp -f *.sha1 ~/.m2/repository/cn/fossc/jdk/tools/${version}/

rm -rf cn/
mkdir -p cn/fossc/jdk/tools/${version}/
cp -f *.jar cn/fossc/jdk/tools/${version}/
cp -f *.pom cn/fossc/jdk/tools/${version}/
cp -f *.asc cn/fossc/jdk/tools/${version}/
cp -f *.md5 cn/fossc/jdk/tools/${version}/
cp -f *.sha1 cn/fossc/jdk/tools/${version}/

zip -r bundle.zip cn/

echo "input user:token"
read userToken
bearer=$(echo ${userToken}  | base64)

curl --request POST \
  --verbose \
  --header "Authorization: Bearer ${bearer}" \
  --form bundle=@bundle.zip \
	--form name=cn.fossc.jdk:tools:${version} \
  --form publishingType=AUTOMATIC  https://central.sonatype.com/api/v1/publisher/upload

#mvn deploy:deploy-file -Dfile=tools-1.8.jar  -DpomFile=tools-1.8.pom -Dclassifiers=sources,javadoc,,,sources,javadoc   -Dtypes=jar,jar,pom.asc,jar.asc,jar.asc,jar.asc  -Dfiles=tools-1.8-sources.jar,tools-1.8-javadoc.jar,tools-1.8.pom.asc,tools-1.8.jar.asc,tools-1.8-sources.jar.asc,tools-1.8-javadoc.jar.asc -DrepositoryId=oss -Durl=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/

#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-list
#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-close  -DstagingRepositoryId=cnpivoto-1019
#mvn -DserverId=oss -DnexusUrl=https://s01.oss.sonatype.org/ -DshowReleased=true -Ddetail=true org.sonatype.plugins:nexus-staging-maven-plugin:1.6.7:rc-release  -DstagingRepositoryId=cnpivoto-1019
