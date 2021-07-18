#!/bin/bash

# [UNIX] Scripty Installer 0.1

# TEST IF 'sy' IS ALREADY INSTALLED (or name sy is taken)

_PATH_SY="$(command -v sy)"
if [ "$_PATH_SY" != "" ]
then
  echo "Scripty already installed or some other command uses 'sy'!"
  exit
fi

# TEST IF JAVA IS INSTALLED AND AT LEAST VERSION 11

_PATH_JAVA="$(command -v java)"
if [ "$_PATH_JAVA" = "" ]
then
  echo "Scripty is a command implemented in Java."
  echo "You need to install Java (Version 11 or higher)"
  exit
  #TODO: Test Java version -> else
fi


# TEST IF USER IS ROOT
if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi



# VARS

_ARGS=( "$@" )
_INFO=false




# FUNCTIONS

argsContains(){
  _argsContains=false

  for arg in "${_ARGS[@]}"
  do
    for narg in "$@"
    do
      if [ "$arg" = "$narg" ]
      then
        _argsContains=true
        return
      fi
    done
  done
}

info(){
  if [ "$_INFO" = "true" ]
  then
    echo "$@"
  fi
}

cmdlineHook(){
  echo "Creating sy command at '/usr/local/bin/sy'"
  curl -o /usr/local/bin/sy https://raw.githubusercontent.com/1Programm/Scripty/master/sy.sh
  curl -o /usr/local/bin/sy.d/scripty.jar https://raw.githubusercontent.com/1Programm/Scripty/master/releases/scripty-latest.jar
  chmod +x /usr/local/bin/sy
}





# CHECK ARGS
argsContains "-i" "--informative"

if [ "$_argsContains" = "true" ]
then
  _INFO=true
fi

info "Scripty Installer [0.1]"
info "Args: $@"





# SETUP COMMANDLINE HOOK
cmdlineHook