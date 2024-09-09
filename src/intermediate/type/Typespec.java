package intermediate.type;

import java.util.ArrayList;

import intermediate.symtab.*;

/**
 * <h1>Typespec</h1>
 *
 * <p>The type specification object for various datatypes.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Typespec
{
    private Form form;               // type form
    private SymtabEntry identifier;  // type identifier
    private TypeInfo info;           // type information
    
    public enum Form
    {
        SCALAR, ENUMERATION, SUBRANGE, ARRAY, RECORD, UNKNOWN;

        public String toString() { return super.toString().toLowerCase(); }
    }
    
    /**
     * Type information interface.
     */
    private interface TypeInfo {}
    
    /**
     * Enumeration type information.
     */
    private class EnumerationInfo implements TypeInfo
    {
        private ArrayList<SymtabEntry> constants;
    }
    
    /**
     * Subrange type information.
     */
    private class SubrangeInfo implements TypeInfo
    {
        private Typespec baseType;
        private int minValue;
        private int maxValue;
    }
    
    /**
     * Array type information.
     */
    private class ArrayInfo implements TypeInfo
    {
        private Typespec indexType;
        private Typespec elementType;
        private int elementCount;
    }
    
    /**
     * Record type information.
     */
    private class RecordInfo implements TypeInfo
    {
        String typePath;
        private Symtab symtab;
    }

    /**
     * Constructor.
     * @param form the type form.
     */
    public Typespec(Form form)
    {
        this.form = form;
        this.identifier = null;
        
        // Initialize the appropriate type information.
        switch (form)
        {
            case ENUMERATION:
                info = new EnumerationInfo();
                ((EnumerationInfo) info).constants = 
                                                new ArrayList<SymtabEntry>();
                break;

            case SUBRANGE:
                info = new SubrangeInfo();
                ((SubrangeInfo) info).minValue = 0;
                ((SubrangeInfo) info).maxValue = 0;
                ((SubrangeInfo) info).baseType = null;
                break;

            case ARRAY:
                info = new ArrayInfo();
                ((ArrayInfo) info).indexType = null;
                ((ArrayInfo) info).elementType = null;
                ((ArrayInfo) info).elementCount = 0;
                break;

            case RECORD:
                info = new RecordInfo();
                ((RecordInfo) info).typePath = null;
                ((RecordInfo) info).symtab = null;
                break;
                
            default: break;
        }
    }
    
    /**
     * Determine whether or not the type is structured (array or record).
     * @return true if structured, false if not.
     */
    public boolean isStructured() 
    { 
        return (form == Form.ARRAY) || (form == Form.RECORD);
    }

    /**
     * Get the type form.
     * @return the form.
     */
    public Form getForm() { return form; }

    /**
     * Get the type identifier.
     * @return the identifier's symbol table entry.
     */
    public SymtabEntry getIdentifier() { return identifier; }

    /**
     * Setter.
     * @param identifier the type identifier (symbol table entry).
     */
    public void setIdentifier(SymtabEntry identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Get the base type of this type.
     * @return the base type.
     */
    public Typespec baseType()
    {
        return form == Form.SUBRANGE ? ((SubrangeInfo) info).baseType : this;
    }

    /**
     * Get the subrange base type.
     * @return the base type.
     */
    public Typespec getSubrangeBaseType() 
    { 
        return ((SubrangeInfo) info).baseType; 
    }

    /**
     * Set the subrange base type.
     * @param baseType the base type to set.
     */
    public void setSubrangeBaseType(Typespec baseType)
    {
        ((SubrangeInfo) info).baseType = baseType;
    }

    /**
     * Get the subrange minimum value.
     * @return the value.
     */
    public int getSubrangeMinValue() { return ((SubrangeInfo) info).minValue; }

    /**
     * Get the subrange maximum value.
     * @return the value.
     */
    public int getSubrangeMaxValue() { return ((SubrangeInfo) info).maxValue; }

    /**
     * Set the subrange minimum value.
     * @param minValue the value to set.
     */
    public void setSubrangeMinValue(int minValue)
    {
        ((SubrangeInfo) info).minValue = minValue;
    }

    /**
     * Set the subrange maximum value.
     * @param maxValue the value to set.
     */
    public void setSubrangeMaxValue(int maxValue)
    {
        ((SubrangeInfo) info).maxValue = maxValue;
    }

    /**
     * Get the arraylist of symbol table entries of enumeration constants.
     * @return the arraylist.
     */
    public ArrayList<SymtabEntry> getEnumerationConstants()
    {
        return ((EnumerationInfo) info).constants;
    }

    /**
     * Set the vector of enumeration constants symbol table entries.
     * @parm constants the vector to set.
     */
    public void setEnumerationConstants(ArrayList<SymtabEntry> constants)
    {
        ((EnumerationInfo) info).constants = constants;
    }

    /**
     * Get the array index data type.
     * @return the data type.
     */
    public Typespec getArrayIndexType()
    {
        return ((ArrayInfo) info).indexType;
    }

    /**
     * Set the array index data type.
     * @parm index_type the data type to set.
     */
    public void setArrayIndexType(Typespec indexType)
    {
        ((ArrayInfo) info).indexType = indexType;
    }

    /**
     * Get the array element data type.
     * @return the data type.
     */
    public Typespec getArrayElementType()
    {
        return ((ArrayInfo) info).elementType;
    }

    /**
     * Set the array element data type.
     * @return elmt_type the data type to set.
     */
    public void setArrayElementType(Typespec elementType)
    {
        ((ArrayInfo) info).elementType = elementType;
    }

    /**
     * Get the array element count.
     * @return the count.
     */
    public int getArrayElementCount() { return ((ArrayInfo) info).elementCount; }

    /**
     * Set the array element count.
     * @parm elmt_count the count to set.
     */
    public void setArrayElementCount(int elementCount)
    {
        ((ArrayInfo) info).elementCount = elementCount;
    }
    
    /**
     * Get the base type of an array.
     * @return the base type of its final dimension.
     */
    public Typespec getArrayBaseType()
    {
        Typespec elmtType = this;
        
        while (elmtType.form == Form.ARRAY)
        {
            elmtType = elmtType.getArrayElementType();
        }
        
        return elmtType.baseType();
    }

    /**
     * Get the record's symbol table.
     * @return the symbol table.
     */
    public Symtab getRecordSymtab() { return ((RecordInfo) info).symtab; }

    /**
     * Set the record's symbol table.
     * @parm symtab the symbol table to set.
     */
    public void setRecordSymtab(Symtab symtab)
    {
        ((RecordInfo) info).symtab = symtab;
    }
    
    /**
     * Get a record type's fully qualified type path.
     * @return the path.
     */
    public String getRecordTypePath() { return ((RecordInfo) info).typePath; }
    
    /**
     * Set a record type's fully qualified type path.
     * @param typePath the path to set.
     */
    public void setRecordTypePath(String typePath) 
    { 
        ((RecordInfo) info).typePath = typePath; 
    }
}
