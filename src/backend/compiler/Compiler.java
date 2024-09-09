package backend.compiler;

import antlr4.*;

import intermediate.symtab.*;
import intermediate.symtab.Predefined;

/**
 * Compile Pascal to Jasmin assembly language.
 */
public class Compiler extends AKABaseVisitor<Object>
{
    private SymtabEntry programId;  // symbol table entry of the program name
    private String programName;     // the program name
    
    private CodeGenerator       code;            // base code generator
    private ProgramGenerator    programCode;     // program code generator
    private StatementGenerator  statementCode;   // statement code generator
    private ExpressionGenerator expressionCode;  // expression code generator
    
    /**
     * Constructor for the base compiler.
     * @param programId the symtab entry for the program name.
     */
    public Compiler(SymtabEntry programId)
    {
        this.programId = programId;        
        programName = programId.getName();
        
        code = new CodeGenerator(programName, "j", this);
    }
    
    /**
     * Constructor for child compilers of procedures and functions.
     * @param parent the parent compiler.
     */
    public Compiler(Compiler parent)
    {
        this.code        = parent.code;
        this.programCode = parent.programCode;
        this.programId   = parent.programId;
        this.programName = parent.programName;
    }
    
    /**
     * Constructor for child compilers of records.
     * @param parent the parent compiler.
     * @param recordId the symbol table entry of the name of the record to compile.
     */
    public Compiler(Compiler parent, SymtabEntry recordId)
    {        
        String recordTypePath = recordId.getType().getRecordTypePath();
        code = new CodeGenerator(recordTypePath, "j", this);
        createNewGenerators(code);
        
        programCode.emitRecord(recordId, recordTypePath);
    }
    
    /**
     * Create new child code generators.
     * @param parentGenerator the parent code generator.
     */
    private void createNewGenerators(CodeGenerator parentGenerator)
    {
        programCode    = new ProgramGenerator(parentGenerator, this);
        statementCode  = new StatementGenerator(programCode, this);
        expressionCode = new ExpressionGenerator(programCode, this);
    }

    /**
     * Get the name of the object (Jasmin) file.
     * @return the name.
     */
    public String getObjectFileName() { return code.getObjectFileName(); }
    
    @Override 
    public Object visitProgram(AKAParser.ProgramContext ctx) { 
    	createNewGenerators(code);
        programCode.emitProgram(ctx);
        return null; 
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitProgramIdentifier(AKAParser.ProgramIdentifierContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitDefList(AKAParser.DefListContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitFuncblock(AKAParser.FuncblockContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitMainblock(AKAParser.MainblockContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override 
	public Object visitStatement(AKAParser.StatementContext ctx) { 
		
		return visitChildren(ctx); 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override 
	public Object visitAssignment(AKAParser.AssignmentContext ctx) {
		statementCode.emitAssignment(ctx);
        return null;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitLhs(AKAParser.LhsContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitRhs(AKAParser.RhsContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitVarType(AKAParser.VarTypeContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public Object visitDeclaration(AKAParser.DeclarationContext ctx) { return visitChildren(ctx); }
	
	
	@Override 
	public Object visitIfStatement(AKAParser.IfStatementContext ctx) { 
		statementCode.emitIf(ctx);
        return null; 
	}

	
	@Override public Object visitCondition(AKAParser.ConditionContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override 
	public Object visitWhileStatement(AKAParser.WhileStatementContext ctx) { 
		statementCode.emitWhile(ctx);
        return null;
	}

	
	@Override 
	public Object visitDisplay(AKAParser.DisplayContext ctx) { 
		statementCode.emitWrite(ctx);
        return null;
	}

	
	@Override 
	public Object visitGuard(AKAParser.GuardContext ctx) { 
		statementCode.emitGuard(ctx);
        return null; 
	}


	
	@Override 
	public Object visitDefCall(AKAParser.DefCallContext ctx) {
		statementCode.emitProcedureCall(ctx);
		return visitChildren(ctx); 
	}


	
	@Override 
	public Object visitDefinition(AKAParser.DefinitionContext ctx) { 
		createNewGenerators(programCode);
        programCode.emitDefinition(ctx);
        return null; 
	}

	
	@Override 
	public Object visitDefinitionnoreturn(AKAParser.DefinitionnoreturnContext ctx) { 
		createNewGenerators(programCode);
        programCode.emitDefinitionNoReturn(ctx);
        return null; 
	}

	
	@Override public Object visitExpression(AKAParser.ExpressionContext ctx) { 
		expressionCode.emitExpression(ctx);
        return null;
	}

	
	@Override 
	public Object visitVariableFactor(AKAParser.VariableFactorContext ctx) { 
		expressionCode.emitLoadValue(ctx.variable());
        return null;
	}

	
	@Override 
	public Object visitNumberFactor(AKAParser.NumberFactorContext ctx) { 
		expressionCode.emitLoadNumberConstant(ctx.numberConstant());
		return null;
	}

	
	@Override 
	public Object visitStringFactor(AKAParser.StringFactorContext ctx) { 
		String unquoted = ctx.getText().substring(1, ctx.getText().length()-1);
		expressionCode.emitLoadConstant(unquoted);
        
        return null;
	}
	
	
	
	@Override 
	public Object visitBooleanFactor(AKAParser.BooleanFactorContext ctx) { 
		expressionCode.emitLoadBooleanConstant(ctx.booleanConstant());
		return null;
	}

	
	
	@Override public Object visitDefCallFactor(AKAParser.DefCallFactorContext ctx) { 
		statementCode.emitFunctionCall(ctx.defCall());
        return null; 
	}
	
	
	
	@Override 
	public Object visitNotFactor(AKAParser.NotFactorContext ctx) { 
		expressionCode.emitNotFactor(ctx);
        return null; 
	}

	
	
	
	@Override 
	public Object visitParenthesizedFactor(AKAParser.ParenthesizedFactorContext ctx) { 
		return visit(ctx.expression()); 
	}

	
	@Override 
	public Object visitVariable(AKAParser.VariableContext ctx) { 
		expressionCode.emitLoadVariable(ctx);        
        return null;
	}
}
