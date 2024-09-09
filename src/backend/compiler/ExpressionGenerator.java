package backend.compiler;

import antlr4.AKAParser;

import intermediate.symtab.*;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

/**
 * <h1>ExpressionGenerator</h1>
 *
 * <p>Generate code for an expression.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ExpressionGenerator extends CodeGenerator
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public ExpressionGenerator(CodeGenerator parent, Compiler compiler)
    {
        super(parent, compiler);
    }
    
    /**
     * Emit code for an expression.
     * @param ctx the ExpressionContext.
     */
    public void emitExpression(AKAParser.ExpressionContext ctx)
    {
        AKAParser.SimpleExpressionContext simpleCtx1 = 
                                                ctx.simpleExpression().get(0);
        AKAParser.RelOperatorContext relOpCtx = ctx.relOperator();
        Typespec type1 = simpleCtx1.type;
        emitSimpleExpression(simpleCtx1);
        
        // More than one simple expression?
        if (relOpCtx != null)
        {
            String op = relOpCtx.getText();
            AKAParser.SimpleExpressionContext simpleCtx2 = 
                                                ctx.simpleExpression().get(1);
            Typespec type2 = simpleCtx2.type;


            boolean numberMode      = false;
            boolean booleanMode		= false;


            if (   (type1 == Predefined.numberType)
                && (type2 == Predefined.numberType)) 
            {
                numberMode = true;
            }
            

            Label trueLabel = new Label();
  
            Label exitLabel = new Label();

            if (numberMode)
            {
                emitSimpleExpression(simpleCtx2);
                
                emit(FCMPG);

                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("<>")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }
            else  // stringMode
            {
                emitSimpleExpression(simpleCtx2);
                emit(INVOKEVIRTUAL,
                     "java/lang/String.compareTo(Ljava/lang/String;)I");
                localStack.decrease(1);
                
                if      (op.equals("==" )) emit(IFEQ, trueLabel);
                else if (op.equals("<>")) emit(IFNE, trueLabel);
                else if (op.equals("<" )) emit(IFLT, trueLabel);
                else if (op.equals("<=")) emit(IFLE, trueLabel);
                else if (op.equals(">" )) emit(IFGT, trueLabel);
                else if (op.equals(">=")) emit(IFGE, trueLabel);
            }

            emit(ICONST_0); // false
            emit(GOTO, exitLabel);
            emitLabel(trueLabel);
            emit(ICONST_1); // true
            emitLabel(exitLabel);
            
            localStack.decrease(1);  // only one branch will be taken
        }
    }
    
    /**
     * Emit code for a simple expression.
     * @param ctx the SimpleExpressionContext.
     */
    public void emitSimpleExpression(AKAParser.SimpleExpressionContext ctx)
    {
        int count = ctx.term().size();
        Boolean negate =    (ctx.sign() != null) 
                         && ctx.sign().getText().equals("-");
        
        // First term.
        AKAParser.TermContext termCtx1 = ctx.term().get(0);
        Typespec type1 = termCtx1.type;
        emitTerm(termCtx1);
        
        if (negate) emit(FNEG);
        
        // Loop over the subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOperator().get(i-1).getText().toLowerCase();
            AKAParser.TermContext termCtx2 = ctx.term().get(i);
            Typespec type2 = termCtx2.type;

            boolean numberMode    = false;
            boolean booleanMode = false;
            if (   (type1 == Predefined.numberType)
                && (type2 == Predefined.numberType)) 
            {
                numberMode = true;
            }
            else if (   ((type1 == Predefined.booleanType) && (type2 == Predefined.booleanType))
                     || ((type1 == Predefined.booleanType) && (type2 == Predefined.numberType))
                     || ((type1 == Predefined.numberType) && (type2 == Predefined.booleanType))
            		)
            {
                booleanMode = true;
            }
                            
            if (numberMode)
            {
                emitTerm(termCtx2);
                
                if (op.equals("+")) emit(FADD);
                else                emit(FSUB);
            }
            else if (booleanMode && ((type1 == Predefined.booleanType) && (type2 == Predefined.booleanType)))
            {
                emitTerm(termCtx2);
                emit(IOR);

            }
            else if (booleanMode && ((type1 == Predefined.booleanType) && (type2 == Predefined.numberType)))
            {
            	Label falseLabel = new Label();
            	Label skipLabel = new Label();
            	emitTerm(termCtx2);
            	emit(F2I);
            	emit(IFEQ, falseLabel);
            	emitLoadConstant(1);
            	emit(GOTO, skipLabel);
            	emitLabel(falseLabel);
            	emitLoadConstant(0);
            	emitLabel(skipLabel);
            	emit(IOR);
            	localStack.decrease(1);
            }
            else if (booleanMode && ((type1 == Predefined.numberType) && (type2 == Predefined.booleanType)))
            {
            	Label falseLabel = new Label();
            	Label skipLabel = new Label();
            	emit(F2I);
            	emit(IFEQ, falseLabel);
            	emitLoadConstant(1);
            	emit(GOTO, skipLabel);
            	emitLabel(falseLabel);
            	emitLoadConstant(0);
            	emitLabel(skipLabel);
            	emitTerm(termCtx2);
            	emit(IOR);
            	localStack.decrease(1);
            }
            else if ((type1 == Predefined.stringType) && (type2 == Predefined.numberType)) {
            	emit(NEW, "java/lang/StringBuilder");
                emit(DUP_X1);             
                emit(SWAP);                  
                emit(INVOKESTATIC, "java/lang/String/valueOf(Ljava/lang/Object;)" +
                                   "Ljava/lang/String;");
                emit(INVOKESPECIAL, "java/lang/StringBuilder/<init>" +
                                    "(Ljava/lang/String;)V");
                localStack.decrease(1);
                
                emitTerm(termCtx2);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/append(F)" +
                        "Ljava/lang/StringBuilder;");
                localStack.decrease(1);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/toString()" +
                        "Ljava/lang/String;");
                localStack.decrease(1);
            }
            else if ((type1 == Predefined.numberType) && (type2 == Predefined.stringType)) {
            	emit(NEW, "java/lang/StringBuilder");
                emit(DUP_X1);             
                emit(SWAP);
                emit(INVOKESTATIC, "java/lang/String/valueOf(F)" +
                        "Ljava/lang/String;");
                emit(INVOKESPECIAL, "java/lang/StringBuilder/<init>" +
                         "(Ljava/lang/String;)V");
                localStack.decrease(1);
                emitTerm(termCtx2);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/append(Ljava/lang/String;)" +
                        "Ljava/lang/StringBuilder;");
                localStack.decrease(1);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/toString()" +
                        "Ljava/lang/String;");
                localStack.decrease(1);
                type1 = Predefined.stringType;
            }
            else  // stringMode
            {
                emit(NEW, "java/lang/StringBuilder");
                emit(DUP_X1);             
                emit(SWAP);                  
                emit(INVOKESTATIC, "java/lang/String/valueOf(Ljava/lang/Object;)" +
                                   "Ljava/lang/String;");
                emit(INVOKESPECIAL, "java/lang/StringBuilder/<init>" +
                                    "(Ljava/lang/String;)V");
                localStack.decrease(1);
                
                emitTerm(termCtx2);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/append(Ljava/lang/String;)" +
                                    "Ljava/lang/StringBuilder;");
                localStack.decrease(1);
                emit(INVOKEVIRTUAL, "java/lang/StringBuilder/toString()" +
                                    "Ljava/lang/String;");
                localStack.decrease(1);
            }
        }
    }
    
    /**
     * Emit code for a term.
     * @param ctx the TermContext.
     */
    public void emitTerm(AKAParser.TermContext ctx)
    {
        int count = ctx.factor().size();
        
        // First factor.
        AKAParser.FactorContext factorCtx1 = ctx.factor().get(0);
        Typespec type1 = factorCtx1.type;
        compiler.visit(factorCtx1);
        
        // Loop over the subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOperator().get(i-1).getText().toLowerCase();
            AKAParser.FactorContext factorCtx2 = ctx.factor().get(i);
            Typespec type2 = factorCtx2.type;

            boolean numberMode    = false;

            if (   (type1 == Predefined.numberType)
                && (type2 == Predefined.numberType)) 
            {
                numberMode = true;
            }
            
                
            if (numberMode)
            {
                compiler.visit(factorCtx2); 
                
                if      (op.equals("*")) emit(FMUL);
                else if (op.equals("/")) emit(FDIV);
            }
            else if ((type1 == Predefined.numberType) && (type2 == Predefined.booleanType)) 
            {
            	Label falseLabel = new Label();
            	Label skipLabel = new Label();
            	emit(F2I);
            	emit(IFEQ, falseLabel);
            	emitLoadConstant(1);
            	emit(GOTO, skipLabel);
            	emitLabel(falseLabel);
            	emitLoadConstant(0);
            	emitLabel(skipLabel);
            	compiler.visit(factorCtx2);  
            	emit(IAND);
            	localStack.decrease(1);
            }
            else if ((type1 == Predefined.booleanType) && (type2 == Predefined.numberType))
            {
            	Label falseLabel = new Label();
            	Label skipLabel = new Label();
            	compiler.visit(factorCtx2);  
            	emit(F2I);
            	emit(IFEQ, falseLabel);
            	emitLoadConstant(1);
            	emit(GOTO, skipLabel);
            	emitLabel(falseLabel);
            	emitLoadConstant(0);
            	emitLabel(skipLabel);
            	emit(IAND);
            	localStack.decrease(1);
            } 
            else 
            {
            	compiler.visit(factorCtx2);                 
            	emit(IAND);
            }
        }
    }
    
    /**
     * Emit code for NOT.
     * @param ctx the NotFactorContext.
     */
    public void emitNotFactor(AKAParser.NotFactorContext ctx)
    {
        compiler.visit(ctx.factor());
        emit(ICONST_1);
        emit(IXOR);
    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param ctx the VariableContext.
     */
    public void emitLoadValue(AKAParser.VariableContext varCtx)
    {
        // Load the scalar value or structure address.
        Typespec variableType = emitLoadVariable(varCtx);
        
        // Load an array element's or record field's value.
//        int modifierCount = varCtx.modifier().size();
//        if (modifierCount > 0)
//        {
//            PascalParser.ModifierContext lastModCtx =
//                                    varCtx.modifier().get(modifierCount - 1);
//            
//            if (lastModCtx.indexList() != null)
//            {
//                emitLoadArrayElementValue(variableType);
//            }
//            else
//            {
//                emitLoadRecordFieldValue(lastModCtx.field(), variableType);
//            }
//        }
    }

    /**
     * Emit code to load a scalar variable's value 
     * or a structured variable's address.
     * @param variableNode the variable node.
     * @return the datatype of the variable.
     */
    public Typespec emitLoadVariable(AKAParser.VariableContext varCtx)
    {
        SymtabEntry variableId = varCtx.entry;
        Typespec variableType = varCtx.type;
        
        
        
        int nestingLevel = variableId.getSymtab().getNestingLevel();
		int slot = variableId.getSlotNumber();
		
		// Program variable.
		if (nestingLevel == 1) 
		{
		String targetName = variableId.getName();
		String name = programName + "/" + targetName;
		
		emitRangeCheck(variableType);
		emit(GETSTATIC, name, typeDescriptor(variableType.baseType()));
		}
		
		// Local variable.
		else 
		{
		emitRangeCheck(variableType);
		emitLoadLocal(variableType.baseType(), slot);
		}

        // Loop over subscript and field modifiers.
//        for (int i = 0; i < modifierCount; ++i)
//        {
//            PascalParser.ModifierContext modCtx = varCtx.modifier().get(i);
//            boolean lastModifier = i == modifierCount - 1;
//
//            // Subscript
//            if (modCtx.indexList() != null) 
//            {
//                variableType = emitLoadArrayElementAccess(
//                                modCtx.indexList(), variableType, lastModifier);
//            }
//            
//            // Field
//            else if (!lastModifier)
//            {
//                variableType = emitLoadRecordField(modCtx.field(), variableType);
//            }
//        }

        return variableType;
    }

    /**
     * Emit code to access an array element by loading the array address
     * and the subscript value. This can subsequently be followed by code
     * to load the array element's value or to store into the array element. 
     * @param subscriptsNode the SUBSCRIPTS node.
     * @param elmtType the array element type.
     * @param lastModifier true if this is the variable's last modifier.
     * @return the type of the element.
     */
//    private Typespec emitLoadArrayElementAccess(
//                                    PascalParser.IndexListContext indexListCtx,
//                                    Typespec elmtType, boolean lastModifier)
//    {
//        int indexCount = indexListCtx.index().size();
//        
//        // Loop over the subscripts.
//        for (int i = 0; i < indexCount; i++)
//        {
//            PascalParser.IndexContext indexCtx = indexListCtx.index().get(i);
//            emitExpression(indexCtx.expression());
//
//            Typespec indexType = elmtType.getArrayIndexType();
//
//            if (indexType.getForm() == SUBRANGE) 
//            {
//                int min = indexType.getSubrangeMinValue();
//                if (min != 0) 
//                {
//                    emitLoadConstant(min);
//                    emit(ISUB);
//                }
//            }
//
//            if (!lastModifier || (i < indexCount - 1)) emit(AALOAD);
//            elmtType = elmtType.getArrayElementType();
//        }
//
//        return elmtType;
//    }

    /**
     * Emit a load of an array element's value.
     * @param elmtType the element type if character, else null.
     */
//    private void emitLoadArrayElementValue(Typespec elmtType)
//    {
//        Form form = SCALAR;
//
//        if (elmtType != null) 
//        {
//            elmtType = elmtType.baseType();
//            form = elmtType.getForm();
//        }
//
//        // Load a character from a string.
//        if (elmtType == Predefined.charType) 
//        {
//            emit(INVOKEVIRTUAL, "java/lang/StringBuilder.charAt(I)C");
//        }
//
//        // Load an array element.
//        else 
//        {
//            emit(  elmtType == Predefined.integerType ? IALOAD
//                 : elmtType == Predefined.realType    ? FALOAD
//                 : elmtType == Predefined.booleanType ? BALOAD
//                 : elmtType == Predefined.charType    ? CALOAD
//                 : form == ENUMERATION                ? IALOAD
//                 :                                      AALOAD);
//        }
//    }
    
//    private void emitLoadRecordFieldValue(
//                        PascalParser.FieldContext fieldCtx, Typespec recordType)
//    {
//        emitLoadRecordField(fieldCtx, recordType);
//    }
//
//    /**
//     * Emit code to load the address or value of a record field.
//     * @param fieldCtx the FieldContext.
//     * @param last true if this is the variable's last field, else false.
//     * @return the type of the field.
//     */
//    private Typespec emitLoadRecordField(
//                        PascalParser.FieldContext fieldCtx, Typespec recordType)
//    {
//        SymtabEntry fieldId = fieldCtx.entry;
//        String fieldName = fieldId.getName();
//        Typespec fieldType = fieldCtx.type;  
//        
//        String recordTypePath = recordType.getRecordTypePath();
//        String fieldPath = recordTypePath + "/" + fieldName;        
//        emit(GETFIELD, fieldPath, typeDescriptor(fieldType));
//
//        return fieldType;
//    }

    /**
     * Emit code to load a boolean constant
     * @param boolCtx the BooleanConstantContext
     */
    public void emitLoadBooleanConstant(AKAParser.BooleanConstantContext boolCtx) {
    	if (boolCtx.TRUE() != null) {
    		emitLoadConstant(1);
    	} else {
    		emitLoadConstant(0);
    	}
    }
    
    /**
     * Emit code to load real constant.
     * @parm intCtx the IntegerConstantContext.
     */
    public void emitLoadNumberConstant(AKAParser.NumberConstantContext numCtx)
    {
        float value = Float.parseFloat(numCtx.getText());
        emitLoadConstant(value);
    }
    public void emitLoadNumberConstant(float num)
    {
        emitLoadConstant(num);
    }
}
