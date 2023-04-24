# d-sync

First, make sure you are in the d-sync directory.
Here are some commands you can use :

Compile everything:
javac src/gui/*.java; javac src/syncing/*.java; javac src/*.java

Run from terminal :
java -cp . src.Main

Create exectutable jar file :
jar -cvmf manifest.mf Dsync.jar ./classFiles ./src ./LICENSE.md ./README.md

(The Main-Class attribute is defined in manifest.mf)

Compile and run :
javac src/gui/*.java; javac src/syncing/*.java; javac src/*.java ; java -cp . src.Main

Compile and create jar :
javac src/gui/*.java; javac src/syncing/*.java; javac src/*.java ; jar -cvmf manifest.mf Dsync.jar ./classFiles ./src ./LICENSE.md ./README.md
