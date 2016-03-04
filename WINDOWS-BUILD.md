# Building on Windows

## Needed software:
IntelliJ IDEA Community Edition (https://www.jetbrains.com/idea/download/)

JDK 1.8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

## Installation
After downloading both installers do the following:

First install JDK. Follow the instructions of the installer and if you choose a different installation location remember it or write it down.
Now install IntelliJ by again following all the installer instructions.

Start IntelliJ and configure it with the first start configuration wizard. The default configuration should be fine but make shure that all of the subversion services and Maven gets enabled.

## Loading the sourcecode
Now there are two options:

1. First run of IntelliJ:

    Project from Version Control > GitHub

2. You already have created a project prior to making this:

    Navigate to File > New > Project from Version Control > GitHub

For both options:

Enter the following:

URL: https://github.com/SG-O/DecredJWallet.git

Parent Directory: Wherever you want.

Directory Name: Leave it or change to whatever you want.

Clone

If you are asked if you want to open the project click on yes.

In the upper right corner you will be asked to import Maven projects. Enable auto import there.

## Test if everything works
Open the Project Explorer on the left side of the window (1: Project) and navigate to:

DecredJWallet > src > main > java

and open main in that folder with a double click.

Chose Run... under the menu Run and in the dialog that opened click on main.

Now DecredJWallet should run for the first time. You might be asked to download the binaries and you should do so.

After a short loading time you now should see the main GUI unless you haven't created a wallet yet.

## Building the GUI

Under build click on Build Artifacts...
Now click on build.

If no error message is shown you have successfully built DecredJWallet.
The jar file is located in the subfolder classes/artifacts/DecredJWallet_jar of the folder you you cloned the sourcecode in.

## HELP
If you have any problems with these instructions please post in the following forum:
https://forum.decred.org/threads/decred-j-wallet-java-gui-wallet.382/