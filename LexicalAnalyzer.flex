%%// Options of the scanner

%class Lexer5	//Name
%unicode			//Use unicode
%line         //Use line counter (yyline variable)
%column       //Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%standalone		//Standalone mode

// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric	 = {Alpha}|{Numeric}
Zero           = "0"
NZero          = [1-9]
Minus          = "-"

VarName 	   = {AlphaLowerCase}({Numeric}|{AlphaLowerCase})*
// ProgName B9NJOUR est accepté ou obligé une lettre miniscule?
// hypothese oui
ProgName       = {AlphaUpperCase}{AlphaNumeric}*({AlphaLowerCase}|{Numeric}){AlphaNumeric}*
// hypothese non
// ProgName       = {AlphaUpperCase}{AlphaNumeric}*{AlphaLowerCase}{AlphaNumeric}*
EndLine        = "\n"
Number         = {Zero}|({NZero}{Numeric}*)
// OLD
Sign           = [+-]
Integer        = {Sign}?(([1-9][0-9]*)|0)
Decimal        = \.[0-9]*
Exponent       = [eE]{Integer}
Real           = {Integer}{Decimal}?{Exponent}?
Identifier     = {Alpha}{AlphaNumeric}*

%xstate YYINITIAL, COMMENT, LINE_COMMENT

%%// Identification of tokens
<YYINITIAL>{
	"BEGINPROG"         {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.BEGINPROG, yyline, yycolumn);}
	"ENDPROG"           {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDPROG, yyline, yycolumn);}
	"VARIABLES"         {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.VARIABLES, yyline, yycolumn);}
	{EndLine}           {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn);}
	{ProgName}          {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn);}
	","                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.COMMA, yyline, yycolumn);}
	{VarName}			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn);}
	":="                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn);}
	{Number}            {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn);}
	"("                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn);}
	")"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn);}
	"-"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.MINUS, yyline, yycolumn);}
	"+"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.PLUS, yyline, yycolumn);}
	"*"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.TIMES, yyline, yycolumn);}
	"/"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn);}
	"IF"                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.IF, yyline, yycolumn);}
	"THEN"              {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.THEN, yyline, yycolumn);}
	"ENDIF"             {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDIF, yyline, yycolumn);}
	"ELSE"              {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ELSE, yyline, yycolumn);}
	"NOT"               {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.NOT, yyline, yycolumn);}
	"AND"               {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.AND, yyline, yycolumn);}
	"OR"                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.OR, yyline, yycolumn);}
	"="                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.EQ, yyline, yycolumn);}
	">="                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.GEQ, yyline, yycolumn);}
	">"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.GT, yyline, yycolumn);}
	"<="                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.LEQ, yyline, yycolumn);}
	"<"                 {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.LT, yyline, yycolumn);}
	"<>"                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.NEQ, yyline, yycolumn);}
	"WHILE"             {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.WHILE, yyline, yycolumn);}
	"DO"                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.DO, yyline, yycolumn);}
	"ENDWHILE"			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDWHILE, yyline, yycolumn);}
	"FOR"               {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.FOR, yyline, yycolumn);}
	"TO"                {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.TO, yyline, yycolumn);}
	"ENDFOR"			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDFOR, yyline, yycolumn);}
	"PRINT"             {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.PRINT, yyline, yycolumn);}
	"READ"              {System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.READ, yyline, yycolumn);}
	// ? "EOS"			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.EOS, yyline, yycolumn);}
	"//"				{yybegin(LINE_COMMENT);}
	"/*"				{yybegin(COMMENT);}
	. {}
}

<LINE_COMMENT> {
	{EndLine} { yybegin(YYINITIAL);System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn);}
	. {}
}

<COMMENT> {
	"*/" 				{yybegin(COMMENT);}
	. {}
	
}
