#########################################################################
# File Name: changeLatency.sh
# Author: Shungeng Zhang
# mail: szhan45@lsu.edu
# Created Time: 18:59:51 22/11/16
#########################################################################
#!/bin/bash

# $1 -- network latency
if [[ $1 == "" ]];
then
	exit
elif [[ $1 == 0 ]];
then
	sudo tc qdisc del dev br100 root netem delay 20ms 1ms distribution normal
else
	sudo tc qdisc del dev br100 root netem delay ${1}ms 1ms distribution normal
	sudo tc qdisc add dev br100 root netem delay ${1}ms 1ms distribution normal
fi
