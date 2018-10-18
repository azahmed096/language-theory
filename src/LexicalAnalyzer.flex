%%// Options of the scanner

%class Lexer5	//Name
%unicode			//Use unicode
%line         //Use line counter (yyline variable)
%column       //Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol


%{
	boolean isEOF(){
		return zzAtEOF;
	}
%}

// Return value of the program
%eofval{
	
	
	return null;
%eofval}

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric	 = {Alpha}|{Numeric}
Zero           = "0"
NZero          = [1-9]

VarName 	   = {AlphaLowerCase}({Numeric}|{AlphaLowerCase})*
// ProgName B9NJOUR est accepté ou obligé une lettre miniscule?
// hypothese oui
ProgName       = {AlphaUpperCase}{AlphaNumeric}*({AlphaLowerCase}|{Numeric}){AlphaNumeric}*
// hypothese non
// ProgName       = {AlphaUpperCase}{AlphaNumeric}*{AlphaLowerCase}{AlphaNumeric}*
EndLine        = "\n"
Number         = {Zero}|({NZero}{Numeric}*)
BeginComment   = "/*"

// OLD
Sign           = [+-]
Integer        = {Sign}?(([1-9][0-9]*)|0)
Decimal        = \.[0-9]*
Ignore 		   = " " | "\t"

%xstate YYINITIAL, COMMENT, LINE_COMMENT

%%// Identification of tokens
<YYINITIAL>{
	"BEGINPROG"         {return new Symbol(LexicalUnit.BEGINPROG, yyline, yycolumn, yytext());}
	"ENDPROG"           {return new Symbol(LexicalUnit.ENDPROG, yyline, yycolumn, yytext());}
	"VARIABLES"         {return new Symbol(LexicalUnit.VARIABLES, yyline, yycolumn, yytext());}
	{EndLine}           {return new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn, "\\n");}
	{ProgName}          {return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());}
	","                 {return new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext());}
	{VarName}			{
		return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
	}
	":="                {return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
	{Number}            {return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
	"("                 {return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
	")"                 {return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}
	"-"                 {return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
	"+"                 {return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
	"*"                 {return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
	"/"                 {return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}
	"IF"                {return new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
	"THEN"              {return new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
	"ENDIF"             {return new Symbol(LexicalUnit.ENDIF, yyline, yycolumn, yytext());}
	"ELSE"              {return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
	"NOT"               {return new Symbol(LexicalUnit.NOT, yyline, yycolumn, yytext());}
	"AND"               {return new Symbol(LexicalUnit.AND, yyline, yycolumn, yytext());}
	"OR"                {return new Symbol(LexicalUnit.OR, yyline, yycolumn, yytext());}
	"="                 {return new Symbol(LexicalUnit.EQ, yyline, yycolumn, yytext());}
	">="                {return new Symbol(LexicalUnit.GEQ, yyline, yycolumn, yytext());}
	">"                 {return new Symbol(LexicalUnit.GT, yyline, yycolumn, yytext());}
	"<="                {return new Symbol(LexicalUnit.LEQ, yyline, yycolumn, yytext());}
	"<"                 {return new Symbol(LexicalUnit.LT, yyline, yycolumn, yytext());}
	"<>"                {return new Symbol(LexicalUnit.NEQ, yyline, yycolumn, yytext());}
	"WHILE"             {return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
	"DO"                {return new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext());}
	"ENDWHILE"			{return new Symbol(LexicalUnit.ENDWHILE, yyline, yycolumn, yytext());}
	"FOR"               {return new Symbol(LexicalUnit.FOR, yyline, yycolumn, yytext());}
	"TO"                {return new Symbol(LexicalUnit.TO, yyline, yycolumn, yytext());}
	"ENDFOR"			{return new Symbol(LexicalUnit.ENDFOR, yyline, yycolumn, yytext());}
	"PRINT"             {return new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
	"READ"              {return new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext());}
	// ? "EOS"			{System.out.println("token: "+ yytext()); return new Symbol(LexicalUnit.EOS, yyline, yycolumn);}
	"//"				{yybegin(LINE_COMMENT);}
	{BeginComment}				{yybegin(COMMENT);}
	{Ignore}			{}
	.					{throw new UnexpectedTokenException("Unexpected token");}
}

<LINE_COMMENT> {
	/*System.out.println(new Symbol(LexicalUnit.ENDLINE, yyline, yycolumn, "\\n"));*/
	{EndLine} { yybegin(YYINITIAL);}
	. {}
}

<COMMENT> {
	"*/" 				{yybegin(YYINITIAL);}
	{BeginComment}				{throw new UnexpectedTokenException("Nested comments are forbidden");}
	.|{EndLine} {}
}
