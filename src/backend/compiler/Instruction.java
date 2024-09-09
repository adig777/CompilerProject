package backend.compiler;

/**
 * <h1>Instruction</h1>
 *
 * <p>Jasmin instructions.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum Instruction
{
    // Load constant
    ICONST_0(1), ICONST_1(1), ICONST_2(1), ICONST_3(1), 
    ICONST_4(1), ICONST_5(1), ICONST_M1(1),
    FCONST_0(1), FCONST_1(1), FCONST_2(1), ACONST_NULL(1),
    BIPUSH(1), SIPUSH(1), LDC(1),

    // Load value or address
    ILOAD_0(1), ILOAD_1(1), ILOAD_2(1), ILOAD_3(1),
    FLOAD_0(1), FLOAD_1(1), FLOAD_2(1), FLOAD_3(1),
    ALOAD_0(1), ALOAD_1(1), ALOAD_2(1), ALOAD_3(1),
    LLOAD_0(2), LLOAD_1(2), LLOAD_2(2), LLOAD_3(2),
    ILOAD(1),   FLOAD(1),   ALOAD(1),
    GETSTATIC(1), GETFIELD(0),

    // Store value or address
    ISTORE_0(-1),  ISTORE_1(-1), ISTORE_2(-1), ISTORE_3(-1),
    FSTORE_0(-1),  FSTORE_1(-1), FSTORE_2(-1), FSTORE_3(-1),
    ASTORE_0(-1),  ASTORE_1(-1), ASTORE_2(-1), ASTORE_3(-1),
    LSTORE_0(-2),  LSTORE_1(-2), LSTORE_2(-2), LSTORE_3(-2),
    ISTORE(-1),    FSTORE(-1),   ASTORE(-1),
    PUTSTATIC(-1), PUTFIELD(-2),

    // Operand stack
    POP(-1), SWAP(0), DUP(1), DUP_X1(1), DUP_X2(1),

    // Arithmetic and logical
    IADD(-1), FADD(-1), ISUB(-1), FSUB(-1), IMUL(-1), FMUL(-1),
    IDIV(-1), FDIV(-1), IREM(-1), FREM(-1), INEG(0),  FNEG(0),
    IINC(0),  IAND(-1), IOR(-1),  IXOR(-1),

    // Type conversion and checking
    I2F(0), I2C(0), I2D(0), F2I(0), F2D(0), D2F(0),
    CHECKCAST(0),

    // Objects and arrays
    NEW(1), NEWARRAY(0), ANEWARRAY(0), MULTIANEWARRAY(0),
    IALOAD(-1), FALOAD(-1), BALOAD(-1), CALOAD(-1), AALOAD(-1),
    IASTORE(-3), FASTORE(-3), BASTORE(-3), CASTORE(-3), AASTORE(-3),

    // Compare and branch
    IFEQ(-1), IFNE(-1), IFLT(-1), IFLE(-1), IFGT(-1), IFGE(-1),
    IF_ICMPEQ(-2), IF_ICMPNE(-2), IF_ICMPLT(-2), 
    IF_ICMPLE(-2), IF_ICMPGT(-2), IF_ICMPGE(-2),
    FCMPG(-1), GOTO(0), LOOKUPSWITCH(-1),

    // Call and return
    INVOKESTATIC(0), INVOKESPECIAL(0), 
    INVOKEVIRTUAL(0), INVOKENONVIRTUAL(0),
    RETURN(0), IRETURN(-1), FRETURN(-1), ARETURN(-1),

    // No operation
    NOP(0);
    
    public int stackUse;
    
    Instruction(int stackUse) { this.stackUse = stackUse; }

    /**
     * Generate the instruction text that is emitted.
     * @return the text.
     */
    public String toString() { return super.toString().toLowerCase(); }
}
