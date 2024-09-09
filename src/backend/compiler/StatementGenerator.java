package backend.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import antlr4.AKAParser;
import antlr4.AKAParser.DefCallContext;
import antlr4.AKAParser.VariableContext;
import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>StatementGenerator</h1>
 *
 * <p>Emit code for executable statements.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param parent the parent generator.
     * @param compiler the compiler to use.
     */
    public StatementGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }

    /**
     * Emit code for an assignment statement.
     * @param ctx the AssignmentStatementContext.
     */
    public void emitAssignment(AKAParser.AssignmentContext ctx)
    {
        AKAParser.VariableContext   varCtx  = ctx.lhs().variable();
        AKAParser.ExpressionContext exprCtx = ctx.rhs().declaration().expression();
        SymtabEntry varId = varCtx.entry;
        Typespec varType  = varCtx.type;
        Typespec exprType = exprCtx.type;
        compiler.visit(exprCtx);
        emitStoreValue(varId,varType);
        
        

        // The last modifier, if any, is the variable's last subscript or field.
//        int modifierCount = varCtx.modifier().size();
//        AKAParser.ModifierContext lastModCtx = modifierCount == 0
//                            ? null : varCtx.modifier().get(modifierCount - 1);

        // The target variable has subscripts and/or fields.
//        if (modifierCount > 0) 
//        {
//            lastModCtx = varCtx.modifier().get(modifierCount - 1);
//            compiler.visit(varCtx);
//        }
        
        // Emit code to evaluate the expression.
//        compiler.visit(exprCtx);
        
        
        // Emit code to store the expression value into the target variable.
        // The target variable has no subscripts or fields.
//        if (lastModCtx == null) emitStoreValue(varId, varId.getType());
//
//        // The target variable is a field.
//        else if (lastModCtx.field() != null)
//        {
//            emitStoreValue(lastModCtx.field().entry, lastModCtx.field().type);
//        }
//
//        // The target variable is an array element.
//        else
//        {
//            emitStoreValue(null, varType);
//        }
//        emitStoreValue(varId,varType);
    }

    /**
     * Emit code for an IF statement.
     * @param ctx the IfStatementContext.
     */
    public void emitIf(AKAParser.IfStatementContext ctx)
    {
        /***** Complete this method. *****/
    	Label next_label = new Label();
		compiler.visit(ctx.ifBlock().condition().expression());
		if (ctx.elseBlock() == null && ctx.elseifBlock().size() == 0) {// No else
			emit(IFEQ, next_label); 
			compiler.visit(ctx.ifBlock().statementList());
		} else if (ctx.elseifBlock().size() == 0){// Has else
			Label false_label = new Label(); 
			emit(IFEQ, false_label); 
			compiler.visit(ctx.ifBlock().statementList()); 
			emit(GOTO, next_label);
			emitLabel(false_label);
			compiler.visit(ctx.elseBlock().statementList());
		} else if (ctx.elseBlock() == null && ctx.elseifBlock().size() > 0) {//No Else
			Label elseif_label = new Label();
			emit(IFEQ, elseif_label);
			compiler.visit(ctx.ifBlock().statementList());
			emit(GOTO, next_label);
			
			
			for (AKAParser.ElseifBlockContext elseifCtx : ctx.elseifBlock()) {
				emitLabel(elseif_label);
				compiler.visit(elseifCtx.condition().expression());
				elseif_label = new Label();
				emit(IFEQ, elseif_label);
				compiler.visit(elseifCtx.statementList());
				emit(GOTO, next_label);
			}
			emitLabel(elseif_label);
		} else {		// Has elseif and else
			Label elseif_label = new Label();
			emit(IFEQ, elseif_label);
			compiler.visit(ctx.ifBlock().statementList());
			emit(GOTO, next_label);
			
			
			for (AKAParser.ElseifBlockContext elseifCtx : ctx.elseifBlock()) {
				emitLabel(elseif_label);
				compiler.visit(elseifCtx.condition().expression());
				elseif_label = new Label();
				emit(IFEQ, elseif_label);
				compiler.visit(elseifCtx.statementList());
				emit(GOTO, next_label);
			}
			emitLabel(elseif_label);
			compiler.visit(ctx.elseBlock().statementList());
		}
		
		
		
		
		
		
		
		
		emitLabel(next_label); // next-label
    }
    
    public void emitGuard(AKAParser.GuardContext ctx)
    {
        Label guardStart = new Label();
        Label guardEnd = new Label();
        
        List<AKAParser.ConditionContext> conditions = ctx.paramList().condition();
        for (AKAParser.ConditionContext condition : conditions) {
            compiler.visit(condition.expression());
            emit(IFEQ, guardEnd);
        }

        emitLabel(guardStart);
        for (AKAParser.StatementContext statement : ctx.statementList().statement()) {
            for (AKAParser.ConditionContext condition : conditions) {
                compiler.visit(condition.expression());
                emit(IFEQ, guardEnd);
            }
            compiler.visit(statement);
            
        }
        emitLabel(guardEnd);
    }
    
    /**
     * Emit code for a WHILE statement.
     * @param ctx the WhileStatementContext.
     */
    public void emitWhile(AKAParser.WhileStatementContext ctx)
    {
        /***** Complete this method. *****/
         
         Label loopTopLabel  = new Label();
         Label loopExitLabel = new Label();
         
         emitLabel(loopTopLabel);
         
         compiler.visit(ctx.condition().expression());

         emit(IFEQ, loopExitLabel); 
         
         compiler.visit(ctx.statementList());
         
         emit(GOTO, loopTopLabel);
                    
         emitLabel(loopExitLabel);   
    }
    
    /**
     * Emit code for a procedure call statement.
     * @param ctx the ProcedureCallStatementContext.
     */
    public void emitProcedureCall(AKAParser.DefCallContext ctx)
    {
        /***** Complete this method. *****/
    	SymtabEntry procedureEntry = ctx.defName().entry;
        
    	emitCall(procedureEntry, ctx.argumentList());
    }
    
    /**
     * Emit code for a function call statement.
     * @param ctx the FunctionCallContext.
     */
    public void emitFunctionCall(AKAParser.DefCallContext ctx)
    {
        /***** Complete this method. *****/
    	SymtabEntry functionEntry = ctx.defName().entry;
        
    	emitCall(functionEntry, ctx.argumentList());
    }
    
    /**
     * Emit a call to a procedure or a function.
     * @param routineId the routine name's symbol table entry.
     * @param argListCtx the ArgumentListContext.
     */
    private void emitCall(SymtabEntry routineId,
                          AKAParser.ArgumentListContext argListCtx)
    {
        /***** Complete this method. *****/
        String routineCall = programName + "/" + routineId.getName() + "(";
        //the argument list may be null
        if(argListCtx != null)
        {
            ArrayList<SymtabEntry> routineParameters = routineId.getRoutineParameters();
            
            //visits all of the arguments to the routine
            for(int i = 0; i < argListCtx.argument().size(); i++)
            {
                AKAParser.ExpressionContext expCtx = argListCtx.argument(i).expression();
                compiler.visit(expCtx);
                
                Typespec expType = expCtx.type;
                
                routineCall += typeDescriptor(expType);
            }
        }
        
        routineCall += ")";
        // adds return type to string
        routineCall += typeDescriptor(routineId);
        emit(INVOKESTATIC, routineCall);
    }   

    /**
     * Emit code for a WRITE statement.
     * @param ctx the WriteStatementContext.
     */
    public void emitWrite(AKAParser.DisplayContext ctx)
    {
        emitWrite(ctx.expression(), true);
    }

    /**
     * Emit code for a WRITELN statement.
     * @param ctx the WritelnStatementContext.
     */
//    public void emitWriteln(AKAParser.WritelnStatementContext ctx)
//    {
//        emitWrite(ctx.writeArguments(), true);
//    }

    /**
     * Emit code for a call to WRITE or WRITELN.
     * @param argsCtx the WriteArgumentsContext.
     * @param needLF true if need a line feed.
     */
    private void emitWrite(AKAParser.ExpressionContext expressionCtx,
                           boolean needLF)
    {
        emit(GETSTATIC, "java/lang/System/out", "Ljava/io/PrintStream;");

        // WRITELN with no arguments.
        if (expressionCtx == null) 
        {
            emit(INVOKEVIRTUAL, "java/io/PrintStream.println()V");
            localStack.decrease(1);
        }
            
        // Generate code for the arguments.
        else
        {
            StringBuffer format = new StringBuffer();
            int exprCount = createWriteFormat(expressionCtx, format, needLF);
            
            // Load the format string.
            emit(LDC, format.toString());
            
            // Emit the arguments array.
            if (exprCount > 0)
            {
                emitArgumentsArray(expressionCtx, exprCount);

                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)" +
                     "Ljava/io/PrintStream;");
                localStack.decrease(2);
                emit(POP);
            }
            else
            {
                emit(INVOKEVIRTUAL,
                     "java/io/PrintStream/print(Ljava/lang/String;)V");
                localStack.decrease(2);
            }
        }
    }
    
    /**
     * Create the printf format string.
     * @param argsCtx the WriteArgumentsContext.
     * @param format the format string to create.
     * @return the count of expression arguments.
     */
    private int createWriteFormat(AKAParser.ExpressionContext expCtx,
                                  StringBuffer format, boolean needLF)
    {
        int exprCount = 0;
        format.append("\"");
        
        
        Typespec type = expCtx.type;
        String argText = expCtx.getText();
        
        // Append any literal strings.
        if (argText.charAt(0) == '\'') 
        {
//            format.append(convertString(argText));
        }
        
        // For any other expressions, append a field specifier.
        else
        {
            exprCount++;
            format.append("%");
            
//            AKAParser.FieldWidthContext fwCtx = expCtx.fieldWidth();              
//            if (fwCtx != null)
//            {
//                String sign = (   (fwCtx.sign() != null) 
//                               && (fwCtx.sign().getText().equals("-"))) 
//                            ? "-" : "";
//                format.append(sign)
//                      .append(fwCtx.integerConstant().getText());
//                
//                AKAParser.DecimalPlacesContext dpCtx = 
//                                                    fwCtx.decimalPlaces();
//                if (dpCtx != null)
//                {
//                    format.append(".")
//                          .append(dpCtx.integerConstant().getText());
//                }
//            }
            
            String typeFlag = type == Predefined.numberType    ? "f" 
                            : type == Predefined.booleanType ? "b"  
                            :                                  "s";
            format.append(typeFlag);
        }
        
        // Loop over the write arguments.
//        for (AKAParser.WriteArgumentContext argCtx : argsCtx.writeArgument())
//        {
//            Typespec type = argCtx.expression().type;
//            String argText = argCtx.getText();
//            
//            // Append any literal strings.
//            if (argText.charAt(0) == '\'') 
//            {
//                format.append(convertString(argText));
//            }
//            
//            // For any other expressions, append a field specifier.
//            else
//            {
//                exprCount++;
//                format.append("%");
//                
//                AKAParser.FieldWidthContext fwCtx = argCtx.fieldWidth();              
//                if (fwCtx != null)
//                {
//                    String sign = (   (fwCtx.sign() != null) 
//                                   && (fwCtx.sign().getText().equals("-"))) 
//                                ? "-" : "";
//                    format.append(sign)
//                          .append(fwCtx.integerConstant().getText());
//                    
//                    AKAParser.DecimalPlacesContext dpCtx = 
//                                                        fwCtx.decimalPlaces();
//                    if (dpCtx != null)
//                    {
//                        format.append(".")
//                              .append(dpCtx.integerConstant().getText());
//                    }
//                }
//                
//                String typeFlag = type == Predefined.integerType ? "d" 
//                                : type == Predefined.numberType    ? "f" 
//                                : type == Predefined.booleanType ? "b" 
//                                : type == Predefined.charType    ? "c" 
//                                :                                  "s";
//                format.append(typeFlag);
//            }
//        }
        
        format.append(needLF ? "\\n\"" : "\"");
 
        return exprCount;
    }
    
    /**
     * Emit the printf arguments array.
     * @param argsCtx
     * @param exprCount
     */
    private void emitArgumentsArray(AKAParser.ExpressionContext expCtx,
                                    int exprCount)
    {
        // Create the arguments array.
        emitLoadConstant(exprCount);
        emit(ANEWARRAY, "java/lang/Object");

        int index = 0;
    
        String argText = expCtx.getText();
        AKAParser.ExpressionContext exprCtx = expCtx;
        Typespec type = exprCtx.type.baseType();
        
        // Skip string constants, which were made part of
        // the format string.
        if (argText.charAt(0) != '\'') 
        {
            emit(DUP);
            emitLoadConstant(index++);

            compiler.visit(exprCtx);

            Form form = type.getForm();
            if (    ((form == SCALAR) || (form == ENUMERATION))
                 && (type != Predefined.stringType))
            {
                emit(INVOKESTATIC, valueOfSignature(type));
            }

            // Store the value into the array.
            emit(AASTORE);
        }

        // Loop over the write arguments to fill the arguments array.
//        for (AKAParser.WriteArgumentContext argCtx : 
//                                                    argsCtx.writeArgument())
//        {
//            String argText = argCtx.getText();
//            AKAParser.ExpressionContext exprCtx = argCtx.expression();
//            Typespec type = exprCtx.type.baseType();
//            
//            // Skip string constants, which were made part of
//            // the format string.
//            if (argText.charAt(0) != '\'') 
//            {
//                emit(DUP);
//                emitLoadConstant(index++);
//
//                compiler.visit(exprCtx);
//
//                Form form = type.getForm();
//                if (    ((form == SCALAR) || (form == ENUMERATION))
//                     && (type != Predefined.stringType))
//                {
//                    emit(INVOKESTATIC, valueOfSignature(type));
//                }
//
//                // Store the value into the array.
//                emit(AASTORE);
//            }
//        }
    }

    /**
     * Emit code for a READ statement.
     * @param ctx the ReadStatementContext.
     */
//    public void emitRead(AKAParser.ReadStatementContext ctx)
//    {
//        emitRead(ctx.readArguments(), false);
//    }
}
