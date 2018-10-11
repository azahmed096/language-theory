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
	"BEGINPROG"         {System.out.println(new Symbol(LexicalUnit.BEGINPROG, yyline, yycolumn, yytext()));}
	"ENDPROG"           {System.out.println(new Symbol(LexicalUnit.ENDPROG, yyline, yycolumn, yytext()));}
	"VARIABLES"         {System.out.println(new Symbol(LexicalUnit.VARIABLES, yyline, yycolumn, yytext()));}
	{EndLine}           {System.out.println(new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn, "\\n"));}
	{ProgName}          {System.out.println(new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()));}
	","                 {System.out.println(new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext()));}
	{VarName}			{System.out.println(new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()));}
	":="                {System.out.println(new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()));}
	{Number}            {System.out.println(new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()));}
	"("                 {System.out.println(new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext()));}
	")"                 {System.out.println(new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext()));}
	"-"                 {System.out.println(new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()));}
	"+"                 {System.out.println(new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()));}
	"*"                 {System.out.println(new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()));}
	"/"                 {System.out.println(new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()));}
	"IF"                {System.out.println(new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext()));}
	"THEN"              {System.out.println(new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext()));}
	"ENDIF"             {System.out.println(new Symbol(LexicalUnit.ENDIF, yyline, yycolumn, yytext()));}
	"ELSE"              {System.out.println(new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext()));}
	"NOT"               {System.out.println(new Symbol(LexicalUnit.NOT, yyline, yycolumn, yytext()));}
	"AND"               {System.out.println(new Symbol(LexicalUnit.AND, yyline, yycolumn, yytext()));}
	"OR"                {System.out.println(new Symbol(LexicalUnit.OR, yyline, yycolumn, yytext()));}
	"="                 {System.out.println(new Symbol(LexicalUnit.EQ, yyline, yycolumn, yytext()));}
	">="                {System.out.println(new Symbol(LexicalUnit.GEQ, yyline, yycolumn, yytext()));}
	">"                 {System.out.println(new Symbol(LexicalUnit.GT, yyline, yycolumn, yytext()));}
	"<="                {System.out.println(new Symbol(LexicalUnit.LEQ, yyline, yycolumn, yytext()));}
	"<"                 {System.out.println(new Symbol(LexicalUnit.LT, yyline, yycolumn, yytext()));}
	"<>"                {System.out.println(new Symbol(LexicalUnit.NEQ, yyline, yycolumn, yytext()));}
	"WHILE"             {System.out.println(new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext()));}
	"DO"                {System.out.println(new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext()));}
	"ENDWHILE"			{System.out.println(new Symbol(LexicalUnit.ENDWHILE, yyline, yycolumn, yytext()));}
	"FOR"               {System.out.println(new Symbol(LexicalUnit.FOR, yyline, yycolumn, yytext()));}
	"TO"                {System.out.println(new Symbol(LexicalUnit.TO, yyline, yycolumn, yytext()));}
	"ENDFOR"			{System.out.println(new Symbol(LexicalUnit.ENDFOR, yyline, yycolumn, yytext()));}
	"PRINT"             {System.out.println(new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext()));}
	"READ"              {System.out.println(new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext()));}
	// ? "EOS"			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.EOS, yyline, yycolumn);}
	"//"				{yybegin(LINE_COMMENT);}
	"/*"				{yybegin(COMMENT);}
	. {}
}

<LINE_COMMENT> {
	{EndLine} { System.out.println(new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn, "\\n"));yybegin(YYINITIAL);}
	. {}
}

<COMMENT> {
	"*/" 				{yybegin(YYINITIAL);}
	. {}
	
}
