#!/bin/bash
cd src
javac client/*.java server/*.java utilities/*.java testapp/*.java
gnome-terminal --tab -t 'Server 1' -- java server.Server 1 0 "server1"
gnome-terminal --tab -t 'Server 2' -- java server.Server 2 0 "server2"
