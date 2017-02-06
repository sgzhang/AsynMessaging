#########################################################################
# File Name: SERVER_START.sh
# Author: Shungeng Zhang
# mail: szhan45@lsu.edu
# Created Time: 21:35:12 05/08/16
#########################################################################
#!/bin/bash
BIN_PATH=$(pwd)"/bin"
JAR_PATH=$(pwd)"/lib"
#JAVA_OPTS="-agentpath:/home/szhang/jprofiler9/bin/linux-x64/libjprofilerti.so=port=8849 $JAVA_OPTS"
if [[ $4 == "debug" ]];
then
	JAVA_OPTS='-agentpath:/usr/lib64/oprofile/libjvmti_oprofile.so'
fi
export JAVA_OPTS

# $1 type
# $2 port
# $3 worker threads
# $4 debug (for script)
# $5 sndbuf size
# $6 data size or response ratio

# compile first
#./compile.sh && java -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test $1 $2

if [[ $1 == "nio1" || $1 == "bio" ]];
then
	if [[ $4 == "debug" ]];
	then
		./compile.sh &&sudo operf --events=CPU_CLK_UNHALTED:100000 -k /usr/lib/debug/lib/modules/2.6.32-358.123.2.openstack.el6.x86_64/vmlinux /home/szhang/jdk1.8.0_91/bin/java $JAVA_OPTS -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test $1 $2 $3 $5
	else
		./compile.sh && java $JAVA_OPTS -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test $1 $2 $3 $5
	fi
elif [[ $1 == "nio2" ]];
then
	if [[ $4 == "debug" ]];
	then
		./compile.sh && sudo operf --events=CPU_CLK_UNHALTED:100000 -k /usr/lib/debug/lib/modules/2.6.32-358.123.2.openstack.el6.x86_64/vmlinux /home/szhang/jdk1.8.0_91/bin/java $JAVA_OPTS -Xms2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH com.sgzhang.test.Server $1 $2 $3 $5
	else
		./compile.sh && java $JAVA_OPTS -Xms2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH com.sgzhang.test.Server $1 $2 $3 $5
	fi
elif [[ $1 == "netty" ]];
then
	./compile.sh && java -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH:lib/netty-all-4.1.6.Final.jar io.netty.example.echo.EchoServer $1 $2 $3 $5
elif [[ $1 == "netty1" ]];
then
	# one event per request version
	#./compile.sh && java -Xmx2048m -Xms1024m -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH:lib/*:netty-parent_one.jar io.netty.example.echo.EchoServer $1 $2 $3 $5
	
	# mixed version
	./compile.sh && java -Xmx2048m -Xms1024m -cp libs/*:netty-parent_mixed100.jar io.netty.example.echo.EchoServer $1 $2 $3 $5 $6
else
	exit 0
fi

# jvisualvm option
#java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=192.168.10.133 -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test $1 $2 $3
# java -classpath $BIN_PATH:$JAR_PATH -cp $BIN_PATH Test $1 $2
