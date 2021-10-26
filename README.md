# Scripty

## Summary:
Scripty is an engine to execute custom commands specified by modules.
You can implement your own Module by using the scripty-modules-api Library.


## Requirements:
- **Java**: Scripty written mostly in Java, so you need Java 11 or higher!

## Installation:

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
modules-dev                 -> A command to manage the development of a custom module. It will start an interactive conversation to help you set up a module or to update it.
commands-list               -> Lists all installed commands.
repos-list                  -> Lists all specified repositories under which new modules will be discovered.
repos-add [url]             -> Add a new repository for discovery from [url].
repos-remove [url]          -> Removes a specific repository [url] from discovery.
```


#### Modules
A Module is basically just a package for commands.
A module can be used to publish to a repository and be downloaded from there.

#### 1.0 Custom Module
To create a custom Module one must use the dependency:
```xml
<dependency>
    <groupId>com.programm.projects</groupId>
    <artifactId>scripty-modules-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
<details>
<summary><b>1.1 Commands</b></summary>
A class can be either a scripty-command by annotating it with the `@Command` annotation.

A Method inside that class must also use the `@Command` annotation to declare that that method should be used to process the input for the command. If a class is annotated with the `@Command` annotation without specifying a value (name for the command) a name will be generated from the ClassName like following:
```
HelloWorld.class     -> 'hello-world'
CommandTest.class    -> 'test'
TestingCommand.class -> 'testing'
HelloCmd.class       -> 'hello'
```
The name will be checked for duplicates in the `SY-SETUP-PHASE`.
</details>


<details>
<summary><b>1.2 Services</b></summary>
A class can also be a scripty-service by annotating it with the `@Service` annotation.

You can annotate methods with the `@Command` annotation inside the service to define a scripty-command without using a separate class for it. The name for that command will be generated similar to when you use the class - annotation - approach.
</details>

<details>
<summary><b>1.3 Annotation controll</b></summary>
Inside a scripty-service or a scripty-command class you can use different annotations to get specific data and to register listeners for events.

#### 1.3.1 Get
You can use the `@Get` annotation like this:
```java
@Service
public class TestService {
    @Get
    private IContext ctx;
}
```
to get a value specified by the type of the variable.
Only certain values can be inserted. But also all loaded commands can be inserted this way:
```java
@Service
public class OtherService {
    @Get
    private TestService test;
}
```

If you don't have access to a specific class as they don't depend on each other you can use the command name to retrieve a command.
```java
@Service
public class YetAnotherService {
    @Get("other")
    private ICommand otherCmd;
}
```
The ICommand interface provides a way to run a command and to retrieve information about it. But beware it is not the actual command instance as that could be a method and does not need to implement the ICommand interface.

#### 1.3.2 Event Methods
There are 3 event-listener-annotations.

`@PreSetup` to run a method before (or more like while) Scripty is being set up.

`@PostSetup` to run a method after everything is finished setting up. Here you can finally use the variables initialized by the `@Get` annotation without worry, as some values might not be setup before.

`@PreShutdown` to run a method before scripty is being shut down. All resources and functions are still available but this method can only be called a certain amount of time before being canceled and Scripty shutting down.
</details>

<details>
<summary><b>1.4 Examples</b></summary>
Some examples to give some ideas ad clarity :D

#### Example 01 - Logging and output
```java
@Service
public class MyService {
    @Get
    private IContext ctx;
    
    @PreSetup
    public void init(){
        ctx.log().println("Hello from my service.");
        
        ctx.out().println("You should not do this as this will show this message everytime even if you don't use this command ...");
        
        ctx.err().println("An Error happened !!!");
    }
}
```

#### Example 02 - Method @Get Argument
```java
@Service
public class AService {
    
    @PreSetup
    public void init(@Get IContext ctx){
        ctx.log().println("Got the context through a method argument ;-)");
    }
}
```

#### Example 03 - Talking to a known Command

````java
@Command
public class PingCommand {
    
    private int num = 0;

    @Get
    private PongCommand pong;

    @Command
    public void run(@Get IContext ctx, Object... args) {
        if(args.length == 0){
            num = 10; //Do Ping-Pong 10x
        }
        if(args[0].getClass() != Integer.class){
            ctx.err().println("Should be called with an integer as first argument!");
            return;
        }
        
        ctx.out().println("Ping!");
        pong.run(ctx, num);
    }

}

@Command
public class PongCommand {
    
    @Get
    private PingCommand ping;
    
    @Command
    public void run(@Get IContext ctx, Object... args){
        if(args.length == 0){
            ctx.err().println("You should call Ping command!");
            return;
        }
        if(args[0].getClass() != Integer.class){
            ctx.err().println("Should be called with an integer as first argument!");
            return;
        }
        
        int num = (int) args[0];
        
        if(num > 0) {
            ctx.out().println("Pong!");
            ping.run(ctx, num - 1);
        }
    }
}
````
</details>

###### Author: Programm