#########################################################################
# File Name: SERVER_START.sh
# Author: Shungeng Zhang
# mail: szhan45@lsu.edu
# Created Time: 21:35:12 05/08/16
#########################################################################
#!/bin/bash
BIN_PATH=$(pwd)"/bin"
JAR_PATH=$(pwd)"/lib/*"
#JAVA_OPTS="-agentpath:/home/szhang/jprofiler9/bin/linux-x64/libjprofilerti.so=port=8849 $JAVA_OPTS"
export JAVA_OPTS

# $1 type
# $2 port
# $3 worker threads
# $4 debug (for script)
# $5 sndbuf size

# one request test
./compile.sh && java -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH com.simple.server.Main
#./compile.sh && java -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test$1 $2 $3 $5
