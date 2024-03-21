import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;

public class ErrorStrategy extends DefaultErrorStrategy {

    @Override
    public void reportMatch(Parser recognizer) {
        super.reportMatch(recognizer);
    }

}
