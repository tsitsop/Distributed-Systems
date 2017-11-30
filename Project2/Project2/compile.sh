CLASSPATH=src:lib/joda-time-2.4.jar

javac -classpath $CLASSPATH src/main/java/events/*.java src/main/java/messages/*.java src/main/java/*.java
