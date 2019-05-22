#! /bin/bash

export homeDir=$STOCK_TICKER_HOME

if [ -z "$homeDir" ]
then
	echo "Home directory is not set"
	exit 1
fi

cd ${homeDir}
rm log.log
mvn clean install 
