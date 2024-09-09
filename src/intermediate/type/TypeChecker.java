package intermediate.type;

import static intermediate.type.Typespec.Form.*;

import intermediate.symtab.*;
import intermediate.type.Typespec.Form;

/**
 * <h1>TypeChecker</h1>
 *
 * <p>Perform type checking.</p>
 *
 * <p>Copyright (c) 2020 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TypeChecker
{
    /**
     * Check if a type specification is integer.
     * @param type the type specification to check.
     * @return true if integer, else false.
     */
    public static boolean isNumber(Typespec type)
    {
        return (type != null) && (type.baseType() == Predefined.numberType);
    }

    /**
     * Check if both type specifications are integer.
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if both are integer, else false.
     */
    public static boolean areBothNumber(Typespec type1, Typespec type2)
    {
        return isNumber(type1) && isNumber(type2);
    }

    /**
     * Check if a type specification is boolean.
     * @param type the type specification to check.
     * @return true if boolean, else false.
     */
    public static boolean isBoolean(Typespec type)
    {
        return (type != null) && (type.baseType() == Predefined.booleanType);
    }

    /**
     * Check if both type specifications are boolean.
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if both are boolean, else false.
     */
    public static boolean areBothBoolean(Typespec type1, Typespec type2)
    {
        return isBoolean(type1) && isBoolean(type2);
    }

//    /**
//     * Check if a type specification is char.
//     * @param type the type specification to check.
//     * @return true if char, else false.
//     */
//    public static boolean isChar(Typespec type)
//    {
//        return (type != null) && (type.baseType() == Predefined.charType);
//    }
    
    /**
     * Check if a type specification is string.
     * @param type the type specification to check.
     * @return true if integer, else false.
     */
    public static boolean isString(Typespec type)
    {
        return (type != null) && (type.baseType() == Predefined.stringType);
    }

    /**
     * Check if both type specifications are string.
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if both are integer, else false.
     */
    public static boolean areBothString(Typespec type1, Typespec type2)
    {
        return isString(type1) && isString(type2);
    }

    /**
     * Check if two type specifications are assignment compatible.
     * @param targetType the target type specification.
     * @param valueType the value type specification.
     * @return true if the value can be assigned to the target, else false.
     */
    public static boolean areAssignmentCompatible(Typespec targetType,
                                                  Typespec valueType)
    {
        if ((targetType == null) || (valueType == null)) return false;

        targetType = targetType.baseType();
        valueType  = valueType.baseType();

        boolean compatible = false;

        // Identical types.
        if (targetType == valueType) compatible = true;

        return compatible;
    }

    /**
     * Check if two type specifications are comparison compatible.
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if the types can be compared to each other, else false.
     */
    public static boolean areComparisonCompatible(Typespec type1,
                                                  Typespec type2)
    {
        if ((type1 == null) || (type2 == null)) return false;

        type1 = type1.baseType();
        type2 = type2.baseType();
        Form form = type1.getForm();

        boolean compatible = false;

        // Two identical scalar or enumeration types.
        if (   (type1 == type2)
            && ((form == SCALAR) || (form == ENUMERATION))) 
        {
            compatible = true;
        }

        return compatible;
    }
}
