A simple stock viewer. The purpose of this application was to learn javafx.

Requirements
Java 11
mvn 3.3.9

Build
set environment variable STOCK_TRACKER_HOME
        set this to where you want logs and the database to be kept
./bin/build.sh

Run
./bin/start.sh

For this application to run properly, you will need to set the configuration properties for the database and get a free api key from Alpha Vantage. The api key must be placed in APIRequest.java then recompile project. Currently, only the intraday statistics are working.
