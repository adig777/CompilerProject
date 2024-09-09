package intermediate.util;

import java.util.ArrayList;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;
import intermediate.type.Typespec.Form;

import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.type.Typespec.Form.*;

/**
 * <h1>CrossReferencer</h1>
 *
 * <p>Generate a cross-reference listing.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CrossReferencer
{
    private static final int NAME_WIDTH = 16;

    private static final String NAME_FORMAT       = "%-" + NAME_WIDTH + "s";
    private static final String NUMBERS_LABEL     = " Line numbers    ";
    private static final String NUMBERS_UNDERLINE = " ------------    ";
    private static final String NUMBER_FORMAT = " %03d";

    private static final int LABEL_WIDTH  = NUMBERS_LABEL.length();
    private static final int INDENT_WIDTH = NAME_WIDTH + LABEL_WIDTH;

    private static final StringBuilder INDENT = new StringBuilder(INDENT_WIDTH);
    static {
        for (int i = 0; i < INDENT_WIDTH; ++i) INDENT.append(" ");
    }

    /**
     * Print the cross-reference table.
     * @param symtabStack the symbol table stack.
     */
    public void print(SymtabStack symtabStack)
    {
        System.out.println("\n===== CROSS-REFERENCE TABLE =====");

        SymtabEntry programId = symtabStack.getProgramId();
        printRoutine(programId);
    }

    /**
     * Print a cross-reference table for a routine.
     * @param routineId the routine identifier's symbol table entry.
     */
    private void printRoutine(SymtabEntry routineId)
    {
        Kind kind = routineId.getKind();
        System.out.println("\n*** " + kind.toString().toUpperCase() +
                           " " + routineId.getName() + " ***");
        printColumnHeadings();

        // Print the entries in the routine's symbol table.
        Symtab symtab = routineId.getRoutineSymtab();
        printSymtab(symtab);

        // Print any procedures and functions defined in the routine.
        ArrayList<SymtabEntry> subroutineIds = routineId.getSubroutines();
        if (subroutineIds != null) 
        {
            for (SymtabEntry rtnId : subroutineIds) printRoutine(rtnId);
        }
    }

    /**
     * Print column headings.
     */
    private void printColumnHeadings()
    {
        System.out.println();
        System.out.println(String.format(NAME_FORMAT, "Identifier")
                           + NUMBERS_LABEL +     "Type specification");
        System.out.println(String.format(NAME_FORMAT, "----------")
                           + NUMBERS_UNDERLINE + "------------------");
    }

    /**
     * Print the entries in a symbol table.
     * @param symtab the symbol table.
     * @param recordTypes the list to fill with RECORD type specifications.
     */
    private void printSymtab(Symtab symtab)
    {
        ArrayList<SymtabEntry> sorted = symtab.sortedEntries();

        // Loop over the sorted list of table entries
        // to print each entry of this symbol table.
        for (SymtabEntry entry : sorted) 
        {
            ArrayList<Integer> lineNumbers = entry.getLineNumbers();

            // For each entry, print the identifier name
            // followed by the line numbers.
            System.out.print(String.format(NAME_FORMAT, entry.getName()));
            if (lineNumbers != null) 
            {
                for (Integer lineNumber : lineNumbers) 
                {
                    System.out.print(String.format(NUMBER_FORMAT, lineNumber));
                }
            }

            // Print the symbol table entry.
            System.out.println();
            printEntry(entry);
        }
        
        // Loop over the sorted list of entries again
        // to print each nested record's symbol table.
        for (SymtabEntry entry : sorted) 
        {
            if (entry.getKind() == TYPE)
            {
                Typespec type = entry.getType();
                if (type.getForm() == RECORD) printRecord(type);
            }
        }
    }

    /**
     * Print a symbol table entry.
     * @param entry the symbol table entry.
     */
    private void printEntry(SymtabEntry entry)
    {
        Kind kind = entry.getKind();
        int nestingLevel = entry.getSymtab().getNestingLevel();
        System.out.println(INDENT + "Kind: " + kind.toString().replace("_", " "));
        System.out.println(INDENT + "Scope nesting level: " + nestingLevel);

        // Print the type specification.
        Typespec type = entry.getType();
        printType(type);

        switch (kind) 
        {
            case CONSTANT: 
            {
                Object value = entry.getValue();
                System.out.println(INDENT + "Value: " + toString(value, type));

                // Print the type details only if the type is unnamed.
                if (type.getIdentifier() == null) 
                {
                    printTypeDetail(type);
                }

                break;
            }

            case ENUMERATION_CONSTANT: 
            {
                Object value = entry.getValue();
                System.out.println(INDENT + "Value: " + toString(value, type));

                break;
            }

            case TYPE: 
            {
                // Print the type details only when the type is first defined.
                if (entry == type.getIdentifier()) 
                {
                    printTypeDetail(type);
                }

                break;
            }

            case VARIABLE: 
            {
                // Print the type details only if the type is unnamed.
                if (type.getIdentifier() == null) 
                {
                    printTypeDetail(type);
                }

                break;
            }
            
            case RECORD_FIELD:
            {
                printTypeDetail(type);
                break;
            }
            
            default: break;
        }
    }

    /**
     * Print a type specification.
     * @param type the type specification.
     */
    private void printType(Typespec type)
    {
        if (type != null) 
        {
            Form form = type.getForm();
            SymtabEntry typeId = type.getIdentifier();
            String typeName = typeId != null ? typeId.getName() : "<unnamed>";

            System.out.println(INDENT + "Type form: " + form +
                               ", Type id: " + typeName);
        }
    }

    private static final String ENUM_CONST_FORMAT = "%" + NAME_WIDTH + "s = %s";

    /**
     * Print the details of a type specification.
     * @param type the type specification.
     */
    private void printTypeDetail(Typespec type)
    {
        Form form = type.getForm();

        switch (form) 
        {
            case ENUMERATION: 
            {
                ArrayList<SymtabEntry> constantIds = 
                                                type.getEnumerationConstants();

                System.out.println(INDENT + "--- Enumeration constants ---");

                // Print each enumeration constant and its value.
                for (SymtabEntry constantId : constantIds) 
                {
                    String name = constantId.getName();
                    Object value = constantId.getValue();

                    System.out.println(INDENT + String.format(ENUM_CONST_FORMAT,
                                                              name, value));
                }

                break;
            }

            case SUBRANGE: 
            {
                Object minValue = type.getSubrangeMinValue();
                Object maxValue = type.getSubrangeMaxValue();
                Typespec baseType = type.baseType();

                System.out.println(INDENT + "--- Base type ---");
                printType(baseType);

                // Print the base type details only if the type is unnamed.
                if (baseType.getIdentifier() == null) 
                {
                    printTypeDetail(baseType);
                }

                System.out.print(INDENT + "Range: ");
                System.out.println(toString(minValue, baseType) + ".." +
                                   toString(maxValue, baseType));

                break;
            }

            case ARRAY: 
            {
                Typespec indexType = type.getArrayIndexType();
                Typespec elementType = type.getArrayElementType();
                int count = type.getArrayElementCount();

                System.out.println(INDENT + "--- INDEX TYPE ---");
                printType(indexType);

                // Print the index type details only if the type is unnamed.
                if (indexType.getIdentifier() == null) 
                {
                    printTypeDetail(indexType);
                }

                System.out.println(INDENT + "--- ELEMENT TYPE ---");
                printType(elementType);
                System.out.println(INDENT.toString() + count + " elements");

                // Print the element type details only if the type is unnamed.
                if (elementType.getIdentifier() == null) 
                {
                    printTypeDetail(elementType);
                }

                break;
            }
            
            default: break;
        }
    }

    /**
     * Print the cross-reference table for a record type.
     * @param recordType the RECORD type specification.
     */
    private void printRecord(Typespec recordType)
    {
        SymtabEntry recordId = recordType.getIdentifier();
        String name = recordId != null ? recordId.getName() : "<unnamed>";

        System.out.println("\n--- RECORD " + name + " ---");
        printColumnHeadings();

        // Print the entries in the record's symbol table.
        Symtab symtab = recordType.getRecordSymtab();
        printSymtab(symtab);
    }

    /**
     * Convert a value to a string.
     * @param value the value.
     * @param type the value's datatype.
     * @return the string.
     */
    private String toString(Object value, Typespec type)
    {
        return type == Predefined.stringType ? "'" + (String) value + "'"
                                             : value.toString();
    }
}
