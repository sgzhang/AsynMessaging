# Name:           Shungeng Zhang
# Project:        PA-1b (Non-blocking JDBC With the Asynchronous MySQL Connector)
# Instructor:     Dr. Feng Chen
# Class:          cs7103-au15
# LoginID:        cs710319
#!/bin/bash
PROJECT_PATH=$(pwd)
JAR_PATH=$PROJECT_PATH/lib
BIN_PATH=$PROJECT_PATH/bin
SRC_PATH=$PROJECT_PATH/src
#javac -classpath :$JAR_PATH/* $SRC_PATH/*.java $SRC_PATH/com/sgzhang/nio/*.java $SRC_PATH/com/sgzhang/bio/*.java $SRC_PATH/com/sgzhang/util/*.java -d $BIN_PATH
javac -classpath :$JAR_PATH/* $SRC_PATH/*.java $SRC_PATH/com/sgzhang/nio/*.java $SRC_PATH/com/sgzhang/bio/*.java $SRC_PATH/com/sgzhang/util/*.java $SRC_PATH/com/simple/server/*.java $SRC_PATH/com/sgzhang/test/*.java $SRC_PATH/com/sgzhang/test/task/*.java $SRC_PATH/io/netty/example/echo/*.java $SRC_PATH/io/netty/example/utils/*.java -d $BIN_PATH
