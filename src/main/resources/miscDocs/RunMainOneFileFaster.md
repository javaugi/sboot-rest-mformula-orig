That's a common frustration with Maven projects in IDEs like NetBeans! The good news is you can configure the NetBeans action for running a single file to 
    use a more efficient Maven goal that primarily focuses on compiling the single file's dependencies and then executing it.

You need to modify the Run File action properties in your project settings:

1. NetBeans Project Configuration for Faster Execution
2. Right-click on your Maven project in the NetBeans Projects window and select Properties.
3. In the Project Properties dialog, select the Actions category.
4. In the Actions pane, select the Run file via main() action.
5. Modify the following two properties in the "Run file via main()" action:
    Execute Goals: Change this to:
        process-classes org.codehaus.mojo:exec-maven-plugin:1.6.0:java
    From the default goal is:
        process-classes org.codehaus.mojo:exec-maven-plugin:1.6.0:java
    
The process-classes goal ensures that your classes are compiled, and then the exec:java goal is executed. Specifying the java goal for the 
    exec-maven-plugin is generally simpler than the exec goal for running a Java class. (Note: The version 1.6.0 or later is recommended, 
    NetBeans often uses this or a recent version by default)

Set Properties: 
    Change this to:
        exec.mainClass=${packageClassName} exec.cleanupDaemonThreads=false exec.classpathScope=compile
    From the default:
        exec.vmArgs=
        exec.args=${exec.vmArgs} -classpath %classpath ${exec.mainClass} ${exec.appArgs}
        exec.executable=java
        exec.mainClass=${packageClassName}
        exec.classpathScope=${classPathScope}

exec.mainClass=${packageClassName}: This tells the plugin to run the file you have selected in the IDE.
exec.classpathScope=compile: This limits the classpath scope, potentially speeding up dependency resolution compared to the default runtime scope, 
        which includes test dependencies.

Click OK to save the changes.

Now, when you right-click a Java file with a main method and select Run File, NetBeans will execute the process-classes goal (compiling the necessary parts) 
    followed by the exec:java goal for that specific class, which should be much faster than a full build.
