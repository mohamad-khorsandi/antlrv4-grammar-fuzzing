//package main.java;
//
//public class Depth {
//    private final Integer depth;
//    private final boolean isRecur;
//
//    public Depth(Integer depth, boolean isRecur) {
//        this.depth = depth;
//        this.isRecur = isRecur;
//    }
//
//    public static Depth recursive() {
//        return new Depth(null, true);
//    }
//
//    public static Depth of(Integer depth) {
//        return new Depth(depth, false);
//    }
//
//    public int getDepth() {
//        if (this.isRecur)
//            throw new RuntimeException();
//        else
//            return this.depth;
//    }
//
//    public boolean isRecur() {
//        return isRecur;
//    }
//
//    public void
//}
