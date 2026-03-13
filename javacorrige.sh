#!/bin/bash
DIRNAME=$(pwd)
java -cp "$DIRNAME/target/javacorrige-1.0-SNAPSHOT-jar-with-dependencies.jar" com.javacorrige.Main "$@"