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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


class YamlSchemaChecker  {

    @Command(name = "yaml-schema-checker", mixinStandardHelpOptions = true, version = "yaml-schema-checker 1.0",
            description = "Validates YAML files against a given schema.")
    static class CheckYaml implements Callable<Integer>{
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
                CommandLine cmd = new CommandLine(new CheckYaml());
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
