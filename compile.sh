#!/bin/bash
cd src
rm channel/*.class initiators/*.class protocol/*.class server/*.class state/*.class testapp/*.class utilities/*.class
javac channel/*.java initiators/*.java protocol/*.java server/*.java state/*.java testapp/*.java utilities/*.java