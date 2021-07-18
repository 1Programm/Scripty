#!/bin/bash

# [UNIX] Scripty Installer 0.1

# TEST IF 'sy' IS ALREADY INSTALLED (or name sy is taken)

_PATH_SY="$(command -v sy)"
if [ "$_PATH_SY" != "" ]
then
  _SY_TOKEN="$(sy --is-scripty)"

  if [ "$_SY_TOKEN" = "Recognize Token: 53 63 72 69 70 74 79" ]
  then
    _SY_VERSION="$(sy --version)"
    echo "$_SY_VERSION"
    echo "Scripty already installed. Use 'sy update' to update scripty or 'sy uninstall' to uninstall it!"
  else
    echo "Some other command uses the key 'sy' - Scripty cannot be installed!"
  fi

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
  if [ -f "/usr/local/bin/sy" ]
  then
    echo "/usr/local/bin/sy already exists!"
    echo "Could not install scripty!"
    exit
  fi

  _SY_VERSIONS=( "$(curl -sS https://raw.githubusercontent.com/1Programm/Scripty/master/releases/versions)" )
  _SY_DOWNLOAD_VERSION="$1"

  echo "VERSIONS:" "${_SY_VERSIONS[@]}"

  if [ "$_SY_DOWNLOAD_VERSION" = "" ]
  then
    _SY_DOWNLOAD_VERSION="${_SY_VERSIONS[0]}"
  fi

  echo "Downloading version $_SY_DOWNLOAD_VERSION"

  echo "Creating sy command at '/usr/local/bin/sy'"
  mkdir /usr/local/bin/sy.d

  cp /Users/julian/Desktop/Programming/Java/projects/scripty/sy.sh /usr/local/bin/sy
  cp /Users/julian/Desktop/Programming/Java/projects/scripty/releases/scripty-"$_SY_DOWNLOAD_VERSION".jar /usr/local/bin/sy.d/scripty.jar

  #curl -sS -o /usr/local/bin/sy https://raw.githubusercontent.com/1Programm/Scripty/master/sy.sh
  chmod +x /usr/local/bin/sy

  #curl -sS -o /usr/local/bin/sy.d/scripty.jar https://raw.githubusercontent.com/1Programm/Scripty/master/releases/scripty-latest.jar
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
cmdlineHook "$@"