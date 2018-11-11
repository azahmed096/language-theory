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
	// Initialized to endline to ignore empty line on the beginning of the file
	private LexicalUnit lastType = LexicalUnit.ENDLINE;
	
	// ** fin de fichier
	private boolean endprog = false;

	private Symbol getSymbol(LexicalUnit type){
		Symbol res = null;

		// ** fin de fichier
		if (type != LexicalUnit.ENDLINE || (lastType != LexicalUnit.ENDLINE && !endprog)){
			res = new Symbol(type, yyline, yycolumn, yytext());
		}

		/*
		if (lastType != LexicalUnit.ENDLINE || type != LexicalUnit.ENDLINE){
			res = new Symbol(type, yyline, yycolumn, yytext());
		}
		*/
		// ** fin de fichier
		if (type == LexicalUnit.ENDPROG){
			endprog = true;
		}
		lastType = type;
		return res;
	}
%}

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
	"BEGINPROG"         {return getSymbol(LexicalUnit.BEGINPROG);}
	"ENDPROG"           {return getSymbol(LexicalUnit.ENDPROG);}
	"VARIABLES"         {return getSymbol(LexicalUnit.VARIABLES);}
	{EndLine}           {Symbol endline = getSymbol(LexicalUnit.ENDLINE); if (endline != null) {return endline;}}
	{ProgName}          {return getSymbol(LexicalUnit.PROGNAME);}
	","                 {return getSymbol(LexicalUnit.COMMA);}
	{VarName}			{
		return getSymbol(LexicalUnit.VARNAME);
	}
	":="                {return getSymbol(LexicalUnit.ASSIGN);}
	{Number}            {return getSymbol(LexicalUnit.NUMBER);}
	"("                 {return getSymbol(LexicalUnit.LPAREN);}
	")"                 {return getSymbol(LexicalUnit.RPAREN);}
	"-"                 {return getSymbol(LexicalUnit.MINUS);}
	"+"                 {return getSymbol(LexicalUnit.PLUS);}
	"*"                 {return getSymbol(LexicalUnit.TIMES);}
	"/"                 {return getSymbol(LexicalUnit.DIVIDE);}
	"IF"                {return getSymbol(LexicalUnit.IF);}
	"THEN"              {return getSymbol(LexicalUnit.THEN);}
	"ENDIF"             {return getSymbol(LexicalUnit.ENDIF);}
	"ELSE"              {return getSymbol(LexicalUnit.ELSE);}
	"NOT"               {return getSymbol(LexicalUnit.NOT);}
	"AND"               {return getSymbol(LexicalUnit.AND);}
	"OR"                {return getSymbol(LexicalUnit.OR);}
	"="                 {return getSymbol(LexicalUnit.EQ);}
	">="                {return getSymbol(LexicalUnit.GEQ);}
	">"                 {return getSymbol(LexicalUnit.GT);}
	"<="                {return getSymbol(LexicalUnit.LEQ);}
	"<"                 {return getSymbol(LexicalUnit.LT);}
	"<>"                {return getSymbol(LexicalUnit.NEQ);}
	"WHILE"             {return getSymbol(LexicalUnit.WHILE);}
	"DO"                {return getSymbol(LexicalUnit.DO);}
	"ENDWHILE"			{return getSymbol(LexicalUnit.ENDWHILE);}
	"FOR"               {return getSymbol(LexicalUnit.FOR);}
	"TO"                {return getSymbol(LexicalUnit.TO);}
	"ENDFOR"			{return getSymbol(LexicalUnit.ENDFOR);}
	"PRINT"             {return getSymbol(LexicalUnit.PRINT);}
	"READ"              {return getSymbol(LexicalUnit.READ);}
	"//"				{yybegin(LINE_COMMENT);}
	{BeginComment}				{yybegin(COMMENT);}
	{Ignore}			{}
	.					{throw new UnexpectedTokenException("Unexpected token");}
}

<LINE_COMMENT> {
	{EndLine} { yybegin(YYINITIAL); Symbol endline = getSymbol(LexicalUnit.ENDLINE); if (endline != null) {return endline;} /* " $\\backslash n$ " */}
	. {}
}

<COMMENT> {
	"*/" 				{yybegin(YYINITIAL);}
	{BeginComment}				{throw new UnexpectedTokenException("Nested comments are forbidden");}
	.|{EndLine} {}
}
