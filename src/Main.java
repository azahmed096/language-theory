import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Main {

	public static void main(String[] argv) throws IOException {
		Config cfg = new Config(argv);
		if (!cfg.isCorrect()){
			System.err.println(cfg.getError());
		} else {
			SymbolTable table = new SymbolTable();
			List<Symbol> symboles = new ArrayList<Symbol>();
			java.io.Reader reader = cfg.getFortranReader();
			Lexer5 scanner = new Lexer5(reader);
			Symbol symbol;

			while ((symbol = scanner.yylex()).getType() != LexicalUnit.EOS){
				symboles.add(symbol);
				if (symbol.getType() == LexicalUnit.VARNAME){
					table.foundIdentifier(symbol);
				}
			}
			/*
			for (Symbol s: symboles){
				System.out.println(s);
			}
			System.out.println(table);*/
			symboles.add(new Symbol(LexicalUnit.EOS));
			Iterator<Symbol> iterator = symboles.iterator();
			try{
				ParseTree tree = new Parser(iterator).beginParse(cfg);
				List<String> s = new CodeGenerator().generateProgram(tree, table);
				System.out.println(Java8util.Stringjoin("\n", s));
				// BroTree lcrs = new BroTree(tree);
				// BinaryTree bin = new BinaryConverter().fromBro(lcrs);
				// cfg.getTexStream().write(bin.toLaTeX());
				// cfg.getTexStream().close();
			} catch(UnexpectedTokenException e){
				System.err.println("Unexpected token" + e.getMessage());
			}
		}
	}
}