#!/bin/sh

# [UNIX] Scripty Installer 0.1

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

touch text.txt
echo Finished!
