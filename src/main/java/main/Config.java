package main;

import java.util.ArrayList;

public class Config {
    public static final String GRAMMAR_PATH = "src/main/resources/Java.g4";
    public static String STARTING_RULE = "compilationUnit";
    public static Integer SEED = null;
    public static final int MAX_DEPTH = 16;
    public static double SIGMA = 6;
    public static double QUESTION_PROP = .7;
}

//todo does this work correct? (ebnfSuffix |)

//    element
//    : labeledElement (ebnfSuffix |)
//    | atom (ebnfSuffix |)
//    | ebnf
//    | actionBlock (QUESTION predicateOptions?)?
//    ;