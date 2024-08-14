## Socket chat app

Spring Boot WebSocket Chat Application

## Description

This project is a real-time chat application built using Spring Boot and WebSocket. It allows users to join a chat room, send messages, receive read receipts, and see typing indicators. The application also supports private messaging between users.

## Features

- Real-time messaging
- User join/leave notifications
- Typing indicators
- Read receipts
- Private messaging
- User list management

## Technologies Used

- Java
- Spring Boot
- WebSocket
- STOMP
- SockJS
- Maven
- HTML/CSS/JavaScript
- Bootstrap

## Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/your-username/spring-boot-websocket-chat.git
cd spring-boot-websocket-chat
```

### Build the Project

```sh
mvn clean install
```

### Run the Application

```sh
mvn spring-boot:run
```

### Access the Application

Open your web browser and navigate to `http://localhost:8080`.

## Usage

1. Enter your username and click "Connect".
2. Type your message in the input field and click "Send".
3. Click on a user in the user list to send a private message.
4. Messages will display read receipts when they are read by the recipient.
5. Typing indicators will show when a user is typing.

## Project Structure

- `src/main/java/com/socketchat` - Contains the Java source code.
    - `controller` - Contains the Spring Boot controllers.
    - `message` - Contains the message classes.
- `src/main/resources/static` - Contains the static resources (HTML, CSS, JavaScript).
- `src/main/resources/application.properties` - Contains the application configuration.
- `pom.xml` - Maven configuration file.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgements

- Spring Boot
- WebSocket
- STOMP
- SockJS
- Bootstrap
