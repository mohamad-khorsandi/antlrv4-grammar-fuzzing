package fuzzer;


public class FuzzParams {
    public FuzzParams(String startingRule, int maxDepth, double plusStarGaussianSigma, double questionBernoulliProp) {
        this.startingRule = startingRule;
        this.maxDepth = maxDepth;
        this.plusStarGaussianSigma = plusStarGaussianSigma;
        this.questionBernoulliProp = questionBernoulliProp;
    }

    public final String startingRule;
    public final int maxDepth;
    public final double plusStarGaussianSigma;
    public final double questionBernoulliProp;
}
