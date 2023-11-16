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

Backend can then be built with `./build` to clean the current build, create new 
build and copy dependencies into project directories. Then it can be run with `./start`
 to execute class dat3.app.App with necessary .jar files.

## Endpoints!
### `/users`
```
GET:
Each field of a user will act as an equality filter, meaning you can find all users 
with the name "mads" by doing:
/users?name=mads
All responses will be a list of users, e.g.
{
    "statusCode":{integer},
    "users": [],
}

DELETE:
This endpoint does not accept anything but an equality filter on the id. Therefore, to 
delete a specific user, do:
/users?id={id}
This returns a statusCode of 0 if a user was deleted.

PUT:
This endpoint does not accept anything but an equality filter on the id. However, all other 
specified fields will be updated to that specified value. E.g.
/users?id={id}&name=Carly Rae Jepsen
will update the specific resource to the new name 'Carly Rae Jepsen'. This returns a 
statusCode of 0 if a user was updated. 

POST: 
This endpoint does not exist, since it is user registration, and is done by the 
endpoint '/register'.
```

### `/incidents`
```
GET:
Each field of an incident can act as an equality filter (except lists), which means 
you can filter by priority, header, acknowledgedBy and so on. Most commonly id will be used 
as a filter. To get all incidents use 'id=*'.
/incidents?id={id}
Which returns a list of incidents. If filtered by id, it will return a list of length one. 
Example response:
{
    "statusCode":0,
    "msg":[
        {
            "priority":1,
            "header":"A computer relentlessly just died",
            "acknowledgedBy": {
                "_id":"6555d373d417766094b78ffe",
                "email":"logan.emma.brown@matrix_technologies.gov",
                "name":"Logan Emma Brown",
                "phoneNumber":"89 06 78 78",
                "onCall":false,
                "onDuty":true
            },
            "creationDate":1700123507162,
            "id":"6555d373d417766094b79009",
            "users":[]
        }
    ]
}

DELETE:
This endpoint only accepts filters on id field. If the id field is not satisfied, the query 
will no complete and an error is returned.
To use this endpoint post a JSON body. Example body: 
{
    id: "6555d373d417766094b79009",
}

PUT:
This endpoint only accepts filters on id field. However, all other fields specified are 
intepreted as the value to update. Fir exanple:
{
    id: "6555d373d417766094b79009",
    header: "Set a new header!",
}
will update the incident by id '6555d373d417766094b79009' to the new header specified. 

POST:
This endpoint creates a new incident. One example of a body could be:
{
    "priority": 1,
    "header": "Some new header",
    "acknowledgedBy": "6555d373d417766094b79009",
    "creationDate": 1700123507162,
    "users": [
        "6555d373d417766094b79009",
        "6555d373d411234094b45687",
        "09872345987890237987b213",
        "3245d9043890d9560b494459",
    ],
}
it is important to note that an incident will be created as long as there is at least one field 
specified AND the id is not specified.
```
