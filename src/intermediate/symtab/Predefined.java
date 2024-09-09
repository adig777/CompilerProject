package intermediate.symtab;

import java.util.ArrayList;

import intermediate.symtab.SymtabEntry.Kind;
import intermediate.symtab.SymtabEntry.Routine;
import intermediate.type.*;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.*;
import static intermediate.type.Typespec.Form.*;

/**
 * <h1>Predefined</h1>
 *
 * <p>Enter the predefined Pascal types, identifiers, and constants
 * into the symbol table.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Predefined
{
    // Predefined types.
    public static Typespec numberType;
    public static Typespec booleanType;
    public static Typespec stringType;
    public static Typespec undefinedType;

    // Predefined identifiers.
    public static SymtabEntry numberId;
    public static SymtabEntry booleanId;
    public static SymtabEntry stringId;
    public static SymtabEntry falseId;
    public static SymtabEntry trueId;
    public static SymtabEntry readId;
    public static SymtabEntry readlnId;
    public static SymtabEntry writeId;
    public static SymtabEntry absId;
    public static SymtabEntry arctanId;
    public static SymtabEntry chrId;
    public static SymtabEntry cosId;
    public static SymtabEntry eofId;
    public static SymtabEntry eolnId;
    public static SymtabEntry expId;
    public static SymtabEntry lnId;
    public static SymtabEntry oddId;
    public static SymtabEntry ordId;
    public static SymtabEntry predId;
    public static SymtabEntry roundId;
    public static SymtabEntry sinId;
    public static SymtabEntry sqrId;
    public static SymtabEntry sqrtId;
    public static SymtabEntry succId;
    public static SymtabEntry truncId;

    /**
     * Initialize a symbol table stack with predefined identifiers.
     * @param symTab the symbol table stack to initialize.
     */
    public static void initialize(SymtabStack symTabStack)
    {
        initializeTypes(symTabStack);
        initializeConstants(symTabStack);
        initializeStandardRoutines(symTabStack);
    }

    /**
     * Initialize the predefined types.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeTypes(SymtabStack symTabStack)
    {
        // Type integer.
        numberId = symTabStack.enterLocal("number", TYPE);
        numberType = new Typespec(SCALAR);
        numberType.setIdentifier(numberId);
        numberId.setType(numberType);


        // Type boolean.
        booleanId = symTabStack.enterLocal("boolean", TYPE);
        booleanType = new Typespec(ENUMERATION);
        booleanType.setIdentifier(booleanId);
        booleanId.setType(booleanType);


        // Type string.
        stringId = symTabStack.enterLocal("string", TYPE);
        stringType = new Typespec(SCALAR);
        stringType.setIdentifier(stringId);
        stringId.setType(stringType);

        // Undefined type.
        undefinedType = new Typespec(SCALAR);
    }

    /**
     * Initialize the predefined constant.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeConstants(SymtabStack symTabStack)
    {
        // Boolean enumeration constant false.
        falseId = symTabStack.enterLocal("false", ENUMERATION_CONSTANT);
        falseId.setType(booleanType);
        falseId.setValue(0);

        // Boolean enumeration constant true.
        trueId = symTabStack.enterLocal("true", ENUMERATION_CONSTANT);
        trueId.setType(booleanType);
        trueId.setValue(1);

        // Add false and true to the boolean enumeration type.
        ArrayList<SymtabEntry> constants = booleanType.getEnumerationConstants();
        constants.add(falseId);
        constants.add(trueId);
    }

    /**
     * Initialize the standard procedures and functions.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeStandardRoutines(SymtabStack symTabStack)
    {
        readId    = enterStandard(symTabStack, DEFINITIONNORETURN, "read",    READ);
        readlnId  = enterStandard(symTabStack, DEFINITIONNORETURN, "readln",  READLN);
        writeId   = enterStandard(symTabStack, DEFINITIONNORETURN, "write",   WRITE);

        absId    = enterStandard(symTabStack, DEFINITION, "abs",    ABS);
        arctanId = enterStandard(symTabStack, DEFINITION, "arctan", ARCTAN);
        chrId    = enterStandard(symTabStack, DEFINITION, "chr",    CHR);
        cosId    = enterStandard(symTabStack, DEFINITION, "cos",    COS);
        eofId    = enterStandard(symTabStack, DEFINITION, "eof",    EOF);
        eolnId   = enterStandard(symTabStack, DEFINITION, "eoln",   EOLN);
        expId    = enterStandard(symTabStack, DEFINITION, "exp",    EXP);
        lnId     = enterStandard(symTabStack, DEFINITION, "ln",     LN);
        oddId    = enterStandard(symTabStack, DEFINITION, "odd",    ODD);
        ordId    = enterStandard(symTabStack, DEFINITION, "ord",    ORD);
        predId   = enterStandard(symTabStack, DEFINITION, "pred",   PRED);
        roundId  = enterStandard(symTabStack, DEFINITION, "round",  ROUND);
        sinId    = enterStandard(symTabStack, DEFINITION, "sin",    SIN);
        sqrId    = enterStandard(symTabStack, DEFINITION, "sqr",    SQR);
        sqrtId   = enterStandard(symTabStack, DEFINITION, "sqrt",   SQRT);
        succId   = enterStandard(symTabStack, DEFINITION, "succ",   SUCC);
        truncId  = enterStandard(symTabStack, DEFINITION, "trunc",  TRUNC);
    }

    /**
     * Enter a standard procedure or function into the symbol table stack.
     * @param symTabStack the symbol table stack to initialize.
     * @param kind either PROCEDURE or FUNCTION.
     * @param name the procedure or function name.
     * @param routineCode the routine code.
     */
    private static SymtabEntry enterStandard(SymtabStack symTabStack,
                                             Kind kind, String name,
                                             Routine routineCode)
    {
        SymtabEntry routineId = symTabStack.enterLocal(name, kind);
        routineId.setRoutineCode(routineCode);

        return routineId;
    }
}
