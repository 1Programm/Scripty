#!/bin/bash

# [UNIX] Scripty Installer

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

sy_install(){
  if [ -f "/usr/local/bin/sy" ]
  then
    echo "/usr/local/bin/sy already exists!"
    echo "Could not install scripty!"
    exit
  fi


  info "Downloading versions file: [https://raw.githubusercontent.com/1Programm/Scripty/master/releases/versions] ..."
  _VERSIONS_CONTENT="$(curl -sS https://raw.githubusercontent.com/1Programm/Scripty/master/releases/versions)"
  read -a _SY_VERSIONS <<< $_VERSIONS_CONTENT

  for arg in "${_ARGS[@]}"
  do
      if [[ $arg != -*  ]]
      then
        _SY_DOWNLOAD_VERSION="$arg"
        break
      fi
  done

  if [ "$_SY_DOWNLOAD_VERSION" = "" ]
  then
    info "No version specified! Loading latest version ..."
    _SY_DOWNLOAD_VERSION="${_SY_VERSIONS[0]}"
  else
    info "A specific version was specified [$_SY_DOWNLOAD_VERSION]. Testing if it is a valid version ..."
    _TEST=false
    for v in "${_SY_VERSIONS[@]}"
    do
      if [ "$v" = "$_SY_DOWNLOAD_VERSION" ]
      then
        _TEST=true
        break
      fi
    done

    if [ "$_TEST" = "false" ]
    then
      echo "Invalid version: $_SY_DOWNLOAD_VERSION !"
      exit
    else
      info "Valid version $_SY_DOWNLOAD_VERSION!"
    fi
  fi

  info ""

  echo "Downloading version [$_SY_DOWNLOAD_VERSION] ..."
  echo "Creating sy command at [/usr/local/bin/sy]"

  info ""
  info "Setting up workspace folder at [/usr/local/bin/sy.d] ..."
  mkdir /usr/local/bin/sy.d

  info "Downloading 'sy' script command from [https://raw.githubusercontent.com/1Programm/Scripty/master/sy.sh] ..."
  curl -sS -o /usr/local/bin/sy https://raw.githubusercontent.com/1Programm/Scripty/master/sy.sh
  chmod +x /usr/local/bin/sy

  _TMP_PATH="https://raw.githubusercontent.com/1Programm/Scripty/master/releases/scripty-$_SY_DOWNLOAD_VERSION.jar"
  info "Downloading Scripty-Engine from [$_TMP_PATH] ..."
  curl -sS -o /usr/local/bin/sy.d/scripty.jar "$_TMP_PATH"

  info "Creating version file at [/usr/local/bin/sy.d/version] ..."
  echo "$_SY_DOWNLOAD_VERSION" > /usr/local/bin/sy.d/version

  info ""
  echo "Installation successful!"
}





# CHECK ARGS
argsContains "-i" "--informative"

if [ "$_argsContains" = "true" ]
then
  _INFO=true
fi

info "Scripty Installer"
info "Args: [$@]"
info ""





# SETUP COMMANDLINE HOOK
sy_install "$@"