#!/bin/sh
mkdir -p target/classes
echo "compiling"
javac -d target/classes src/main/java/jnanorest/*.java src/main/java/jnanorest/demo/*.java
echo "packaging target/jnanorest.jar"
(cd target/classes && jar -cfe ../jnanorest.jar jnanorest.demo.Demo *)
echo "done."
echo "Run: java -jar target/jnanorest.jar"
echo " or: java -cp target/jnanorest.jar jnanorest.demo.DynDemo"
