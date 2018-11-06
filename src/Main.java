import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Main {

	public static void main(String[] argv) {

		if (argv.length == 0) {
			System.out.println("Usage : java Lexer5 [ --encoding <name> ] <inputfile(s)>");
		} else {
			int firstFilePos = 0;
			String encodingName = "UTF-8";
			if (argv[0].equals("--encoding")) {
				firstFilePos = 2;
				encodingName = argv[1];
				try {
					java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid?
				} catch (Exception e) {
					System.out.println("Invalid encoding '" + encodingName + "'");
					return;
				}
			}
			SymbolTable table = new SymbolTable();
			List<Symbol> symboles = new ArrayList<>();
			for (int i = firstFilePos; i < argv.length; i++) {
				Lexer5 scanner = null;
				try {
					java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
					java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
					scanner = new Lexer5(reader);

					Symbol symbol;
					
					while ((symbol = scanner.yylex()).getType() != LexicalUnit.EOS){
						symboles.add(symbol);
						if (symbol.getType() == LexicalUnit.VARNAME){
							table.foundIdentifier(symbol);
						}
					}
				} catch (java.io.FileNotFoundException e) {
					System.out.println("File not found : \"" + argv[i] + "\"");
				} catch (java.io.IOException e) {
					System.out.println("IO error scanning file \"" + argv[i] + "\"");
					System.out.println(e);
				} catch (UnexpectedTokenException e){
					System.out.print(e.getMessage());
				} catch (Exception e) {
					System.out.println("Unexpected exception:");
					e.printStackTrace();
				}
			}
			
			for (Symbol symbol: symboles){
				System.out.println(symbol);
			}
			System.out.println(table);
			symboles.add(new Symbol(LexicalUnit.EOS));
			Iterator<Symbol> iterator = symboles.iterator();
			// iterator.next();
			System.out.println("kkkkk");
			new Parser(iterator).beginParse();
		}

	
	}
}