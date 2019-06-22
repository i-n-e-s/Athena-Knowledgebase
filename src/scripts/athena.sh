#!/bin/bash

#
# GENERAL INFORMATION
# This script is used for more convenient nohup startup of the athena jar file.
# It retains the process id in a athena_pid.txt file to easily stop it later.
#
# Use:
# athena.sh [start|stop] [jar file] [arguments to pass onto jar]
# e.g.
# $ ./athena.sh start athena-knowledgebase-0.3-afe2c8c.jar -scrape-paper-author
# $ ./athena.sh stop
#

# Start function taking the second and following arguments. Taking the first as 
# jar file and passing the rest to the jar.
# Reports failure if jar is not found.
function start() {
	if [[ -f $1 && ${1: -4} == ".jar" ]]; then
		nohup java -jar "${@:1}" > athena_srv.log 2>&1 &
		echo $! > athena_pid.txt
	else
		echo "File $1 not found or it is no jar file."
	fi
}

# Stop function to take down the process with the id specified in the athena_pid.txt.
function stop() {
	kill -9 `cat athena_pid.txt`
	rm athena_pid.txt
}

# "main" Skript, checking for start/stop and calling appropriate functions.
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
