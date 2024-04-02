package fuzzer;


public class FuzzerConfig {
    public static final String GRAMMAR_PATH = "src/main/resources/Java.g4";
    public static String STARTING_RULE = "compilationUnit";
    public static Integer SEED = null;
    public static final int MAX_DEPTH = 16;
    public static double PLUS_STAR_GAUSSIAN_SIGMA = 6;
    public static double QUESTION_BERNOULLI_PROP = .7;
}
