import java.util.Map;
import java.util.TreeMap;

/**
 * This class represent a symbol table
 * that tracks all found symbol and the
 * first line where they have been detected.
 */
public class SymbolTable {
    private Map<Object, Integer> identifiers;

    /**
     * Constructor
     */
    public SymbolTable(){
        // The tree map keys are sorted in alphabetical order.
        identifiers = new TreeMap<Object, Integer>();
    }

    /**
     * This method should be called every time
     * an identifier has been encountered
     */
    public void foundIdentifier(Symbol symbol){
        if (!identifiers.containsKey(symbol.getValue())){
            identifiers.put(symbol.getValue(), symbol.getLine() + 1);
        }
    }

    /**
     * Returns the display of identifiers
     * sorted in alphabetical order and with the
     * first line where each identifier has been encountered.
     */
    public String toString(){
        String res = "\nIdentifiers\n";
        for (Map.Entry entry: identifiers.entrySet()){
            res += entry.getKey() + "\t" + entry.getValue() + "\n";
        }
        return res;
    }
}