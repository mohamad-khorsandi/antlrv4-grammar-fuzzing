import org.antlr.v4.runtime.*;

public class HardErrorStrategy extends DefaultErrorStrategy {


    @Override
    protected void reportMissingToken(Parser recognizer) {
        super.reportMissingToken(recognizer);
        throw new RuntimeException();

    }

    @Override
    protected void reportUnwantedToken(Parser recognizer) {
        super.reportUnwantedToken(recognizer);
        throw new RuntimeException();
    }

    @Override
    protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
        super.reportFailedPredicate(recognizer, e);
        throw new RuntimeException();
    }

    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        super.reportInputMismatch(recognizer, e);
        throw new RuntimeException();
    }

    @Override
    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
        super.reportNoViableAlternative(recognizer, e);
        throw new RuntimeException();
    }

    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        super.reportError(recognizer, e);
        throw new RuntimeException();
    }

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        super.recover(recognizer, e);
        throw new RuntimeException();
    }
}
