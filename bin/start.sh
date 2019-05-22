#! /bin/bash 

export homeDir=$STOCK_TICKER_HOME

if [ -z "$homeDir" ]
then
	echo "Home directory is not set"
	exit 1
fi


#mvn exec:java
java -jar $homeDir/target/stockTicker-0.0.1-shaded.jar
