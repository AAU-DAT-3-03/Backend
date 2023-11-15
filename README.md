# Backend

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

Backend can then be built with `./build` to clean the current build, create new build and copy dependencies into project directories.
Then it can be run with `./start` to execute class dat3.app.App with necessary .jar files.

```
Endpoint:
/users

GET:
Each field of a user will act as an equality filter, meaning you can find all users with the name "mads" by doing:
/users?name=mads
All responses will be a list of users, e.g.
{
    "statusCode":{integer},
    "users": [],
}

DELETE:
This endpoint does not accept anything but an equality filter on the id. Therefore, to delete a specific user, do:
/users?id={id}
This returns a statusCode of 0 if a user was deleted.

PUT:
This endpoint does not accept anything but an equality filter on the id. However, all other specified fields will be updated to that specified value. E.g.
/users?id={id}&name=Carly Rae Jepsen
will update the specific resource to the new name 'Carly Rae Jepsen'. This returns a statusCode of 0 if a user was updated. 
```
