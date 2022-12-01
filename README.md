# Spring Darts Game

This projects provides a backend service for a darts game. This is a reference project I built to get acquainted with
the authentication and authorization with OAuth, learn about access tokens (JWT), and create a backend for the online
darts service. I used the Spring Boot framework in this project.

It's based on a [JetBrains Academy](https://hyperskill.org/projects/228) project.

## Requirements

To build and run this project, you need:

- [JDK 17](https://www.openjdk.java.net/projects/jdk/17/)
- [Gradle 7.5](https://gradle.org/install/)

## How to use

Clone the repository and run the following commands to build and run the project:

```shell
./gradlew build
./gradlew bootRun
```

The service is available at `http://localhost:28852`. The port can be changed in the `application.properties` file.

### Processes

- [Get a token](#get-a-token)
- [Create a new game](#create-a-new-game)
- [Get a list of all games](#get-a-list-of-all-games)
- [Join a game](#join-a-game)
- [Get the status of a game](#get-the-status-of-a-game)
- [Throw a dart](#throw-a-dart)
- [View the history](#view-the-history)
- [Cancel a game](#cancel-a-game)
- [Revert a move](#revert-a-move)

### Endpoints

To access the endpoints you need to have a valid bearer token. Read the [Get a token](#get-a-token) section to learn how
to get one.

| Endpoint | ALL | GAMER | REFEREE | ADMIN |
|---|---|---|---|---|
| POST `/api/game/create` | - | + | - | - |
| GET `/api/game/list` | - | + | - | - |
| GET `/api/game/join{gameid}` | - | + | - | - |
| GET `/api/game/status` | - | + | - | - |
| POST `/api/game/throw` | - | + | - | - |
| GET `/api/game/history` | - | + | + | - |
| PUT `/api/game/cancel` | - | - | + | - |
| PUT `/api/game/revert` | - | - | + | - |

#### Get a token

_Note: The users and authentication are hard coded in memory. They can be found in the `SecurityConfiguration` class.
The client id and secret are also hard coded in the `OAuth2Configuration` class. This is for demonstration purposes
only._

Start Postman (or any other tool you prefer) and send a POST request to `http://localhost:28852/oauth/token` with a
http basic authentication using the client id and secret from the `OAuthConfiguration` class.

```
POST /oauth/token
```

Response:

```
{
    "access_token": <token>,
    "token_type": "bearer",
    "refresh_token": <refresh_token>,
    "expires_in": 3599,
    "scope": "read write update",
    "jti": <jti>
}
```

#### Create a new game

```
POST `/api/game/create`
{
  "targetScore": <Integer>
}
```

Response:

```
{
    "gameId": <Integer>,
    "playerOne": <String PlayerOne>,
    "playerTwo": "",
    "gameStatus": <Enum>
    "playerOneScores": <Integer>,
    "playerTwoScores": <Integer>,
    "turn": <String>
}
```

#### Get a list of all games

```
GET `/api/game/list`
```

Response:

```
[
    {
        "gameId": <Integer>,
        "playerOne": <String PlayerOne>,
        "playerTwo": "",
        "gameStatus": <Enum>
        "playerOneScores": <Integer>,
        "playerTwoScores": <Integer>,
        "turn": <String>
    },
    ...
]
```

#### Join a game

```
GET `/api/game/join{gameid}`
```

Response:

```
{
    "gameId": <Integer>,
    "playerOne": <String PlayerOne>,
    "playerTwo": <String PlayerTwo>,
    "gameStatus": <Enum>
    "playerOneScores": <Integer>,
    "playerTwoScores": <Integer>,
    "turn": <String>
}
```

#### Get the status of a game

```
GET `/api/game/status`
```

Response:

```
{
    "gameId": <Integer>,
    "playerOne": <String PlayerOne>,
    "playerTwo": <String PlayerTwo>,
    "gameStatus": <Enum>
    "playerOneScores": <Integer>,
    "playerTwoScores": <Integer>,
    "turn": <String>
}
```

#### Throw a dart

```
POST `/api/game/throw`
{
  "first": "<multiplicator>:<score>",
  "second": "<multiplicator>:<score> or <none>",
  "third": "<multiplicator>:<score> or <none>"
}
```

Response:

```
{
    "gameId": <Integer>,
    "playerOne": <String PlayerOne>,
    "playerTwo": <String PlayerTwo>,
    "gameStatus": <Enum>
    "playerOneScores": <Integer>,
    "playerTwoScores": <Integer>,
    "turn": <String>
}
```

#### View the history

```
GET `/api/game/history`
```

Response:

```
[
   {
      "gameId":"<Long>",
      "move": 0,
      "playerOne": <String PlayerOne>,
      "playerTwo": <String PlayerTwo>,
      "gameStatus":"started",
      "playerOneScores":"<Integer>",
      "playerTwoScores":"<Integer>",
      "turn":"<String>"
   },
    ...
]
```

#### Cancel a game

```
PUT `/api/game/cancel`
{
  "gameid": <Integer>,
  "status": <String>
}
```

Response:

```
{
  "gameId": <Integer>,
  "playerOne": <String PlayerOne>,
  "playerTwo": <String PlayerTwo>,
  "gameStatus": <String "new status">,
  "playerOneScores": <Integer>,
  "playerTwoScores": <Integer>,
  "turn": <String>
}
```

#### Revert a move

```
PUT `/api/game/revert`
{
  "gameid": <Integer>,
  "move": <Integer>
}
```

Response:

```
{
  "gameId": <Integer>,
  "playerOne": <String PlayerOne>,
  "playerTwo": <String PlayerTwo>,
  "gameStatus": <String>,
  "playerOneScores": <Integer>,
  "playerTwoScores": <Integer>,
  "turn": <String>
}
```

## Architecture

The system is built on a Spring Framework application context. The application itself follows the model-view-controller
pattern. The application consists of the following components:

- **Authentication**: The authentication is done with OAuth2. The client id and secret are hard coded in
  the `OAuth2Configuration`.
- **Authorization**: The authorization is done with Spring Security. The users and roles are hard coded in
  the `SecurityConfiguration`.
- **Controller**: The controller is responsible for handling the requests and responses. It uses the service to get the
  data.
- **Entity**: The entity classes are used to store the data in the database.
- **Model**: The model classes are used to transfer data between the controller and the service and representing the
  game
  data.
- **Service**: The service is responsible for the business logic. It uses the repository to get the data.
- **Repository**: The repository is responsible for the data access. It uses the database to get the data.
- **Database**: The database is used to store the data. It's an in-memory H2 database.
- **Utility**: The utility classes are used to provide helper methods.

## Stack

- Java 17
- Gradle 7.5
- Spring Boot 2.5.6
- H2 Database 1.4.200*

_*) Upgrading to the latest version (2.0.202) causes
a `java.lang.NoSuchMethodError: org.h2.mvstore.MVStore.open(Ljava/lang/String;Ljava/lang/String;I)V` error. The database
model is not compatible with the latest version._

## Dependencies

- [Spring Boot Starter 2.7.6](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)
- [Spring Security OAuth2 Autoconfigure 2.6.8](https://mvnrepository.com/artifact/org.springframework.security.oauth.boot/spring-security-oauth2-autoconfigure)
- [Spring Boot Starter Data JPA 2.7.6](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
- [H2 Database 1.4.200](https://mvnrepository.com/artifact/com.h2database/h2)
- [Lombok 1.18.24](https://mvnrepository.com/artifact/org.projectlombok/lombok)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Notes

This project is for reference purposes only. Many solutions are not optimal and can be improved. Especially the security
is not optimal. The authentication and authorization is done with in-memory users and roles. The client id and secret
are also hard coded. **This is for demonstration purposes only.** In a real world application, the users and roles
should be
in a database and the client id and secret should be stored in a secure place.
