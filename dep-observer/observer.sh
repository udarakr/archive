#!/bin/bash

tail -fn0 /home/udara/wso2/git/wso2/fresh/product-es/modules/distribution/target/wso2es-2.0.0-SNAPSHOT/repository/logs/wso2carbon.log | while read line ; do
        echo "$line" | grep "Deployed webapp"
        if [ $? = 0 ]
            then
             echo 1 > /dev/ttyUSB0 # New webapp deplyed
        fi
        echo "$line" | grep "Undeployed webapp"
        if [ $? = 0 ]
            then
              echo 0 > /dev/ttyUSB0 # webapp undeplyed
        fi
done
