// $ANTLR 3.4 GraphlrJava.g 2012-06-10 15:30:15

    package org.pbit.graphlr;
  
    import org.neo4j.cypher.javacompat.ExecutionEngine;
    import org.neo4j.cypher.javacompat.ExecutionResult;
    import org.neo4j.graphdb.GraphDatabaseService;
    import org.neo4j.graphdb.Node;
    import org.neo4j.graphdb.Transaction;
    import org.neo4j.graphdb.factory.GraphDatabaseSetting;
    import org.neo4j.graphdb.factory.GraphDatabaseSettings;
    import org.neo4j.graphdb.index.Index;
    import org.neo4j.helpers.collection.IteratorUtil;
    import org.neo4j.test.TestGraphDatabaseFactory;
    import org.neo4j.graphdb.RelationshipType;
    import java.util.Iterator;
    import java.util.Stack;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created
 *          elementValuePair and elementValuePairs rules, then used them in the
 *          annotation rule.  Allows it to recognize annotation references with
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which
 *          has the Identifier portion in it, the parser would fail on constants in
 *          annotation definitions because it expected two identifiers.
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *         
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and
 *          normalInterfaceDeclaration rather than classDeclaration and
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation,
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java
 *      letter-or-digit is a character for which the method
 *      Character.isJavaIdentifierPart(int) returns true."
 */
@SuppressWarnings({"all", "warnings", "unchecked", "cast"})
public class GraphlrJavaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABSTRACT", "AMP", "AMPAMP", "AMPEQ", "ASSERT", "BANG", "BANGEQ", "BAR", "BARBAR", "BAREQ", "BOOLEAN", "BREAK", "BYTE", "CARET", "CARETEQ", "CASE", "CATCH", "CHAR", "CHARLITERAL", "CLASS", "COLON", "COMMA", "COMMENT", "CONST", "CONTINUE", "DEFAULT", "DO", "DOT", "DOUBLE", "DOUBLELITERAL", "DoubleSuffix", "ELLIPSIS", "ELSE", "ENUM", "EQ", "EQEQ", "EXTENDS", "EscapeSequence", "Exponent", "FALSE", "FINAL", "FINALLY", "FLOAT", "FLOATLITERAL", "FOR", "FloatSuffix", "GOTO", "GT", "HexDigit", "HexPrefix", "IDENTIFIER", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "INTLITERAL", "IdentifierPart", "IdentifierStart", "IntegerNumber", "LBRACE", "LBRACKET", "LINE_COMMENT", "LONG", "LONGLITERAL", "LPAREN", "LT", "LongSuffix", "MONKEYS_AT", "NATIVE", "NEW", "NULL", "NonIntegerNumber", "PACKAGE", "PERCENT", "PERCENTEQ", "PLUS", "PLUSEQ", "PLUSPLUS", "PRIVATE", "PROTECTED", "PUBLIC", "QUES", "RBRACE", "RBRACKET", "RETURN", "RPAREN", "SEMI", "SHORT", "SLASH", "SLASHEQ", "STAR", "STAREQ", "STATIC", "STRICTFP", "STRINGLITERAL", "SUB", "SUBEQ", "SUBSUB", "SUPER", "SWITCH", "SYNCHRONIZED", "SurrogateIdentifer", "THIS", "THROW", "THROWS", "TILDE", "TRANSIENT", "TRUE", "TRY", "VOID", "VOLATILE", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ABSTRACT=4;
    public static final int AMP=5;
    public static final int AMPAMP=6;
    public static final int AMPEQ=7;
    public static final int ASSERT=8;
    public static final int BANG=9;
    public static final int BANGEQ=10;
    public static final int BAR=11;
    public static final int BARBAR=12;
    public static final int BAREQ=13;
    public static final int BOOLEAN=14;
    public static final int BREAK=15;
    public static final int BYTE=16;
    public static final int CARET=17;
    public static final int CARETEQ=18;
    public static final int CASE=19;
    public static final int CATCH=20;
    public static final int CHAR=21;
    public static final int CHARLITERAL=22;
    public static final int CLASS=23;
    public static final int COLON=24;
    public static final int COMMA=25;
    public static final int COMMENT=26;
    public static final int CONST=27;
    public static final int CONTINUE=28;
    public static final int DEFAULT=29;
    public static final int DO=30;
    public static final int DOT=31;
    public static final int DOUBLE=32;
    public static final int DOUBLELITERAL=33;
    public static final int DoubleSuffix=34;
    public static final int ELLIPSIS=35;
    public static final int ELSE=36;
    public static final int ENUM=37;
    public static final int EQ=38;
    public static final int EQEQ=39;
    public static final int EXTENDS=40;
    public static final int EscapeSequence=41;
    public static final int Exponent=42;
    public static final int FALSE=43;
    public static final int FINAL=44;
    public static final int FINALLY=45;
    public static final int FLOAT=46;
    public static final int FLOATLITERAL=47;
    public static final int FOR=48;
    public static final int FloatSuffix=49;
    public static final int GOTO=50;
    public static final int GT=51;
    public static final int HexDigit=52;
    public static final int HexPrefix=53;
    public static final int IDENTIFIER=54;
    public static final int IF=55;
    public static final int IMPLEMENTS=56;
    public static final int IMPORT=57;
    public static final int INSTANCEOF=58;
    public static final int INT=59;
    public static final int INTERFACE=60;
    public static final int INTLITERAL=61;
    public static final int IdentifierPart=62;
    public static final int IdentifierStart=63;
    public static final int IntegerNumber=64;
    public static final int LBRACE=65;
    public static final int LBRACKET=66;
    public static final int LINE_COMMENT=67;
    public static final int LONG=68;
    public static final int LONGLITERAL=69;
    public static final int LPAREN=70;
    public static final int LT=71;
    public static final int LongSuffix=72;
    public static final int MONKEYS_AT=73;
    public static final int NATIVE=74;
    public static final int NEW=75;
    public static final int NULL=76;
    public static final int NonIntegerNumber=77;
    public static final int PACKAGE=78;
    public static final int PERCENT=79;
    public static final int PERCENTEQ=80;
    public static final int PLUS=81;
    public static final int PLUSEQ=82;
    public static final int PLUSPLUS=83;
    public static final int PRIVATE=84;
    public static final int PROTECTED=85;
    public static final int PUBLIC=86;
    public static final int QUES=87;
    public static final int RBRACE=88;
    public static final int RBRACKET=89;
    public static final int RETURN=90;
    public static final int RPAREN=91;
    public static final int SEMI=92;
    public static final int SHORT=93;
    public static final int SLASH=94;
    public static final int SLASHEQ=95;
    public static final int STAR=96;
    public static final int STAREQ=97;
    public static final int STATIC=98;
    public static final int STRICTFP=99;
    public static final int STRINGLITERAL=100;
    public static final int SUB=101;
    public static final int SUBEQ=102;
    public static final int SUBSUB=103;
    public static final int SUPER=104;
    public static final int SWITCH=105;
    public static final int SYNCHRONIZED=106;
    public static final int SurrogateIdentifer=107;
    public static final int THIS=108;
    public static final int THROW=109;
    public static final int THROWS=110;
    public static final int TILDE=111;
    public static final int TRANSIENT=112;
    public static final int TRUE=113;
    public static final int TRY=114;
    public static final int VOID=115;
    public static final int VOLATILE=116;
    public static final int WHILE=117;
    public static final int WS=118;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public GraphlrJavaParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public GraphlrJavaParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
        this.state.ruleMemo = new HashMap[381+1];
         

    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return GraphlrJavaParser.tokenNames; }
    public String getGrammarFileName() { return "GraphlrJava.g"; }


        private final GraphDatabaseService db = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().
                    setConfig( GraphDatabaseSettings.node_keys_indexable, "type,name" ).
                    setConfig( GraphDatabaseSettings.relationship_keys_indexable, "IMPLEMENTS" ).
                    setConfig( GraphDatabaseSettings.node_auto_indexing, GraphDatabaseSetting.TRUE ).
                    setConfig( GraphDatabaseSettings.relationship_auto_indexing, GraphDatabaseSetting.TRUE ).
                    newGraphDatabase();
        
        private Map<Long, Tree> id2Tree = new HashMap<Long, Tree>();
        
        private Stack<Node> clazzes = new Stack<Node>();
        
        public final List<Tree> runCypher(String stmt) {
            ExecutionEngine engine = new ExecutionEngine(db);
            ExecutionResult res = engine.execute(stmt);
            Iterator<Node> ci = res.columnAs("ret");
            List<Tree> ret = new ArrayList<Tree>(); 
            for (Node node : IteratorUtil.asIterable(ci)) {
                ret.add(id2Tree.get(node.getId())); 
            }
            
            return ret;
        }
        
        private enum Rels implements RelationshipType {
            IMPLEMENTS
        }


    public static class compilationUnit_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "compilationUnit"
    // GraphlrJava.g:352:1: compilationUnit : ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* ;
    public final GraphlrJavaParser.compilationUnit_return compilationUnit() throws RecognitionException {
        GraphlrJavaParser.compilationUnit_return retval = new GraphlrJavaParser.compilationUnit_return();
        retval.start = input.LT(1);

        int compilationUnit_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope annotations1 =null;

        ParserRuleReturnScope packageDeclaration2 =null;

        ParserRuleReturnScope importDeclaration3 =null;

        ParserRuleReturnScope typeDeclaration4 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }

            // GraphlrJava.g:353:5: ( ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )* )
            // GraphlrJava.g:353:9: ( ( annotations )? packageDeclaration )? ( importDeclaration )* ( typeDeclaration )*
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:353:9: ( ( annotations )? packageDeclaration )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==MONKEYS_AT) ) {
                int LA2_1 = input.LA(2);

                if ( (synpred2_GraphlrJava()) ) {
                    alt2=1;
                }
            }
            else if ( (LA2_0==PACKAGE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // GraphlrJava.g:353:13: ( annotations )? packageDeclaration
                    {
                    // GraphlrJava.g:353:13: ( annotations )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==MONKEYS_AT) ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // GraphlrJava.g:353:14: annotations
                            {
                            pushFollow(FOLLOW_annotations_in_compilationUnit120);
                            annotations1=annotations();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations1.getTree());


                            }
                            break;

                    }


                    pushFollow(FOLLOW_packageDeclaration_in_compilationUnit149);
                    packageDeclaration2=packageDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, packageDeclaration2.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:357:9: ( importDeclaration )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==IMPORT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // GraphlrJava.g:357:10: importDeclaration
            	    {
            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit171);
            	    importDeclaration3=importDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, importDeclaration3.getTree());


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            // GraphlrJava.g:359:9: ( typeDeclaration )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ABSTRACT||LA4_0==BOOLEAN||LA4_0==BYTE||LA4_0==CHAR||LA4_0==CLASS||LA4_0==DOUBLE||LA4_0==ENUM||LA4_0==FINAL||LA4_0==FLOAT||LA4_0==IDENTIFIER||(LA4_0 >= INT && LA4_0 <= INTERFACE)||LA4_0==LONG||LA4_0==LT||(LA4_0 >= MONKEYS_AT && LA4_0 <= NATIVE)||(LA4_0 >= PRIVATE && LA4_0 <= PUBLIC)||(LA4_0 >= SEMI && LA4_0 <= SHORT)||(LA4_0 >= STATIC && LA4_0 <= STRICTFP)||LA4_0==SYNCHRONIZED||LA4_0==TRANSIENT||(LA4_0 >= VOID && LA4_0 <= VOLATILE)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // GraphlrJava.g:359:10: typeDeclaration
            	    {
            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit193);
            	    typeDeclaration4=typeDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeDeclaration4.getTree());


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 1, compilationUnit_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "compilationUnit"


    public static class packageDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "packageDeclaration"
    // GraphlrJava.g:363:1: packageDeclaration : 'package' qualifiedName ';' ;
    public final GraphlrJavaParser.packageDeclaration_return packageDeclaration() throws RecognitionException {
        GraphlrJavaParser.packageDeclaration_return retval = new GraphlrJavaParser.packageDeclaration_return();
        retval.start = input.LT(1);

        int packageDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal5=null;
        Token char_literal7=null;
        ParserRuleReturnScope qualifiedName6 =null;


        Tree string_literal5_tree=null;
        Tree char_literal7_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }

            // GraphlrJava.g:364:5: ( 'package' qualifiedName ';' )
            // GraphlrJava.g:364:9: 'package' qualifiedName ';'
            {
            root_0 = (Tree)adaptor.nil();


            string_literal5=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_packageDeclaration224); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal5_tree = 
            (Tree)adaptor.create(string_literal5)
            ;
            adaptor.addChild(root_0, string_literal5_tree);
            }


            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration226);
            qualifiedName6=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName6.getTree());


            char_literal7=(Token)match(input,SEMI,FOLLOW_SEMI_in_packageDeclaration236); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal7_tree = 
            (Tree)adaptor.create(char_literal7)
            ;
            adaptor.addChild(root_0, char_literal7_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 2, packageDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "packageDeclaration"


    public static class importDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "importDeclaration"
    // GraphlrJava.g:368:1: importDeclaration : ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' );
    public final GraphlrJavaParser.importDeclaration_return importDeclaration() throws RecognitionException {
        GraphlrJavaParser.importDeclaration_return retval = new GraphlrJavaParser.importDeclaration_return();
        retval.start = input.LT(1);

        int importDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal8=null;
        Token string_literal9=null;
        Token IDENTIFIER10=null;
        Token char_literal11=null;
        Token char_literal12=null;
        Token char_literal13=null;
        Token string_literal14=null;
        Token string_literal15=null;
        Token IDENTIFIER16=null;
        Token char_literal17=null;
        Token IDENTIFIER18=null;
        Token char_literal19=null;
        Token char_literal20=null;
        Token char_literal21=null;

        Tree string_literal8_tree=null;
        Tree string_literal9_tree=null;
        Tree IDENTIFIER10_tree=null;
        Tree char_literal11_tree=null;
        Tree char_literal12_tree=null;
        Tree char_literal13_tree=null;
        Tree string_literal14_tree=null;
        Tree string_literal15_tree=null;
        Tree IDENTIFIER16_tree=null;
        Tree char_literal17_tree=null;
        Tree IDENTIFIER18_tree=null;
        Tree char_literal19_tree=null;
        Tree char_literal20_tree=null;
        Tree char_literal21_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }

            // GraphlrJava.g:369:5: ( 'import' ( 'static' )? IDENTIFIER '.' '*' ';' | 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==IMPORT) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==STATIC) ) {
                    int LA9_2 = input.LA(3);

                    if ( (LA9_2==IDENTIFIER) ) {
                        int LA9_3 = input.LA(4);

                        if ( (LA9_3==DOT) ) {
                            int LA9_4 = input.LA(5);

                            if ( (LA9_4==STAR) ) {
                                alt9=1;
                            }
                            else if ( (LA9_4==IDENTIFIER) ) {
                                alt9=2;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                            	int nvaeMark = input.mark();
                            	try {
                            		for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++)
                            			input.consume();
                            		NoViableAltException nvae =
                            			new NoViableAltException("", 9, 4, input);

                            		throw nvae;
                            	} finally {
                            		input.rewind(nvaeMark);
                            	}
                            }
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                        	int nvaeMark = input.mark();
                        	try {
                        		for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++)
                        			input.consume();
                        		NoViableAltException nvae =
                        			new NoViableAltException("", 9, 3, input);

                        		throw nvae;
                        	} finally {
                        		input.rewind(nvaeMark);
                        	}
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	int nvaeMark = input.mark();
                    	try {
                    		for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++)
                    			input.consume();
                    		NoViableAltException nvae =
                    			new NoViableAltException("", 9, 2, input);

                    		throw nvae;
                    	} finally {
                    		input.rewind(nvaeMark);
                    	}
                    }
                }
                else if ( (LA9_1==IDENTIFIER) ) {
                    int LA9_3 = input.LA(3);

                    if ( (LA9_3==DOT) ) {
                        int LA9_4 = input.LA(4);

                        if ( (LA9_4==STAR) ) {
                            alt9=1;
                        }
                        else if ( (LA9_4==IDENTIFIER) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                        	int nvaeMark = input.mark();
                        	try {
                        		for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++)
                        			input.consume();
                        		NoViableAltException nvae =
                        			new NoViableAltException("", 9, 4, input);

                        		throw nvae;
                        	} finally {
                        		input.rewind(nvaeMark);
                        	}
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	int nvaeMark = input.mark();
                    	try {
                    		for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++)
                    			input.consume();
                    		NoViableAltException nvae =
                    			new NoViableAltException("", 9, 3, input);

                    		throw nvae;
                    	} finally {
                    		input.rewind(nvaeMark);
                    	}
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 9, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 9, 0, input);

            	throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // GraphlrJava.g:369:9: 'import' ( 'static' )? IDENTIFIER '.' '*' ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration257); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal8_tree = 
                    (Tree)adaptor.create(string_literal8)
                    ;
                    adaptor.addChild(root_0, string_literal8_tree);
                    }


                    // GraphlrJava.g:370:9: ( 'static' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==STATIC) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // GraphlrJava.g:370:10: 'static'
                            {
                            string_literal9=(Token)match(input,STATIC,FOLLOW_STATIC_in_importDeclaration268); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal9_tree = 
                            (Tree)adaptor.create(string_literal9)
                            ;
                            adaptor.addChild(root_0, string_literal9_tree);
                            }


                            }
                            break;

                    }


                    IDENTIFIER10=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration289); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER10_tree = 
                    (Tree)adaptor.create(IDENTIFIER10)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER10_tree);
                    }


                    char_literal11=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclaration291); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal11_tree = 
                    (Tree)adaptor.create(char_literal11)
                    ;
                    adaptor.addChild(root_0, char_literal11_tree);
                    }


                    char_literal12=(Token)match(input,STAR,FOLLOW_STAR_in_importDeclaration293); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal12_tree = 
                    (Tree)adaptor.create(char_literal12)
                    ;
                    adaptor.addChild(root_0, char_literal12_tree);
                    }


                    char_literal13=(Token)match(input,SEMI,FOLLOW_SEMI_in_importDeclaration303); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal13_tree = 
                    (Tree)adaptor.create(char_literal13)
                    ;
                    adaptor.addChild(root_0, char_literal13_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:374:9: 'import' ( 'static' )? IDENTIFIER ( '.' IDENTIFIER )+ ( '.' '*' )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal14=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal14_tree = 
                    (Tree)adaptor.create(string_literal14)
                    ;
                    adaptor.addChild(root_0, string_literal14_tree);
                    }


                    // GraphlrJava.g:375:9: ( 'static' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==STATIC) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // GraphlrJava.g:375:10: 'static'
                            {
                            string_literal15=(Token)match(input,STATIC,FOLLOW_STATIC_in_importDeclaration332); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal15_tree = 
                            (Tree)adaptor.create(string_literal15)
                            ;
                            adaptor.addChild(root_0, string_literal15_tree);
                            }


                            }
                            break;

                    }


                    IDENTIFIER16=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration353); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER16_tree = 
                    (Tree)adaptor.create(IDENTIFIER16)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER16_tree);
                    }


                    // GraphlrJava.g:378:9: ( '.' IDENTIFIER )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==DOT) ) {
                            int LA7_1 = input.LA(2);

                            if ( (LA7_1==IDENTIFIER) ) {
                                alt7=1;
                            }


                        }


                        switch (alt7) {
                    	case 1 :
                    	    // GraphlrJava.g:378:10: '.' IDENTIFIER
                    	    {
                    	    char_literal17=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclaration364); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal17_tree = 
                    	    (Tree)adaptor.create(char_literal17)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal17_tree);
                    	    }


                    	    IDENTIFIER18=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_importDeclaration366); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER18_tree = 
                    	    (Tree)adaptor.create(IDENTIFIER18)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER18_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    // GraphlrJava.g:380:9: ( '.' '*' )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==DOT) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // GraphlrJava.g:380:10: '.' '*'
                            {
                            char_literal19=(Token)match(input,DOT,FOLLOW_DOT_in_importDeclaration388); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal19_tree = 
                            (Tree)adaptor.create(char_literal19)
                            ;
                            adaptor.addChild(root_0, char_literal19_tree);
                            }


                            char_literal20=(Token)match(input,STAR,FOLLOW_STAR_in_importDeclaration390); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal20_tree = 
                            (Tree)adaptor.create(char_literal20)
                            ;
                            adaptor.addChild(root_0, char_literal20_tree);
                            }


                            }
                            break;

                    }


                    char_literal21=(Token)match(input,SEMI,FOLLOW_SEMI_in_importDeclaration411); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal21_tree = 
                    (Tree)adaptor.create(char_literal21)
                    ;
                    adaptor.addChild(root_0, char_literal21_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 3, importDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "importDeclaration"


    public static class qualifiedImportName_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedImportName"
    // GraphlrJava.g:385:1: qualifiedImportName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final GraphlrJavaParser.qualifiedImportName_return qualifiedImportName() throws RecognitionException {
        GraphlrJavaParser.qualifiedImportName_return retval = new GraphlrJavaParser.qualifiedImportName_return();
        retval.start = input.LT(1);

        int qualifiedImportName_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER22=null;
        Token char_literal23=null;
        Token IDENTIFIER24=null;

        Tree IDENTIFIER22_tree=null;
        Tree char_literal23_tree=null;
        Tree IDENTIFIER24_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }

            // GraphlrJava.g:386:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // GraphlrJava.g:386:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER22=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName431); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER22_tree = 
            (Tree)adaptor.create(IDENTIFIER22)
            ;
            adaptor.addChild(root_0, IDENTIFIER22_tree);
            }


            // GraphlrJava.g:387:9: ( '.' IDENTIFIER )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==DOT) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // GraphlrJava.g:387:10: '.' IDENTIFIER
            	    {
            	    char_literal23=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedImportName442); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal23_tree = 
            	    (Tree)adaptor.create(char_literal23)
            	    ;
            	    adaptor.addChild(root_0, char_literal23_tree);
            	    }


            	    IDENTIFIER24=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedImportName444); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER24_tree = 
            	    (Tree)adaptor.create(IDENTIFIER24)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER24_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 4, qualifiedImportName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedImportName"


    public static class typeDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeDeclaration"
    // GraphlrJava.g:391:1: typeDeclaration : ( classOrInterfaceDeclaration | ';' );
    public final GraphlrJavaParser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        GraphlrJavaParser.typeDeclaration_return retval = new GraphlrJavaParser.typeDeclaration_return();
        retval.start = input.LT(1);

        int typeDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal26=null;
        ParserRuleReturnScope classOrInterfaceDeclaration25 =null;


        Tree char_literal26_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }

            // GraphlrJava.g:392:5: ( classOrInterfaceDeclaration | ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ABSTRACT||LA11_0==BOOLEAN||LA11_0==BYTE||LA11_0==CHAR||LA11_0==CLASS||LA11_0==DOUBLE||LA11_0==ENUM||LA11_0==FINAL||LA11_0==FLOAT||LA11_0==IDENTIFIER||(LA11_0 >= INT && LA11_0 <= INTERFACE)||LA11_0==LONG||LA11_0==LT||(LA11_0 >= MONKEYS_AT && LA11_0 <= NATIVE)||(LA11_0 >= PRIVATE && LA11_0 <= PUBLIC)||LA11_0==SHORT||(LA11_0 >= STATIC && LA11_0 <= STRICTFP)||LA11_0==SYNCHRONIZED||LA11_0==TRANSIENT||(LA11_0 >= VOID && LA11_0 <= VOLATILE)) ) {
                alt11=1;
            }
            else if ( (LA11_0==SEMI) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 11, 0, input);

            	throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GraphlrJava.g:392:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration475);
                    classOrInterfaceDeclaration25=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration25.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:393:9: ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal26=(Token)match(input,SEMI,FOLLOW_SEMI_in_typeDeclaration485); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal26_tree = 
                    (Tree)adaptor.create(char_literal26)
                    ;
                    adaptor.addChild(root_0, char_literal26_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 5, typeDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeDeclaration"


    public static class classOrInterfaceDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classOrInterfaceDeclaration"
    // GraphlrJava.g:396:1: classOrInterfaceDeclaration : ( classDeclaration | interfaceDeclaration );
    public final GraphlrJavaParser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        GraphlrJavaParser.classOrInterfaceDeclaration_return retval = new GraphlrJavaParser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);

        int classOrInterfaceDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope classDeclaration27 =null;

        ParserRuleReturnScope interfaceDeclaration28 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }

            // GraphlrJava.g:397:5: ( classDeclaration | interfaceDeclaration )
            int alt12=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA12_1 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA12_2 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA12_3 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA12_4 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA12_5 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA12_6 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA12_7 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA12_8 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA12_9 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA12_10 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA12_11 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA12_12 = input.LA(2);

                if ( (synpred12_GraphlrJava()) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 12, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt12=1;
                }
                break;
            case INTERFACE:
                {
                alt12=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 12, 0, input);

            	throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // GraphlrJava.g:397:10: classDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration506);
                    classDeclaration27=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration27.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:398:9: interfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration516);
                    interfaceDeclaration28=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration28.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 6, classOrInterfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceDeclaration"


    public static class modifiers_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "modifiers"
    // GraphlrJava.g:402:1: modifiers : ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* ;
    public final GraphlrJavaParser.modifiers_return modifiers() throws RecognitionException {
        GraphlrJavaParser.modifiers_return retval = new GraphlrJavaParser.modifiers_return();
        retval.start = input.LT(1);

        int modifiers_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal30=null;
        Token string_literal31=null;
        Token string_literal32=null;
        Token string_literal33=null;
        Token string_literal34=null;
        Token string_literal35=null;
        Token string_literal36=null;
        Token string_literal37=null;
        Token string_literal38=null;
        Token string_literal39=null;
        Token string_literal40=null;
        ParserRuleReturnScope annotation29 =null;


        Tree string_literal30_tree=null;
        Tree string_literal31_tree=null;
        Tree string_literal32_tree=null;
        Tree string_literal33_tree=null;
        Tree string_literal34_tree=null;
        Tree string_literal35_tree=null;
        Tree string_literal36_tree=null;
        Tree string_literal37_tree=null;
        Tree string_literal38_tree=null;
        Tree string_literal39_tree=null;
        Tree string_literal40_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }

            // GraphlrJava.g:403:5: ( ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )* )
            // GraphlrJava.g:404:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:404:5: ( annotation | 'public' | 'protected' | 'private' | 'static' | 'abstract' | 'final' | 'native' | 'synchronized' | 'transient' | 'volatile' | 'strictfp' )*
            loop13:
            do {
                int alt13=13;
                switch ( input.LA(1) ) {
                case MONKEYS_AT:
                    {
                    int LA13_2 = input.LA(2);

                    if ( (LA13_2==IDENTIFIER) ) {
                        alt13=1;
                    }


                    }
                    break;
                case PUBLIC:
                    {
                    alt13=2;
                    }
                    break;
                case PROTECTED:
                    {
                    alt13=3;
                    }
                    break;
                case PRIVATE:
                    {
                    alt13=4;
                    }
                    break;
                case STATIC:
                    {
                    alt13=5;
                    }
                    break;
                case ABSTRACT:
                    {
                    alt13=6;
                    }
                    break;
                case FINAL:
                    {
                    alt13=7;
                    }
                    break;
                case NATIVE:
                    {
                    alt13=8;
                    }
                    break;
                case SYNCHRONIZED:
                    {
                    alt13=9;
                    }
                    break;
                case TRANSIENT:
                    {
                    alt13=10;
                    }
                    break;
                case VOLATILE:
                    {
                    alt13=11;
                    }
                    break;
                case STRICTFP:
                    {
                    alt13=12;
                    }
                    break;

                }

                switch (alt13) {
            	case 1 :
            	    // GraphlrJava.g:404:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_modifiers551);
            	    annotation29=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation29.getTree());


            	    }
            	    break;
            	case 2 :
            	    // GraphlrJava.g:405:9: 'public'
            	    {
            	    string_literal30=(Token)match(input,PUBLIC,FOLLOW_PUBLIC_in_modifiers561); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal30_tree = 
            	    (Tree)adaptor.create(string_literal30)
            	    ;
            	    adaptor.addChild(root_0, string_literal30_tree);
            	    }


            	    }
            	    break;
            	case 3 :
            	    // GraphlrJava.g:406:9: 'protected'
            	    {
            	    string_literal31=(Token)match(input,PROTECTED,FOLLOW_PROTECTED_in_modifiers571); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal31_tree = 
            	    (Tree)adaptor.create(string_literal31)
            	    ;
            	    adaptor.addChild(root_0, string_literal31_tree);
            	    }


            	    }
            	    break;
            	case 4 :
            	    // GraphlrJava.g:407:9: 'private'
            	    {
            	    string_literal32=(Token)match(input,PRIVATE,FOLLOW_PRIVATE_in_modifiers581); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal32_tree = 
            	    (Tree)adaptor.create(string_literal32)
            	    ;
            	    adaptor.addChild(root_0, string_literal32_tree);
            	    }


            	    }
            	    break;
            	case 5 :
            	    // GraphlrJava.g:408:9: 'static'
            	    {
            	    string_literal33=(Token)match(input,STATIC,FOLLOW_STATIC_in_modifiers591); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal33_tree = 
            	    (Tree)adaptor.create(string_literal33)
            	    ;
            	    adaptor.addChild(root_0, string_literal33_tree);
            	    }


            	    }
            	    break;
            	case 6 :
            	    // GraphlrJava.g:409:9: 'abstract'
            	    {
            	    string_literal34=(Token)match(input,ABSTRACT,FOLLOW_ABSTRACT_in_modifiers601); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal34_tree = 
            	    (Tree)adaptor.create(string_literal34)
            	    ;
            	    adaptor.addChild(root_0, string_literal34_tree);
            	    }


            	    }
            	    break;
            	case 7 :
            	    // GraphlrJava.g:410:9: 'final'
            	    {
            	    string_literal35=(Token)match(input,FINAL,FOLLOW_FINAL_in_modifiers611); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal35_tree = 
            	    (Tree)adaptor.create(string_literal35)
            	    ;
            	    adaptor.addChild(root_0, string_literal35_tree);
            	    }


            	    }
            	    break;
            	case 8 :
            	    // GraphlrJava.g:411:9: 'native'
            	    {
            	    string_literal36=(Token)match(input,NATIVE,FOLLOW_NATIVE_in_modifiers621); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal36_tree = 
            	    (Tree)adaptor.create(string_literal36)
            	    ;
            	    adaptor.addChild(root_0, string_literal36_tree);
            	    }


            	    }
            	    break;
            	case 9 :
            	    // GraphlrJava.g:412:9: 'synchronized'
            	    {
            	    string_literal37=(Token)match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_modifiers631); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal37_tree = 
            	    (Tree)adaptor.create(string_literal37)
            	    ;
            	    adaptor.addChild(root_0, string_literal37_tree);
            	    }


            	    }
            	    break;
            	case 10 :
            	    // GraphlrJava.g:413:9: 'transient'
            	    {
            	    string_literal38=(Token)match(input,TRANSIENT,FOLLOW_TRANSIENT_in_modifiers641); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal38_tree = 
            	    (Tree)adaptor.create(string_literal38)
            	    ;
            	    adaptor.addChild(root_0, string_literal38_tree);
            	    }


            	    }
            	    break;
            	case 11 :
            	    // GraphlrJava.g:414:9: 'volatile'
            	    {
            	    string_literal39=(Token)match(input,VOLATILE,FOLLOW_VOLATILE_in_modifiers651); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal39_tree = 
            	    (Tree)adaptor.create(string_literal39)
            	    ;
            	    adaptor.addChild(root_0, string_literal39_tree);
            	    }


            	    }
            	    break;
            	case 12 :
            	    // GraphlrJava.g:415:9: 'strictfp'
            	    {
            	    string_literal40=(Token)match(input,STRICTFP,FOLLOW_STRICTFP_in_modifiers661); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal40_tree = 
            	    (Tree)adaptor.create(string_literal40)
            	    ;
            	    adaptor.addChild(root_0, string_literal40_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 7, modifiers_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "modifiers"


    public static class variableModifiers_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableModifiers"
    // GraphlrJava.g:420:1: variableModifiers : ( 'final' | annotation )* ;
    public final GraphlrJavaParser.variableModifiers_return variableModifiers() throws RecognitionException {
        GraphlrJavaParser.variableModifiers_return retval = new GraphlrJavaParser.variableModifiers_return();
        retval.start = input.LT(1);

        int variableModifiers_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal41=null;
        ParserRuleReturnScope annotation42 =null;


        Tree string_literal41_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }

            // GraphlrJava.g:421:5: ( ( 'final' | annotation )* )
            // GraphlrJava.g:421:9: ( 'final' | annotation )*
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:421:9: ( 'final' | annotation )*
            loop14:
            do {
                int alt14=3;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==FINAL) ) {
                    alt14=1;
                }
                else if ( (LA14_0==MONKEYS_AT) ) {
                    alt14=2;
                }


                switch (alt14) {
            	case 1 :
            	    // GraphlrJava.g:421:13: 'final'
            	    {
            	    string_literal41=(Token)match(input,FINAL,FOLLOW_FINAL_in_variableModifiers693); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal41_tree = 
            	    (Tree)adaptor.create(string_literal41)
            	    ;
            	    adaptor.addChild(root_0, string_literal41_tree);
            	    }


            	    }
            	    break;
            	case 2 :
            	    // GraphlrJava.g:422:13: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_variableModifiers707);
            	    annotation42=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation42.getTree());


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 8, variableModifiers_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableModifiers"


    public static class classDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classDeclaration"
    // GraphlrJava.g:427:1: classDeclaration : ( normalClassDeclaration | enumDeclaration );
    public final GraphlrJavaParser.classDeclaration_return classDeclaration() throws RecognitionException {
        GraphlrJavaParser.classDeclaration_return retval = new GraphlrJavaParser.classDeclaration_return();
        retval.start = input.LT(1);

        int classDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope normalClassDeclaration43 =null;

        ParserRuleReturnScope enumDeclaration44 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }

            // GraphlrJava.g:428:5: ( normalClassDeclaration | enumDeclaration )
            int alt15=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA15_1 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA15_2 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA15_3 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA15_4 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA15_5 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA15_6 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA15_7 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA15_8 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA15_9 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA15_10 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA15_11 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA15_12 = input.LA(2);

                if ( (synpred27_GraphlrJava()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 15, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case CLASS:
                {
                alt15=1;
                }
                break;
            case ENUM:
                {
                alt15=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 15, 0, input);

            	throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // GraphlrJava.g:428:9: normalClassDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration743);
                    normalClassDeclaration43=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration43.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:429:9: enumDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration753);
                    enumDeclaration44=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration44.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 9, classDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classDeclaration"


    public static class normalClassDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalClassDeclaration"
    // GraphlrJava.g:432:1: normalClassDeclaration : modifiers 'class' name= IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody ;
    public final GraphlrJavaParser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        GraphlrJavaParser.normalClassDeclaration_return retval = new GraphlrJavaParser.normalClassDeclaration_return();
        retval.start = input.LT(1);

        int normalClassDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token name=null;
        Token string_literal46=null;
        Token string_literal48=null;
        Token string_literal50=null;
        ParserRuleReturnScope modifiers45 =null;

        ParserRuleReturnScope typeParameters47 =null;

        ParserRuleReturnScope type49 =null;

        ParserRuleReturnScope typeList51 =null;

        ParserRuleReturnScope classBody52 =null;


        Tree name_tree=null;
        Tree string_literal46_tree=null;
        Tree string_literal48_tree=null;
        Tree string_literal50_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }

            // GraphlrJava.g:433:5: ( modifiers 'class' name= IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody )
            // GraphlrJava.g:433:9: modifiers 'class' name= IDENTIFIER ( typeParameters )? ( 'extends' type )? ( 'implements' typeList )? classBody
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_normalClassDeclaration773);
            modifiers45=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers45.getTree());


            string_literal46=(Token)match(input,CLASS,FOLLOW_CLASS_in_normalClassDeclaration775); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal46_tree = 
            (Tree)adaptor.create(string_literal46)
            ;
            adaptor.addChild(root_0, string_literal46_tree);
            }


            name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalClassDeclaration779); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            name_tree = 
            (Tree)adaptor.create(name)
            ;
            adaptor.addChild(root_0, name_tree);
            }


            if ( state.backtracking==0 ) {
                    Transaction transaction = db.beginTx();
                    try {
                        Node node = db.createNode();
                        node.setProperty("type", "class");
                        node.setProperty("name", (name!=null?name.getText():null));
                        id2Tree.put(node.getId(), name_tree);
                        clazzes.push(node);

                        transaction.success();
                    }
                    finally {
                        transaction.finish();
                    }
                }

            // GraphlrJava.g:449:9: ( typeParameters )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LT) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // GraphlrJava.g:449:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration796);
                    typeParameters47=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters47.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:451:9: ( 'extends' type )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==EXTENDS) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // GraphlrJava.g:451:10: 'extends' type
                    {
                    string_literal48=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_normalClassDeclaration818); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal48_tree = 
                    (Tree)adaptor.create(string_literal48)
                    ;
                    adaptor.addChild(root_0, string_literal48_tree);
                    }


                    pushFollow(FOLLOW_type_in_normalClassDeclaration820);
                    type49=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type49.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:453:9: ( 'implements' typeList )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==IMPLEMENTS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // GraphlrJava.g:453:10: 'implements' typeList
                    {
                    string_literal50=(Token)match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_normalClassDeclaration842); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal50_tree = 
                    (Tree)adaptor.create(string_literal50)
                    ;
                    adaptor.addChild(root_0, string_literal50_tree);
                    }


                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration844);
                    typeList51=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList51.getTree());


                    }
                    break;

            }


            pushFollow(FOLLOW_classBody_in_normalClassDeclaration877);
            classBody52=classBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody52.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 10, normalClassDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalClassDeclaration"


    public static class typeParameters_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeParameters"
    // GraphlrJava.g:459:1: typeParameters : '<' typeParameter ( ',' typeParameter )* '>' ;
    public final GraphlrJavaParser.typeParameters_return typeParameters() throws RecognitionException {
        GraphlrJavaParser.typeParameters_return retval = new GraphlrJavaParser.typeParameters_return();
        retval.start = input.LT(1);

        int typeParameters_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal53=null;
        Token char_literal55=null;
        Token char_literal57=null;
        ParserRuleReturnScope typeParameter54 =null;

        ParserRuleReturnScope typeParameter56 =null;


        Tree char_literal53_tree=null;
        Tree char_literal55_tree=null;
        Tree char_literal57_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }

            // GraphlrJava.g:460:5: ( '<' typeParameter ( ',' typeParameter )* '>' )
            // GraphlrJava.g:460:9: '<' typeParameter ( ',' typeParameter )* '>'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal53=(Token)match(input,LT,FOLLOW_LT_in_typeParameters898); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal53_tree = 
            (Tree)adaptor.create(char_literal53)
            ;
            adaptor.addChild(root_0, char_literal53_tree);
            }


            pushFollow(FOLLOW_typeParameter_in_typeParameters912);
            typeParameter54=typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter54.getTree());


            // GraphlrJava.g:462:13: ( ',' typeParameter )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // GraphlrJava.g:462:14: ',' typeParameter
            	    {
            	    char_literal55=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeParameters927); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal55_tree = 
            	    (Tree)adaptor.create(char_literal55)
            	    ;
            	    adaptor.addChild(root_0, char_literal55_tree);
            	    }


            	    pushFollow(FOLLOW_typeParameter_in_typeParameters929);
            	    typeParameter56=typeParameter();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameter56.getTree());


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            char_literal57=(Token)match(input,GT,FOLLOW_GT_in_typeParameters954); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal57_tree = 
            (Tree)adaptor.create(char_literal57)
            ;
            adaptor.addChild(root_0, char_literal57_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 11, typeParameters_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeParameters"


    public static class typeParameter_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeParameter"
    // GraphlrJava.g:467:1: typeParameter : IDENTIFIER ( 'extends' typeBound )? ;
    public final GraphlrJavaParser.typeParameter_return typeParameter() throws RecognitionException {
        GraphlrJavaParser.typeParameter_return retval = new GraphlrJavaParser.typeParameter_return();
        retval.start = input.LT(1);

        int typeParameter_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER58=null;
        Token string_literal59=null;
        ParserRuleReturnScope typeBound60 =null;


        Tree IDENTIFIER58_tree=null;
        Tree string_literal59_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }

            // GraphlrJava.g:468:5: ( IDENTIFIER ( 'extends' typeBound )? )
            // GraphlrJava.g:468:9: IDENTIFIER ( 'extends' typeBound )?
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER58=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeParameter974); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER58_tree = 
            (Tree)adaptor.create(IDENTIFIER58)
            ;
            adaptor.addChild(root_0, IDENTIFIER58_tree);
            }


            // GraphlrJava.g:469:9: ( 'extends' typeBound )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==EXTENDS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // GraphlrJava.g:469:10: 'extends' typeBound
                    {
                    string_literal59=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_typeParameter985); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal59_tree = 
                    (Tree)adaptor.create(string_literal59)
                    ;
                    adaptor.addChild(root_0, string_literal59_tree);
                    }


                    pushFollow(FOLLOW_typeBound_in_typeParameter987);
                    typeBound60=typeBound();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeBound60.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 12, typeParameter_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeParameter"


    public static class typeBound_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeBound"
    // GraphlrJava.g:474:1: typeBound : type ( '&' type )* ;
    public final GraphlrJavaParser.typeBound_return typeBound() throws RecognitionException {
        GraphlrJavaParser.typeBound_return retval = new GraphlrJavaParser.typeBound_return();
        retval.start = input.LT(1);

        int typeBound_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal62=null;
        ParserRuleReturnScope type61 =null;

        ParserRuleReturnScope type63 =null;


        Tree char_literal62_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }

            // GraphlrJava.g:475:5: ( type ( '&' type )* )
            // GraphlrJava.g:475:9: type ( '&' type )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_type_in_typeBound1019);
            type61=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type61.getTree());


            // GraphlrJava.g:476:9: ( '&' type )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AMP) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // GraphlrJava.g:476:10: '&' type
            	    {
            	    char_literal62=(Token)match(input,AMP,FOLLOW_AMP_in_typeBound1030); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal62_tree = 
            	    (Tree)adaptor.create(char_literal62)
            	    ;
            	    adaptor.addChild(root_0, char_literal62_tree);
            	    }


            	    pushFollow(FOLLOW_type_in_typeBound1032);
            	    type63=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type63.getTree());


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 13, typeBound_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeBound"


    public static class enumDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumDeclaration"
    // GraphlrJava.g:481:1: enumDeclaration : modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody ;
    public final GraphlrJavaParser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        GraphlrJavaParser.enumDeclaration_return retval = new GraphlrJavaParser.enumDeclaration_return();
        retval.start = input.LT(1);

        int enumDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal65=null;
        Token IDENTIFIER66=null;
        Token string_literal67=null;
        ParserRuleReturnScope modifiers64 =null;

        ParserRuleReturnScope typeList68 =null;

        ParserRuleReturnScope enumBody69 =null;


        Tree string_literal65_tree=null;
        Tree IDENTIFIER66_tree=null;
        Tree string_literal67_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }

            // GraphlrJava.g:482:5: ( modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody )
            // GraphlrJava.g:482:9: modifiers ( 'enum' ) IDENTIFIER ( 'implements' typeList )? enumBody
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_enumDeclaration1064);
            modifiers64=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers64.getTree());


            // GraphlrJava.g:483:9: ( 'enum' )
            // GraphlrJava.g:483:10: 'enum'
            {
            string_literal65=(Token)match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1076); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal65_tree = 
            (Tree)adaptor.create(string_literal65)
            ;
            adaptor.addChild(root_0, string_literal65_tree);
            }


            }


            IDENTIFIER66=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumDeclaration1097); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER66_tree = 
            (Tree)adaptor.create(IDENTIFIER66)
            ;
            adaptor.addChild(root_0, IDENTIFIER66_tree);
            }


            // GraphlrJava.g:486:9: ( 'implements' typeList )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==IMPLEMENTS) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // GraphlrJava.g:486:10: 'implements' typeList
                    {
                    string_literal67=(Token)match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_enumDeclaration1108); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal67_tree = 
                    (Tree)adaptor.create(string_literal67)
                    ;
                    adaptor.addChild(root_0, string_literal67_tree);
                    }


                    pushFollow(FOLLOW_typeList_in_enumDeclaration1110);
                    typeList68=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList68.getTree());


                    }
                    break;

            }


            pushFollow(FOLLOW_enumBody_in_enumDeclaration1131);
            enumBody69=enumBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBody69.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 14, enumDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"


    public static class enumBody_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumBody"
    // GraphlrJava.g:492:1: enumBody : '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' ;
    public final GraphlrJavaParser.enumBody_return enumBody() throws RecognitionException {
        GraphlrJavaParser.enumBody_return retval = new GraphlrJavaParser.enumBody_return();
        retval.start = input.LT(1);

        int enumBody_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal70=null;
        Token char_literal72=null;
        Token char_literal74=null;
        ParserRuleReturnScope enumConstants71 =null;

        ParserRuleReturnScope enumBodyDeclarations73 =null;


        Tree char_literal70_tree=null;
        Tree char_literal72_tree=null;
        Tree char_literal74_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }

            // GraphlrJava.g:493:5: ( '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}' )
            // GraphlrJava.g:493:9: '{' ( enumConstants )? ( ',' )? ( enumBodyDeclarations )? '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal70=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_enumBody1156); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal70_tree = 
            (Tree)adaptor.create(char_literal70)
            ;
            adaptor.addChild(root_0, char_literal70_tree);
            }


            // GraphlrJava.g:494:9: ( enumConstants )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==IDENTIFIER||LA23_0==MONKEYS_AT) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // GraphlrJava.g:494:10: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody1167);
                    enumConstants71=enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstants71.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:496:9: ( ',' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==COMMA) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // GraphlrJava.g:496:9: ','
                    {
                    char_literal72=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumBody1189); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal72_tree = 
                    (Tree)adaptor.create(char_literal72)
                    ;
                    adaptor.addChild(root_0, char_literal72_tree);
                    }


                    }
                    break;

            }


            // GraphlrJava.g:497:9: ( enumBodyDeclarations )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SEMI) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // GraphlrJava.g:497:10: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody1202);
                    enumBodyDeclarations73=enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumBodyDeclarations73.getTree());


                    }
                    break;

            }


            char_literal74=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_enumBody1224); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal74_tree = 
            (Tree)adaptor.create(char_literal74)
            ;
            adaptor.addChild(root_0, char_literal74_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 15, enumBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumBody"


    public static class enumConstants_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumConstants"
    // GraphlrJava.g:502:1: enumConstants : enumConstant ( ',' enumConstant )* ;
    public final GraphlrJavaParser.enumConstants_return enumConstants() throws RecognitionException {
        GraphlrJavaParser.enumConstants_return retval = new GraphlrJavaParser.enumConstants_return();
        retval.start = input.LT(1);

        int enumConstants_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal76=null;
        ParserRuleReturnScope enumConstant75 =null;

        ParserRuleReturnScope enumConstant77 =null;


        Tree char_literal76_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }

            // GraphlrJava.g:503:5: ( enumConstant ( ',' enumConstant )* )
            // GraphlrJava.g:503:9: enumConstant ( ',' enumConstant )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_enumConstant_in_enumConstants1244);
            enumConstant75=enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant75.getTree());


            // GraphlrJava.g:504:9: ( ',' enumConstant )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA) ) {
                    int LA26_1 = input.LA(2);

                    if ( (LA26_1==IDENTIFIER||LA26_1==MONKEYS_AT) ) {
                        alt26=1;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // GraphlrJava.g:504:10: ',' enumConstant
            	    {
            	    char_literal76=(Token)match(input,COMMA,FOLLOW_COMMA_in_enumConstants1255); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal76_tree = 
            	    (Tree)adaptor.create(char_literal76)
            	    ;
            	    adaptor.addChild(root_0, char_literal76_tree);
            	    }


            	    pushFollow(FOLLOW_enumConstant_in_enumConstants1257);
            	    enumConstant77=enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumConstant77.getTree());


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 16, enumConstants_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumConstants"


    public static class enumConstant_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumConstant"
    // GraphlrJava.g:512:1: enumConstant : ( annotations )? IDENTIFIER ( arguments )? ( classBody )? ;
    public final GraphlrJavaParser.enumConstant_return enumConstant() throws RecognitionException {
        GraphlrJavaParser.enumConstant_return retval = new GraphlrJavaParser.enumConstant_return();
        retval.start = input.LT(1);

        int enumConstant_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER79=null;
        ParserRuleReturnScope annotations78 =null;

        ParserRuleReturnScope arguments80 =null;

        ParserRuleReturnScope classBody81 =null;


        Tree IDENTIFIER79_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }

            // GraphlrJava.g:513:5: ( ( annotations )? IDENTIFIER ( arguments )? ( classBody )? )
            // GraphlrJava.g:513:9: ( annotations )? IDENTIFIER ( arguments )? ( classBody )?
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:513:9: ( annotations )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==MONKEYS_AT) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // GraphlrJava.g:513:10: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant1291);
                    annotations78=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotations78.getTree());


                    }
                    break;

            }


            IDENTIFIER79=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumConstant1312); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER79_tree = 
            (Tree)adaptor.create(IDENTIFIER79)
            ;
            adaptor.addChild(root_0, IDENTIFIER79_tree);
            }


            // GraphlrJava.g:516:9: ( arguments )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LPAREN) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // GraphlrJava.g:516:10: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant1323);
                    arguments80=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments80.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:518:9: ( classBody )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LBRACE) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // GraphlrJava.g:518:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant1345);
                    classBody81=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody81.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 17, enumConstant_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumConstant"


    public static class enumBodyDeclarations_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumBodyDeclarations"
    // GraphlrJava.g:524:1: enumBodyDeclarations : ';' ( classBodyDeclaration )* ;
    public final GraphlrJavaParser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        GraphlrJavaParser.enumBodyDeclarations_return retval = new GraphlrJavaParser.enumBodyDeclarations_return();
        retval.start = input.LT(1);

        int enumBodyDeclarations_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal82=null;
        ParserRuleReturnScope classBodyDeclaration83 =null;


        Tree char_literal82_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }

            // GraphlrJava.g:525:5: ( ';' ( classBodyDeclaration )* )
            // GraphlrJava.g:525:9: ';' ( classBodyDeclaration )*
            {
            root_0 = (Tree)adaptor.nil();


            char_literal82=(Token)match(input,SEMI,FOLLOW_SEMI_in_enumBodyDeclarations1386); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal82_tree = 
            (Tree)adaptor.create(char_literal82)
            ;
            adaptor.addChild(root_0, char_literal82_tree);
            }


            // GraphlrJava.g:526:9: ( classBodyDeclaration )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==ABSTRACT||LA30_0==BOOLEAN||LA30_0==BYTE||LA30_0==CHAR||LA30_0==CLASS||LA30_0==DOUBLE||LA30_0==ENUM||LA30_0==FINAL||LA30_0==FLOAT||LA30_0==IDENTIFIER||(LA30_0 >= INT && LA30_0 <= INTERFACE)||LA30_0==LBRACE||LA30_0==LONG||LA30_0==LT||(LA30_0 >= MONKEYS_AT && LA30_0 <= NATIVE)||(LA30_0 >= PRIVATE && LA30_0 <= PUBLIC)||(LA30_0 >= SEMI && LA30_0 <= SHORT)||(LA30_0 >= STATIC && LA30_0 <= STRICTFP)||LA30_0==SYNCHRONIZED||LA30_0==TRANSIENT||(LA30_0 >= VOID && LA30_0 <= VOLATILE)) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // GraphlrJava.g:526:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1398);
            	    classBodyDeclaration83=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration83.getTree());


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 18, enumBodyDeclarations_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumBodyDeclarations"


    public static class interfaceDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceDeclaration"
    // GraphlrJava.g:530:1: interfaceDeclaration : ( normalInterfaceDeclaration | annotationTypeDeclaration );
    public final GraphlrJavaParser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        GraphlrJavaParser.interfaceDeclaration_return retval = new GraphlrJavaParser.interfaceDeclaration_return();
        retval.start = input.LT(1);

        int interfaceDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope normalInterfaceDeclaration84 =null;

        ParserRuleReturnScope annotationTypeDeclaration85 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }

            // GraphlrJava.g:531:5: ( normalInterfaceDeclaration | annotationTypeDeclaration )
            int alt31=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA31_1 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA31_2 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA31_3 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA31_4 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA31_5 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA31_6 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA31_7 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA31_8 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA31_9 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA31_10 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA31_11 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA31_12 = input.LA(2);

                if ( (synpred43_GraphlrJava()) ) {
                    alt31=1;
                }
                else if ( (true) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 31, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case INTERFACE:
                {
                alt31=1;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 31, 0, input);

            	throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // GraphlrJava.g:531:9: normalInterfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1429);
                    normalInterfaceDeclaration84=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration84.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:532:9: annotationTypeDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1439);
                    annotationTypeDeclaration85=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration85.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 19, interfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceDeclaration"


    public static class normalInterfaceDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalInterfaceDeclaration"
    // GraphlrJava.g:535:1: normalInterfaceDeclaration : modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody ;
    public final GraphlrJavaParser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        GraphlrJavaParser.normalInterfaceDeclaration_return retval = new GraphlrJavaParser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);

        int normalInterfaceDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal87=null;
        Token IDENTIFIER88=null;
        Token string_literal90=null;
        ParserRuleReturnScope modifiers86 =null;

        ParserRuleReturnScope typeParameters89 =null;

        ParserRuleReturnScope typeList91 =null;

        ParserRuleReturnScope interfaceBody92 =null;


        Tree string_literal87_tree=null;
        Tree IDENTIFIER88_tree=null;
        Tree string_literal90_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }

            // GraphlrJava.g:536:5: ( modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody )
            // GraphlrJava.g:536:9: modifiers 'interface' IDENTIFIER ( typeParameters )? ( 'extends' typeList )? interfaceBody
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_normalInterfaceDeclaration1463);
            modifiers86=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers86.getTree());


            string_literal87=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_normalInterfaceDeclaration1465); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal87_tree = 
            (Tree)adaptor.create(string_literal87)
            ;
            adaptor.addChild(root_0, string_literal87_tree);
            }


            IDENTIFIER88=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1467); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER88_tree = 
            (Tree)adaptor.create(IDENTIFIER88)
            ;
            adaptor.addChild(root_0, IDENTIFIER88_tree);
            }


            // GraphlrJava.g:537:9: ( typeParameters )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LT) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // GraphlrJava.g:537:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1478);
                    typeParameters89=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters89.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:539:9: ( 'extends' typeList )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==EXTENDS) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // GraphlrJava.g:539:10: 'extends' typeList
                    {
                    string_literal90=(Token)match(input,EXTENDS,FOLLOW_EXTENDS_in_normalInterfaceDeclaration1500); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal90_tree = 
                    (Tree)adaptor.create(string_literal90)
                    ;
                    adaptor.addChild(root_0, string_literal90_tree);
                    }


                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1502);
                    typeList91=typeList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList91.getTree());


                    }
                    break;

            }


            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1523);
            interfaceBody92=interfaceBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBody92.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 20, normalInterfaceDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalInterfaceDeclaration"


    public static class typeList_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeList"
    // GraphlrJava.g:544:1: typeList : type ( ',' type )* ;
    public final GraphlrJavaParser.typeList_return typeList() throws RecognitionException {
        GraphlrJavaParser.typeList_return retval = new GraphlrJavaParser.typeList_return();
        retval.start = input.LT(1);

        int typeList_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal94=null;
        ParserRuleReturnScope type93 =null;

        ParserRuleReturnScope type95 =null;


        Tree char_literal94_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }

            // GraphlrJava.g:545:5: ( type ( ',' type )* )
            // GraphlrJava.g:545:9: type ( ',' type )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_type_in_typeList1543);
            type93=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type93.getTree());


            // GraphlrJava.g:546:9: ( ',' type )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // GraphlrJava.g:546:10: ',' type
            	    {
            	    char_literal94=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeList1554); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal94_tree = 
            	    (Tree)adaptor.create(char_literal94)
            	    ;
            	    adaptor.addChild(root_0, char_literal94_tree);
            	    }


            	    pushFollow(FOLLOW_type_in_typeList1556);
            	    type95=type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, type95.getTree());


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 21, typeList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeList"


    public static class classBody_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classBody"
    // GraphlrJava.g:550:1: classBody : '{' ( classBodyDeclaration )* '}' ;
    public final GraphlrJavaParser.classBody_return classBody() throws RecognitionException {
        GraphlrJavaParser.classBody_return retval = new GraphlrJavaParser.classBody_return();
        retval.start = input.LT(1);

        int classBody_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal96=null;
        Token char_literal98=null;
        ParserRuleReturnScope classBodyDeclaration97 =null;


        Tree char_literal96_tree=null;
        Tree char_literal98_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }

            // GraphlrJava.g:551:5: ( '{' ( classBodyDeclaration )* '}' )
            // GraphlrJava.g:551:9: '{' ( classBodyDeclaration )* '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal96=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_classBody1587); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal96_tree = 
            (Tree)adaptor.create(char_literal96)
            ;
            adaptor.addChild(root_0, char_literal96_tree);
            }


            // GraphlrJava.g:552:9: ( classBodyDeclaration )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==ABSTRACT||LA35_0==BOOLEAN||LA35_0==BYTE||LA35_0==CHAR||LA35_0==CLASS||LA35_0==DOUBLE||LA35_0==ENUM||LA35_0==FINAL||LA35_0==FLOAT||LA35_0==IDENTIFIER||(LA35_0 >= INT && LA35_0 <= INTERFACE)||LA35_0==LBRACE||LA35_0==LONG||LA35_0==LT||(LA35_0 >= MONKEYS_AT && LA35_0 <= NATIVE)||(LA35_0 >= PRIVATE && LA35_0 <= PUBLIC)||(LA35_0 >= SEMI && LA35_0 <= SHORT)||(LA35_0 >= STATIC && LA35_0 <= STRICTFP)||LA35_0==SYNCHRONIZED||LA35_0==TRANSIENT||(LA35_0 >= VOID && LA35_0 <= VOLATILE)) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // GraphlrJava.g:552:10: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1599);
            	    classBodyDeclaration97=classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBodyDeclaration97.getTree());


            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            char_literal98=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_classBody1621); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal98_tree = 
            (Tree)adaptor.create(char_literal98)
            ;
            adaptor.addChild(root_0, char_literal98_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 22, classBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classBody"


    public static class interfaceBody_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceBody"
    // GraphlrJava.g:557:1: interfaceBody : '{' ( interfaceBodyDeclaration )* '}' ;
    public final GraphlrJavaParser.interfaceBody_return interfaceBody() throws RecognitionException {
        GraphlrJavaParser.interfaceBody_return retval = new GraphlrJavaParser.interfaceBody_return();
        retval.start = input.LT(1);

        int interfaceBody_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal99=null;
        Token char_literal101=null;
        ParserRuleReturnScope interfaceBodyDeclaration100 =null;


        Tree char_literal99_tree=null;
        Tree char_literal101_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }

            // GraphlrJava.g:558:5: ( '{' ( interfaceBodyDeclaration )* '}' )
            // GraphlrJava.g:558:9: '{' ( interfaceBodyDeclaration )* '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal99=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_interfaceBody1641); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal99_tree = 
            (Tree)adaptor.create(char_literal99)
            ;
            adaptor.addChild(root_0, char_literal99_tree);
            }


            // GraphlrJava.g:559:9: ( interfaceBodyDeclaration )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==ABSTRACT||LA36_0==BOOLEAN||LA36_0==BYTE||LA36_0==CHAR||LA36_0==CLASS||LA36_0==DOUBLE||LA36_0==ENUM||LA36_0==FINAL||LA36_0==FLOAT||LA36_0==IDENTIFIER||(LA36_0 >= INT && LA36_0 <= INTERFACE)||LA36_0==LONG||LA36_0==LT||(LA36_0 >= MONKEYS_AT && LA36_0 <= NATIVE)||(LA36_0 >= PRIVATE && LA36_0 <= PUBLIC)||(LA36_0 >= SEMI && LA36_0 <= SHORT)||(LA36_0 >= STATIC && LA36_0 <= STRICTFP)||LA36_0==SYNCHRONIZED||LA36_0==TRANSIENT||(LA36_0 >= VOID && LA36_0 <= VOLATILE)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // GraphlrJava.g:559:10: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1653);
            	    interfaceBodyDeclaration100=interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceBodyDeclaration100.getTree());


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            char_literal101=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_interfaceBody1675); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal101_tree = 
            (Tree)adaptor.create(char_literal101)
            ;
            adaptor.addChild(root_0, char_literal101_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 23, interfaceBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceBody"


    public static class classBodyDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classBodyDeclaration"
    // GraphlrJava.g:564:1: classBodyDeclaration : ( ';' | ( 'static' )? block | memberDecl );
    public final GraphlrJavaParser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        GraphlrJavaParser.classBodyDeclaration_return retval = new GraphlrJavaParser.classBodyDeclaration_return();
        retval.start = input.LT(1);

        int classBodyDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal102=null;
        Token string_literal103=null;
        ParserRuleReturnScope block104 =null;

        ParserRuleReturnScope memberDecl105 =null;


        Tree char_literal102_tree=null;
        Tree string_literal103_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }

            // GraphlrJava.g:565:5: ( ';' | ( 'static' )? block | memberDecl )
            int alt38=3;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt38=1;
                }
                break;
            case STATIC:
                {
                int LA38_2 = input.LA(2);

                if ( (LA38_2==LBRACE) ) {
                    alt38=2;
                }
                else if ( (LA38_2==ABSTRACT||LA38_2==BOOLEAN||LA38_2==BYTE||LA38_2==CHAR||LA38_2==CLASS||LA38_2==DOUBLE||LA38_2==ENUM||LA38_2==FINAL||LA38_2==FLOAT||LA38_2==IDENTIFIER||(LA38_2 >= INT && LA38_2 <= INTERFACE)||LA38_2==LONG||LA38_2==LT||(LA38_2 >= MONKEYS_AT && LA38_2 <= NATIVE)||(LA38_2 >= PRIVATE && LA38_2 <= PUBLIC)||LA38_2==SHORT||(LA38_2 >= STATIC && LA38_2 <= STRICTFP)||LA38_2==SYNCHRONIZED||LA38_2==TRANSIENT||(LA38_2 >= VOID && LA38_2 <= VOLATILE)) ) {
                    alt38=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 38, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case LBRACE:
                {
                alt38=2;
                }
                break;
            case ABSTRACT:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CLASS:
            case DOUBLE:
            case ENUM:
            case FINAL:
            case FLOAT:
            case IDENTIFIER:
            case INT:
            case INTERFACE:
            case LONG:
            case LT:
            case MONKEYS_AT:
            case NATIVE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case SHORT:
            case STRICTFP:
            case SYNCHRONIZED:
            case TRANSIENT:
            case VOID:
            case VOLATILE:
                {
                alt38=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 38, 0, input);

            	throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // GraphlrJava.g:565:9: ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal102=(Token)match(input,SEMI,FOLLOW_SEMI_in_classBodyDeclaration1695); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal102_tree = 
                    (Tree)adaptor.create(char_literal102)
                    ;
                    adaptor.addChild(root_0, char_literal102_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:566:9: ( 'static' )? block
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:566:9: ( 'static' )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==STATIC) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // GraphlrJava.g:566:10: 'static'
                            {
                            string_literal103=(Token)match(input,STATIC,FOLLOW_STATIC_in_classBodyDeclaration1706); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal103_tree = 
                            (Tree)adaptor.create(string_literal103)
                            ;
                            adaptor.addChild(root_0, string_literal103_tree);
                            }


                            }
                            break;

                    }


                    pushFollow(FOLLOW_block_in_classBodyDeclaration1728);
                    block104=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block104.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:569:9: memberDecl
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration1738);
                    memberDecl105=memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, memberDecl105.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 24, classBodyDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classBodyDeclaration"


    public static class memberDecl_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "memberDecl"
    // GraphlrJava.g:572:1: memberDecl : ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration );
    public final GraphlrJavaParser.memberDecl_return memberDecl() throws RecognitionException {
        GraphlrJavaParser.memberDecl_return retval = new GraphlrJavaParser.memberDecl_return();
        retval.start = input.LT(1);

        int memberDecl_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope fieldDeclaration106 =null;

        ParserRuleReturnScope methodDeclaration107 =null;

        ParserRuleReturnScope classDeclaration108 =null;

        ParserRuleReturnScope interfaceDeclaration109 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }

            // GraphlrJava.g:573:5: ( fieldDeclaration | methodDeclaration | classDeclaration | interfaceDeclaration )
            int alt39=4;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA39_1 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA39_2 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA39_3 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA39_4 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA39_5 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA39_6 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA39_7 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA39_8 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA39_9 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA39_10 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA39_11 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA39_12 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else if ( (synpred54_GraphlrJava()) ) {
                    alt39=3;
                }
                else if ( (true) ) {
                    alt39=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA39_13 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 13, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA39_14 = input.LA(2);

                if ( (synpred52_GraphlrJava()) ) {
                    alt39=1;
                }
                else if ( (synpred53_GraphlrJava()) ) {
                    alt39=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 39, 14, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case LT:
            case VOID:
                {
                alt39=2;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt39=3;
                }
                break;
            case INTERFACE:
                {
                alt39=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 39, 0, input);

            	throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // GraphlrJava.g:573:10: fieldDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_fieldDeclaration_in_memberDecl1759);
                    fieldDeclaration106=fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, fieldDeclaration106.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:574:10: methodDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_methodDeclaration_in_memberDecl1770);
                    methodDeclaration107=methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, methodDeclaration107.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:575:10: classDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_memberDecl1781);
                    classDeclaration108=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration108.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:576:10: interfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl1792);
                    interfaceDeclaration109=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration109.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 25, memberDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "memberDecl"


    public static class methodDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodDeclaration"
    // GraphlrJava.g:580:1: methodDeclaration : ( modifiers ( typeParameters )? name= IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) name= IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) );
    public final GraphlrJavaParser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        GraphlrJavaParser.methodDeclaration_return retval = new GraphlrJavaParser.methodDeclaration_return();
        retval.start = input.LT(1);

        int methodDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token name=null;
        Token string_literal113=null;
        Token char_literal115=null;
        Token char_literal118=null;
        Token string_literal122=null;
        Token char_literal124=null;
        Token char_literal125=null;
        Token string_literal126=null;
        Token char_literal129=null;
        ParserRuleReturnScope modifiers110 =null;

        ParserRuleReturnScope typeParameters111 =null;

        ParserRuleReturnScope formalParameters112 =null;

        ParserRuleReturnScope qualifiedNameList114 =null;

        ParserRuleReturnScope explicitConstructorInvocation116 =null;

        ParserRuleReturnScope blockStatement117 =null;

        ParserRuleReturnScope modifiers119 =null;

        ParserRuleReturnScope typeParameters120 =null;

        ParserRuleReturnScope type121 =null;

        ParserRuleReturnScope formalParameters123 =null;

        ParserRuleReturnScope qualifiedNameList127 =null;

        ParserRuleReturnScope block128 =null;


        Tree name_tree=null;
        Tree string_literal113_tree=null;
        Tree char_literal115_tree=null;
        Tree char_literal118_tree=null;
        Tree string_literal122_tree=null;
        Tree char_literal124_tree=null;
        Tree char_literal125_tree=null;
        Tree string_literal126_tree=null;
        Tree char_literal129_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }

            // GraphlrJava.g:581:5: ( modifiers ( typeParameters )? name= IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' | modifiers ( typeParameters )? ( type | 'void' ) name= IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' ) )
            int alt49=2;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA49_1 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA49_2 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA49_3 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA49_4 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA49_5 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA49_6 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA49_7 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA49_8 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA49_9 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA49_10 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA49_11 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA49_12 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case LT:
                {
                int LA49_13 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 13, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA49_14 = input.LA(2);

                if ( (synpred59_GraphlrJava()) ) {
                    alt49=1;
                }
                else if ( (true) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 49, 14, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case VOID:
                {
                alt49=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 49, 0, input);

            	throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // GraphlrJava.g:583:10: modifiers ( typeParameters )? name= IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_modifiers_in_methodDeclaration1830);
                    modifiers110=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers110.getTree());


                    // GraphlrJava.g:584:9: ( typeParameters )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==LT) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // GraphlrJava.g:584:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclaration1841);
                            typeParameters111=typeParameters();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters111.getTree());


                            }
                            break;

                    }


                    name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration1864); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    name_tree = 
                    (Tree)adaptor.create(name)
                    ;
                    adaptor.addChild(root_0, name_tree);
                    }


                    if ( state.backtracking==0 ) {
                            Transaction transaction = db.beginTx();
                            try {
                                Node node = db.createNode();
                                node.setProperty("type", "method");
                                node.setProperty("name", (name!=null?name.getText():null));
                                id2Tree.put(node.getId(), name_tree);
                                Node cls = clazzes.peek();
                                cls.createRelationshipTo(node, Rels.IMPLEMENTS);
                                
                                transaction.success();
                            }
                            finally {
                                transaction.finish();
                            }
                        }

                    pushFollow(FOLLOW_formalParameters_in_methodDeclaration1880);
                    formalParameters112=formalParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters112.getTree());


                    // GraphlrJava.g:604:9: ( 'throws' qualifiedNameList )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==THROWS) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // GraphlrJava.g:604:10: 'throws' qualifiedNameList
                            {
                            string_literal113=(Token)match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration1891); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal113_tree = 
                            (Tree)adaptor.create(string_literal113)
                            ;
                            adaptor.addChild(root_0, string_literal113_tree);
                            }


                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration1893);
                            qualifiedNameList114=qualifiedNameList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList114.getTree());


                            }
                            break;

                    }


                    char_literal115=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_methodDeclaration1914); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal115_tree = 
                    (Tree)adaptor.create(char_literal115)
                    ;
                    adaptor.addChild(root_0, char_literal115_tree);
                    }


                    // GraphlrJava.g:607:9: ( explicitConstructorInvocation )?
                    int alt42=2;
                    switch ( input.LA(1) ) {
                        case LT:
                            {
                            alt42=1;
                            }
                            break;
                        case THIS:
                            {
                            int LA42_2 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            int LA42_3 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case SUPER:
                            {
                            int LA42_4 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA42_5 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case CHARLITERAL:
                        case DOUBLELITERAL:
                        case FALSE:
                        case FLOATLITERAL:
                        case INTLITERAL:
                        case LONGLITERAL:
                        case NULL:
                        case STRINGLITERAL:
                        case TRUE:
                            {
                            int LA42_6 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case NEW:
                            {
                            int LA42_7 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case BOOLEAN:
                        case BYTE:
                        case CHAR:
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case LONG:
                        case SHORT:
                            {
                            int LA42_8 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                        case VOID:
                            {
                            int LA42_9 = input.LA(2);

                            if ( (synpred57_GraphlrJava()) ) {
                                alt42=1;
                            }
                            }
                            break;
                    }

                    switch (alt42) {
                        case 1 :
                            // GraphlrJava.g:607:10: explicitConstructorInvocation
                            {
                            pushFollow(FOLLOW_explicitConstructorInvocation_in_methodDeclaration1926);
                            explicitConstructorInvocation116=explicitConstructorInvocation();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, explicitConstructorInvocation116.getTree());


                            }
                            break;

                    }


                    // GraphlrJava.g:609:9: ( blockStatement )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==ABSTRACT||(LA43_0 >= ASSERT && LA43_0 <= BANG)||(LA43_0 >= BOOLEAN && LA43_0 <= BYTE)||(LA43_0 >= CHAR && LA43_0 <= CLASS)||LA43_0==CONTINUE||LA43_0==DO||(LA43_0 >= DOUBLE && LA43_0 <= DOUBLELITERAL)||LA43_0==ENUM||(LA43_0 >= FALSE && LA43_0 <= FINAL)||(LA43_0 >= FLOAT && LA43_0 <= FOR)||(LA43_0 >= IDENTIFIER && LA43_0 <= IF)||(LA43_0 >= INT && LA43_0 <= INTLITERAL)||LA43_0==LBRACE||(LA43_0 >= LONG && LA43_0 <= LT)||(LA43_0 >= MONKEYS_AT && LA43_0 <= NULL)||LA43_0==PLUS||(LA43_0 >= PLUSPLUS && LA43_0 <= PUBLIC)||LA43_0==RETURN||(LA43_0 >= SEMI && LA43_0 <= SHORT)||(LA43_0 >= STATIC && LA43_0 <= SUB)||(LA43_0 >= SUBSUB && LA43_0 <= SYNCHRONIZED)||(LA43_0 >= THIS && LA43_0 <= THROW)||(LA43_0 >= TILDE && LA43_0 <= WHILE)) ) {
                            alt43=1;
                        }


                        switch (alt43) {
                    	case 1 :
                    	    // GraphlrJava.g:609:10: blockStatement
                    	    {
                    	    pushFollow(FOLLOW_blockStatement_in_methodDeclaration1948);
                    	    blockStatement117=blockStatement();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement117.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);


                    char_literal118=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_methodDeclaration1969); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal118_tree = 
                    (Tree)adaptor.create(char_literal118)
                    ;
                    adaptor.addChild(root_0, char_literal118_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:612:9: modifiers ( typeParameters )? ( type | 'void' ) name= IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ( block | ';' )
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_modifiers_in_methodDeclaration1979);
                    modifiers119=modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers119.getTree());


                    // GraphlrJava.g:613:9: ( typeParameters )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==LT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // GraphlrJava.g:613:10: typeParameters
                            {
                            pushFollow(FOLLOW_typeParameters_in_methodDeclaration1990);
                            typeParameters120=typeParameters();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters120.getTree());


                            }
                            break;

                    }


                    // GraphlrJava.g:615:9: ( type | 'void' )
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==BOOLEAN||LA45_0==BYTE||LA45_0==CHAR||LA45_0==DOUBLE||LA45_0==FLOAT||LA45_0==IDENTIFIER||LA45_0==INT||LA45_0==LONG||LA45_0==SHORT) ) {
                        alt45=1;
                    }
                    else if ( (LA45_0==VOID) ) {
                        alt45=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	NoViableAltException nvae =
                    		new NoViableAltException("", 45, 0, input);

                    	throw nvae;
                    }
                    switch (alt45) {
                        case 1 :
                            // GraphlrJava.g:615:10: type
                            {
                            pushFollow(FOLLOW_type_in_methodDeclaration2012);
                            type121=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type121.getTree());


                            }
                            break;
                        case 2 :
                            // GraphlrJava.g:616:13: 'void'
                            {
                            string_literal122=(Token)match(input,VOID,FOLLOW_VOID_in_methodDeclaration2026); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal122_tree = 
                            (Tree)adaptor.create(string_literal122)
                            ;
                            adaptor.addChild(root_0, string_literal122_tree);
                            }


                            }
                            break;

                    }


                    name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodDeclaration2048); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    name_tree = 
                    (Tree)adaptor.create(name)
                    ;
                    adaptor.addChild(root_0, name_tree);
                    }


                    if ( state.backtracking==0 ) {
                            Transaction transaction = db.beginTx();
                            try {
                                Node node = db.createNode();
                                node.setProperty("type", "method");
                                node.setProperty("name", (name!=null?name.getText():null));
                                id2Tree.put(node.getId(), name_tree);
                                Node cls = clazzes.peek();
                                cls.createRelationshipTo(node, Rels.IMPLEMENTS);
                                
                                transaction.success();
                            }
                            finally {
                                transaction.finish();
                            }
                        }

                    pushFollow(FOLLOW_formalParameters_in_methodDeclaration2064);
                    formalParameters123=formalParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters123.getTree());


                    // GraphlrJava.g:636:9: ( '[' ']' )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==LBRACKET) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                    	case 1 :
                    	    // GraphlrJava.g:636:10: '[' ']'
                    	    {
                    	    char_literal124=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_methodDeclaration2075); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal124_tree = 
                    	    (Tree)adaptor.create(char_literal124)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal124_tree);
                    	    }


                    	    char_literal125=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_methodDeclaration2077); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal125_tree = 
                    	    (Tree)adaptor.create(char_literal125)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal125_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop46;
                        }
                    } while (true);


                    // GraphlrJava.g:638:9: ( 'throws' qualifiedNameList )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==THROWS) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // GraphlrJava.g:638:10: 'throws' qualifiedNameList
                            {
                            string_literal126=(Token)match(input,THROWS,FOLLOW_THROWS_in_methodDeclaration2099); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal126_tree = 
                            (Tree)adaptor.create(string_literal126)
                            ;
                            adaptor.addChild(root_0, string_literal126_tree);
                            }


                            pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaration2101);
                            qualifiedNameList127=qualifiedNameList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList127.getTree());


                            }
                            break;

                    }


                    // GraphlrJava.g:640:9: ( block | ';' )
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==LBRACE) ) {
                        alt48=1;
                    }
                    else if ( (LA48_0==SEMI) ) {
                        alt48=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	NoViableAltException nvae =
                    		new NoViableAltException("", 48, 0, input);

                    	throw nvae;
                    }
                    switch (alt48) {
                        case 1 :
                            // GraphlrJava.g:641:13: block
                            {
                            pushFollow(FOLLOW_block_in_methodDeclaration2156);
                            block128=block();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, block128.getTree());


                            }
                            break;
                        case 2 :
                            // GraphlrJava.g:642:13: ';'
                            {
                            char_literal129=(Token)match(input,SEMI,FOLLOW_SEMI_in_methodDeclaration2170); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal129_tree = 
                            (Tree)adaptor.create(char_literal129)
                            ;
                            adaptor.addChild(root_0, char_literal129_tree);
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 26, methodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"


    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldDeclaration"
    // GraphlrJava.g:647:1: fieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final GraphlrJavaParser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        GraphlrJavaParser.fieldDeclaration_return retval = new GraphlrJavaParser.fieldDeclaration_return();
        retval.start = input.LT(1);

        int fieldDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal133=null;
        Token char_literal135=null;
        ParserRuleReturnScope modifiers130 =null;

        ParserRuleReturnScope type131 =null;

        ParserRuleReturnScope variableDeclarator132 =null;

        ParserRuleReturnScope variableDeclarator134 =null;


        Tree char_literal133_tree=null;
        Tree char_literal135_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }

            // GraphlrJava.g:648:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // GraphlrJava.g:648:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_fieldDeclaration2202);
            modifiers130=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers130.getTree());


            pushFollow(FOLLOW_type_in_fieldDeclaration2212);
            type131=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type131.getTree());


            pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2222);
            variableDeclarator132=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator132.getTree());


            // GraphlrJava.g:651:9: ( ',' variableDeclarator )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // GraphlrJava.g:651:10: ',' variableDeclarator
            	    {
            	    char_literal133=(Token)match(input,COMMA,FOLLOW_COMMA_in_fieldDeclaration2233); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal133_tree = 
            	    (Tree)adaptor.create(char_literal133)
            	    ;
            	    adaptor.addChild(root_0, char_literal133_tree);
            	    }


            	    pushFollow(FOLLOW_variableDeclarator_in_fieldDeclaration2235);
            	    variableDeclarator134=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator134.getTree());


            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);


            char_literal135=(Token)match(input,SEMI,FOLLOW_SEMI_in_fieldDeclaration2256); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal135_tree = 
            (Tree)adaptor.create(char_literal135)
            ;
            adaptor.addChild(root_0, char_literal135_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 27, fieldDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "fieldDeclaration"


    public static class variableDeclarator_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableDeclarator"
    // GraphlrJava.g:656:1: variableDeclarator : IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? ;
    public final GraphlrJavaParser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        GraphlrJavaParser.variableDeclarator_return retval = new GraphlrJavaParser.variableDeclarator_return();
        retval.start = input.LT(1);

        int variableDeclarator_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER136=null;
        Token char_literal137=null;
        Token char_literal138=null;
        Token char_literal139=null;
        ParserRuleReturnScope variableInitializer140 =null;


        Tree IDENTIFIER136_tree=null;
        Tree char_literal137_tree=null;
        Tree char_literal138_tree=null;
        Tree char_literal139_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }

            // GraphlrJava.g:657:5: ( IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )? )
            // GraphlrJava.g:657:9: IDENTIFIER ( '[' ']' )* ( '=' variableInitializer )?
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER136=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variableDeclarator2276); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER136_tree = 
            (Tree)adaptor.create(IDENTIFIER136)
            ;
            adaptor.addChild(root_0, IDENTIFIER136_tree);
            }


            // GraphlrJava.g:658:9: ( '[' ']' )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==LBRACKET) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // GraphlrJava.g:658:10: '[' ']'
            	    {
            	    char_literal137=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_variableDeclarator2287); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal137_tree = 
            	    (Tree)adaptor.create(char_literal137)
            	    ;
            	    adaptor.addChild(root_0, char_literal137_tree);
            	    }


            	    char_literal138=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_variableDeclarator2289); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal138_tree = 
            	    (Tree)adaptor.create(char_literal138)
            	    ;
            	    adaptor.addChild(root_0, char_literal138_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            // GraphlrJava.g:660:9: ( '=' variableInitializer )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==EQ) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // GraphlrJava.g:660:10: '=' variableInitializer
                    {
                    char_literal139=(Token)match(input,EQ,FOLLOW_EQ_in_variableDeclarator2311); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal139_tree = 
                    (Tree)adaptor.create(char_literal139)
                    ;
                    adaptor.addChild(root_0, char_literal139_tree);
                    }


                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2313);
                    variableInitializer140=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer140.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 28, variableDeclarator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableDeclarator"


    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceBodyDeclaration"
    // GraphlrJava.g:667:1: interfaceBodyDeclaration : ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' );
    public final GraphlrJavaParser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        GraphlrJavaParser.interfaceBodyDeclaration_return retval = new GraphlrJavaParser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);

        int interfaceBodyDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal145=null;
        ParserRuleReturnScope interfaceFieldDeclaration141 =null;

        ParserRuleReturnScope interfaceMethodDeclaration142 =null;

        ParserRuleReturnScope interfaceDeclaration143 =null;

        ParserRuleReturnScope classDeclaration144 =null;


        Tree char_literal145_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }

            // GraphlrJava.g:668:5: ( interfaceFieldDeclaration | interfaceMethodDeclaration | interfaceDeclaration | classDeclaration | ';' )
            int alt53=5;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA53_1 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA53_2 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA53_3 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA53_4 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA53_5 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA53_6 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA53_7 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA53_8 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA53_9 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA53_10 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA53_11 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA53_12 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else if ( (synpred70_GraphlrJava()) ) {
                    alt53=3;
                }
                else if ( (synpred71_GraphlrJava()) ) {
                    alt53=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA53_13 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 13, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA53_14 = input.LA(2);

                if ( (synpred68_GraphlrJava()) ) {
                    alt53=1;
                }
                else if ( (synpred69_GraphlrJava()) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 53, 14, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case LT:
            case VOID:
                {
                alt53=2;
                }
                break;
            case INTERFACE:
                {
                alt53=3;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt53=4;
                }
                break;
            case SEMI:
                {
                alt53=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 53, 0, input);

            	throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // GraphlrJava.g:669:9: interfaceFieldDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2352);
                    interfaceFieldDeclaration141=interfaceFieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration141.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:670:9: interfaceMethodDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2362);
                    interfaceMethodDeclaration142=interfaceMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceMethodDeclaration142.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:671:9: interfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2372);
                    interfaceDeclaration143=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceDeclaration143.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:672:9: classDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classDeclaration_in_interfaceBodyDeclaration2382);
                    classDeclaration144=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classDeclaration144.getTree());


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:673:9: ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal145=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceBodyDeclaration2392); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal145_tree = 
                    (Tree)adaptor.create(char_literal145)
                    ;
                    adaptor.addChild(root_0, char_literal145_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 29, interfaceBodyDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceBodyDeclaration"


    public static class interfaceMethodDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceMethodDeclaration"
    // GraphlrJava.g:676:1: interfaceMethodDeclaration : modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' ;
    public final GraphlrJavaParser.interfaceMethodDeclaration_return interfaceMethodDeclaration() throws RecognitionException {
        GraphlrJavaParser.interfaceMethodDeclaration_return retval = new GraphlrJavaParser.interfaceMethodDeclaration_return();
        retval.start = input.LT(1);

        int interfaceMethodDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal149=null;
        Token IDENTIFIER150=null;
        Token char_literal152=null;
        Token char_literal153=null;
        Token string_literal154=null;
        Token char_literal156=null;
        ParserRuleReturnScope modifiers146 =null;

        ParserRuleReturnScope typeParameters147 =null;

        ParserRuleReturnScope type148 =null;

        ParserRuleReturnScope formalParameters151 =null;

        ParserRuleReturnScope qualifiedNameList155 =null;


        Tree string_literal149_tree=null;
        Tree IDENTIFIER150_tree=null;
        Tree char_literal152_tree=null;
        Tree char_literal153_tree=null;
        Tree string_literal154_tree=null;
        Tree char_literal156_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }

            // GraphlrJava.g:677:5: ( modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';' )
            // GraphlrJava.g:677:9: modifiers ( typeParameters )? ( type | 'void' ) IDENTIFIER formalParameters ( '[' ']' )* ( 'throws' qualifiedNameList )? ';'
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceMethodDeclaration2412);
            modifiers146=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers146.getTree());


            // GraphlrJava.g:678:9: ( typeParameters )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LT) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // GraphlrJava.g:678:10: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_interfaceMethodDeclaration2423);
                    typeParameters147=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters147.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:680:9: ( type | 'void' )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==BOOLEAN||LA55_0==BYTE||LA55_0==CHAR||LA55_0==DOUBLE||LA55_0==FLOAT||LA55_0==IDENTIFIER||LA55_0==INT||LA55_0==LONG||LA55_0==SHORT) ) {
                alt55=1;
            }
            else if ( (LA55_0==VOID) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 55, 0, input);

            	throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // GraphlrJava.g:680:10: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceMethodDeclaration2445);
                    type148=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type148.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:681:10: 'void'
                    {
                    string_literal149=(Token)match(input,VOID,FOLLOW_VOID_in_interfaceMethodDeclaration2456); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal149_tree = 
                    (Tree)adaptor.create(string_literal149)
                    ;
                    adaptor.addChild(root_0, string_literal149_tree);
                    }


                    }
                    break;

            }


            IDENTIFIER150=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2476); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER150_tree = 
            (Tree)adaptor.create(IDENTIFIER150)
            ;
            adaptor.addChild(root_0, IDENTIFIER150_tree);
            }


            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaration2486);
            formalParameters151=formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameters151.getTree());


            // GraphlrJava.g:685:9: ( '[' ']' )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==LBRACKET) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // GraphlrJava.g:685:10: '[' ']'
            	    {
            	    char_literal152=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_interfaceMethodDeclaration2497); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal152_tree = 
            	    (Tree)adaptor.create(char_literal152)
            	    ;
            	    adaptor.addChild(root_0, char_literal152_tree);
            	    }


            	    char_literal153=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_interfaceMethodDeclaration2499); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal153_tree = 
            	    (Tree)adaptor.create(char_literal153)
            	    ;
            	    adaptor.addChild(root_0, char_literal153_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);


            // GraphlrJava.g:687:9: ( 'throws' qualifiedNameList )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==THROWS) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // GraphlrJava.g:687:10: 'throws' qualifiedNameList
                    {
                    string_literal154=(Token)match(input,THROWS,FOLLOW_THROWS_in_interfaceMethodDeclaration2521); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal154_tree = 
                    (Tree)adaptor.create(string_literal154)
                    ;
                    adaptor.addChild(root_0, string_literal154_tree);
                    }


                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2523);
                    qualifiedNameList155=qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedNameList155.getTree());


                    }
                    break;

            }


            char_literal156=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceMethodDeclaration2536); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal156_tree = 
            (Tree)adaptor.create(char_literal156)
            ;
            adaptor.addChild(root_0, char_literal156_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 30, interfaceMethodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceMethodDeclaration"


    public static class interfaceFieldDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceFieldDeclaration"
    // GraphlrJava.g:696:1: interfaceFieldDeclaration : modifiers type variableDeclarator ( ',' variableDeclarator )* ';' ;
    public final GraphlrJavaParser.interfaceFieldDeclaration_return interfaceFieldDeclaration() throws RecognitionException {
        GraphlrJavaParser.interfaceFieldDeclaration_return retval = new GraphlrJavaParser.interfaceFieldDeclaration_return();
        retval.start = input.LT(1);

        int interfaceFieldDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal160=null;
        Token char_literal162=null;
        ParserRuleReturnScope modifiers157 =null;

        ParserRuleReturnScope type158 =null;

        ParserRuleReturnScope variableDeclarator159 =null;

        ParserRuleReturnScope variableDeclarator161 =null;


        Tree char_literal160_tree=null;
        Tree char_literal162_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }

            // GraphlrJava.g:697:5: ( modifiers type variableDeclarator ( ',' variableDeclarator )* ';' )
            // GraphlrJava.g:697:9: modifiers type variableDeclarator ( ',' variableDeclarator )* ';'
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceFieldDeclaration2558);
            modifiers157=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers157.getTree());


            pushFollow(FOLLOW_type_in_interfaceFieldDeclaration2560);
            type158=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type158.getTree());


            pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2562);
            variableDeclarator159=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator159.getTree());


            // GraphlrJava.g:698:9: ( ',' variableDeclarator )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // GraphlrJava.g:698:10: ',' variableDeclarator
            	    {
            	    char_literal160=(Token)match(input,COMMA,FOLLOW_COMMA_in_interfaceFieldDeclaration2573); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal160_tree = 
            	    (Tree)adaptor.create(char_literal160)
            	    ;
            	    adaptor.addChild(root_0, char_literal160_tree);
            	    }


            	    pushFollow(FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2575);
            	    variableDeclarator161=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator161.getTree());


            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);


            char_literal162=(Token)match(input,SEMI,FOLLOW_SEMI_in_interfaceFieldDeclaration2596); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal162_tree = 
            (Tree)adaptor.create(char_literal162)
            ;
            adaptor.addChild(root_0, char_literal162_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 31, interfaceFieldDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceFieldDeclaration"


    public static class type_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // GraphlrJava.g:704:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final GraphlrJavaParser.type_return type() throws RecognitionException {
        GraphlrJavaParser.type_return retval = new GraphlrJavaParser.type_return();
        retval.start = input.LT(1);

        int type_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal164=null;
        Token char_literal165=null;
        Token char_literal167=null;
        Token char_literal168=null;
        ParserRuleReturnScope classOrInterfaceType163 =null;

        ParserRuleReturnScope primitiveType166 =null;


        Tree char_literal164_tree=null;
        Tree char_literal165_tree=null;
        Tree char_literal167_tree=null;
        Tree char_literal168_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }

            // GraphlrJava.g:705:5: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==IDENTIFIER) ) {
                alt61=1;
            }
            else if ( (LA61_0==BOOLEAN||LA61_0==BYTE||LA61_0==CHAR||LA61_0==DOUBLE||LA61_0==FLOAT||LA61_0==INT||LA61_0==LONG||LA61_0==SHORT) ) {
                alt61=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 61, 0, input);

            	throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // GraphlrJava.g:705:9: classOrInterfaceType ( '[' ']' )*
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceType_in_type2617);
                    classOrInterfaceType163=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType163.getTree());


                    // GraphlrJava.g:706:9: ( '[' ']' )*
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==LBRACKET) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // GraphlrJava.g:706:10: '[' ']'
                    	    {
                    	    char_literal164=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_type2628); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal164_tree = 
                    	    (Tree)adaptor.create(char_literal164)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal164_tree);
                    	    }


                    	    char_literal165=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_type2630); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal165_tree = 
                    	    (Tree)adaptor.create(char_literal165)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal165_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop59;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:708:9: primitiveType ( '[' ']' )*
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_type2651);
                    primitiveType166=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType166.getTree());


                    // GraphlrJava.g:709:9: ( '[' ']' )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LBRACKET) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // GraphlrJava.g:709:10: '[' ']'
                    	    {
                    	    char_literal167=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_type2662); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal167_tree = 
                    	    (Tree)adaptor.create(char_literal167)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal167_tree);
                    	    }


                    	    char_literal168=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_type2664); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal168_tree = 
                    	    (Tree)adaptor.create(char_literal168)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal168_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 32, type_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "type"


    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classOrInterfaceType"
    // GraphlrJava.g:714:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final GraphlrJavaParser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        GraphlrJavaParser.classOrInterfaceType_return retval = new GraphlrJavaParser.classOrInterfaceType_return();
        retval.start = input.LT(1);

        int classOrInterfaceType_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER169=null;
        Token char_literal171=null;
        Token IDENTIFIER172=null;
        ParserRuleReturnScope typeArguments170 =null;

        ParserRuleReturnScope typeArguments173 =null;


        Tree IDENTIFIER169_tree=null;
        Tree char_literal171_tree=null;
        Tree IDENTIFIER172_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }

            // GraphlrJava.g:715:5: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            // GraphlrJava.g:715:9: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER169=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2696); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER169_tree = 
            (Tree)adaptor.create(IDENTIFIER169)
            ;
            adaptor.addChild(root_0, IDENTIFIER169_tree);
            }


            // GraphlrJava.g:716:9: ( typeArguments )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LT) ) {
                int LA62_1 = input.LA(2);

                if ( (LA62_1==BOOLEAN||LA62_1==BYTE||LA62_1==CHAR||LA62_1==DOUBLE||LA62_1==FLOAT||LA62_1==IDENTIFIER||LA62_1==INT||LA62_1==LONG||LA62_1==QUES||LA62_1==SHORT) ) {
                    alt62=1;
                }
            }
            switch (alt62) {
                case 1 :
                    // GraphlrJava.g:716:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2707);
                    typeArguments170=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments170.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:718:9: ( '.' IDENTIFIER ( typeArguments )? )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==DOT) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // GraphlrJava.g:718:10: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    char_literal171=(Token)match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType2729); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal171_tree = 
            	    (Tree)adaptor.create(char_literal171)
            	    ;
            	    adaptor.addChild(root_0, char_literal171_tree);
            	    }


            	    IDENTIFIER172=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType2731); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER172_tree = 
            	    (Tree)adaptor.create(IDENTIFIER172)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER172_tree);
            	    }


            	    // GraphlrJava.g:719:13: ( typeArguments )?
            	    int alt63=2;
            	    int LA63_0 = input.LA(1);

            	    if ( (LA63_0==LT) ) {
            	        int LA63_1 = input.LA(2);

            	        if ( (LA63_1==BOOLEAN||LA63_1==BYTE||LA63_1==CHAR||LA63_1==DOUBLE||LA63_1==FLOAT||LA63_1==IDENTIFIER||LA63_1==INT||LA63_1==LONG||LA63_1==QUES||LA63_1==SHORT) ) {
            	            alt63=1;
            	        }
            	    }
            	    switch (alt63) {
            	        case 1 :
            	            // GraphlrJava.g:719:14: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2746);
            	            typeArguments173=typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments173.getTree());


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 33, classOrInterfaceType_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceType"


    public static class primitiveType_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primitiveType"
    // GraphlrJava.g:724:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final GraphlrJavaParser.primitiveType_return primitiveType() throws RecognitionException {
        GraphlrJavaParser.primitiveType_return retval = new GraphlrJavaParser.primitiveType_return();
        retval.start = input.LT(1);

        int primitiveType_StartIndex = input.index();

        Tree root_0 = null;

        Token set174=null;

        Tree set174_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }

            // GraphlrJava.g:725:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // GraphlrJava.g:
            {
            root_0 = (Tree)adaptor.nil();


            set174=(Token)input.LT(1);

            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (Tree)adaptor.create(set174)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 34, primitiveType_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "primitiveType"


    public static class typeArguments_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeArguments"
    // GraphlrJava.g:735:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final GraphlrJavaParser.typeArguments_return typeArguments() throws RecognitionException {
        GraphlrJavaParser.typeArguments_return retval = new GraphlrJavaParser.typeArguments_return();
        retval.start = input.LT(1);

        int typeArguments_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal175=null;
        Token char_literal177=null;
        Token char_literal179=null;
        ParserRuleReturnScope typeArgument176 =null;

        ParserRuleReturnScope typeArgument178 =null;


        Tree char_literal175_tree=null;
        Tree char_literal177_tree=null;
        Tree char_literal179_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }

            // GraphlrJava.g:736:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // GraphlrJava.g:736:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal175=(Token)match(input,LT,FOLLOW_LT_in_typeArguments2883); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal175_tree = 
            (Tree)adaptor.create(char_literal175)
            ;
            adaptor.addChild(root_0, char_literal175_tree);
            }


            pushFollow(FOLLOW_typeArgument_in_typeArguments2885);
            typeArgument176=typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument176.getTree());


            // GraphlrJava.g:737:9: ( ',' typeArgument )*
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==COMMA) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // GraphlrJava.g:737:10: ',' typeArgument
            	    {
            	    char_literal177=(Token)match(input,COMMA,FOLLOW_COMMA_in_typeArguments2896); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal177_tree = 
            	    (Tree)adaptor.create(char_literal177)
            	    ;
            	    adaptor.addChild(root_0, char_literal177_tree);
            	    }


            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2898);
            	    typeArgument178=typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArgument178.getTree());


            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);


            char_literal179=(Token)match(input,GT,FOLLOW_GT_in_typeArguments2920); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal179_tree = 
            (Tree)adaptor.create(char_literal179)
            ;
            adaptor.addChild(root_0, char_literal179_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 35, typeArguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeArguments"


    public static class typeArgument_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeArgument"
    // GraphlrJava.g:742:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final GraphlrJavaParser.typeArgument_return typeArgument() throws RecognitionException {
        GraphlrJavaParser.typeArgument_return retval = new GraphlrJavaParser.typeArgument_return();
        retval.start = input.LT(1);

        int typeArgument_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal181=null;
        Token set182=null;
        ParserRuleReturnScope type180 =null;

        ParserRuleReturnScope type183 =null;


        Tree char_literal181_tree=null;
        Tree set182_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }

            // GraphlrJava.g:743:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==BOOLEAN||LA67_0==BYTE||LA67_0==CHAR||LA67_0==DOUBLE||LA67_0==FLOAT||LA67_0==IDENTIFIER||LA67_0==INT||LA67_0==LONG||LA67_0==SHORT) ) {
                alt67=1;
            }
            else if ( (LA67_0==QUES) ) {
                alt67=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 67, 0, input);

            	throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // GraphlrJava.g:743:9: type
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_type_in_typeArgument2940);
                    type180=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type180.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:744:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal181=(Token)match(input,QUES,FOLLOW_QUES_in_typeArgument2950); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal181_tree = 
                    (Tree)adaptor.create(char_literal181)
                    ;
                    adaptor.addChild(root_0, char_literal181_tree);
                    }


                    // GraphlrJava.g:745:9: ( ( 'extends' | 'super' ) type )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==EXTENDS||LA66_0==SUPER) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // GraphlrJava.g:746:13: ( 'extends' | 'super' ) type
                            {
                            set182=(Token)input.LT(1);

                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                                (Tree)adaptor.create(set182)
                                );
                                state.errorRecovery=false;
                                state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            pushFollow(FOLLOW_type_in_typeArgument3018);
                            type183=type();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, type183.getTree());


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 36, typeArgument_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeArgument"


    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedNameList"
    // GraphlrJava.g:753:1: qualifiedNameList : qualifiedName ( ',' qualifiedName )* ;
    public final GraphlrJavaParser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        GraphlrJavaParser.qualifiedNameList_return retval = new GraphlrJavaParser.qualifiedNameList_return();
        retval.start = input.LT(1);

        int qualifiedNameList_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal185=null;
        ParserRuleReturnScope qualifiedName184 =null;

        ParserRuleReturnScope qualifiedName186 =null;


        Tree char_literal185_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }

            // GraphlrJava.g:754:5: ( qualifiedName ( ',' qualifiedName )* )
            // GraphlrJava.g:754:9: qualifiedName ( ',' qualifiedName )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3049);
            qualifiedName184=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName184.getTree());


            // GraphlrJava.g:755:9: ( ',' qualifiedName )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // GraphlrJava.g:755:10: ',' qualifiedName
            	    {
            	    char_literal185=(Token)match(input,COMMA,FOLLOW_COMMA_in_qualifiedNameList3060); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal185_tree = 
            	    (Tree)adaptor.create(char_literal185)
            	    ;
            	    adaptor.addChild(root_0, char_literal185_tree);
            	    }


            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3062);
            	    qualifiedName186=qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName186.getTree());


            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 37, qualifiedNameList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedNameList"


    public static class formalParameters_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameters"
    // GraphlrJava.g:759:1: formalParameters : '(' ( formalParameterDecls )? ')' ;
    public final GraphlrJavaParser.formalParameters_return formalParameters() throws RecognitionException {
        GraphlrJavaParser.formalParameters_return retval = new GraphlrJavaParser.formalParameters_return();
        retval.start = input.LT(1);

        int formalParameters_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal187=null;
        Token char_literal189=null;
        ParserRuleReturnScope formalParameterDecls188 =null;


        Tree char_literal187_tree=null;
        Tree char_literal189_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }

            // GraphlrJava.g:760:5: ( '(' ( formalParameterDecls )? ')' )
            // GraphlrJava.g:760:9: '(' ( formalParameterDecls )? ')'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal187=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_formalParameters3093); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal187_tree = 
            (Tree)adaptor.create(char_literal187)
            ;
            adaptor.addChild(root_0, char_literal187_tree);
            }


            // GraphlrJava.g:761:9: ( formalParameterDecls )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==BOOLEAN||LA69_0==BYTE||LA69_0==CHAR||LA69_0==DOUBLE||LA69_0==FINAL||LA69_0==FLOAT||LA69_0==IDENTIFIER||LA69_0==INT||LA69_0==LONG||LA69_0==MONKEYS_AT||LA69_0==SHORT) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // GraphlrJava.g:761:10: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3104);
                    formalParameterDecls188=formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameterDecls188.getTree());


                    }
                    break;

            }


            char_literal189=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_formalParameters3126); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal189_tree = 
            (Tree)adaptor.create(char_literal189)
            ;
            adaptor.addChild(root_0, char_literal189_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 38, formalParameters_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameters"


    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameterDecls"
    // GraphlrJava.g:766:1: formalParameterDecls : ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl );
    public final GraphlrJavaParser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        GraphlrJavaParser.formalParameterDecls_return retval = new GraphlrJavaParser.formalParameterDecls_return();
        retval.start = input.LT(1);

        int formalParameterDecls_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal192=null;
        Token char_literal195=null;
        ParserRuleReturnScope ellipsisParameterDecl190 =null;

        ParserRuleReturnScope normalParameterDecl191 =null;

        ParserRuleReturnScope normalParameterDecl193 =null;

        ParserRuleReturnScope normalParameterDecl194 =null;

        ParserRuleReturnScope ellipsisParameterDecl196 =null;


        Tree char_literal192_tree=null;
        Tree char_literal195_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }

            // GraphlrJava.g:767:5: ( ellipsisParameterDecl | normalParameterDecl ( ',' normalParameterDecl )* | ( normalParameterDecl ',' )+ ellipsisParameterDecl )
            int alt72=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA72_1 = input.LA(2);

                if ( (synpred96_GraphlrJava()) ) {
                    alt72=1;
                }
                else if ( (synpred98_GraphlrJava()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 72, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA72_2 = input.LA(2);

                if ( (synpred96_GraphlrJava()) ) {
                    alt72=1;
                }
                else if ( (synpred98_GraphlrJava()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 72, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA72_3 = input.LA(2);

                if ( (synpred96_GraphlrJava()) ) {
                    alt72=1;
                }
                else if ( (synpred98_GraphlrJava()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 72, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA72_4 = input.LA(2);

                if ( (synpred96_GraphlrJava()) ) {
                    alt72=1;
                }
                else if ( (synpred98_GraphlrJava()) ) {
                    alt72=2;
                }
                else if ( (true) ) {
                    alt72=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 72, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 72, 0, input);

            	throw nvae;
            }

            switch (alt72) {
                case 1 :
                    // GraphlrJava.g:767:9: ellipsisParameterDecl
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3146);
                    ellipsisParameterDecl190=ellipsisParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl190.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:768:9: normalParameterDecl ( ',' normalParameterDecl )*
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3156);
                    normalParameterDecl191=normalParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl191.getTree());


                    // GraphlrJava.g:769:9: ( ',' normalParameterDecl )*
                    loop70:
                    do {
                        int alt70=2;
                        int LA70_0 = input.LA(1);

                        if ( (LA70_0==COMMA) ) {
                            alt70=1;
                        }


                        switch (alt70) {
                    	case 1 :
                    	    // GraphlrJava.g:769:10: ',' normalParameterDecl
                    	    {
                    	    char_literal192=(Token)match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3167); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal192_tree = 
                    	    (Tree)adaptor.create(char_literal192)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal192_tree);
                    	    }


                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3169);
                    	    normalParameterDecl193=normalParameterDecl();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl193.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop70;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:771:9: ( normalParameterDecl ',' )+ ellipsisParameterDecl
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:771:9: ( normalParameterDecl ',' )+
                    int cnt71=0;
                    loop71:
                    do {
                        int alt71=2;
                        switch ( input.LA(1) ) {
                        case FINAL:
                            {
                            int LA71_1 = input.LA(2);

                            if ( (synpred99_GraphlrJava()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case MONKEYS_AT:
                            {
                            int LA71_2 = input.LA(2);

                            if ( (synpred99_GraphlrJava()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case IDENTIFIER:
                            {
                            int LA71_3 = input.LA(2);

                            if ( (synpred99_GraphlrJava()) ) {
                                alt71=1;
                            }


                            }
                            break;
                        case BOOLEAN:
                        case BYTE:
                        case CHAR:
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case LONG:
                        case SHORT:
                            {
                            int LA71_4 = input.LA(2);

                            if ( (synpred99_GraphlrJava()) ) {
                                alt71=1;
                            }


                            }
                            break;

                        }

                        switch (alt71) {
                    	case 1 :
                    	    // GraphlrJava.g:771:10: normalParameterDecl ','
                    	    {
                    	    pushFollow(FOLLOW_normalParameterDecl_in_formalParameterDecls3191);
                    	    normalParameterDecl194=normalParameterDecl();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalParameterDecl194.getTree());


                    	    char_literal195=(Token)match(input,COMMA,FOLLOW_COMMA_in_formalParameterDecls3201); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal195_tree = 
                    	    (Tree)adaptor.create(char_literal195)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal195_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt71 >= 1 ) break loop71;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(71, input);
                                throw eee;
                        }
                        cnt71++;
                    } while (true);


                    pushFollow(FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3223);
                    ellipsisParameterDecl196=ellipsisParameterDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ellipsisParameterDecl196.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 39, formalParameterDecls_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameterDecls"


    public static class normalParameterDecl_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "normalParameterDecl"
    // GraphlrJava.g:777:1: normalParameterDecl : variableModifiers type IDENTIFIER ( '[' ']' )* ;
    public final GraphlrJavaParser.normalParameterDecl_return normalParameterDecl() throws RecognitionException {
        GraphlrJavaParser.normalParameterDecl_return retval = new GraphlrJavaParser.normalParameterDecl_return();
        retval.start = input.LT(1);

        int normalParameterDecl_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER199=null;
        Token char_literal200=null;
        Token char_literal201=null;
        ParserRuleReturnScope variableModifiers197 =null;

        ParserRuleReturnScope type198 =null;


        Tree IDENTIFIER199_tree=null;
        Tree char_literal200_tree=null;
        Tree char_literal201_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }

            // GraphlrJava.g:778:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
            // GraphlrJava.g:778:9: variableModifiers type IDENTIFIER ( '[' ']' )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_normalParameterDecl3243);
            variableModifiers197=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers197.getTree());


            pushFollow(FOLLOW_type_in_normalParameterDecl3245);
            type198=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type198.getTree());


            IDENTIFIER199=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_normalParameterDecl3247); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER199_tree = 
            (Tree)adaptor.create(IDENTIFIER199)
            ;
            adaptor.addChild(root_0, IDENTIFIER199_tree);
            }


            // GraphlrJava.g:779:9: ( '[' ']' )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==LBRACKET) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // GraphlrJava.g:779:10: '[' ']'
            	    {
            	    char_literal200=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_normalParameterDecl3258); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal200_tree = 
            	    (Tree)adaptor.create(char_literal200)
            	    ;
            	    adaptor.addChild(root_0, char_literal200_tree);
            	    }


            	    char_literal201=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_normalParameterDecl3260); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal201_tree = 
            	    (Tree)adaptor.create(char_literal201)
            	    ;
            	    adaptor.addChild(root_0, char_literal201_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 40, normalParameterDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "normalParameterDecl"


    public static class ellipsisParameterDecl_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "ellipsisParameterDecl"
    // GraphlrJava.g:783:1: ellipsisParameterDecl : variableModifiers type '...' IDENTIFIER ;
    public final GraphlrJavaParser.ellipsisParameterDecl_return ellipsisParameterDecl() throws RecognitionException {
        GraphlrJavaParser.ellipsisParameterDecl_return retval = new GraphlrJavaParser.ellipsisParameterDecl_return();
        retval.start = input.LT(1);

        int ellipsisParameterDecl_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal204=null;
        Token IDENTIFIER205=null;
        ParserRuleReturnScope variableModifiers202 =null;

        ParserRuleReturnScope type203 =null;


        Tree string_literal204_tree=null;
        Tree IDENTIFIER205_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }

            // GraphlrJava.g:784:5: ( variableModifiers type '...' IDENTIFIER )
            // GraphlrJava.g:784:9: variableModifiers type '...' IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_ellipsisParameterDecl3291);
            variableModifiers202=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers202.getTree());


            pushFollow(FOLLOW_type_in_ellipsisParameterDecl3301);
            type203=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type203.getTree());


            string_literal204=(Token)match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3304); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal204_tree = 
            (Tree)adaptor.create(string_literal204)
            ;
            adaptor.addChild(root_0, string_literal204_tree);
            }


            IDENTIFIER205=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3314); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER205_tree = 
            (Tree)adaptor.create(IDENTIFIER205)
            ;
            adaptor.addChild(root_0, IDENTIFIER205_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 41, ellipsisParameterDecl_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "ellipsisParameterDecl"


    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "explicitConstructorInvocation"
    // GraphlrJava.g:790:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' );
    public final GraphlrJavaParser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        GraphlrJavaParser.explicitConstructorInvocation_return retval = new GraphlrJavaParser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);

        int explicitConstructorInvocation_StartIndex = input.index();

        Tree root_0 = null;

        Token set207=null;
        Token char_literal209=null;
        Token char_literal211=null;
        Token string_literal213=null;
        Token char_literal215=null;
        ParserRuleReturnScope nonWildcardTypeArguments206 =null;

        ParserRuleReturnScope arguments208 =null;

        ParserRuleReturnScope primary210 =null;

        ParserRuleReturnScope nonWildcardTypeArguments212 =null;

        ParserRuleReturnScope arguments214 =null;


        Tree set207_tree=null;
        Tree char_literal209_tree=null;
        Tree char_literal211_tree=null;
        Tree string_literal213_tree=null;
        Tree char_literal215_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }

            // GraphlrJava.g:791:5: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' | primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';' )
            int alt76=2;
            switch ( input.LA(1) ) {
            case LT:
                {
                alt76=1;
                }
                break;
            case THIS:
                {
                int LA76_2 = input.LA(2);

                if ( (synpred103_GraphlrJava()) ) {
                    alt76=1;
                }
                else if ( (true) ) {
                    alt76=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 76, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case TRUE:
            case VOID:
                {
                alt76=2;
                }
                break;
            case SUPER:
                {
                int LA76_4 = input.LA(2);

                if ( (synpred103_GraphlrJava()) ) {
                    alt76=1;
                }
                else if ( (true) ) {
                    alt76=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 76, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 76, 0, input);

            	throw nvae;
            }

            switch (alt76) {
                case 1 :
                    // GraphlrJava.g:791:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:791:9: ( nonWildcardTypeArguments )?
                    int alt74=2;
                    int LA74_0 = input.LA(1);

                    if ( (LA74_0==LT) ) {
                        alt74=1;
                    }
                    switch (alt74) {
                        case 1 :
                            // GraphlrJava.g:791:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3336);
                            nonWildcardTypeArguments206=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments206.getTree());


                            }
                            break;

                    }


                    set207=(Token)input.LT(1);

                    if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                        (Tree)adaptor.create(set207)
                        );
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3394);
                    arguments208=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments208.getTree());


                    char_literal209=(Token)match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3396); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal209_tree = 
                    (Tree)adaptor.create(char_literal209)
                    ;
                    adaptor.addChild(root_0, char_literal209_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:798:9: primary '.' ( nonWildcardTypeArguments )? 'super' arguments ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3407);
                    primary210=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary210.getTree());


                    char_literal211=(Token)match(input,DOT,FOLLOW_DOT_in_explicitConstructorInvocation3417); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal211_tree = 
                    (Tree)adaptor.create(char_literal211)
                    ;
                    adaptor.addChild(root_0, char_literal211_tree);
                    }


                    // GraphlrJava.g:800:9: ( nonWildcardTypeArguments )?
                    int alt75=2;
                    int LA75_0 = input.LA(1);

                    if ( (LA75_0==LT) ) {
                        alt75=1;
                    }
                    switch (alt75) {
                        case 1 :
                            // GraphlrJava.g:800:10: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3428);
                            nonWildcardTypeArguments212=nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments212.getTree());


                            }
                            break;

                    }


                    string_literal213=(Token)match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3449); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal213_tree = 
                    (Tree)adaptor.create(string_literal213)
                    ;
                    adaptor.addChild(root_0, string_literal213_tree);
                    }


                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3459);
                    arguments214=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments214.getTree());


                    char_literal215=(Token)match(input,SEMI,FOLLOW_SEMI_in_explicitConstructorInvocation3461); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal215_tree = 
                    (Tree)adaptor.create(char_literal215)
                    ;
                    adaptor.addChild(root_0, char_literal215_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 42, explicitConstructorInvocation_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "explicitConstructorInvocation"


    public static class qualifiedName_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualifiedName"
    // GraphlrJava.g:806:1: qualifiedName : IDENTIFIER ( '.' IDENTIFIER )* ;
    public final GraphlrJavaParser.qualifiedName_return qualifiedName() throws RecognitionException {
        GraphlrJavaParser.qualifiedName_return retval = new GraphlrJavaParser.qualifiedName_return();
        retval.start = input.LT(1);

        int qualifiedName_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER216=null;
        Token char_literal217=null;
        Token IDENTIFIER218=null;

        Tree IDENTIFIER216_tree=null;
        Tree char_literal217_tree=null;
        Tree IDENTIFIER218_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }

            // GraphlrJava.g:807:5: ( IDENTIFIER ( '.' IDENTIFIER )* )
            // GraphlrJava.g:807:9: IDENTIFIER ( '.' IDENTIFIER )*
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER216=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3481); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER216_tree = 
            (Tree)adaptor.create(IDENTIFIER216)
            ;
            adaptor.addChild(root_0, IDENTIFIER216_tree);
            }


            // GraphlrJava.g:808:9: ( '.' IDENTIFIER )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==DOT) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // GraphlrJava.g:808:10: '.' IDENTIFIER
            	    {
            	    char_literal217=(Token)match(input,DOT,FOLLOW_DOT_in_qualifiedName3492); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal217_tree = 
            	    (Tree)adaptor.create(char_literal217)
            	    ;
            	    adaptor.addChild(root_0, char_literal217_tree);
            	    }


            	    IDENTIFIER218=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_qualifiedName3494); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    IDENTIFIER218_tree = 
            	    (Tree)adaptor.create(IDENTIFIER218)
            	    ;
            	    adaptor.addChild(root_0, IDENTIFIER218_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 43, qualifiedName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "qualifiedName"


    public static class annotations_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotations"
    // GraphlrJava.g:812:1: annotations : ( annotation )+ ;
    public final GraphlrJavaParser.annotations_return annotations() throws RecognitionException {
        GraphlrJavaParser.annotations_return retval = new GraphlrJavaParser.annotations_return();
        retval.start = input.LT(1);

        int annotations_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope annotation219 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }

            // GraphlrJava.g:813:5: ( ( annotation )+ )
            // GraphlrJava.g:813:9: ( annotation )+
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:813:9: ( annotation )+
            int cnt78=0;
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==MONKEYS_AT) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // GraphlrJava.g:813:10: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3526);
            	    annotation219=annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation219.getTree());


            	    }
            	    break;

            	default :
            	    if ( cnt78 >= 1 ) break loop78;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(78, input);
                        throw eee;
                }
                cnt78++;
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 44, annotations_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotations"


    public static class annotation_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotation"
    // GraphlrJava.g:821:1: annotation : '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? ;
    public final GraphlrJavaParser.annotation_return annotation() throws RecognitionException {
        GraphlrJavaParser.annotation_return retval = new GraphlrJavaParser.annotation_return();
        retval.start = input.LT(1);

        int annotation_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal220=null;
        Token char_literal222=null;
        Token char_literal225=null;
        ParserRuleReturnScope qualifiedName221 =null;

        ParserRuleReturnScope elementValuePairs223 =null;

        ParserRuleReturnScope elementValue224 =null;


        Tree char_literal220_tree=null;
        Tree char_literal222_tree=null;
        Tree char_literal225_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }

            // GraphlrJava.g:822:5: ( '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )? )
            // GraphlrJava.g:822:9: '@' qualifiedName ( '(' ( elementValuePairs | elementValue )? ')' )?
            {
            root_0 = (Tree)adaptor.nil();


            char_literal220=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotation3559); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal220_tree = 
            (Tree)adaptor.create(char_literal220)
            ;
            adaptor.addChild(root_0, char_literal220_tree);
            }


            pushFollow(FOLLOW_qualifiedName_in_annotation3561);
            qualifiedName221=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qualifiedName221.getTree());


            // GraphlrJava.g:823:9: ( '(' ( elementValuePairs | elementValue )? ')' )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==LPAREN) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // GraphlrJava.g:823:13: '(' ( elementValuePairs | elementValue )? ')'
                    {
                    char_literal222=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_annotation3575); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal222_tree = 
                    (Tree)adaptor.create(char_literal222)
                    ;
                    adaptor.addChild(root_0, char_literal222_tree);
                    }


                    // GraphlrJava.g:824:19: ( elementValuePairs | elementValue )?
                    int alt79=3;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==IDENTIFIER) ) {
                        int LA79_1 = input.LA(2);

                        if ( (LA79_1==EQ) ) {
                            alt79=1;
                        }
                        else if ( ((LA79_1 >= AMP && LA79_1 <= AMPAMP)||(LA79_1 >= BANGEQ && LA79_1 <= BARBAR)||LA79_1==CARET||LA79_1==DOT||LA79_1==EQEQ||LA79_1==GT||LA79_1==INSTANCEOF||LA79_1==LBRACKET||(LA79_1 >= LPAREN && LA79_1 <= LT)||LA79_1==PERCENT||LA79_1==PLUS||LA79_1==PLUSPLUS||LA79_1==QUES||LA79_1==RPAREN||LA79_1==SLASH||LA79_1==STAR||LA79_1==SUB||LA79_1==SUBSUB) ) {
                            alt79=2;
                        }
                    }
                    else if ( (LA79_0==BANG||LA79_0==BOOLEAN||LA79_0==BYTE||(LA79_0 >= CHAR && LA79_0 <= CHARLITERAL)||(LA79_0 >= DOUBLE && LA79_0 <= DOUBLELITERAL)||LA79_0==FALSE||(LA79_0 >= FLOAT && LA79_0 <= FLOATLITERAL)||LA79_0==INT||LA79_0==INTLITERAL||LA79_0==LBRACE||(LA79_0 >= LONG && LA79_0 <= LPAREN)||LA79_0==MONKEYS_AT||(LA79_0 >= NEW && LA79_0 <= NULL)||LA79_0==PLUS||LA79_0==PLUSPLUS||LA79_0==SHORT||(LA79_0 >= STRINGLITERAL && LA79_0 <= SUB)||(LA79_0 >= SUBSUB && LA79_0 <= SUPER)||LA79_0==THIS||LA79_0==TILDE||LA79_0==TRUE||LA79_0==VOID) ) {
                        alt79=2;
                    }
                    switch (alt79) {
                        case 1 :
                            // GraphlrJava.g:824:23: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3602);
                            elementValuePairs223=elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePairs223.getTree());


                            }
                            break;
                        case 2 :
                            // GraphlrJava.g:825:23: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3626);
                            elementValue224=elementValue();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue224.getTree());


                            }
                            break;

                    }


                    char_literal225=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_annotation3662); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal225_tree = 
                    (Tree)adaptor.create(char_literal225)
                    ;
                    adaptor.addChild(root_0, char_literal225_tree);
                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 45, annotation_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotation"


    public static class elementValuePairs_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValuePairs"
    // GraphlrJava.g:831:1: elementValuePairs : elementValuePair ( ',' elementValuePair )* ;
    public final GraphlrJavaParser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        GraphlrJavaParser.elementValuePairs_return retval = new GraphlrJavaParser.elementValuePairs_return();
        retval.start = input.LT(1);

        int elementValuePairs_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal227=null;
        ParserRuleReturnScope elementValuePair226 =null;

        ParserRuleReturnScope elementValuePair228 =null;


        Tree char_literal227_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }

            // GraphlrJava.g:832:5: ( elementValuePair ( ',' elementValuePair )* )
            // GraphlrJava.g:832:9: elementValuePair ( ',' elementValuePair )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3694);
            elementValuePair226=elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair226.getTree());


            // GraphlrJava.g:833:9: ( ',' elementValuePair )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);

                if ( (LA81_0==COMMA) ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // GraphlrJava.g:833:10: ',' elementValuePair
            	    {
            	    char_literal227=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValuePairs3705); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal227_tree = 
            	    (Tree)adaptor.create(char_literal227)
            	    ;
            	    adaptor.addChild(root_0, char_literal227_tree);
            	    }


            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3707);
            	    elementValuePair228=elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValuePair228.getTree());


            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 46, elementValuePairs_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValuePairs"


    public static class elementValuePair_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValuePair"
    // GraphlrJava.g:837:1: elementValuePair : IDENTIFIER '=' elementValue ;
    public final GraphlrJavaParser.elementValuePair_return elementValuePair() throws RecognitionException {
        GraphlrJavaParser.elementValuePair_return retval = new GraphlrJavaParser.elementValuePair_return();
        retval.start = input.LT(1);

        int elementValuePair_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER229=null;
        Token char_literal230=null;
        ParserRuleReturnScope elementValue231 =null;


        Tree IDENTIFIER229_tree=null;
        Tree char_literal230_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }

            // GraphlrJava.g:838:5: ( IDENTIFIER '=' elementValue )
            // GraphlrJava.g:838:9: IDENTIFIER '=' elementValue
            {
            root_0 = (Tree)adaptor.nil();


            IDENTIFIER229=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_elementValuePair3738); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER229_tree = 
            (Tree)adaptor.create(IDENTIFIER229)
            ;
            adaptor.addChild(root_0, IDENTIFIER229_tree);
            }


            char_literal230=(Token)match(input,EQ,FOLLOW_EQ_in_elementValuePair3740); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal230_tree = 
            (Tree)adaptor.create(char_literal230)
            ;
            adaptor.addChild(root_0, char_literal230_tree);
            }


            pushFollow(FOLLOW_elementValue_in_elementValuePair3742);
            elementValue231=elementValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue231.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 47, elementValuePair_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValuePair"


    public static class elementValue_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValue"
    // GraphlrJava.g:841:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final GraphlrJavaParser.elementValue_return elementValue() throws RecognitionException {
        GraphlrJavaParser.elementValue_return retval = new GraphlrJavaParser.elementValue_return();
        retval.start = input.LT(1);

        int elementValue_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope conditionalExpression232 =null;

        ParserRuleReturnScope annotation233 =null;

        ParserRuleReturnScope elementValueArrayInitializer234 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }

            // GraphlrJava.g:842:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt82=3;
            switch ( input.LA(1) ) {
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case SHORT:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt82=1;
                }
                break;
            case MONKEYS_AT:
                {
                alt82=2;
                }
                break;
            case LBRACE:
                {
                alt82=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 82, 0, input);

            	throw nvae;
            }

            switch (alt82) {
                case 1 :
                    // GraphlrJava.g:842:9: conditionalExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3762);
                    conditionalExpression232=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression232.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:843:9: annotation
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_annotation_in_elementValue3772);
                    annotation233=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotation233.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:844:9: elementValueArrayInitializer
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue3782);
                    elementValueArrayInitializer234=elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValueArrayInitializer234.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 48, elementValue_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValue"


    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "elementValueArrayInitializer"
    // GraphlrJava.g:847:1: elementValueArrayInitializer : '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' ;
    public final GraphlrJavaParser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        GraphlrJavaParser.elementValueArrayInitializer_return retval = new GraphlrJavaParser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);

        int elementValueArrayInitializer_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal235=null;
        Token char_literal237=null;
        Token char_literal239=null;
        Token char_literal240=null;
        ParserRuleReturnScope elementValue236 =null;

        ParserRuleReturnScope elementValue238 =null;


        Tree char_literal235_tree=null;
        Tree char_literal237_tree=null;
        Tree char_literal239_tree=null;
        Tree char_literal240_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }

            // GraphlrJava.g:848:5: ( '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}' )
            // GraphlrJava.g:848:9: '{' ( elementValue ( ',' elementValue )* )? ( ',' )? '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal235=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_elementValueArrayInitializer3802); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal235_tree = 
            (Tree)adaptor.create(char_literal235)
            ;
            adaptor.addChild(root_0, char_literal235_tree);
            }


            // GraphlrJava.g:849:9: ( elementValue ( ',' elementValue )* )?
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==BANG||LA84_0==BOOLEAN||LA84_0==BYTE||(LA84_0 >= CHAR && LA84_0 <= CHARLITERAL)||(LA84_0 >= DOUBLE && LA84_0 <= DOUBLELITERAL)||LA84_0==FALSE||(LA84_0 >= FLOAT && LA84_0 <= FLOATLITERAL)||LA84_0==IDENTIFIER||LA84_0==INT||LA84_0==INTLITERAL||LA84_0==LBRACE||(LA84_0 >= LONG && LA84_0 <= LPAREN)||LA84_0==MONKEYS_AT||(LA84_0 >= NEW && LA84_0 <= NULL)||LA84_0==PLUS||LA84_0==PLUSPLUS||LA84_0==SHORT||(LA84_0 >= STRINGLITERAL && LA84_0 <= SUB)||(LA84_0 >= SUBSUB && LA84_0 <= SUPER)||LA84_0==THIS||LA84_0==TILDE||LA84_0==TRUE||LA84_0==VOID) ) {
                alt84=1;
            }
            switch (alt84) {
                case 1 :
                    // GraphlrJava.g:849:10: elementValue ( ',' elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3813);
                    elementValue236=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue236.getTree());


                    // GraphlrJava.g:850:13: ( ',' elementValue )*
                    loop83:
                    do {
                        int alt83=2;
                        int LA83_0 = input.LA(1);

                        if ( (LA83_0==COMMA) ) {
                            int LA83_1 = input.LA(2);

                            if ( (LA83_1==BANG||LA83_1==BOOLEAN||LA83_1==BYTE||(LA83_1 >= CHAR && LA83_1 <= CHARLITERAL)||(LA83_1 >= DOUBLE && LA83_1 <= DOUBLELITERAL)||LA83_1==FALSE||(LA83_1 >= FLOAT && LA83_1 <= FLOATLITERAL)||LA83_1==IDENTIFIER||LA83_1==INT||LA83_1==INTLITERAL||LA83_1==LBRACE||(LA83_1 >= LONG && LA83_1 <= LPAREN)||LA83_1==MONKEYS_AT||(LA83_1 >= NEW && LA83_1 <= NULL)||LA83_1==PLUS||LA83_1==PLUSPLUS||LA83_1==SHORT||(LA83_1 >= STRINGLITERAL && LA83_1 <= SUB)||(LA83_1 >= SUBSUB && LA83_1 <= SUPER)||LA83_1==THIS||LA83_1==TILDE||LA83_1==TRUE||LA83_1==VOID) ) {
                                alt83=1;
                            }


                        }


                        switch (alt83) {
                    	case 1 :
                    	    // GraphlrJava.g:850:14: ',' elementValue
                    	    {
                    	    char_literal237=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer3828); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal237_tree = 
                    	    (Tree)adaptor.create(char_literal237)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal237_tree);
                    	    }


                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer3830);
                    	    elementValue238=elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue238.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop83;
                        }
                    } while (true);


                    }
                    break;

            }


            // GraphlrJava.g:852:12: ( ',' )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==COMMA) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // GraphlrJava.g:852:13: ','
                    {
                    char_literal239=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer3859); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal239_tree = 
                    (Tree)adaptor.create(char_literal239)
                    ;
                    adaptor.addChild(root_0, char_literal239_tree);
                    }


                    }
                    break;

            }


            char_literal240=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_elementValueArrayInitializer3863); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal240_tree = 
            (Tree)adaptor.create(char_literal240)
            ;
            adaptor.addChild(root_0, char_literal240_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 49, elementValueArrayInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "elementValueArrayInitializer"


    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeDeclaration"
    // GraphlrJava.g:859:1: annotationTypeDeclaration : modifiers '@' 'interface' IDENTIFIER annotationTypeBody ;
    public final GraphlrJavaParser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        GraphlrJavaParser.annotationTypeDeclaration_return retval = new GraphlrJavaParser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);

        int annotationTypeDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal242=null;
        Token string_literal243=null;
        Token IDENTIFIER244=null;
        ParserRuleReturnScope modifiers241 =null;

        ParserRuleReturnScope annotationTypeBody245 =null;


        Tree char_literal242_tree=null;
        Tree string_literal243_tree=null;
        Tree IDENTIFIER244_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }

            // GraphlrJava.g:860:5: ( modifiers '@' 'interface' IDENTIFIER annotationTypeBody )
            // GraphlrJava.g:860:9: modifiers '@' 'interface' IDENTIFIER annotationTypeBody
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationTypeDeclaration3886);
            modifiers241=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers241.getTree());


            char_literal242=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration3888); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal242_tree = 
            (Tree)adaptor.create(char_literal242)
            ;
            adaptor.addChild(root_0, char_literal242_tree);
            }


            string_literal243=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationTypeDeclaration3898); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal243_tree = 
            (Tree)adaptor.create(string_literal243)
            ;
            adaptor.addChild(root_0, string_literal243_tree);
            }


            IDENTIFIER244=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationTypeDeclaration3908); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER244_tree = 
            (Tree)adaptor.create(IDENTIFIER244)
            ;
            adaptor.addChild(root_0, IDENTIFIER244_tree);
            }


            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3918);
            annotationTypeBody245=annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeBody245.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 50, annotationTypeDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeDeclaration"


    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeBody"
    // GraphlrJava.g:867:1: annotationTypeBody : '{' ( annotationTypeElementDeclaration )* '}' ;
    public final GraphlrJavaParser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        GraphlrJavaParser.annotationTypeBody_return retval = new GraphlrJavaParser.annotationTypeBody_return();
        retval.start = input.LT(1);

        int annotationTypeBody_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal246=null;
        Token char_literal248=null;
        ParserRuleReturnScope annotationTypeElementDeclaration247 =null;


        Tree char_literal246_tree=null;
        Tree char_literal248_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }

            // GraphlrJava.g:868:5: ( '{' ( annotationTypeElementDeclaration )* '}' )
            // GraphlrJava.g:868:9: '{' ( annotationTypeElementDeclaration )* '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal246=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_annotationTypeBody3939); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal246_tree = 
            (Tree)adaptor.create(char_literal246)
            ;
            adaptor.addChild(root_0, char_literal246_tree);
            }


            // GraphlrJava.g:869:9: ( annotationTypeElementDeclaration )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);

                if ( (LA86_0==ABSTRACT||LA86_0==BOOLEAN||LA86_0==BYTE||LA86_0==CHAR||LA86_0==CLASS||LA86_0==DOUBLE||LA86_0==ENUM||LA86_0==FINAL||LA86_0==FLOAT||LA86_0==IDENTIFIER||(LA86_0 >= INT && LA86_0 <= INTERFACE)||LA86_0==LONG||LA86_0==LT||(LA86_0 >= MONKEYS_AT && LA86_0 <= NATIVE)||(LA86_0 >= PRIVATE && LA86_0 <= PUBLIC)||(LA86_0 >= SEMI && LA86_0 <= SHORT)||(LA86_0 >= STATIC && LA86_0 <= STRICTFP)||LA86_0==SYNCHRONIZED||LA86_0==TRANSIENT||(LA86_0 >= VOID && LA86_0 <= VOLATILE)) ) {
                    alt86=1;
                }


                switch (alt86) {
            	case 1 :
            	    // GraphlrJava.g:869:10: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3951);
            	    annotationTypeElementDeclaration247=annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeElementDeclaration247.getTree());


            	    }
            	    break;

            	default :
            	    break loop86;
                }
            } while (true);


            char_literal248=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_annotationTypeBody3973); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal248_tree = 
            (Tree)adaptor.create(char_literal248)
            ;
            adaptor.addChild(root_0, char_literal248_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 51, annotationTypeBody_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeBody"


    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationTypeElementDeclaration"
    // GraphlrJava.g:877:1: annotationTypeElementDeclaration : ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' );
    public final GraphlrJavaParser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        GraphlrJavaParser.annotationTypeElementDeclaration_return retval = new GraphlrJavaParser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);

        int annotationTypeElementDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal255=null;
        ParserRuleReturnScope annotationMethodDeclaration249 =null;

        ParserRuleReturnScope interfaceFieldDeclaration250 =null;

        ParserRuleReturnScope normalClassDeclaration251 =null;

        ParserRuleReturnScope normalInterfaceDeclaration252 =null;

        ParserRuleReturnScope enumDeclaration253 =null;

        ParserRuleReturnScope annotationTypeDeclaration254 =null;


        Tree char_literal255_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }

            // GraphlrJava.g:878:5: ( annotationMethodDeclaration | interfaceFieldDeclaration | normalClassDeclaration | normalInterfaceDeclaration | enumDeclaration | annotationTypeDeclaration | ';' )
            int alt87=7;
            switch ( input.LA(1) ) {
            case MONKEYS_AT:
                {
                int LA87_1 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PUBLIC:
                {
                int LA87_2 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PROTECTED:
                {
                int LA87_3 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case PRIVATE:
                {
                int LA87_4 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STATIC:
                {
                int LA87_5 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 5, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
                {
                int LA87_6 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 6, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case FINAL:
                {
                int LA87_7 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 7, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case NATIVE:
                {
                int LA87_8 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 8, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SYNCHRONIZED:
                {
                int LA87_9 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 9, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case TRANSIENT:
                {
                int LA87_10 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 10, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case VOLATILE:
                {
                int LA87_11 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case STRICTFP:
                {
                int LA87_12 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else if ( (synpred119_GraphlrJava()) ) {
                    alt87=3;
                }
                else if ( (synpred120_GraphlrJava()) ) {
                    alt87=4;
                }
                else if ( (synpred121_GraphlrJava()) ) {
                    alt87=5;
                }
                else if ( (synpred122_GraphlrJava()) ) {
                    alt87=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 12, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA87_13 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 13, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA87_14 = input.LA(2);

                if ( (synpred117_GraphlrJava()) ) {
                    alt87=1;
                }
                else if ( (synpred118_GraphlrJava()) ) {
                    alt87=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 87, 14, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case CLASS:
                {
                alt87=3;
                }
                break;
            case INTERFACE:
                {
                alt87=4;
                }
                break;
            case ENUM:
                {
                alt87=5;
                }
                break;
            case SEMI:
                {
                alt87=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 87, 0, input);

            	throw nvae;
            }

            switch (alt87) {
                case 1 :
                    // GraphlrJava.g:878:9: annotationMethodDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration3995);
                    annotationMethodDeclaration249=annotationMethodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationMethodDeclaration249.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:879:9: interfaceFieldDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4005);
                    interfaceFieldDeclaration250=interfaceFieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interfaceFieldDeclaration250.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:880:9: normalClassDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4015);
                    normalClassDeclaration251=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalClassDeclaration251.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:881:9: normalInterfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4025);
                    normalInterfaceDeclaration252=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, normalInterfaceDeclaration252.getTree());


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:882:9: enumDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4035);
                    enumDeclaration253=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enumDeclaration253.getTree());


                    }
                    break;
                case 6 :
                    // GraphlrJava.g:883:9: annotationTypeDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4045);
                    annotationTypeDeclaration254=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, annotationTypeDeclaration254.getTree());


                    }
                    break;
                case 7 :
                    // GraphlrJava.g:884:9: ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal255=(Token)match(input,SEMI,FOLLOW_SEMI_in_annotationTypeElementDeclaration4055); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal255_tree = 
                    (Tree)adaptor.create(char_literal255)
                    ;
                    adaptor.addChild(root_0, char_literal255_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 52, annotationTypeElementDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementDeclaration"


    public static class annotationMethodDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationMethodDeclaration"
    // GraphlrJava.g:887:1: annotationMethodDeclaration : modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' ;
    public final GraphlrJavaParser.annotationMethodDeclaration_return annotationMethodDeclaration() throws RecognitionException {
        GraphlrJavaParser.annotationMethodDeclaration_return retval = new GraphlrJavaParser.annotationMethodDeclaration_return();
        retval.start = input.LT(1);

        int annotationMethodDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER258=null;
        Token char_literal259=null;
        Token char_literal260=null;
        Token string_literal261=null;
        Token char_literal263=null;
        ParserRuleReturnScope modifiers256 =null;

        ParserRuleReturnScope type257 =null;

        ParserRuleReturnScope elementValue262 =null;


        Tree IDENTIFIER258_tree=null;
        Tree char_literal259_tree=null;
        Tree char_literal260_tree=null;
        Tree string_literal261_tree=null;
        Tree char_literal263_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }

            // GraphlrJava.g:888:5: ( modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';' )
            // GraphlrJava.g:888:9: modifiers type IDENTIFIER '(' ')' ( 'default' elementValue )? ';'
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationMethodDeclaration4075);
            modifiers256=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers256.getTree());


            pushFollow(FOLLOW_type_in_annotationMethodDeclaration4077);
            type257=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type257.getTree());


            IDENTIFIER258=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4079); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER258_tree = 
            (Tree)adaptor.create(IDENTIFIER258)
            ;
            adaptor.addChild(root_0, IDENTIFIER258_tree);
            }


            char_literal259=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_annotationMethodDeclaration4089); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal259_tree = 
            (Tree)adaptor.create(char_literal259)
            ;
            adaptor.addChild(root_0, char_literal259_tree);
            }


            char_literal260=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_annotationMethodDeclaration4091); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal260_tree = 
            (Tree)adaptor.create(char_literal260)
            ;
            adaptor.addChild(root_0, char_literal260_tree);
            }


            // GraphlrJava.g:889:17: ( 'default' elementValue )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==DEFAULT) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // GraphlrJava.g:889:18: 'default' elementValue
                    {
                    string_literal261=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_annotationMethodDeclaration4094); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal261_tree = 
                    (Tree)adaptor.create(string_literal261)
                    ;
                    adaptor.addChild(root_0, string_literal261_tree);
                    }


                    pushFollow(FOLLOW_elementValue_in_annotationMethodDeclaration4096);
                    elementValue262=elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, elementValue262.getTree());


                    }
                    break;

            }


            char_literal263=(Token)match(input,SEMI,FOLLOW_SEMI_in_annotationMethodDeclaration4125); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal263_tree = 
            (Tree)adaptor.create(char_literal263)
            ;
            adaptor.addChild(root_0, char_literal263_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 53, annotationMethodDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationMethodDeclaration"


    public static class block_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // GraphlrJava.g:894:1: block : '{' ( blockStatement )* '}' ;
    public final GraphlrJavaParser.block_return block() throws RecognitionException {
        GraphlrJavaParser.block_return retval = new GraphlrJavaParser.block_return();
        retval.start = input.LT(1);

        int block_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal264=null;
        Token char_literal266=null;
        ParserRuleReturnScope blockStatement265 =null;


        Tree char_literal264_tree=null;
        Tree char_literal266_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }

            // GraphlrJava.g:895:5: ( '{' ( blockStatement )* '}' )
            // GraphlrJava.g:895:9: '{' ( blockStatement )* '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal264=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_block4149); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal264_tree = 
            (Tree)adaptor.create(char_literal264)
            ;
            adaptor.addChild(root_0, char_literal264_tree);
            }


            // GraphlrJava.g:896:9: ( blockStatement )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==ABSTRACT||(LA89_0 >= ASSERT && LA89_0 <= BANG)||(LA89_0 >= BOOLEAN && LA89_0 <= BYTE)||(LA89_0 >= CHAR && LA89_0 <= CLASS)||LA89_0==CONTINUE||LA89_0==DO||(LA89_0 >= DOUBLE && LA89_0 <= DOUBLELITERAL)||LA89_0==ENUM||(LA89_0 >= FALSE && LA89_0 <= FINAL)||(LA89_0 >= FLOAT && LA89_0 <= FOR)||(LA89_0 >= IDENTIFIER && LA89_0 <= IF)||(LA89_0 >= INT && LA89_0 <= INTLITERAL)||LA89_0==LBRACE||(LA89_0 >= LONG && LA89_0 <= LT)||(LA89_0 >= MONKEYS_AT && LA89_0 <= NULL)||LA89_0==PLUS||(LA89_0 >= PLUSPLUS && LA89_0 <= PUBLIC)||LA89_0==RETURN||(LA89_0 >= SEMI && LA89_0 <= SHORT)||(LA89_0 >= STATIC && LA89_0 <= SUB)||(LA89_0 >= SUBSUB && LA89_0 <= SYNCHRONIZED)||(LA89_0 >= THIS && LA89_0 <= THROW)||(LA89_0 >= TILDE && LA89_0 <= WHILE)) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // GraphlrJava.g:896:10: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block4160);
            	    blockStatement265=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement265.getTree());


            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);


            char_literal266=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_block4181); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal266_tree = 
            (Tree)adaptor.create(char_literal266)
            ;
            adaptor.addChild(root_0, char_literal266_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 54, block_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "block"


    public static class blockStatement_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "blockStatement"
    // GraphlrJava.g:925:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final GraphlrJavaParser.blockStatement_return blockStatement() throws RecognitionException {
        GraphlrJavaParser.blockStatement_return retval = new GraphlrJavaParser.blockStatement_return();
        retval.start = input.LT(1);

        int blockStatement_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope localVariableDeclarationStatement267 =null;

        ParserRuleReturnScope classOrInterfaceDeclaration268 =null;

        ParserRuleReturnScope statement269 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }

            // GraphlrJava.g:926:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt90=3;
            switch ( input.LA(1) ) {
            case FINAL:
                {
                int LA90_1 = input.LA(2);

                if ( (synpred125_GraphlrJava()) ) {
                    alt90=1;
                }
                else if ( (synpred126_GraphlrJava()) ) {
                    alt90=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 90, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case MONKEYS_AT:
                {
                int LA90_2 = input.LA(2);

                if ( (synpred125_GraphlrJava()) ) {
                    alt90=1;
                }
                else if ( (synpred126_GraphlrJava()) ) {
                    alt90=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 90, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA90_3 = input.LA(2);

                if ( (synpred125_GraphlrJava()) ) {
                    alt90=1;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 90, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA90_4 = input.LA(2);

                if ( (synpred125_GraphlrJava()) ) {
                    alt90=1;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 90, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ABSTRACT:
            case CLASS:
            case ENUM:
            case INTERFACE:
            case NATIVE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case STATIC:
            case STRICTFP:
            case TRANSIENT:
            case VOLATILE:
                {
                alt90=2;
                }
                break;
            case SYNCHRONIZED:
                {
                int LA90_11 = input.LA(2);

                if ( (synpred126_GraphlrJava()) ) {
                    alt90=2;
                }
                else if ( (true) ) {
                    alt90=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 90, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case ASSERT:
            case BANG:
            case BREAK:
            case CHARLITERAL:
            case CONTINUE:
            case DO:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case FOR:
            case IF:
            case INTLITERAL:
            case LBRACE:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case RETURN:
            case SEMI:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case SWITCH:
            case THIS:
            case THROW:
            case TILDE:
            case TRUE:
            case TRY:
            case VOID:
            case WHILE:
                {
                alt90=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 90, 0, input);

            	throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // GraphlrJava.g:926:9: localVariableDeclarationStatement
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4203);
                    localVariableDeclarationStatement267=localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclarationStatement267.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:927:9: classOrInterfaceDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4213);
                    classOrInterfaceDeclaration268=classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceDeclaration268.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:928:9: statement
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_statement_in_blockStatement4223);
                    statement269=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement269.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 55, blockStatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "blockStatement"


    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableDeclarationStatement"
    // GraphlrJava.g:932:1: localVariableDeclarationStatement : localVariableDeclaration ';' ;
    public final GraphlrJavaParser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        GraphlrJavaParser.localVariableDeclarationStatement_return retval = new GraphlrJavaParser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);

        int localVariableDeclarationStatement_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal271=null;
        ParserRuleReturnScope localVariableDeclaration270 =null;


        Tree char_literal271_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }

            // GraphlrJava.g:933:5: ( localVariableDeclaration ';' )
            // GraphlrJava.g:933:9: localVariableDeclaration ';'
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4244);
            localVariableDeclaration270=localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration270.getTree());


            char_literal271=(Token)match(input,SEMI,FOLLOW_SEMI_in_localVariableDeclarationStatement4254); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal271_tree = 
            (Tree)adaptor.create(char_literal271)
            ;
            adaptor.addChild(root_0, char_literal271_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 56, localVariableDeclarationStatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableDeclarationStatement"


    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableDeclaration"
    // GraphlrJava.g:937:1: localVariableDeclaration : variableModifiers type variableDeclarator ( ',' variableDeclarator )* ;
    public final GraphlrJavaParser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        GraphlrJavaParser.localVariableDeclaration_return retval = new GraphlrJavaParser.localVariableDeclaration_return();
        retval.start = input.LT(1);

        int localVariableDeclaration_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal275=null;
        ParserRuleReturnScope variableModifiers272 =null;

        ParserRuleReturnScope type273 =null;

        ParserRuleReturnScope variableDeclarator274 =null;

        ParserRuleReturnScope variableDeclarator276 =null;


        Tree char_literal275_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }

            // GraphlrJava.g:938:5: ( variableModifiers type variableDeclarator ( ',' variableDeclarator )* )
            // GraphlrJava.g:938:9: variableModifiers type variableDeclarator ( ',' variableDeclarator )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4274);
            variableModifiers272=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers272.getTree());


            pushFollow(FOLLOW_type_in_localVariableDeclaration4276);
            type273=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type273.getTree());


            pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4286);
            variableDeclarator274=variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator274.getTree());


            // GraphlrJava.g:940:9: ( ',' variableDeclarator )*
            loop91:
            do {
                int alt91=2;
                int LA91_0 = input.LA(1);

                if ( (LA91_0==COMMA) ) {
                    alt91=1;
                }


                switch (alt91) {
            	case 1 :
            	    // GraphlrJava.g:940:10: ',' variableDeclarator
            	    {
            	    char_literal275=(Token)match(input,COMMA,FOLLOW_COMMA_in_localVariableDeclaration4297); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal275_tree = 
            	    (Tree)adaptor.create(char_literal275)
            	    ;
            	    adaptor.addChild(root_0, char_literal275_tree);
            	    }


            	    pushFollow(FOLLOW_variableDeclarator_in_localVariableDeclaration4299);
            	    variableDeclarator276=variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableDeclarator276.getTree());


            	    }
            	    break;

            	default :
            	    break loop91;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 57, localVariableDeclaration_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableDeclaration"


    public static class statement_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "statement"
    // GraphlrJava.g:944:1: statement : ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' );
    public final GraphlrJavaParser.statement_return statement() throws RecognitionException {
        GraphlrJavaParser.statement_return retval = new GraphlrJavaParser.statement_return();
        retval.start = input.LT(1);

        int statement_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal278=null;
        Token char_literal280=null;
        Token char_literal282=null;
        Token string_literal283=null;
        Token char_literal285=null;
        Token char_literal287=null;
        Token string_literal288=null;
        Token string_literal291=null;
        Token string_literal294=null;
        Token string_literal297=null;
        Token string_literal299=null;
        Token char_literal301=null;
        Token string_literal303=null;
        Token char_literal305=null;
        Token char_literal307=null;
        Token string_literal308=null;
        Token string_literal311=null;
        Token char_literal313=null;
        Token string_literal314=null;
        Token char_literal316=null;
        Token string_literal317=null;
        Token IDENTIFIER318=null;
        Token char_literal319=null;
        Token string_literal320=null;
        Token IDENTIFIER321=null;
        Token char_literal322=null;
        Token char_literal324=null;
        Token IDENTIFIER325=null;
        Token char_literal326=null;
        Token char_literal328=null;
        ParserRuleReturnScope block277 =null;

        ParserRuleReturnScope expression279 =null;

        ParserRuleReturnScope expression281 =null;

        ParserRuleReturnScope expression284 =null;

        ParserRuleReturnScope expression286 =null;

        ParserRuleReturnScope parExpression289 =null;

        ParserRuleReturnScope statement290 =null;

        ParserRuleReturnScope statement292 =null;

        ParserRuleReturnScope forstatement293 =null;

        ParserRuleReturnScope parExpression295 =null;

        ParserRuleReturnScope statement296 =null;

        ParserRuleReturnScope statement298 =null;

        ParserRuleReturnScope parExpression300 =null;

        ParserRuleReturnScope trystatement302 =null;

        ParserRuleReturnScope parExpression304 =null;

        ParserRuleReturnScope switchBlockStatementGroups306 =null;

        ParserRuleReturnScope parExpression309 =null;

        ParserRuleReturnScope block310 =null;

        ParserRuleReturnScope expression312 =null;

        ParserRuleReturnScope expression315 =null;

        ParserRuleReturnScope expression323 =null;

        ParserRuleReturnScope statement327 =null;


        Tree string_literal278_tree=null;
        Tree char_literal280_tree=null;
        Tree char_literal282_tree=null;
        Tree string_literal283_tree=null;
        Tree char_literal285_tree=null;
        Tree char_literal287_tree=null;
        Tree string_literal288_tree=null;
        Tree string_literal291_tree=null;
        Tree string_literal294_tree=null;
        Tree string_literal297_tree=null;
        Tree string_literal299_tree=null;
        Tree char_literal301_tree=null;
        Tree string_literal303_tree=null;
        Tree char_literal305_tree=null;
        Tree char_literal307_tree=null;
        Tree string_literal308_tree=null;
        Tree string_literal311_tree=null;
        Tree char_literal313_tree=null;
        Tree string_literal314_tree=null;
        Tree char_literal316_tree=null;
        Tree string_literal317_tree=null;
        Tree IDENTIFIER318_tree=null;
        Tree char_literal319_tree=null;
        Tree string_literal320_tree=null;
        Tree IDENTIFIER321_tree=null;
        Tree char_literal322_tree=null;
        Tree char_literal324_tree=null;
        Tree IDENTIFIER325_tree=null;
        Tree char_literal326_tree=null;
        Tree char_literal328_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }

            // GraphlrJava.g:945:5: ( block | ( 'assert' ) expression ( ':' expression )? ';' | 'assert' expression ( ':' expression )? ';' | 'if' parExpression statement ( 'else' statement )? | forstatement | 'while' parExpression statement | 'do' statement 'while' parExpression ';' | trystatement | 'switch' parExpression '{' switchBlockStatementGroups '}' | 'synchronized' parExpression block | 'return' ( expression )? ';' | 'throw' expression ';' | 'break' ( IDENTIFIER )? ';' | 'continue' ( IDENTIFIER )? ';' | expression ';' | IDENTIFIER ':' statement | ';' )
            int alt98=17;
            switch ( input.LA(1) ) {
            case LBRACE:
                {
                alt98=1;
                }
                break;
            case ASSERT:
                {
                int LA98_2 = input.LA(2);

                if ( (synpred130_GraphlrJava()) ) {
                    alt98=2;
                }
                else if ( (synpred132_GraphlrJava()) ) {
                    alt98=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 98, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case IF:
                {
                alt98=4;
                }
                break;
            case FOR:
                {
                alt98=5;
                }
                break;
            case WHILE:
                {
                alt98=6;
                }
                break;
            case DO:
                {
                alt98=7;
                }
                break;
            case TRY:
                {
                alt98=8;
                }
                break;
            case SWITCH:
                {
                alt98=9;
                }
                break;
            case SYNCHRONIZED:
                {
                alt98=10;
                }
                break;
            case RETURN:
                {
                alt98=11;
                }
                break;
            case THROW:
                {
                alt98=12;
                }
                break;
            case BREAK:
                {
                alt98=13;
                }
                break;
            case CONTINUE:
                {
                alt98=14;
                }
                break;
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case SHORT:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt98=15;
                }
                break;
            case IDENTIFIER:
                {
                int LA98_22 = input.LA(2);

                if ( (synpred148_GraphlrJava()) ) {
                    alt98=15;
                }
                else if ( (synpred149_GraphlrJava()) ) {
                    alt98=16;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 98, 22, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case SEMI:
                {
                alt98=17;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 98, 0, input);

            	throw nvae;
            }

            switch (alt98) {
                case 1 :
                    // GraphlrJava.g:945:9: block
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_statement4330);
                    block277=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block277.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:947:9: ( 'assert' ) expression ( ':' expression )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:947:9: ( 'assert' )
                    // GraphlrJava.g:947:10: 'assert'
                    {
                    string_literal278=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4354); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal278_tree = 
                    (Tree)adaptor.create(string_literal278)
                    ;
                    adaptor.addChild(root_0, string_literal278_tree);
                    }


                    }


                    pushFollow(FOLLOW_expression_in_statement4374);
                    expression279=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression279.getTree());


                    // GraphlrJava.g:949:20: ( ':' expression )?
                    int alt92=2;
                    int LA92_0 = input.LA(1);

                    if ( (LA92_0==COLON) ) {
                        alt92=1;
                    }
                    switch (alt92) {
                        case 1 :
                            // GraphlrJava.g:949:21: ':' expression
                            {
                            char_literal280=(Token)match(input,COLON,FOLLOW_COLON_in_statement4377); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal280_tree = 
                            (Tree)adaptor.create(char_literal280)
                            ;
                            adaptor.addChild(root_0, char_literal280_tree);
                            }


                            pushFollow(FOLLOW_expression_in_statement4379);
                            expression281=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression281.getTree());


                            }
                            break;

                    }


                    char_literal282=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4383); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal282_tree = 
                    (Tree)adaptor.create(char_literal282)
                    ;
                    adaptor.addChild(root_0, char_literal282_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:950:9: 'assert' expression ( ':' expression )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal283=(Token)match(input,ASSERT,FOLLOW_ASSERT_in_statement4393); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal283_tree = 
                    (Tree)adaptor.create(string_literal283)
                    ;
                    adaptor.addChild(root_0, string_literal283_tree);
                    }


                    pushFollow(FOLLOW_expression_in_statement4396);
                    expression284=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression284.getTree());


                    // GraphlrJava.g:950:30: ( ':' expression )?
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( (LA93_0==COLON) ) {
                        alt93=1;
                    }
                    switch (alt93) {
                        case 1 :
                            // GraphlrJava.g:950:31: ':' expression
                            {
                            char_literal285=(Token)match(input,COLON,FOLLOW_COLON_in_statement4399); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal285_tree = 
                            (Tree)adaptor.create(char_literal285)
                            ;
                            adaptor.addChild(root_0, char_literal285_tree);
                            }


                            pushFollow(FOLLOW_expression_in_statement4401);
                            expression286=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression286.getTree());


                            }
                            break;

                    }


                    char_literal287=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4405); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal287_tree = 
                    (Tree)adaptor.create(char_literal287)
                    ;
                    adaptor.addChild(root_0, char_literal287_tree);
                    }


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:951:9: 'if' parExpression statement ( 'else' statement )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal288=(Token)match(input,IF,FOLLOW_IF_in_statement4427); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal288_tree = 
                    (Tree)adaptor.create(string_literal288)
                    ;
                    adaptor.addChild(root_0, string_literal288_tree);
                    }


                    pushFollow(FOLLOW_parExpression_in_statement4429);
                    parExpression289=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression289.getTree());


                    pushFollow(FOLLOW_statement_in_statement4431);
                    statement290=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement290.getTree());


                    // GraphlrJava.g:951:38: ( 'else' statement )?
                    int alt94=2;
                    int LA94_0 = input.LA(1);

                    if ( (LA94_0==ELSE) ) {
                        int LA94_1 = input.LA(2);

                        if ( (synpred133_GraphlrJava()) ) {
                            alt94=1;
                        }
                    }
                    switch (alt94) {
                        case 1 :
                            // GraphlrJava.g:951:39: 'else' statement
                            {
                            string_literal291=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement4434); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            string_literal291_tree = 
                            (Tree)adaptor.create(string_literal291)
                            ;
                            adaptor.addChild(root_0, string_literal291_tree);
                            }


                            pushFollow(FOLLOW_statement_in_statement4436);
                            statement292=statement();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, statement292.getTree());


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:952:9: forstatement
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_forstatement_in_statement4458);
                    forstatement293=forstatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, forstatement293.getTree());


                    }
                    break;
                case 6 :
                    // GraphlrJava.g:953:9: 'while' parExpression statement
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal294=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement4468); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal294_tree = 
                    (Tree)adaptor.create(string_literal294)
                    ;
                    adaptor.addChild(root_0, string_literal294_tree);
                    }


                    pushFollow(FOLLOW_parExpression_in_statement4470);
                    parExpression295=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression295.getTree());


                    pushFollow(FOLLOW_statement_in_statement4472);
                    statement296=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement296.getTree());


                    }
                    break;
                case 7 :
                    // GraphlrJava.g:954:9: 'do' statement 'while' parExpression ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal297=(Token)match(input,DO,FOLLOW_DO_in_statement4482); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal297_tree = 
                    (Tree)adaptor.create(string_literal297)
                    ;
                    adaptor.addChild(root_0, string_literal297_tree);
                    }


                    pushFollow(FOLLOW_statement_in_statement4484);
                    statement298=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement298.getTree());


                    string_literal299=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement4486); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal299_tree = 
                    (Tree)adaptor.create(string_literal299)
                    ;
                    adaptor.addChild(root_0, string_literal299_tree);
                    }


                    pushFollow(FOLLOW_parExpression_in_statement4488);
                    parExpression300=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression300.getTree());


                    char_literal301=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4490); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal301_tree = 
                    (Tree)adaptor.create(char_literal301)
                    ;
                    adaptor.addChild(root_0, char_literal301_tree);
                    }


                    }
                    break;
                case 8 :
                    // GraphlrJava.g:955:9: trystatement
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_trystatement_in_statement4500);
                    trystatement302=trystatement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, trystatement302.getTree());


                    }
                    break;
                case 9 :
                    // GraphlrJava.g:956:9: 'switch' parExpression '{' switchBlockStatementGroups '}'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal303=(Token)match(input,SWITCH,FOLLOW_SWITCH_in_statement4510); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal303_tree = 
                    (Tree)adaptor.create(string_literal303)
                    ;
                    adaptor.addChild(root_0, string_literal303_tree);
                    }


                    pushFollow(FOLLOW_parExpression_in_statement4512);
                    parExpression304=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression304.getTree());


                    char_literal305=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_statement4514); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal305_tree = 
                    (Tree)adaptor.create(char_literal305)
                    ;
                    adaptor.addChild(root_0, char_literal305_tree);
                    }


                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4516);
                    switchBlockStatementGroups306=switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroups306.getTree());


                    char_literal307=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_statement4518); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal307_tree = 
                    (Tree)adaptor.create(char_literal307)
                    ;
                    adaptor.addChild(root_0, char_literal307_tree);
                    }


                    }
                    break;
                case 10 :
                    // GraphlrJava.g:957:9: 'synchronized' parExpression block
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal308=(Token)match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_statement4528); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal308_tree = 
                    (Tree)adaptor.create(string_literal308)
                    ;
                    adaptor.addChild(root_0, string_literal308_tree);
                    }


                    pushFollow(FOLLOW_parExpression_in_statement4530);
                    parExpression309=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression309.getTree());


                    pushFollow(FOLLOW_block_in_statement4532);
                    block310=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block310.getTree());


                    }
                    break;
                case 11 :
                    // GraphlrJava.g:958:9: 'return' ( expression )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal311=(Token)match(input,RETURN,FOLLOW_RETURN_in_statement4542); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal311_tree = 
                    (Tree)adaptor.create(string_literal311)
                    ;
                    adaptor.addChild(root_0, string_literal311_tree);
                    }


                    // GraphlrJava.g:958:18: ( expression )?
                    int alt95=2;
                    int LA95_0 = input.LA(1);

                    if ( (LA95_0==BANG||LA95_0==BOOLEAN||LA95_0==BYTE||(LA95_0 >= CHAR && LA95_0 <= CHARLITERAL)||(LA95_0 >= DOUBLE && LA95_0 <= DOUBLELITERAL)||LA95_0==FALSE||(LA95_0 >= FLOAT && LA95_0 <= FLOATLITERAL)||LA95_0==IDENTIFIER||LA95_0==INT||LA95_0==INTLITERAL||(LA95_0 >= LONG && LA95_0 <= LPAREN)||(LA95_0 >= NEW && LA95_0 <= NULL)||LA95_0==PLUS||LA95_0==PLUSPLUS||LA95_0==SHORT||(LA95_0 >= STRINGLITERAL && LA95_0 <= SUB)||(LA95_0 >= SUBSUB && LA95_0 <= SUPER)||LA95_0==THIS||LA95_0==TILDE||LA95_0==TRUE||LA95_0==VOID) ) {
                        alt95=1;
                    }
                    switch (alt95) {
                        case 1 :
                            // GraphlrJava.g:958:19: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4545);
                            expression312=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression312.getTree());


                            }
                            break;

                    }


                    char_literal313=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4550); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal313_tree = 
                    (Tree)adaptor.create(char_literal313)
                    ;
                    adaptor.addChild(root_0, char_literal313_tree);
                    }


                    }
                    break;
                case 12 :
                    // GraphlrJava.g:959:9: 'throw' expression ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal314=(Token)match(input,THROW,FOLLOW_THROW_in_statement4560); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal314_tree = 
                    (Tree)adaptor.create(string_literal314)
                    ;
                    adaptor.addChild(root_0, string_literal314_tree);
                    }


                    pushFollow(FOLLOW_expression_in_statement4562);
                    expression315=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression315.getTree());


                    char_literal316=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4564); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal316_tree = 
                    (Tree)adaptor.create(char_literal316)
                    ;
                    adaptor.addChild(root_0, char_literal316_tree);
                    }


                    }
                    break;
                case 13 :
                    // GraphlrJava.g:960:9: 'break' ( IDENTIFIER )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal317=(Token)match(input,BREAK,FOLLOW_BREAK_in_statement4574); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal317_tree = 
                    (Tree)adaptor.create(string_literal317)
                    ;
                    adaptor.addChild(root_0, string_literal317_tree);
                    }


                    // GraphlrJava.g:961:13: ( IDENTIFIER )?
                    int alt96=2;
                    int LA96_0 = input.LA(1);

                    if ( (LA96_0==IDENTIFIER) ) {
                        alt96=1;
                    }
                    switch (alt96) {
                        case 1 :
                            // GraphlrJava.g:961:14: IDENTIFIER
                            {
                            IDENTIFIER318=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4589); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENTIFIER318_tree = 
                            (Tree)adaptor.create(IDENTIFIER318)
                            ;
                            adaptor.addChild(root_0, IDENTIFIER318_tree);
                            }


                            }
                            break;

                    }


                    char_literal319=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4606); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal319_tree = 
                    (Tree)adaptor.create(char_literal319)
                    ;
                    adaptor.addChild(root_0, char_literal319_tree);
                    }


                    }
                    break;
                case 14 :
                    // GraphlrJava.g:963:9: 'continue' ( IDENTIFIER )? ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal320=(Token)match(input,CONTINUE,FOLLOW_CONTINUE_in_statement4616); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal320_tree = 
                    (Tree)adaptor.create(string_literal320)
                    ;
                    adaptor.addChild(root_0, string_literal320_tree);
                    }


                    // GraphlrJava.g:964:13: ( IDENTIFIER )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==IDENTIFIER) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // GraphlrJava.g:964:14: IDENTIFIER
                            {
                            IDENTIFIER321=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4631); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENTIFIER321_tree = 
                            (Tree)adaptor.create(IDENTIFIER321)
                            ;
                            adaptor.addChild(root_0, IDENTIFIER321_tree);
                            }


                            }
                            break;

                    }


                    char_literal322=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4648); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal322_tree = 
                    (Tree)adaptor.create(char_literal322)
                    ;
                    adaptor.addChild(root_0, char_literal322_tree);
                    }


                    }
                    break;
                case 15 :
                    // GraphlrJava.g:966:9: expression ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_statement4658);
                    expression323=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression323.getTree());


                    char_literal324=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4661); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal324_tree = 
                    (Tree)adaptor.create(char_literal324)
                    ;
                    adaptor.addChild(root_0, char_literal324_tree);
                    }


                    }
                    break;
                case 16 :
                    // GraphlrJava.g:967:9: IDENTIFIER ':' statement
                    {
                    root_0 = (Tree)adaptor.nil();


                    IDENTIFIER325=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_statement4676); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER325_tree = 
                    (Tree)adaptor.create(IDENTIFIER325)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER325_tree);
                    }


                    char_literal326=(Token)match(input,COLON,FOLLOW_COLON_in_statement4678); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal326_tree = 
                    (Tree)adaptor.create(char_literal326)
                    ;
                    adaptor.addChild(root_0, char_literal326_tree);
                    }


                    pushFollow(FOLLOW_statement_in_statement4680);
                    statement327=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement327.getTree());


                    }
                    break;
                case 17 :
                    // GraphlrJava.g:968:9: ';'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal328=(Token)match(input,SEMI,FOLLOW_SEMI_in_statement4690); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal328_tree = 
                    (Tree)adaptor.create(char_literal328)
                    ;
                    adaptor.addChild(root_0, char_literal328_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 58, statement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "statement"


    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchBlockStatementGroups"
    // GraphlrJava.g:972:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final GraphlrJavaParser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        GraphlrJavaParser.switchBlockStatementGroups_return retval = new GraphlrJavaParser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);

        int switchBlockStatementGroups_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope switchBlockStatementGroup329 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }

            // GraphlrJava.g:973:5: ( ( switchBlockStatementGroup )* )
            // GraphlrJava.g:973:9: ( switchBlockStatementGroup )*
            {
            root_0 = (Tree)adaptor.nil();


            // GraphlrJava.g:973:9: ( switchBlockStatementGroup )*
            loop99:
            do {
                int alt99=2;
                int LA99_0 = input.LA(1);

                if ( (LA99_0==CASE||LA99_0==DEFAULT) ) {
                    alt99=1;
                }


                switch (alt99) {
            	case 1 :
            	    // GraphlrJava.g:973:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4712);
            	    switchBlockStatementGroup329=switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, switchBlockStatementGroup329.getTree());


            	    }
            	    break;

            	default :
            	    break loop99;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 59, switchBlockStatementGroups_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroups"


    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchBlockStatementGroup"
    // GraphlrJava.g:976:1: switchBlockStatementGroup : switchLabel ( blockStatement )* ;
    public final GraphlrJavaParser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        GraphlrJavaParser.switchBlockStatementGroup_return retval = new GraphlrJavaParser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);

        int switchBlockStatementGroup_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope switchLabel330 =null;

        ParserRuleReturnScope blockStatement331 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }

            // GraphlrJava.g:977:5: ( switchLabel ( blockStatement )* )
            // GraphlrJava.g:978:9: switchLabel ( blockStatement )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup4741);
            switchLabel330=switchLabel();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, switchLabel330.getTree());


            // GraphlrJava.g:979:9: ( blockStatement )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( (LA100_0==ABSTRACT||(LA100_0 >= ASSERT && LA100_0 <= BANG)||(LA100_0 >= BOOLEAN && LA100_0 <= BYTE)||(LA100_0 >= CHAR && LA100_0 <= CLASS)||LA100_0==CONTINUE||LA100_0==DO||(LA100_0 >= DOUBLE && LA100_0 <= DOUBLELITERAL)||LA100_0==ENUM||(LA100_0 >= FALSE && LA100_0 <= FINAL)||(LA100_0 >= FLOAT && LA100_0 <= FOR)||(LA100_0 >= IDENTIFIER && LA100_0 <= IF)||(LA100_0 >= INT && LA100_0 <= INTLITERAL)||LA100_0==LBRACE||(LA100_0 >= LONG && LA100_0 <= LT)||(LA100_0 >= MONKEYS_AT && LA100_0 <= NULL)||LA100_0==PLUS||(LA100_0 >= PLUSPLUS && LA100_0 <= PUBLIC)||LA100_0==RETURN||(LA100_0 >= SEMI && LA100_0 <= SHORT)||(LA100_0 >= STATIC && LA100_0 <= SUB)||(LA100_0 >= SUBSUB && LA100_0 <= SYNCHRONIZED)||(LA100_0 >= THIS && LA100_0 <= THROW)||(LA100_0 >= TILDE && LA100_0 <= WHILE)) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // GraphlrJava.g:979:10: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup4752);
            	    blockStatement331=blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, blockStatement331.getTree());


            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 60, switchBlockStatementGroup_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroup"


    public static class switchLabel_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "switchLabel"
    // GraphlrJava.g:983:1: switchLabel : ( 'case' expression ':' | 'default' ':' );
    public final GraphlrJavaParser.switchLabel_return switchLabel() throws RecognitionException {
        GraphlrJavaParser.switchLabel_return retval = new GraphlrJavaParser.switchLabel_return();
        retval.start = input.LT(1);

        int switchLabel_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal332=null;
        Token char_literal334=null;
        Token string_literal335=null;
        Token char_literal336=null;
        ParserRuleReturnScope expression333 =null;


        Tree string_literal332_tree=null;
        Tree char_literal334_tree=null;
        Tree string_literal335_tree=null;
        Tree char_literal336_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }

            // GraphlrJava.g:984:5: ( 'case' expression ':' | 'default' ':' )
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==CASE) ) {
                alt101=1;
            }
            else if ( (LA101_0==DEFAULT) ) {
                alt101=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 101, 0, input);

            	throw nvae;
            }
            switch (alt101) {
                case 1 :
                    // GraphlrJava.g:984:9: 'case' expression ':'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal332=(Token)match(input,CASE,FOLLOW_CASE_in_switchLabel4783); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal332_tree = 
                    (Tree)adaptor.create(string_literal332)
                    ;
                    adaptor.addChild(root_0, string_literal332_tree);
                    }


                    pushFollow(FOLLOW_expression_in_switchLabel4785);
                    expression333=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression333.getTree());


                    char_literal334=(Token)match(input,COLON,FOLLOW_COLON_in_switchLabel4787); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal334_tree = 
                    (Tree)adaptor.create(char_literal334)
                    ;
                    adaptor.addChild(root_0, char_literal334_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:985:9: 'default' ':'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal335=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_switchLabel4797); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal335_tree = 
                    (Tree)adaptor.create(string_literal335)
                    ;
                    adaptor.addChild(root_0, string_literal335_tree);
                    }


                    char_literal336=(Token)match(input,COLON,FOLLOW_COLON_in_switchLabel4799); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal336_tree = 
                    (Tree)adaptor.create(char_literal336)
                    ;
                    adaptor.addChild(root_0, char_literal336_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 61, switchLabel_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "switchLabel"


    public static class trystatement_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "trystatement"
    // GraphlrJava.g:989:1: trystatement : 'try' block ( catches 'finally' block | catches | 'finally' block ) ;
    public final GraphlrJavaParser.trystatement_return trystatement() throws RecognitionException {
        GraphlrJavaParser.trystatement_return retval = new GraphlrJavaParser.trystatement_return();
        retval.start = input.LT(1);

        int trystatement_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal337=null;
        Token string_literal340=null;
        Token string_literal343=null;
        ParserRuleReturnScope block338 =null;

        ParserRuleReturnScope catches339 =null;

        ParserRuleReturnScope block341 =null;

        ParserRuleReturnScope catches342 =null;

        ParserRuleReturnScope block344 =null;


        Tree string_literal337_tree=null;
        Tree string_literal340_tree=null;
        Tree string_literal343_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }

            // GraphlrJava.g:990:5: ( 'try' block ( catches 'finally' block | catches | 'finally' block ) )
            // GraphlrJava.g:990:9: 'try' block ( catches 'finally' block | catches | 'finally' block )
            {
            root_0 = (Tree)adaptor.nil();


            string_literal337=(Token)match(input,TRY,FOLLOW_TRY_in_trystatement4820); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal337_tree = 
            (Tree)adaptor.create(string_literal337)
            ;
            adaptor.addChild(root_0, string_literal337_tree);
            }


            pushFollow(FOLLOW_block_in_trystatement4822);
            block338=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block338.getTree());


            // GraphlrJava.g:991:9: ( catches 'finally' block | catches | 'finally' block )
            int alt102=3;
            int LA102_0 = input.LA(1);

            if ( (LA102_0==CATCH) ) {
                int LA102_1 = input.LA(2);

                if ( (synpred153_GraphlrJava()) ) {
                    alt102=1;
                }
                else if ( (synpred154_GraphlrJava()) ) {
                    alt102=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 102, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else if ( (LA102_0==FINALLY) ) {
                alt102=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 102, 0, input);

            	throw nvae;
            }
            switch (alt102) {
                case 1 :
                    // GraphlrJava.g:991:13: catches 'finally' block
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4836);
                    catches339=catches();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, catches339.getTree());


                    string_literal340=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_trystatement4838); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal340_tree = 
                    (Tree)adaptor.create(string_literal340)
                    ;
                    adaptor.addChild(root_0, string_literal340_tree);
                    }


                    pushFollow(FOLLOW_block_in_trystatement4840);
                    block341=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block341.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:992:13: catches
                    {
                    pushFollow(FOLLOW_catches_in_trystatement4854);
                    catches342=catches();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, catches342.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:993:13: 'finally' block
                    {
                    string_literal343=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_trystatement4868); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal343_tree = 
                    (Tree)adaptor.create(string_literal343)
                    ;
                    adaptor.addChild(root_0, string_literal343_tree);
                    }


                    pushFollow(FOLLOW_block_in_trystatement4870);
                    block344=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block344.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 62, trystatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "trystatement"


    public static class catches_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "catches"
    // GraphlrJava.g:997:1: catches : catchClause ( catchClause )* ;
    public final GraphlrJavaParser.catches_return catches() throws RecognitionException {
        GraphlrJavaParser.catches_return retval = new GraphlrJavaParser.catches_return();
        retval.start = input.LT(1);

        int catches_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope catchClause345 =null;

        ParserRuleReturnScope catchClause346 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }

            // GraphlrJava.g:998:5: ( catchClause ( catchClause )* )
            // GraphlrJava.g:998:9: catchClause ( catchClause )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_catchClause_in_catches4901);
            catchClause345=catchClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause345.getTree());


            // GraphlrJava.g:999:9: ( catchClause )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);

                if ( (LA103_0==CATCH) ) {
                    alt103=1;
                }


                switch (alt103) {
            	case 1 :
            	    // GraphlrJava.g:999:10: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches4912);
            	    catchClause346=catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, catchClause346.getTree());


            	    }
            	    break;

            	default :
            	    break loop103;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 63, catches_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "catches"


    public static class catchClause_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "catchClause"
    // GraphlrJava.g:1003:1: catchClause : 'catch' '(' formalParameter ')' block ;
    public final GraphlrJavaParser.catchClause_return catchClause() throws RecognitionException {
        GraphlrJavaParser.catchClause_return retval = new GraphlrJavaParser.catchClause_return();
        retval.start = input.LT(1);

        int catchClause_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal347=null;
        Token char_literal348=null;
        Token char_literal350=null;
        ParserRuleReturnScope formalParameter349 =null;

        ParserRuleReturnScope block351 =null;


        Tree string_literal347_tree=null;
        Tree char_literal348_tree=null;
        Tree char_literal350_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }

            // GraphlrJava.g:1004:5: ( 'catch' '(' formalParameter ')' block )
            // GraphlrJava.g:1004:9: 'catch' '(' formalParameter ')' block
            {
            root_0 = (Tree)adaptor.nil();


            string_literal347=(Token)match(input,CATCH,FOLLOW_CATCH_in_catchClause4943); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal347_tree = 
            (Tree)adaptor.create(string_literal347)
            ;
            adaptor.addChild(root_0, string_literal347_tree);
            }


            char_literal348=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_catchClause4945); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal348_tree = 
            (Tree)adaptor.create(char_literal348)
            ;
            adaptor.addChild(root_0, char_literal348_tree);
            }


            pushFollow(FOLLOW_formalParameter_in_catchClause4947);
            formalParameter349=formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, formalParameter349.getTree());


            char_literal350=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_catchClause4957); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal350_tree = 
            (Tree)adaptor.create(char_literal350)
            ;
            adaptor.addChild(root_0, char_literal350_tree);
            }


            pushFollow(FOLLOW_block_in_catchClause4959);
            block351=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block351.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 64, catchClause_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "catchClause"


    public static class formalParameter_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formalParameter"
    // GraphlrJava.g:1008:1: formalParameter : variableModifiers type IDENTIFIER ( '[' ']' )* ;
    public final GraphlrJavaParser.formalParameter_return formalParameter() throws RecognitionException {
        GraphlrJavaParser.formalParameter_return retval = new GraphlrJavaParser.formalParameter_return();
        retval.start = input.LT(1);

        int formalParameter_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER354=null;
        Token char_literal355=null;
        Token char_literal356=null;
        ParserRuleReturnScope variableModifiers352 =null;

        ParserRuleReturnScope type353 =null;


        Tree IDENTIFIER354_tree=null;
        Tree char_literal355_tree=null;
        Tree char_literal356_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }

            // GraphlrJava.g:1009:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* )
            // GraphlrJava.g:1009:9: variableModifiers type IDENTIFIER ( '[' ']' )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_formalParameter4980);
            variableModifiers352=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers352.getTree());


            pushFollow(FOLLOW_type_in_formalParameter4982);
            type353=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type353.getTree());


            IDENTIFIER354=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_formalParameter4984); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER354_tree = 
            (Tree)adaptor.create(IDENTIFIER354)
            ;
            adaptor.addChild(root_0, IDENTIFIER354_tree);
            }


            // GraphlrJava.g:1010:9: ( '[' ']' )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==LBRACKET) ) {
                    alt104=1;
                }


                switch (alt104) {
            	case 1 :
            	    // GraphlrJava.g:1010:10: '[' ']'
            	    {
            	    char_literal355=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_formalParameter4995); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal355_tree = 
            	    (Tree)adaptor.create(char_literal355)
            	    ;
            	    adaptor.addChild(root_0, char_literal355_tree);
            	    }


            	    char_literal356=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_formalParameter4997); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal356_tree = 
            	    (Tree)adaptor.create(char_literal356)
            	    ;
            	    adaptor.addChild(root_0, char_literal356_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 65, formalParameter_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "formalParameter"


    public static class forstatement_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "forstatement"
    // GraphlrJava.g:1014:1: forstatement : ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement );
    public final GraphlrJavaParser.forstatement_return forstatement() throws RecognitionException {
        GraphlrJavaParser.forstatement_return retval = new GraphlrJavaParser.forstatement_return();
        retval.start = input.LT(1);

        int forstatement_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal357=null;
        Token char_literal358=null;
        Token IDENTIFIER361=null;
        Token char_literal362=null;
        Token char_literal364=null;
        Token string_literal366=null;
        Token char_literal367=null;
        Token char_literal369=null;
        Token char_literal371=null;
        Token char_literal373=null;
        ParserRuleReturnScope variableModifiers359 =null;

        ParserRuleReturnScope type360 =null;

        ParserRuleReturnScope expression363 =null;

        ParserRuleReturnScope statement365 =null;

        ParserRuleReturnScope forInit368 =null;

        ParserRuleReturnScope expression370 =null;

        ParserRuleReturnScope expressionList372 =null;

        ParserRuleReturnScope statement374 =null;


        Tree string_literal357_tree=null;
        Tree char_literal358_tree=null;
        Tree IDENTIFIER361_tree=null;
        Tree char_literal362_tree=null;
        Tree char_literal364_tree=null;
        Tree string_literal366_tree=null;
        Tree char_literal367_tree=null;
        Tree char_literal369_tree=null;
        Tree char_literal371_tree=null;
        Tree char_literal373_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }

            // GraphlrJava.g:1015:5: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement | 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement )
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( (LA108_0==FOR) ) {
                int LA108_1 = input.LA(2);

                if ( (synpred157_GraphlrJava()) ) {
                    alt108=1;
                }
                else if ( (true) ) {
                    alt108=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 108, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 108, 0, input);

            	throw nvae;
            }
            switch (alt108) {
                case 1 :
                    // GraphlrJava.g:1017:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal357=(Token)match(input,FOR,FOLLOW_FOR_in_forstatement5046); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal357_tree = 
                    (Tree)adaptor.create(string_literal357)
                    ;
                    adaptor.addChild(root_0, string_literal357_tree);
                    }


                    char_literal358=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5048); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal358_tree = 
                    (Tree)adaptor.create(char_literal358)
                    ;
                    adaptor.addChild(root_0, char_literal358_tree);
                    }


                    pushFollow(FOLLOW_variableModifiers_in_forstatement5050);
                    variableModifiers359=variableModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers359.getTree());


                    pushFollow(FOLLOW_type_in_forstatement5052);
                    type360=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type360.getTree());


                    IDENTIFIER361=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_forstatement5054); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER361_tree = 
                    (Tree)adaptor.create(IDENTIFIER361)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER361_tree);
                    }


                    char_literal362=(Token)match(input,COLON,FOLLOW_COLON_in_forstatement5056); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal362_tree = 
                    (Tree)adaptor.create(char_literal362)
                    ;
                    adaptor.addChild(root_0, char_literal362_tree);
                    }


                    pushFollow(FOLLOW_expression_in_forstatement5067);
                    expression363=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression363.getTree());


                    char_literal364=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5069); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal364_tree = 
                    (Tree)adaptor.create(char_literal364)
                    ;
                    adaptor.addChild(root_0, char_literal364_tree);
                    }


                    pushFollow(FOLLOW_statement_in_forstatement5071);
                    statement365=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement365.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1021:9: 'for' '(' ( forInit )? ';' ( expression )? ';' ( expressionList )? ')' statement
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal366=(Token)match(input,FOR,FOLLOW_FOR_in_forstatement5103); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal366_tree = 
                    (Tree)adaptor.create(string_literal366)
                    ;
                    adaptor.addChild(root_0, string_literal366_tree);
                    }


                    char_literal367=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_forstatement5105); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal367_tree = 
                    (Tree)adaptor.create(char_literal367)
                    ;
                    adaptor.addChild(root_0, char_literal367_tree);
                    }


                    // GraphlrJava.g:1022:17: ( forInit )?
                    int alt105=2;
                    int LA105_0 = input.LA(1);

                    if ( (LA105_0==BANG||LA105_0==BOOLEAN||LA105_0==BYTE||(LA105_0 >= CHAR && LA105_0 <= CHARLITERAL)||(LA105_0 >= DOUBLE && LA105_0 <= DOUBLELITERAL)||(LA105_0 >= FALSE && LA105_0 <= FINAL)||(LA105_0 >= FLOAT && LA105_0 <= FLOATLITERAL)||LA105_0==IDENTIFIER||LA105_0==INT||LA105_0==INTLITERAL||(LA105_0 >= LONG && LA105_0 <= LPAREN)||LA105_0==MONKEYS_AT||(LA105_0 >= NEW && LA105_0 <= NULL)||LA105_0==PLUS||LA105_0==PLUSPLUS||LA105_0==SHORT||(LA105_0 >= STRINGLITERAL && LA105_0 <= SUB)||(LA105_0 >= SUBSUB && LA105_0 <= SUPER)||LA105_0==THIS||LA105_0==TILDE||LA105_0==TRUE||LA105_0==VOID) ) {
                        alt105=1;
                    }
                    switch (alt105) {
                        case 1 :
                            // GraphlrJava.g:1022:18: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forstatement5125);
                            forInit368=forInit();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, forInit368.getTree());


                            }
                            break;

                    }


                    char_literal369=(Token)match(input,SEMI,FOLLOW_SEMI_in_forstatement5146); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal369_tree = 
                    (Tree)adaptor.create(char_literal369)
                    ;
                    adaptor.addChild(root_0, char_literal369_tree);
                    }


                    // GraphlrJava.g:1024:17: ( expression )?
                    int alt106=2;
                    int LA106_0 = input.LA(1);

                    if ( (LA106_0==BANG||LA106_0==BOOLEAN||LA106_0==BYTE||(LA106_0 >= CHAR && LA106_0 <= CHARLITERAL)||(LA106_0 >= DOUBLE && LA106_0 <= DOUBLELITERAL)||LA106_0==FALSE||(LA106_0 >= FLOAT && LA106_0 <= FLOATLITERAL)||LA106_0==IDENTIFIER||LA106_0==INT||LA106_0==INTLITERAL||(LA106_0 >= LONG && LA106_0 <= LPAREN)||(LA106_0 >= NEW && LA106_0 <= NULL)||LA106_0==PLUS||LA106_0==PLUSPLUS||LA106_0==SHORT||(LA106_0 >= STRINGLITERAL && LA106_0 <= SUB)||(LA106_0 >= SUBSUB && LA106_0 <= SUPER)||LA106_0==THIS||LA106_0==TILDE||LA106_0==TRUE||LA106_0==VOID) ) {
                        alt106=1;
                    }
                    switch (alt106) {
                        case 1 :
                            // GraphlrJava.g:1024:18: expression
                            {
                            pushFollow(FOLLOW_expression_in_forstatement5166);
                            expression370=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression370.getTree());


                            }
                            break;

                    }


                    char_literal371=(Token)match(input,SEMI,FOLLOW_SEMI_in_forstatement5187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal371_tree = 
                    (Tree)adaptor.create(char_literal371)
                    ;
                    adaptor.addChild(root_0, char_literal371_tree);
                    }


                    // GraphlrJava.g:1026:17: ( expressionList )?
                    int alt107=2;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==BANG||LA107_0==BOOLEAN||LA107_0==BYTE||(LA107_0 >= CHAR && LA107_0 <= CHARLITERAL)||(LA107_0 >= DOUBLE && LA107_0 <= DOUBLELITERAL)||LA107_0==FALSE||(LA107_0 >= FLOAT && LA107_0 <= FLOATLITERAL)||LA107_0==IDENTIFIER||LA107_0==INT||LA107_0==INTLITERAL||(LA107_0 >= LONG && LA107_0 <= LPAREN)||(LA107_0 >= NEW && LA107_0 <= NULL)||LA107_0==PLUS||LA107_0==PLUSPLUS||LA107_0==SHORT||(LA107_0 >= STRINGLITERAL && LA107_0 <= SUB)||(LA107_0 >= SUBSUB && LA107_0 <= SUPER)||LA107_0==THIS||LA107_0==TILDE||LA107_0==TRUE||LA107_0==VOID) ) {
                        alt107=1;
                    }
                    switch (alt107) {
                        case 1 :
                            // GraphlrJava.g:1026:18: expressionList
                            {
                            pushFollow(FOLLOW_expressionList_in_forstatement5207);
                            expressionList372=expressionList();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList372.getTree());


                            }
                            break;

                    }


                    char_literal373=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_forstatement5228); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal373_tree = 
                    (Tree)adaptor.create(char_literal373)
                    ;
                    adaptor.addChild(root_0, char_literal373_tree);
                    }


                    pushFollow(FOLLOW_statement_in_forstatement5230);
                    statement374=statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, statement374.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 66, forstatement_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "forstatement"


    public static class forInit_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "forInit"
    // GraphlrJava.g:1030:1: forInit : ( localVariableDeclaration | expressionList );
    public final GraphlrJavaParser.forInit_return forInit() throws RecognitionException {
        GraphlrJavaParser.forInit_return retval = new GraphlrJavaParser.forInit_return();
        retval.start = input.LT(1);

        int forInit_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope localVariableDeclaration375 =null;

        ParserRuleReturnScope expressionList376 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }

            // GraphlrJava.g:1031:5: ( localVariableDeclaration | expressionList )
            int alt109=2;
            switch ( input.LA(1) ) {
            case FINAL:
            case MONKEYS_AT:
                {
                alt109=1;
                }
                break;
            case IDENTIFIER:
                {
                int LA109_3 = input.LA(2);

                if ( (synpred161_GraphlrJava()) ) {
                    alt109=1;
                }
                else if ( (true) ) {
                    alt109=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 109, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA109_4 = input.LA(2);

                if ( (synpred161_GraphlrJava()) ) {
                    alt109=1;
                }
                else if ( (true) ) {
                    alt109=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 109, 4, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BANG:
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case PLUS:
            case PLUSPLUS:
            case STRINGLITERAL:
            case SUB:
            case SUBSUB:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt109=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 109, 0, input);

            	throw nvae;
            }

            switch (alt109) {
                case 1 :
                    // GraphlrJava.g:1031:9: localVariableDeclaration
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit5250);
                    localVariableDeclaration375=localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, localVariableDeclaration375.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1032:9: expressionList
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_expressionList_in_forInit5260);
                    expressionList376=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList376.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 67, forInit_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "forInit"


    public static class parExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "parExpression"
    // GraphlrJava.g:1035:1: parExpression : '(' expression ')' ;
    public final GraphlrJavaParser.parExpression_return parExpression() throws RecognitionException {
        GraphlrJavaParser.parExpression_return retval = new GraphlrJavaParser.parExpression_return();
        retval.start = input.LT(1);

        int parExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal377=null;
        Token char_literal379=null;
        ParserRuleReturnScope expression378 =null;


        Tree char_literal377_tree=null;
        Tree char_literal379_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }

            // GraphlrJava.g:1036:5: ( '(' expression ')' )
            // GraphlrJava.g:1036:9: '(' expression ')'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal377=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_parExpression5280); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal377_tree = 
            (Tree)adaptor.create(char_literal377)
            ;
            adaptor.addChild(root_0, char_literal377_tree);
            }


            pushFollow(FOLLOW_expression_in_parExpression5282);
            expression378=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression378.getTree());


            char_literal379=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_parExpression5284); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal379_tree = 
            (Tree)adaptor.create(char_literal379)
            ;
            adaptor.addChild(root_0, char_literal379_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 68, parExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "parExpression"


    public static class expressionList_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expressionList"
    // GraphlrJava.g:1039:1: expressionList : expression ( ',' expression )* ;
    public final GraphlrJavaParser.expressionList_return expressionList() throws RecognitionException {
        GraphlrJavaParser.expressionList_return retval = new GraphlrJavaParser.expressionList_return();
        retval.start = input.LT(1);

        int expressionList_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal381=null;
        ParserRuleReturnScope expression380 =null;

        ParserRuleReturnScope expression382 =null;


        Tree char_literal381_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }

            // GraphlrJava.g:1040:5: ( expression ( ',' expression )* )
            // GraphlrJava.g:1040:9: expression ( ',' expression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_expression_in_expressionList5304);
            expression380=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression380.getTree());


            // GraphlrJava.g:1041:9: ( ',' expression )*
            loop110:
            do {
                int alt110=2;
                int LA110_0 = input.LA(1);

                if ( (LA110_0==COMMA) ) {
                    alt110=1;
                }


                switch (alt110) {
            	case 1 :
            	    // GraphlrJava.g:1041:10: ',' expression
            	    {
            	    char_literal381=(Token)match(input,COMMA,FOLLOW_COMMA_in_expressionList5315); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal381_tree = 
            	    (Tree)adaptor.create(char_literal381)
            	    ;
            	    adaptor.addChild(root_0, char_literal381_tree);
            	    }


            	    pushFollow(FOLLOW_expression_in_expressionList5317);
            	    expression382=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression382.getTree());


            	    }
            	    break;

            	default :
            	    break loop110;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 69, expressionList_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "expressionList"


    public static class expression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expression"
    // GraphlrJava.g:1046:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final GraphlrJavaParser.expression_return expression() throws RecognitionException {
        GraphlrJavaParser.expression_return retval = new GraphlrJavaParser.expression_return();
        retval.start = input.LT(1);

        int expression_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope conditionalExpression383 =null;

        ParserRuleReturnScope assignmentOperator384 =null;

        ParserRuleReturnScope expression385 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }

            // GraphlrJava.g:1047:5: ( conditionalExpression ( assignmentOperator expression )? )
            // GraphlrJava.g:1047:9: conditionalExpression ( assignmentOperator expression )?
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_conditionalExpression_in_expression5349);
            conditionalExpression383=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression383.getTree());


            // GraphlrJava.g:1048:9: ( assignmentOperator expression )?
            int alt111=2;
            int LA111_0 = input.LA(1);

            if ( (LA111_0==AMPEQ||LA111_0==BAREQ||LA111_0==CARETEQ||LA111_0==EQ||LA111_0==GT||LA111_0==LT||LA111_0==PERCENTEQ||LA111_0==PLUSEQ||LA111_0==SLASHEQ||LA111_0==STAREQ||LA111_0==SUBEQ) ) {
                alt111=1;
            }
            switch (alt111) {
                case 1 :
                    // GraphlrJava.g:1048:10: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression5360);
                    assignmentOperator384=assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, assignmentOperator384.getTree());


                    pushFollow(FOLLOW_expression_in_expression5362);
                    expression385=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression385.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 70, expression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "expression"


    public static class assignmentOperator_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assignmentOperator"
    // GraphlrJava.g:1053:1: assignmentOperator : ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' );
    public final GraphlrJavaParser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        GraphlrJavaParser.assignmentOperator_return retval = new GraphlrJavaParser.assignmentOperator_return();
        retval.start = input.LT(1);

        int assignmentOperator_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal386=null;
        Token string_literal387=null;
        Token string_literal388=null;
        Token string_literal389=null;
        Token string_literal390=null;
        Token string_literal391=null;
        Token string_literal392=null;
        Token string_literal393=null;
        Token string_literal394=null;
        Token char_literal395=null;
        Token char_literal396=null;
        Token char_literal397=null;
        Token char_literal398=null;
        Token char_literal399=null;
        Token char_literal400=null;
        Token char_literal401=null;
        Token char_literal402=null;
        Token char_literal403=null;
        Token char_literal404=null;

        Tree char_literal386_tree=null;
        Tree string_literal387_tree=null;
        Tree string_literal388_tree=null;
        Tree string_literal389_tree=null;
        Tree string_literal390_tree=null;
        Tree string_literal391_tree=null;
        Tree string_literal392_tree=null;
        Tree string_literal393_tree=null;
        Tree string_literal394_tree=null;
        Tree char_literal395_tree=null;
        Tree char_literal396_tree=null;
        Tree char_literal397_tree=null;
        Tree char_literal398_tree=null;
        Tree char_literal399_tree=null;
        Tree char_literal400_tree=null;
        Tree char_literal401_tree=null;
        Tree char_literal402_tree=null;
        Tree char_literal403_tree=null;
        Tree char_literal404_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }

            // GraphlrJava.g:1054:5: ( '=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<' '<' '=' | '>' '>' '>' '=' | '>' '>' '=' )
            int alt112=12;
            switch ( input.LA(1) ) {
            case EQ:
                {
                alt112=1;
                }
                break;
            case PLUSEQ:
                {
                alt112=2;
                }
                break;
            case SUBEQ:
                {
                alt112=3;
                }
                break;
            case STAREQ:
                {
                alt112=4;
                }
                break;
            case SLASHEQ:
                {
                alt112=5;
                }
                break;
            case AMPEQ:
                {
                alt112=6;
                }
                break;
            case BAREQ:
                {
                alt112=7;
                }
                break;
            case CARETEQ:
                {
                alt112=8;
                }
                break;
            case PERCENTEQ:
                {
                alt112=9;
                }
                break;
            case LT:
                {
                alt112=10;
                }
                break;
            case GT:
                {
                int LA112_11 = input.LA(2);

                if ( (LA112_11==GT) ) {
                    int LA112_12 = input.LA(3);

                    if ( (LA112_12==GT) ) {
                        alt112=11;
                    }
                    else if ( (LA112_12==EQ) ) {
                        alt112=12;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	int nvaeMark = input.mark();
                    	try {
                    		for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++)
                    			input.consume();
                    		NoViableAltException nvae =
                    			new NoViableAltException("", 112, 12, input);

                    		throw nvae;
                    	} finally {
                    		input.rewind(nvaeMark);
                    	}
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 112, 11, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 112, 0, input);

            	throw nvae;
            }

            switch (alt112) {
                case 1 :
                    // GraphlrJava.g:1054:9: '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal386=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5394); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal386_tree = 
                    (Tree)adaptor.create(char_literal386)
                    ;
                    adaptor.addChild(root_0, char_literal386_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1055:9: '+='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal387=(Token)match(input,PLUSEQ,FOLLOW_PLUSEQ_in_assignmentOperator5404); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal387_tree = 
                    (Tree)adaptor.create(string_literal387)
                    ;
                    adaptor.addChild(root_0, string_literal387_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1056:9: '-='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal388=(Token)match(input,SUBEQ,FOLLOW_SUBEQ_in_assignmentOperator5414); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal388_tree = 
                    (Tree)adaptor.create(string_literal388)
                    ;
                    adaptor.addChild(root_0, string_literal388_tree);
                    }


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1057:9: '*='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal389=(Token)match(input,STAREQ,FOLLOW_STAREQ_in_assignmentOperator5424); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal389_tree = 
                    (Tree)adaptor.create(string_literal389)
                    ;
                    adaptor.addChild(root_0, string_literal389_tree);
                    }


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:1058:9: '/='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal390=(Token)match(input,SLASHEQ,FOLLOW_SLASHEQ_in_assignmentOperator5434); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal390_tree = 
                    (Tree)adaptor.create(string_literal390)
                    ;
                    adaptor.addChild(root_0, string_literal390_tree);
                    }


                    }
                    break;
                case 6 :
                    // GraphlrJava.g:1059:9: '&='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal391=(Token)match(input,AMPEQ,FOLLOW_AMPEQ_in_assignmentOperator5444); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal391_tree = 
                    (Tree)adaptor.create(string_literal391)
                    ;
                    adaptor.addChild(root_0, string_literal391_tree);
                    }


                    }
                    break;
                case 7 :
                    // GraphlrJava.g:1060:9: '|='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal392=(Token)match(input,BAREQ,FOLLOW_BAREQ_in_assignmentOperator5454); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal392_tree = 
                    (Tree)adaptor.create(string_literal392)
                    ;
                    adaptor.addChild(root_0, string_literal392_tree);
                    }


                    }
                    break;
                case 8 :
                    // GraphlrJava.g:1061:9: '^='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal393=(Token)match(input,CARETEQ,FOLLOW_CARETEQ_in_assignmentOperator5464); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal393_tree = 
                    (Tree)adaptor.create(string_literal393)
                    ;
                    adaptor.addChild(root_0, string_literal393_tree);
                    }


                    }
                    break;
                case 9 :
                    // GraphlrJava.g:1062:9: '%='
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal394=(Token)match(input,PERCENTEQ,FOLLOW_PERCENTEQ_in_assignmentOperator5474); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal394_tree = 
                    (Tree)adaptor.create(string_literal394)
                    ;
                    adaptor.addChild(root_0, string_literal394_tree);
                    }


                    }
                    break;
                case 10 :
                    // GraphlrJava.g:1063:10: '<' '<' '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal395=(Token)match(input,LT,FOLLOW_LT_in_assignmentOperator5485); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal395_tree = 
                    (Tree)adaptor.create(char_literal395)
                    ;
                    adaptor.addChild(root_0, char_literal395_tree);
                    }


                    char_literal396=(Token)match(input,LT,FOLLOW_LT_in_assignmentOperator5487); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal396_tree = 
                    (Tree)adaptor.create(char_literal396)
                    ;
                    adaptor.addChild(root_0, char_literal396_tree);
                    }


                    char_literal397=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5489); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal397_tree = 
                    (Tree)adaptor.create(char_literal397)
                    ;
                    adaptor.addChild(root_0, char_literal397_tree);
                    }


                    }
                    break;
                case 11 :
                    // GraphlrJava.g:1064:10: '>' '>' '>' '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal398=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5500); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal398_tree = 
                    (Tree)adaptor.create(char_literal398)
                    ;
                    adaptor.addChild(root_0, char_literal398_tree);
                    }


                    char_literal399=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5502); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal399_tree = 
                    (Tree)adaptor.create(char_literal399)
                    ;
                    adaptor.addChild(root_0, char_literal399_tree);
                    }


                    char_literal400=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5504); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal400_tree = 
                    (Tree)adaptor.create(char_literal400)
                    ;
                    adaptor.addChild(root_0, char_literal400_tree);
                    }


                    char_literal401=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5506); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal401_tree = 
                    (Tree)adaptor.create(char_literal401)
                    ;
                    adaptor.addChild(root_0, char_literal401_tree);
                    }


                    }
                    break;
                case 12 :
                    // GraphlrJava.g:1065:10: '>' '>' '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal402=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5517); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal402_tree = 
                    (Tree)adaptor.create(char_literal402)
                    ;
                    adaptor.addChild(root_0, char_literal402_tree);
                    }


                    char_literal403=(Token)match(input,GT,FOLLOW_GT_in_assignmentOperator5519); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal403_tree = 
                    (Tree)adaptor.create(char_literal403)
                    ;
                    adaptor.addChild(root_0, char_literal403_tree);
                    }


                    char_literal404=(Token)match(input,EQ,FOLLOW_EQ_in_assignmentOperator5521); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal404_tree = 
                    (Tree)adaptor.create(char_literal404)
                    ;
                    adaptor.addChild(root_0, char_literal404_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 71, assignmentOperator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"


    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalExpression"
    // GraphlrJava.g:1069:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final GraphlrJavaParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        GraphlrJavaParser.conditionalExpression_return retval = new GraphlrJavaParser.conditionalExpression_return();
        retval.start = input.LT(1);

        int conditionalExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal406=null;
        Token char_literal408=null;
        ParserRuleReturnScope conditionalOrExpression405 =null;

        ParserRuleReturnScope expression407 =null;

        ParserRuleReturnScope conditionalExpression409 =null;


        Tree char_literal406_tree=null;
        Tree char_literal408_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }

            // GraphlrJava.g:1070:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // GraphlrJava.g:1070:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5542);
            conditionalOrExpression405=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression405.getTree());


            // GraphlrJava.g:1071:9: ( '?' expression ':' conditionalExpression )?
            int alt113=2;
            int LA113_0 = input.LA(1);

            if ( (LA113_0==QUES) ) {
                alt113=1;
            }
            switch (alt113) {
                case 1 :
                    // GraphlrJava.g:1071:10: '?' expression ':' conditionalExpression
                    {
                    char_literal406=(Token)match(input,QUES,FOLLOW_QUES_in_conditionalExpression5553); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal406_tree = 
                    (Tree)adaptor.create(char_literal406)
                    ;
                    adaptor.addChild(root_0, char_literal406_tree);
                    }


                    pushFollow(FOLLOW_expression_in_conditionalExpression5555);
                    expression407=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression407.getTree());


                    char_literal408=(Token)match(input,COLON,FOLLOW_COLON_in_conditionalExpression5557); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal408_tree = 
                    (Tree)adaptor.create(char_literal408)
                    ;
                    adaptor.addChild(root_0, char_literal408_tree);
                    }


                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression5559);
                    conditionalExpression409=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression409.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 72, conditionalExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"


    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalOrExpression"
    // GraphlrJava.g:1075:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final GraphlrJavaParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        GraphlrJavaParser.conditionalOrExpression_return retval = new GraphlrJavaParser.conditionalOrExpression_return();
        retval.start = input.LT(1);

        int conditionalOrExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal411=null;
        ParserRuleReturnScope conditionalAndExpression410 =null;

        ParserRuleReturnScope conditionalAndExpression412 =null;


        Tree string_literal411_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }

            // GraphlrJava.g:1076:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // GraphlrJava.g:1076:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5590);
            conditionalAndExpression410=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression410.getTree());


            // GraphlrJava.g:1077:9: ( '||' conditionalAndExpression )*
            loop114:
            do {
                int alt114=2;
                int LA114_0 = input.LA(1);

                if ( (LA114_0==BARBAR) ) {
                    alt114=1;
                }


                switch (alt114) {
            	case 1 :
            	    // GraphlrJava.g:1077:10: '||' conditionalAndExpression
            	    {
            	    string_literal411=(Token)match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression5601); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal411_tree = 
            	    (Tree)adaptor.create(string_literal411)
            	    ;
            	    adaptor.addChild(root_0, string_literal411_tree);
            	    }


            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5603);
            	    conditionalAndExpression412=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalAndExpression412.getTree());


            	    }
            	    break;

            	default :
            	    break loop114;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 73, conditionalOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"


    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditionalAndExpression"
    // GraphlrJava.g:1081:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final GraphlrJavaParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        GraphlrJavaParser.conditionalAndExpression_return retval = new GraphlrJavaParser.conditionalAndExpression_return();
        retval.start = input.LT(1);

        int conditionalAndExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal414=null;
        ParserRuleReturnScope inclusiveOrExpression413 =null;

        ParserRuleReturnScope inclusiveOrExpression415 =null;


        Tree string_literal414_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }

            // GraphlrJava.g:1082:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // GraphlrJava.g:1082:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5634);
            inclusiveOrExpression413=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression413.getTree());


            // GraphlrJava.g:1083:9: ( '&&' inclusiveOrExpression )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( (LA115_0==AMPAMP) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // GraphlrJava.g:1083:10: '&&' inclusiveOrExpression
            	    {
            	    string_literal414=(Token)match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression5645); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal414_tree = 
            	    (Tree)adaptor.create(string_literal414)
            	    ;
            	    adaptor.addChild(root_0, string_literal414_tree);
            	    }


            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5647);
            	    inclusiveOrExpression415=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, inclusiveOrExpression415.getTree());


            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 74, conditionalAndExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"


    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "inclusiveOrExpression"
    // GraphlrJava.g:1087:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final GraphlrJavaParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        GraphlrJavaParser.inclusiveOrExpression_return retval = new GraphlrJavaParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);

        int inclusiveOrExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal417=null;
        ParserRuleReturnScope exclusiveOrExpression416 =null;

        ParserRuleReturnScope exclusiveOrExpression418 =null;


        Tree char_literal417_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }

            // GraphlrJava.g:1088:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // GraphlrJava.g:1088:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5678);
            exclusiveOrExpression416=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression416.getTree());


            // GraphlrJava.g:1089:9: ( '|' exclusiveOrExpression )*
            loop116:
            do {
                int alt116=2;
                int LA116_0 = input.LA(1);

                if ( (LA116_0==BAR) ) {
                    alt116=1;
                }


                switch (alt116) {
            	case 1 :
            	    // GraphlrJava.g:1089:10: '|' exclusiveOrExpression
            	    {
            	    char_literal417=(Token)match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression5689); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal417_tree = 
            	    (Tree)adaptor.create(char_literal417)
            	    ;
            	    adaptor.addChild(root_0, char_literal417_tree);
            	    }


            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5691);
            	    exclusiveOrExpression418=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exclusiveOrExpression418.getTree());


            	    }
            	    break;

            	default :
            	    break loop116;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 75, inclusiveOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"


    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "exclusiveOrExpression"
    // GraphlrJava.g:1093:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final GraphlrJavaParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        GraphlrJavaParser.exclusiveOrExpression_return retval = new GraphlrJavaParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);

        int exclusiveOrExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal420=null;
        ParserRuleReturnScope andExpression419 =null;

        ParserRuleReturnScope andExpression421 =null;


        Tree char_literal420_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }

            // GraphlrJava.g:1094:5: ( andExpression ( '^' andExpression )* )
            // GraphlrJava.g:1094:9: andExpression ( '^' andExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5722);
            andExpression419=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression419.getTree());


            // GraphlrJava.g:1095:9: ( '^' andExpression )*
            loop117:
            do {
                int alt117=2;
                int LA117_0 = input.LA(1);

                if ( (LA117_0==CARET) ) {
                    alt117=1;
                }


                switch (alt117) {
            	case 1 :
            	    // GraphlrJava.g:1095:10: '^' andExpression
            	    {
            	    char_literal420=(Token)match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression5733); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal420_tree = 
            	    (Tree)adaptor.create(char_literal420)
            	    ;
            	    adaptor.addChild(root_0, char_literal420_tree);
            	    }


            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5735);
            	    andExpression421=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, andExpression421.getTree());


            	    }
            	    break;

            	default :
            	    break loop117;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 76, exclusiveOrExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"


    public static class andExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "andExpression"
    // GraphlrJava.g:1099:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final GraphlrJavaParser.andExpression_return andExpression() throws RecognitionException {
        GraphlrJavaParser.andExpression_return retval = new GraphlrJavaParser.andExpression_return();
        retval.start = input.LT(1);

        int andExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal423=null;
        ParserRuleReturnScope equalityExpression422 =null;

        ParserRuleReturnScope equalityExpression424 =null;


        Tree char_literal423_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }

            // GraphlrJava.g:1100:5: ( equalityExpression ( '&' equalityExpression )* )
            // GraphlrJava.g:1100:9: equalityExpression ( '&' equalityExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_equalityExpression_in_andExpression5766);
            equalityExpression422=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression422.getTree());


            // GraphlrJava.g:1101:9: ( '&' equalityExpression )*
            loop118:
            do {
                int alt118=2;
                int LA118_0 = input.LA(1);

                if ( (LA118_0==AMP) ) {
                    alt118=1;
                }


                switch (alt118) {
            	case 1 :
            	    // GraphlrJava.g:1101:10: '&' equalityExpression
            	    {
            	    char_literal423=(Token)match(input,AMP,FOLLOW_AMP_in_andExpression5777); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal423_tree = 
            	    (Tree)adaptor.create(char_literal423)
            	    ;
            	    adaptor.addChild(root_0, char_literal423_tree);
            	    }


            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5779);
            	    equalityExpression424=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, equalityExpression424.getTree());


            	    }
            	    break;

            	default :
            	    break loop118;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 77, andExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "andExpression"


    public static class equalityExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "equalityExpression"
    // GraphlrJava.g:1105:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final GraphlrJavaParser.equalityExpression_return equalityExpression() throws RecognitionException {
        GraphlrJavaParser.equalityExpression_return retval = new GraphlrJavaParser.equalityExpression_return();
        retval.start = input.LT(1);

        int equalityExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token set426=null;
        ParserRuleReturnScope instanceOfExpression425 =null;

        ParserRuleReturnScope instanceOfExpression427 =null;


        Tree set426_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }

            // GraphlrJava.g:1106:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // GraphlrJava.g:1106:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5810);
            instanceOfExpression425=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression425.getTree());


            // GraphlrJava.g:1107:9: ( ( '==' | '!=' ) instanceOfExpression )*
            loop119:
            do {
                int alt119=2;
                int LA119_0 = input.LA(1);

                if ( (LA119_0==BANGEQ||LA119_0==EQEQ) ) {
                    alt119=1;
                }


                switch (alt119) {
            	case 1 :
            	    // GraphlrJava.g:1108:13: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    set426=(Token)input.LT(1);

            	    if ( input.LA(1)==BANGEQ||input.LA(1)==EQEQ ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (Tree)adaptor.create(set426)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5887);
            	    instanceOfExpression427=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, instanceOfExpression427.getTree());


            	    }
            	    break;

            	default :
            	    break loop119;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 78, equalityExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "equalityExpression"


    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "instanceOfExpression"
    // GraphlrJava.g:1115:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final GraphlrJavaParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        GraphlrJavaParser.instanceOfExpression_return retval = new GraphlrJavaParser.instanceOfExpression_return();
        retval.start = input.LT(1);

        int instanceOfExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal429=null;
        ParserRuleReturnScope relationalExpression428 =null;

        ParserRuleReturnScope type430 =null;


        Tree string_literal429_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }

            // GraphlrJava.g:1116:5: ( relationalExpression ( 'instanceof' type )? )
            // GraphlrJava.g:1116:9: relationalExpression ( 'instanceof' type )?
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5918);
            relationalExpression428=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression428.getTree());


            // GraphlrJava.g:1117:9: ( 'instanceof' type )?
            int alt120=2;
            int LA120_0 = input.LA(1);

            if ( (LA120_0==INSTANCEOF) ) {
                alt120=1;
            }
            switch (alt120) {
                case 1 :
                    // GraphlrJava.g:1117:10: 'instanceof' type
                    {
                    string_literal429=(Token)match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression5929); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal429_tree = 
                    (Tree)adaptor.create(string_literal429)
                    ;
                    adaptor.addChild(root_0, string_literal429_tree);
                    }


                    pushFollow(FOLLOW_type_in_instanceOfExpression5931);
                    type430=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type430.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 79, instanceOfExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"


    public static class relationalExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "relationalExpression"
    // GraphlrJava.g:1121:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final GraphlrJavaParser.relationalExpression_return relationalExpression() throws RecognitionException {
        GraphlrJavaParser.relationalExpression_return retval = new GraphlrJavaParser.relationalExpression_return();
        retval.start = input.LT(1);

        int relationalExpression_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope shiftExpression431 =null;

        ParserRuleReturnScope relationalOp432 =null;

        ParserRuleReturnScope shiftExpression433 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }

            // GraphlrJava.g:1122:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // GraphlrJava.g:1122:9: shiftExpression ( relationalOp shiftExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5962);
            shiftExpression431=shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression431.getTree());


            // GraphlrJava.g:1123:9: ( relationalOp shiftExpression )*
            loop121:
            do {
                int alt121=2;
                int LA121_0 = input.LA(1);

                if ( (LA121_0==LT) ) {
                    int LA121_2 = input.LA(2);

                    if ( (LA121_2==BANG||LA121_2==BOOLEAN||LA121_2==BYTE||(LA121_2 >= CHAR && LA121_2 <= CHARLITERAL)||(LA121_2 >= DOUBLE && LA121_2 <= DOUBLELITERAL)||LA121_2==EQ||LA121_2==FALSE||(LA121_2 >= FLOAT && LA121_2 <= FLOATLITERAL)||LA121_2==IDENTIFIER||LA121_2==INT||LA121_2==INTLITERAL||(LA121_2 >= LONG && LA121_2 <= LPAREN)||(LA121_2 >= NEW && LA121_2 <= NULL)||LA121_2==PLUS||LA121_2==PLUSPLUS||LA121_2==SHORT||(LA121_2 >= STRINGLITERAL && LA121_2 <= SUB)||(LA121_2 >= SUBSUB && LA121_2 <= SUPER)||LA121_2==THIS||LA121_2==TILDE||LA121_2==TRUE||LA121_2==VOID) ) {
                        alt121=1;
                    }


                }
                else if ( (LA121_0==GT) ) {
                    int LA121_3 = input.LA(2);

                    if ( (LA121_3==BANG||LA121_3==BOOLEAN||LA121_3==BYTE||(LA121_3 >= CHAR && LA121_3 <= CHARLITERAL)||(LA121_3 >= DOUBLE && LA121_3 <= DOUBLELITERAL)||LA121_3==EQ||LA121_3==FALSE||(LA121_3 >= FLOAT && LA121_3 <= FLOATLITERAL)||LA121_3==IDENTIFIER||LA121_3==INT||LA121_3==INTLITERAL||(LA121_3 >= LONG && LA121_3 <= LPAREN)||(LA121_3 >= NEW && LA121_3 <= NULL)||LA121_3==PLUS||LA121_3==PLUSPLUS||LA121_3==SHORT||(LA121_3 >= STRINGLITERAL && LA121_3 <= SUB)||(LA121_3 >= SUBSUB && LA121_3 <= SUPER)||LA121_3==THIS||LA121_3==TILDE||LA121_3==TRUE||LA121_3==VOID) ) {
                        alt121=1;
                    }


                }


                switch (alt121) {
            	case 1 :
            	    // GraphlrJava.g:1123:10: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5973);
            	    relationalOp432=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalOp432.getTree());


            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5975);
            	    shiftExpression433=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression433.getTree());


            	    }
            	    break;

            	default :
            	    break loop121;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 80, relationalExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "relationalExpression"


    public static class relationalOp_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "relationalOp"
    // GraphlrJava.g:1127:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final GraphlrJavaParser.relationalOp_return relationalOp() throws RecognitionException {
        GraphlrJavaParser.relationalOp_return retval = new GraphlrJavaParser.relationalOp_return();
        retval.start = input.LT(1);

        int relationalOp_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal434=null;
        Token char_literal435=null;
        Token char_literal436=null;
        Token char_literal437=null;
        Token char_literal438=null;
        Token char_literal439=null;

        Tree char_literal434_tree=null;
        Tree char_literal435_tree=null;
        Tree char_literal436_tree=null;
        Tree char_literal437_tree=null;
        Tree char_literal438_tree=null;
        Tree char_literal439_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }

            // GraphlrJava.g:1128:5: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt122=4;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==LT) ) {
                int LA122_1 = input.LA(2);

                if ( (LA122_1==EQ) ) {
                    alt122=1;
                }
                else if ( (LA122_1==BANG||LA122_1==BOOLEAN||LA122_1==BYTE||(LA122_1 >= CHAR && LA122_1 <= CHARLITERAL)||(LA122_1 >= DOUBLE && LA122_1 <= DOUBLELITERAL)||LA122_1==FALSE||(LA122_1 >= FLOAT && LA122_1 <= FLOATLITERAL)||LA122_1==IDENTIFIER||LA122_1==INT||LA122_1==INTLITERAL||(LA122_1 >= LONG && LA122_1 <= LPAREN)||(LA122_1 >= NEW && LA122_1 <= NULL)||LA122_1==PLUS||LA122_1==PLUSPLUS||LA122_1==SHORT||(LA122_1 >= STRINGLITERAL && LA122_1 <= SUB)||(LA122_1 >= SUBSUB && LA122_1 <= SUPER)||LA122_1==THIS||LA122_1==TILDE||LA122_1==TRUE||LA122_1==VOID) ) {
                    alt122=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 122, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else if ( (LA122_0==GT) ) {
                int LA122_2 = input.LA(2);

                if ( (LA122_2==EQ) ) {
                    alt122=2;
                }
                else if ( (LA122_2==BANG||LA122_2==BOOLEAN||LA122_2==BYTE||(LA122_2 >= CHAR && LA122_2 <= CHARLITERAL)||(LA122_2 >= DOUBLE && LA122_2 <= DOUBLELITERAL)||LA122_2==FALSE||(LA122_2 >= FLOAT && LA122_2 <= FLOATLITERAL)||LA122_2==IDENTIFIER||LA122_2==INT||LA122_2==INTLITERAL||(LA122_2 >= LONG && LA122_2 <= LPAREN)||(LA122_2 >= NEW && LA122_2 <= NULL)||LA122_2==PLUS||LA122_2==PLUSPLUS||LA122_2==SHORT||(LA122_2 >= STRINGLITERAL && LA122_2 <= SUB)||(LA122_2 >= SUBSUB && LA122_2 <= SUPER)||LA122_2==THIS||LA122_2==TILDE||LA122_2==TRUE||LA122_2==VOID) ) {
                    alt122=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 122, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 122, 0, input);

            	throw nvae;
            }
            switch (alt122) {
                case 1 :
                    // GraphlrJava.g:1128:10: '<' '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal434=(Token)match(input,LT,FOLLOW_LT_in_relationalOp6007); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal434_tree = 
                    (Tree)adaptor.create(char_literal434)
                    ;
                    adaptor.addChild(root_0, char_literal434_tree);
                    }


                    char_literal435=(Token)match(input,EQ,FOLLOW_EQ_in_relationalOp6009); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal435_tree = 
                    (Tree)adaptor.create(char_literal435)
                    ;
                    adaptor.addChild(root_0, char_literal435_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1129:10: '>' '='
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal436=(Token)match(input,GT,FOLLOW_GT_in_relationalOp6020); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal436_tree = 
                    (Tree)adaptor.create(char_literal436)
                    ;
                    adaptor.addChild(root_0, char_literal436_tree);
                    }


                    char_literal437=(Token)match(input,EQ,FOLLOW_EQ_in_relationalOp6022); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal437_tree = 
                    (Tree)adaptor.create(char_literal437)
                    ;
                    adaptor.addChild(root_0, char_literal437_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1130:9: '<'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal438=(Token)match(input,LT,FOLLOW_LT_in_relationalOp6032); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal438_tree = 
                    (Tree)adaptor.create(char_literal438)
                    ;
                    adaptor.addChild(root_0, char_literal438_tree);
                    }


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1131:9: '>'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal439=(Token)match(input,GT,FOLLOW_GT_in_relationalOp6042); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal439_tree = 
                    (Tree)adaptor.create(char_literal439)
                    ;
                    adaptor.addChild(root_0, char_literal439_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 81, relationalOp_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "relationalOp"


    public static class shiftExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shiftExpression"
    // GraphlrJava.g:1134:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final GraphlrJavaParser.shiftExpression_return shiftExpression() throws RecognitionException {
        GraphlrJavaParser.shiftExpression_return retval = new GraphlrJavaParser.shiftExpression_return();
        retval.start = input.LT(1);

        int shiftExpression_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope additiveExpression440 =null;

        ParserRuleReturnScope shiftOp441 =null;

        ParserRuleReturnScope additiveExpression442 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }

            // GraphlrJava.g:1135:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // GraphlrJava.g:1135:9: additiveExpression ( shiftOp additiveExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_additiveExpression_in_shiftExpression6062);
            additiveExpression440=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression440.getTree());


            // GraphlrJava.g:1136:9: ( shiftOp additiveExpression )*
            loop123:
            do {
                int alt123=2;
                int LA123_0 = input.LA(1);

                if ( (LA123_0==LT) ) {
                    int LA123_1 = input.LA(2);

                    if ( (LA123_1==LT) ) {
                        int LA123_4 = input.LA(3);

                        if ( (LA123_4==BANG||LA123_4==BOOLEAN||LA123_4==BYTE||(LA123_4 >= CHAR && LA123_4 <= CHARLITERAL)||(LA123_4 >= DOUBLE && LA123_4 <= DOUBLELITERAL)||LA123_4==FALSE||(LA123_4 >= FLOAT && LA123_4 <= FLOATLITERAL)||LA123_4==IDENTIFIER||LA123_4==INT||LA123_4==INTLITERAL||(LA123_4 >= LONG && LA123_4 <= LPAREN)||(LA123_4 >= NEW && LA123_4 <= NULL)||LA123_4==PLUS||LA123_4==PLUSPLUS||LA123_4==SHORT||(LA123_4 >= STRINGLITERAL && LA123_4 <= SUB)||(LA123_4 >= SUBSUB && LA123_4 <= SUPER)||LA123_4==THIS||LA123_4==TILDE||LA123_4==TRUE||LA123_4==VOID) ) {
                            alt123=1;
                        }


                    }


                }
                else if ( (LA123_0==GT) ) {
                    int LA123_2 = input.LA(2);

                    if ( (LA123_2==GT) ) {
                        int LA123_5 = input.LA(3);

                        if ( (LA123_5==GT) ) {
                            int LA123_7 = input.LA(4);

                            if ( (LA123_7==BANG||LA123_7==BOOLEAN||LA123_7==BYTE||(LA123_7 >= CHAR && LA123_7 <= CHARLITERAL)||(LA123_7 >= DOUBLE && LA123_7 <= DOUBLELITERAL)||LA123_7==FALSE||(LA123_7 >= FLOAT && LA123_7 <= FLOATLITERAL)||LA123_7==IDENTIFIER||LA123_7==INT||LA123_7==INTLITERAL||(LA123_7 >= LONG && LA123_7 <= LPAREN)||(LA123_7 >= NEW && LA123_7 <= NULL)||LA123_7==PLUS||LA123_7==PLUSPLUS||LA123_7==SHORT||(LA123_7 >= STRINGLITERAL && LA123_7 <= SUB)||(LA123_7 >= SUBSUB && LA123_7 <= SUPER)||LA123_7==THIS||LA123_7==TILDE||LA123_7==TRUE||LA123_7==VOID) ) {
                                alt123=1;
                            }


                        }
                        else if ( (LA123_5==BANG||LA123_5==BOOLEAN||LA123_5==BYTE||(LA123_5 >= CHAR && LA123_5 <= CHARLITERAL)||(LA123_5 >= DOUBLE && LA123_5 <= DOUBLELITERAL)||LA123_5==FALSE||(LA123_5 >= FLOAT && LA123_5 <= FLOATLITERAL)||LA123_5==IDENTIFIER||LA123_5==INT||LA123_5==INTLITERAL||(LA123_5 >= LONG && LA123_5 <= LPAREN)||(LA123_5 >= NEW && LA123_5 <= NULL)||LA123_5==PLUS||LA123_5==PLUSPLUS||LA123_5==SHORT||(LA123_5 >= STRINGLITERAL && LA123_5 <= SUB)||(LA123_5 >= SUBSUB && LA123_5 <= SUPER)||LA123_5==THIS||LA123_5==TILDE||LA123_5==TRUE||LA123_5==VOID) ) {
                            alt123=1;
                        }


                    }


                }


                switch (alt123) {
            	case 1 :
            	    // GraphlrJava.g:1136:10: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression6073);
            	    shiftOp441=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftOp441.getTree());


            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression6075);
            	    additiveExpression442=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression442.getTree());


            	    }
            	    break;

            	default :
            	    break loop123;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 82, shiftExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "shiftExpression"


    public static class shiftOp_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "shiftOp"
    // GraphlrJava.g:1141:1: shiftOp : ( '<' '<' | '>' '>' '>' | '>' '>' );
    public final GraphlrJavaParser.shiftOp_return shiftOp() throws RecognitionException {
        GraphlrJavaParser.shiftOp_return retval = new GraphlrJavaParser.shiftOp_return();
        retval.start = input.LT(1);

        int shiftOp_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal443=null;
        Token char_literal444=null;
        Token char_literal445=null;
        Token char_literal446=null;
        Token char_literal447=null;
        Token char_literal448=null;
        Token char_literal449=null;

        Tree char_literal443_tree=null;
        Tree char_literal444_tree=null;
        Tree char_literal445_tree=null;
        Tree char_literal446_tree=null;
        Tree char_literal447_tree=null;
        Tree char_literal448_tree=null;
        Tree char_literal449_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }

            // GraphlrJava.g:1142:5: ( '<' '<' | '>' '>' '>' | '>' '>' )
            int alt124=3;
            int LA124_0 = input.LA(1);

            if ( (LA124_0==LT) ) {
                alt124=1;
            }
            else if ( (LA124_0==GT) ) {
                int LA124_2 = input.LA(2);

                if ( (LA124_2==GT) ) {
                    int LA124_3 = input.LA(3);

                    if ( (LA124_3==GT) ) {
                        alt124=2;
                    }
                    else if ( (LA124_3==BANG||LA124_3==BOOLEAN||LA124_3==BYTE||(LA124_3 >= CHAR && LA124_3 <= CHARLITERAL)||(LA124_3 >= DOUBLE && LA124_3 <= DOUBLELITERAL)||LA124_3==FALSE||(LA124_3 >= FLOAT && LA124_3 <= FLOATLITERAL)||LA124_3==IDENTIFIER||LA124_3==INT||LA124_3==INTLITERAL||(LA124_3 >= LONG && LA124_3 <= LPAREN)||(LA124_3 >= NEW && LA124_3 <= NULL)||LA124_3==PLUS||LA124_3==PLUSPLUS||LA124_3==SHORT||(LA124_3 >= STRINGLITERAL && LA124_3 <= SUB)||(LA124_3 >= SUBSUB && LA124_3 <= SUPER)||LA124_3==THIS||LA124_3==TILDE||LA124_3==TRUE||LA124_3==VOID) ) {
                        alt124=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                    	int nvaeMark = input.mark();
                    	try {
                    		for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++)
                    			input.consume();
                    		NoViableAltException nvae =
                    			new NoViableAltException("", 124, 3, input);

                    		throw nvae;
                    	} finally {
                    		input.rewind(nvaeMark);
                    	}
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 124, 2, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 124, 0, input);

            	throw nvae;
            }
            switch (alt124) {
                case 1 :
                    // GraphlrJava.g:1142:10: '<' '<'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal443=(Token)match(input,LT,FOLLOW_LT_in_shiftOp6108); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal443_tree = 
                    (Tree)adaptor.create(char_literal443)
                    ;
                    adaptor.addChild(root_0, char_literal443_tree);
                    }


                    char_literal444=(Token)match(input,LT,FOLLOW_LT_in_shiftOp6110); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal444_tree = 
                    (Tree)adaptor.create(char_literal444)
                    ;
                    adaptor.addChild(root_0, char_literal444_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1143:10: '>' '>' '>'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal445=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6121); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal445_tree = 
                    (Tree)adaptor.create(char_literal445)
                    ;
                    adaptor.addChild(root_0, char_literal445_tree);
                    }


                    char_literal446=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6123); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal446_tree = 
                    (Tree)adaptor.create(char_literal446)
                    ;
                    adaptor.addChild(root_0, char_literal446_tree);
                    }


                    char_literal447=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6125); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal447_tree = 
                    (Tree)adaptor.create(char_literal447)
                    ;
                    adaptor.addChild(root_0, char_literal447_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1144:10: '>' '>'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal448=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6136); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal448_tree = 
                    (Tree)adaptor.create(char_literal448)
                    ;
                    adaptor.addChild(root_0, char_literal448_tree);
                    }


                    char_literal449=(Token)match(input,GT,FOLLOW_GT_in_shiftOp6138); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal449_tree = 
                    (Tree)adaptor.create(char_literal449)
                    ;
                    adaptor.addChild(root_0, char_literal449_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 83, shiftOp_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "shiftOp"


    public static class additiveExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "additiveExpression"
    // GraphlrJava.g:1148:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final GraphlrJavaParser.additiveExpression_return additiveExpression() throws RecognitionException {
        GraphlrJavaParser.additiveExpression_return retval = new GraphlrJavaParser.additiveExpression_return();
        retval.start = input.LT(1);

        int additiveExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token set451=null;
        ParserRuleReturnScope multiplicativeExpression450 =null;

        ParserRuleReturnScope multiplicativeExpression452 =null;


        Tree set451_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }

            // GraphlrJava.g:1149:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // GraphlrJava.g:1149:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6159);
            multiplicativeExpression450=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression450.getTree());


            // GraphlrJava.g:1150:9: ( ( '+' | '-' ) multiplicativeExpression )*
            loop125:
            do {
                int alt125=2;
                int LA125_0 = input.LA(1);

                if ( (LA125_0==PLUS||LA125_0==SUB) ) {
                    alt125=1;
                }


                switch (alt125) {
            	case 1 :
            	    // GraphlrJava.g:1151:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    set451=(Token)input.LT(1);

            	    if ( input.LA(1)==PLUS||input.LA(1)==SUB ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (Tree)adaptor.create(set451)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6236);
            	    multiplicativeExpression452=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression452.getTree());


            	    }
            	    break;

            	default :
            	    break loop125;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 84, additiveExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "additiveExpression"


    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "multiplicativeExpression"
    // GraphlrJava.g:1158:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final GraphlrJavaParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        GraphlrJavaParser.multiplicativeExpression_return retval = new GraphlrJavaParser.multiplicativeExpression_return();
        retval.start = input.LT(1);

        int multiplicativeExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token set454=null;
        ParserRuleReturnScope unaryExpression453 =null;

        ParserRuleReturnScope unaryExpression455 =null;


        Tree set454_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }

            // GraphlrJava.g:1159:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // GraphlrJava.g:1160:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6274);
            unaryExpression453=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression453.getTree());


            // GraphlrJava.g:1161:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( (LA126_0==PERCENT||LA126_0==SLASH||LA126_0==STAR) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // GraphlrJava.g:1162:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    set454=(Token)input.LT(1);

            	    if ( input.LA(1)==PERCENT||input.LA(1)==SLASH||input.LA(1)==STAR ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
            	        (Tree)adaptor.create(set454)
            	        );
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6369);
            	    unaryExpression455=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression455.getTree());


            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 85, multiplicativeExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"


    public static class unaryExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExpression"
    // GraphlrJava.g:1174:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final GraphlrJavaParser.unaryExpression_return unaryExpression() throws RecognitionException {
        GraphlrJavaParser.unaryExpression_return retval = new GraphlrJavaParser.unaryExpression_return();
        retval.start = input.LT(1);

        int unaryExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal456=null;
        Token char_literal458=null;
        Token string_literal460=null;
        Token string_literal462=null;
        ParserRuleReturnScope unaryExpression457 =null;

        ParserRuleReturnScope unaryExpression459 =null;

        ParserRuleReturnScope unaryExpression461 =null;

        ParserRuleReturnScope unaryExpression463 =null;

        ParserRuleReturnScope unaryExpressionNotPlusMinus464 =null;


        Tree char_literal456_tree=null;
        Tree char_literal458_tree=null;
        Tree string_literal460_tree=null;
        Tree string_literal462_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }

            // GraphlrJava.g:1175:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt127=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt127=1;
                }
                break;
            case SUB:
                {
                alt127=2;
                }
                break;
            case PLUSPLUS:
                {
                alt127=3;
                }
                break;
            case SUBSUB:
                {
                alt127=4;
                }
                break;
            case BANG:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case LPAREN:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case SUPER:
            case THIS:
            case TILDE:
            case TRUE:
            case VOID:
                {
                alt127=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 127, 0, input);

            	throw nvae;
            }

            switch (alt127) {
                case 1 :
                    // GraphlrJava.g:1175:9: '+' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal456=(Token)match(input,PLUS,FOLLOW_PLUS_in_unaryExpression6402); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal456_tree = 
                    (Tree)adaptor.create(char_literal456)
                    ;
                    adaptor.addChild(root_0, char_literal456_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6405);
                    unaryExpression457=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression457.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1176:9: '-' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal458=(Token)match(input,SUB,FOLLOW_SUB_in_unaryExpression6415); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal458_tree = 
                    (Tree)adaptor.create(char_literal458)
                    ;
                    adaptor.addChild(root_0, char_literal458_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6417);
                    unaryExpression459=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression459.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1177:9: '++' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal460=(Token)match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression6427); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal460_tree = 
                    (Tree)adaptor.create(string_literal460)
                    ;
                    adaptor.addChild(root_0, string_literal460_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6429);
                    unaryExpression461=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression461.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1178:9: '--' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal462=(Token)match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression6439); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal462_tree = 
                    (Tree)adaptor.create(string_literal462)
                    ;
                    adaptor.addChild(root_0, string_literal462_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6441);
                    unaryExpression463=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression463.getTree());


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:1179:9: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6451);
                    unaryExpressionNotPlusMinus464=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus464.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 86, unaryExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "unaryExpression"


    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // GraphlrJava.g:1182:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? );
    public final GraphlrJavaParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        GraphlrJavaParser.unaryExpressionNotPlusMinus_return retval = new GraphlrJavaParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        int unaryExpressionNotPlusMinus_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal465=null;
        Token char_literal467=null;
        Token set472=null;
        ParserRuleReturnScope unaryExpression466 =null;

        ParserRuleReturnScope unaryExpression468 =null;

        ParserRuleReturnScope castExpression469 =null;

        ParserRuleReturnScope primary470 =null;

        ParserRuleReturnScope selector471 =null;


        Tree char_literal465_tree=null;
        Tree char_literal467_tree=null;
        Tree set472_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }

            // GraphlrJava.g:1183:5: ( '~' unaryExpression | '!' unaryExpression | castExpression | primary ( selector )* ( '++' | '--' )? )
            int alt130=4;
            switch ( input.LA(1) ) {
            case TILDE:
                {
                alt130=1;
                }
                break;
            case BANG:
                {
                alt130=2;
                }
                break;
            case LPAREN:
                {
                int LA130_3 = input.LA(2);

                if ( (synpred202_GraphlrJava()) ) {
                    alt130=3;
                }
                else if ( (true) ) {
                    alt130=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 130, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CHARLITERAL:
            case DOUBLE:
            case DOUBLELITERAL:
            case FALSE:
            case FLOAT:
            case FLOATLITERAL:
            case IDENTIFIER:
            case INT:
            case INTLITERAL:
            case LONG:
            case LONGLITERAL:
            case NEW:
            case NULL:
            case SHORT:
            case STRINGLITERAL:
            case SUPER:
            case THIS:
            case TRUE:
            case VOID:
                {
                alt130=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 130, 0, input);

            	throw nvae;
            }

            switch (alt130) {
                case 1 :
                    // GraphlrJava.g:1183:9: '~' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal465=(Token)match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6471); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal465_tree = 
                    (Tree)adaptor.create(char_literal465)
                    ;
                    adaptor.addChild(root_0, char_literal465_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6473);
                    unaryExpression466=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression466.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1184:9: '!' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal467=(Token)match(input,BANG,FOLLOW_BANG_in_unaryExpressionNotPlusMinus6483); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal467_tree = 
                    (Tree)adaptor.create(char_literal467)
                    ;
                    adaptor.addChild(root_0, char_literal467_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6485);
                    unaryExpression468=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression468.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1185:9: castExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6495);
                    castExpression469=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression469.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1186:9: primary ( selector )* ( '++' | '--' )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6505);
                    primary470=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primary470.getTree());


                    // GraphlrJava.g:1187:9: ( selector )*
                    loop128:
                    do {
                        int alt128=2;
                        int LA128_0 = input.LA(1);

                        if ( (LA128_0==DOT||LA128_0==LBRACKET) ) {
                            alt128=1;
                        }


                        switch (alt128) {
                    	case 1 :
                    	    // GraphlrJava.g:1187:10: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6516);
                    	    selector471=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector471.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop128;
                        }
                    } while (true);


                    // GraphlrJava.g:1189:9: ( '++' | '--' )?
                    int alt129=2;
                    int LA129_0 = input.LA(1);

                    if ( (LA129_0==PLUSPLUS||LA129_0==SUBSUB) ) {
                        alt129=1;
                    }
                    switch (alt129) {
                        case 1 :
                            // GraphlrJava.g:
                            {
                            set472=(Token)input.LT(1);

                            if ( input.LA(1)==PLUSPLUS||input.LA(1)==SUBSUB ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                                (Tree)adaptor.create(set472)
                                );
                                state.errorRecovery=false;
                                state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 87, unaryExpressionNotPlusMinus_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"


    public static class castExpression_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "castExpression"
    // GraphlrJava.g:1194:1: castExpression : ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus );
    public final GraphlrJavaParser.castExpression_return castExpression() throws RecognitionException {
        GraphlrJavaParser.castExpression_return retval = new GraphlrJavaParser.castExpression_return();
        retval.start = input.LT(1);

        int castExpression_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal473=null;
        Token char_literal475=null;
        Token char_literal477=null;
        Token char_literal479=null;
        ParserRuleReturnScope primitiveType474 =null;

        ParserRuleReturnScope unaryExpression476 =null;

        ParserRuleReturnScope type478 =null;

        ParserRuleReturnScope unaryExpressionNotPlusMinus480 =null;


        Tree char_literal473_tree=null;
        Tree char_literal475_tree=null;
        Tree char_literal477_tree=null;
        Tree char_literal479_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }

            // GraphlrJava.g:1195:5: ( '(' primitiveType ')' unaryExpression | '(' type ')' unaryExpressionNotPlusMinus )
            int alt131=2;
            int LA131_0 = input.LA(1);

            if ( (LA131_0==LPAREN) ) {
                int LA131_1 = input.LA(2);

                if ( (synpred206_GraphlrJava()) ) {
                    alt131=1;
                }
                else if ( (true) ) {
                    alt131=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 131, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 131, 0, input);

            	throw nvae;
            }
            switch (alt131) {
                case 1 :
                    // GraphlrJava.g:1195:9: '(' primitiveType ')' unaryExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal473=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6586); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal473_tree = 
                    (Tree)adaptor.create(char_literal473)
                    ;
                    adaptor.addChild(root_0, char_literal473_tree);
                    }


                    pushFollow(FOLLOW_primitiveType_in_castExpression6588);
                    primitiveType474=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType474.getTree());


                    char_literal475=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6590); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal475_tree = 
                    (Tree)adaptor.create(char_literal475)
                    ;
                    adaptor.addChild(root_0, char_literal475_tree);
                    }


                    pushFollow(FOLLOW_unaryExpression_in_castExpression6592);
                    unaryExpression476=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpression476.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1196:9: '(' type ')' unaryExpressionNotPlusMinus
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal477=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_castExpression6602); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal477_tree = 
                    (Tree)adaptor.create(char_literal477)
                    ;
                    adaptor.addChild(root_0, char_literal477_tree);
                    }


                    pushFollow(FOLLOW_type_in_castExpression6604);
                    type478=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type478.getTree());


                    char_literal479=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_castExpression6606); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal479_tree = 
                    (Tree)adaptor.create(char_literal479)
                    ;
                    adaptor.addChild(root_0, char_literal479_tree);
                    }


                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6608);
                    unaryExpressionNotPlusMinus480=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus480.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 88, castExpression_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "castExpression"


    public static class primary_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "primary"
    // GraphlrJava.g:1202:1: primary : ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final GraphlrJavaParser.primary_return primary() throws RecognitionException {
        GraphlrJavaParser.primary_return retval = new GraphlrJavaParser.primary_return();
        retval.start = input.LT(1);

        int primary_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal482=null;
        Token char_literal483=null;
        Token IDENTIFIER484=null;
        Token IDENTIFIER486=null;
        Token char_literal487=null;
        Token IDENTIFIER488=null;
        Token string_literal490=null;
        Token char_literal495=null;
        Token char_literal496=null;
        Token char_literal497=null;
        Token string_literal498=null;
        Token string_literal499=null;
        Token char_literal500=null;
        Token string_literal501=null;
        ParserRuleReturnScope parExpression481 =null;

        ParserRuleReturnScope identifierSuffix485 =null;

        ParserRuleReturnScope identifierSuffix489 =null;

        ParserRuleReturnScope superSuffix491 =null;

        ParserRuleReturnScope literal492 =null;

        ParserRuleReturnScope creator493 =null;

        ParserRuleReturnScope primitiveType494 =null;


        Tree string_literal482_tree=null;
        Tree char_literal483_tree=null;
        Tree IDENTIFIER484_tree=null;
        Tree IDENTIFIER486_tree=null;
        Tree char_literal487_tree=null;
        Tree IDENTIFIER488_tree=null;
        Tree string_literal490_tree=null;
        Tree char_literal495_tree=null;
        Tree char_literal496_tree=null;
        Tree char_literal497_tree=null;
        Tree string_literal498_tree=null;
        Tree string_literal499_tree=null;
        Tree char_literal500_tree=null;
        Tree string_literal501_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }

            // GraphlrJava.g:1203:5: ( parExpression | 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt137=8;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt137=1;
                }
                break;
            case THIS:
                {
                alt137=2;
                }
                break;
            case IDENTIFIER:
                {
                alt137=3;
                }
                break;
            case SUPER:
                {
                alt137=4;
                }
                break;
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case NULL:
            case STRINGLITERAL:
            case TRUE:
                {
                alt137=5;
                }
                break;
            case NEW:
                {
                alt137=6;
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                alt137=7;
                }
                break;
            case VOID:
                {
                alt137=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 137, 0, input);

            	throw nvae;
            }

            switch (alt137) {
                case 1 :
                    // GraphlrJava.g:1203:9: parExpression
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_parExpression_in_primary6630);
                    parExpression481=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression481.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1204:9: 'this' ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal482=(Token)match(input,THIS,FOLLOW_THIS_in_primary6652); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal482_tree = 
                    (Tree)adaptor.create(string_literal482)
                    ;
                    adaptor.addChild(root_0, string_literal482_tree);
                    }


                    // GraphlrJava.g:1205:9: ( '.' IDENTIFIER )*
                    loop132:
                    do {
                        int alt132=2;
                        int LA132_0 = input.LA(1);

                        if ( (LA132_0==DOT) ) {
                            int LA132_2 = input.LA(2);

                            if ( (LA132_2==IDENTIFIER) ) {
                                int LA132_3 = input.LA(3);

                                if ( (synpred208_GraphlrJava()) ) {
                                    alt132=1;
                                }


                            }


                        }


                        switch (alt132) {
                    	case 1 :
                    	    // GraphlrJava.g:1205:10: '.' IDENTIFIER
                    	    {
                    	    char_literal483=(Token)match(input,DOT,FOLLOW_DOT_in_primary6663); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal483_tree = 
                    	    (Tree)adaptor.create(char_literal483)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal483_tree);
                    	    }


                    	    IDENTIFIER484=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6665); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER484_tree = 
                    	    (Tree)adaptor.create(IDENTIFIER484)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER484_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop132;
                        }
                    } while (true);


                    // GraphlrJava.g:1207:9: ( identifierSuffix )?
                    int alt133=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA133_1 = input.LA(2);

                            if ( (synpred209_GraphlrJava()) ) {
                                alt133=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt133=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA133_3 = input.LA(2);

                            if ( (synpred209_GraphlrJava()) ) {
                                alt133=1;
                            }
                            }
                            break;
                    }

                    switch (alt133) {
                        case 1 :
                            // GraphlrJava.g:1207:10: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6687);
                            identifierSuffix485=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix485.getTree());


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1209:9: IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    IDENTIFIER486=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6708); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER486_tree = 
                    (Tree)adaptor.create(IDENTIFIER486)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER486_tree);
                    }


                    // GraphlrJava.g:1210:9: ( '.' IDENTIFIER )*
                    loop134:
                    do {
                        int alt134=2;
                        int LA134_0 = input.LA(1);

                        if ( (LA134_0==DOT) ) {
                            int LA134_2 = input.LA(2);

                            if ( (LA134_2==IDENTIFIER) ) {
                                int LA134_3 = input.LA(3);

                                if ( (synpred211_GraphlrJava()) ) {
                                    alt134=1;
                                }


                            }


                        }


                        switch (alt134) {
                    	case 1 :
                    	    // GraphlrJava.g:1210:10: '.' IDENTIFIER
                    	    {
                    	    char_literal487=(Token)match(input,DOT,FOLLOW_DOT_in_primary6719); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal487_tree = 
                    	    (Tree)adaptor.create(char_literal487)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal487_tree);
                    	    }


                    	    IDENTIFIER488=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary6721); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    IDENTIFIER488_tree = 
                    	    (Tree)adaptor.create(IDENTIFIER488)
                    	    ;
                    	    adaptor.addChild(root_0, IDENTIFIER488_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop134;
                        }
                    } while (true);


                    // GraphlrJava.g:1212:9: ( identifierSuffix )?
                    int alt135=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA135_1 = input.LA(2);

                            if ( (synpred212_GraphlrJava()) ) {
                                alt135=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt135=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA135_3 = input.LA(2);

                            if ( (synpred212_GraphlrJava()) ) {
                                alt135=1;
                            }
                            }
                            break;
                    }

                    switch (alt135) {
                        case 1 :
                            // GraphlrJava.g:1212:10: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6743);
                            identifierSuffix489=identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, identifierSuffix489.getTree());


                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1214:9: 'super' superSuffix
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal490=(Token)match(input,SUPER,FOLLOW_SUPER_in_primary6764); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal490_tree = 
                    (Tree)adaptor.create(string_literal490)
                    ;
                    adaptor.addChild(root_0, string_literal490_tree);
                    }


                    pushFollow(FOLLOW_superSuffix_in_primary6774);
                    superSuffix491=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix491.getTree());


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:1216:9: literal
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_literal_in_primary6784);
                    literal492=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal492.getTree());


                    }
                    break;
                case 6 :
                    // GraphlrJava.g:1217:9: creator
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_creator_in_primary6794);
                    creator493=creator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, creator493.getTree());


                    }
                    break;
                case 7 :
                    // GraphlrJava.g:1218:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_primary6804);
                    primitiveType494=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType494.getTree());


                    // GraphlrJava.g:1219:9: ( '[' ']' )*
                    loop136:
                    do {
                        int alt136=2;
                        int LA136_0 = input.LA(1);

                        if ( (LA136_0==LBRACKET) ) {
                            alt136=1;
                        }


                        switch (alt136) {
                    	case 1 :
                    	    // GraphlrJava.g:1219:10: '[' ']'
                    	    {
                    	    char_literal495=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_primary6815); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal495_tree = 
                    	    (Tree)adaptor.create(char_literal495)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal495_tree);
                    	    }


                    	    char_literal496=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_primary6817); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal496_tree = 
                    	    (Tree)adaptor.create(char_literal496)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal496_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop136;
                        }
                    } while (true);


                    char_literal497=(Token)match(input,DOT,FOLLOW_DOT_in_primary6838); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal497_tree = 
                    (Tree)adaptor.create(char_literal497)
                    ;
                    adaptor.addChild(root_0, char_literal497_tree);
                    }


                    string_literal498=(Token)match(input,CLASS,FOLLOW_CLASS_in_primary6840); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal498_tree = 
                    (Tree)adaptor.create(string_literal498)
                    ;
                    adaptor.addChild(root_0, string_literal498_tree);
                    }


                    }
                    break;
                case 8 :
                    // GraphlrJava.g:1222:9: 'void' '.' 'class'
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal499=(Token)match(input,VOID,FOLLOW_VOID_in_primary6850); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal499_tree = 
                    (Tree)adaptor.create(string_literal499)
                    ;
                    adaptor.addChild(root_0, string_literal499_tree);
                    }


                    char_literal500=(Token)match(input,DOT,FOLLOW_DOT_in_primary6852); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal500_tree = 
                    (Tree)adaptor.create(char_literal500)
                    ;
                    adaptor.addChild(root_0, char_literal500_tree);
                    }


                    string_literal501=(Token)match(input,CLASS,FOLLOW_CLASS_in_primary6854); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal501_tree = 
                    (Tree)adaptor.create(string_literal501)
                    ;
                    adaptor.addChild(root_0, string_literal501_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 89, primary_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "primary"


    public static class superSuffix_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "superSuffix"
    // GraphlrJava.g:1226:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final GraphlrJavaParser.superSuffix_return superSuffix() throws RecognitionException {
        GraphlrJavaParser.superSuffix_return retval = new GraphlrJavaParser.superSuffix_return();
        retval.start = input.LT(1);

        int superSuffix_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal503=null;
        Token IDENTIFIER505=null;
        ParserRuleReturnScope arguments502 =null;

        ParserRuleReturnScope typeArguments504 =null;

        ParserRuleReturnScope arguments506 =null;


        Tree char_literal503_tree=null;
        Tree IDENTIFIER505_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }

            // GraphlrJava.g:1227:5: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt140=2;
            int LA140_0 = input.LA(1);

            if ( (LA140_0==LPAREN) ) {
                alt140=1;
            }
            else if ( (LA140_0==DOT) ) {
                alt140=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 140, 0, input);

            	throw nvae;
            }
            switch (alt140) {
                case 1 :
                    // GraphlrJava.g:1227:9: arguments
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_arguments_in_superSuffix6880);
                    arguments502=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments502.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1228:9: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal503=(Token)match(input,DOT,FOLLOW_DOT_in_superSuffix6890); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal503_tree = 
                    (Tree)adaptor.create(char_literal503)
                    ;
                    adaptor.addChild(root_0, char_literal503_tree);
                    }


                    // GraphlrJava.g:1228:13: ( typeArguments )?
                    int alt138=2;
                    int LA138_0 = input.LA(1);

                    if ( (LA138_0==LT) ) {
                        alt138=1;
                    }
                    switch (alt138) {
                        case 1 :
                            // GraphlrJava.g:1228:14: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_superSuffix6893);
                            typeArguments504=typeArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments504.getTree());


                            }
                            break;

                    }


                    IDENTIFIER505=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix6914); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER505_tree = 
                    (Tree)adaptor.create(IDENTIFIER505)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER505_tree);
                    }


                    // GraphlrJava.g:1231:9: ( arguments )?
                    int alt139=2;
                    int LA139_0 = input.LA(1);

                    if ( (LA139_0==LPAREN) ) {
                        alt139=1;
                    }
                    switch (alt139) {
                        case 1 :
                            // GraphlrJava.g:1231:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix6925);
                            arguments506=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments506.getTree());


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 90, superSuffix_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "superSuffix"


    public static class identifierSuffix_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "identifierSuffix"
    // GraphlrJava.g:1236:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator );
    public final GraphlrJavaParser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        GraphlrJavaParser.identifierSuffix_return retval = new GraphlrJavaParser.identifierSuffix_return();
        retval.start = input.LT(1);

        int identifierSuffix_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal507=null;
        Token char_literal508=null;
        Token char_literal509=null;
        Token string_literal510=null;
        Token char_literal511=null;
        Token char_literal513=null;
        Token char_literal515=null;
        Token string_literal516=null;
        Token char_literal517=null;
        Token IDENTIFIER519=null;
        Token char_literal521=null;
        Token string_literal522=null;
        Token char_literal523=null;
        Token string_literal524=null;
        ParserRuleReturnScope expression512 =null;

        ParserRuleReturnScope arguments514 =null;

        ParserRuleReturnScope nonWildcardTypeArguments518 =null;

        ParserRuleReturnScope arguments520 =null;

        ParserRuleReturnScope arguments525 =null;

        ParserRuleReturnScope innerCreator526 =null;


        Tree char_literal507_tree=null;
        Tree char_literal508_tree=null;
        Tree char_literal509_tree=null;
        Tree string_literal510_tree=null;
        Tree char_literal511_tree=null;
        Tree char_literal513_tree=null;
        Tree char_literal515_tree=null;
        Tree string_literal516_tree=null;
        Tree char_literal517_tree=null;
        Tree IDENTIFIER519_tree=null;
        Tree char_literal521_tree=null;
        Tree string_literal522_tree=null;
        Tree char_literal523_tree=null;
        Tree string_literal524_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }

            // GraphlrJava.g:1237:5: ( ( '[' ']' )+ '.' 'class' | ( '[' expression ']' )+ | arguments | '.' 'class' | '.' nonWildcardTypeArguments IDENTIFIER arguments | '.' 'this' | '.' 'super' arguments | innerCreator )
            int alt143=8;
            switch ( input.LA(1) ) {
            case LBRACKET:
                {
                int LA143_1 = input.LA(2);

                if ( (LA143_1==RBRACKET) ) {
                    alt143=1;
                }
                else if ( (LA143_1==BANG||LA143_1==BOOLEAN||LA143_1==BYTE||(LA143_1 >= CHAR && LA143_1 <= CHARLITERAL)||(LA143_1 >= DOUBLE && LA143_1 <= DOUBLELITERAL)||LA143_1==FALSE||(LA143_1 >= FLOAT && LA143_1 <= FLOATLITERAL)||LA143_1==IDENTIFIER||LA143_1==INT||LA143_1==INTLITERAL||(LA143_1 >= LONG && LA143_1 <= LPAREN)||(LA143_1 >= NEW && LA143_1 <= NULL)||LA143_1==PLUS||LA143_1==PLUSPLUS||LA143_1==SHORT||(LA143_1 >= STRINGLITERAL && LA143_1 <= SUB)||(LA143_1 >= SUBSUB && LA143_1 <= SUPER)||LA143_1==THIS||LA143_1==TILDE||LA143_1==TRUE||LA143_1==VOID) ) {
                    alt143=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 143, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
                }
                break;
            case LPAREN:
                {
                alt143=3;
                }
                break;
            case DOT:
                {
                switch ( input.LA(2) ) {
                case CLASS:
                    {
                    alt143=4;
                    }
                    break;
                case THIS:
                    {
                    alt143=6;
                    }
                    break;
                case SUPER:
                    {
                    alt143=7;
                    }
                    break;
                case NEW:
                    {
                    alt143=8;
                    }
                    break;
                case LT:
                    {
                    alt143=5;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 143, 3, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 143, 0, input);

            	throw nvae;
            }

            switch (alt143) {
                case 1 :
                    // GraphlrJava.g:1237:9: ( '[' ']' )+ '.' 'class'
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:1237:9: ( '[' ']' )+
                    int cnt141=0;
                    loop141:
                    do {
                        int alt141=2;
                        int LA141_0 = input.LA(1);

                        if ( (LA141_0==LBRACKET) ) {
                            alt141=1;
                        }


                        switch (alt141) {
                    	case 1 :
                    	    // GraphlrJava.g:1237:10: '[' ']'
                    	    {
                    	    char_literal507=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix6958); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal507_tree = 
                    	    (Tree)adaptor.create(char_literal507)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal507_tree);
                    	    }


                    	    char_literal508=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix6960); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal508_tree = 
                    	    (Tree)adaptor.create(char_literal508)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal508_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt141 >= 1 ) break loop141;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(141, input);
                                throw eee;
                        }
                        cnt141++;
                    } while (true);


                    char_literal509=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix6981); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal509_tree = 
                    (Tree)adaptor.create(char_literal509)
                    ;
                    adaptor.addChild(root_0, char_literal509_tree);
                    }


                    string_literal510=(Token)match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix6983); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal510_tree = 
                    (Tree)adaptor.create(string_literal510)
                    ;
                    adaptor.addChild(root_0, string_literal510_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1240:9: ( '[' expression ']' )+
                    {
                    root_0 = (Tree)adaptor.nil();


                    // GraphlrJava.g:1240:9: ( '[' expression ']' )+
                    int cnt142=0;
                    loop142:
                    do {
                        int alt142=2;
                        int LA142_0 = input.LA(1);

                        if ( (LA142_0==LBRACKET) ) {
                            int LA142_2 = input.LA(2);

                            if ( (synpred224_GraphlrJava()) ) {
                                alt142=1;
                            }


                        }


                        switch (alt142) {
                    	case 1 :
                    	    // GraphlrJava.g:1240:10: '[' expression ']'
                    	    {
                    	    char_literal511=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix6994); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal511_tree = 
                    	    (Tree)adaptor.create(char_literal511)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal511_tree);
                    	    }


                    	    pushFollow(FOLLOW_expression_in_identifierSuffix6996);
                    	    expression512=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression512.getTree());


                    	    char_literal513=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix6998); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal513_tree = 
                    	    (Tree)adaptor.create(char_literal513)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal513_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt142 >= 1 ) break loop142;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(142, input);
                                throw eee;
                        }
                        cnt142++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1242:9: arguments
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_arguments_in_identifierSuffix7019);
                    arguments514=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments514.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1243:9: '.' 'class'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal515=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7029); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal515_tree = 
                    (Tree)adaptor.create(char_literal515)
                    ;
                    adaptor.addChild(root_0, char_literal515_tree);
                    }


                    string_literal516=(Token)match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix7031); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal516_tree = 
                    (Tree)adaptor.create(string_literal516)
                    ;
                    adaptor.addChild(root_0, string_literal516_tree);
                    }


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:1244:9: '.' nonWildcardTypeArguments IDENTIFIER arguments
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal517=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7041); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal517_tree = 
                    (Tree)adaptor.create(char_literal517)
                    ;
                    adaptor.addChild(root_0, char_literal517_tree);
                    }


                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7043);
                    nonWildcardTypeArguments518=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments518.getTree());


                    IDENTIFIER519=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_identifierSuffix7045); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER519_tree = 
                    (Tree)adaptor.create(IDENTIFIER519)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER519_tree);
                    }


                    pushFollow(FOLLOW_arguments_in_identifierSuffix7047);
                    arguments520=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments520.getTree());


                    }
                    break;
                case 6 :
                    // GraphlrJava.g:1245:9: '.' 'this'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal521=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7057); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal521_tree = 
                    (Tree)adaptor.create(char_literal521)
                    ;
                    adaptor.addChild(root_0, char_literal521_tree);
                    }


                    string_literal522=(Token)match(input,THIS,FOLLOW_THIS_in_identifierSuffix7059); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal522_tree = 
                    (Tree)adaptor.create(string_literal522)
                    ;
                    adaptor.addChild(root_0, string_literal522_tree);
                    }


                    }
                    break;
                case 7 :
                    // GraphlrJava.g:1246:9: '.' 'super' arguments
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal523=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix7069); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal523_tree = 
                    (Tree)adaptor.create(char_literal523)
                    ;
                    adaptor.addChild(root_0, char_literal523_tree);
                    }


                    string_literal524=(Token)match(input,SUPER,FOLLOW_SUPER_in_identifierSuffix7071); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal524_tree = 
                    (Tree)adaptor.create(string_literal524)
                    ;
                    adaptor.addChild(root_0, string_literal524_tree);
                    }


                    pushFollow(FOLLOW_arguments_in_identifierSuffix7073);
                    arguments525=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments525.getTree());


                    }
                    break;
                case 8 :
                    // GraphlrJava.g:1247:9: innerCreator
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix7083);
                    innerCreator526=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator526.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 91, identifierSuffix_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"


    public static class selector_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selector"
    // GraphlrJava.g:1251:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' );
    public final GraphlrJavaParser.selector_return selector() throws RecognitionException {
        GraphlrJavaParser.selector_return retval = new GraphlrJavaParser.selector_return();
        retval.start = input.LT(1);

        int selector_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal527=null;
        Token IDENTIFIER528=null;
        Token char_literal530=null;
        Token string_literal531=null;
        Token char_literal532=null;
        Token string_literal533=null;
        Token char_literal536=null;
        Token char_literal538=null;
        ParserRuleReturnScope arguments529 =null;

        ParserRuleReturnScope superSuffix534 =null;

        ParserRuleReturnScope innerCreator535 =null;

        ParserRuleReturnScope expression537 =null;


        Tree char_literal527_tree=null;
        Tree IDENTIFIER528_tree=null;
        Tree char_literal530_tree=null;
        Tree string_literal531_tree=null;
        Tree char_literal532_tree=null;
        Tree string_literal533_tree=null;
        Tree char_literal536_tree=null;
        Tree char_literal538_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }

            // GraphlrJava.g:1252:5: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | innerCreator | '[' expression ']' )
            int alt145=5;
            int LA145_0 = input.LA(1);

            if ( (LA145_0==DOT) ) {
                switch ( input.LA(2) ) {
                case IDENTIFIER:
                    {
                    alt145=1;
                    }
                    break;
                case THIS:
                    {
                    alt145=2;
                    }
                    break;
                case SUPER:
                    {
                    alt145=3;
                    }
                    break;
                case NEW:
                    {
                    alt145=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 145, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }

            }
            else if ( (LA145_0==LBRACKET) ) {
                alt145=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 145, 0, input);

            	throw nvae;
            }
            switch (alt145) {
                case 1 :
                    // GraphlrJava.g:1252:9: '.' IDENTIFIER ( arguments )?
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal527=(Token)match(input,DOT,FOLLOW_DOT_in_selector7105); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal527_tree = 
                    (Tree)adaptor.create(char_literal527)
                    ;
                    adaptor.addChild(root_0, char_literal527_tree);
                    }


                    IDENTIFIER528=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector7107); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENTIFIER528_tree = 
                    (Tree)adaptor.create(IDENTIFIER528)
                    ;
                    adaptor.addChild(root_0, IDENTIFIER528_tree);
                    }


                    // GraphlrJava.g:1253:9: ( arguments )?
                    int alt144=2;
                    int LA144_0 = input.LA(1);

                    if ( (LA144_0==LPAREN) ) {
                        alt144=1;
                    }
                    switch (alt144) {
                        case 1 :
                            // GraphlrJava.g:1253:10: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector7118);
                            arguments529=arguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments529.getTree());


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1255:9: '.' 'this'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal530=(Token)match(input,DOT,FOLLOW_DOT_in_selector7139); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal530_tree = 
                    (Tree)adaptor.create(char_literal530)
                    ;
                    adaptor.addChild(root_0, char_literal530_tree);
                    }


                    string_literal531=(Token)match(input,THIS,FOLLOW_THIS_in_selector7141); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal531_tree = 
                    (Tree)adaptor.create(string_literal531)
                    ;
                    adaptor.addChild(root_0, string_literal531_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1256:9: '.' 'super' superSuffix
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal532=(Token)match(input,DOT,FOLLOW_DOT_in_selector7151); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal532_tree = 
                    (Tree)adaptor.create(char_literal532)
                    ;
                    adaptor.addChild(root_0, char_literal532_tree);
                    }


                    string_literal533=(Token)match(input,SUPER,FOLLOW_SUPER_in_selector7153); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal533_tree = 
                    (Tree)adaptor.create(string_literal533)
                    ;
                    adaptor.addChild(root_0, string_literal533_tree);
                    }


                    pushFollow(FOLLOW_superSuffix_in_selector7163);
                    superSuffix534=superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, superSuffix534.getTree());


                    }
                    break;
                case 4 :
                    // GraphlrJava.g:1258:9: innerCreator
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_innerCreator_in_selector7173);
                    innerCreator535=innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, innerCreator535.getTree());


                    }
                    break;
                case 5 :
                    // GraphlrJava.g:1259:9: '[' expression ']'
                    {
                    root_0 = (Tree)adaptor.nil();


                    char_literal536=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_selector7183); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal536_tree = 
                    (Tree)adaptor.create(char_literal536)
                    ;
                    adaptor.addChild(root_0, char_literal536_tree);
                    }


                    pushFollow(FOLLOW_expression_in_selector7185);
                    expression537=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression537.getTree());


                    char_literal538=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_selector7187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal538_tree = 
                    (Tree)adaptor.create(char_literal538)
                    ;
                    adaptor.addChild(root_0, char_literal538_tree);
                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 92, selector_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "selector"


    public static class creator_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "creator"
    // GraphlrJava.g:1262:1: creator : ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator );
    public final GraphlrJavaParser.creator_return creator() throws RecognitionException {
        GraphlrJavaParser.creator_return retval = new GraphlrJavaParser.creator_return();
        retval.start = input.LT(1);

        int creator_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal539=null;
        Token string_literal543=null;
        ParserRuleReturnScope nonWildcardTypeArguments540 =null;

        ParserRuleReturnScope classOrInterfaceType541 =null;

        ParserRuleReturnScope classCreatorRest542 =null;

        ParserRuleReturnScope classOrInterfaceType544 =null;

        ParserRuleReturnScope classCreatorRest545 =null;

        ParserRuleReturnScope arrayCreator546 =null;


        Tree string_literal539_tree=null;
        Tree string_literal543_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }

            // GraphlrJava.g:1263:5: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest | 'new' classOrInterfaceType classCreatorRest | arrayCreator )
            int alt146=3;
            int LA146_0 = input.LA(1);

            if ( (LA146_0==NEW) ) {
                int LA146_1 = input.LA(2);

                if ( (synpred236_GraphlrJava()) ) {
                    alt146=1;
                }
                else if ( (synpred237_GraphlrJava()) ) {
                    alt146=2;
                }
                else if ( (true) ) {
                    alt146=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 146, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 146, 0, input);

            	throw nvae;
            }
            switch (alt146) {
                case 1 :
                    // GraphlrJava.g:1263:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal539=(Token)match(input,NEW,FOLLOW_NEW_in_creator7207); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal539_tree = 
                    (Tree)adaptor.create(string_literal539)
                    ;
                    adaptor.addChild(root_0, string_literal539_tree);
                    }


                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator7209);
                    nonWildcardTypeArguments540=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments540.getTree());


                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7211);
                    classOrInterfaceType541=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType541.getTree());


                    pushFollow(FOLLOW_classCreatorRest_in_creator7213);
                    classCreatorRest542=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest542.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1264:9: 'new' classOrInterfaceType classCreatorRest
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal543=(Token)match(input,NEW,FOLLOW_NEW_in_creator7223); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal543_tree = 
                    (Tree)adaptor.create(string_literal543)
                    ;
                    adaptor.addChild(root_0, string_literal543_tree);
                    }


                    pushFollow(FOLLOW_classOrInterfaceType_in_creator7225);
                    classOrInterfaceType544=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType544.getTree());


                    pushFollow(FOLLOW_classCreatorRest_in_creator7227);
                    classCreatorRest545=classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest545.getTree());


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1265:9: arrayCreator
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_arrayCreator_in_creator7237);
                    arrayCreator546=arrayCreator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayCreator546.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 93, creator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "creator"


    public static class arrayCreator_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arrayCreator"
    // GraphlrJava.g:1268:1: arrayCreator : ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* );
    public final GraphlrJavaParser.arrayCreator_return arrayCreator() throws RecognitionException {
        GraphlrJavaParser.arrayCreator_return retval = new GraphlrJavaParser.arrayCreator_return();
        retval.start = input.LT(1);

        int arrayCreator_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal547=null;
        Token char_literal549=null;
        Token char_literal550=null;
        Token char_literal551=null;
        Token char_literal552=null;
        Token string_literal554=null;
        Token char_literal556=null;
        Token char_literal558=null;
        Token char_literal559=null;
        Token char_literal561=null;
        Token char_literal562=null;
        Token char_literal563=null;
        ParserRuleReturnScope createdName548 =null;

        ParserRuleReturnScope arrayInitializer553 =null;

        ParserRuleReturnScope createdName555 =null;

        ParserRuleReturnScope expression557 =null;

        ParserRuleReturnScope expression560 =null;


        Tree string_literal547_tree=null;
        Tree char_literal549_tree=null;
        Tree char_literal550_tree=null;
        Tree char_literal551_tree=null;
        Tree char_literal552_tree=null;
        Tree string_literal554_tree=null;
        Tree char_literal556_tree=null;
        Tree char_literal558_tree=null;
        Tree char_literal559_tree=null;
        Tree char_literal561_tree=null;
        Tree char_literal562_tree=null;
        Tree char_literal563_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }

            // GraphlrJava.g:1269:5: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer | 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )* )
            int alt150=2;
            int LA150_0 = input.LA(1);

            if ( (LA150_0==NEW) ) {
                int LA150_1 = input.LA(2);

                if ( (synpred239_GraphlrJava()) ) {
                    alt150=1;
                }
                else if ( (true) ) {
                    alt150=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                	int nvaeMark = input.mark();
                	try {
                		input.consume();
                		NoViableAltException nvae =
                			new NoViableAltException("", 150, 1, input);

                		throw nvae;
                	} finally {
                		input.rewind(nvaeMark);
                	}
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 150, 0, input);

            	throw nvae;
            }
            switch (alt150) {
                case 1 :
                    // GraphlrJava.g:1269:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal547=(Token)match(input,NEW,FOLLOW_NEW_in_arrayCreator7257); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal547_tree = 
                    (Tree)adaptor.create(string_literal547)
                    ;
                    adaptor.addChild(root_0, string_literal547_tree);
                    }


                    pushFollow(FOLLOW_createdName_in_arrayCreator7259);
                    createdName548=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName548.getTree());


                    char_literal549=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7269); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal549_tree = 
                    (Tree)adaptor.create(char_literal549)
                    ;
                    adaptor.addChild(root_0, char_literal549_tree);
                    }


                    char_literal550=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7271); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal550_tree = 
                    (Tree)adaptor.create(char_literal550)
                    ;
                    adaptor.addChild(root_0, char_literal550_tree);
                    }


                    // GraphlrJava.g:1271:9: ( '[' ']' )*
                    loop147:
                    do {
                        int alt147=2;
                        int LA147_0 = input.LA(1);

                        if ( (LA147_0==LBRACKET) ) {
                            alt147=1;
                        }


                        switch (alt147) {
                    	case 1 :
                    	    // GraphlrJava.g:1271:10: '[' ']'
                    	    {
                    	    char_literal551=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7282); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal551_tree = 
                    	    (Tree)adaptor.create(char_literal551)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal551_tree);
                    	    }


                    	    char_literal552=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7284); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal552_tree = 
                    	    (Tree)adaptor.create(char_literal552)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal552_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop147;
                        }
                    } while (true);


                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreator7305);
                    arrayInitializer553=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer553.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1275:9: 'new' createdName '[' expression ']' ( '[' expression ']' )* ( '[' ']' )*
                    {
                    root_0 = (Tree)adaptor.nil();


                    string_literal554=(Token)match(input,NEW,FOLLOW_NEW_in_arrayCreator7316); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal554_tree = 
                    (Tree)adaptor.create(string_literal554)
                    ;
                    adaptor.addChild(root_0, string_literal554_tree);
                    }


                    pushFollow(FOLLOW_createdName_in_arrayCreator7318);
                    createdName555=createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, createdName555.getTree());


                    char_literal556=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7328); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal556_tree = 
                    (Tree)adaptor.create(char_literal556)
                    ;
                    adaptor.addChild(root_0, char_literal556_tree);
                    }


                    pushFollow(FOLLOW_expression_in_arrayCreator7330);
                    expression557=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression557.getTree());


                    char_literal558=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7340); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal558_tree = 
                    (Tree)adaptor.create(char_literal558)
                    ;
                    adaptor.addChild(root_0, char_literal558_tree);
                    }


                    // GraphlrJava.g:1278:9: ( '[' expression ']' )*
                    loop148:
                    do {
                        int alt148=2;
                        int LA148_0 = input.LA(1);

                        if ( (LA148_0==LBRACKET) ) {
                            int LA148_1 = input.LA(2);

                            if ( (synpred240_GraphlrJava()) ) {
                                alt148=1;
                            }


                        }


                        switch (alt148) {
                    	case 1 :
                    	    // GraphlrJava.g:1278:13: '[' expression ']'
                    	    {
                    	    char_literal559=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7354); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal559_tree = 
                    	    (Tree)adaptor.create(char_literal559)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal559_tree);
                    	    }


                    	    pushFollow(FOLLOW_expression_in_arrayCreator7356);
                    	    expression560=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression560.getTree());


                    	    char_literal561=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7370); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal561_tree = 
                    	    (Tree)adaptor.create(char_literal561)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal561_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop148;
                        }
                    } while (true);


                    // GraphlrJava.g:1281:9: ( '[' ']' )*
                    loop149:
                    do {
                        int alt149=2;
                        int LA149_0 = input.LA(1);

                        if ( (LA149_0==LBRACKET) ) {
                            int LA149_2 = input.LA(2);

                            if ( (LA149_2==RBRACKET) ) {
                                alt149=1;
                            }


                        }


                        switch (alt149) {
                    	case 1 :
                    	    // GraphlrJava.g:1281:10: '[' ']'
                    	    {
                    	    char_literal562=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_arrayCreator7392); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal562_tree = 
                    	    (Tree)adaptor.create(char_literal562)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal562_tree);
                    	    }


                    	    char_literal563=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_arrayCreator7394); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal563_tree = 
                    	    (Tree)adaptor.create(char_literal563)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal563_tree);
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop149;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 94, arrayCreator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arrayCreator"


    public static class variableInitializer_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableInitializer"
    // GraphlrJava.g:1285:1: variableInitializer : ( arrayInitializer | expression );
    public final GraphlrJavaParser.variableInitializer_return variableInitializer() throws RecognitionException {
        GraphlrJavaParser.variableInitializer_return retval = new GraphlrJavaParser.variableInitializer_return();
        retval.start = input.LT(1);

        int variableInitializer_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope arrayInitializer564 =null;

        ParserRuleReturnScope expression565 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }

            // GraphlrJava.g:1286:5: ( arrayInitializer | expression )
            int alt151=2;
            int LA151_0 = input.LA(1);

            if ( (LA151_0==LBRACE) ) {
                alt151=1;
            }
            else if ( (LA151_0==BANG||LA151_0==BOOLEAN||LA151_0==BYTE||(LA151_0 >= CHAR && LA151_0 <= CHARLITERAL)||(LA151_0 >= DOUBLE && LA151_0 <= DOUBLELITERAL)||LA151_0==FALSE||(LA151_0 >= FLOAT && LA151_0 <= FLOATLITERAL)||LA151_0==IDENTIFIER||LA151_0==INT||LA151_0==INTLITERAL||(LA151_0 >= LONG && LA151_0 <= LPAREN)||(LA151_0 >= NEW && LA151_0 <= NULL)||LA151_0==PLUS||LA151_0==PLUSPLUS||LA151_0==SHORT||(LA151_0 >= STRINGLITERAL && LA151_0 <= SUB)||(LA151_0 >= SUBSUB && LA151_0 <= SUPER)||LA151_0==THIS||LA151_0==TILDE||LA151_0==TRUE||LA151_0==VOID) ) {
                alt151=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 151, 0, input);

            	throw nvae;
            }
            switch (alt151) {
                case 1 :
                    // GraphlrJava.g:1286:9: arrayInitializer
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer7425);
                    arrayInitializer564=arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arrayInitializer564.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1287:9: expression
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_variableInitializer7435);
                    expression565=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression565.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 95, variableInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "variableInitializer"


    public static class arrayInitializer_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arrayInitializer"
    // GraphlrJava.g:1290:1: arrayInitializer : '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' ;
    public final GraphlrJavaParser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        GraphlrJavaParser.arrayInitializer_return retval = new GraphlrJavaParser.arrayInitializer_return();
        retval.start = input.LT(1);

        int arrayInitializer_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal566=null;
        Token char_literal568=null;
        Token char_literal570=null;
        Token char_literal571=null;
        ParserRuleReturnScope variableInitializer567 =null;

        ParserRuleReturnScope variableInitializer569 =null;


        Tree char_literal566_tree=null;
        Tree char_literal568_tree=null;
        Tree char_literal570_tree=null;
        Tree char_literal571_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }

            // GraphlrJava.g:1291:5: ( '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}' )
            // GraphlrJava.g:1291:9: '{' ( variableInitializer ( ',' variableInitializer )* )? ( ',' )? '}'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal566=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_arrayInitializer7455); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal566_tree = 
            (Tree)adaptor.create(char_literal566)
            ;
            adaptor.addChild(root_0, char_literal566_tree);
            }


            // GraphlrJava.g:1292:13: ( variableInitializer ( ',' variableInitializer )* )?
            int alt153=2;
            int LA153_0 = input.LA(1);

            if ( (LA153_0==BANG||LA153_0==BOOLEAN||LA153_0==BYTE||(LA153_0 >= CHAR && LA153_0 <= CHARLITERAL)||(LA153_0 >= DOUBLE && LA153_0 <= DOUBLELITERAL)||LA153_0==FALSE||(LA153_0 >= FLOAT && LA153_0 <= FLOATLITERAL)||LA153_0==IDENTIFIER||LA153_0==INT||LA153_0==INTLITERAL||LA153_0==LBRACE||(LA153_0 >= LONG && LA153_0 <= LPAREN)||(LA153_0 >= NEW && LA153_0 <= NULL)||LA153_0==PLUS||LA153_0==PLUSPLUS||LA153_0==SHORT||(LA153_0 >= STRINGLITERAL && LA153_0 <= SUB)||(LA153_0 >= SUBSUB && LA153_0 <= SUPER)||LA153_0==THIS||LA153_0==TILDE||LA153_0==TRUE||LA153_0==VOID) ) {
                alt153=1;
            }
            switch (alt153) {
                case 1 :
                    // GraphlrJava.g:1292:14: variableInitializer ( ',' variableInitializer )*
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7471);
                    variableInitializer567=variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer567.getTree());


                    // GraphlrJava.g:1293:17: ( ',' variableInitializer )*
                    loop152:
                    do {
                        int alt152=2;
                        int LA152_0 = input.LA(1);

                        if ( (LA152_0==COMMA) ) {
                            int LA152_1 = input.LA(2);

                            if ( (LA152_1==BANG||LA152_1==BOOLEAN||LA152_1==BYTE||(LA152_1 >= CHAR && LA152_1 <= CHARLITERAL)||(LA152_1 >= DOUBLE && LA152_1 <= DOUBLELITERAL)||LA152_1==FALSE||(LA152_1 >= FLOAT && LA152_1 <= FLOATLITERAL)||LA152_1==IDENTIFIER||LA152_1==INT||LA152_1==INTLITERAL||LA152_1==LBRACE||(LA152_1 >= LONG && LA152_1 <= LPAREN)||(LA152_1 >= NEW && LA152_1 <= NULL)||LA152_1==PLUS||LA152_1==PLUSPLUS||LA152_1==SHORT||(LA152_1 >= STRINGLITERAL && LA152_1 <= SUB)||(LA152_1 >= SUBSUB && LA152_1 <= SUPER)||LA152_1==THIS||LA152_1==TILDE||LA152_1==TRUE||LA152_1==VOID) ) {
                                alt152=1;
                            }


                        }


                        switch (alt152) {
                    	case 1 :
                    	    // GraphlrJava.g:1293:18: ',' variableInitializer
                    	    {
                    	    char_literal568=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7490); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    char_literal568_tree = 
                    	    (Tree)adaptor.create(char_literal568)
                    	    ;
                    	    adaptor.addChild(root_0, char_literal568_tree);
                    	    }


                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer7492);
                    	    variableInitializer569=variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, variableInitializer569.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop152;
                        }
                    } while (true);


                    }
                    break;

            }


            // GraphlrJava.g:1296:13: ( ',' )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==COMMA) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // GraphlrJava.g:1296:14: ','
                    {
                    char_literal570=(Token)match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer7542); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal570_tree = 
                    (Tree)adaptor.create(char_literal570)
                    ;
                    adaptor.addChild(root_0, char_literal570_tree);
                    }


                    }
                    break;

            }


            char_literal571=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_arrayInitializer7555); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal571_tree = 
            (Tree)adaptor.create(char_literal571)
            ;
            adaptor.addChild(root_0, char_literal571_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 96, arrayInitializer_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arrayInitializer"


    public static class createdName_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "createdName"
    // GraphlrJava.g:1301:1: createdName : ( classOrInterfaceType | primitiveType );
    public final GraphlrJavaParser.createdName_return createdName() throws RecognitionException {
        GraphlrJavaParser.createdName_return retval = new GraphlrJavaParser.createdName_return();
        retval.start = input.LT(1);

        int createdName_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope classOrInterfaceType572 =null;

        ParserRuleReturnScope primitiveType573 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }

            // GraphlrJava.g:1302:5: ( classOrInterfaceType | primitiveType )
            int alt155=2;
            int LA155_0 = input.LA(1);

            if ( (LA155_0==IDENTIFIER) ) {
                alt155=1;
            }
            else if ( (LA155_0==BOOLEAN||LA155_0==BYTE||LA155_0==CHAR||LA155_0==DOUBLE||LA155_0==FLOAT||LA155_0==INT||LA155_0==LONG||LA155_0==SHORT) ) {
                alt155=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 155, 0, input);

            	throw nvae;
            }
            switch (alt155) {
                case 1 :
                    // GraphlrJava.g:1302:9: classOrInterfaceType
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName7589);
                    classOrInterfaceType572=classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classOrInterfaceType572.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1303:9: primitiveType
                    {
                    root_0 = (Tree)adaptor.nil();


                    pushFollow(FOLLOW_primitiveType_in_createdName7599);
                    primitiveType573=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primitiveType573.getTree());


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 97, createdName_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "createdName"


    public static class innerCreator_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "innerCreator"
    // GraphlrJava.g:1306:1: innerCreator : '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest ;
    public final GraphlrJavaParser.innerCreator_return innerCreator() throws RecognitionException {
        GraphlrJavaParser.innerCreator_return retval = new GraphlrJavaParser.innerCreator_return();
        retval.start = input.LT(1);

        int innerCreator_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal574=null;
        Token string_literal575=null;
        Token IDENTIFIER577=null;
        ParserRuleReturnScope nonWildcardTypeArguments576 =null;

        ParserRuleReturnScope typeArguments578 =null;

        ParserRuleReturnScope classCreatorRest579 =null;


        Tree char_literal574_tree=null;
        Tree string_literal575_tree=null;
        Tree IDENTIFIER577_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }

            // GraphlrJava.g:1307:5: ( '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest )
            // GraphlrJava.g:1307:9: '.' 'new' ( nonWildcardTypeArguments )? IDENTIFIER ( typeArguments )? classCreatorRest
            {
            root_0 = (Tree)adaptor.nil();


            char_literal574=(Token)match(input,DOT,FOLLOW_DOT_in_innerCreator7620); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal574_tree = 
            (Tree)adaptor.create(char_literal574)
            ;
            adaptor.addChild(root_0, char_literal574_tree);
            }


            string_literal575=(Token)match(input,NEW,FOLLOW_NEW_in_innerCreator7622); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal575_tree = 
            (Tree)adaptor.create(string_literal575)
            ;
            adaptor.addChild(root_0, string_literal575_tree);
            }


            // GraphlrJava.g:1308:9: ( nonWildcardTypeArguments )?
            int alt156=2;
            int LA156_0 = input.LA(1);

            if ( (LA156_0==LT) ) {
                alt156=1;
            }
            switch (alt156) {
                case 1 :
                    // GraphlrJava.g:1308:10: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator7633);
                    nonWildcardTypeArguments576=nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nonWildcardTypeArguments576.getTree());


                    }
                    break;

            }


            IDENTIFIER577=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_innerCreator7654); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER577_tree = 
            (Tree)adaptor.create(IDENTIFIER577)
            ;
            adaptor.addChild(root_0, IDENTIFIER577_tree);
            }


            // GraphlrJava.g:1311:9: ( typeArguments )?
            int alt157=2;
            int LA157_0 = input.LA(1);

            if ( (LA157_0==LT) ) {
                alt157=1;
            }
            switch (alt157) {
                case 1 :
                    // GraphlrJava.g:1311:10: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_innerCreator7665);
                    typeArguments578=typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeArguments578.getTree());


                    }
                    break;

            }


            pushFollow(FOLLOW_classCreatorRest_in_innerCreator7686);
            classCreatorRest579=classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, classCreatorRest579.getTree());


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 98, innerCreator_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "innerCreator"


    public static class classCreatorRest_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classCreatorRest"
    // GraphlrJava.g:1317:1: classCreatorRest : arguments ( classBody )? ;
    public final GraphlrJavaParser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        GraphlrJavaParser.classCreatorRest_return retval = new GraphlrJavaParser.classCreatorRest_return();
        retval.start = input.LT(1);

        int classCreatorRest_StartIndex = input.index();

        Tree root_0 = null;

        ParserRuleReturnScope arguments580 =null;

        ParserRuleReturnScope classBody581 =null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }

            // GraphlrJava.g:1318:5: ( arguments ( classBody )? )
            // GraphlrJava.g:1318:9: arguments ( classBody )?
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_arguments_in_classCreatorRest7707);
            arguments580=arguments();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arguments580.getTree());


            // GraphlrJava.g:1319:9: ( classBody )?
            int alt158=2;
            int LA158_0 = input.LA(1);

            if ( (LA158_0==LBRACE) ) {
                alt158=1;
            }
            switch (alt158) {
                case 1 :
                    // GraphlrJava.g:1319:10: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest7718);
                    classBody581=classBody();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, classBody581.getTree());


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 99, classCreatorRest_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"


    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "nonWildcardTypeArguments"
    // GraphlrJava.g:1324:1: nonWildcardTypeArguments : '<' typeList '>' ;
    public final GraphlrJavaParser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        GraphlrJavaParser.nonWildcardTypeArguments_return retval = new GraphlrJavaParser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);

        int nonWildcardTypeArguments_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal582=null;
        Token char_literal584=null;
        ParserRuleReturnScope typeList583 =null;


        Tree char_literal582_tree=null;
        Tree char_literal584_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }

            // GraphlrJava.g:1325:5: ( '<' typeList '>' )
            // GraphlrJava.g:1325:9: '<' typeList '>'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal582=(Token)match(input,LT,FOLLOW_LT_in_nonWildcardTypeArguments7750); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal582_tree = 
            (Tree)adaptor.create(char_literal582)
            ;
            adaptor.addChild(root_0, char_literal582_tree);
            }


            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments7752);
            typeList583=typeList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, typeList583.getTree());


            char_literal584=(Token)match(input,GT,FOLLOW_GT_in_nonWildcardTypeArguments7762); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal584_tree = 
            (Tree)adaptor.create(char_literal584)
            ;
            adaptor.addChild(root_0, char_literal584_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 100, nonWildcardTypeArguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"


    public static class arguments_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arguments"
    // GraphlrJava.g:1329:1: arguments : '(' ( expressionList )? ')' ;
    public final GraphlrJavaParser.arguments_return arguments() throws RecognitionException {
        GraphlrJavaParser.arguments_return retval = new GraphlrJavaParser.arguments_return();
        retval.start = input.LT(1);

        int arguments_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal585=null;
        Token char_literal587=null;
        ParserRuleReturnScope expressionList586 =null;


        Tree char_literal585_tree=null;
        Tree char_literal587_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }

            // GraphlrJava.g:1330:5: ( '(' ( expressionList )? ')' )
            // GraphlrJava.g:1330:9: '(' ( expressionList )? ')'
            {
            root_0 = (Tree)adaptor.nil();


            char_literal585=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_arguments7782); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal585_tree = 
            (Tree)adaptor.create(char_literal585)
            ;
            adaptor.addChild(root_0, char_literal585_tree);
            }


            // GraphlrJava.g:1330:13: ( expressionList )?
            int alt159=2;
            int LA159_0 = input.LA(1);

            if ( (LA159_0==BANG||LA159_0==BOOLEAN||LA159_0==BYTE||(LA159_0 >= CHAR && LA159_0 <= CHARLITERAL)||(LA159_0 >= DOUBLE && LA159_0 <= DOUBLELITERAL)||LA159_0==FALSE||(LA159_0 >= FLOAT && LA159_0 <= FLOATLITERAL)||LA159_0==IDENTIFIER||LA159_0==INT||LA159_0==INTLITERAL||(LA159_0 >= LONG && LA159_0 <= LPAREN)||(LA159_0 >= NEW && LA159_0 <= NULL)||LA159_0==PLUS||LA159_0==PLUSPLUS||LA159_0==SHORT||(LA159_0 >= STRINGLITERAL && LA159_0 <= SUB)||(LA159_0 >= SUBSUB && LA159_0 <= SUPER)||LA159_0==THIS||LA159_0==TILDE||LA159_0==TRUE||LA159_0==VOID) ) {
                alt159=1;
            }
            switch (alt159) {
                case 1 :
                    // GraphlrJava.g:1330:14: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments7785);
                    expressionList586=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expressionList586.getTree());


                    }
                    break;

            }


            char_literal587=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_arguments7798); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal587_tree = 
            (Tree)adaptor.create(char_literal587)
            ;
            adaptor.addChild(root_0, char_literal587_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 101, arguments_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "arguments"


    public static class literal_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // GraphlrJava.g:1334:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final GraphlrJavaParser.literal_return literal() throws RecognitionException {
        GraphlrJavaParser.literal_return retval = new GraphlrJavaParser.literal_return();
        retval.start = input.LT(1);

        int literal_StartIndex = input.index();

        Tree root_0 = null;

        Token set588=null;

        Tree set588_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }

            // GraphlrJava.g:1335:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            // GraphlrJava.g:
            {
            root_0 = (Tree)adaptor.nil();


            set588=(Token)input.LT(1);

            if ( input.LA(1)==CHARLITERAL||input.LA(1)==DOUBLELITERAL||input.LA(1)==FALSE||input.LA(1)==FLOATLITERAL||input.LA(1)==INTLITERAL||input.LA(1)==LONGLITERAL||input.LA(1)==NULL||input.LA(1)==STRINGLITERAL||input.LA(1)==TRUE ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (Tree)adaptor.create(set588)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 102, literal_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class classHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "classHeader"
    // GraphlrJava.g:1350:1: classHeader : modifiers 'class' IDENTIFIER ;
    public final GraphlrJavaParser.classHeader_return classHeader() throws RecognitionException {
        GraphlrJavaParser.classHeader_return retval = new GraphlrJavaParser.classHeader_return();
        retval.start = input.LT(1);

        int classHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal590=null;
        Token IDENTIFIER591=null;
        ParserRuleReturnScope modifiers589 =null;


        Tree string_literal590_tree=null;
        Tree IDENTIFIER591_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }

            // GraphlrJava.g:1351:5: ( modifiers 'class' IDENTIFIER )
            // GraphlrJava.g:1351:9: modifiers 'class' IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_classHeader7922);
            modifiers589=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers589.getTree());


            string_literal590=(Token)match(input,CLASS,FOLLOW_CLASS_in_classHeader7924); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal590_tree = 
            (Tree)adaptor.create(string_literal590)
            ;
            adaptor.addChild(root_0, string_literal590_tree);
            }


            IDENTIFIER591=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classHeader7926); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER591_tree = 
            (Tree)adaptor.create(IDENTIFIER591)
            ;
            adaptor.addChild(root_0, IDENTIFIER591_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 103, classHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "classHeader"


    public static class enumHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enumHeader"
    // GraphlrJava.g:1354:1: enumHeader : modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER ;
    public final GraphlrJavaParser.enumHeader_return enumHeader() throws RecognitionException {
        GraphlrJavaParser.enumHeader_return retval = new GraphlrJavaParser.enumHeader_return();
        retval.start = input.LT(1);

        int enumHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token set593=null;
        Token IDENTIFIER594=null;
        ParserRuleReturnScope modifiers592 =null;


        Tree set593_tree=null;
        Tree IDENTIFIER594_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }

            // GraphlrJava.g:1355:5: ( modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER )
            // GraphlrJava.g:1355:9: modifiers ( 'enum' | IDENTIFIER ) IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_enumHeader7946);
            modifiers592=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers592.getTree());


            set593=(Token)input.LT(1);

            if ( input.LA(1)==ENUM||input.LA(1)==IDENTIFIER ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (Tree)adaptor.create(set593)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            IDENTIFIER594=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_enumHeader7954); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER594_tree = 
            (Tree)adaptor.create(IDENTIFIER594)
            ;
            adaptor.addChild(root_0, IDENTIFIER594_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 104, enumHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "enumHeader"


    public static class interfaceHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "interfaceHeader"
    // GraphlrJava.g:1358:1: interfaceHeader : modifiers 'interface' IDENTIFIER ;
    public final GraphlrJavaParser.interfaceHeader_return interfaceHeader() throws RecognitionException {
        GraphlrJavaParser.interfaceHeader_return retval = new GraphlrJavaParser.interfaceHeader_return();
        retval.start = input.LT(1);

        int interfaceHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal596=null;
        Token IDENTIFIER597=null;
        ParserRuleReturnScope modifiers595 =null;


        Tree string_literal596_tree=null;
        Tree IDENTIFIER597_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }

            // GraphlrJava.g:1359:5: ( modifiers 'interface' IDENTIFIER )
            // GraphlrJava.g:1359:9: modifiers 'interface' IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_interfaceHeader7974);
            modifiers595=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers595.getTree());


            string_literal596=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_interfaceHeader7976); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal596_tree = 
            (Tree)adaptor.create(string_literal596)
            ;
            adaptor.addChild(root_0, string_literal596_tree);
            }


            IDENTIFIER597=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_interfaceHeader7978); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER597_tree = 
            (Tree)adaptor.create(IDENTIFIER597)
            ;
            adaptor.addChild(root_0, IDENTIFIER597_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 105, interfaceHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "interfaceHeader"


    public static class annotationHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "annotationHeader"
    // GraphlrJava.g:1362:1: annotationHeader : modifiers '@' 'interface' IDENTIFIER ;
    public final GraphlrJavaParser.annotationHeader_return annotationHeader() throws RecognitionException {
        GraphlrJavaParser.annotationHeader_return retval = new GraphlrJavaParser.annotationHeader_return();
        retval.start = input.LT(1);

        int annotationHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token char_literal599=null;
        Token string_literal600=null;
        Token IDENTIFIER601=null;
        ParserRuleReturnScope modifiers598 =null;


        Tree char_literal599_tree=null;
        Tree string_literal600_tree=null;
        Tree IDENTIFIER601_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }

            // GraphlrJava.g:1363:5: ( modifiers '@' 'interface' IDENTIFIER )
            // GraphlrJava.g:1363:9: modifiers '@' 'interface' IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_annotationHeader7998);
            modifiers598=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers598.getTree());


            char_literal599=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_annotationHeader8000); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal599_tree = 
            (Tree)adaptor.create(char_literal599)
            ;
            adaptor.addChild(root_0, char_literal599_tree);
            }


            string_literal600=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationHeader8002); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            string_literal600_tree = 
            (Tree)adaptor.create(string_literal600)
            ;
            adaptor.addChild(root_0, string_literal600_tree);
            }


            IDENTIFIER601=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_annotationHeader8004); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER601_tree = 
            (Tree)adaptor.create(IDENTIFIER601)
            ;
            adaptor.addChild(root_0, IDENTIFIER601_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 106, annotationHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "annotationHeader"


    public static class typeHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typeHeader"
    // GraphlrJava.g:1366:1: typeHeader : modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER ;
    public final GraphlrJavaParser.typeHeader_return typeHeader() throws RecognitionException {
        GraphlrJavaParser.typeHeader_return retval = new GraphlrJavaParser.typeHeader_return();
        retval.start = input.LT(1);

        int typeHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal603=null;
        Token string_literal604=null;
        Token char_literal605=null;
        Token string_literal606=null;
        Token IDENTIFIER607=null;
        ParserRuleReturnScope modifiers602 =null;


        Tree string_literal603_tree=null;
        Tree string_literal604_tree=null;
        Tree char_literal605_tree=null;
        Tree string_literal606_tree=null;
        Tree IDENTIFIER607_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }

            // GraphlrJava.g:1367:5: ( modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER )
            // GraphlrJava.g:1367:9: modifiers ( 'class' | 'enum' | ( ( '@' )? 'interface' ) ) IDENTIFIER
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_typeHeader8024);
            modifiers602=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers602.getTree());


            // GraphlrJava.g:1367:19: ( 'class' | 'enum' | ( ( '@' )? 'interface' ) )
            int alt161=3;
            switch ( input.LA(1) ) {
            case CLASS:
                {
                alt161=1;
                }
                break;
            case ENUM:
                {
                alt161=2;
                }
                break;
            case INTERFACE:
            case MONKEYS_AT:
                {
                alt161=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
            	NoViableAltException nvae =
            		new NoViableAltException("", 161, 0, input);

            	throw nvae;
            }

            switch (alt161) {
                case 1 :
                    // GraphlrJava.g:1367:20: 'class'
                    {
                    string_literal603=(Token)match(input,CLASS,FOLLOW_CLASS_in_typeHeader8027); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal603_tree = 
                    (Tree)adaptor.create(string_literal603)
                    ;
                    adaptor.addChild(root_0, string_literal603_tree);
                    }


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1367:28: 'enum'
                    {
                    string_literal604=(Token)match(input,ENUM,FOLLOW_ENUM_in_typeHeader8029); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal604_tree = 
                    (Tree)adaptor.create(string_literal604)
                    ;
                    adaptor.addChild(root_0, string_literal604_tree);
                    }


                    }
                    break;
                case 3 :
                    // GraphlrJava.g:1367:35: ( ( '@' )? 'interface' )
                    {
                    // GraphlrJava.g:1367:35: ( ( '@' )? 'interface' )
                    // GraphlrJava.g:1367:36: ( '@' )? 'interface'
                    {
                    // GraphlrJava.g:1367:36: ( '@' )?
                    int alt160=2;
                    int LA160_0 = input.LA(1);

                    if ( (LA160_0==MONKEYS_AT) ) {
                        alt160=1;
                    }
                    switch (alt160) {
                        case 1 :
                            // GraphlrJava.g:1367:36: '@'
                            {
                            char_literal605=(Token)match(input,MONKEYS_AT,FOLLOW_MONKEYS_AT_in_typeHeader8032); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            char_literal605_tree = 
                            (Tree)adaptor.create(char_literal605)
                            ;
                            adaptor.addChild(root_0, char_literal605_tree);
                            }


                            }
                            break;

                    }


                    string_literal606=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_typeHeader8036); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal606_tree = 
                    (Tree)adaptor.create(string_literal606)
                    ;
                    adaptor.addChild(root_0, string_literal606_tree);
                    }


                    }


                    }
                    break;

            }


            IDENTIFIER607=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_typeHeader8040); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER607_tree = 
            (Tree)adaptor.create(IDENTIFIER607)
            ;
            adaptor.addChild(root_0, IDENTIFIER607_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 107, typeHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "typeHeader"


    public static class methodHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "methodHeader"
    // GraphlrJava.g:1370:1: methodHeader : modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' ;
    public final GraphlrJavaParser.methodHeader_return methodHeader() throws RecognitionException {
        GraphlrJavaParser.methodHeader_return retval = new GraphlrJavaParser.methodHeader_return();
        retval.start = input.LT(1);

        int methodHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token string_literal611=null;
        Token IDENTIFIER612=null;
        Token char_literal613=null;
        ParserRuleReturnScope modifiers608 =null;

        ParserRuleReturnScope typeParameters609 =null;

        ParserRuleReturnScope type610 =null;


        Tree string_literal611_tree=null;
        Tree IDENTIFIER612_tree=null;
        Tree char_literal613_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }

            // GraphlrJava.g:1371:5: ( modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '(' )
            // GraphlrJava.g:1371:9: modifiers ( typeParameters )? ( type | 'void' )? IDENTIFIER '('
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_methodHeader8060);
            modifiers608=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers608.getTree());


            // GraphlrJava.g:1371:19: ( typeParameters )?
            int alt162=2;
            int LA162_0 = input.LA(1);

            if ( (LA162_0==LT) ) {
                alt162=1;
            }
            switch (alt162) {
                case 1 :
                    // GraphlrJava.g:1371:19: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_methodHeader8062);
                    typeParameters609=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeParameters609.getTree());


                    }
                    break;

            }


            // GraphlrJava.g:1371:35: ( type | 'void' )?
            int alt163=3;
            switch ( input.LA(1) ) {
                case IDENTIFIER:
                    {
                    int LA163_1 = input.LA(2);

                    if ( (LA163_1==DOT||LA163_1==IDENTIFIER||LA163_1==LBRACKET||LA163_1==LT) ) {
                        alt163=1;
                    }
                    }
                    break;
                case BOOLEAN:
                case BYTE:
                case CHAR:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                case SHORT:
                    {
                    alt163=1;
                    }
                    break;
                case VOID:
                    {
                    alt163=2;
                    }
                    break;
            }

            switch (alt163) {
                case 1 :
                    // GraphlrJava.g:1371:36: type
                    {
                    pushFollow(FOLLOW_type_in_methodHeader8066);
                    type610=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type610.getTree());


                    }
                    break;
                case 2 :
                    // GraphlrJava.g:1371:41: 'void'
                    {
                    string_literal611=(Token)match(input,VOID,FOLLOW_VOID_in_methodHeader8068); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal611_tree = 
                    (Tree)adaptor.create(string_literal611)
                    ;
                    adaptor.addChild(root_0, string_literal611_tree);
                    }


                    }
                    break;

            }


            IDENTIFIER612=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodHeader8072); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER612_tree = 
            (Tree)adaptor.create(IDENTIFIER612)
            ;
            adaptor.addChild(root_0, IDENTIFIER612_tree);
            }


            char_literal613=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_methodHeader8074); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            char_literal613_tree = 
            (Tree)adaptor.create(char_literal613)
            ;
            adaptor.addChild(root_0, char_literal613_tree);
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 108, methodHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "methodHeader"


    public static class fieldHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldHeader"
    // GraphlrJava.g:1374:1: fieldHeader : modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final GraphlrJavaParser.fieldHeader_return fieldHeader() throws RecognitionException {
        GraphlrJavaParser.fieldHeader_return retval = new GraphlrJavaParser.fieldHeader_return();
        retval.start = input.LT(1);

        int fieldHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER616=null;
        Token char_literal617=null;
        Token char_literal618=null;
        Token set619=null;
        ParserRuleReturnScope modifiers614 =null;

        ParserRuleReturnScope type615 =null;


        Tree IDENTIFIER616_tree=null;
        Tree char_literal617_tree=null;
        Tree char_literal618_tree=null;
        Tree set619_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }

            // GraphlrJava.g:1375:5: ( modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // GraphlrJava.g:1375:9: modifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_modifiers_in_fieldHeader8094);
            modifiers614=modifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, modifiers614.getTree());


            pushFollow(FOLLOW_type_in_fieldHeader8096);
            type615=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type615.getTree());


            IDENTIFIER616=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldHeader8098); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER616_tree = 
            (Tree)adaptor.create(IDENTIFIER616)
            ;
            adaptor.addChild(root_0, IDENTIFIER616_tree);
            }


            // GraphlrJava.g:1375:35: ( '[' ']' )*
            loop164:
            do {
                int alt164=2;
                int LA164_0 = input.LA(1);

                if ( (LA164_0==LBRACKET) ) {
                    alt164=1;
                }


                switch (alt164) {
            	case 1 :
            	    // GraphlrJava.g:1375:36: '[' ']'
            	    {
            	    char_literal617=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_fieldHeader8101); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal617_tree = 
            	    (Tree)adaptor.create(char_literal617)
            	    ;
            	    adaptor.addChild(root_0, char_literal617_tree);
            	    }


            	    char_literal618=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_fieldHeader8102); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal618_tree = 
            	    (Tree)adaptor.create(char_literal618)
            	    ;
            	    adaptor.addChild(root_0, char_literal618_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop164;
                }
            } while (true);


            set619=(Token)input.LT(1);

            if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (Tree)adaptor.create(set619)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 109, fieldHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "fieldHeader"


    public static class localVariableHeader_return extends ParserRuleReturnScope {
        Tree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "localVariableHeader"
    // GraphlrJava.g:1378:1: localVariableHeader : variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) ;
    public final GraphlrJavaParser.localVariableHeader_return localVariableHeader() throws RecognitionException {
        GraphlrJavaParser.localVariableHeader_return retval = new GraphlrJavaParser.localVariableHeader_return();
        retval.start = input.LT(1);

        int localVariableHeader_StartIndex = input.index();

        Tree root_0 = null;

        Token IDENTIFIER622=null;
        Token char_literal623=null;
        Token char_literal624=null;
        Token set625=null;
        ParserRuleReturnScope variableModifiers620 =null;

        ParserRuleReturnScope type621 =null;


        Tree IDENTIFIER622_tree=null;
        Tree char_literal623_tree=null;
        Tree char_literal624_tree=null;
        Tree set625_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }

            // GraphlrJava.g:1379:5: ( variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' ) )
            // GraphlrJava.g:1379:9: variableModifiers type IDENTIFIER ( '[' ']' )* ( '=' | ',' | ';' )
            {
            root_0 = (Tree)adaptor.nil();


            pushFollow(FOLLOW_variableModifiers_in_localVariableHeader8132);
            variableModifiers620=variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, variableModifiers620.getTree());


            pushFollow(FOLLOW_type_in_localVariableHeader8134);
            type621=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, type621.getTree());


            IDENTIFIER622=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_localVariableHeader8136); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IDENTIFIER622_tree = 
            (Tree)adaptor.create(IDENTIFIER622)
            ;
            adaptor.addChild(root_0, IDENTIFIER622_tree);
            }


            // GraphlrJava.g:1379:43: ( '[' ']' )*
            loop165:
            do {
                int alt165=2;
                int LA165_0 = input.LA(1);

                if ( (LA165_0==LBRACKET) ) {
                    alt165=1;
                }


                switch (alt165) {
            	case 1 :
            	    // GraphlrJava.g:1379:44: '[' ']'
            	    {
            	    char_literal623=(Token)match(input,LBRACKET,FOLLOW_LBRACKET_in_localVariableHeader8139); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal623_tree = 
            	    (Tree)adaptor.create(char_literal623)
            	    ;
            	    adaptor.addChild(root_0, char_literal623_tree);
            	    }


            	    char_literal624=(Token)match(input,RBRACKET,FOLLOW_RBRACKET_in_localVariableHeader8140); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    char_literal624_tree = 
            	    (Tree)adaptor.create(char_literal624)
            	    ;
            	    adaptor.addChild(root_0, char_literal624_tree);
            	    }


            	    }
            	    break;

            	default :
            	    break loop165;
                }
            } while (true);


            set625=(Token)input.LT(1);

            if ( input.LA(1)==COMMA||input.LA(1)==EQ||input.LA(1)==SEMI ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (Tree)adaptor.create(set625)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (Tree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Tree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 110, localVariableHeader_StartIndex); }

        }
        return retval;
    }
    // $ANTLR end "localVariableHeader"

    // $ANTLR start synpred2_GraphlrJava
    public final void synpred2_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:353:13: ( ( annotations )? packageDeclaration )
        // GraphlrJava.g:353:13: ( annotations )? packageDeclaration
        {
        // GraphlrJava.g:353:13: ( annotations )?
        int alt166=2;
        int LA166_0 = input.LA(1);

        if ( (LA166_0==MONKEYS_AT) ) {
            alt166=1;
        }
        switch (alt166) {
            case 1 :
                // GraphlrJava.g:353:14: annotations
                {
                pushFollow(FOLLOW_annotations_in_synpred2_GraphlrJava120);
                annotations();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        pushFollow(FOLLOW_packageDeclaration_in_synpred2_GraphlrJava149);
        packageDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred2_GraphlrJava

    // $ANTLR start synpred12_GraphlrJava
    public final void synpred12_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:397:10: ( classDeclaration )
        // GraphlrJava.g:397:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred12_GraphlrJava506);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred12_GraphlrJava

    // $ANTLR start synpred27_GraphlrJava
    public final void synpred27_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:428:9: ( normalClassDeclaration )
        // GraphlrJava.g:428:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred27_GraphlrJava743);
        normalClassDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred27_GraphlrJava

    // $ANTLR start synpred43_GraphlrJava
    public final void synpred43_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:531:9: ( normalInterfaceDeclaration )
        // GraphlrJava.g:531:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred43_GraphlrJava1429);
        normalInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred43_GraphlrJava

    // $ANTLR start synpred52_GraphlrJava
    public final void synpred52_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:573:10: ( fieldDeclaration )
        // GraphlrJava.g:573:10: fieldDeclaration
        {
        pushFollow(FOLLOW_fieldDeclaration_in_synpred52_GraphlrJava1759);
        fieldDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred52_GraphlrJava

    // $ANTLR start synpred53_GraphlrJava
    public final void synpred53_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:574:10: ( methodDeclaration )
        // GraphlrJava.g:574:10: methodDeclaration
        {
        pushFollow(FOLLOW_methodDeclaration_in_synpred53_GraphlrJava1770);
        methodDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred53_GraphlrJava

    // $ANTLR start synpred54_GraphlrJava
    public final void synpred54_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:575:10: ( classDeclaration )
        // GraphlrJava.g:575:10: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred54_GraphlrJava1781);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred54_GraphlrJava

    // $ANTLR start synpred57_GraphlrJava
    public final void synpred57_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:607:10: ( explicitConstructorInvocation )
        // GraphlrJava.g:607:10: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred57_GraphlrJava1926);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred57_GraphlrJava

    // $ANTLR start synpred59_GraphlrJava
    public final void synpred59_GraphlrJava_fragment() throws RecognitionException {
        Token name=null;


        // GraphlrJava.g:583:10: ( modifiers ( typeParameters )? name= IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}' )
        // GraphlrJava.g:583:10: modifiers ( typeParameters )? name= IDENTIFIER formalParameters ( 'throws' qualifiedNameList )? '{' ( explicitConstructorInvocation )? ( blockStatement )* '}'
        {
        pushFollow(FOLLOW_modifiers_in_synpred59_GraphlrJava1830);
        modifiers();

        state._fsp--;
        if (state.failed) return ;


        // GraphlrJava.g:584:9: ( typeParameters )?
        int alt169=2;
        int LA169_0 = input.LA(1);

        if ( (LA169_0==LT) ) {
            alt169=1;
        }
        switch (alt169) {
            case 1 :
                // GraphlrJava.g:584:10: typeParameters
                {
                pushFollow(FOLLOW_typeParameters_in_synpred59_GraphlrJava1841);
                typeParameters();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred59_GraphlrJava1864); if (state.failed) return ;


        pushFollow(FOLLOW_formalParameters_in_synpred59_GraphlrJava1880);
        formalParameters();

        state._fsp--;
        if (state.failed) return ;


        // GraphlrJava.g:604:9: ( 'throws' qualifiedNameList )?
        int alt170=2;
        int LA170_0 = input.LA(1);

        if ( (LA170_0==THROWS) ) {
            alt170=1;
        }
        switch (alt170) {
            case 1 :
                // GraphlrJava.g:604:10: 'throws' qualifiedNameList
                {
                match(input,THROWS,FOLLOW_THROWS_in_synpred59_GraphlrJava1891); if (state.failed) return ;


                pushFollow(FOLLOW_qualifiedNameList_in_synpred59_GraphlrJava1893);
                qualifiedNameList();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        match(input,LBRACE,FOLLOW_LBRACE_in_synpred59_GraphlrJava1914); if (state.failed) return ;


        // GraphlrJava.g:607:9: ( explicitConstructorInvocation )?
        int alt171=2;
        switch ( input.LA(1) ) {
            case LT:
                {
                alt171=1;
                }
                break;
            case THIS:
                {
                int LA171_2 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case LPAREN:
                {
                int LA171_3 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case SUPER:
                {
                int LA171_4 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case IDENTIFIER:
                {
                int LA171_5 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case CHARLITERAL:
            case DOUBLELITERAL:
            case FALSE:
            case FLOATLITERAL:
            case INTLITERAL:
            case LONGLITERAL:
            case NULL:
            case STRINGLITERAL:
            case TRUE:
                {
                int LA171_6 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case NEW:
                {
                int LA171_7 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                int LA171_8 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
            case VOID:
                {
                int LA171_9 = input.LA(2);

                if ( (synpred57_GraphlrJava()) ) {
                    alt171=1;
                }
                }
                break;
        }

        switch (alt171) {
            case 1 :
                // GraphlrJava.g:607:10: explicitConstructorInvocation
                {
                pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred59_GraphlrJava1926);
                explicitConstructorInvocation();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        // GraphlrJava.g:609:9: ( blockStatement )*
        loop172:
        do {
            int alt172=2;
            int LA172_0 = input.LA(1);

            if ( (LA172_0==ABSTRACT||(LA172_0 >= ASSERT && LA172_0 <= BANG)||(LA172_0 >= BOOLEAN && LA172_0 <= BYTE)||(LA172_0 >= CHAR && LA172_0 <= CLASS)||LA172_0==CONTINUE||LA172_0==DO||(LA172_0 >= DOUBLE && LA172_0 <= DOUBLELITERAL)||LA172_0==ENUM||(LA172_0 >= FALSE && LA172_0 <= FINAL)||(LA172_0 >= FLOAT && LA172_0 <= FOR)||(LA172_0 >= IDENTIFIER && LA172_0 <= IF)||(LA172_0 >= INT && LA172_0 <= INTLITERAL)||LA172_0==LBRACE||(LA172_0 >= LONG && LA172_0 <= LT)||(LA172_0 >= MONKEYS_AT && LA172_0 <= NULL)||LA172_0==PLUS||(LA172_0 >= PLUSPLUS && LA172_0 <= PUBLIC)||LA172_0==RETURN||(LA172_0 >= SEMI && LA172_0 <= SHORT)||(LA172_0 >= STATIC && LA172_0 <= SUB)||(LA172_0 >= SUBSUB && LA172_0 <= SYNCHRONIZED)||(LA172_0 >= THIS && LA172_0 <= THROW)||(LA172_0 >= TILDE && LA172_0 <= WHILE)) ) {
                alt172=1;
            }


            switch (alt172) {
        	case 1 :
        	    // GraphlrJava.g:609:10: blockStatement
        	    {
        	    pushFollow(FOLLOW_blockStatement_in_synpred59_GraphlrJava1948);
        	    blockStatement();

        	    state._fsp--;
        	    if (state.failed) return ;


        	    }
        	    break;

        	default :
        	    break loop172;
            }
        } while (true);


        match(input,RBRACE,FOLLOW_RBRACE_in_synpred59_GraphlrJava1969); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred59_GraphlrJava

    // $ANTLR start synpred68_GraphlrJava
    public final void synpred68_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:669:9: ( interfaceFieldDeclaration )
        // GraphlrJava.g:669:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred68_GraphlrJava2352);
        interfaceFieldDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred68_GraphlrJava

    // $ANTLR start synpred69_GraphlrJava
    public final void synpred69_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:670:9: ( interfaceMethodDeclaration )
        // GraphlrJava.g:670:9: interfaceMethodDeclaration
        {
        pushFollow(FOLLOW_interfaceMethodDeclaration_in_synpred69_GraphlrJava2362);
        interfaceMethodDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred69_GraphlrJava

    // $ANTLR start synpred70_GraphlrJava
    public final void synpred70_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:671:9: ( interfaceDeclaration )
        // GraphlrJava.g:671:9: interfaceDeclaration
        {
        pushFollow(FOLLOW_interfaceDeclaration_in_synpred70_GraphlrJava2372);
        interfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred70_GraphlrJava

    // $ANTLR start synpred71_GraphlrJava
    public final void synpred71_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:672:9: ( classDeclaration )
        // GraphlrJava.g:672:9: classDeclaration
        {
        pushFollow(FOLLOW_classDeclaration_in_synpred71_GraphlrJava2382);
        classDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred71_GraphlrJava

    // $ANTLR start synpred96_GraphlrJava
    public final void synpred96_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:767:9: ( ellipsisParameterDecl )
        // GraphlrJava.g:767:9: ellipsisParameterDecl
        {
        pushFollow(FOLLOW_ellipsisParameterDecl_in_synpred96_GraphlrJava3146);
        ellipsisParameterDecl();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred96_GraphlrJava

    // $ANTLR start synpred98_GraphlrJava
    public final void synpred98_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:768:9: ( normalParameterDecl ( ',' normalParameterDecl )* )
        // GraphlrJava.g:768:9: normalParameterDecl ( ',' normalParameterDecl )*
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred98_GraphlrJava3156);
        normalParameterDecl();

        state._fsp--;
        if (state.failed) return ;


        // GraphlrJava.g:769:9: ( ',' normalParameterDecl )*
        loop175:
        do {
            int alt175=2;
            int LA175_0 = input.LA(1);

            if ( (LA175_0==COMMA) ) {
                alt175=1;
            }


            switch (alt175) {
        	case 1 :
        	    // GraphlrJava.g:769:10: ',' normalParameterDecl
        	    {
        	    match(input,COMMA,FOLLOW_COMMA_in_synpred98_GraphlrJava3167); if (state.failed) return ;


        	    pushFollow(FOLLOW_normalParameterDecl_in_synpred98_GraphlrJava3169);
        	    normalParameterDecl();

        	    state._fsp--;
        	    if (state.failed) return ;


        	    }
        	    break;

        	default :
        	    break loop175;
            }
        } while (true);


        }

    }
    // $ANTLR end synpred98_GraphlrJava

    // $ANTLR start synpred99_GraphlrJava
    public final void synpred99_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:771:10: ( normalParameterDecl ',' )
        // GraphlrJava.g:771:10: normalParameterDecl ','
        {
        pushFollow(FOLLOW_normalParameterDecl_in_synpred99_GraphlrJava3191);
        normalParameterDecl();

        state._fsp--;
        if (state.failed) return ;


        match(input,COMMA,FOLLOW_COMMA_in_synpred99_GraphlrJava3201); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred99_GraphlrJava

    // $ANTLR start synpred103_GraphlrJava
    public final void synpred103_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:791:9: ( ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';' )
        // GraphlrJava.g:791:9: ( nonWildcardTypeArguments )? ( 'this' | 'super' ) arguments ';'
        {
        // GraphlrJava.g:791:9: ( nonWildcardTypeArguments )?
        int alt176=2;
        int LA176_0 = input.LA(1);

        if ( (LA176_0==LT) ) {
            alt176=1;
        }
        switch (alt176) {
            case 1 :
                // GraphlrJava.g:791:10: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred103_GraphlrJava3336);
                nonWildcardTypeArguments();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
            input.consume();
            state.errorRecovery=false;
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        pushFollow(FOLLOW_arguments_in_synpred103_GraphlrJava3394);
        arguments();

        state._fsp--;
        if (state.failed) return ;


        match(input,SEMI,FOLLOW_SEMI_in_synpred103_GraphlrJava3396); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred103_GraphlrJava

    // $ANTLR start synpred117_GraphlrJava
    public final void synpred117_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:878:9: ( annotationMethodDeclaration )
        // GraphlrJava.g:878:9: annotationMethodDeclaration
        {
        pushFollow(FOLLOW_annotationMethodDeclaration_in_synpred117_GraphlrJava3995);
        annotationMethodDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred117_GraphlrJava

    // $ANTLR start synpred118_GraphlrJava
    public final void synpred118_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:879:9: ( interfaceFieldDeclaration )
        // GraphlrJava.g:879:9: interfaceFieldDeclaration
        {
        pushFollow(FOLLOW_interfaceFieldDeclaration_in_synpred118_GraphlrJava4005);
        interfaceFieldDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred118_GraphlrJava

    // $ANTLR start synpred119_GraphlrJava
    public final void synpred119_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:880:9: ( normalClassDeclaration )
        // GraphlrJava.g:880:9: normalClassDeclaration
        {
        pushFollow(FOLLOW_normalClassDeclaration_in_synpred119_GraphlrJava4015);
        normalClassDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred119_GraphlrJava

    // $ANTLR start synpred120_GraphlrJava
    public final void synpred120_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:881:9: ( normalInterfaceDeclaration )
        // GraphlrJava.g:881:9: normalInterfaceDeclaration
        {
        pushFollow(FOLLOW_normalInterfaceDeclaration_in_synpred120_GraphlrJava4025);
        normalInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred120_GraphlrJava

    // $ANTLR start synpred121_GraphlrJava
    public final void synpred121_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:882:9: ( enumDeclaration )
        // GraphlrJava.g:882:9: enumDeclaration
        {
        pushFollow(FOLLOW_enumDeclaration_in_synpred121_GraphlrJava4035);
        enumDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred121_GraphlrJava

    // $ANTLR start synpred122_GraphlrJava
    public final void synpred122_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:883:9: ( annotationTypeDeclaration )
        // GraphlrJava.g:883:9: annotationTypeDeclaration
        {
        pushFollow(FOLLOW_annotationTypeDeclaration_in_synpred122_GraphlrJava4045);
        annotationTypeDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred122_GraphlrJava

    // $ANTLR start synpred125_GraphlrJava
    public final void synpred125_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:926:9: ( localVariableDeclarationStatement )
        // GraphlrJava.g:926:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred125_GraphlrJava4203);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred125_GraphlrJava

    // $ANTLR start synpred126_GraphlrJava
    public final void synpred126_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:927:9: ( classOrInterfaceDeclaration )
        // GraphlrJava.g:927:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred126_GraphlrJava4213);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred126_GraphlrJava

    // $ANTLR start synpred130_GraphlrJava
    public final void synpred130_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:947:9: ( ( 'assert' ) expression ( ':' expression )? ';' )
        // GraphlrJava.g:947:9: ( 'assert' ) expression ( ':' expression )? ';'
        {
        // GraphlrJava.g:947:9: ( 'assert' )
        // GraphlrJava.g:947:10: 'assert'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred130_GraphlrJava4354); if (state.failed) return ;


        }


        pushFollow(FOLLOW_expression_in_synpred130_GraphlrJava4374);
        expression();

        state._fsp--;
        if (state.failed) return ;


        // GraphlrJava.g:949:20: ( ':' expression )?
        int alt179=2;
        int LA179_0 = input.LA(1);

        if ( (LA179_0==COLON) ) {
            alt179=1;
        }
        switch (alt179) {
            case 1 :
                // GraphlrJava.g:949:21: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred130_GraphlrJava4377); if (state.failed) return ;


                pushFollow(FOLLOW_expression_in_synpred130_GraphlrJava4379);
                expression();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        match(input,SEMI,FOLLOW_SEMI_in_synpred130_GraphlrJava4383); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred130_GraphlrJava

    // $ANTLR start synpred132_GraphlrJava
    public final void synpred132_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:950:9: ( 'assert' expression ( ':' expression )? ';' )
        // GraphlrJava.g:950:9: 'assert' expression ( ':' expression )? ';'
        {
        match(input,ASSERT,FOLLOW_ASSERT_in_synpred132_GraphlrJava4393); if (state.failed) return ;


        pushFollow(FOLLOW_expression_in_synpred132_GraphlrJava4396);
        expression();

        state._fsp--;
        if (state.failed) return ;


        // GraphlrJava.g:950:30: ( ':' expression )?
        int alt180=2;
        int LA180_0 = input.LA(1);

        if ( (LA180_0==COLON) ) {
            alt180=1;
        }
        switch (alt180) {
            case 1 :
                // GraphlrJava.g:950:31: ':' expression
                {
                match(input,COLON,FOLLOW_COLON_in_synpred132_GraphlrJava4399); if (state.failed) return ;


                pushFollow(FOLLOW_expression_in_synpred132_GraphlrJava4401);
                expression();

                state._fsp--;
                if (state.failed) return ;


                }
                break;

        }


        match(input,SEMI,FOLLOW_SEMI_in_synpred132_GraphlrJava4405); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred132_GraphlrJava

    // $ANTLR start synpred133_GraphlrJava
    public final void synpred133_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:951:39: ( 'else' statement )
        // GraphlrJava.g:951:39: 'else' statement
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred133_GraphlrJava4434); if (state.failed) return ;


        pushFollow(FOLLOW_statement_in_synpred133_GraphlrJava4436);
        statement();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred133_GraphlrJava

    // $ANTLR start synpred148_GraphlrJava
    public final void synpred148_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:966:9: ( expression ';' )
        // GraphlrJava.g:966:9: expression ';'
        {
        pushFollow(FOLLOW_expression_in_synpred148_GraphlrJava4658);
        expression();

        state._fsp--;
        if (state.failed) return ;


        match(input,SEMI,FOLLOW_SEMI_in_synpred148_GraphlrJava4661); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred148_GraphlrJava

    // $ANTLR start synpred149_GraphlrJava
    public final void synpred149_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:967:9: ( IDENTIFIER ':' statement )
        // GraphlrJava.g:967:9: IDENTIFIER ':' statement
        {
        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred149_GraphlrJava4676); if (state.failed) return ;


        match(input,COLON,FOLLOW_COLON_in_synpred149_GraphlrJava4678); if (state.failed) return ;


        pushFollow(FOLLOW_statement_in_synpred149_GraphlrJava4680);
        statement();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred149_GraphlrJava

    // $ANTLR start synpred153_GraphlrJava
    public final void synpred153_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:991:13: ( catches 'finally' block )
        // GraphlrJava.g:991:13: catches 'finally' block
        {
        pushFollow(FOLLOW_catches_in_synpred153_GraphlrJava4836);
        catches();

        state._fsp--;
        if (state.failed) return ;


        match(input,FINALLY,FOLLOW_FINALLY_in_synpred153_GraphlrJava4838); if (state.failed) return ;


        pushFollow(FOLLOW_block_in_synpred153_GraphlrJava4840);
        block();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred153_GraphlrJava

    // $ANTLR start synpred154_GraphlrJava
    public final void synpred154_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:992:13: ( catches )
        // GraphlrJava.g:992:13: catches
        {
        pushFollow(FOLLOW_catches_in_synpred154_GraphlrJava4854);
        catches();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred154_GraphlrJava

    // $ANTLR start synpred157_GraphlrJava
    public final void synpred157_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1017:9: ( 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement )
        // GraphlrJava.g:1017:9: 'for' '(' variableModifiers type IDENTIFIER ':' expression ')' statement
        {
        match(input,FOR,FOLLOW_FOR_in_synpred157_GraphlrJava5046); if (state.failed) return ;


        match(input,LPAREN,FOLLOW_LPAREN_in_synpred157_GraphlrJava5048); if (state.failed) return ;


        pushFollow(FOLLOW_variableModifiers_in_synpred157_GraphlrJava5050);
        variableModifiers();

        state._fsp--;
        if (state.failed) return ;


        pushFollow(FOLLOW_type_in_synpred157_GraphlrJava5052);
        type();

        state._fsp--;
        if (state.failed) return ;


        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred157_GraphlrJava5054); if (state.failed) return ;


        match(input,COLON,FOLLOW_COLON_in_synpred157_GraphlrJava5056); if (state.failed) return ;


        pushFollow(FOLLOW_expression_in_synpred157_GraphlrJava5067);
        expression();

        state._fsp--;
        if (state.failed) return ;


        match(input,RPAREN,FOLLOW_RPAREN_in_synpred157_GraphlrJava5069); if (state.failed) return ;


        pushFollow(FOLLOW_statement_in_synpred157_GraphlrJava5071);
        statement();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred157_GraphlrJava

    // $ANTLR start synpred161_GraphlrJava
    public final void synpred161_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1031:9: ( localVariableDeclaration )
        // GraphlrJava.g:1031:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred161_GraphlrJava5250);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred161_GraphlrJava

    // $ANTLR start synpred202_GraphlrJava
    public final void synpred202_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1185:9: ( castExpression )
        // GraphlrJava.g:1185:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred202_GraphlrJava6495);
        castExpression();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred202_GraphlrJava

    // $ANTLR start synpred206_GraphlrJava
    public final void synpred206_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1195:9: ( '(' primitiveType ')' unaryExpression )
        // GraphlrJava.g:1195:9: '(' primitiveType ')' unaryExpression
        {
        match(input,LPAREN,FOLLOW_LPAREN_in_synpred206_GraphlrJava6586); if (state.failed) return ;


        pushFollow(FOLLOW_primitiveType_in_synpred206_GraphlrJava6588);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;


        match(input,RPAREN,FOLLOW_RPAREN_in_synpred206_GraphlrJava6590); if (state.failed) return ;


        pushFollow(FOLLOW_unaryExpression_in_synpred206_GraphlrJava6592);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred206_GraphlrJava

    // $ANTLR start synpred208_GraphlrJava
    public final void synpred208_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1205:10: ( '.' IDENTIFIER )
        // GraphlrJava.g:1205:10: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred208_GraphlrJava6663); if (state.failed) return ;


        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred208_GraphlrJava6665); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred208_GraphlrJava

    // $ANTLR start synpred209_GraphlrJava
    public final void synpred209_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1207:10: ( identifierSuffix )
        // GraphlrJava.g:1207:10: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred209_GraphlrJava6687);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred209_GraphlrJava

    // $ANTLR start synpred211_GraphlrJava
    public final void synpred211_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1210:10: ( '.' IDENTIFIER )
        // GraphlrJava.g:1210:10: '.' IDENTIFIER
        {
        match(input,DOT,FOLLOW_DOT_in_synpred211_GraphlrJava6719); if (state.failed) return ;


        match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_synpred211_GraphlrJava6721); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred211_GraphlrJava

    // $ANTLR start synpred212_GraphlrJava
    public final void synpred212_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1212:10: ( identifierSuffix )
        // GraphlrJava.g:1212:10: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred212_GraphlrJava6743);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred212_GraphlrJava

    // $ANTLR start synpred224_GraphlrJava
    public final void synpred224_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1240:10: ( '[' expression ']' )
        // GraphlrJava.g:1240:10: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred224_GraphlrJava6994); if (state.failed) return ;


        pushFollow(FOLLOW_expression_in_synpred224_GraphlrJava6996);
        expression();

        state._fsp--;
        if (state.failed) return ;


        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred224_GraphlrJava6998); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred224_GraphlrJava

    // $ANTLR start synpred236_GraphlrJava
    public final void synpred236_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1263:9: ( 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest )
        // GraphlrJava.g:1263:9: 'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
        {
        match(input,NEW,FOLLOW_NEW_in_synpred236_GraphlrJava7207); if (state.failed) return ;


        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred236_GraphlrJava7209);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;


        pushFollow(FOLLOW_classOrInterfaceType_in_synpred236_GraphlrJava7211);
        classOrInterfaceType();

        state._fsp--;
        if (state.failed) return ;


        pushFollow(FOLLOW_classCreatorRest_in_synpred236_GraphlrJava7213);
        classCreatorRest();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred236_GraphlrJava

    // $ANTLR start synpred237_GraphlrJava
    public final void synpred237_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1264:9: ( 'new' classOrInterfaceType classCreatorRest )
        // GraphlrJava.g:1264:9: 'new' classOrInterfaceType classCreatorRest
        {
        match(input,NEW,FOLLOW_NEW_in_synpred237_GraphlrJava7223); if (state.failed) return ;


        pushFollow(FOLLOW_classOrInterfaceType_in_synpred237_GraphlrJava7225);
        classOrInterfaceType();

        state._fsp--;
        if (state.failed) return ;


        pushFollow(FOLLOW_classCreatorRest_in_synpred237_GraphlrJava7227);
        classCreatorRest();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred237_GraphlrJava

    // $ANTLR start synpred239_GraphlrJava
    public final void synpred239_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1269:9: ( 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer )
        // GraphlrJava.g:1269:9: 'new' createdName '[' ']' ( '[' ']' )* arrayInitializer
        {
        match(input,NEW,FOLLOW_NEW_in_synpred239_GraphlrJava7257); if (state.failed) return ;


        pushFollow(FOLLOW_createdName_in_synpred239_GraphlrJava7259);
        createdName();

        state._fsp--;
        if (state.failed) return ;


        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_GraphlrJava7269); if (state.failed) return ;


        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_GraphlrJava7271); if (state.failed) return ;


        // GraphlrJava.g:1271:9: ( '[' ']' )*
        loop193:
        do {
            int alt193=2;
            int LA193_0 = input.LA(1);

            if ( (LA193_0==LBRACKET) ) {
                alt193=1;
            }


            switch (alt193) {
        	case 1 :
        	    // GraphlrJava.g:1271:10: '[' ']'
        	    {
        	    match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred239_GraphlrJava7282); if (state.failed) return ;


        	    match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred239_GraphlrJava7284); if (state.failed) return ;


        	    }
        	    break;

        	default :
        	    break loop193;
            }
        } while (true);


        pushFollow(FOLLOW_arrayInitializer_in_synpred239_GraphlrJava7305);
        arrayInitializer();

        state._fsp--;
        if (state.failed) return ;


        }

    }
    // $ANTLR end synpred239_GraphlrJava

    // $ANTLR start synpred240_GraphlrJava
    public final void synpred240_GraphlrJava_fragment() throws RecognitionException {
        // GraphlrJava.g:1278:13: ( '[' expression ']' )
        // GraphlrJava.g:1278:13: '[' expression ']'
        {
        match(input,LBRACKET,FOLLOW_LBRACKET_in_synpred240_GraphlrJava7354); if (state.failed) return ;


        pushFollow(FOLLOW_expression_in_synpred240_GraphlrJava7356);
        expression();

        state._fsp--;
        if (state.failed) return ;


        match(input,RBRACKET,FOLLOW_RBRACKET_in_synpred240_GraphlrJava7370); if (state.failed) return ;


        }

    }
    // $ANTLR end synpred240_GraphlrJava

    // Delegated rules

    public final boolean synpred212_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred212_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred126_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred126_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred148_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred148_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred70_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred70_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred121_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred121_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred240_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred240_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred98_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred98_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred224_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred224_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred133_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred133_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred239_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred239_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred154_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred154_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred53_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred53_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred125_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred125_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred161_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred161_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred117_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred117_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred119_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred119_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred69_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred69_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred132_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred132_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred236_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred236_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred237_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred237_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred71_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred71_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred153_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred153_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred149_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred149_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred206_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred206_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred52_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred52_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred202_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred202_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred43_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred43_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred209_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred209_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred68_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred68_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred208_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred208_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred157_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred157_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred54_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred54_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred57_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred57_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred118_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred118_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred130_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred130_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred211_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred211_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred59_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred59_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred96_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred96_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred103_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred103_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred99_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred99_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred120_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred120_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred122_GraphlrJava() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred122_GraphlrJava_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_annotations_in_compilationUnit120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit149 = new BitSet(new long[]{0x1200102000800012L,0x0011040C10700600L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit171 = new BitSet(new long[]{0x1200102000800012L,0x0011040C10700600L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit193 = new BitSet(new long[]{0x1000102000800012L,0x0011040C10700600L});
    public static final BitSet FOLLOW_PACKAGE_in_packageDeclaration224 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration226 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_packageDeclaration236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclaration257 = new BitSet(new long[]{0x0040000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_STATIC_in_importDeclaration268 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration289 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration291 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclaration293 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclaration303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclaration320 = new BitSet(new long[]{0x0040000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_STATIC_in_importDeclaration332 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration353 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration364 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_importDeclaration366 = new BitSet(new long[]{0x0000000080000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration388 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_STAR_in_importDeclaration390 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_importDeclaration411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName431 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedImportName442 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedImportName444 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_typeDeclaration485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifiers551 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_PUBLIC_in_modifiers561 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_PROTECTED_in_modifiers571 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_PRIVATE_in_modifiers581 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_STATIC_in_modifiers591 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_ABSTRACT_in_modifiers601 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_FINAL_in_modifiers611 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_NATIVE_in_modifiers621 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_modifiers631 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_TRANSIENT_in_modifiers641 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_VOLATILE_in_modifiers651 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_STRICTFP_in_modifiers661 = new BitSet(new long[]{0x0000100000000012L,0x0011040C00700600L});
    public static final BitSet FOLLOW_FINAL_in_variableModifiers693 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_annotation_in_variableModifiers707 = new BitSet(new long[]{0x0000100000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalClassDeclaration773 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_normalClassDeclaration775 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalClassDeclaration779 = new BitSet(new long[]{0x0100010000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration796 = new BitSet(new long[]{0x0100010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_normalClassDeclaration818 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration820 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_normalClassDeclaration842 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration844 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_typeParameters898 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters912 = new BitSet(new long[]{0x0008000002000000L});
    public static final BitSet FOLLOW_COMMA_in_typeParameters927 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters929 = new BitSet(new long[]{0x0008000002000000L});
    public static final BitSet FOLLOW_GT_in_typeParameters954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeParameter974 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_typeParameter985 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound1019 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AMP_in_typeBound1030 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_typeBound1032 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_modifiers_in_enumDeclaration1064 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1076 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumDeclaration1097 = new BitSet(new long[]{0x0100000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_enumDeclaration1108 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration1110 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_enumBody1156 = new BitSet(new long[]{0x0040000002000000L,0x0000000011000200L});
    public static final BitSet FOLLOW_enumConstants_in_enumBody1167 = new BitSet(new long[]{0x0000000002000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_COMMA_in_enumBody1189 = new BitSet(new long[]{0x0000000000000000L,0x0000000011000000L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody1202 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RBRACE_in_enumBody1224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1244 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_enumConstants1255 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1257 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant1291 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumConstant1312 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000042L});
    public static final BitSet FOLLOW_arguments_in_enumConstant1323 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_classBody_in_enumConstant1345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_enumBodyDeclarations1386 = new BitSet(new long[]{0x1840502100A14012L,0x0019040C30700692L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1398 = new BitSet(new long[]{0x1840502100A14012L,0x0019040C30700692L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_normalInterfaceDeclaration1463 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_normalInterfaceDeclaration1465 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalInterfaceDeclaration1467 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000082L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1478 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_normalInterfaceDeclaration1500 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1502 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1543 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList1554 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_typeList1556 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_LBRACE_in_classBody1587 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700692L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1599 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700692L});
    public static final BitSet FOLLOW_RBRACE_in_classBody1621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_interfaceBody1641 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700690L});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1653 = new BitSet(new long[]{0x1840502100A14010L,0x0019040C31700690L});
    public static final BitSet FOLLOW_RBRACE_in_interfaceBody1675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_classBodyDeclaration1695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_classBodyDeclaration1706 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration1738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDecl1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDecl1770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1830 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclaration1841 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration1864 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaration1880 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000002L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclaration1891 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration1893 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_methodDeclaration1914 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1EF2L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_methodDeclaration1926 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_blockStatement_in_methodDeclaration1948 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_RBRACE_in_methodDeclaration1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodDeclaration1979 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
    public static final BitSet FOLLOW_typeParameters_in_methodDeclaration1990 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
    public static final BitSet FOLLOW_type_in_methodDeclaration2012 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_VOID_in_methodDeclaration2026 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodDeclaration2048 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaration2064 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000006L});
    public static final BitSet FOLLOW_LBRACKET_in_methodDeclaration2075 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_methodDeclaration2077 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000006L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclaration2099 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaration2101 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000002L});
    public static final BitSet FOLLOW_block_in_methodDeclaration2156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_methodDeclaration2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldDeclaration2202 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_fieldDeclaration2212 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2222 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COMMA_in_fieldDeclaration2233 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_fieldDeclaration2235 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_fieldDeclaration2256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variableDeclarator2276 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_variableDeclarator2287 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_variableDeclarator2289 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_EQ_in_variableDeclarator2311 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1872L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_interfaceBodyDeclaration2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_interfaceBodyDeclaration2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceBodyDeclaration2372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceBodyDeclaration2382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_interfaceBodyDeclaration2392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceMethodDeclaration2412 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceMethodDeclaration2423 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
    public static final BitSet FOLLOW_type_in_interfaceMethodDeclaration2445 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_VOID_in_interfaceMethodDeclaration2456 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceMethodDeclaration2476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaration2486 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000004L});
    public static final BitSet FOLLOW_LBRACKET_in_interfaceMethodDeclaration2497 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_interfaceMethodDeclaration2499 = new BitSet(new long[]{0x0000000000000000L,0x0000400010000004L});
    public static final BitSet FOLLOW_THROWS_in_interfaceMethodDeclaration2521 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaration2523 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceMethodDeclaration2536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceFieldDeclaration2558 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_interfaceFieldDeclaration2560 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2562 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COMMA_in_interfaceFieldDeclaration2573 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_interfaceFieldDeclaration2575 = new BitSet(new long[]{0x0000000002000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_interfaceFieldDeclaration2596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type2617 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_type2628 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2630 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_type2651 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_type2662 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_type2664 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2696 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2707 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType2729 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType2731 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2746 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_LT_in_typeArguments2883 = new BitSet(new long[]{0x0840400100214000L,0x0000000020800010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2885 = new BitSet(new long[]{0x0008000002000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments2896 = new BitSet(new long[]{0x0840400100214000L,0x0000000020800010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2898 = new BitSet(new long[]{0x0008000002000000L});
    public static final BitSet FOLLOW_GT_in_typeArguments2920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument2950 = new BitSet(new long[]{0x0000010000000002L,0x0000010000000000L});
    public static final BitSet FOLLOW_set_in_typeArgument2974 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_typeArgument3018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3049 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_qualifiedNameList3060 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3062 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_LPAREN_in_formalParameters3093 = new BitSet(new long[]{0x0840500100214000L,0x0000000028000210L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3104 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_formalParameters3126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3156 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3167 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3169 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_formalParameterDecls3191 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDecls3201 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_formalParameterDecls3223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_normalParameterDecl3243 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_normalParameterDecl3245 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_normalParameterDecl3247 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_normalParameterDecl3258 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_normalParameterDecl3260 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableModifiers_in_ellipsisParameterDecl3291 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_ellipsisParameterDecl3301 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_ELLIPSIS_in_ellipsisParameterDecl3304 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ellipsisParameterDecl3314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3336 = new BitSet(new long[]{0x0000000000000000L,0x0000110000000000L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3362 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3394 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3407 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DOT_in_explicitConstructorInvocation3417 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000080L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3428 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3459 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_explicitConstructorInvocation3461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3481 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName3492 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_qualifiedName3494 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_annotation_in_annotations3526 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotation3559 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_annotation3561 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_annotation3575 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1A72L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3602 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_elementValue_in_annotation3626 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_annotation3662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3694 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_elementValuePairs3705 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3707 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_elementValuePair3738 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_elementValuePair3740 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_elementValueArrayInitializer3802 = new BitSet(new long[]{0x2840C80302614200L,0x000A91B0210A1A72L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3813 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer3828 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer3830 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer3859 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RBRACE_in_elementValueArrayInitializer3863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeDeclaration3886 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationTypeDeclaration3888 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationTypeDeclaration3898 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationTypeDeclaration3908 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration3918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_annotationTypeBody3939 = new BitSet(new long[]{0x1840502100A14010L,0x0011040C31700610L});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody3951 = new BitSet(new long[]{0x1840502100A14010L,0x0011040C31700610L});
    public static final BitSet FOLLOW_RBRACE_in_annotationTypeBody3973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_annotationTypeElementDeclaration3995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_annotationTypeElementDeclaration4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementDeclaration4015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementDeclaration4025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementDeclaration4035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementDeclaration4045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_annotationTypeElementDeclaration4055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationMethodDeclaration4075 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_annotationMethodDeclaration4077 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationMethodDeclaration4079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_annotationMethodDeclaration4089 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_annotationMethodDeclaration4091 = new BitSet(new long[]{0x0000000020000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_DEFAULT_in_annotationMethodDeclaration4094 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1A72L});
    public static final BitSet FOLLOW_elementValue_in_annotationMethodDeclaration4096 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_annotationMethodDeclaration4125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_block4149 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_blockStatement_in_block4160 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_RBRACE_in_block4181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement4223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4244 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_localVariableDeclarationStatement4254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4274 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4276 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4286 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_localVariableDeclaration4297 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_localVariableDeclaration4299 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_block_in_statement4330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4354 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_statement4374 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_statement4377 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_statement4379 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4393 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_statement4396 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_statement4399 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_statement4401 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement4427 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_parExpression_in_statement4429 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_statement4431 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_ELSE_in_statement4434 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_statement4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forstatement_in_statement4458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement4468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_parExpression_in_statement4470 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_statement4472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement4482 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_statement4484 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_WHILE_in_statement4486 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_parExpression_in_statement4488 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trystatement_in_statement4500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SWITCH_in_statement4510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_parExpression_in_statement4512 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_statement4514 = new BitSet(new long[]{0x0000000020080000L,0x0000000001000000L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4516 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RBRACE_in_statement4518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_statement4528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_parExpression_in_statement4530 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_statement4532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement4542 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0300A1870L});
    public static final BitSet FOLLOW_expression_in_statement4545 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_statement4560 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_statement4562 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_statement4574 = new BitSet(new long[]{0x0040000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4589 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement4616 = new BitSet(new long[]{0x0040000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4631 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statement4658 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_statement4661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_statement4676 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_statement4678 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_statement4680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_statement4690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups4712 = new BitSet(new long[]{0x0000000020080002L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup4741 = new BitSet(new long[]{0x38C1D82350E1C312L,0x003FB7BC347A1E72L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup4752 = new BitSet(new long[]{0x38C1D82350E1C312L,0x003FB7BC347A1E72L});
    public static final BitSet FOLLOW_CASE_in_switchLabel4783 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_switchLabel4785 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_switchLabel4797 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_switchLabel4799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_trystatement4820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_trystatement4822 = new BitSet(new long[]{0x0000200000100000L});
    public static final BitSet FOLLOW_catches_in_trystatement4836 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement4838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_trystatement4840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_trystatement4854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_trystatement4868 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_trystatement4870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4901 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_catchClause_in_catches4912 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_CATCH_in_catchClause4943 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_catchClause4945 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause4947 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_catchClause4957 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_catchClause4959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter4980 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_formalParameter4982 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_formalParameter4984 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_formalParameter4995 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_formalParameter4997 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_FOR_in_forstatement5046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5048 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_variableModifiers_in_forstatement5050 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_forstatement5052 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_forstatement5054 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_forstatement5056 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_forstatement5067 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5069 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_forstatement5071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_forstatement5103 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_forstatement5105 = new BitSet(new long[]{0x2840D80300614200L,0x000A91B0300A1A70L});
    public static final BitSet FOLLOW_forInit_in_forstatement5125 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5146 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0300A1870L});
    public static final BitSet FOLLOW_expression_in_forstatement5166 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_forstatement5187 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1870L});
    public static final BitSet FOLLOW_expressionList_in_forstatement5207 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_forstatement5228 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_forstatement5230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit5250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit5260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_parExpression5280 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_parExpression5282 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_parExpression5284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList5304 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList5315 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_expressionList5317 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression5349 = new BitSet(new long[]{0x0008004000042082L,0x0000004280050080L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression5360 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_expression5362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSEQ_in_assignmentOperator5404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBEQ_in_assignmentOperator5414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAREQ_in_assignmentOperator5424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLASHEQ_in_assignmentOperator5434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPEQ_in_assignmentOperator5444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAREQ_in_assignmentOperator5454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETEQ_in_assignmentOperator5464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENTEQ_in_assignmentOperator5474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5485 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_LT_in_assignmentOperator5487 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5500 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5502 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5504 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5517 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_assignmentOperator5519 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_assignmentOperator5521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5542 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression5553 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5555 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression5557 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression5559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5590 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression5601 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5603 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5634 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression5645 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5647 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5678 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression5689 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5691 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5722 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression5733 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5735 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5766 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AMP_in_andExpression5777 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5779 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5810 = new BitSet(new long[]{0x0000008000000402L});
    public static final BitSet FOLLOW_set_in_equalityExpression5837 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5887 = new BitSet(new long[]{0x0000008000000402L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5918 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression5929 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5962 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5973 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5975 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_LT_in_relationalOp6007 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp6009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp6020 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp6022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp6032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp6042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6062 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression6073 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6075 = new BitSet(new long[]{0x0008000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_LT_in_shiftOp6108 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_LT_in_shiftOp6110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6121 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6123 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_shiftOp6136 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_shiftOp6138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6159 = new BitSet(new long[]{0x0000000000000002L,0x0000002000020000L});
    public static final BitSet FOLLOW_set_in_additiveExpression6186 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6236 = new BitSet(new long[]{0x0000000000000002L,0x0000002000020000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6274 = new BitSet(new long[]{0x0000000000000002L,0x0000000140008000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression6301 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6369 = new BitSet(new long[]{0x0000000000000002L,0x0000000140008000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression6402 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression6415 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression6427 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression6439 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6471 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_unaryExpressionNotPlusMinus6483 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6505 = new BitSet(new long[]{0x0000000080000002L,0x0000008000080004L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6516 = new BitSet(new long[]{0x0000000080000002L,0x0000008000080004L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6586 = new BitSet(new long[]{0x0800400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression6588 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6590 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression6592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression6602 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_castExpression6604 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression6606 = new BitSet(new long[]{0x2840C80300614200L,0x000A911020001870L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary6630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary6652 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
    public static final BitSet FOLLOW_DOT_in_primary6663 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6665 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6708 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
    public static final BitSet FOLLOW_DOT_in_primary6719 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary6721 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000044L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary6764 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_superSuffix_in_primary6774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary6784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary6794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6804 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_primary6815 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary6817 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DOT_in_primary6838 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_primary6840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary6850 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DOT_in_primary6852 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_primary6854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix6890 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix6893 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix6914 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_superSuffix6925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix6958 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix6960 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6981 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix6983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix6994 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix6996 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix6998 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7029 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix7031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_identifierSuffix7043 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_identifierSuffix7045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7057 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix7059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix7069 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_SUPER_in_identifierSuffix7071 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix7073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix7083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7105 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector7107 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_selector7118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7139 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_THIS_in_selector7141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector7151 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_SUPER_in_selector7153 = new BitSet(new long[]{0x0000000080000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_superSuffix_in_selector7163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerCreator_in_selector7173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector7183 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_selector7185 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector7187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator7209 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7211 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator7223 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator7225 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator7227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayCreator_in_creator7237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7257 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7269 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7282 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7284 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreator7305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_arrayCreator7316 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_createdName_in_arrayCreator7318 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7328 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7330 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7340 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7354 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_arrayCreator7356 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7370 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_arrayCreator7392 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_arrayCreator7394 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer7425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer7435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_arrayInitializer7455 = new BitSet(new long[]{0x2840C80302614200L,0x000A91B0210A1872L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7471 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7490 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1872L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer7492 = new BitSet(new long[]{0x0000000002000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer7542 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_RBRACE_in_arrayInitializer7555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName7589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName7599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_innerCreator7620 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_NEW_in_innerCreator7622 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator7633 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_innerCreator7654 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000C0L});
    public static final BitSet FOLLOW_typeArguments_in_innerCreator7665 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator7686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest7707 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest7718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_nonWildcardTypeArguments7750 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments7752 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_GT_in_nonWildcardTypeArguments7762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments7782 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0280A1870L});
    public static final BitSet FOLLOW_expressionList_in_arguments7785 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments7798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classHeader7922 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_CLASS_in_classHeader7924 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classHeader7926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_enumHeader7946 = new BitSet(new long[]{0x0040002000000000L});
    public static final BitSet FOLLOW_set_in_enumHeader7948 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_enumHeader7954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceHeader7974 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_interfaceHeader7976 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_interfaceHeader7978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationHeader7998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_annotationHeader8000 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationHeader8002 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_annotationHeader8004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_typeHeader8024 = new BitSet(new long[]{0x1000002000800000L,0x0000000000000200L});
    public static final BitSet FOLLOW_CLASS_in_typeHeader8027 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ENUM_in_typeHeader8029 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_MONKEYS_AT_in_typeHeader8032 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_typeHeader8036 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_typeHeader8040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_methodHeader8060 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000090L});
    public static final BitSet FOLLOW_typeParameters_in_methodHeader8062 = new BitSet(new long[]{0x0840400100214000L,0x0008000020000010L});
    public static final BitSet FOLLOW_type_in_methodHeader8066 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_VOID_in_methodHeader8068 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodHeader8072 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_methodHeader8074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_fieldHeader8094 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_fieldHeader8096 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldHeader8098 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
    public static final BitSet FOLLOW_LBRACKET_in_fieldHeader8101 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_fieldHeader8102 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
    public static final BitSet FOLLOW_set_in_fieldHeader8106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableHeader8132 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_localVariableHeader8134 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_localVariableHeader8136 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
    public static final BitSet FOLLOW_LBRACKET_in_localVariableHeader8139 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_localVariableHeader8140 = new BitSet(new long[]{0x0000004002000000L,0x0000000010000004L});
    public static final BitSet FOLLOW_set_in_localVariableHeader8144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred2_GraphlrJava120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred2_GraphlrJava149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred12_GraphlrJava506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred27_GraphlrJava743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred43_GraphlrJava1429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_synpred52_GraphlrJava1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodDeclaration_in_synpred53_GraphlrJava1770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred54_GraphlrJava1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred57_GraphlrJava1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_synpred59_GraphlrJava1830 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_typeParameters_in_synpred59_GraphlrJava1841 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred59_GraphlrJava1864 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_formalParameters_in_synpred59_GraphlrJava1880 = new BitSet(new long[]{0x0000000000000000L,0x0000400000000002L});
    public static final BitSet FOLLOW_THROWS_in_synpred59_GraphlrJava1891 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_synpred59_GraphlrJava1893 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_synpred59_GraphlrJava1914 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1EF2L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred59_GraphlrJava1926 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_blockStatement_in_synpred59_GraphlrJava1948 = new BitSet(new long[]{0x38C1D82350E1C310L,0x003FB7BC357A1E72L});
    public static final BitSet FOLLOW_RBRACE_in_synpred59_GraphlrJava1969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred68_GraphlrJava2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaration_in_synpred69_GraphlrJava2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_synpred70_GraphlrJava2372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_synpred71_GraphlrJava2382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ellipsisParameterDecl_in_synpred96_GraphlrJava3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_GraphlrJava3156 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_COMMA_in_synpred98_GraphlrJava3167 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred98_GraphlrJava3169 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_normalParameterDecl_in_synpred99_GraphlrJava3191 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_COMMA_in_synpred99_GraphlrJava3201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred103_GraphlrJava3336 = new BitSet(new long[]{0x0000000000000000L,0x0000110000000000L});
    public static final BitSet FOLLOW_set_in_synpred103_GraphlrJava3362 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_synpred103_GraphlrJava3394 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred103_GraphlrJava3396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodDeclaration_in_synpred117_GraphlrJava3995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceFieldDeclaration_in_synpred118_GraphlrJava4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_synpred119_GraphlrJava4015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_synpred120_GraphlrJava4025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_synpred121_GraphlrJava4035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_synpred122_GraphlrJava4045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred125_GraphlrJava4203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred126_GraphlrJava4213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred130_GraphlrJava4354 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred130_GraphlrJava4374 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_synpred130_GraphlrJava4377 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred130_GraphlrJava4379 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred130_GraphlrJava4383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_synpred132_GraphlrJava4393 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred132_GraphlrJava4396 = new BitSet(new long[]{0x0000000001000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_synpred132_GraphlrJava4399 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred132_GraphlrJava4401 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred132_GraphlrJava4405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred133_GraphlrJava4434 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_synpred133_GraphlrJava4436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_synpred148_GraphlrJava4658 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_synpred148_GraphlrJava4661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred149_GraphlrJava4676 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_synpred149_GraphlrJava4678 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_synpred149_GraphlrJava4680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred153_GraphlrJava4836 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_FINALLY_in_synpred153_GraphlrJava4838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_synpred153_GraphlrJava4840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred154_GraphlrJava4854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_synpred157_GraphlrJava5046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LPAREN_in_synpred157_GraphlrJava5048 = new BitSet(new long[]{0x0840500100214000L,0x0000000020000210L});
    public static final BitSet FOLLOW_variableModifiers_in_synpred157_GraphlrJava5050 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_type_in_synpred157_GraphlrJava5052 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred157_GraphlrJava5054 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_COLON_in_synpred157_GraphlrJava5056 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred157_GraphlrJava5067 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred157_GraphlrJava5069 = new BitSet(new long[]{0x28C1C8035061C300L,0x002EB7B0340A1872L});
    public static final BitSet FOLLOW_statement_in_synpred157_GraphlrJava5071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred161_GraphlrJava5250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred202_GraphlrJava6495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_synpred206_GraphlrJava6586 = new BitSet(new long[]{0x0800400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred206_GraphlrJava6588 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_synpred206_GraphlrJava6590 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred206_GraphlrJava6592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred208_GraphlrJava6663 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred208_GraphlrJava6665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred209_GraphlrJava6687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred211_GraphlrJava6719 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_synpred211_GraphlrJava6721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred212_GraphlrJava6743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred224_GraphlrJava6994 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred224_GraphlrJava6996 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred224_GraphlrJava6998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred236_GraphlrJava7207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred236_GraphlrJava7209 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_synpred236_GraphlrJava7211 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_classCreatorRest_in_synpred236_GraphlrJava7213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred237_GraphlrJava7223 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_synpred237_GraphlrJava7225 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_classCreatorRest_in_synpred237_GraphlrJava7227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_synpred239_GraphlrJava7257 = new BitSet(new long[]{0x0840400100214000L,0x0000000020000010L});
    public static final BitSet FOLLOW_createdName_in_synpred239_GraphlrJava7259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred239_GraphlrJava7269 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred239_GraphlrJava7271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred239_GraphlrJava7282 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred239_GraphlrJava7284 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000006L});
    public static final BitSet FOLLOW_arrayInitializer_in_synpred239_GraphlrJava7305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_synpred240_GraphlrJava7354 = new BitSet(new long[]{0x2840C80300614200L,0x000A91B0200A1870L});
    public static final BitSet FOLLOW_expression_in_synpred240_GraphlrJava7356 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_RBRACKET_in_synpred240_GraphlrJava7370 = new BitSet(new long[]{0x0000000000000002L});

}