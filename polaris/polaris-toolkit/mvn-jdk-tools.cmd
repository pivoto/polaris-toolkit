rem mvn install:help  -Dgoal=install-file -Ddetail=true
rem mvn deploy:deploy-file -Dfile=%java_home%\lib\tools.jar  -DartifactId=tools -DgroupId=cn.pivoto.jdk -Dpackaging=jar -Dversion=1.8  -DgeneratePom=true -DrepositoryId=oss -Durl=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
mvn install:install-file -Dfile=%java_home%\lib\tools.jar  -DartifactId=tools -DgroupId=cn.pivoto.jdk -Dpackaging=jar -Dversion=1.8  -DgeneratePom=true
pause
