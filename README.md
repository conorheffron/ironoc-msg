# ironoc-msg

[![Java CI with Gradle](https://github.com/conorheffron/ironoc-msg/actions/workflows/gradle.yml/badge.svg)](https://github.com/conorheffron/ironoc-msg/actions/workflows/gradle.yml)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

### Overview
 - Sample client server messaging service over web sockets.

### Tech
 - JDK 17, Spring Messaging, STOMP js, Gradle 8

### Quick Start

### 1. Build Project
```shell
./gradlew clean build
```

### 2. Run Application
```shell
./gradlew bootRun
```

### 3. Open a series of browsers (Edge, Chrome, Firefox..), login, connect, register web socket session & see who is late to the conversation
```shell
http://localhost:8080/
```

#### Login (Spoofed Security for Demo Purposes)
![spring-security-login](./screenshots/spring-security-login.png)

#### Testing in various Browsers
![multi-browser-sessions](./screenshots/multi-browser-sessions.png)
