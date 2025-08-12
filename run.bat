@echo off

javac util/src/*.java -d util/build

set root=%cd%
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
    mkdir dp\out
    java -cp dp/build;util/build;dp/lib/* dp.src.Main %2
) ELSE (
    echo "Unknown command. Usage ./run.bat <module> <args>"
)
