# BlueFlex Framework - Mermaid Flowcharts

> **How to use**: Copy any diagram code and paste into [Mermaid Live Editor](https://mermaid.live/) or any Mermaid-compatible tool (GitHub, GitLab, Notion, Obsidian, etc.)

## Table of Contents
1. [Complete System Architecture](#1-complete-system-architecture)
2. [Application Startup Flow](#2-application-startup-flow)
3. [Service Manager Flow](#3-service-manager-flow)
4. [CLI Command Processing](#4-cli-command-processing)
5. [Service Lifecycle](#5-service-lifecycle)
6. [Configuration Loading](#6-configuration-loading)
7. [Class Diagram](#7-class-diagram)
8. [Sequence Diagrams](#8-sequence-diagrams)

---

## 1. Complete System Architecture

```mermaid
graph TB
    Start([JVM Start]) --> Main[Main.java]
    Main --> SM[ServiceManager]
    Main --> CLI[CLI]

    SM --> ConfigLoader[Load services.json]
    ConfigLoader --> Jackson[ObjectMapper<br/>Jackson]
    Jackson --> ConfigList[List of Configuration]

    ConfigList --> Loop{For Each<br/>Config}
    Loop -->|enabled=true| CreateService[Create Service<br/>Instance]
    Loop -->|enabled=false| Skip[Skip Service]

    CreateService --> CheckConfig{Implements<br/>Configurable?}
    CheckConfig -->|Yes| Configure[configure Map]
    CheckConfig -->|No| StartService
    Configure --> StartService[service.start]

    StartService --> Register[Add to<br/>services List]
    Register --> NextService{More<br/>Services?}
    NextService -->|Yes| Loop
    NextService -->|No| Ready[System Ready]

    Ready --> CLI
    CLI --> UserInput[Wait for<br/>User Commands]

    UserInput --> Commands{Command<br/>Type}
    Commands -->|start| CmdStart[Start Service]
    Commands -->|stop| CmdStop[Stop Service]
    Commands -->|restart| CmdRestart[Restart Service]
    Commands -->|list| CmdList[List Services]
    Commands -->|help| CmdHelp[Show Help]
    Commands -->|exit| Shutdown[Shutdown All]

    CmdStart --> SM
    CmdStop --> SM
    CmdRestart --> SM
    CmdList --> SM
    Shutdown --> StopAll[manager.stopAll]
    StopAll --> Exit([System Exit])

    style Start fill:#90EE90
    style Main fill:#87CEEB
    style SM fill:#FFB6C1
    style CLI fill:#DDA0DD
    style Ready fill:#F0E68C
    style Exit fill:#FFA07A
```

---

## 2. Application Startup Flow

```mermaid
flowchart TD
    A([main String args]) --> B[Create ServiceManager]
    B --> C[manager.startAll]

    C --> D[Read DEFAULT_CONFIG_PATH]
    D --> E{Config Property<br/>Set?}
    E -->|Yes| F[Use -Dblueflex.config]
    E -->|No| G[Use config/services.json]

    F --> H[ObjectMapper.readValue]
    G --> H

    H --> I{Parse<br/>Success?}
    I -->|No| J[Log Error]
    I -->|Yes| K[Get List Configuration]

    J --> End1[Continue with<br/>No Services]

    K --> L{For Each<br/>Configuration}
    L --> M{enabled<br/>== true?}
    M -->|No| N[Skip to Next]
    M -->|Yes| O[Class.forName className]

    O --> P{Class<br/>Found?}
    P -->|No| Q[Log Error<br/>Continue]
    P -->|Yes| R[newInstance]

    R --> S{instanceof<br/>AppService?}
    S -->|No| T[Log Warning<br/>Continue]
    S -->|Yes| U[Add to services List]

    U --> V{instanceof<br/>Configurable?}
    V -->|Yes| W[service.configure config]
    V -->|No| X[Skip Config]

    W --> Y[service.start]
    X --> Y
    Y --> Z[Log: Started ServiceName]

    Z --> AA{More<br/>Configs?}
    AA -->|Yes| L
    AA -->|No| BB[Create CLI manager]

    N --> AA
    Q --> AA
    T --> AA

    BB --> CC[cli.runUntilStopped]
    CC --> DD[Scanner Loop<br/>Wait for Input]

    End1 --> BB

    style A fill:#90EE90
    style CC fill:#87CEEB
    style DD fill:#DDA0DD
```

---

## 3. Service Manager Flow

```mermaid
flowchart TD
    SM[ServiceManager Instance] --> Props[Properties]
    Props --> Prop1[log: AppLogger]
    Props --> Prop2[services: List AppService]
    Props --> Prop3[DEFAULT_CONFIG_PATH]

    SM --> Methods[Methods]

    Methods --> StartAll[startAll]
    Methods --> StopAll[stopAll]
    Methods --> StartService[startService AppService]
    Methods --> StopService[stopService AppService]
    Methods --> RestartByName[restartServiceByName String]
    Methods --> StopByType[stopServiceByType String]
    Methods --> GetServices[getServices]

    StartAll --> SA1[Load JSON Config]
    SA1 --> SA2[Parse with Jackson]
    SA2 --> SA3[Loop Configurations]
    SA3 --> SA4[Instantiate Services]
    SA4 --> SA5[Configure if needed]
    SA5 --> SA6[Call start on each]
    SA6 --> SA7[Log success/errors]

    StopAll --> ST1[Log: Stopping all...]
    ST1 --> ST2[Loop services list]
    ST2 --> ST3[Call stopService]
    ST3 --> ST4[Log: All stopped]

    StartService --> SS1{isRunning?}
    SS1 -->|No| SS2[service.start]
    SS1 -->|Yes| SS3[Log: Already running]
    SS2 --> SS4[Log: Started]

    StopService --> SP1{isRunning?}
    SP1 -->|Yes| SP2[service.stop]
    SP1 -->|No| SP3[Log: Already stopped]
    SP2 --> SP4[Log: Stopped]

    RestartByName --> RN1[Find by name]
    RN1 --> RN2{Found?}
    RN2 -->|Yes| RN3[stopService]
    RN2 -->|No| RN4[Log: Not found]
    RN3 --> RN5[startService]
    RN5 --> RN6[Log: Restarted]

    StopByType --> SBT1[Find by type]
    SBT1 --> SBT2{Found?}
    SBT2 -->|Yes| SBT3[stopService]
    SBT2 -->|No| SBT4[Log: Not found]

    GetServices --> GS1[Return services list]

    style SM fill:#FFB6C1
    style Methods fill:#F0E68C
    style Props fill:#E6E6FA
```

---

## 4. CLI Command Processing

```mermaid
flowchart TD
    CLI[CLI Running] --> Wait[Scanner.readLine]
    Wait --> Input{Input<br/>Received?}
    Input -->|null/empty| Wait
    Input -->|has text| Parse[Split by whitespace]

    Parse --> Extract[cmdName = parts 0]
    Extract --> Args[args = parts 1..n]

    Args --> Find[findCommand cmdName]
    Find --> Match{Command<br/>Found?}

    Match -->|No| Suggest[suggestCommand]
    Suggest --> ShowSuggestions[Show Similar Commands]
    ShowSuggestions --> Wait

    Match -->|Yes| Async[executor.submit]
    Async --> TryCatch{Try Execute}

    TryCatch -->|Error| LogError[Log: Command failed]
    TryCatch -->|Success| ExecCmd[cmd.executor.execute args]

    ExecCmd --> CmdType{Command<br/>Type}

    CmdType -->|help/h| Help[showHelp]
    CmdType -->|stop/exit| Stop[stopCLI]
    CmdType -->|restart/r| Restart[manager.restartServiceByName]
    CmdType -->|stop-type/st| StopType[manager.stopServiceByType]
    CmdType -->|list/ls| List[List Services]

    Help --> ShowCommands[Display all commands]
    ShowCommands --> Wait

    Stop --> StopFlag[running = false]
    StopFlag --> StopMgr[manager.stopAll]
    StopMgr --> ShutdownExec[executor.shutdownNow]
    ShutdownExec --> End([Exit Loop])

    Restart --> CheckArgs1{args.length<br/>> 0?}
    CheckArgs1 -->|No| WarnRestart[Log: Usage restart name]
    CheckArgs1 -->|Yes| DoRestart[Call restartServiceByName]
    WarnRestart --> Wait
    DoRestart --> Wait

    StopType --> CheckArgs2{args.length<br/>> 0?}
    CheckArgs2 -->|No| WarnStopType[Log: Usage stop-type type]
    CheckArgs2 -->|Yes| DoStopType[Call stopServiceByType]
    WarnStopType --> Wait
    DoStopType --> Wait

    List --> NotImpl[Log: Not implemented]
    NotImpl --> Wait

    LogError --> Wait

    style CLI fill:#DDA0DD
    style Wait fill:#E6E6FA
    style End fill:#FFA07A
    style CmdType fill:#F0E68C
```

---

## 5. Service Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Created: Constructor Called

    Created --> Registered: Added to ServiceManager

    Registered --> Configuring: implements Configurable
    Registered --> Starting: No Configuration

    Configuring --> Configured: configure(Map) called
    Configured --> Starting: Configuration Complete

    Starting --> Running: start() called<br/>running = true<br/>ServiceStartTime = now()

    Running --> Running: Service Active

    Running --> Stopping: stop() called

    Stopping --> Stopped: running = false<br/>ServiceInstanceCounter--

    Stopped --> Restarting: restart command
    Stopped --> [*]: Shutdown

    Restarting --> Starting: Restart Sequence

    note right of Created
        ServiceName
        ServiceType
        ServiceVersion
        ServiceDescription
        running = false
    end note

    note right of Running
        isRunning() returns true
        Service logic executes
        Resources allocated
    end note

    note right of Stopped
        isRunning() returns false
        Resources released
        Ready for restart or removal
    end note
```

---

## 6. Configuration Loading

```mermaid
flowchart LR
    A[services.json File] --> B[ServiceManager.startAll]

    B --> C{System Property<br/>blueflex.config?}
    C -->|Set| D[Use Custom Path]
    C -->|Not Set| E[Use config/services.json]

    D --> F[File Object]
    E --> F

    F --> G[ObjectMapper]
    G --> H[readValue with TypeReference]

    H --> I{Valid<br/>JSON?}
    I -->|No| J[Throw Exception]
    I -->|Yes| K[List Configuration]

    J --> L[Catch Block]
    L --> M[Log Error Message]
    M --> N[Return Empty List]

    K --> O[For Each Configuration]
    O --> P[Configuration Object]

    P --> Q[className: String]
    P --> R[enabled: boolean]
    P --> S[config: Map String,Object]

    Q --> T{Load Class}
    T -->|Success| U[Create Instance]
    T -->|Fail| V[Log Error<br/>Continue Loop]

    U --> W{instanceof<br/>ConfigurableAppService?}
    W -->|Yes| X[Call configure config]
    W -->|No| Y[Skip Configuration]

    X --> Z[Service Configured]
    Y --> Z
    Z --> AA[Call service.start]

    V --> AB{More<br/>Configs?}
    AA --> AB
    AB -->|Yes| O
    AB -->|No| AC[All Services Started]

    style A fill:#90EE90
    style K fill:#87CEEB
    style P fill:#FFB6C1
    style AC fill:#F0E68C
```

---

## 7. Class Diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args)$
    }

    class ServiceManager {
        -AppLogger log
        -List~AppService~ services
        -String DEFAULT_CONFIG_PATH$
        +startAll()
        +stopAll()
        +startService(AppService)
        +stopService(AppService)
        +restartServiceByName(String)
        +stopServiceByType(String)
        +getServices() List~AppService~
    }

    class AppService {
        -String ServiceName
        -String ServiceType
        -String ServiceVersion
        -LocalDateTime ServiceStartTime
        -String ServiceDescription
        -int ServiceInstanceCounter$
        -boolean isRunning
        +AppService(String, String, String, String)
        +start()
        +stop()
        +isRunning() boolean
        +getServiceName() String
        +getServiceType() String
        +getServiceStartTime() LocalDateTime
        +getServiceInstanceCounter() int
    }

    class ConfigurableAppService {
        <<interface>>
        +configure(Map~String,Object~)
    }

    class CLI {
        -AppLogger log
        -ServiceManager manager
        -Map~String,Command~ commands
        -ExecutorService executor
        -boolean running
        +CLI(ServiceManager)
        -registerDefaultCommands()
        +register(String, String, String, CommandExecutor)
        -showHelp()
        -stopCLI()
        +runUntilStopped()
        -findCommand(String) Command
        -suggestCommand(String)
    }

    class Command {
        +String name
        +String description
        +String alias
        +CommandExecutor executor
        +Command(String, String, String, CommandExecutor)
    }

    class CommandExecutor {
        <<interface>>
        +execute(String[])
    }

    class AppLogger {
        -String tag
        -DateTimeFormatter TIME_FORMAT$
        +AppLogger(String)
        -log(Level, String)
        +info(String)
        +warn(String)
        +error(String)
        +debug(String)
    }

    class Level {
        <<enumeration>>
        INFO
        WARN
        ERROR
        DEBUG
    }

    class Configuration {
        +String className
        +boolean enabled
        +Map~String,Object~ config
        +Configuration()
        +Configuration(String, boolean, Map)
    }

    class MainUI {
        -AppLogger log
        -JFrame frame
        -int width
        -int height
        -String title
        +MainUI()
        +configure(Map~String,Object~)
        +start()
        +stop()
    }

    class HelloService {
        -AppLogger log
        +HelloService()
        +start()
    }

    Main --> ServiceManager: creates
    Main --> CLI: creates
    ServiceManager --> AppService: manages *
    ServiceManager --> Configuration: reads
    ServiceManager --> AppLogger: uses
    CLI --> ServiceManager: controls
    CLI --> Command: contains *
    CLI --> AppLogger: uses
    CLI ..> AppService: extends
    Command --> CommandExecutor: has
    AppLogger --> Level: uses
    MainUI --|> AppService: extends
    MainUI ..|> ConfigurableAppService: implements
    HelloService --|> AppService: extends

```

---

## 8. Sequence Diagrams

### 8.1 System Startup Sequence

```mermaid
sequenceDiagram
    actor User
    participant Main
    participant ServiceManager
    participant ObjectMapper
    participant AppService
    participant CLI

    User->>Main: java Main
    activate Main
    Main->>ServiceManager: new ServiceManager()
    activate ServiceManager
    ServiceManager-->>Main: manager

    Main->>ServiceManager: startAll()
    ServiceManager->>ObjectMapper: readValue(services.json)
    activate ObjectMapper
    ObjectMapper-->>ServiceManager: List<Configuration>
    deactivate ObjectMapper

    loop For each enabled config
        ServiceManager->>ServiceManager: Class.forName(className)
        ServiceManager->>AppService: newInstance()
        activate AppService
        AppService-->>ServiceManager: service

        alt implements ConfigurableAppService
            ServiceManager->>AppService: configure(config)
        end

        ServiceManager->>AppService: start()
        AppService->>AppService: running = true
        AppService-->>ServiceManager: started
    end

    ServiceManager-->>Main: all started
    deactivate ServiceManager

    Main->>CLI: new CLI(manager)
    activate CLI
    CLI-->>Main: cli

    Main->>CLI: runUntilStopped()
    CLI->>User: CLI started...
    deactivate Main

    loop Until exit
        User->>CLI: command input
        CLI->>ServiceManager: execute command
        activate ServiceManager
        ServiceManager->>AppService: operation
        AppService-->>ServiceManager: result
        deactivate ServiceManager
        CLI->>User: response
    end

    User->>CLI: exit
    CLI->>ServiceManager: stopAll()
    activate ServiceManager
    ServiceManager->>AppService: stop()
    deactivate AppService
    ServiceManager-->>CLI: stopped
    deactivate ServiceManager
    CLI->>User: CLI stopped
    deactivate CLI
```

### 8.2 Service Restart Sequence

```mermaid
sequenceDiagram
    actor User
    participant CLI
    participant ServiceManager
    participant AppService
    participant AppLogger

    User->>CLI: restart HelloService
    activate CLI

    CLI->>CLI: parse command
    CLI->>CLI: findCommand("restart")
    CLI->>ServiceManager: restartServiceByName("HelloService")
    activate ServiceManager

    ServiceManager->>ServiceManager: find service by name

    alt Service Found
        ServiceManager->>AppLogger: log("Restarting...")
        ServiceManager->>AppService: isRunning()?
        activate AppService

        alt If Running
            ServiceManager->>AppService: stop()
            AppService->>AppService: running = false
            AppService->>AppLogger: log("Stopped")
            AppService-->>ServiceManager: stopped
        end

        ServiceManager->>AppService: start()
        AppService->>AppService: running = true
        AppService->>AppLogger: log("Started")
        AppService-->>ServiceManager: started
        deactivate AppService

        ServiceManager->>AppLogger: log("Restarted")
    else Service Not Found
        ServiceManager->>AppLogger: log("Service not found")
    end

    ServiceManager-->>CLI: result
    deactivate ServiceManager
    CLI->>User: display result
    deactivate CLI
```

### 8.3 Configuration Loading Sequence

```mermaid
sequenceDiagram
    participant ServiceManager
    participant System
    participant File
    participant ObjectMapper
    participant Configuration
    participant AppService
    participant ConfigurableAppService

    ServiceManager->>System: getProperty("blueflex.config")
    alt Property exists
        System-->>ServiceManager: custom path
    else Default
        System-->>ServiceManager: null
        ServiceManager->>ServiceManager: use "config/services.json"
    end

    ServiceManager->>File: new File(path)
    activate File
    File-->>ServiceManager: file

    ServiceManager->>ObjectMapper: readValue(file, TypeReference)
    activate ObjectMapper
    ObjectMapper->>File: read JSON
    File-->>ObjectMapper: JSON content
    ObjectMapper->>Configuration: parse each object
    activate Configuration
    Configuration-->>ObjectMapper: Configuration instance
    deactivate Configuration
    ObjectMapper-->>ServiceManager: List<Configuration>
    deactivate ObjectMapper
    deactivate File

    loop For each Configuration
        alt enabled == true
            ServiceManager->>ServiceManager: Class.forName(className)
            ServiceManager->>AppService: newInstance()
            activate AppService
            AppService-->>ServiceManager: service

            alt config != null && instanceof ConfigurableAppService
                ServiceManager->>ConfigurableAppService: configure(config)
                activate ConfigurableAppService
                ConfigurableAppService->>ConfigurableAppService: extract config values
                ConfigurableAppService->>ConfigurableAppService: set instance variables
                ConfigurableAppService-->>ServiceManager: configured
                deactivate ConfigurableAppService
            end

            ServiceManager->>AppService: start()
            AppService->>AppService: running = true
            AppService-->>ServiceManager: started
            deactivate AppService
        else enabled == false
            ServiceManager->>ServiceManager: skip
        end
    end
```

---

## How to Export These Diagrams

### Option 1: Mermaid Live Editor
1. Go to https://mermaid.live/
2. Copy any diagram code above
3. Paste into the editor
4. Export as:
   - PNG
   - SVG
   - Markdown
   - Or edit and save as .mmd file

### Option 2: GitHub/GitLab
- These diagrams render automatically in README.md files
- Just paste the code blocks

### Option 3: VS Code
- Install "Markdown Preview Mermaid Support" extension
- View diagrams in preview mode

### Option 4: Obsidian/Notion
- Paste code blocks directly (they support Mermaid)

### Option 5: draw.io Integration
1. Install draw.io desktop or use https://app.diagrams.net/
2. Use "Insert > Advanced > Mermaid" option
3. Paste the code
4. It will render as a diagram

---

## Quick Reference

- **System Flow**: Diagram #1 and #2
- **Service Management**: Diagram #3 and #5
- **CLI Processing**: Diagram #4
- **Configuration**: Diagram #6
- **Class Structure**: Diagram #7
- **Interactions**: Diagram #8

All diagrams are interconnected and represent different views of your BlueFlex framework!
