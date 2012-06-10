package org.pbit.graphlr;

import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;


public class GraphlrTest {
    public static void main(String[] args) throws Exception {
        try {
            CharStream input = new ANTLRFileStream("/data/workspace/Graphlr/src/org/pbit/graphlr/GraphlrTest.java");
            GraphlrJavaLexer lex = new GraphlrJavaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            GraphlrJavaParser parser = new GraphlrJavaParser(tokens);
            parser.compilationUnit();
            
            //find compilation units
            List<Tree> l = parser.runCypher("start ret=node:node_auto_index(type = 'class') return ret");
            for (Tree t : l) {
                System.out.println("Compilation unit found: " + t.getText()); 
            }
            
            //find methods of a particular class
            l = parser.runCypher("start n=node:node_auto_index(name = 'GraphlrTest') match n-[:IMPLEMENTS]->ret return ret");
            for (Tree t : l) {
                System.out.println("GraphlrTest implements: " + t.getText()); 
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
    
    protected void dummy () {}
}

final class Dummy {
    
}