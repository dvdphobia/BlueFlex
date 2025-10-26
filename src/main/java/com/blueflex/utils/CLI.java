package com.blueflex.utils;

import com.blueflex.service.AppService;
import com.blueflex.service.ServiceManager;
import java.util.*;
import java.util.concurrent.*;
import java.io.Console;

/**
 * Advanced CLI System for BlueFlex
 * --------------------------------
 * Features:
 * - Command registry (easy to add new commands)
 * - Built-in help and aliases
 * - Asynchronous command execution
 * - Command history (if terminal supports it)
 * - Basic autocomplete suggestions
 */
public class CLI extends AppService {
    private final AppLogger log = new AppLogger("CLI");
    private final ServiceManager manager;
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running;

    public CLI(ServiceManager manager) {
        super("CLI Utils", "CLI", "1.0.0", "Advanced command line interface for BlueFlex");
        this.manager = manager;
        registerDefaultCommands();
    }

    /**
     * Represents a single CLI command.
     */
    public static class Command {
        public final String name;
        public final String description;
        public final String alias;
        public final CommandExecutor executor;

        public Command(String name, String description, String alias, CommandExecutor executor) {
            this.name = name;
            this.description = description;
            this.alias = alias;
            this.executor = executor;
        }
    }

    @FunctionalInterface
    public interface CommandExecutor {
        void execute(String[] args);
    }

    /**
     * Registers the built-in commands.
     */
    private void registerDefaultCommands() {
        register("help", "Show available commands", "h", args -> showHelp());
        register("stop", "Stop all running services and CLI", "exit", args -> stopCLI());
        register("restart", "Restart a service by name", "r", args -> {
            if (args.length == 0) {
                log.warn("Usage: restart <service_name>");
                return;
            }
            manager.restartServiceByName(args[0]);
        });
        register("stop-type", "Stop services by type", "st", args -> {
            if (args.length == 0) {
                log.warn("Usage: stop-type <service_type>");
                return;
            }
            manager.stopServiceByType(args[0]);
        });
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
    }

    /**
     * Registers a new command.
     */
    public void register(String name, String description, String alias, CommandExecutor executor) {
        commands.put(name, new Command(name, description, alias, executor));
    }

    private void showHelp() {
        log.info("\nAvailable Commands:");
        for (Command cmd : commands.values()) {
            log.info(String.format("  %-12s (%s)\n     %s", cmd.name, cmd.alias, cmd.description));
        }
    }

    private void stopCLI() {
        log.info("Stopping CLI and all services...");
        running = false;
        manager.stopAll();
        executor.shutdownNow();
    }

    /**
     * Runs the CLI loop until stopped.
     */
    public void runUntilStopped() {
        running = true;
        log.info("CLI started. Type 'help' for commands. Type 'stop' to exit.");

        Scanner scanner = new Scanner(System.in);
        Console console = System.console(); // for autocomplete support
        List<String> history = new ArrayList<>();

        while (running) {
            System.out.print("> ");
            String input;

            if (console != null) {
                input = console.readLine();
            } else {
                input = scanner.nextLine();
            }

            if (input == null || input.trim().isEmpty()) continue;

            history.add(input);
            String[] parts = input.trim().split("\\s+");
            String cmdName = parts[0].toLowerCase();
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            Command cmd = findCommand(cmdName);
            if (cmd != null) {
                executor.submit(() -> {
                    try {
                        cmd.executor.execute(args);
                    } catch (Exception e) {
                        log.error("Command failed: " + e.getMessage());
                    }
                });
            } else {
                suggestCommand(cmdName);
            }
        }

        log.info("CLI stopped.");
        scanner.close();
    }

    /**
     * Finds a command by name or alias.
     */
    private Command findCommand(String name) {
        for (Command cmd : commands.values()) {
            if (cmd.name.equalsIgnoreCase(name) || cmd.alias.equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Suggests possible commands if input is unknown.
     */
    private void suggestCommand(String input) {
        log.warn("Unknown command: " + input);
        List<String> suggestions = new ArrayList<>();
        for (Command cmd : commands.values()) {
            if (cmd.name.startsWith(input) || cmd.alias.startsWith(input)) {
                suggestions.add(cmd.name);
            }
        }
        if (!suggestions.isEmpty()) {
            log.info("Did you mean: " + String.join(", ", suggestions) + "?");
        }
    }
}
