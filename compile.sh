#!/bin/bash
cd src
rm server/*.class utilities/*.class testapp/*.class protocol/*.class
javac server/*.java utilities/*.java testapp/*.java protocol/*.java
