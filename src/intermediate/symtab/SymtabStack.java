package intermediate.symtab;

import java.util.ArrayList;

import intermediate.symtab.SymtabEntry.Kind;

/**
 * <h1>SymtabStack</h1>
 *
 * <p>The symbol table stack.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SymtabStack
    extends ArrayList<Symtab>
{
    private static final long serialVersionUID = 0L; 

    private int currentNestingLevel;  // current scope nesting level
    private SymtabEntry programId;    // entry for the main program id

    /**
     * Constructor.
     */
    public SymtabStack()
    {
        this.currentNestingLevel = 0;
        add(new Symtab(currentNestingLevel));
    }

    /**
     * Getter.
     * @return the current nesting level.
     */
    public int getCurrentNestingLevel() { return currentNestingLevel;  }

    /**
     * Getter.
     * @return the symbol table entry for the main program identifier.
     */
    public SymtabEntry getProgramId() { return programId; }

    /**
     * Setter.
     * @param entry the symbol table entry for the main program identifier.
     */
    public void setProgramId(SymtabEntry id) { this.programId = id; }

    /**
     * Return the local symbol table which is at the top of the stack.
     * @return the local symbol table.
     */
    public Symtab getLocalSymtab() { return get(currentNestingLevel); }

    /**
     * Push a new symbol table onto the symbol table stack.
     * @return the pushed symbol table.
     */
    public Symtab push()
    {
        Symtab symtab = new Symtab(++currentNestingLevel);
        add(symtab);

        return symtab;
    }

    /**
     * Push a symbol table onto the symbol table stack.
     * @return the pushed symbol table.
     */
    public Symtab push(Symtab symtab)
    {
        ++currentNestingLevel;
        add(symtab);

        return symtab;
    }

    /**
     * Pop a symbol table off the symbol table stack.
     * @return the popped symbol table.
     */
    public Symtab pop()
    {
        Symtab symtab = get(currentNestingLevel);
        remove(currentNestingLevel--);

        return symtab;
    }

    /**
     * Create and enter a new entry into the local symbol table.
     * @param name the name of the entry.
     * @param kind what kind of entry.
     * @return the new entry.
     */
    public SymtabEntry enterLocal(String name, Kind kind)
    {
        return get(currentNestingLevel).enter(name, kind);
    }

    /**
     * Look up an existing symbol table entry in the local symbol table.
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    public SymtabEntry lookupLocal(String name)
    {
        return get(currentNestingLevel).lookup(name);
    }

    /**
     * Look up an existing symbol table entry throughout the stack.
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    public SymtabEntry lookup(String name)
    {
        SymtabEntry foundEntry = null;

        // Search the current and enclosing scopes.
        for (int i = currentNestingLevel; (i >= 0) && (foundEntry == null); --i)
        {
            foundEntry = get(i).lookup(name);
        }

        return foundEntry;
    }
}
