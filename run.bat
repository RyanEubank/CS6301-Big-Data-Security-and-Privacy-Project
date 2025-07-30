@echo off

javac util/src/*.java -d util/build

set root=%cd%
set module="%~1"

IF %module% == "--statistics" (
    javac -cp util/build statistics/src/*.java -d statistics/build
    java -cp statistics/build;util/build statistics.src.Main %2 %3
) ELSE IF %module% == "--inference" (
    javac -cp util/build inference/src/*.java -d inference/build
    java -cp inference/build;util/build inference.src.Main %2 %3 %4 %5
) ELSE IF %module% == "--dp" (
    javac -cp util/build;dp/lib/* dp/src/*.java -d dp/build
    java -cp dp/build;util/build;dp/lib/* dp.src.Main %2
    mkdir dp/out
) ELSE (
    echo "Unknown command."
)
