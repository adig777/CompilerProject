package backend.compiler;

/**
 * <h1>Label</h1>
 *
 * <p>Jasmin instruction label.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class Label
{
    private static int index = 0;  // index for generating label strings
    private String label;          // the label string

    /**
     * Constructor.
     */
    public Label() { this.label = "L" + String.format("%03d", ++index); }

    /**
     * Generate the label string. 
     * @return the string.
     */
    public String toString() { return this.label; }
}
