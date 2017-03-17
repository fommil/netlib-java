#!/bin/sh

(cd generator && mvn clean && mvn install "$@") &&
mvn clean &&
mvn install "$@"
