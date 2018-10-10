FROM openjdk:6
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN java -jar jflex-1.6.1.jar LexicalAnalyzer.flex
RUN javac Lexer5.java
CMD ["java", "Lexer5", "00-Factorial.sf"]