package frontend;

import org.antlr.v4.runtime.ParserRuleContext;

public class SemanticErrorHandler
{
    public enum Code
    {
        UNDECLARED_IDENTIFIER      ("Undeclared identifier"),
        REDECLARED_IDENTIFIER      ("Redeclared identifier"),
        INVALID_CONSTANT           ("Invalid constant"),
        INVALID_SIGN               ("Invalid sign"),
        INVALID_TYPE               ("Invalid type"),
        INVALID_VARIABLE           ("Invalid variable"),
        TYPE_MISMATCH              ("Mismatched datatype"),
        TYPE_MUST_BE_INTEGER       ("Datatype must be integer"),
        TYPE_MUST_BE_NUMERIC       ("Datatype must be integer or real"),
        TYPE_MUST_BE_BOOLEAN       ("Datatype must be boolean"),
        TYPE_MUST_BE_BOOLEAN_OR_NUMERIC ("Datatype must be boolean or number"),
        INCOMPATIBLE_ASSIGNMENT    ("Incompatible assignment"),
        INCOMPATIBLE_COMPARISON    ("Incompatible comparison"),
        RETURN_VARIABLE_UNINITIALIZED ("Return variable never initialized"),
//        DUPLICATE_CASE_CONSTANT    ("Duplicate CASE constant"),
//        INVALID_CONTROL_VARIABLE   ("Invalid control variable datatype"),
        NAME_MUST_BE_DEFINITION     ("Must be a definition name"),
        NAME_MUST_BE_DEFINITIONNORETURN      ("Must be a definitionnoreturn name"),
        ARGUMENT_COUNT_MISMATCH    ("Invalid number of arguments"),
        ARGUMENT_MUST_BE_VARIABLE  ("Argument must be a variable"),
//        INVALID_REFERENCE_PARAMETER("Reference parameter cannot be scalar"),
        INVALID_RETURN_TYPE        ("Invalid function return type");
//        TOO_MANY_SUBSCRIPTS        ("Too many subscripts"),
//        INVALID_FIELD              ("Invalid field");
        
        private String message;
        
        Code(String message) { this.message = message; }
    }
    
    private int count = 0;
    
    /**
     * Get the count of semantic errors.
     * @return the count.
     */
    public int getCount() { return count; };
    
    /**
     * Flag a semantic error.
     * @param code the error code.
     * @param lineNumber the line number of the offending line.
     * @param text the text near the error.
     */
    public void flag(Code code, int lineNumber, String text)
    {
        if (count == 0)
        {
            System.out.println("\n===== SEMANTIC ERRORS =====\n");
            System.out.printf("%-4s %-40s %s\n", "Line", "Message", "Found near");
            System.out.printf("%-4s %-40s %s\n", "----", "-------", "----------");
        }
        
        count++;
        
        System.out.printf("%03d  %-40s \"%s\"\n", 
                          lineNumber, code.message, text);
    }
    
    /**
     * Flag a semantic error.
     * @param code the error code.
     * @param the context containing the error.
     */
    public void flag(Code code, ParserRuleContext ctx)
    {
        flag(code, ctx.getStart().getLine(), ctx.getText());
    }
}
