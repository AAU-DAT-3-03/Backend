@echo off
javac -sourcepath src/main/java -d out src/main/java/dat3/server/Main.java
java -cp out dat3/server/Main