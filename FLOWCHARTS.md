# BlueFlex Framework - Visual Flowcharts

## Table of Contents
1. [System Initialization Flow](#system-initialization-flow)
2. [Service Loading Flow](#service-loading-flow)
3. [CLI Command Processing Flow](#cli-command-processing-flow)
4. [Service Configuration Flow](#service-configuration-flow)
5. [Error Handling Flow](#error-handling-flow)
6. [Complete Request-Response Flow](#complete-request-response-flow)

---

## System Initialization Flow

```
                    ╔═══════════════════════╗
                    ║  JVM Starts           ║
                    ║  Main.main(String[])  ║
                    ╚═══════════╤═══════════╝
                                │
                                │ 1. Create instances
                                ▼
                    ┌───────────────────────┐
                    │  AppLogger initialized│
                    │  log = new AppLogger()│
                    └───────────┬───────────┘
                                │
                                │ 2. Create manager
                                ▼
                    ┌───────────────────────┐
                    │  ServiceManager       │
                    │  manager = new SM()   │
                    └───────────┬───────────┘
                                │
                                │ 3. Load config
                                ▼
                    ┌───────────────────────┐
                    │  Read services.json   │
                    │  Path resolution:     │
                    │  1. -Dblueflex.config │
                    │  2. config/services.json│
                    └───────────┬───────────┘
                                │
                                │ 4. Parse JSON
                                ▼
                    ┌───────────────────────┐
                    │  ObjectMapper         │
                    │  Parse List<Config>   │
                    └───────────┬───────────┘
                                │
                ┌───────────────┴───────────────┐
                │                               │
                ▼                               ▼
    ┌────────────────────┐        ┌──────────────────────┐
    │  For each config:  │        │  Validation:         │
    │  - className       │        │  - enabled = true?   │
    │  - enabled         │        │  - class exists?     │
    │  - config map      │        │  - extends AppService│
    └────────┬───────────┘        └──────────┬───────────┘
             │                               │
             └───────────────┬───────────────┘
                             │
                             │ 5. Instantiate services
                             ▼
                 ┌───────────────────────┐
                 │  Class.forName()      │
                 │  newInstance()        │
                 └───────────┬───────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
                 ▼                       ▼
     ┌──────────────────┐   ┌──────────────────────┐
     │  implements      │   │  Regular AppService  │
     │  Configurable?   │   │  No config needed    │
     └──────┬───────────┘   └──────────┬───────────┘
            │                          │
            │ YES                      │
            ▼                          │
     ┌──────────────────┐              │
     │  configure(map)  │              │
     │  Inject config   │              │
     └──────┬───────────┘              │
            │                          │
            └──────────┬───────────────┘
                       │
                       │ 6. Start service
                       ▼
             ┌─────────────────────┐
             │  service.start()    │
             │  running = true     │
             └──────────┬──────────┘
                        │
                        │ 7. Register
                        ▼
             ┌─────────────────────┐
             │  Add to services    │
             │  List<AppService>   │
             └──────────┬──────────┘
                        │
                        │ 8. Log success
                        ▼
             ┌─────────────────────┐
             │  log.info("Started")│
             └──────────┬──────────┘
                        │
                        │ 9. Initialize CLI
                        ▼
             ┌─────────────────────┐
             │  CLI.start()        │
             │  Wait for commands  │
             └─────────────────────┘
```

---

## Service Loading Flow (Detailed)

```
    ╔═══════════════════════════════════╗
    ║  ServiceManager.startAll()        ║
    ╚═══════════════╤═══════════════════╝
                    │
                    ▼
    ┌───────────────────────────────────┐
    │  Load Configuration File          │
    │  Path: DEFAULT_CONFIG_PATH        │
    │  System.getProperty("blueflex.config")│
    │  Default: "config/services.json"  │
    └───────────────┬───────────────────┘
                    │
                    ▼
    ┌───────────────────────────────────┐
    │  ObjectMapper.readValue()         │
    │  Type: List<Configuration>        │
    └───────────────┬───────────────────┘
                    │
                    │ Success?
        ┌───────────┴───────────┐
        │                       │
       YES                     NO
        │                       │
        ▼                       ▼
    Loop each              ┌─────────────┐
    configuration          │  Catch      │
        │                  │  Exception  │
        │                  │  log.error()│
        ▼                  └─────────────┘
    ┌─────────────┐
    │  Check      │
    │  enabled?   │
    └──────┬──────┘
           │
     ┌─────┴─────┐
     │           │
    YES         NO (skip)
     │           │
     ▼           └──────► Continue
    ┌──────────────────┐
    │  Load Class      │
    │  Class.forName() │
    └────────┬─────────┘
             │
             ▼
    ┌──────────────────┐
    │  Create Instance │
    │  newInstance()   │
    └────────┬─────────┘
             │
             ▼
    ┌──────────────────────────┐
    │  instanceof AppService?  │
    └────────┬─────────────────┘
             │
       ┌─────┴─────┐
       │           │
      YES         NO
       │           │
       ▼           ▼
    Add to      Log warning
    services    "Not an AppService"
    list            │
       │            └──────► Continue
       ▼
    ┌──────────────────────────┐
    │  Check if Configurable   │
    │  instanceof Configurable?│
    └────────┬─────────────────┘
             │
       ┌─────┴─────┐
       │           │
      YES         NO (skip config)
       │           │
       ▼           │
    ┌──────────────┤
    │  configure() │
    │  Pass map    │
    └──────────────┤
                   │
                   ▼
         ┌─────────────────┐
         │  service.start()│
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │  log.info()     │
         │  "Started: X"   │
         └────────┬────────┘
                  │
                  ▼
         Continue to next service

    ┌──────────────────────────┐
    │  All Services Started    │
    │  Ready for CLI commands  │
    └──────────────────────────┘
```

---

## CLI Command Processing Flow

```
    ╔═══════════════════════════════╗
    ║  CLI Running (Scanner Loop)   ║
    ╚═══════════════╤═══════════════╝
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Wait for User Input          │
    │  scanner.nextLine()           │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Parse Input                  │
    │  Split by space               │
    │  cmd[0] = command             │
    │  cmd[1..n] = args             │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Lookup Command in Registry   │
    │  Map<String, CommandHandler>  │
    └───────────────┬───────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
    Found                   Not Found
        │                       │
        ▼                       ▼
    Execute                 Show Error
    Handler                 "Unknown command"
        │                       │
        │                       └──► Back to input
        │
    ┌───┴─────────────────────────────────┐
    │                                     │
    ▼                                     ▼
┌────────────┐                    ┌──────────────┐
│  "start"   │                    │   "stop"     │
│   command  │                    │   command    │
└─────┬──────┘                    └──────┬───────┘
      │                                  │
      ▼                                  ▼
Find service                      Find service
by name                           by type
      │                                  │
      ▼                                  ▼
Check if                          Check if
already running                   running
      │                                  │
  ┌───┴───┐                         ┌────┴────┐
  │       │                         │         │
 YES     NO                        YES       NO
  │       │                         │         │
  │       ▼                         │         ▼
  │   Call start()                  │   Already stopped
  │   log.info()                    │   log.warn()
  │       │                         │         │
  └───┬───┘                         └────┬────┘
      │                                  │
      └──────────┬───────────────────────┘
                 │
                 ▼
         Back to CLI input

    ┌────────────────────┐
    │   "restart"        │
    │    command         │
    └─────────┬──────────┘
              │
              ▼
    Find service by name
              │
              ▼
        ┌──────────┐
        │  stop()  │
        └─────┬────┘
              │
              ▼
        ┌──────────┐
        │  start() │
        └─────┬────┘
              │
              ▼
         log.info()
              │
              └──► Back to CLI


    ┌────────────────────┐
    │   "list" / "ls"    │
    └─────────┬──────────┘
              │
              ▼
    Loop all services
    in manager.getServices()
              │
              ▼
    ┌──────────────────────┐
    │  For each service:   │
    │  - name              │
    │  - version           │
    │  - status (✓/✗)     │
    │  - description       │
    └──────────┬───────────┘
              │
              ▼
    Display total count
              │
              └──► Back to CLI


    ┌────────────────────┐
    │  "exit" / "quit"   │
    └─────────┬──────────┘
              │
              ▼
    ┌─────────────────────┐
    │  manager.stopAll()  │
    └─────────┬───────────┘
              │
              ▼
    ┌─────────────────────┐
    │  log.info("Shutdown")│
    └─────────┬───────────┘
              │
              ▼
    ┌─────────────────────┐
    │  System.exit(0)     │
    └─────────────────────┘
```

---

## Service Configuration Flow

```
    ╔══════════════════════════════╗
    ║  ConfigurableAppService      ║
    ║  configure(Map<String,Object>)║
    ╚═══════════════╤══════════════╝
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Receive config Map           │
    │  From services.json           │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Example config:              │
    │  {                            │
    │    "width": 800,              │
    │    "height": 600,             │
    │    "title": "App"             │
    │  }                            │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Extract Values               │
    │  width = (int) map.get("width")│
    │  height = (int) map.get("height")│
    │  title = (String) map.get("title")│
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Validate Values (Optional)   │
    │  - Check ranges               │
    │  - Check required fields      │
    │  - Set defaults if missing    │
    └───────────────┬───────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
    Valid                   Invalid
        │                       │
        ▼                       ▼
    Store in              Throw Exception
    instance              or Log Warning
    variables                  │
        │                      │
        └──────────┬───────────┘
                   │
                   ▼
         ┌─────────────────┐
         │  Configuration  │
         │  Complete       │
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │  Service Ready  │
         │  for start()    │
         └─────────────────┘

Example Implementation:

    public class MainUI extends AppService
                    implements ConfigurableAppService {

        private int width = 800;    // defaults
        private int height = 600;
        private String title = "App";

        @Override
        public void configure(Map<String, Object> config) {
            if (config.containsKey("width")) {
                this.width = (Integer) config.get("width");
            }
            if (config.containsKey("height")) {
                this.height = (Integer) config.get("height");
            }
            if (config.containsKey("title")) {
                this.title = (String) config.get("title");
            }

            // Validation
            if (width < 400 || height < 300) {
                throw new IllegalArgumentException(
                    "Window size too small"
                );
            }
        }

        @Override
        public void start() {
            super.start();
            // Use configured values
            createWindow(width, height, title);
        }
    }
```

---

## Error Handling Flow

```
    ╔═══════════════════════════════╗
    ║  Operation Attempt            ║
    ╚═══════════════╤═══════════════╝
                    │
                    ▼
            Try-Catch Block
                    │
        ┌───────────┴───────────┐
        │                       │
    Success                 Exception
        │                       │
        ▼                       ▼
    Continue            ┌──────────────┐
    normally            │ Catch Block  │
                        └──────┬───────┘
                               │
                   ┌───────────┴────────────┐
                   │                        │
                   ▼                        ▼
        ClassNotFoundException    Other Exceptions
        (Service not found)       (Various)
                   │                        │
                   ▼                        ▼
        ┌──────────────────┐    ┌──────────────────┐
        │ log.error()      │    │ log.error()      │
        │ "Failed to load" │    │ Exception message│
        └────────┬─────────┘    └────────┬─────────┘
                 │                       │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │  Continue with next   │
                 │  service (don't crash)│
                 └───────────┬───────────┘
                             │
                             ▼
                     Framework continues

Error Types and Handling:

1. Configuration Errors
   ├── File not found
   │   └── Use default config
   ├── Parse error
   │   └── Log and skip
   └── Invalid format
       └── Log and use defaults

2. Service Errors
   ├── Class not found
   │   └── Log warning, continue
   ├── Not AppService
   │   └── Log warning, skip
   └── Start failed
       └── Log error, mark failed

3. Runtime Errors
   ├── Service crash
   │   └── Log, mark stopped
   ├── Command not found
   │   └── Show help
   └── Invalid argument
       └── Show usage

Logging Levels:
┌──────────┬─────────────────────────┐
│  INFO    │ Normal operations       │
│  WARN    │ Non-critical issues     │
│  ERROR   │ Critical failures       │
│  DEBUG   │ Detailed diagnostics    │
└──────────┴─────────────────────────┘
```

---

## Complete Request-Response Flow

```
    ╔═══════════════════════════════════════════════╗
    ║          User Types Command                   ║
    ║          "start HelloService"                 ║
    ╚═══════════════════╤═══════════════════════════╝
                        │
                        ▼
        ┌───────────────────────────────┐
        │  CLI.processInput()           │
        │  Parse: cmd="start"           │
        │         args=["HelloService"] │
        └───────────────┬───────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │  Lookup command in registry   │
        │  commands.get("start")        │
        └───────────────┬───────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │  CommandHandler.execute()     │
        │  with args                    │
        └───────────────┬───────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │  Validate args.length         │
        │  Need at least 1 arg          │
        └───────────────┬───────────────┘
                        │
            ┌───────────┴───────────┐
            │                       │
        Valid                   Invalid
            │                       │
            ▼                       ▼
     Search service          Show usage error
     in manager              "Usage: start <name>"
            │                       │
            ▼                       │
     Loop services                  │
     by name                        │
            │                       │
    ┌───────┴────────┐              │
    │                │              │
  Found          Not Found          │
    │                │              │
    ▼                ▼              │
Check if        Show error          │
running         "Service not found" │
    │                │              │
┌───┴───┐            │              │
│       │            │              │
YES    NO            │              │
│       │            │              │
│       ▼            │              │
│   Call             │              │
│   manager          │              │
│   .startService()  │              │
│       │            │              │
│       ▼            │              │
│   service.start()  │              │
│       │            │              │
│       ▼            │              │
│   running = true   │              │
│       │            │              │
│       ▼            │              │
│   log.info()       │              │
│   "Started: X"     │              │
│       │            │              │
│       └────────────┼──────────────┘
│                    │
▼                    │
log.warn()           │
"Already running"    │
│                    │
└────────────────────┘
            │
            ▼
    ┌───────────────────────────────┐
    │  Response shown to user       │
    │  "[INFO] Started: HelloService"│
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Return to CLI prompt         │
    │  "BlueFlex> "                 │
    └───────────────────────────────┘


Data Flow Diagram:

    services.json ──────► ServiceManager ──────► AppService
         │                     │                      │
         │                     │                      │
         ▼                     ▼                      ▼
    Configuration         List<Service>          start()
         │                     │                   stop()
         │                     │                   isRunning()
         ▼                     ▼                      │
    ObjectMapper          CLI Commands               │
         │                     │                      │
         └─────────────────────┴──────────────────────┘
                               │
                               ▼
                          User Interface
                        (Console/GUI/API)
```

---

## Service Dependency Resolution Flow (Future Feature)

```
    ╔═══════════════════════════════╗
    ║  Services with Dependencies   ║
    ╚═══════════════╤═══════════════╝
                    │
                    ▼
    Example services.json:
    [
      {
        "className": "DatabaseService",
        "dependsOn": []
      },
      {
        "className": "APIService",
        "dependsOn": ["DatabaseService"]
      },
      {
        "className": "UIService",
        "dependsOn": ["APIService", "DatabaseService"]
      }
    ]
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Build Dependency Graph       │
    │  Using DFS/Topological Sort   │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Detect Circular Dependencies │
    └───────────────┬───────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
    No Cycle                Cycle Found
        │                       │
        ▼                       ▼
    Calculate              Throw Error
    start order            "Circular dependency"
        │
        ▼
    Start Order:
    1. DatabaseService  ─┐
    2. APIService       ─┼─► Start in sequence
    3. UIService        ─┘
        │
        ▼
    All services running
    with dependencies satisfied
```

---

## Shutdown Flow

```
    ╔═══════════════════════════════╗
    ║  User types "exit" or Ctrl+C  ║
    ╚═══════════════╤═══════════════╝
                    │
                    ▼
    ┌───────────────────────────────┐
    │  CLI.exit command or          │
    │  Shutdown hook triggered      │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  manager.stopAll()            │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  log.info("Stopping all...")  │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  Loop services in reverse     │
    │  (Last started, first stopped)│
    └───────────────┬───────────────┘
                    │
                    ▼
    For each service:
    ┌───────────────────────────────┐
    │  if (service.isRunning())     │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  service.stop()               │
    │  - Clean up resources         │
    │  - Close connections          │
    │  - Save state if needed       │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  running = false              │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  log.info("Stopped: X")       │
    └───────────────┬───────────────┘
                    │
                    ▼
    Continue to next service
                    │
                    ▼
    ┌───────────────────────────────┐
    │  All services stopped         │
    │  log.info("All stopped")      │
    └───────────────┬───────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  System.exit(0)               │
    │  JVM terminates               │
    └───────────────────────────────┘
```

---

## Quick Reference: Key Classes Flow

```
Main
 ├── Creates ServiceManager
 │   ├── Reads services.json
 │   ├── Creates AppService instances
 │   └── Calls start() on each
 └── Creates CLI
     ├── Registers commands
     └── Processes user input
         ├── start → manager.startService()
         ├── stop → manager.stopService()
         ├── restart → stop + start
         ├── list → manager.getServices()
         └── exit → manager.stopAll()

AppService (abstract)
 ├── ServiceName (String)
 ├── ServiceType (String)
 ├── ServiceVersion (String)
 ├── running (boolean)
 ├── start() → running = true
 └── stop() → running = false

ConfigurableAppService (interface)
 └── configure(Map<String, Object>)
     └── Called before start()

ServiceManager
 ├── services: List<AppService>
 ├── startAll()
 ├── stopAll()
 ├── startService(service)
 ├── stopService(service)
 └── getServices()

CLI
 ├── commands: Map<String, CommandHandler>
 ├── register(name, handler)
 └── processInput(String)

AppLogger
 ├── info(msg)
 ├── warn(msg)
 ├── error(msg)
 └── debug(msg)
```

---

## Summary: Understanding the Flow

1. **Initialization**: Main → ServiceManager → Load Config → Start Services
2. **Configuration**: JSON → ObjectMapper → Map → configure() → Service
3. **Lifecycle**: Constructor → configure() → start() → [running] → stop()
4. **CLI**: Input → Parse → Lookup → Execute → ServiceManager → Service
5. **Shutdown**: exit → stopAll() → Loop services → stop() each → System.exit()

Use these flowcharts to understand:
- How data flows through the system
- Where to add new features
- How to debug issues
- The lifecycle of services
- Command processing logic

---

**Refer to BUILD_GUIDE.md for implementation details!**
