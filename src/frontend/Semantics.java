package frontend;

import java.util.ArrayList;
import java.util.HashSet;

import antlr4.*;

import intermediate.symtab.*;
import intermediate.symtab.SymtabEntry.Kind;
import intermediate.type.*;
import intermediate.type.Typespec.*;
import intermediate.util.*;

import static frontend.SemanticErrorHandler.Code.*;
import static intermediate.symtab.SymtabEntry.Kind.*;
import static intermediate.symtab.SymtabEntry.Routine.*;
import static intermediate.type.Typespec.Form.*;
import static intermediate.util.BackendMode.*;

/**
 * Semantic operations.
 * Perform type checking and create symbol tables.
 */
public class Semantics extends AKABaseVisitor<Object>
{
    private BackendMode mode;
    private SymtabStack symtabStack;
    private SymtabEntry programId;
    private SemanticErrorHandler error;
    
    public Semantics(BackendMode mode)
    {
        // Create and initialize the symbol table stack.
        this.symtabStack = new SymtabStack();
        Predefined.initialize(symtabStack);
        
        this.mode = mode;
        this.error = new SemanticErrorHandler();
    }
    
    public SymtabEntry getProgramId() { return programId; }
    public int getErrorCount() { return error.getCount(); };
    
    /**
     * Return the default value for a data type.
     * @param type the data type.
     * @return the default value.
     */
    public static Object defaultValue(Typespec type)
    {
        type = type.baseType();

        if      (type == Predefined.numberType) return Float.valueOf(0.0f);
        else if (type == Predefined.booleanType) return Boolean.valueOf(false);
        else /* string */                        return String.valueOf("#");
    }

    
    
    //_________________________________________________________________
    
    
    
    @Override 
    public Object visitProgram(AKAParser.ProgramContext ctx) {
    	AKAParser.ProgramIdentifierContext idCtx = ctx.programIdentifier();
    	String programName = idCtx.IDENTIFIER().getText();
    	
    	programId = symtabStack.enterLocal(programName, PROGRAM);
        programId.setRoutineSymtab(symtabStack.push());
        
        symtabStack.setProgramId(programId);
        symtabStack.getLocalSymtab().setOwner(programId);
        idCtx.entry = programId;
    	
        if (ctx.funcblock() != null) {
        	visit(ctx.funcblock());
        }
    	visit(ctx.mainblock());
    	
    	CrossReferencer crossReferencer = new CrossReferencer();
        crossReferencer.print(symtabStack);
    	return null; 
    }
	
	@Override 
	public Object visitAssignment(AKAParser.AssignmentContext ctx) { 
		AKAParser.VarTypeContext typeCtx = ctx.varType();
		AKAParser.LhsContext lhsCtx = ctx.lhs();
        AKAParser.RhsContext rhsCtx = ctx.rhs();
        
        
        Typespec lhsType;
        Typespec rhsType;
        if (typeCtx != null) {
        	SymtabEntry varSE;
        	if (typeCtx.NUMBER() != null) {
        		
        		SymtabEntry dup =symtabStack.lookupLocal(lhsCtx.getText());
            	if (dup != null) {
            		error.flag(REDECLARED_IDENTIFIER, lhsCtx);
            	}
        		//asdf
        		lhsType = Predefined.numberType;
        		varSE = symtabStack.enterLocal(lhsCtx.getText(), VARIABLE);
        		varSE.setType(lhsType);
        		visitChildren(ctx);
        		//visit(rhsCtx);
        		rhsType = rhsCtx.declaration().expression().type;
        		//varSE.setValue(rhsCtx.value);
        		Symtab symtab = symtabStack.getLocalSymtab();
        		varSE.setSlotNumber(symtab.nextSlotNumber());
        	} else if (typeCtx.BOOL() != null) {
        		
        		SymtabEntry dup =symtabStack.lookupLocal(lhsCtx.getText());
            	if (dup != null) {
            		error.flag(REDECLARED_IDENTIFIER, lhsCtx);
            	}
        		
        		lhsType = Predefined.booleanType;
        		symtabStack.enterLocal(lhsCtx.getText(), VARIABLE);
        		varSE = symtabStack.enterLocal(lhsCtx.getText(), VARIABLE);
        		varSE.setType(lhsType);
        		visitChildren(ctx);
        		rhsType = rhsCtx.declaration().expression().type;
        	} else {
        		
        		SymtabEntry dup =symtabStack.lookupLocal(lhsCtx.getText());
            	if (dup != null) {
            		error.flag(REDECLARED_IDENTIFIER, lhsCtx);
            	}
        		
        		lhsType = Predefined.stringType;
        		symtabStack.enterLocal(lhsCtx.getText(), VARIABLE);
        		varSE = symtabStack.enterLocal(lhsCtx.getText(), VARIABLE);
        		varSE.setType(lhsType);
        		visitChildren(ctx);
        		rhsType = rhsCtx.declaration().expression().type;
        		Symtab symtab = symtabStack.getLocalSymtab();
        		varSE.setSlotNumber(symtab.nextSlotNumber());
        	}
        } else {
        	
        	visitChildren(ctx);
            
            lhsType = lhsCtx.variable().type;
            rhsType = rhsCtx.declaration().expression().type;
        }
        
        if (!TypeChecker.areAssignmentCompatible(lhsType, rhsType))
        {
            error.flag(INCOMPATIBLE_ASSIGNMENT, rhsCtx);
        }
        
        return null;
	}
	
	
	@Override 
	public Object visitLhs(AKAParser.LhsContext ctx) { 
		AKAParser.VariableContext varCtx = ctx.variable();
        visit(varCtx);
        ctx.type = varCtx.type;
        
        return null;
	}
	
	@Override 
	public Object visitRhs(AKAParser.RhsContext ctx) { 
		AKAParser.DeclarationContext decCtx = ctx.declaration();
        visit(decCtx);
        
        return null;
	}
	
	
	@Override 
	public Object visitDeclaration(AKAParser.DeclarationContext ctx) {
		AKAParser.ExpressionContext exprCtx = ctx.expression();
		visit(exprCtx);
		return null;
	}
	
	
	@Override 
	public Object visitIfStatement(AKAParser.IfStatementContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override 
	public Object visitIfBlock(AKAParser.IfBlockContext ctx) {
		AKAParser.ExpressionContext exprCtx = ctx.condition().expression();
		visit(exprCtx);
		Typespec exprType = exprCtx.type;
		
		if(!TypeChecker.isBoolean(exprType))
		{
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
		
		visitChildren(ctx);
		
		return null;
	}

	
	@Override 
	public Object visitElseifBlock(AKAParser.ElseifBlockContext ctx) { 
		AKAParser.ExpressionContext exprCtx = ctx.condition().expression();
		visit(exprCtx);
		Typespec exprType = exprCtx.type;
		
		if(!TypeChecker.isBoolean(exprType))
		{
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
		
		visitChildren(ctx);
		
		return null;
	}

	
	@Override 
	public Object visitElseBlock(AKAParser.ElseBlockContext ctx) { 
		visitChildren(ctx);
		
		return null;
	}
	
	
	@Override 
	public Object visitCondition(AKAParser.ConditionContext ctx) {
		AKAParser.ExpressionContext expCtx = ctx.expression();
		visit(expCtx);
		return null; 
	}
	
	
	@Override 
	public Object visitWhileStatement(AKAParser.WhileStatementContext ctx) { 
		AKAParser.ExpressionContext exprCtx = ctx.condition().expression();
        visit(exprCtx);
        Typespec exprType = exprCtx.type;
        
        if (!TypeChecker.isBoolean(exprType))
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, exprCtx);
        }
        
        visit(ctx.statementList());
        return null;
	}
	
	
	@Override 
	public Object visitGuard(AKAParser.GuardContext ctx) {
		AKAParser.ParamListContext paramListCtx = ctx.paramList();
		visit(paramListCtx);
		for (AKAParser.ConditionContext cdtCtx : paramListCtx.condition()) {
			Typespec exprType = cdtCtx.expression().type;
			if (!TypeChecker.isBoolean(exprType))
	        {
	            error.flag(TYPE_MUST_BE_BOOLEAN, cdtCtx.expression());
	        }
		}
		
		
		visit(ctx.statementList());
		
		return null;
	}
	
	
	@Override 
	public Object visitDefCall(AKAParser.DefCallContext ctx) { 
		AKAParser.DefNameContext nameCtx = ctx.defName();
		AKAParser.ArgumentListContext listCtx = ctx.argumentList();
        String name = ctx.defName().getText().toLowerCase();
        SymtabEntry procedureId = symtabStack.lookup(name);
        boolean badName = false;
        
        if (procedureId == null)
        {
            error.flag(UNDECLARED_IDENTIFIER, nameCtx);
            badName = true;
        }
        else if (procedureId.getKind() != DEFINITIONNORETURN)
        {
            error.flag(NAME_MUST_BE_DEFINITIONNORETURN, nameCtx);
            badName = true;
        }
        
        // Bad procedure name. Do a simple arguments check and then leave.
        if (badName)
        {
            for (AKAParser.ArgumentContext exprCtx : listCtx.argument())
            {
                visit(exprCtx);
            }
        }
        
        // Good procedure name.
        else
        {
            ArrayList<SymtabEntry> parms = procedureId.getRoutineParameters();
            checkCallArguments(listCtx, parms);
        }
        
        nameCtx.entry = procedureId;
        return null;
	}
	

	@Override 
	public Object visitVarList(AKAParser.VarListContext ctx) {
		Kind kind = VALUE_PARAMETER;
		ArrayList<SymtabEntry> parameterList = new ArrayList<>();
		// Loop over the parameter declarations.
		int i = 0;
        for (AKAParser.VariableContext varCtx : 
                                                    ctx.variable())
        {
        	int lineNumber = varCtx.getStart().getLine();   
            String varName = varCtx.variableIdentifier().IDENTIFIER().getText().toLowerCase();
            SymtabEntry varId = symtabStack.lookupLocal(varName);
            
            if (varId == null) 
        	{
        		varId = symtabStack.enterLocal(varName, kind);
        	} else {
        		error.flag(REDECLARED_IDENTIFIER, varCtx.variableIdentifier());
        	}
            
        	AKAParser.VarTypeContext varType = ctx.varType(i);
        	if (varType.NUMBER() != null) {
        		varId.setType(Predefined.numberType);
        	} else if (varType.STRING() != null) {
        		varId.setType(Predefined.stringType);
        	} else if (varType.BOOL() != null) {
        		varId.setType(Predefined.booleanType);
        	}
        	
        	varCtx.entry = varId;
            varCtx.type  = varId.getType();
            
            parameterList.add(varId);
            varId.appendLineNumber(lineNumber);    
        	i++;
        }
        
        return parameterList;
		
		
	}
	
	
	@Override 
	@SuppressWarnings("unchecked")
	public Object visitDefinition(AKAParser.DefinitionContext ctx) { 
	
        AKAParser.DefNameContext defNameCtx = ctx.defName();
        AKAParser.VarListContext params = null;
        Typespec returnType = null;
        String routineName;
        
        params = ctx.varList();
        
        
        routineName = defNameCtx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry routineId = symtabStack.lookupLocal(routineName);
        
        if (routineId != null)
        {
            error.flag(REDECLARED_IDENTIFIER, 
                       ctx.getStart().getLine(), routineName);
            return null;
        }

        routineId = symtabStack.enterLocal(routineName, DEFINITION);
        routineId.setRoutineCode(DECLARED);
        defNameCtx.entry = routineId;
        
        // Append to the parent routine's list of subroutines.
        SymtabEntry parentId = symtabStack.getLocalSymtab().getOwner();
        parentId.appendSubroutine(routineId);
        
        routineId.setRoutineSymtab(symtabStack.push());
        defNameCtx.entry = routineId;
        
        Symtab symtab = symtabStack.getLocalSymtab();
        symtab.setOwner(routineId);
        
        if (params != null)
        {
            ArrayList<SymtabEntry> parameterIds = (ArrayList<SymtabEntry>) 
                                visit(params);
            routineId.setRoutineParameters(parameterIds);
            
            for (SymtabEntry parmId : parameterIds)
            {
                parmId.setSlotNumber(symtab.nextSlotNumber());
            }
        }
        
        
        AKAParser.VarTypeContext returnTypeCtx = ctx.varType();
        AKAParser.VariableContext returnValueCtx;
        if (returnTypeCtx != null) {
        	if (returnTypeCtx.NUMBER() != null) {
        		returnValueCtx = ctx.variable();
        		String returnName = returnValueCtx.getText().toLowerCase();
                SymtabEntry returnId = symtabStack.lookupLocal(returnName);
                
                if (returnId == null) 
            	{
                	returnId = symtabStack.enterLocal(returnName, VALUE_PARAMETER);
                	returnId.setSlotNumber(symtab.nextSlotNumber());
            	} else {
            		error.flag(REDECLARED_IDENTIFIER, returnValueCtx.variableIdentifier());
            	}
                visit(returnValueCtx);
                returnType = Predefined.numberType;
                returnId.setType(returnType);
        	} else if (returnTypeCtx.BOOL() != null) {
        		returnValueCtx = ctx.variable();
        		String returnName = returnValueCtx.getText().toLowerCase();
                SymtabEntry returnId = symtabStack.lookupLocal(returnName);
                
                if (returnId == null) 
            	{
                	returnId = symtabStack.enterLocal(returnName, VALUE_PARAMETER);
                	returnId.setSlotNumber(symtab.nextSlotNumber());
            	} else {
            		error.flag(REDECLARED_IDENTIFIER, returnValueCtx.variableIdentifier());
            	}
                visit(returnValueCtx);
                returnType = Predefined.booleanType;
                returnId.setType(returnType);
        	} else {
        		returnValueCtx = ctx.variable();
        		String returnName = returnValueCtx.getText().toLowerCase();
                SymtabEntry returnId = symtabStack.lookupLocal(returnName);
                
                if (returnId == null) 
            	{
                	returnId = symtabStack.enterLocal(returnName, VALUE_PARAMETER);
                	returnId.setSlotNumber(symtab.nextSlotNumber());
            	} else {
            		error.flag(REDECLARED_IDENTIFIER, returnValueCtx.variableIdentifier());
            	}
                visit(returnValueCtx);
                returnType = Predefined.stringType;
                returnId.setType(returnType);
        	}
        } else {
	        returnValueCtx = ctx.variable();
	        visit(returnValueCtx);
	        returnType = returnValueCtx.type;
        }
        
        

        if (returnType.getForm() != SCALAR)
        {
        	error.flag(INVALID_RETURN_TYPE, returnValueCtx.variableIdentifier());
        	returnType = Predefined.numberType;
        }

        routineId.setType(returnType);
        defNameCtx.type = returnType;
        
        
        
        
        SymtabEntry assocVarId = symtabStack.enterLocal(routineName, VARIABLE);
        assocVarId.setSlotNumber(symtab.nextSlotNumber());
        assocVarId.setType(returnType);
        
        if (ctx.statementList() != null) {
        	visit(ctx.statementList());
        	
        	// Make sure return variable is assigned
        	boolean returnVarAssigned = false;
            for (AKAParser.StatementContext stmtCtx :  ctx.statementList().statement())
            {
            	if(stmtCtx.assignment() != null) {
            		String lhsName = stmtCtx.assignment().lhs().getText().toLowerCase();
            		if (lhsName.equals(returnValueCtx.getText().toLowerCase())) {
            			returnVarAssigned = true;
            		}
            	}
            }
            
            if (returnVarAssigned != true) {
            	error.flag(RETURN_VARIABLE_UNINITIALIZED, returnValueCtx.variableIdentifier());
            }
        } else {
        	error.flag(RETURN_VARIABLE_UNINITIALIZED, returnValueCtx.variableIdentifier());
        }
        
        
        routineId.setExecutable(ctx.statementList());
        
        symtabStack.pop();
        
        return null;
		
	}
	
	
	@Override 
	@SuppressWarnings("unchecked")
	public Object visitDefinitionnoreturn(AKAParser.DefinitionnoreturnContext ctx) { 
		 	AKAParser.DefNameContext defNameCtx = ctx.defName();
	        AKAParser.VarListContext params = ctx.varList();
	        String routineName;
	        
	        
	        routineName = defNameCtx.IDENTIFIER().getText().toLowerCase();
	        SymtabEntry routineId = symtabStack.lookupLocal(routineName);
	        
	        if (routineId != null)
	        {
	            error.flag(REDECLARED_IDENTIFIER, 
	                       ctx.getStart().getLine(), routineName);
	            return null;
	        }

	        routineId = symtabStack.enterLocal(routineName, DEFINITIONNORETURN);
	        routineId.setRoutineCode(DECLARED);
	        defNameCtx.entry = routineId;
	        
	        // Append to the parent routine's list of subroutines.
	        SymtabEntry parentId = symtabStack.getLocalSymtab().getOwner();
	        parentId.appendSubroutine(routineId);
	        
	        routineId.setRoutineSymtab(symtabStack.push());
	        defNameCtx.entry = routineId;
	        
	        Symtab symtab = symtabStack.getLocalSymtab();
	        symtab.setOwner(routineId);

	        if (params != null)
	        {
	            ArrayList<SymtabEntry> parameterIds = (ArrayList<SymtabEntry>) 
	                                visit(params);
	            routineId.setRoutineParameters(parameterIds);
	            
	            for (SymtabEntry parmId : parameterIds)
	            {
	                parmId.setSlotNumber(symtab.nextSlotNumber());
	            }
	        }
	        
	        if (ctx.statementList() != null) {
	        	visit(ctx.statementList());
	        }    
	        routineId.setExecutable(ctx.statementList());

	        symtabStack.pop();
	        return null;
	}
	
	
	@Override 
	public Object visitExpression(AKAParser.ExpressionContext ctx) { 
		AKAParser.SimpleExpressionContext simpleCtx1 =
                ctx.simpleExpression().get(0);

		// First simple expression.
		visit(simpleCtx1);

		Typespec simpleType1 = simpleCtx1.type;
		ctx.type = simpleType1;

		AKAParser.RelOperatorContext relOpCtx = ctx.relOperator();

		// Second simple expression?
		if (relOpCtx != null)
		{
			AKAParser.SimpleExpressionContext simpleCtx2 = 
                ctx.simpleExpression().get(1);
			visit(simpleCtx2);
			Typespec simpleType2 = simpleCtx2.type;
			if (simpleType2 == Predefined.booleanType && simpleType1 != Predefined.stringType) {
				simpleCtx1.type = Predefined.booleanType;
				simpleType1 = simpleCtx1.type;
			}
			if (!TypeChecker.areComparisonCompatible(simpleType1, simpleType2))
			{
				error.flag(INCOMPATIBLE_COMPARISON, ctx);
			}

			ctx.type = Predefined.booleanType;
		}

		return null;
	}
	
	
	@Override 
	public Object visitSimpleExpression(AKAParser.SimpleExpressionContext ctx) { 
		int count = ctx.term().size();
        AKAParser.SignContext signCtx = ctx.sign();
        Boolean hasSign = signCtx != null;
        AKAParser.TermContext termCtx1 = ctx.term().get(0);
        
        if (hasSign)
        {
            String sign = signCtx.getText();
            if (sign.equals("+") && sign.equals("-"))
            {
                error.flag(INVALID_SIGN, signCtx);
            }
        }
        
        // First term.
        visit(termCtx1);
        Typespec termType1 = termCtx1.type;        
        
        // Loop over any subsequent terms.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.addOperator().get(i-1).getText().toLowerCase();
            AKAParser.TermContext termCtx2 = ctx.term().get(i);
            visit(termCtx2);
            Typespec termType2 = termCtx2.type;
            
            // Both operands boolean ==> boolean result. Else type mismatch.
            if (op.equals("or"))
            {
            	if (TypeChecker.isString(termType1)) 
            	{
            		error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, termCtx1);
            		termType1 = Predefined.booleanType;
            	}
            	if (TypeChecker.isString(termType2)) 
            	{
            		error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, termCtx2);
            		termType2 = Predefined.booleanType;
            	}
            	if ((TypeChecker.isBoolean(termType1) && TypeChecker.isNumber(termType2))
            		|| (TypeChecker.isBoolean(termType2) && TypeChecker.isNumber(termType1))
            		|| (TypeChecker.isBoolean(termType2) && TypeChecker.isBoolean(termType1))
            		)
                {
                    // do nothing
                }
            	else
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, termCtx2);
                    termType1 = Predefined.booleanType;
                    termType2 = Predefined.booleanType;
                }
                if (hasSign)
                {
                    error.flag(INVALID_SIGN, signCtx);
                }
                
                termType2 = Predefined.booleanType;
            }
            else if (op.equals("+"))
            {
                // Both operands integer ==> integer result
                if (TypeChecker.areBothNumber(termType1, termType2)) 
                {
                    termType2 = Predefined.numberType;
                }
                
                // Both operands string ==> string result
                else if (TypeChecker.areBothString(termType1, termType2))
                {
                    if (hasSign) error.flag(INVALID_SIGN, signCtx);                    
                    termType2 = Predefined.stringType;
                }
                else if(termType1 == Predefined.stringType && termType2 == Predefined.numberType) {
                	if (hasSign) error.flag(INVALID_SIGN, signCtx);
                	termType1 = Predefined.stringType;
                	termType2 = Predefined.stringType;
                }
                else if(termType1 == Predefined.numberType && termType2 == Predefined.stringType) {
                	if (hasSign) error.flag(INVALID_SIGN, signCtx);
                	termType1 = Predefined.stringType;
                	termType2 = Predefined.stringType;
                }
                // Type mismatch.
                else
                {
                    if (!TypeChecker.isNumber(termType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                        termType2 = Predefined.numberType;
                    }
                    if (!TypeChecker.isNumber(termType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx2);
                        termType2 = Predefined.numberType;
                    }
                }
            }
            else  // -
            {
                // Both operands integer ==> integer result
                if (TypeChecker.areBothNumber(termType1, termType2)) 
                {
                    termType2 = Predefined.numberType;
                }
                
                // Type mismatch.
                else
                {
                    if (!TypeChecker.isNumber(termType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx1);
                        termType2 = Predefined.numberType;
                    }
                    if (!TypeChecker.isNumber(termType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, termCtx2);
                        termType2 = Predefined.numberType;
                    }
                }
            }
            
            termType1 = termType2;
        }
        
        ctx.type = termType1;
        return null;
	}
	
	
	@Override 
	public Object visitTerm(AKAParser.TermContext ctx) { 
		int count = ctx.factor().size();
        AKAParser.FactorContext factorCtx1 = ctx.factor().get(0);
        
        // First factor.
        visit(factorCtx1);
        Typespec factorType1 = factorCtx1.type; 
        
        // Loop over any subsequent factors.
        for (int i = 1; i < count; i++)
        {
            String op = ctx.mulOperator().get(i-1).getText().toLowerCase();
            AKAParser.FactorContext factorCtx2 = ctx.factor().get(i);
            visit(factorCtx2);
            Typespec factorType2 = factorCtx2.type;
            
            if (op.equals("*"))
            {
                // Both operands integer  ==> integer result
                if (TypeChecker.areBothNumber(factorType1, factorType2)) 
                {
                    factorType2 = Predefined.numberType;
                }
                
                // Type mismatch.
                else
                {
                    if (!TypeChecker.isNumber(factorType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                        factorType2 = Predefined.numberType;
                    }
                    if (!TypeChecker.isNumber(factorType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                        factorType2 = Predefined.numberType;
                    }
                }
            }
            else if (op.equals("/"))
            {
                // All integer and real operand combinations ==> real result
                if (   TypeChecker.areBothNumber(factorType1, factorType2))
                {
                    factorType2 = Predefined.numberType;
                }
                
                // Type mismatch.
                else 
                {
                    if (!TypeChecker.isNumber(factorType1))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx1);
                        factorType2 = Predefined.numberType;
                    }
                    if (!TypeChecker.isNumber(factorType2))
                    {
                        error.flag(TYPE_MUST_BE_NUMERIC, factorCtx2);
                        factorType2 = Predefined.numberType;
                    }
                }
            }
//            div mod need integers?
//            else if (op.equals("div") || op.equals("mod"))
//            {
//                // Both operands integer ==> integer result. Else type mismatch.
//                if (!TypeChecker.isNumber(factorType1))
//                {
//                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx1);
//                    factorType2 = Predefined.numberType;
//                }
//                if (!TypeChecker.isNumber(factorType2))
//                {
//                    error.flag(TYPE_MUST_BE_INTEGER, factorCtx2);
//                    factorType2 = Predefined.numberType;
//                }
//            }
            else if (op.equals("and"))
            {
                // Both operands boolean ==> boolean result. Else type mismatch.
//                if (!TypeChecker.isBoolean(factorType1) && !TypeChecker.isNumber(factorType2))
//                {
//                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx1);
//                    factorType2 = Predefined.booleanType;
//                }
//                if (!TypeChecker.isBoolean(factorType2) && !TypeChecker.isNumber(factorType1))
//                {
//                    error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx2);
//                    factorType2 = Predefined.booleanType;
//                }
            	if (TypeChecker.isString(factorType1)) 
            	{
            		error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, factorCtx1);
                  factorType1 = Predefined.booleanType;
            	}
            	if (TypeChecker.isString(factorType2)) 
            	{
            		error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, factorCtx2);
                  factorType2 = Predefined.booleanType;
            	}
            	if ((TypeChecker.isBoolean(factorType1) && TypeChecker.isNumber(factorType2))
            		|| (TypeChecker.isNumber(factorType1) && TypeChecker.isBoolean(factorType2))
            		|| (TypeChecker.isBoolean(factorType2) && TypeChecker.isBoolean(factorType1))
            		)
                {
                    factorType2 = Predefined.booleanType;
                }
            	else
                {
                    error.flag(TYPE_MUST_BE_BOOLEAN_OR_NUMERIC, factorCtx2);
                    factorType1 = Predefined.booleanType;
                    factorType2 = Predefined.booleanType;
                }
            }
            
            factorType1 = factorType2;
        }

        ctx.type = factorType1;
        return null;
	}
	
	
	@Override 
	public Object visitVariableFactor(AKAParser.VariableFactorContext ctx) { 
		AKAParser.VariableContext varCtx = ctx.variable();
        visit(varCtx);        
        ctx.type  = varCtx.type;
        
        return null;
	}
	
	
	@Override 
	public Object visitNumberFactor(AKAParser.NumberFactorContext ctx) { 
		AKAParser.NumberConstantContext numberCtx = ctx.numberConstant();
		
		visit(numberCtx);
		
		ctx.type =  Predefined.numberType;
		
		return null;
	}
	
	
	@Override 
	public Object visitStringFactor(AKAParser.StringFactorContext ctx) { 
		ctx.type = Predefined.stringType;
        return null;
	}
	
	
	@Override 
	public Object visitBooleanFactor(AKAParser.BooleanFactorContext ctx) { 
		ctx.type = Predefined.booleanType;
		return null;
	}
	
	
	@Override 
	public Object visitDefCallFactor(AKAParser.DefCallFactorContext ctx) { 
		AKAParser.DefCallContext callCtx = ctx.defCall();
		AKAParser.DefNameContext nameCtx = callCtx.defName();
		AKAParser.ArgumentListContext listCtx = callCtx.argumentList();
        String name = callCtx.defName().getText().toLowerCase();
        SymtabEntry functionId = symtabStack.lookup(name);
        boolean badName = false;
        
        ctx.type = Predefined.numberType;

        if (functionId == null)
        {
            error.flag(UNDECLARED_IDENTIFIER, nameCtx);
            badName = true;
        }
        else if (functionId.getKind() != DEFINITION)
        {
            error.flag(NAME_MUST_BE_DEFINITION, nameCtx);
            badName = true;
        }
        
        // Bad function name. Do a simple arguments check and then leave.
        if (badName)
        {
            for (AKAParser.ArgumentContext exprCtx : listCtx.argument())
            {
                visit(exprCtx);
            }
        }
        
        // Good function name.
        else
        {
            ArrayList<SymtabEntry> parameters = functionId.getRoutineParameters();
            checkCallArguments(listCtx, parameters);
            ctx.type = functionId.getType();
        }
        
        nameCtx.entry = functionId;
        nameCtx.type  = ctx.type;

        return null;
	}
	
	
	@Override 
	public Object visitNotFactor(AKAParser.NotFactorContext ctx) { 
		AKAParser.FactorContext factorCtx = ctx.factor();
        visit(factorCtx);
        
        if (factorCtx.type != Predefined.booleanType)
        {
            error.flag(TYPE_MUST_BE_BOOLEAN, factorCtx);
        }
        
        ctx.type = Predefined.booleanType;
        return null;
	}
	
	
	@Override 
	public Object visitParenthesizedFactor(AKAParser.ParenthesizedFactorContext ctx) { 
		AKAParser.ExpressionContext exprCtx = ctx.expression();
        visit(exprCtx);
        ctx.type = exprCtx.type;

        return null;
	}
	
	
	@Override 
	public Object visitVariableIdentifier(AKAParser.VariableIdentifierContext ctx) { 
		String variableName = ctx.IDENTIFIER().getText().toLowerCase();
        SymtabEntry variableId = symtabStack.lookup(variableName);
        
        
        if (variableId != null)
        {
            int lineNumber = ctx.getStart().getLine();
            ctx.type = variableId.getType();
            ctx.entry = variableId;
            variableId.appendLineNumber(lineNumber);
            
            Kind kind = variableId.getKind();
            switch (kind)
            {
                case TYPE:
                case PROGRAM:
                case PROGRAM_PARAMETER:
                case DEFINITIONNORETURN:
                case UNDEFINED:
                    error.flag(INVALID_VARIABLE, ctx);
                    break;
                    
                default: break;
            }
        }
        else
        {
            error.flag(UNDECLARED_IDENTIFIER, ctx);
            ctx.type = Predefined.numberType;
        }

        return null; 
	}
	
	
	@Override 
	public Object visitVariable(AKAParser.VariableContext ctx) { 
		AKAParser.VariableIdentifierContext varIdCtx = ctx.variableIdentifier();
		visit(varIdCtx);
		ctx.entry = varIdCtx.entry;
		ctx.type  = variableDatatype(ctx, varIdCtx.type);

		return null; 
	}
	
	
	@Override public Object visitNumberConstant(AKAParser.NumberConstantContext ctx) { 
		ctx.type  = Predefined.numberType;
        ctx.value = Float.parseFloat(ctx.getText());
        
        return ctx.value;
	}
	
	
	@Override 
	public Object visitStringConstant(AKAParser.StringConstantContext ctx) { 
		String akaString = ctx.STRINGWORD().getText();
        String unquoted = akaString.substring(1, akaString.length()-1);
        ctx.type  = Predefined.stringType;            
        ctx.value = unquoted.replace("\"", "\\\""); 
        
        return ctx.value;
	}
	
	
	@Override 
	public Object visitBooleanConstant(AKAParser.BooleanConstantContext ctx) {
		ctx.type = Predefined.booleanType;
		ctx.value = Boolean.parseBoolean(ctx.getText());
		
		return ctx.value; 
	}
    
    
    
    
    //_________________________________________________________________
    
    
    
    
//    /**
//     * Return the number of values in a datatype.
//     * @param type the datatype.
//     * @return the number of values.
//     */
//    private int typeCount(Typespec type)
//    {
//        int count = 0;
//        
//        if (type.getForm() == ENUMERATION)
//        {
//            ArrayList<SymtabEntry> constants = type.getEnumerationConstants();
//            count = constants.size();
//        }
//        else  // subrange
//        {
//            int minValue = type.getSubrangeMinValue();
//            int maxValue = type.getSubrangeMaxValue();
//            count = maxValue - minValue + 1;
//        }
//        
//        return count;
//    }

    
    
    /**
     * Perform semantic operations on procedure and function call arguments.
     * @param listCtx the ArgumentListContext.
     * @param parameters the arraylist of parameters to fill.
     */
    private void checkCallArguments(AKAParser.ArgumentListContext listCtx,
                                    ArrayList<SymtabEntry> parameters)
    {
        int parmsCount = parameters.size();
        int argsCount = listCtx != null ? listCtx.argument().size() : 0;
        
        if (parmsCount != argsCount)
        {
            error.flag(ARGUMENT_COUNT_MISMATCH, listCtx);
            return;
        }
        
        // Check each argument against the corresponding parameter.
        for (int i = 0; i < parmsCount; i++)
        {
            AKAParser.ArgumentContext argCtx = listCtx.argument().get(i);
            AKAParser.ExpressionContext exprCtx = argCtx.expression();
            visit(exprCtx);
            
            SymtabEntry parmId = parameters.get(i);
            Typespec parmType = parmId.getType();
            Typespec argType  = exprCtx.type;
            
            // For a VAR parameter, the argument must be a variable
            // with the same datatype.
            if (parmId.getKind() == REFERENCE_PARAMETER)
            {
                if (expressionIsVariable(exprCtx))
                {
                    if (parmType != argType)
                    {
                        error.flag(TYPE_MISMATCH, exprCtx);
                    }
                }
                else
                {
                    error.flag(ARGUMENT_MUST_BE_VARIABLE, exprCtx);
                }
            }
            
            // For a value parameter, the argument type must be
            // assignment compatible with the parameter type.
            else if (!TypeChecker.areAssignmentCompatible(parmType, argType))
            {
            	//asdf
                error.flag(TYPE_MISMATCH, exprCtx);
            }
        }
    }

    /**
     * Determine whether or not an expression is a variable only.
     * @param exprCtx the ExpressionContext.
     * @return true if it's an expression only, else false.
     */
    private boolean expressionIsVariable(AKAParser.ExpressionContext exprCtx)
    {
        // Only a single simple expression?
        if (exprCtx.simpleExpression().size() == 1)
        {
        	AKAParser.SimpleExpressionContext simpleCtx = 
                                              exprCtx.simpleExpression().get(0);
            // Only a single term?
            if (simpleCtx.term().size() == 1)
            {
            	AKAParser.TermContext termCtx = simpleCtx.term().get(0);
                
                // Only a single factor?
                if (termCtx.factor().size() == 1)
                {
                    return termCtx.factor().get(0) instanceof 
                    		AKAParser.VariableFactorContext;
                }
            }
        }
        
        return false;
    }


    /**
     * Determine the datatype of a variable that can have modifiers.
     * @param varCtx the VariableContext.
     * @param varType the variable's datatype without the modifiers.
     * @return the datatype with any modifiers.
     */
    private Typespec variableDatatype(
                        AKAParser.VariableContext varCtx, Typespec varType)
    {
        Typespec type = varType;
        
        // Loop over the modifiers.
//        for (AKAParser.ModifierContext modCtx : varCtx.modifier())
//        {
//            // Subscripts.
//            if (modCtx.indexList() != null)
//            {
//            	AKAParser.IndexListContext indexListCtx = modCtx.indexList();
//                
//                // Loop over the subscripts.
//                for (AKAParser.IndexContext indexCtx : indexListCtx.index())
//                {
//                    if (type.getForm() == ARRAY)
//                    {
//                        Typespec indexType = type.getArrayIndexType();
//                        AKAParser.ExpressionContext exprCtx = 
//                                                        indexCtx.expression();
//                        visit(exprCtx);
//                        
//                        if (indexType.baseType() != exprCtx.type.baseType())
//                        {
//                            error.flag(TYPE_MISMATCH, exprCtx);
//                        }
//                        
//                        // Datatype of the next dimension.
//                        type = type.getArrayElementType();
//                    }
//                    else
//                    {
//                        error.flag(TOO_MANY_SUBSCRIPTS, indexCtx);
//                    }
//                }
//            }
//            else  // Record field.
//            {
//                if (type.getForm() == RECORD)
//                {
//                    Symtab symtab = type.getRecordSymtab();
//                    AKAParser.FieldContext fieldCtx = modCtx.field();
//                    String fieldName = 
//                                fieldCtx.IDENTIFIER().getText().toLowerCase();
//                    SymtabEntry fieldId = symtab.lookup(fieldName);
//
//                    // Field of the record type?
//                    if (fieldId != null) 
//                    {
//                        type = fieldId.getType();
//                        fieldCtx.entry = fieldId;
//                        fieldCtx.type = type;
//                        fieldId.appendLineNumber(modCtx.getStart().getLine());
//                    }
//                    else 
//                    {
//                        error.flag(INVALID_FIELD, modCtx);
//                    }
//                }
//                
//                // Not a record variable.
//                else 
//                {
//                    error.flag(INVALID_FIELD, modCtx);
//                }
//            }
//        }
        
        return type;
    }
    
}
