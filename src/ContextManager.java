import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextManager {
    private List<Context> contexts = new ArrayList<Context>();
    private List<String> globales = new ArrayList<String>();
    private List<String> instructions;
    private Registers registers = new Registers();

    public ContextManager(List<String> instructions, List<String> globales) {
        this.instructions = instructions;
        this.globales.addAll(globales);
    }

    public ContextManager(List<String> instructions){
        this.instructions = instructions;
    }

    public void EnterContext() {
        contexts.add(new Context());
    }

    public  void exitContext() {
        contexts.remove(contexts.size() - 1);
    }

    public String getLoaded(String identifier) {
        String src = getVariable(identifier);
        String reg = newVar();
        instructions.add(reg + " = load i32, i32* " + src);
        return reg;
    }

    public String getVariable(String identifier) {
        for (int i = contexts.size() - 1; i >= 0; --i) {
            // System.out.println(i + "=" + contexts.size());
            Context context = contexts.get(i);
            if (context.hasVariable(identifier)){
                return context.getConvertedName(identifier);
            }
        }
        int index = globales.indexOf(identifier);
        if (index != -1) {
            return "%" + globales.get(index);
        }
        throw new RuntimeException("Undefined variable: " + identifier);
    }

    public void declareVariable(String identifier) {
        Context currentContext = contexts.get(contexts.size() - 1);
        currentContext.addVariable(identifier);
        instructions.add(currentContext.getConvertedName(identifier) + " = alloca i32");
    }

    public void assign(String identifier, String source) {
        String dest = getVariable(identifier);
        instructions.add("store i32 "+source+", i32* "+dest);
    }

    public String newVar() {
        return registers.getNewRegister();
    }
}