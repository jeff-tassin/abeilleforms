This project does not currently provide a shell script to run the application 
after a build.

To run the program you must include the following jar files in the classpath.
These files are located in the thirdparty subdirectory.

* batik-awt-util.jar - provides linear and radial gradient fill support
* dt.jar  - provides bean info classes for standard Swing components.
* looks-1.2.2.jar - JGoodies look and feel
* jh.jar - Java Help 

You must also include the following jar from the help subdirectory:
* formshelp.jar - Abeille Forms Designer help files.


Main class:
com.jeta.swingbuilder.main.Launcher 

Define the following system property to enable System.out on the console:
-Djeta1.debug=true


