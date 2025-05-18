package org.schema;

import org.jline.builtins.ConfigurationPath;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.jansi.AnsiConsole;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;


class YamlSchemaChecker  {

    @Command(name = "",
            description = {
                    "Example interactive shell with completion and autosuggestions. " +
                            "Hit @|magenta <TAB>|@ to see available commands.",
                    "Hit @|magenta ALT-S|@ to toggle tailtips.",
                    ""},
            footer = {"", "Press Ctrl-D to exit."},
            subcommands = {
                    CheckYaml.class, CommandLine.HelpCommand.class})
    static class CliCommands implements Runnable {
        PrintWriter out;

        CliCommands() {
        }

        public void setReader(LineReader reader) {
            out = reader.getTerminal().writer();
        }

        public void run() {
            out.println(new CommandLine(this).getUsageMessage());
        }
    }
    @Command(name = "yaml-schema-checker", mixinStandardHelpOptions = true, version = "yaml-schema-checker 1.0",
            description = "Validates YAML files against a given schema.")
    static class CheckYaml implements Callable<Integer>{
        public static void main(String[] args) {
            int exitCode = new CommandLine(new YamlSchemaChecker()).execute(args);
            System.exit(exitCode);
        }

        @Override
        public Integer call() {
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(options);
            String document = "\n- Hesperiidae\n- Papilionidae\n- Apatelodidae\n- Epiplemidae";
            List<String> list = yaml.load(document);
            System.out.println(list);
            return 0;
        }
    }

    public static void main(String args[]) throws IOException {
        AnsiConsole.systemInstall();

        CliCommands commands = new CliCommands();
        Terminal terminal = TerminalBuilder.builder().build();
        Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));

        Builtins builtins = new Builtins(workDir, new ConfigurationPath(workDir.get(), workDir.get()), null);
        builtins.rename(Builtins.Command.TTOP, "top");
        builtins.alias("zle", "widget");
        builtins.alias("bindkey", "keymap");

        Parser parser = new DefaultParser();
        PicocliCommands.PicocliCommandsFactory factory = new PicocliCommands.PicocliCommandsFactory();

        CommandLine cmd = new CommandLine(commands);
        PicocliCommands picocliCommands = new PicocliCommands(cmd);
        SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, null);
        systemRegistry.setCommandRegistries(builtins,picocliCommands);
        systemRegistry.register("help", picocliCommands);
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(systemRegistry.completer())
                .parser(parser)
                .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
                .build();
        commands.setReader(reader);
        factory.setTerminal(terminal);
        String prompt = "prompt> ";
        String rightPrompt = null;
        String line;

        while (true) {
            try {
                systemRegistry.cleanUp();
                line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
                systemRegistry.execute(line);
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            } catch (Exception e) {
                systemRegistry.trace(e);
            }
        }

    }
}
