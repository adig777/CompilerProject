grammar AKA;

@header {
    package antlr4;
    import java.util.HashMap;
    import java.util.Map;
    import intermediate.symtab.SymtabEntry;
    import intermediate.type.Typespec;
}



// Reserved Words
NUMBER : N U M B E R;
BOOL : B O O L ;
STRING : S T R I N G;
MAIN : M A I N ;
DISPLAY : D I S P L A Y ;
IF : I F ;
WHILE : W H I L E ;
ELSE : E L S E ;
ELSEIF : E L S E I F ;
GUARD : G U A R D ;
DEF : D E F ;
NOT : N O T ;
TRUE : T R U E ;
FALSE : F A L S E ;
AND : A N D ;
OR : O R ;

// General Program Format
program           : programIdentifier '{' (funcblock)? mainblock '}';

programIdentifier   locals [ SymtabEntry entry = null ]
    : IDENTIFIER ;

funcblock		 : defList ;
defList: (definition | definitionnoreturn) (definition | definitionnoreturn)* ;

mainblock        : MAIN '{' (statementList)? '}';

// Statements
statement 
: assignment ';'
| ifStatement
| whileStatement
| guard
| display ';'
| defCall ';'
;


// Assignment
assignment : (varType)? lhs '=' rhs;
lhs locals [ Typespec type = null ]: variable ;
rhs locals [Object value = null ]: declaration ;

varType: NUMBER | BOOL | STRING ;
declaration locals [ Typespec type = null ] : expression ;


// Statements Expanded	
ifStatement : ifBlock elseifBlock* elseBlock? ;

ifBlock : IF '(' condition ')'  '{' (statementList)? '}' ;

elseifBlock : ELSEIF '(' condition ')' '{' (statementList)? '}' ;

elseBlock : ELSE '{' (statementList)? '}';
 
condition: expression ;
 
whileStatement: WHILE '(' condition ')' '{' (statementList)? '}' ;
 
display: (DISPLAY) '(' (expression)? ')' ;

guard: GUARD '('paramList ')' '{' statementList '}' ;

paramList: condition (',' condition)* ;



// Definition Calls and Definitions
defCall: defName '(' argumentList? ')';
argumentList : argument ( ',' argument )* ;
argument     : expression ;

defName locals [ Typespec type = null, SymtabEntry entry = null ]  : IDENTIFIER ;

varList : (varType variable) (',' (varType variable))* ;

definition: DEF  defName '(' varList? ':' (varType variable) ')' '{' (statementList)? '}';

definitionnoreturn: DEF defName '(' varList? ')' '{' (statementList)? '}';

 
 
// Statement List
statementList : statement ( statement )* ;


// Expression Stuff
expression          locals [ Typespec type = null ] 
    : simpleExpression (relOperator simpleExpression)? ;
    
simpleExpression    locals [ Typespec type = null ] 
    : sign? term (addOperator term)* ;
    
term                locals [ Typespec type = null ]
    : factor (mulOperator factor)* ;

factor              locals [ Typespec type = null ] 
    : variable             # variableFactor
    | numberConstant       # numberFactor
    | stringConstant       # stringFactor
    | booleanConstant	   # booleanFactor
    | defCall              # defCallFactor
    | NOT factor           # notFactor
    | '(' expression ')'   # parenthesizedFactor
    ;

// Variable Stuff
variableIdentifier  locals [ Typespec type = null, SymtabEntry entry = null ] 
    : IDENTIFIER ;

variable	locals [ Typespec type = null, SymtabEntry entry = null ] 
    : variableIdentifier ;
    

// Operators    
relOperator : '==' | '~' | '<' | '<=' | '>' | '>=' ;
addOperator: '+' | '-' | OR ;
mulOperator: '*' | '/' | AND ;

// Signs
sign			: '+' | '-';

// Constants
numberConstant locals [ Typespec type = null, Object value = null ] : (sign)? INTEGER ('.' INTEGER)? ;
stringConstant locals [ Typespec type = null, Object value = null ] : STRINGWORD ;
booleanConstant locals [ Typespec type = null, Object value = null ] : (TRUE) | (FALSE) ;

// Skip Whitespace
NEWLINE : '\r'? '\n' -> skip ;
WS : [ \t]+ -> skip ;
                     
                     
// String Format and Comment Format
DOUBLEQUOTE : ('"');
STRINGWORD : DOUBLEQUOTE STRING_CHAR* DOUBLEQUOTE ;
COMMENT : '$' COMMENT_CHARACTER* '$' -> skip ;

fragment COMMENT_CHARACTER : ~('$') ;

fragment STRING_CHAR : ~('"') ;      // any non-quote character

                     

// Integer and Identifier Format
IDENTIFIER : [a-zA-Z][a-zA-Z0-9]* ;
INTEGER : [0-9]+ ;

// Case Insensitive
fragment A : ('a' | 'A') ;
fragment B : ('b' | 'B') ;
fragment C : ('c' | 'C') ;
fragment D : ('d' | 'D') ;
fragment E : ('e' | 'E') ;
fragment F : ('f' | 'F') ;
fragment G : ('g' | 'G') ;
fragment H : ('h' | 'H') ;
fragment I : ('i' | 'I') ;
fragment J : ('j' | 'J') ;
fragment K : ('k' | 'K') ;
fragment L : ('l' | 'L') ;
fragment M : ('m' | 'M') ;
fragment N : ('n' | 'N') ;
fragment O : ('o' | 'O') ;
fragment P : ('p' | 'P') ;
fragment Q : ('q' | 'Q') ;
fragment R : ('r' | 'R') ;
fragment S : ('s' | 'S') ;
fragment T : ('t' | 'T') ;
fragment U : ('u' | 'U') ;
fragment V : ('v' | 'V') ;
fragment W : ('w' | 'W') ;
fragment X : ('x' | 'X') ;
fragment Y : ('y' | 'Y') ;
fragment Z : ('z' | 'Z') ;
