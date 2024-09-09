package frontend;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorHandler extends BaseErrorListener
{
    private static int count = 0;
    private static boolean first = true;
    
    public int getCount() { return count; };
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException ex)
    {
        if (first)
        {
            System.out.println("\n\n===== SYNTAX ERRORS =====\n");
            System.out.printf("%-4s %-35s\n", "Line", "Message");
            System.out.printf("%-4s %-35s\n", "----", "-------");
            
            first = false;
        }
        
        count++;
        System.out.printf("%03d  %-35s\n", line, msg);
    }
}
