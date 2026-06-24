grammar MiniLang;

program
    : statement* EOF
    ;

statement
    : declaration ';'
    | assignment ';'
    | printStmt ';'
    | ifStmt
    | repeatStmt
    ;

declaration
    : type ID ('=' expr)?
    ;

assignment
    : ID '=' expr
    ;

printStmt
    : 'print' '(' expr ')'
    ;

ifStmt
    : 'if' '(' expr ')' block ('else' block)?
    ;

repeatStmt
    : 'repeat' block 'until' '(' expr ')' ';'
    ;

block
    : '{' statement* '}'
    ;

expr
    : logicalExpr
    ;

logicalExpr
    : relationalExpr (op=('&&'|'||') relationalExpr)*
    ;

relationalExpr
    : additiveExpr (op=('>'|'<'|'>='|'<='|'=='|'!=') additiveExpr)*
    ;

additiveExpr
    : multiplicativeExpr (op=('+'|'-') multiplicativeExpr)*
    ;

multiplicativeExpr
    : primaryExpr (op=('*'|'/') primaryExpr)*
    ;

primaryExpr
    : '(' expr ')'     # ParenthesizedExpr
    | INT              # IntExpr
    | FLOAT            # FloatExpr
    | STRING           # StringExpr
    | BOOL             # BoolExpr
    | ID               # IdExpr
    ;

type
    : 'int'
    | 'float'
    | 'string'
    | 'bool'
    ;

BOOL
    : 'true'
    | 'false'
    ;

ID
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

FLOAT
    : [0-9]+ '.' [0-9]+
    ;

INT
    : [0-9]+
    ;

STRING
    : '"' .*? '"'
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

WS
    : [ \t\r\n]+ -> skip
    ;