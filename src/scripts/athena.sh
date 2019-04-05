#!/bin/bash

function start() {
	nohup java -jar "${@:1}" > athena_srv.log 2>&1 &
	echo $! > athena_pid.txt
}

function stop() {
	kill -9 `cat athena_pid.txt`
	rm athena_pid.txt
}

# "main"
if [ "$1" = "start" ]; then
	if [ -f athena_pid.txt ]; then
		echo 'There is already a athena_pid.txt file. An athena process may be already running. Please check an stop it if desired.'
	else
		start "${@:2}"
	fi
elif [ "$1" = "stop" ]; then
	stop
else
	echo 'Unsupported operation.'
fi
