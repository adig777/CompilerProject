package intermediate.symtab;

import java.util.ArrayList;

import intermediate.type.*;

/**
 * <h1>SymtabEntryImpl</h1>
 *
 * <p>An implementation of a symbol table entry.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SymtabEntry
{
    private String name;                     // entry name
    private Kind kind;                       // what kind of identifier
    private Symtab symtab;                   // parent symbol table
    private Typespec typespec;               // type specification
    private int slotNumber;                  // local variables array slot number
    private ArrayList<Integer> lineNumbers;  // source line numbers
    private EntryInfo info;                  // entry information
    
    /**
     * What kind of identifier.
     */
    public enum Kind
    {
        CONSTANT, ENUMERATION_CONSTANT, TYPE, VARIABLE, RECORD_FIELD,
        VALUE_PARAMETER, REFERENCE_PARAMETER, PROGRAM_PARAMETER,
        PROGRAM, DEFINITION, DEFINITIONNORETURN,
        UNDEFINED;
        
        public String toString() { return super.toString().toLowerCase(); }
    }
    
    /**
     * Which routine.
     */
    public enum Routine
    {
        DECLARED, FORWARD,
        READ, READLN, WRITE,
        ABS, ARCTAN, CHR, COS, EOF, EOLN, EXP, LN, ODD, ORD,
        PRED, ROUND, SIN, SQR, SQRT, SUCC, TRUNC,
    }

    /**
     * Entry information interface.
     */
    private interface EntryInfo {}
    
    /**
     * Value information.
     */
    private class ValueInfo implements EntryInfo
    {
        private Object value;
    }
    
    /**
     * Routine information.
     */
    private class RoutineInfo implements EntryInfo
    {
        private Routine code;                        // routine code
        private Symtab symtab;                       // routine's symbol table
        private ArrayList<SymtabEntry> parameters;   // routine's formal parameters
        private ArrayList<SymtabEntry> subroutines;  // symtab entries of subroutines
        private Object executable;                   // routine's executable code
    }
    
    /**
     * Constructor.
     * @param name the name of the entry.
     * @param kind the kind of entry.
     * @param symtab the symbol table that contains this entry.
     */
    public SymtabEntry(String name, Kind kind, Symtab symtab)
    {
        this.name = name;
        this.kind = kind;
        this.symtab = symtab;
        this.lineNumbers = new ArrayList<Integer>();

        // Initialize the appropriate entry information.
        switch (kind)
        {
            case CONSTANT:
            case ENUMERATION_CONSTANT:
            case VARIABLE:
            case RECORD_FIELD:
            case VALUE_PARAMETER:
                info = new ValueInfo();
                break;
                
            case PROGRAM:
            case DEFINITION:
            case DEFINITIONNORETURN:
                info = new RoutineInfo();
                ((RoutineInfo) info).parameters  = new ArrayList<SymtabEntry>();
                ((RoutineInfo) info).subroutines = new ArrayList<SymtabEntry>();
                break;
            
            default: break;
        }
    }

    /**
     * Get the name of the entry.
     * @return the name.
     */
    public String getName() { return name; }
    
    /**
     * Get the kind of entry.
     * @return the kind.
     */
    public Kind getKind() { return kind; }
    
    /**
     * Set the kind of entry.
     * @param kind the kind to set.
     */
    public void setKind(Kind kind) { this.kind = kind; }

    /**
     * Get the symbol table that contains this entry.
     * @return the symbol table.
     */
    public Symtab getSymtab() { return symtab; }

    /**
     * Get the slot number of the local variables array.
     * @return the number.
     */
    public int getSlotNumber() { return slotNumber; }

    /**
     * Set the slot number of the local variables array.
     * @param slotNumber the number to set.
     */
    public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }

    /**
     * Get the type specification of the entry.
     * @return the type specification.
     */
    public Typespec getType() { return typespec; }

    /**
     * Set the type specification.
     * @param typespec the type specification to set.
     */
    public void setType(Typespec typespec) { this.typespec = typespec; }

    /**
     * Get the arraylist of source line numbers for the entry.
     * @return the arraylist.
     */
    public ArrayList<Integer> getLineNumbers() { return lineNumbers; }

    /**
     * Append a source line number to the entry.
     * @param lineNumber the line number to append.
     */
    public void appendLineNumber(int lineNumber)
    {
        lineNumbers.add(lineNumber);
    }
    
    /**
     * Get the data value stored with this entry.
     * @return the data value.
     */
    public Object getValue() { return ((ValueInfo) info).value; }

    /**
     * Set the data value into this entry.
     * @parm value the value to set.
     */
    public void setValue(Object value) {  ((ValueInfo) info).value = value; }

    /**
     * Get the routine code.
     * @return the code.
     */
    public Routine getRoutineCode() { return ((RoutineInfo) info).code; }

    /**
     * Set the routine code.
     * @parm code the code to set.
     */
    public void setRoutineCode(Routine code) { ((RoutineInfo) info).code = code; }

    /**
     * Get the routine's symbol table.
     * @return the symbol table.
     */
    public Symtab getRoutineSymtab()  { return ((RoutineInfo) info).symtab; }

    /**
     * Set the routine's symbol table.
     * @parm symtab the symbol table to set.
     */
    public void setRoutineSymtab(Symtab symtab) 
    { 
        ((RoutineInfo) info).symtab = symtab; 
    }

    /**
     * Get the arraylist of symbol table entries of the routine's formal parameters.
     * @return the arraylist.
     */
    public ArrayList<SymtabEntry> getRoutineParameters() 
    { 
        return ((RoutineInfo) info).parameters; 
    }

    /**
     * Set the arraylist symbol table entries of parameters of the routine.
     * @parm parameters the arraylist to set.
     */
    public void setRoutineParameters(ArrayList<SymtabEntry> parameters)
    {
        ((RoutineInfo) info).parameters = parameters;
    }

    /**
     * Get the arraylist of symbol table entries of the nested subroutines.
     * @return the arraylist.
     */
    public ArrayList<SymtabEntry> getSubroutines() 
    { 
        return ((RoutineInfo) info).subroutines; 
    }

    /**
     * Append to the arraylist of symbol table entries of the nested subroutines.
     * @parm subroutineId the symbol table entry of the subroutine to append.
     */
    public void appendSubroutine(SymtabEntry subroutineId)
    {
        ((RoutineInfo) info).subroutines.add(subroutineId);
    }
    
    /**
     * Get the routine's executable code.
     * @return the executable code.
     */
    public Object getExecutable() { return ((RoutineInfo) info).executable; }

    /**
     * Set the routine's executable code.
     * @parm executable the executable code to set.
     */
    public void setExecutable(Object executable)
    {
        ((RoutineInfo) info).executable = executable;
    }
}
