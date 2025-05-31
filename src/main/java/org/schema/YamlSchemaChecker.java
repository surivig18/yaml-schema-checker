package org.schema;

import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.consoleui.prompt.PromptResultItemIF;
import org.jline.consoleui.prompt.builder.PromptBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;


class YamlSchemaChecker  {

    @Command(name = "yaml-schema-checker", mixinStandardHelpOptions = true, version = "yaml-schema-checker 1.0",
            description = "Validates YAML files against a given schema.")
    static class CheckYaml implements Callable<Integer> {
        PromptBuilder promptBuilder;

        ConsolePrompt prompt;

        void setPrompt(ConsolePrompt prompt) {
            this.prompt = prompt;
        }

        @Override
        public Integer call() {
            try {
                promptBuilder = new PromptBuilder();
                LoaderOptions options = new LoaderOptions();

                promptBuilder.createInputPrompt()
                        .name("yamlFilePath")
                        .message("Enter your yaml file path")
                        .defaultValue("src/main/resources/schema.yaml")
                        .addPrompt();
                Map<String, PromptResultItemIF> result = prompt.prompt(promptBuilder.build());
                String yamlFilePath = result.get("yamlFilePath").getResult();
                if (yamlFilePath == null || yamlFilePath.isEmpty()) {
                    System.out.println("No file path provided. Exiting.");
                    return 1; // Exit with an error code
                }
                // Load the YAML file
                Yaml yaml = new Yaml(options);

                try (FileInputStream inputStream = new FileInputStream(yamlFilePath)) {
                    Object yamlContent = yaml.load(inputStream);
                    System.out.println("YAML content loaded successfully:");
                    System.out.println(yamlContent);
                }
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return 0; // Return a non-zero exit code on error
            }
        }
    }


    public static void main(String args[]) throws IOException {
        Terminal terminal = TerminalBuilder.builder().build();
        ConsolePrompt prompt = new ConsolePrompt(terminal);
        PromptBuilder builder = prompt.getPromptBuilder();

        // Create a list prompt for single selection
        builder.createListPrompt()
                .name("choice")
                .message("Choose what you want to do?")
                .newItem("yaml-schema-checker")
                .text("Yaml Check")
                .add()
                .newItem("yaml-modify").
                text("Yaml Modify")
                .add()
                .pageSize(3) // Show 3 items at a time
                .addPrompt();

        try {
            Map<String, PromptResultItemIF> result = prompt.prompt(builder.build());
            if( result.get("choice").getResult().equals("yaml-schema-checker")) {
                CheckYaml checkYaml = new CheckYaml();
                checkYaml.setPrompt(prompt);
                CommandLine cmd = new CommandLine(checkYaml);
                cmd.execute();
            }
            else{
                System.out.println("Not developed yettt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
