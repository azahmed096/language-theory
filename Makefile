FLEX=LexicalAnalyzer.flex
SOURCE_DIR=$(shell readlink -f ./src)
CLASS_PATH=$(shell readlink -f ./build)
DOC_DIR=$(shell readlink -f ./doc)
TEST_DIR=$(shell readlink -f ./test)
TEST_FILE=10-For.sf
TEX_FILE=tree.tex
DIST_DIR=$(shell readlink -f ./dist)
CLASS_FILES:=$(CLASS_PATH)/Java8util.class $(CLASS_PATH)/Grammar.class $(CLASS_PATH)/Config.class
CLASS_FILES+=$(CLASS_PATH)/UnexpectedTokenException.class $(CLASS_PATH)/LexicalUnit.class $(CLASS_PATH)/Symbol.class
CLASS_FILES+=$(CLASS_PATH)/Lexer5.class $(CLASS_PATH)/SymbolTable.class $(CLASS_PATH)/ParseTree.class
CLASS_FILES+=$(CLASS_PATH)/Parser.class $(CLASS_PATH)/VarListExtract.class
CLASS_FILES+=$(CLASS_PATH)/Context.class $(CLASS_PATH)/ContextManager.class
CLASS_FILES+=$(CLASS_PATH)/LCRSTree.class $(CLASS_PATH)/BinaryTree.class $(CLASS_PATH)/BinaryConverter.class $(CLASS_PATH)/Registers.class
CLASS_FILES+=$(CLASS_PATH)/Expression.class $(CLASS_PATH)/CodeGenerator.class $(CLASS_PATH)/Main.class
# 
all: jar report javadoc

jar: compile
	@cd $(CLASS_PATH) && jar cfe $(DIST_DIR)/part3.jar Main *.class

report: $(DOC_DIR)/report.pdf

tree: execute $(TEX_FILE)
	lualatex $(TEX_FILE)

$(DOC_DIR)/report.pdf: $(DOC_DIR)/report.tex
	@cd $(DOC_DIR) && xelatex report.tex && xelatex report.tex

compile: $(CLASS_FILES)


execute: jar 
	@java -ea -jar $(DIST_DIR)/part3.jar -v -wt $(TEX_FILE) $(TEST_DIR)/$(TEST_FILE) # -nest-comment.sf

execute_not_verbose: jar
	java -ea -jar $(DIST_DIR)/part3.jar -wt $(TEX_FILE) $(TEST_DIR)/$(TEST_FILE) # -nest-comment.sf

$(SOURCE_DIR)/Lexer5.java: $(SOURCE_DIR)/$(FLEX)
	java -jar jflex-1.6.1.jar $?

$(CLASS_PATH)/:
	mkdir -p $(CLASS_PATH)	

$(CLASS_PATH)/%.class: $(SOURCE_DIR)/%.java $(CLASS_PATH)/
	@javac -d $(CLASS_PATH) -classpath $(CLASS_PATH) $<

clean:
	rm -Rf $(CLASS_PATH)/*.class $(DOC_DIR)/report.aux $(DOC_DIR)/report.log

javadoc:
	cd $(SOURCE_DIR) && javadoc -d $(DOC_DIR) *.java

