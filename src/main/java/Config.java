package main.java;

import java.util.ArrayList;

public class Config {
    public static final String GRAMMAR_PATH = "src/main/resources/Java8.g4";
    public static double SIGMA = 4;
    public static String STARTING_RULE = "compilationUnit";
    public static ArrayList<Character> ALL_CHARS = new ArrayList<>();
    public static Integer SEED = 2;

    static {
        // Add printable ASCII characters
        for (int i = 32; i <= 126; i++) {
            ALL_CHARS.add((char) i);
        }

        // Add the three additional characters
        ALL_CHARS.add((char)13); //CR (\r)
        ALL_CHARS.add((char)10); //LF (\n)
        ALL_CHARS.add((char)9);  //TAB
    }
}
