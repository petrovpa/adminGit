#!/bin/sh
CP="";
cd ./lib
for file in *
do
  CP="./lib/$file:$CP"
done
echo "$CP"
cd ../
"$JAVA_HOME/bin/java" -classpath $CP com.bivgroup.flextera.insurance.bivfront.db.DatabaseManager $* 
