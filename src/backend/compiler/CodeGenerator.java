package backend.compiler;

import java.io.FileWriter;
import java.io.PrintWriter;

import antlr4.AKAParser;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;
import static backend.compiler.Instruction.*;

public class CodeGenerator
{    
    protected PrintWriter objectFile;
    protected String programName;
    protected LocalVariables localVariables;
    protected LocalStack localStack;
    protected Compiler compiler;
    
    protected String objectFileName;
        
    protected static int count = 0;
    
    /**
     * Constructor.
     * @param programName the name of the program.
     * @param suffix the suffix for the object file name.
     * @param compiler the compiler to use.
     */
    CodeGenerator(String programName, String suffix, Compiler compiler)
    {
        this.programName    = programName;
        this.localVariables = null;
        this.localStack     = null;
        this.compiler       = compiler;
        
        // Create the Jasmin object file.
        try 
        {
            objectFileName = programName + "." + suffix;
            objectFile = new PrintWriter(new FileWriter(objectFileName));
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Constructor for code generator subclasses.
     * @param the parent code generator.
     * @param the compiler to use.
     */
    public CodeGenerator(CodeGenerator parent, Compiler compiler)
    {
        this.compiler       = compiler;
        this.objectFile     = parent.objectFile;
        this.objectFileName = parent.objectFileName;
        this.programName    = parent.programName;
        this.localVariables = parent.localVariables;
        this.localStack     = parent.localStack;
    }
    
    /**
     * Get the name of the object (Jasmin) file.
     * @return the name.
     */
    public String getObjectFileName() { return objectFileName; }
    
    /**
     * Close the object file.
     */
    public void close() { objectFile.close(); }
    
    /**
     * Get the local variables.
     * @return the local variables.
     */
    public LocalVariables getLocalVariables() { return localVariables; }
    
    /**
     * Get the local stack.
     * @ return the local stack.
     */
    public LocalStack getLocalStack() { return localStack; }

    // =====================
    // General code emitters
    // =====================

    /**
     * Emit a blank line.
     */
    public void emitLine()
    {
        objectFile.println();
        objectFile.flush();
    }
    
    /**
     * Emit a comment.
     * @param text the comment text.
     */
    public void emitComment(String text)
    {
        objectFile.println(";");
        objectFile.println("; " + text);
        objectFile.println(";");
        objectFile.flush();
    }

    /**
     * Emit a statement comment.
     * @param ctx the StatementContext.
     */
    public void emitComment(AKAParser.StatementContext ctx)
    {
        String text = String.format("%03d %s", ctx.getStart().getLine(), 
                                               ctx.getText());
        
        if (text.length() <= 72) emitComment(text);
        else                     emitComment(text.substring(0, 72) + " ...");
    }

    /**
     * Emit a label.
     * @param label the label.
     */
    public void emitLabel(Label label)
    {
        objectFile.println(label + ":");
        objectFile.flush();
    }

    /**
     * Emit a label preceded by an integer value for a switch table.
     * @param label the label.
     */
    public void emitLabel(int value, Label label)
    {
        objectFile.println("\t  " + value + ": " + label);
        objectFile.flush();
    }

    /**
     * Emit a label preceded by a string value for a switch table.
     * @param label the label.
     */
    public void emitLabel(String value, Label label)
    {
        objectFile.println("\t  " + value + ": " + label);
        objectFile.flush();
    }

    /**
     * Emit a directive.
     * @param directive the directive code.
     */
    public void emitDirective(Directive directive)
    {
        objectFile.println(directive.toString());
        objectFile.flush();
        ++count;
    }

    /**
     * Emit a 1-operand directive.
     * @param directive the directive code.
     * @param operand the directive operand.
     */
    public void emitDirective(Directive directive, String operand)
    {
        objectFile.println(directive.toString() + " " + operand);
        objectFile.flush();
        ++count;
    }

    /**
     * Emit a 1-operand directive.
     * @param directive the directive code.
     * @param operand the directive operand.
     */
    public void emitDirective(Directive directive, int operand)
    {
        objectFile.println(directive.toString() + " " + 16);
        objectFile.flush();
        ++count;
    }

    /**
     * Emit a 2-operand directive.
     * @param directive the directive code.
     * @param operand the operand.
     */
    public void emitDirective(Directive directive,
                              String operand1, String operand2)
    {
        objectFile.println(directive.toString() + " " + operand1 +
                                                    " " + operand2);
        objectFile.flush();
        ++count;
    }

    /**
     * Emit a 3-operand directive.
     * @param directive the directive code.
     * @param operand the operand.
     */
    public void emitDirective(Directive directive,
                               String operand1, String operand2,
                               String operand3)
    {
        objectFile.println(directive.toString() + " " + operand1 
                                                + " " + operand2
                                                + " " + operand3);
        objectFile.flush();
        ++count;
    }

    /**
     * Emit a 0-operand instruction.
     * @param instruction the operation code.
     */
    public void emit(Instruction instruction)
    {
        objectFile.println("\t" + instruction.toString());
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 1-operand instruction.
     * @param instruction the operation code.
     * @param operand the operand text.
     */
    public void emit(Instruction instruction, String operand)
    {
        objectFile.println("\t" + instruction.toString() + "\t" + operand);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 1-operand instruction.
     * @param instruction the operation code.
     * @param operand the operand value.
     */
    public void emit(Instruction instruction, int operand)
    {
        objectFile.println("\t" + instruction.toString() + "\t" + operand);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 1-operand instruction.
     * @param instruction the operation code.
     * @param operand the operand value.
     */
    public void emit(Instruction instruction, double operand)
    {
        objectFile.println("\t" + instruction.toString() + "\t" + operand);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 1-operand instruction.
     * @param instruction the operation code.
     * @param label the label operand.
     */
    public void emit(Instruction instruction, Label label)
    {
        objectFile.println("\t" + instruction.toString() + "\t" + label);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 2-operand instruction.
     * @param instruction the operation code.
     * @param operand1 the value of the first operand.
     * @param operand2 the value of the second operand.
     */
    public void emit(Instruction instruction, int operand1, int operand2)
    {
        objectFile.println("\t" + instruction.toString() +
                           "\t" + operand1 + " " + operand2);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    /**
     * Emit a 2-operand instruction.
     * @param instruction the operation code.
     * @param operand1 the text of the first operand.
     * @param operand2 the text of the second operand.
     */
    public void emit(Instruction instruction, String operand1, String operand2)
    {
        objectFile.println("\t" + instruction.toString() +
                           "\t" + operand1 + " " + operand2);
        objectFile.flush();
        
        localStack.increase(instruction.stackUse);
        ++count;
    }

    // =====
    // Loads
    // =====

    /**
     * Emit a load of an integer constant value.
     * @param value the constant value.
     */
    public void emitLoadConstant(int value)
    {
        switch (value) 
        {
            case -1: emit(ICONST_M1); break;
            case  0: emit(ICONST_0);  break;
            case  1: emit(ICONST_1);  break;
            case  2: emit(ICONST_2);  break;
            case  3: emit(ICONST_3);  break;
            case  4: emit(ICONST_4);  break;
            case  5: emit(ICONST_5);  break;

            default: 
            {
                if (   (-128 <= value) 
                    && (value <= 127))        emit(BIPUSH, value);
                else if (   (-32768 <= value) 
                         && (value <= 32767)) emit(SIPUSH, value);
                else                          emit(LDC, value);
            }
        }
    }

    /**
     * Emit a load of a real constant value.
     * @param value the constant value.
     */
    public void emitLoadConstant(double value)
    {
        if      (value == 0.0f) emit(FCONST_0);
        else if (value == 1.0f) emit(FCONST_1);
        else if (value == 2.0f) emit(FCONST_2);
        else                    emit(LDC, value);
    }

    /**
     * Emit a load of a string constant value.
     * @param value the constant value.
     */
    public void emitLoadConstant(String value)
    {
        emit(LDC, "\"" + value + "\"");
    }

    /**
     * Emit code to load the value of a variable, which can be
     * a program variable, a local variable, a constant, or a VAR parameter.
     * @param variableId the variable's symbol table entry.
     */
    public void emitLoadValue(SymtabEntry variableId)
    {
        Typespec type = variableId.getType().baseType();
        Kind kind = variableId.getKind();
        int nestingLevel = variableId.getSymtab().getNestingLevel();
        
        // Constant
        if (kind == CONSTANT)
        {
            Object value = variableId.getValue();
            
            if (type == Predefined.numberType)
            {
                emitLoadConstant((Float) value);
            }
//            else if (type == Predefined.charType)
//            {
//                int ch = (Character) value;
//                emitLoadConstant(ch);
//            }
            else  // string
            {
                emitLoadConstant((String) value);
            }
        }
        
        // Enumeration constant
        else if (kind == ENUMERATION_CONSTANT)
        {
            Object value = variableId.getValue();
            emitLoadConstant((Integer) value);
        }

        // Program variable.
        else if (nestingLevel == 1) 
        {
            String variableName = variableId.getName();
            String name = programName + "/" + variableName;
            emit(GETSTATIC, name, typeDescriptor(type));
        }

        // Local variable.
        else 
        {
            int slot =  variableId.getSlotNumber();
            emitLoadLocal(type, slot);
        }
    }

    /**
     * Emit a load instruction for a local variable.
     * @param type the variable's data type.
     * @param index the variable's index into the local variables array.
     */
    public void emitLoadLocal(Typespec type, int index)
    {
        Form form = null;

        if (type != null) 
        {
            type = type.baseType();
            form = type.getForm();
        }

        if (   (type == Predefined.booleanType)
//            || (type == Predefined.charType)
            || (form == ENUMERATION))
        {
            switch (index) 
            {
                case 0:  emit(ILOAD_0); break;
                case 1:  emit(ILOAD_1); break;
                case 2:  emit(ILOAD_2); break;
                case 3:  emit(ILOAD_3); break;
                default: emit(ILOAD, index);
            }
        }
        else if (type == Predefined.numberType) 
        {
            switch (index) {
                case 0:  emit(FLOAD_0); break;
                case 1:  emit(FLOAD_1); break;
                case 2:  emit(FLOAD_2); break;
                case 3:  emit(FLOAD_3); break;
                default: emit(FLOAD, index);
            }
        }
        else 
        {
            switch (index) 
            {
                case 0:  emit(ALOAD_0); break;
                case 1:  emit(ALOAD_1); break;
                case 2:  emit(ALOAD_2); break;
                case 3:  emit(ALOAD_3); break;
                default: emit(ALOAD, index);
            }
        }
    }

    // ======
    // Stores
    // ======
    
    /**
     * Emit a store of a value that is on top of the operand stack.
     * Store to an array element, a record field, or an ummodified variable.
     * @param targetId the symbol table entry of the target.
     * @param targetType the target's datatype.
     */
    public void emitStoreValue(SymtabEntry targetId, Typespec targetType)
    {
        if (targetId == null)
        {
            emitStoreToArrayElement(targetType);
        }
        else if (targetId.getKind() == RECORD_FIELD) 
        {
            emitStoreToRecordField(targetId);
        }
        else
        {
            emitStoreToUnmodifiedVariable(targetId, targetType);
        }
    }

    /**
     * Emit code to store a value to an ummodified target variable, 
     * which can be a program variable or a local variable.
     * @param targetId the symbol table entry of the variable.
     */
    private void emitStoreToUnmodifiedVariable(SymtabEntry targetId, 
                                               Typespec targetType)
    {
        int nestingLevel = targetId.getSymtab().getNestingLevel();
        int slot = targetId.getSlotNumber();
        
        // Program variable.
        if (nestingLevel == 1) 
        {
            String targetName = targetId.getName();
            String name = programName + "/" + targetName;

            emitRangeCheck(targetType);
            emit(PUTSTATIC, name, typeDescriptor(targetType.baseType()));
        }

        // Local variable.
        else 
        {
            emitRangeCheck(targetType);
            emitStoreLocal(targetType.baseType(), slot);
        }
    }
    
    

    /**
     * Emit a store instruction into a local variable.
     * @param type the data type of the variable.
     * @param slot the variable's slot number.
     */
    public void emitStoreLocal(Typespec type, int slot)
    {
        Form form = null;

        if (type != null) 
        {
            type = type.baseType();
            form = type.getForm();
        }

        if (   (type == Predefined.booleanType)
//            || (type == Predefined.charType)
            || (form == ENUMERATION))
        {
            switch (slot) 
            {
                case 0:  emit(ISTORE_0); break;
                case 1:  emit(ISTORE_1); break;
                case 2:  emit(ISTORE_2); break;
                case 3:  emit(ISTORE_3); break;
                default: emit(ISTORE, slot);
            }
        }
        else if (type == Predefined.numberType) 
        {
            switch (slot) {
                case 0:  emit(FSTORE_0); break;
                case 1:  emit(FSTORE_1); break;
                case 2:  emit(FSTORE_2); break;
                case 3:  emit(FSTORE_3); break;
                default: emit(FSTORE, slot);
            }
        }
        else 
        {
            switch (slot) 
            {
                case 0:  emit(ASTORE_0); break;
                case 1:  emit(ASTORE_1); break;
                case 2:  emit(ASTORE_2); break;
                case 3:  emit(ASTORE_3); break;
                default: emit(ASTORE, slot);
            }
        }
    }

    /**
     * Emit a store to an array element.
     * @param elmtType the element type.
     */
    private void emitStoreToArrayElement(Typespec elmtType)
    {
        Form form = null;

        if (elmtType != null) 
        {
            elmtType = elmtType.baseType();
            form = elmtType.getForm();
        }

        emit(  elmtType == Predefined.numberType  ? FASTORE
             : elmtType == Predefined.booleanType ? BASTORE
//             : elmtType == Predefined.charType    ? CASTORE
             : form == ENUMERATION                ? IASTORE
             :                                      AASTORE);
    }
    /**
     * Emit a store to a record field.
     * @param fieldId the symbol table entry of the field.
     */
    private void emitStoreToRecordField(SymtabEntry fieldId)
    {
        String fieldName = fieldId.getName();
        Typespec fieldType = fieldId.getType();  
        Typespec recordType = fieldId.getSymtab().getOwner().getType();
        
        String recordTypePath = recordType.getRecordTypePath();
        String fieldPath = recordTypePath + "/" + fieldName;
        
        emit(PUTFIELD, fieldPath, typeDescriptor(fieldType));
    }

    // ======================
    // Miscellaneous emitters
    // ======================

    /**
     * Emit the CHECKCAST instruction for a scalar type.
     * @param type the data type.
     */
    public void emitCheckCast(Typespec type)
    {
        String descriptor = typeDescriptor(type);

        // Don't bracket the type with L; if it's not an array.
        if (descriptor.charAt(0) == 'L') 
        {
            descriptor = descriptor.substring(1, descriptor.length() - 1);
        }

        emit(CHECKCAST, descriptor);
    }

    /**
     * Emit the CHECKCAST instruction for a class.
     * @param type the data type.
     */
    public void emitCheckCastClass(Typespec type)
    {
        String descriptor = objectTypeName(type);

        // Don't bracket the type with L; if it's not an array.
        if (descriptor.charAt(0) == 'L') 
        {
            descriptor = descriptor.substring(1, descriptor.length() - 1);
        }

        emit(CHECKCAST, descriptor);
    }

    /**
     * Emit a function return of a value.
     * @param type the type of the return value.
     */
    public void emitReturnValue(Typespec type)
    {
        Form form = null;

        if (type != null) 
        {
            type = type.baseType();
            form = type.getForm();
        }

        if (   (type == Predefined.booleanType)
//            || (type == Predefined.integerType)
//            || (type == Predefined.charType)
            || (form == ENUMERATION))         emit(IRETURN);
        else if (type == Predefined.numberType) emit(FRETURN);
        else                                  emit(ARETURN);
    }

    /**
     * Emit code to perform a runtime range check before an assignment.
     * @param targetType the type of the assignment target.
     */
    public void emitRangeCheck(Typespec targetType)
    {
//        if (targetType.getForm() == SUBRANGE) 
//        {
//            int min = targetType.getSubrangeMinValue();
//            int max = targetType.getSubrangeMaxValue();
//
//            emit(DUP);
//            emitLoadConstant(min);
//            emitLoadConstant(max);
//            emit(INVOKESTATIC, "RangeChecker/check(III)V");
//
//            localStack.use(3);
//        }
    }

    // =========
    // Utilities
    // =========

    /**
     * Emit a type descriptor of an identifier's type.
     * @param id the symbol table entry of an identifier.
     * @return the type descriptor.
     */
    public String typeDescriptor(SymtabEntry id)
    {
        Typespec type = id.getType();
        return type != null ? typeDescriptor(type) : "V";
    }

    /**
     * Return a type descriptor for a Pascal datatype.
     * @param pascalType the datatype.
     * @return the type descriptor.
     */
    public String typeDescriptor(Typespec pascalType)
    {
        Form form = pascalType.getForm();
        StringBuffer buffer = new StringBuffer();

        while (form == ARRAY) 
        {
            buffer.append("[");
            pascalType =  pascalType.getArrayElementType();
            form = pascalType.getForm();
        }

        pascalType = pascalType.baseType();
        String str;

        if      (pascalType == Predefined.numberType) str = "F";
        else if (pascalType == Predefined.booleanType) str = "Z";
        else if (pascalType == Predefined.stringType)  str = "Ljava/lang/String;";
        else if (form == ENUMERATION)                  str = "I";
        else /* (form == RECORD) */ str = "L" + pascalType.getRecordTypePath() + ";";

        buffer.append(str);
        return buffer.toString();
    }

    /**
     * Return the Java object name for a Pascal datatype.
     * @param pascalType the datatype.
     * @return the object name.
     */
    public String objectTypeName(Typespec pascalType)
    {
        Form form = pascalType.getForm();
        StringBuffer buffer = new StringBuffer();
        boolean isArray = false;

        while (form == ARRAY) 
        {
            buffer.append("[");
            pascalType = pascalType.getArrayElementType();
            form = pascalType.getForm();
            isArray = true;
        }

        if (isArray) buffer.append("L");

        pascalType = pascalType.baseType();
        String str;

        if      (pascalType == Predefined.numberType) str = "java/lang/Float";
        else if (pascalType == Predefined.booleanType) str = "java/lang/Boolean";
        else if (pascalType == Predefined.stringType)  str = "Ljava/lang/String;";
        else if (form == ENUMERATION)                  str = "java/lang/Integer";
        else /* (form == RECORD) */ str = "L" + pascalType.getRecordTypePath() + ";";
        
        buffer.append(str);
        if (isArray) buffer.append(";");

        return buffer.toString();
    }

    /**
     * Return whether or not a value needs to be cloned to pass by value.
     * @param formalId the symbol table entry of the formal parameter.
     * @return true if needs wrapping, false if not.
     */
    public boolean needsCloning(SymtabEntry formalId)
    {
        Typespec type = formalId.getType();
        Form form = type.getForm();
        Kind kind = formalId.getKind();

        // Arrays and records are normally passed by reference
        // and so must be cloned to be passed by value.
        return (   (kind == VALUE_PARAMETER))
                && ((form == ARRAY) || (form == RECORD));
    }

    /**
     * Return the valueOf() signature for a given scalar type.
     * @param type the scalar type.
     * @return the valueOf() signature.
     */
    public String valueOfSignature(Typespec type)
    {
        String javaType = objectTypeName(type);
        String typeCode = typeDescriptor(type);

        return String.format("%s/valueOf(%s)L%s;",
                             javaType, typeCode, javaType);
    }

    /**
     * Return the xxxValue() signature for a given scalar type.
     * @param type the scalar type.
     * @return the valueOf() signature.
     */
    public String valueSignature(Typespec type)
    {
        String javaType = objectTypeName(type);
        String typeCode = typeDescriptor(type);
        String typeName = type == Predefined.numberType ? "number"
                        : type == Predefined.booleanType ? "boolean"
                        :                                  "int";

        return (String.format("%s.%sValue()%s",
                              javaType, typeName, typeCode));
    }
}
