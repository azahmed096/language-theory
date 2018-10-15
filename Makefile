FLEX=LexicalAnalyzer.flex
SOURCE_DIR=src
BUILD_DIR=build
TEST_DIR=test
TEST_FILE=00-Factorial.sf

all: compile

compile: $(BUILD_DIR)/UnexpectedTokenException.class $(BUILD_DIR)/LexicalUnit.class $(BUILD_DIR)/Symbol.class $(BUILD_DIR)/Lexer5.class $(BUILD_DIR)/Main.class

execute: compile
	java -classpath $(BUILD_DIR) Main $(TEST_DIR)/$(TEST_FILE) # -nest-comment.sf

$(SOURCE_DIR)/Lexer5.java: $(SOURCE_DIR)/$(FLEX)
	java -jar jflex-1.6.1.jar $?

$(BUILD_DIR)/%.class: $(SOURCE_DIR)/%.java
	javac -d $(BUILD_DIR) -classpath $(BUILD_DIR) $?
