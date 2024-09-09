package intermediate.symtab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import intermediate.symtab.SymtabEntry.Kind;

import static intermediate.symtab.SymtabEntry.Kind.*;

/**
 * <h1>Symtab</h1>
 *
 * <p>The symbol table.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Symtab
    extends TreeMap<String, SymtabEntry>
{
    private static final long serialVersionUID = 0L; 

    private int nestingLevel;       // scope nesting level
    private int slotNumber;         // local variables array slot number
    private int maxSlotNumber;      // max slot number value
    private SymtabEntry ownerId;    // symbol table entry of this symtab's owner
    
    public static final String UNNAMED_PREFIX = "_unnamed_";
    private static int unnamedIndex = 0;

    /**
     * Generate a name for an unnamed type.
     * @return the name;
     */
    public static String generateUnnamedName()
    {
        unnamedIndex++;
        return UNNAMED_PREFIX + unnamedIndex;
    }

    /**
     * Constructor.
     * @param nestingLevel the symbol table's nesting level.
     */
    public Symtab(int nestingLevel)
    {
        this.nestingLevel = nestingLevel;
        this.slotNumber   = -1;
    }

    /**
     * Get the scope nesting level.
     * @return the nesting level.
     */
    public int getNestingLevel() { return nestingLevel; }

    /**
     * Get the maximum local variables array slot number.
     * @return the maximum slot number.
     */
    public int getMaxSlotNumber() { return maxSlotNumber; }

    /**
     * Compute and return the next local variables array slot number
     * @return the slot number.
     */
    public int nextSlotNumber()
    {
        maxSlotNumber = ++slotNumber;
        return slotNumber;
    }

    /**
     * Getter.
     * @return the owner of this symbol table.
     */
    public SymtabEntry getOwner() { return ownerId; }
    
    /**
     * Set the owner of this symbol table.
     * @param ownerId the symbol table entry of the owner.
     */
    public void setOwner(SymtabEntry ownerId) { this.ownerId = ownerId; }

    /**
     * Create and enter a new entry into the symbol table.
     * @param name the name of the entry.
     * @param kind the kind of entry.
     * @return the new entry.
     */
    public SymtabEntry enter(String name, Kind kind)
    {
        SymtabEntry entry = new SymtabEntry(name, kind, this);
        put(name, entry);

        return entry;
    }

    /**
     * Look up an existing symbol table entry.
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    public SymtabEntry lookup(String name) { return get(name); }

    /**
     * Return an arraylist of entries sorted by name.
     * @return the sorted arraylist.
     */
    public ArrayList<SymtabEntry> sortedEntries()
    {
        Collection<SymtabEntry> entries = values();
        Iterator<SymtabEntry> iter = entries.iterator();
        ArrayList<SymtabEntry> list = new ArrayList<SymtabEntry>(size());

        // Iterate over the sorted entries and append them to the list.
        while (iter.hasNext()) list.add(iter.next());

        return list;  // sorted list of entries
    }
    
    /**
     * Reset all the variable entries to a kind.
     * @param kind the kind to set.
     */
    public void resetVariables(Kind kind)
    {
        Collection<SymtabEntry> entries = values();
        Iterator<SymtabEntry> it = entries.iterator();

        // Iterate over the entries and reset their kind.
        while (it.hasNext()) 
        {
            SymtabEntry entry = it.next();
            if (entry.getKind() == VARIABLE) entry.setKind(kind);
        }
    }
}
