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
				new Parser(iterator).beginParse(cfg);
			} catch(UnexpectedTokenException e){
				System.err.println("Unexpected token" + e.getMessage());
			}
		}
	}
}