#########################################################################
# File Name: SHUTDONW.sh
# Author: Shungeng Zhang
# mail: szhan45@lsu.edu
# Created Time: 13:55:32 26/10/16
#########################################################################
#!/bin/bash
sudo pkill -2 java
pkill -15 collectl

sudo pkill -9 java
