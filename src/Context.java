import java.util.ArrayList;

/**
 * Context are only usefull for FOR loops
 * a FOR loop adds only one new variable
 */
public class Context {
    private static int globalCounter = 0;
    // Should not be in the VARNAME regex
    private static char sep = '_';

    private int counter;
    private ArrayList<String> identifiers = new ArrayList<String>();

    public Context(){
        counter = globalCounter++;
    }

    public void addVariable(String var){
        identifiers.add(var);
    }

    public boolean hasVariable(String identifier){
        return identifiers.contains(identifier);
    }

    public String getConvertedName(String identifier) {
        assert hasVariable(identifier);
        // TODO check?? refactor
        return "%" + identifier + sep + counter;
    }
}