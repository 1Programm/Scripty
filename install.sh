#!/bin/sh

# [UNIX] Scripty Installer 0.1

# TEST IF 'sy' IS ALREADY INSTALLED (or name sy is taken)
if command -v sy &> /dev/null
then
  echo "Scripty already installed or some other command uses 'sy'!"
  exit
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
      if [ "$arg" == "$narg" ]
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
  chmod +x /usr/local/bin/sy
}




# STD PRINT
clear
echo "--- Scripty Installer 0.1 for UNIX ---"
echo "--------------------------------------"





# CHECK ARGS
argsContains "-i" "--informative"

if [ "$_argsContains" = "true" ]
then
  _INFO=true
fi

info "Started installer with [" "$@" "]"





# SETUP COMMANDLINE HOOK
cmdlineHook