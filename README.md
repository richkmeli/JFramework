# JFramework

[![Build Status](https://travis-ci.org/richkmeli/JFramework.svg?branch=master)](https://travis-ci.org/richkmeli/JFramework)
[![](https://jitpack.io/v/richkmeli/JFramework.svg)](https://jitpack.io/#richkmeli/JFramework)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f721d8e5c7ba4a9da809808c6892333b)](https://app.codacy.com/app/richkmeli/JFramework?utm_source=github.com&utm_medium=referral&utm_content=richkmeli/JFramework&utm_campaign=Badge_Grade_Dashboard)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/richkmeli/JFramework.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/richkmeli/JFramework/context:java)
[![codecov](https://codecov.io/gh/richkmeli/JFramework/branch/master/graph/badge.svg)](https://codecov.io/gh/richkmeli/JFramework)

Framework for Java 

## Description

JFramework is a Java framework containing several features, with the aim of simplifying application development. 
It provides the following functionalities:

-   **Authentication**
-   **Cryptography**: the entry point class is Crypto. Its protocol is based on server-client model.
-   **Network**
-   **ORM**: supported database: MySQL (Network Server) and Derby (Embedded Server)
-   **System**
-   **Utility**

For every parts it is generated an independent jar (with all its dependencies included) in case of it is necessary doing 
a partial usage of JFramework. The compiled components are placed into **release** folder. 

## Get Started

### Import in your project

To get **JFramework** into your project, you can use: 
-   
-   JFramework Github

|  Repository  | Visibility | Version | 
|--------------|:----:|:---:|
| JitPack  | Public | stable |
| JFramework Github | Github Users | all |

#### Add the JitPack repository to your maven build file
On JitPack repo are published only stable versions and the release scope is public.

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
		...
	</repositories>
	
##### Add the dependency

For all the framework:

    <dependency>
        <groupId>com.github.richkmeli</groupId>
        <artifactId>JFramework</artifactId>
        <version>TAG</version>
    </dependency>
   
For a specific module (auth, crypto, ...) of the framework:
  
    <dependency>
        <groupId>com.github.richkmeli.JFramework</groupId>
        <artifactId>MODULE_NAME</artifactId>
        <version>TAG</version>
    </dependency> 
	
#### Add the JFramework Github repository to your build file
On JFramework Github repo are published all versions and the release scope is Github user only.
(Configuring [Apache Maven](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages) or [Gradle](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages) for use with GitHub Packages)

	 <repositories>
            <repository>
                <id>github</id>
                <url>https://maven.pkg.github.com/richkmeli/JFramework</url>
            </repository>
            ...
        </repositories>

##### Add the dependency

For all the framework:

	<dependency>
	    <groupId>it.richkmeli.jframework</groupId>
	    <artifactId>JFramework</artifactId>
	    <version>TAG</version>
	</dependency>
   
For a specific module (auth, crypto, ...) of the framework:
  
    <dependency>
        <groupId>it.richkmeli.jframework</groupId>
        <artifactId>MODULE_NAME</artifactId>
        <version>TAG</version>
    </dependency> 
    
### Compile

JFramework uses maven as build automation tool, the root (parent) project is a maven multimodules project,
namely **JFramework-multimodules**, in which are contained JFramework and all its sub projects as modules.


To get the jar files, you can download it from the [JFramework Releases](https://github.com/richkmeli/JFramework/releases), instead if you want to build the jar files by yourself, you need to download:

-   java
-   maven

To compile the project and generate the jar file, you have to run:

    mvn package
    
The jar files are located into the folder "release".

