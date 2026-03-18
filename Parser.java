import java.io.*;
import java.util.*;

public class Parser {
    private List<String> commands;
    private int currentIndex;
    private String currentCommand;

    public Parser(String fileName) throws IOException {
        commands = new ArrayList<>();
        currentIndex = 0;
        currentCommand = null;

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            line = cleanLine(line);
            if (!line.equals("")) {
                commands.add(line);
            }
        }

        reader.close();
    }

    private String cleanLine(String line) {
        int commentIndex = line.indexOf("//");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }

        line = line.trim();
        line = line.replaceAll("\\s+", "");
        return line;
    }

    public boolean hasMoreCommands() {
        return currentIndex < commands.size();
    }

    public void advance() {
        if (hasMoreCommands()) {
            currentCommand = commands.get(currentIndex);
            currentIndex++;
        }
    }

    public String commandType() {
        if (currentCommand == null) {
            return null;
        }

        if (currentCommand.startsWith("@")) {
            return "A_COMMAND";
        } else if (currentCommand.startsWith("(") && currentCommand.endsWith(")")) {
            return "L_COMMAND";
        } else {
            return "C_COMMAND";
        }
    }

    public String symbol() {
        String type = commandType();

        if (type.equals("A_COMMAND")) {
            return currentCommand.substring(1);
        } else if (type.equals("L_COMMAND")) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        }

        return null;
    }

    public String dest() {
        if (!commandType().equals("C_COMMAND")) {
            return null;
        }

        int equalIndex = currentCommand.indexOf('=');
        if (equalIndex != -1) {
            return currentCommand.substring(0, equalIndex);
        }

        return null;
    }

    public String comp() {
        if (!commandType().equals("C_COMMAND")) {
            return null;
        }

        int equalIndex = currentCommand.indexOf('=');
        int semicolonIndex = currentCommand.indexOf(';');

        int start;
        int end;

        if (equalIndex != -1) {
            start = equalIndex + 1;
        } else {
            start = 0;
        }

        if (semicolonIndex != -1) {
            end = semicolonIndex;
        } else {
            end = currentCommand.length();
        }

        return currentCommand.substring(start, end);
    }

    public String jump() {
        if (!commandType().equals("C_COMMAND")) {
            return null;
        }

        int semicolonIndex = currentCommand.indexOf(';');
        if (semicolonIndex != -1) {
            return currentCommand.substring(semicolonIndex + 1);
        }

        return null;
    }

    public void close() {
        // Nothing needed here, but kept for structure similarity
    }
}
