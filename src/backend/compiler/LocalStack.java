package backend.compiler;

/**
 * <h1>LocalStack</h1>
 *
 * <p>Maintain a method's local runtime stack.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class LocalStack
{
    private int size;     // current stack size
    private int maxSize;  // maximum attained stack size

    /**
     * Constructor
     */
    public LocalStack() { reset(); }
    
    /**
     * Reset the state of the local stack.
     */
    public void reset()
    {
        this.size = 0;
        this.maxSize = 0;
    }

    /**
     * Get the current stack size.
     * @return the size.
     */
    public int getSize() { return this.size; }

    /**
     * Increase the stack size by a given amount.
     * @param amount the amount to increase.
     */
    public void increase(int amount)
    {
        size += amount;
        maxSize = Math.max(maxSize, size);
    }

    /**
     * Decrease the stack size by a given amount.
     * @param amount the amount to decrease.
     */
    public void decrease(int amount) 
    { 
        size -= amount; 
        if (size < 0) size = 0;
    }

    /**
     * Return the maximum attained stack size.
     * @return the maximum size.
     */
    public int capacity() { return maxSize; }
}
