# BlueFlex Structure Migration Guide

## Current Structure (Keep As-Is)
```
BlueFlex/
├── lib/                                    # External JARs
│   ├── jackson-annotations-2.15.2.jar
│   ├── jackson-core-2.15.2.jar
│   └── jackson-databind-2.15.2.jar
├── src/
│   └── com/dvdphobia/blueflex/
│       ├── Main.java                       # Entry point
│       ├── service/                        # ✓ Keep - Core services
│       │   ├── AppService.java
│       │   ├── ConfigurableAppService.java
│       │   ├── ServiceManager.java
│       │   └── ServiceDebug.java
│       ├── utils/                          # ✓ Keep - Utilities
│       │   ├── AppLogger.java
│       │   ├── CLI.java
│       │   └── Configuration.java
│       ├── ui/                             # ✓ Keep - UI components
│       │   ├── MainUI.java
│       │   └── UI.java
│       ├── network/                        # ✓ Keep - Networking
│       │   └── NetworkHandler.java
│       ├── API/                            # ✓ Keep - API layer
│       │   └── Read.java
│       └── worker/                         # ✓ Keep - Workers
│           └── services.json
└── out/                                    # Build output
```

## Proposed Evolution (Add New Folders)

```
BlueFlex/
├── lib/                                    # Keep existing JARs
├── src/
│   └── com/dvdphobia/blueflex/
│       ├── Main.java                       # ✓ Keep
│       │
│       ├── core/                           # NEW - Framework core
│       │   ├── lifecycle/
│       │   │   ├── ServiceLifecycle.java
│       │   │   ├── LifecycleManager.java
│       │   │   └── ShutdownHook.java
│       │   ├── injection/
│       │   │   ├── Injector.java
│       │   │   ├── ServiceProvider.java
│       │   │   └── annotations/
│       │   │       ├── Inject.java
│       │   │       └── Component.java
│       │   ├── events/
│       │   │   ├── EventBus.java
│       │   │   ├── Event.java
│       │   │   └── EventListener.java
│       │   └── registry/
│       │       ├── ServiceRegistry.java
│       │       └── ServiceMetadata.java
│       │
│       ├── service/                        # ✓ Keep + Enhance
│       │   ├── AppService.java             # Existing
│       │   ├── ConfigurableAppService.java # Existing
│       │   ├── ServiceManager.java         # Existing (improve)
│       │   ├── ServiceDebug.java           # Existing
│       │   └── annotations/                # NEW
│       │       ├── Service.java
│       │       ├── AutoStart.java
│       │       └── DependsOn.java
│       │
│       ├── config/                         # NEW - Enhanced config
│       │   ├── ConfigLoader.java
│       │   ├── ConfigValidator.java
│       │   ├── EnvironmentConfig.java
│       │   ├── YamlConfigLoader.java
│       │   └── PropertiesConfigLoader.java
│       │
│       ├── utils/                          # ✓ Keep + Enhance
│       │   ├── AppLogger.java              # Existing
│       │   ├── CLI.java                    # Existing (improve)
│       │   ├── Configuration.java          # Existing
│       │   └── StringUtils.java            # NEW
│       │
│       ├── ui/                             # ✓ Keep
│       │   ├── MainUI.java
│       │   └── UI.java
│       │
│       ├── network/                        # ✓ Keep + Enhance
│       │   ├── NetworkHandler.java         # Existing (implement)
│       │   ├── http/                       # NEW
│       │   │   ├── HttpServer.java
│       │   │   ├── HttpClient.java
│       │   │   └── Request.java
│       │   └── socket/                     # NEW
│       │       ├── SocketServer.java
│       │       └── SocketClient.java
│       │
│       ├── API/                            # ✓ Keep + Enhance
│       │   ├── Read.java                   # Existing
│       │   └── rest/                       # NEW
│       │       ├── RestController.java
│       │       ├── annotations/
│       │       │   ├── RestEndpoint.java
│       │       │   ├── GET.java
│       │       │   └── POST.java
│       │       └── Response.java
│       │
│       ├── worker/                         # ✓ Keep + Enhance
│       │   ├── services.json               # Existing
│       │   ├── scheduler/                  # NEW
│       │   │   ├── TaskScheduler.java
│       │   │   ├── ScheduledTask.java
│       │   │   └── CronExpression.java
│       │   └── jobs/                       # NEW
│       │       ├── JobQueue.java
│       │       └── BackgroundJob.java
│       │
│       ├── plugin/                         # NEW - Plugin system
│       │   ├── Plugin.java
│       │   ├── PluginLoader.java
│       │   ├── PluginManager.java
│       │   └── PluginDescriptor.java
│       │
│       ├── monitoring/                     # NEW - Monitoring
│       │   ├── metrics/
│       │   │   ├── MetricsCollector.java
│       │   │   ├── Counter.java
│       │   │   └── Gauge.java
│       │   └── health/
│       │       ├── HealthCheck.java
│       │       └── HealthEndpoint.java
│       │
│       ├── security/                       # NEW - Security
│       │   ├── auth/
│       │   │   ├── AuthProvider.java
│       │   │   └── JwtAuth.java
│       │   └── encryption/
│       │       └── Encryptor.java
│       │
│       └── data/                           # NEW - Data access
│           ├── ConnectionPool.java
│           ├── Database.java
│           └── repository/
│               └── Repository.java
│
├── plugins/                                # NEW - External plugins
│   └── README.md
│
├── config/                                 # NEW - Configuration files
│   ├── application.json
│   ├── application-dev.json
│   ├── application-prod.json
│   └── services.json                       # Move from worker/
│
├── examples/                               # NEW - Example projects
│   ├── hello-world/
│   ├── rest-api/
│   └── background-worker/
│
├── docs/                                   # NEW - Documentation
│   ├── getting-started.md
│   ├── api-reference.md
│   └── architecture.md
│
├── tests/                                  # NEW - Test suite
│   └── com/dvdphobia/blueflex/
│       ├── service/
│       ├── core/
│       └── utils/
│
└── out/                                    # Keep build output
```

---

## Step-by-Step Migration Plan

### Phase 1: Setup (No Breaking Changes)
```bash
# Create new directories alongside existing code
mkdir -p src/com/dvdphobia/blueflex/core/{lifecycle,injection,events,registry}
mkdir -p src/com/dvdphobia/blueflex/config
mkdir -p src/com/dvdphobia/blueflex/plugin
mkdir -p src/com/dvdphobia/blueflex/monitoring/{metrics,health}
mkdir -p config
mkdir -p plugins
mkdir -p examples
mkdir -p docs
mkdir -p tests/com/dvdphobia/blueflex
```

### Phase 2: Improve Existing Code (Keep Backward Compatible)

#### 2.1 Fix ServiceManager.java
```java
// Current (line 15):
private static final String DEFAULT_CONFIG_PATH = "src/com/dvdphobia/blueflex/worker/services.json";

// Improve to:
private static final String DEFAULT_CONFIG_PATH =
    System.getProperty("blueflex.config", "config/services.json");
```

#### 2.2 Enhance AppService.java
```java
// Add to existing AppService class:
public String getServiceVersion() { return ServiceVersion; }
public String getServiceDescription() { return ServiceDescription; }

// Fix counter thread safety:
private static final AtomicInteger ServiceInstanceCounter = new AtomicInteger(0);
```

#### 2.3 Complete CLI.java list command
```java
// Replace line 75-77:
register("list", "List all running services", "ls", args -> {
    log.info("\n=== Running Services ===");
    for (AppService service : manager.getServices()) {
        log.info(String.format("  [%s] %s v%s - %s",
            service.isRunning() ? "✓" : "✗",
            service.getServiceName(),
            service.getServiceVersion(),
            service.getServiceDescription()
        ));
    }
    log.info("Total: " + manager.getServices().size());
});
```

### Phase 3: Add New Core Components (Non-Breaking)

#### 3.1 Create core/lifecycle/ServiceLifecycle.java
```java
package com.dvdphobia.blueflex.core.lifecycle;

public interface ServiceLifecycle {
    default void onInit() {}
    default void onStart() {}
    default void onStop() {}
    default void onDestroy() {}
}
```

#### 3.2 Create core/events/EventBus.java
```java
package com.dvdphobia.blueflex.core.events;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private static final EventBus instance = new EventBus();
    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    public static EventBus getInstance() { return instance; }

    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <T> void publish(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
}
```

#### 3.3 Create config/ConfigLoader.java
```java
package com.dvdphobia.blueflex.config;

import com.dvdphobia.blueflex.utils.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

public class ConfigLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Configuration> load(String path) throws Exception {
        // Support environment variable override
        String configPath = System.getenv("BLUEFLEX_CONFIG");
        if (configPath == null) configPath = path;

        return mapper.readValue(new File(configPath),
            new TypeReference<List<Configuration>>() {});
    }
}
```

### Phase 4: Create Example Projects

#### 4.1 examples/hello-world/HelloService.java
```java
package examples;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.utils.AppLogger;

public class HelloService extends AppService {
    private final AppLogger log = new AppLogger("HelloService");

    public HelloService() {
        super("Hello Service", "Example", "1.0.0", "Simple hello world service");
    }

    @Override
    public void start() {
        super.start();
        log.info("Hello from BlueFlex Framework!");
    }
}
```

### Phase 5: Move Configuration Files

```bash
# Move services.json to config/ (keep copy for backward compat)
cp src/com/dvdphobia/blueflex/worker/services.json config/services.json
```

Update `config/services.json`:
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
    "enabled": false,
    "config": {}
  }
]
```

---

## Build System Setup

### Option 1: Maven (pom.xml)
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

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <jackson.version>2.15.2</jackson.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.dvdphobia.blueflex.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Option 2: Gradle (build.gradle)
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
}

application {
    mainClass = 'com.dvdphobia.blueflex.Main'
}

jar {
    manifest {
        attributes 'Main-Class': 'com.dvdphobia.blueflex.Main'
    }
}
```

---

## Quick Start Commands

### Using Current Structure
```bash
# Compile
javac -d out -cp "lib/*" src/com/dvdphobia/blueflex/**/*.java

# Run
java -cp "out:lib/*" com.dvdphobia.blueflex.Main
```

### Using Maven (after setup)
```bash
mvn clean compile
mvn exec:java
mvn package  # Creates JAR in target/
```

### Using Gradle (after setup)
```bash
gradle build
gradle run
java -jar build/libs/blueflex-framework-1.0.0.jar
```

---

## Backward Compatibility Rules

1. **Never break existing code** - All current services must continue working
2. **Add, don't replace** - Add new features alongside old ones
3. **Deprecate gradually** - Mark old APIs as @Deprecated before removing
4. **Provide migration helpers** - Tools to upgrade old code
5. **Keep old config paths** - Support both old and new paths

---

## Summary

✅ **Keep**: All your current code and structure
📁 **Add**: New folders for framework features
🔧 **Fix**: Bugs in existing files (non-breaking)
📦 **Optional**: Maven/Gradle for better dependency management
🎯 **Goal**: Evolve gradually without disrupting existing work
