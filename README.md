# Scripty

## Summary:
Scripty is an engine to execute custom commands specified by modules.
You can implement your own Module by using the scripty-modules-api Library.


## Requirements:
- **Java**: Scripty written mostly in Java, so you need Java 11 or higher!

## Installations:

<details>
<summary><b>Unix</b></summary>

Simply use:
```
sudo curl -s https://raw.githubusercontent.com/1Programm/Scripty/master/install.sh | sudo bash
```

The installation will create a bash script in /usr/local/bin which is a default of the environment PATH variable.\
Because of that the name of that file there can then be used as a command to execute that file.


#### Options:
|Option|Description|
|---|---|
|[version]|Specifies what version you want to download. Check https://github.com/1Programm/Scripty/tree/master/releases for possible versions!|
|-i / --info|Prints debug info when installing Scripty.|

```
sudo curl -s https://raw.githubusercontent.com/1Programm/Scripty/master/install.sh | sudo bash -s -- [options]
```

#### On error:
If running the command tells you that the Java Runtime is not installed follow this link:
[https://stackoverflow.com/questions/12309253/sudo-java-command-not-found-after-exiting-from-root-user](https://stackoverflow.com/questions/12309253/sudo-java-command-not-found-after-exiting-from-root-user)

#### Scripty Home:
Default: ~/.scripty/

</details>

<details>
<summary><b>Windows</b> (Not supported yet)</summary>
Not supported yet!
</details>

## The 'sy' Command:

### Usage:
```
sy [scripty-optionals] [command] [command-args]
```

#### Scripty-Optionals
|Optional|Description|
|---|---|
|-i / --info|Prints more debug info when running the command.|
|-v / --version|Prints the version of scripty and exits.|
|-h / --help|Prints Help and exits.|

#### Builtin-Commands
```
help                        -> Help command.
uninstall (--yes)           -> Uninstall Scripty (--yes forces the uninstallation)
update                      -> Update to latest version.
modules-list                -> Lists all installed modules.
modules-add [name] ([dest]) -> Searches in [sy-repositories] for the module [name] and addes the module at the [dest] - path. 
                               (If no [dest] is specified it will install the module at [scripty-home]/modules/[name]/)
modules-remove [name]       -> Removes the module [name].
modules-update ([name])     -> Updates a module specified by [name]. If no name is specified it will update all installed modules.
modules-dev                 -> *1
commands-list               -> Lists all installed commands.
repos-list                  -> Lists all specified repositories under which new modules will be discovered.
repos-add [url]             -> Add a new repository for discovery from [url].
repos-remove [url]          -> Removes a specific repository [url] from discovery.
```

##### *1 [modules-dev]:
A command to manage the development of a custom module.\
It will start an interactive conversation to help you set up a module or to update it. \
You will answer questions which can be answered by either a normal answer or by pressing only enter which takes the default to that question as an answer.