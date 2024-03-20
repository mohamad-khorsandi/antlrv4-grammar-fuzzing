
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import parser.ANTLRv4Lexer;
import parser.ANTLRv4Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream in = CharStreams.fromFileName("src/resources/src_grammar/CSV.g4");
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);

        Generator generator = new Generator();
        String result = String.valueOf(generator.visitGrammarSpec(parser.grammarSpec()));
        System.out.println("Evaluation result: " + result);

    }
}