#!/bin/bash

cd src

javac server/*.java utilities/*.java testapp/*.java protocol/*.java channel/*.java state/*.java
gnome-terminal --tab -t 'RMI' -- rmiregistry
sleep 1
gnome-terminal --tab -t 'Server 1' -- java server.Server 0 1 1
gnome-terminal --tab -t 'Server 2' -- java server.Server 0 2 2

gnome-terminal --tab -e "bash -c \"java testapp.TestApp 1 DELETE file.txt; exec bash\""
#java testapp.TestApp 1 BACKUP file.txt 1
cd ..
