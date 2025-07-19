javac util/src/*.java -d util/build
javac -classpath util/build inference/src/*.java -d inference/build
javac -classpath util/build statistics/src/*.java -d statistics/build

IF "%~1" == "--statistics" (
    java -cp "statistics/build;util/build" statistics.src.Main %2 %3
) ELSE IF "%~1" == "--inference" (
    java -cp "inference/build;util/build" inference.src.Main %2 %3
)