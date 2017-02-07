#!/bin/bash
JAR_PATH="$(cd "$(dirname "$0")/../target" && pwd)"
java -jar "$JAR_PATH/text-client-jar-with-dependencies.jar" $@