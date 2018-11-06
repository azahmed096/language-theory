FLEX=LexicalAnalyzer.flex
SOURCE_DIR=$(shell readlink -f ./src)
CLASS_PATH=$(shell readlink -f ./build)
DOC_DIR=$(shell readlink -f ./doc)
TEST_DIR=$(shell readlink -f ./test)
TEST_FILE=00-Factorial.sf
DIST_DIR=$(shell readlink -f ./dist)


all: jar report javadoc

jar: compile
	cd $(CLASS_PATH) && jar cfe $(DIST_DIR)/part2.jar Main *.class

report: $(DOC_DIR)/report.pdf

$(DOC_DIR)/report.pdf: $(DOC_DIR)/report.tex
	cd $(DOC_DIR) && xelatex report.tex

compile: $(CLASS_PATH)/UnexpectedTokenException.class $(CLASS_PATH)/LexicalUnit.class $(CLASS_PATH)/Symbol.class $(CLASS_PATH)/Lexer5.class $(CLASS_PATH)/SymbolTable.class $(CLASS_PATH)/ParseTree.class $(CLASS_PATH)/Parser.class $(CLASS_PATH)/Main.class

execute: jar
	java -jar $(DIST_DIR)/part2.jar $(TEST_DIR)/$(TEST_FILE) # -nest-comment.sf

$(SOURCE_DIR)/Lexer5.java: $(SOURCE_DIR)/$(FLEX)
	java -jar jflex-1.6.1.jar $?

$(CLASS_PATH)/:
	mkdir -p $(CLASS_PATH)	

$(CLASS_PATH)/%.class: $(SOURCE_DIR)/%.java $(CLASS_PATH)/
	javac -d $(CLASS_PATH) -classpath $(CLASS_PATH) $<

clean:
	rm $(CLASS_PATH)/*.class $(DOC_DIR)/report.aux $(DOC_DIR)/report.log

javadoc:
	cd $(SOURCE_DIR) && javadoc -d $(DOC_DIR) *.java

