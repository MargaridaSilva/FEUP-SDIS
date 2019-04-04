#!/bin/bash
cd src
javac server/*.java utilities/*.java testapp/*.java
gnome-terminal --tab -t 'Server 1' -- java server.Server 0 1 1
gnome-terminal --tab -t 'Server 2' -- java server.Server 0 2 2
cd ..
