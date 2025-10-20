# BlueFlex Framework - PlantUML Diagrams

> **How to use**: Copy any diagram code and paste into [PlantUML Online Editor](http://www.plantuml.com/plantuml/) or use PlantUML locally/in IDE

## Table of Contents
1. [System Architecture](#1-system-architecture)
2. [Component Diagram](#2-component-diagram)
3. [Class Diagram](#3-class-diagram)
4. [Sequence Diagrams](#4-sequence-diagrams)
5. [Activity Diagrams](#5-activity-diagrams)
6. [State Diagram](#6-state-diagram)
7. [Deployment Diagram](#7-deployment-diagram)

---

## 1. System Architecture

```plantuml
@startuml BlueFlex_System_Architecture
!theme plain
skinparam backgroundColor #FFFFFF
skinparam componentStyle rectangle

package "BlueFlex Framework" {
    component [Main] as main
    component [ServiceManager] as manager
    component [CLI] as cli

    package "Services" {
        component [AppService] as appservice
        component [MainUI] as mainui
        component [HelloService] as hello
    }

    package "Utils" {
        component [AppLogger] as logger
        component [Configuration] as config
    }

    package "External Libraries" {
        component [Jackson\nObjectMapper] as jackson
    }
}

database "services.json" as json
actor User

User --> cli : commands
main --> manager : creates
main --> cli : creates
cli --> manager : controls
manager --> appservice : manages
manager --> json : reads
manager --> jackson : uses
manager --> config : loads
manager --> logger : logs

mainui --|> appservice
hello --|> appservice

cli --> logger
manager --> logger

note right of manager
  Loads configuration from JSON
  Manages service lifecycle
  Provides control interface
end note

note right of appservice
  Base class for all services
  Provides lifecycle hooks
  Tracks service state
end note

@enduml
```

---

## 2. Component Diagram

```plantuml
@startuml BlueFlex_Components
!theme plain

package "com.dvdphobia.blueflex" {

    [Main.java] as Main

    package "service" {
        [AppService] as AppService
        [ConfigurableAppService] as Configurable
        [ServiceManager] as Manager
    }

    package "utils" {
        [CLI] as CLI
        [AppLogger] as Logger
        [Configuration] as Config
    }

    package "ui" {
        [MainUI] as UI
    }

    package "config" {
        [services.json] as JSON
    }
}

package "External" {
    [Jackson Library] as Jackson
    [Java Swing] as Swing
}

Main --> Manager : instantiates
Main --> CLI : instantiates

Manager --> AppService : manages
Manager --> Config : loads
Manager --> JSON : reads
Manager --> Jackson : parses with

CLI --> Manager : commands
CLI --> Logger : logs

AppService --> Logger : logs
AppService <|-- UI : extends

Configurable <|.. UI : implements

UI --> Swing : uses

note right of Manager
  Central service orchestrator
  - startAll()
  - stopAll()
  - restart/stop operations
end note

note bottom of CLI
  Command-line interface
  Async command execution
  Command registry pattern
end note

@enduml
```

---

## 3. Class Diagram

```plantuml
@startuml BlueFlex_Class_Diagram
!theme plain
skinparam classAttributeIconSize 0

class Main {
    + {static} main(args: String[])
}

class ServiceManager {
    - log: AppLogger
    - services: List<AppService>
    - {static} DEFAULT_CONFIG_PATH: String
    + startAll()
    + stopAll()
    + startService(service: AppService)
    + stopService(service: AppService)
    + restartServiceByName(name: String)
    + stopServiceByType(type: String)
    + getServices(): List<AppService>
}

abstract class AppService {
    - ServiceName: String
    - ServiceType: String
    - ServiceVersion: String
    - ServiceStartTime: LocalDateTime
    - ServiceDescription: String
    - {static} ServiceInstanceCounter: int
    - isRunning: boolean
    + AppService(name, type, version, desc)
    + start()
    + stop()
    + isRunning(): boolean
    + getServiceName(): String
    + getServiceType(): String
    + getServiceStartTime(): LocalDateTime
    + getServiceInstanceCounter(): int
}

interface ConfigurableAppService {
    + configure(config: Map<String, Object>)
}

class CLI extends AppService {
    - log: AppLogger
    - manager: ServiceManager
    - commands: Map<String, Command>
    - executor: ExecutorService
    - running: boolean
    + CLI(manager: ServiceManager)
    - registerDefaultCommands()
    + register(name, desc, alias, executor)
    - showHelp()
    - stopCLI()
    + runUntilStopped()
    - findCommand(name: String): Command
    - suggestCommand(input: String)
}

class Command {
    + name: String
    + description: String
    + alias: String
    + executor: CommandExecutor
    + Command(name, desc, alias, exec)
}

interface CommandExecutor {
    + execute(args: String[])
}

class AppLogger {
    - tag: String
    - {static} TIME_FORMAT: DateTimeFormatter
    + AppLogger(tag: String)
    - log(level: Level, message: String)
    + info(message: String)
    + warn(message: String)
    + error(message: String)
    + debug(message: String)
}

enum Level {
    INFO
    WARN
    ERROR
    DEBUG
}

class Configuration {
    + className: String
    + enabled: boolean
    + config: Map<String, Object>
    + Configuration()
    + Configuration(className, enabled, config)
}

class MainUI extends AppService implements ConfigurableAppService {
    - log: AppLogger
    - frame: JFrame
    - width: int
    - height: int
    - title: String
    + MainUI()
    + configure(config: Map<String, Object>)
    + start()
    + stop()
}

class HelloService extends AppService {
    - log: AppLogger
    + HelloService()
    + start()
}

Main --> ServiceManager : creates
Main --> CLI : creates
ServiceManager --> AppService : manages
ServiceManager --> Configuration : uses
ServiceManager --> AppLogger : uses
CLI --> ServiceManager : controls
CLI *-- Command : contains
Command --> CommandExecutor : uses
AppLogger --> Level : uses
CLI --> AppLogger : uses

note right of AppService
  Abstract base class
  for all services
  Provides lifecycle
  management
end note

note right of ConfigurableAppService
  Optional interface
  for services that
  need configuration
end note

note bottom of CLI
  Command pattern
  implementation
  with async execution
end note

@enduml
```

---

## 4. Sequence Diagrams

### 4.1 System Startup

```plantuml
@startuml BlueFlex_Startup_Sequence
!theme plain
autonumber

actor User
participant Main
participant ServiceManager
participant ObjectMapper
participant "AppService\n(instances)" as Service
participant CLI

User -> Main : java Main.main()
activate Main

Main -> ServiceManager : new ServiceManager()
activate ServiceManager
ServiceManager --> Main : manager

Main -> ServiceManager : startAll()

ServiceManager -> ServiceManager : getProperty("blueflex.config")
ServiceManager -> ObjectMapper : readValue(services.json)
activate ObjectMapper
ObjectMapper --> ServiceManager : List<Configuration>
deactivate ObjectMapper

loop for each enabled config
    ServiceManager -> ServiceManager : Class.forName(className)
    ServiceManager -> Service : newInstance()
    activate Service

    alt implements ConfigurableAppService
        ServiceManager -> Service : configure(config)
    end

    ServiceManager -> Service : start()
    Service -> Service : running = true
    Service --> ServiceManager : started
end

ServiceManager --> Main : all services started
deactivate ServiceManager

Main -> CLI : new CLI(manager)
activate CLI
CLI --> Main : cli

Main -> CLI : runUntilStopped()
CLI -> User : "CLI started. Type 'help'..."

loop until exit
    User -> CLI : command input
    CLI -> CLI : parse command
    CLI -> ServiceManager : execute operation
    activate ServiceManager
    ServiceManager -> Service : operation
    Service --> ServiceManager : result
    deactivate ServiceManager
    CLI --> User : display result
end

User -> CLI : exit
CLI -> ServiceManager : stopAll()
activate ServiceManager
ServiceManager -> Service : stop()
Service -> Service : running = false
Service --> ServiceManager : stopped
deactivate Service
ServiceManager --> CLI : all stopped
deactivate ServiceManager

CLI --> User : "CLI stopped"
deactivate CLI
deactivate Main

@enduml
```

### 4.2 Service Configuration

```plantuml
@startuml BlueFlex_Configuration_Sequence
!theme plain
autonumber

participant ServiceManager
participant ObjectMapper
participant Configuration
participant "MainUI\n(ConfigurableAppService)" as MainUI
participant AppLogger

ServiceManager -> ObjectMapper : readValue(file, TypeReference)
activate ObjectMapper

ObjectMapper -> Configuration : create instances
activate Configuration
Configuration --> ObjectMapper : List<Configuration>
deactivate Configuration

ObjectMapper --> ServiceManager : configurations
deactivate ObjectMapper

loop for each configuration
    alt enabled == true
        ServiceManager -> ServiceManager : Class.forName(className)
        ServiceManager -> MainUI : newInstance()
        activate MainUI

        MainUI -> MainUI : set default values
        MainUI --> ServiceManager : service instance

        alt config != null
            ServiceManager -> MainUI : configure(config)
            MainUI -> MainUI : extract width
            MainUI -> MainUI : extract height
            MainUI -> MainUI : extract title
            MainUI -> MainUI : validate values
            MainUI --> ServiceManager : configured
        end

        ServiceManager -> MainUI : start()
        MainUI -> MainUI : running = true
        MainUI -> MainUI : create JFrame
        MainUI -> AppLogger : info("MainUI started")
        activate AppLogger
        AppLogger --> MainUI : logged
        deactivate AppLogger

        MainUI --> ServiceManager : started

        ServiceManager -> ServiceManager : add to services list
    else enabled == false
        ServiceManager -> ServiceManager : skip service
    end
end

ServiceManager -> AppLogger : info("All services started")

@enduml
```

### 4.3 CLI Command Processing

```plantuml
@startuml BlueFlex_CLI_Command_Processing
!theme plain
autonumber

actor User
participant CLI
participant Scanner
participant ExecutorService
participant ServiceManager
participant AppService
participant AppLogger

User -> CLI : runUntilStopped()
activate CLI

CLI -> Scanner : create Scanner(System.in)
activate Scanner

loop while running
    CLI -> User : print "> "
    User -> Scanner : input command
    Scanner --> CLI : command string

    CLI -> CLI : parse input
    CLI -> CLI : split by spaces
    CLI -> CLI : cmdName = parts[0]
    CLI -> CLI : args = parts[1..n]

    CLI -> CLI : findCommand(cmdName)

    alt command found
        CLI -> ExecutorService : submit(task)
        activate ExecutorService

        ExecutorService -> ExecutorService : execute in thread

        alt command == "restart"
            ExecutorService -> ServiceManager : restartServiceByName(args[0])
            activate ServiceManager

            ServiceManager -> ServiceManager : find service by name

            alt service found
                ServiceManager -> AppService : stop()
                activate AppService
                AppService -> AppService : running = false
                AppService --> ServiceManager : stopped
                deactivate AppService

                ServiceManager -> AppService : start()
                activate AppService
                AppService -> AppService : running = true
                AppService --> ServiceManager : started
                deactivate AppService

                ServiceManager -> AppLogger : info("Restarted")
            else service not found
                ServiceManager -> AppLogger : warn("Service not found")
            end

            ServiceManager --> ExecutorService : result
            deactivate ServiceManager

        else command == "stop" (exit)
            ExecutorService -> CLI : stopCLI()
            CLI -> CLI : running = false
            CLI -> ServiceManager : stopAll()
            activate ServiceManager
            ServiceManager -> AppService : stop() each
            ServiceManager --> CLI : all stopped
            deactivate ServiceManager
            CLI -> ExecutorService : shutdownNow()
        end

        ExecutorService --> CLI : task complete
        deactivate ExecutorService

    else command not found
        CLI -> CLI : suggestCommand(input)
        CLI -> AppLogger : warn("Unknown command")
        CLI -> AppLogger : info("Did you mean: ...")
    end
end

Scanner --> CLI : close
deactivate Scanner
deactivate CLI

@enduml
```

---

## 5. Activity Diagrams

### 5.1 ServiceManager.startAll()

```plantuml
@startuml BlueFlex_StartAll_Activity
!theme plain
start

:Read DEFAULT_CONFIG_PATH;
note right
  System.getProperty("blueflex.config")
  or "config/services.json"
end note

:Create ObjectMapper;

:Read JSON file;

if (File exists?) then (yes)
    :Parse JSON to List<Configuration>;
else (no)
    :Log error;
    stop
endif

if (Parse successful?) then (yes)
    :Get configurations list;
else (no)
    :Log error;
    :Catch exception;
    stop
endif

:Initialize empty services list;

partition "Process Each Configuration" {
    while (more configs?) is (yes)
        :Get next configuration;

        if (enabled == true?) then (yes)
            :Load class by className;

            if (Class found?) then (yes)
                :Create instance with newInstance();

                if (instanceof AppService?) then (yes)
                    :Add to services list;

                    if (has config && Configurable?) then (yes)
                        :Call configure(config);
                    else (no)
                        :Skip configuration;
                    endif

                    :Call service.start();
                    :Log "Started: serviceName";

                else (no)
                    :Log warning "Not an AppService";
                endif

            else (no)
                :Log error "Class not found";
            endif

        else (no)
            :Skip service;
        endif
    endwhile (no more)
}

:All services started;

stop

@enduml
```

### 5.2 CLI Command Loop

```plantuml
@startuml BlueFlex_CLI_Activity
!theme plain
start

:Create Scanner(System.in);
:Set running = true;
:Log "CLI started";

while (running == true?) is (yes)
    :Print "> " prompt;
    :Read user input;

    if (input null or empty?) then (yes)
        :Continue;
    else (no)
        :Split input by whitespace;
        :Extract command name;
        :Extract arguments;

        :Find command in registry;

        if (Command found?) then (yes)
            :Submit to ExecutorService;

            fork
                :Execute command in thread;

                if (Command type?) then (start)
                    :Start service;
                elseif (stop)
                    :Stop service;
                elseif (restart)
                    :Restart service;
                elseif (list)
                    :List all services;
                elseif (help)
                    :Show help;
                elseif (exit)
                    :Set running = false;
                    :Stop all services;
                    :Shutdown executor;
                endif

            end fork

        else (no)
            :Log "Unknown command";
            :Suggest similar commands;
        endif
    endif
endwhile (no)

:Log "CLI stopped";
:Close scanner;

stop

@enduml
```

---

## 6. State Diagram

```plantuml
@startuml BlueFlex_Service_State
!theme plain

[*] --> Created : Constructor

Created : ServiceName set
Created : ServiceType set
Created : ServiceVersion set
Created : running = false
Created : ServiceInstanceCounter++

Created --> Registered : Added to\nServiceManager

Registered --> Configuring : implements\nConfigurableAppService

Registered --> Ready : No configuration\nneeded

Configuring --> Ready : configure()\ncalled

Configuring : Extract config values
Configuring : Validate parameters
Configuring : Set instance variables

Ready --> Starting : start() called

Starting --> Running : running = true\nServiceStartTime = now()

Running : Service is active
Running : Resources allocated
Running : Processing logic

Running --> Running : Service continues

Running --> Stopping : stop() called\nor error

Stopping --> Stopped : running = false\nServiceInstanceCounter--

Stopped : Service inactive
Stopped : Resources released
Stopped : Can be restarted

Stopped --> Starting : restart command

Stopped --> [*] : System shutdown

note right of Running
  isRunning() returns true
  Service performs its function
  Can receive commands
end note

note right of Stopped
  isRunning() returns false
  Service is idle
  Ready for restart or removal
end note

@enduml
```

---

## 7. Deployment Diagram

```plantuml
@startuml BlueFlex_Deployment
!theme plain

node "Development Machine" {

    node "JVM (Java 17+)" as jvm {

        artifact "blueflex-framework.jar" as jar {
            component [Main]
            component [ServiceManager]
            component [CLI]
            component [AppService]
            component [Utils]
        }

        artifact "jackson-databind.jar" as jackson1
        artifact "jackson-core.jar" as jackson2
        artifact "jackson-annotations.jar" as jackson3

        jar ..> jackson1 : uses
        jar ..> jackson2 : uses
        jar ..> jackson3 : uses
    }

    folder "config/" {
        file "services.json" as config
    }

    folder "lib/" {
        file "*.jar" as libs
    }

    jvm ..> config : reads
    jvm ..> libs : classpath

    note right of config
        Configuration files
        Service definitions
        Runtime parameters
    end note
}

cloud "Potential Deployment" {
    node "Production Server" {
        node "JVM" as jvm2 {
            artifact "standalone.jar" as standalone
        }

        database "External\nConfiguration" as extconfig

        jvm2 ..> extconfig : reads
    }

    note right of standalone
        Fat JAR with all
        dependencies included
    end note
}

@enduml
```

---

## 8. Package Diagram

```plantuml
@startuml BlueFlex_Package_Diagram
!theme plain

package "com.dvdphobia.blueflex" {

    class Main

    package "service" {
        abstract class AppService
        interface ConfigurableAppService
        class ServiceManager
    }

    package "utils" {
        class CLI
        class AppLogger
        class Configuration
        enum Level
    }

    package "ui" {
        class MainUI
    }

    package "config" {
        file services.json
    }

    package "network" {
        class NetworkHandler
    }

    package "API" {
        class Read
    }

    package "core" {
        note "Future expansion\nFramework core features" as N1
    }
}

package "External Libraries" {
    package "com.fasterxml.jackson" {
        class ObjectMapper
        class TypeReference
    }

    package "javax.swing" {
        class JFrame
    }
}

Main --> service
Main --> utils
service --> utils
utils --> service
ui --> service
ui --> utils
ui --> javax.swing
service --> com.fasterxml.jackson
service --> config

@enduml
```

---

## How to Use PlantUML Diagrams

### Option 1: Online Editor
1. Go to http://www.plantuml.com/plantuml/
2. Copy any diagram code above
3. Paste into the editor
4. Export as PNG, SVG, or other formats

### Option 2: VS Code
1. Install "PlantUML" extension
2. Install Java (required for PlantUML)
3. Create `.puml` file with diagram code
4. Press `Alt+D` to preview

### Option 3: IntelliJ IDEA
1. Install "PlantUML integration" plugin
2. Create `.puml` file
3. Right-click > "Show PlantUML Diagram"

### Option 4: Command Line
```bash
# Install PlantUML
brew install plantuml  # macOS
# or download plantuml.jar

# Generate PNG
plantuml diagram.puml

# Generate SVG
plantuml -tsvg diagram.puml
```

### Option 5: Convert to draw.io
1. Generate SVG from PlantUML
2. Open draw.io
3. File > Import > Select SVG file

---

## Diagram Types Quick Reference

- **System Architecture**: High-level system view
- **Component Diagram**: Module relationships
- **Class Diagram**: Object-oriented structure
- **Sequence Diagrams**: Interaction flows
- **Activity Diagrams**: Process flows
- **State Diagram**: Service lifecycle states
- **Deployment Diagram**: Physical architecture
- **Package Diagram**: Code organization

All diagrams are in PlantUML format and can be exported to any image format!
