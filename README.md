# Backend

This backend is meant for use with the frontend found at https://github.com/AAU-DAT-3-03/Frontend

Starting the backend:
Make sure to have Apache Maven 3.9.5 with Java version: 17.0.8.

Such that `mvn --version` looks something like this:

```
Apache Maven 3.9.5 (hash string)
Maven home: C:\Your\path\to\Maven\apache-maven-3.9.5
Java version: 17.0.8, vendor: Oracle Corporation, runtime: C:\Path\to\jdk-17
Default locale: da_DK, platform encoding: Cp1252
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

Make sure that mongoDB community edition is installed, is setup on the standardport and without authentication.
The connnection string needs to look like this: mongodb://localhost:27017/

The backend can then be built with `./build` to clean the current build, create new 
build and copy dependencies into project directories.
Then it can be run with `./start` to execute class dat3.app.App with necessary .jar files.

This project was developed as part of a semester project at AAU by group dat-3-03
