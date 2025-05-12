package org.schema;

import org.jline.reader.LineReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;


@Command(name = "",
        description = {
                "Example interactive shell with completion and autosuggestions. " +
                        "Hit @|magenta <TAB>|@ to see available commands.",
                "Hit @|magenta ALT-S|@ to toggle tailtips.",
                ""},
        footer = {"", "Press Ctrl-D to exit."},
        subcommands = {
                YamlSchemaChecker.class, CommandLine.HelpCommand.class})
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
class YamlSchemaChecker implements Callable<Integer> {


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
