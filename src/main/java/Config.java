package main.java;

import java.util.ArrayList;

public class Config {
    public static double SIGMA = 1;
    public static String STARTING_RULE = "compilationUnit";
    public static ArrayList<Character> ALL_CHARS = new ArrayList<>();

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
