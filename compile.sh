#!/bin/bash
cd src
rm server/*.class utilities/*.class testapp/*.class
javac server/*.java utilities/*.java testapp/*.java
