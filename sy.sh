#!/bin/bash


# [UNIX] Scripty 0.1




# VARS

_ARGS=( "$@" )
_VERSION=0.1


# FUNCTIONS

info(){
  if [ "$_INFO" = "true" ]
  then
    echo "$@"
  fi
}

printHelp(){
  echo
  echo "# Scripty Help"
  echo "| sy (-v | --version)  -> Print Version"
  echo "| sy (-h | --help)     -> Print Help"
  echo "| sy uninstall [--yes] -> Uninstall Scripty (--yes forces the uninstallation)"
  echo "| sy update            -> Update to latest version"
  echo
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
      echo "Are you sure you want to uninstall scripty? [y/N]: y"
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

      info "Removing script command at [/usr/local/bin/sy] ..."
      sudo rm /usr/local/bin/sy

      info "Removing workspace folder at [/usr/local/bin/sy.d] ..."
      sudo rm -r /usr/local/bin/sy.d

      echo "Done"
    else
      echo "Canceled!"
    fi

    exit
  fi

  if [ "$1" = "update" ]
  then
    _CUR_VERSION="$(cat /usr/local/bin/sy.d/version)"

    info "Downloading versions file: [https://raw.githubusercontent.com/1Programm/Scripty/master/releases/versions] ..."
    _VERSIONS_CONTENT="$(curl -sS https://raw.githubusercontent.com/1Programm/Scripty/master/releases/versions)"
    read -a _SY_VERSIONS <<< $_VERSIONS_CONTENT

    _SY_LATEST="${_SY_VERSIONS[0]}"

    if [ "$_CUR_VERSION" = "$_SY_LATEST" ]
    then
      echo "Scripty already up to date!"
    else
      echo "Updating Scripty [$_CUR_VERSION > $_SY_LATEST] ..."

      info "Updating version file at [/usr/local/bin/sy.d/version]"
      sudo rm /usr/local/bin/sy.d/version
      sudo bash -c "echo $_SY_LATEST > /usr/local/bin/sy.d/version"


      info "Updating Scripty-Engine at [/usr/local/bin/sy.d/scripty.jar]"
      sudo rm /usr/local/bin/sy.d/scripty.jar
      _TMP_PATH="https://raw.githubusercontent.com/1Programm/Scripty/master/releases/scripty-$_SY_LATEST.jar"
      info "Downloading Scripty-Engine from [$_TMP_PATH] ..."
      sudo curl -sS -o /usr/local/bin/sy.d/scripty.jar "$_TMP_PATH"

      echo "Scripty successfully updated to version [$_SY_LATEST]!"
    fi

    exit
  fi

  equalsMultiple "$1" "-v" "--version"
  if [ "$_equalsMultiple" = "true" ]
  then
    cat /usr/local/bin/sy.d/version
    exit
  fi

  equalsMultiple "$1" "-h" "--help"
  if [ "$_equalsMultiple" = "true" ]
  then
    printHelp
    exit
  fi

  equalsMultiple "$1" "--is-scripty"
  if [ "$_equalsMultiple" = "true" ]
  then
    echo "Recognize Token: 53 63 72 69 70 74 79"
    exit
  fi
}

argsContains "-i" "--informative"

if [ "$_argsContains" = "true" ]
then
  _INFO=true
fi

# Try to run basic 'outer' commands so that java must not be started
runCommands "$@"

# No 'outer' command -> execute java scripty engine

java -jar /usr/local/bin/sy.d/scripty.jar "$@"