package main.java;

import main.java.parser.ANTLRv4ParserBaseListener;
import main.java.parser.ANTLRv4ParserBaseVisitor;
import main.java.parser.ANTLRv4ParserListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentVisitor extends ANTLRv4ParserBaseVisitor {
    private List<String> fragments = new ArrayList<>();



    public List<String> getFragments() {
        return fragments;
    }
}