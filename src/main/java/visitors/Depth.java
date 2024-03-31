package visitors;

public class Depth {
    private Integer depth;
    private final boolean isRecur;

    private Depth(Integer depth, boolean isRecur) {
        this.depth = depth;
        this.isRecur = isRecur;
    }

    public static Depth recur() {
        return new Depth(null, true);
    }

    public static Depth of(Integer depth) {
        return new Depth(depth, false);
    }


    public int getDepth() {
        if (this.isRecur)
            throw new RuntimeException();
        else
            return this.depth;
    }

    public boolean isRecur() {
        return isRecur;
    }

    public void inc() {
        if (!this.isRecur)
            depth++;
    }

    public int compareTo(Depth other) {
        if (this.isRecur && other.isRecur)
            return 0;
        if(this.isRecur)
            return 1;
        if(other.isRecur())
            return -1;
        else
            return Integer.compare(this.depth, other.depth);
    }
}
