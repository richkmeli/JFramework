# JFramework

[![Build Status](https://github.com/richkmeli/JFramework/workflows/Build%20and%20Test/badge.svg)](https://github.com/richkmeli/JFramework/actions)
[![](https://jitpack.io/v/richkmeli/JFramework.svg)](https://jitpack.io/#richkmeli/JFramework)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f721d8e5c7ba4a9da809808c6892333b)](https://app.codacy.com/app/richkmeli/JFramework?utm_source=github.com&utm_medium=referral&utm_content=richkmeli/JFramework&utm_campaign=Badge_Grade_Dashboard)
[![codecov](https://codecov.io/gh/richkmeli/JFramework/branch/master/graph/badge.svg)](https://codecov.io/gh/richkmeli/JFramework)

**JFramework** is a modular Java library designed to simplify application development by providing a set of ready-to-use components.

## Features

- **Authentication**: Secure authentication mechanisms.
- **Cryptography**: Encrypts and decrypts data using a client-server protocol.
- **Network**: Utilities for network communication.
- **ORM**: Object-Relational Mapping supporting MySQL and Derby.
- **System**: System info and utilities.
- **Utility**: General purpose helper functions.

## Architecture

The project is a **multi-module Maven project**. It generates:
1. A **single comprehensive JAR** containing all modules (`jframework`).
2. **Individual JARs** for each module (`auth`, `crypto`, `network`, etc.), allowing you to import only what you need.

## Requirements

- **Java 17** or higher
- **Maven 3.6+**

## Getting Started

### Installation via Maven

You can import **JFramework** using GitHub Packages.

#### GitHub Packages

**Repository Configuration** (`~/.m2/settings.xml`):
```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**Repository in pom.xml**:
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/richkmeli/JFramework</url>
    </repository>
</repositories>
```

**Dependency**:
```xml
<dependency>
    <groupId>it.richkmeli.jframework</groupId>
    <artifactId>jframework</artifactId>
    <version>1.2.15</version>
</dependency>
```

**Note**: GitHub Packages requires authentication. Create a Personal Access Token with `read:packages` scope.

### Compiling from Source

To build the project locally:

1. **Prerequisites**:
   - Java 11+
   - Maven

2. **Build**:
   ```bash
   mvn package
   ```

3. **Output**:
   Artifacts will be generated in the `release/` folder.


