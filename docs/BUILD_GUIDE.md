# BlueFlex Framework - Complete Build Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture Flowchart](#system-architecture-flowchart)
3. [Build Process Flowchart](#build-process-flowchart)
4. [Service Lifecycle Flowchart](#service-lifecycle-flowchart)
5. [Quick Start Guide](#quick-start-guide)
6. [Detailed Build Instructions](#detailed-build-instructions)
7. [Development Workflow](#development-workflow)
8. [Troubleshooting](#troubleshooting)

---

## Project Overview

**BlueFlex** is a lightweight Java service framework that manages multiple services with dependency injection, lifecycle management, and configuration loading.

### Key Features
- 🔌 Service registration and management
- ⚙️ JSON-based configuration
- 🔄 Lifecycle management (start/stop/restart)
- 📊 CLI interface for service control
- 🎨 Optional UI components
- 🔧 Configurable services with dependency injection

### Technology Stack
- **Language**: Java 17+
- **Dependencies**: Jackson (JSON processing)
- **Build Tools**: Manual compilation, Maven, or Gradle
- **Architecture**: Service-oriented framework

---

## System Architecture Flowchart

```
┌─────────────────────────────────────────────────────────────────┐
│                         BlueFlex Framework                        │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
                    ┌─────────────────────────┐
                    │      Main.java          │
                    │   (Entry Point)         │
                    └─────────────┬───────────┘
                                  │
                    ┌─────────────▼────────────┐
                    │   ServiceManager         │
                    │  - Load config           │
                    │  - Register services     │
                    │  - Start/Stop control    │
                    └─────────────┬────────────┘
                                  │
                ┌─────────────────┼─────────────────┐
                │                 │                 │
                ▼                 ▼                 ▼
       ┌────────────────┐  ┌───────────┐  ┌──────────────┐
       │ Configuration  │  │   CLI     │  │   AppLogger  │
       │   Loader       │  │ Commands  │  │   (Utils)    │
       └────────┬───────┘  └─────┬─────┘  └──────────────┘
                │                 │
                ▼                 │
      ┌──────────────────┐        │
      │ services.json    │        │
      │  - className     │        │
      │  - enabled       │        │
      │  - config        │        │
      └──────────────────┘        │
                                  │
                ┌─────────────────┴─────────────────┐
                │                                   │
                ▼                                   ▼
    ┌───────────────────────┐         ┌───────────────────────┐
    │   AppService          │         │ ConfigurableAppService│
    │   (Base Class)        │◄────────│      (Interface)      │
    │  - start()            │         │  - configure(Map)     │
    │  - stop()             │         └───────────────────────┘
    │  - isRunning()        │
    └───────────┬───────────┘
                │
                │ (Inherited by)
                │
    ┌───────────┴───────────────────────────┐
    │                                       │
    ▼                                       ▼
┌──────────┐                        ┌──────────────┐
│  MainUI  │                        │ HelloService │
│ (Swing)  │                        │  (Example)   │
└──────────┘                        └──────────────┘
```

---

## Build Process Flowchart

```
                    ┌──────────────────┐
                    │   START BUILD    │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │ Check Java JDK   │
                    │  (Java 17+)      │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │ Verify lib/      │
                    │ dependencies     │
                    │ - jackson-*      │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │ Create out/      │
                    │ directory        │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │ Compile sources  │
                    │ javac -d out ... │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  Copy resources  │
                    │ (services.json)  │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │   Run or Package │
                    │   java -cp ...   │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │    SUCCESS!      │
                    └──────────────────┘

Build Options:
┌─────────────┬─────────────┬─────────────┐
│   Manual    │    Maven    │   Gradle    │
│   javac     │  mvn clean  │gradle build │
│             │  mvn compile│gradle run   │
└─────────────┴─────────────┴─────────────┘
```

---

## Service Lifecycle Flowchart

```
                        ┌──────────────────┐
                        │  Service Created │
                        │  (Constructor)   │
                        └────────┬─────────┘
                                 │
                        ┌────────▼─────────┐
                        │  REGISTERED      │
                        │  (in manager)    │
                        └────────┬─────────┘
                                 │
                        ┌────────▼─────────┐
                        │  configure()     │
                        │  (if configurable)│
                        └────────┬─────────┘
                                 │
                        ┌────────▼─────────┐
                        │    start()       │
                        │  running = true  │
                        └────────┬─────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
          ┌─────────────────┐       ┌──────────────────┐
          │   RUNNING       │       │  Service Logic   │
          │   (active)      │◄──────│  (your code)     │
          └─────────┬───────┘       └──────────────────┘
                    │
                    │ (user command: stop/restart)
                    │
          ┌─────────▼───────┐
          │    stop()       │
          │ running = false │
          └─────────┬───────┘
                    │
                    ├─────────────┐
                    │             │
                    ▼             ▼
          ┌─────────────┐   ┌──────────┐
          │  STOPPED    │   │ restart()│──┐
          │  (inactive) │   └──────────┘  │
          └─────────────┘                 │
                                          │
                    ┌─────────────────────┘
                    │
                    └──────► back to start()

CLI Commands Flow:
┌──────────┐    ┌──────────┐    ┌──────────┐
│  start   │───▶│   stop   │───▶│ restart  │
└──────────┘    └──────────┘    └──────────┘
     │               │                │
     └───────────────┴────────────────┘
                     │
            ┌────────▼────────┐
            │  ServiceManager │
            │  orchestrates   │
            └─────────────────┘
```

---

## Quick Start Guide

### Prerequisites
```bash
# Check Java version (need 17+)
java -version

# Check if you have javac
javac -version
```

### 3-Step Build & Run

```bash
# 1. Compile everything
javac -d out -cp "lib/*" src/com/dvdphobia/blueflex/**/*.java example/*.java

# 2. Copy config files
cp -r src/com/dvdphobia/blueflex/config out/com/dvdphobia/blueflex/

# 3. Run the framework
java -cp "out:lib/*" com.dvdphobia.blueflex.Main
```

---

## Detailed Build Instructions

### Method 1: Manual Build (Current Method)

#### Step 1: Verify Dependencies
```bash
ls -la lib/
# Should see:
# - jackson-annotations-2.15.2.jar
# - jackson-core-2.15.2.jar
# - jackson-databind-2.15.2.jar
```

#### Step 2: Compile Core Framework
```bash
# Create output directory
mkdir -p out

# Compile all source files
javac -d out \
  -cp "lib/*" \
  -sourcepath src \
  src/com/dvdphobia/blueflex/**/*.java
```

#### Step 3: Compile Examples
```bash
# Compile example services
javac -d out \
  -cp "lib/*:out" \
  example/HelloService.java
```

#### Step 4: Copy Configuration Files
```bash
# Copy services.json to output
mkdir -p out/com/dvdphobia/blueflex/config
cp src/com/dvdphobia/blueflex/config/services.json \
   out/com/dvdphobia/blueflex/config/
```

#### Step 5: Run the Framework
```bash
# Run with config from default location
java -cp "out:lib/*" com.dvdphobia.blueflex.Main

# OR run with custom config path
java -cp "out:lib/*" \
  -Dblueflex.config=src/com/dvdphobia/blueflex/config/services.json \
  com.dvdphobia.blueflex.Main
```

---

### Method 2: Using Maven

#### Setup pom.xml
Create `pom.xml` in project root:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.dvdphobia</groupId>
    <artifactId>blueflex-framework</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>BlueFlex Framework</name>
    <description>Lightweight Java service framework</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <jackson.version>2.15.2</jackson.version>
    </properties>

    <dependencies>
        <!-- Jackson for JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>${jackson.version}</version>
        </dependency>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/com/dvdphobia/blueflex/config</directory>
                <targetPath>com/dvdphobia/blueflex/config</targetPath>
            </resource>
        </resources>

        <plugins>
            <!-- Compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Create executable JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.dvdphobia.blueflex.Main</mainClass>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

            <!-- Copy dependencies -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Create fat JAR (all-in-one) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.dvdphobia.blueflex.Main</mainClass>
                                </transformer>
                            </transformers>
                            <finalName>blueflex-framework-${project.version}-standalone</finalName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Maven Build Commands

```bash
# Clean previous builds
mvn clean

# Compile only
mvn compile

# Run without packaging
mvn exec:java -Dexec.mainClass="com.dvdphobia.blueflex.Main"

# Package as JAR
mvn package

# Run the packaged JAR
java -jar target/blueflex-framework-1.0.0.jar

# Run standalone JAR (with dependencies included)
java -jar target/blueflex-framework-1.0.0-standalone.jar

# Install to local Maven repo
mvn install
```

---

### Method 3: Using Gradle

#### Setup build.gradle
Create `build.gradle` in project root:

```gradle
plugins {
    id 'java'
    id 'application'
}

group = 'com.dvdphobia'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.15.2'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.15.2'
}

application {
    mainClass = 'com.dvdphobia.blueflex.Main'
}

sourceSets {
    main {
        java {
            srcDirs = ['src', 'example']
        }
        resources {
            srcDirs = ['src/com/dvdphobia/blueflex/config']
        }
    }
}

jar {
    manifest {
        attributes(
            'Main-Class': 'com.dvdphobia.blueflex.Main'
        )
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

task fatJar(type: Jar) {
    archiveBaseName = 'blueflex-framework-standalone'
    manifest {
        attributes 'Main-Class': 'com.dvdphobia.blueflex.Main'
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    with jar
}
```

#### Gradle Build Commands

```bash
# Build project
gradle build

# Run directly
gradle run

# Create JAR
gradle jar

# Create standalone JAR (with dependencies)
gradle fatJar

# Clean build
gradle clean

# Run JAR
java -jar build/libs/blueflex-framework-1.0.0.jar
```

---

## Development Workflow

### Configuration Flow

```
1. Edit services.json
   ↓
2. Add/remove service entries
   ↓
3. Set enabled: true/false
   ↓
4. Add config parameters
   ↓
5. Rebuild if needed
   ↓
6. Run framework
   ↓
7. Services auto-start based on config
```

### Example services.json Structure

```json
[
  {
    "className": "com.dvdphobia.blueflex.ui.MainUI",
    "enabled": true,
    "config": {
      "width": 800,
      "height": 600,
      "title": "BlueFlex Framework"
    }
  },
  {
    "className": "examples.HelloService",
    "enabled": true,
    "config": {
      "message": "Hello from config!"
    }
  }
]
```

### Creating a New Service

#### Step 1: Create Service Class
```java
package examples;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.service.ConfigurableAppService;
import com.dvdphobia.blueflex.utils.AppLogger;
import java.util.Map;

public class MyCustomService extends AppService implements ConfigurableAppService {
    private final AppLogger log = new AppLogger("MyCustomService");
    private String customConfig;

    public MyCustomService() {
        super("My Custom Service", "Custom", "1.0.0", "Does something useful");
    }

    @Override
    public void configure(Map<String, Object> config) {
        this.customConfig = (String) config.get("customProperty");
        log.info("Configured with: " + customConfig);
    }

    @Override
    public void start() {
        super.start();
        log.info("Service started!");
        // Your initialization logic here
    }

    @Override
    public void stop() {
        super.stop();
        log.info("Service stopped!");
        // Your cleanup logic here
    }
}
```

#### Step 2: Add to services.json
```json
{
  "className": "examples.MyCustomService",
  "enabled": true,
  "config": {
    "customProperty": "myValue"
  }
}
```

#### Step 3: Compile and Run
```bash
# Compile new service
javac -d out -cp "out:lib/*" example/MyCustomService.java

# Run framework
java -cp "out:lib/*" com.dvdphobia.blueflex.Main
```

---

## CLI Commands Reference

Once the framework is running, use these commands:

```
Available Commands:
  start <name>       - Start a service by name
  stop <type>        - Stop a service by type
  restart <name>     - Restart a service
  list (ls)          - List all services
  help (?)           - Show help
  exit (quit)        - Stop all and exit
```

### Example CLI Session
```
BlueFlex> list
=== Running Services ===
  [✓] Main UI v1.0 - Main application UI
  [✓] Hello Service v1.0.0 - Simple hello world service
Total: 2

BlueFlex> restart Hello Service
[INFO] Restarting: Hello Service
[INFO] Stopped: Hello Service
[INFO] Started: Hello Service

BlueFlex> exit
[INFO] Shutting down...
```

---

## Troubleshooting

### Issue: Class not found exception
```
Solution:
1. Check classpath includes lib/* and out/
2. Verify class files are in out/
3. Check package names match directory structure
```

### Issue: services.json not found
```
Solution:
1. Use -Dblueflex.config=<path> to specify config
2. Ensure config directory is copied to out/
3. Check file path is relative to execution directory
```

### Issue: Jackson errors
```
Solution:
1. Verify all 3 Jackson JARs are in lib/
2. Check JAR versions match (2.15.2)
3. Ensure lib/* is in classpath
```

### Issue: Service won't start
```
Solution:
1. Check "enabled": true in services.json
2. Verify class name is correct (case-sensitive)
3. Ensure service has public no-arg constructor
4. Check service extends AppService
```

### Debug Mode
```bash
# Run with verbose logging
java -Dblueflex.debug=true -cp "out:lib/*" com.dvdphobia.blueflex.Main

# Run with specific config
java -Dblueflex.config=/path/to/services.json -cp "out:lib/*" com.dvdphobia.blueflex.Main
```

---

## Project Structure Reference

```
BlueFlex/
├── lib/                          # External dependencies
│   ├── jackson-annotations-2.15.2.jar
│   ├── jackson-core-2.15.2.jar
│   └── jackson-databind-2.15.2.jar
├── src/
│   └── com/dvdphobia/blueflex/
│       ├── Main.java            # Entry point
│       ├── service/             # Service framework
│       │   ├── AppService.java
│       │   ├── ConfigurableAppService.java
│       │   ├── ServiceManager.java
│       │   └── ServiceDebug.java
│       ├── utils/               # Utilities
│       │   ├── AppLogger.java
│       │   ├── CLI.java
│       │   └── Configuration.java
│       ├── ui/                  # UI components
│       │   ├── MainUI.java
│       │   └── UI.java
│       ├── network/             # Networking
│       │   └── NetworkHandler.java
│       ├── API/                 # API layer
│       │   └── Read.java
│       ├── config/              # Configuration
│       │   └── services.json
│       └── core/                # Core framework (future)
├── example/                     # Example services
│   └── HelloService.java
├── out/                         # Compiled classes
├── BUILD_GUIDE.md              # This file
├── MIGRATION_GUIDE.md          # Migration reference
└── FRAMEWORK_ROADMAP.md        # Future plans
```

---

## Next Steps

1. **Get Started**: Follow Quick Start Guide above
2. **Create Services**: Use service template to build custom services
3. **Configure**: Modify services.json for your needs
4. **Expand**: Add more services as needed
5. **Deploy**: Package as JAR for distribution

## Additional Resources

- See `MIGRATION_GUIDE.md` for framework evolution plans
- See `FRAMEWORK_ROADMAP.md` for upcoming features
- Check `example/HelloService.java` for service examples

---

**Happy Building! 🚀**
