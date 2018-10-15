import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
			Map<Object, Integer> identifiers = new TreeMap();
			List<Symbol> symboles = new ArrayList<>();
			for (int i = firstFilePos; i < argv.length; i++) {
				Lexer5 scanner = null;
				try {
					java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
					java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
					scanner = new Lexer5(reader);

					Symbol symbol;
					
					while ((symbol = scanner.yylex()) != null){
					
						if (symbol == null){
							System.out.println("Null");
							continue;
						}
						symboles.add(symbol);
						if (symbol.getType() == LexicalUnit.VARNAME && !identifiers.containsKey(symbol.getValue())){
							identifiers.put(symbol.getValue(), symbol.getLine() + 1);
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
			System.out.println("\nIdentifiers");
			for (Map.Entry entry: identifiers.entrySet()){
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		}

	
	}
}