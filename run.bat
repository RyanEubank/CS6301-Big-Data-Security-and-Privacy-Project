@echo off

javac util/src/*.java -d util/build
javac -classpath util/build inference/src/*.java -d inference/build
javac -classpath util/build statistics/src/*.java -d statistics/build

set module="%~1"

IF %module% == "--statistics" (
    java -cp "statistics/build;util/build" statistics.src.Main %2 %3
) ELSE IF %module% == "--inference" (
    java -cp "inference/build;util/build" inference.src.Main %2 %3 %4 %5
) ELSE (
    echo "Unknown command."
)