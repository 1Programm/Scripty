#!/bin/bash


# [UNIX] Scripty 0.1




# VARS

_ARGS=( "$@" )
_VERSION=0.1


# FUNCTIONS

printHelp(){
  echo "sy (-v | --version)  -> Print Version"
  echo "sy (-h | --help)     -> Print Help"
  echo "sy uninstall [--yes] -> Uninstall Scripty (--yes forces the uninstallation)"
}

equalsMultiple(){
  _equalsMultiple=false

  for ((i = 2; i <= $#; i++ )); do
    if [ "${!i}" = "$1" ]
    then
      _equalsMultiple=true
      return
    fi
  done
}

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

runCommands(){
  if [ "$1" = "uninstall" ]
  then
    _answer=false

    if [ "$2" = "--yes" ]
    then
      _answer=true
    else
      read -p "Are you sure you want to uninstall scripty? [y/N]: " _r
      if [ "$_r" = "y" ]
      then
        _answer=true
      fi
    fi

    if [ "$_answer" = "true" ]
    then
      echo "Uninstalling Scripty..."
      sudo rm /usr/local/bin/sy
      sudo rm -r /usr/local/bin/sy.d
      echo "Done"
    else
      echo "Canceled!"
    fi

    exit
  fi

  equalsMultiple "$1" "-v" "--version"
  if [ "$_equalsMultiple" = "true" ]
  then
    echo "Scripty version $_VERSION"
    exit
  fi

  equalsMultiple "$1" "-h" "--help"
  if [ "$_equalsMultiple" = "true" ]
  then
    printHelp
    exit
  fi
}

# Try to run basic 'outer' commands so that java must not be started
runCommands "$@"

# No 'outer' command -> execute java scripty engine

java -jar /usr/local/bin/sy/scripty.jar "@a"