import java.io.*;
import java.util.*;

public class Assembler {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong file");
            return;
        }

        String inputFile = args[0];
        if (!inputFile.endsWith(".asm")) {
            System.out.println("Wrong file type");
            return;
        }

        String outputFile = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".hack";

        try {
            SymbolTable symbolTable = new SymbolTable();


            Parser firstParser = new Parser(inputFile);
            int romAddress = 0;

            while (firstParser.hasMoreCommands()) {
                firstParser.advance();
                String commandType = firstParser.commandType();

                if (commandType == null) {
                    continue;
                }

                if (commandType.equals("L_COMMAND")) {
                    String symbol = firstParser.symbol();
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, romAddress);
                    }
                } else {
                    romAddress++;
                }
            }
            firstParser.close();


            Parser secondParser = new Parser(inputFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            int nextVariableAddress = 16;

            while (secondParser.hasMoreCommands()) {
                secondParser.advance();
                String commandType = secondParser.commandType();

                if (commandType == null || commandType.equals("L_COMMAND")) {
                    continue;
                }

                if (commandType.equals("A_COMMAND")) {
                    String symbol = secondParser.symbol();
                    int address;

                    if (isNumber(symbol)) {
                        address = Integer.parseInt(symbol);
                    } else {
                        if (!symbolTable.contains(symbol)) {
                            symbolTable.addEntry(symbol, nextVariableAddress);
                            nextVariableAddress++;
                        }
                        address = symbolTable.getAddress(symbol);
                    }

                    writer.write(to16BitBinary(address));
                    writer.newLine();

                } else if (commandType.equals("C_COMMAND")) {
                    String comp = secondParser.comp();
                    String dest = secondParser.dest();
                    String jump = secondParser.jump();

                    String binary = "111" + Code.comp(comp) + Code.dest(dest) + Code.jump(jump);
                    writer.write(binary);
                    writer.newLine();
                }
            }

            secondParser.close();
            writer.close();

            System.out.println("Assembly complete: " + outputFile);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String to16BitBinary(int value) {
        String binary = Integer.toBinaryString(value);
        while (binary.length() < 16) {
            binary = "0" + binary;
        }
        return binary;
    }
}