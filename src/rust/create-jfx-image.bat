set PATH_TO_FX_MODS=c:\java\javafx\javafx-jmods-17.0.2\
set JAVA_HOME=c:\java\openjdk-16.0.1
set JAVA_MODULES=java.naming,java.sql,jdk.crypto.cryptoki,jdk.charsets,java.management,
SET JAVAFX_MODULES=javafx.controls,javafx.fxml

rmdir jrefx /q /s

%JAVA_HOME%\bin\jlink --module-path %PATH_TO_FX_MODS% ^
    --add-modules %JAVA_MODULES%%JAVAFX_MODULES% ^
    --strip-debug --compress 2 --no-header-files --no-man-pages ^
	--output jrefx