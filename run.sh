javac util/src/*.java -d util/build

root=$PWD
module=$1

if [[ $module == "--statistics" ]]; then
    javac -cp util/build statistics/src/*.java -d statistics/build
    java -cp statistics/build:util/build statistics.src.Main $2 $3
elif [[ $module == "--inference" ]]; then
    javac -cp util/build inference/src/*.java -d inference/build
    java -cp inference/build:util/build inference.src.Main $2 $3 $4 $5
elif [[ $module == "--dp" ]]; then
    javac -cp util/build:dp/lib/* dp/src/*.java -d dp/build
	mkdir dp/out
    java -cp dp/build:util/build:dp/lib/* dp.src.Main $2
    echo Files written to dp/out
else
    echo "Unknown command. Usage ./run.sh <module> <args>"
fi
