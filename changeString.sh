#########################################################################
# File Name: changeString.sh
# Author: Shungeng Zhang
# mail: szhan45@lsu.edu
# Created Time: 17:01:42 27/08/16
#########################################################################
#!/bin/bash
CONFIG_LOC="/home/szhang/simple_server/src/com/sgzhang/util/Count.java"
CONFIG_LOC2="/home/szhang/simple_server/src/io/netty/example/utils/Count.java"
LENGTH=$1
sed -i "4s/[0-9].*\(;\)/$LENGTH\1/g" $CONFIG_LOC
sed -i "4s/[0-9].*\(;\)/$LENGTH\1/g" $CONFIG_LOC2
