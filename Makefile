FLEX=LexicalAnalyzer.flex

all: compile

compile: UnexpectedTokenException.class Symbol.class LexicalUnit.class Lexer5.class Main.class

execute: compile
	java Main 00-Factorial.sf # -nest-comment.sf

Lexer5.java: $(FLEX)
	java -jar jflex-1.6.1.jar $?

%.class: %.java
	javac $?
