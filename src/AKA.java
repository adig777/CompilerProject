import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;

import antlr4.*;

import frontend.*;
import intermediate.symtab.*;
import intermediate.util.BackendMode;
//import backend.converter.Converter;
//import backend.interpreter.Executor;
import backend.compiler.Compiler;

import static intermediate.util.BackendMode.*;

public class AKA 
{
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 2)
        {
            System.out.println("USAGE: PascalJava option sourceFileName");
            System.out.println("   option: -convert, -execute, or -compile");
            return;
        }
        
        String option = args[0];
        String sourceFileName = args[1];

        BackendMode mode = EXECUTOR;
        
        if      (option.equalsIgnoreCase("-convert")) mode = CONVERTER;
        else if (option.equalsIgnoreCase("-execute")) mode = EXECUTOR;
        else if (option.equalsIgnoreCase("-compile")) mode = COMPILER;
        else
        {
            System.out.println("ERROR: Invalid option.");
            System.out.println("   Valid options: -convert, -execute, or -compile");
        }
        
        // Generate a source file listing.
        new Listing(sourceFileName);
        
        // Create the input stream.
        InputStream source = new FileInputStream(sourceFileName);
        
        // Create the character stream from the input stream.
        CharStream cs = CharStreams.fromStream(source);
        
        // Custom syntax error handler.
        SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler();
        
        // Create a lexer which scans the character stream
        // to create a token stream.
        AKALexer lexer = new AKALexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorHandler);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Create a parser which parses the token stream.
        AKAParser parser = new AKAParser(tokens);
        
        // Pass 1: Check syntax and create the parse tree.
        System.out.printf("\nPASS 1 Syntax: ");
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorHandler);
        ParseTree tree = parser.program();
        
        int errorCount = syntaxErrorHandler.getCount();
        if (errorCount > 0) 
        {
            System.out.printf("\nThere were %d syntax errors.\n", errorCount);
            System.out.println("Object file not created or modified.");
            return;
        }
        else
        {
            System.out.println("There were no syntax errors.");
        }
        
        // Pass 2: Semantic operations.
        System.out.printf("\nPASS 2 Semantics:\n");
        Semantics pass2 = new Semantics(mode);
        pass2.visit(tree);
        
        errorCount = pass2.getErrorCount();
        if (errorCount > 0)
        {
            System.out.printf("\nThere were %d semantic errors.\n", errorCount);
            System.out.println("Object file not created or modified.");
            return;
        }

        // Pass 3: Translation.
        switch (mode)
        {
            case EXECUTOR:
            {
//                // Pass 3: Execute the Pascal program.
//                System.out.printf("\nPASS 3 Execution:\n\n");
//                SymtabEntry programId = pass2.getProgramId();
//                Executor pass3 = new Executor(programId);
//                pass3.visit(tree);
//                break;
            }
            
            case CONVERTER:
            {
//                // Convert from Pascal to Java.
//                System.out.printf("\nPASS 3 Convert to Java: ");
//                Converter pass3 = new Converter();
//                pass3.visit(tree);
//                
//                System.out.printf("Object file \"%s\" created.\n",
//                                  pass3.getObjectFileName());
//                break;
            }
                
            case COMPILER:
            {
                // Pass 3: Compile the Pascal program.
                System.out.printf("\nPASS 3 Compilation: ");
                SymtabEntry programId = pass2.getProgramId();
                Compiler pass3 = new Compiler(programId);
                pass3.visit(tree);
                
                System.out.printf("Object file \"%s\" created.\n",
                                  pass3.getObjectFileName());
                break;
            }
        }
    }
}
