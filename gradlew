#!/bin/sh
APP_BASE_NAME=`basename "$0"`
APP_HOME=`pwd`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -Xmx64m -Dorg.gradle.appname=$APP_BASE_NAME -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
