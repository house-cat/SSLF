import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

enum Operators {
    PLU,
    MIN,
    MUL,
    DIV,
    MOD,
    SCL,
    EQ,
    GT,
    LT,
    LTE,
    GTE,
    CLN,
    OR,
    AND,
    NOT,
    EQL,
    NEQ,
    OB,
    CB,
    QT,
    CM
}

public class Program {

    private static Program INSTANCE = null;
    private ArrayList<String> functions;
    private ArrayList<String> statements;
    private Stack<HashMap<String, Value>> vars;
    private HashMap<String, CustomFunction> customFunctions;

    private Program() {
        this.vars = new Stack<HashMap<String, Value>>();
        this.vars.push(new HashMap<String, Value>());

        this.customFunctions = new HashMap<String, CustomFunction>();

        this.functions = new ArrayList<String>();
        this.functions.add("print");
        this.functions.add("read");

        this.statements = new ArrayList<String>();
        this.statements.add("while");
        this.statements.add("endwhile");
        this.statements.add("if");
        this.statements.add("else");
        this.statements.add("endif");
        this.statements.add("function");
        this.statements.add("endfunction");
        this.statements.add("return");
    }

    public boolean isFunction(String arg) {
        return this.functions.indexOf(arg) >= 0;
    }

    public boolean isStatement(String arg) {
        return this.statements.indexOf(arg) >= 0;
    }

    public static Program Get() {
        if (INSTANCE == null) {
            INSTANCE = new Program();
        }
        return INSTANCE;
    }

    public Value getVar(String name) {
        if (this.vars.peek().containsKey(name)) {
            return this.vars.peek().get(name);
        }
        throw new RuntimeException("The variable " + name + " is not declared.");
    }

    public void setVal(String name, Value value) {
        this.vars.peek().put(name, value);
    }

    public void pushScope(HashMap<String, Value> scope) {
        this.vars.push(scope);
    }

    public void popScope() {
        this.vars.pop();
    }

    public void addFunction(String name, CustomFunction function) {
        this.customFunctions.put(name, function);
    }

    public CustomFunction getFunction(String name) {
        if (this.customFunctions.containsKey(name)) {
            return this.customFunctions.get(name);
        }
        throw new RuntimeException("The function " + name + " is not defined.");
    }

    public boolean functionExists(String name) {
        if (this.customFunctions.containsKey(name) || this.functions.contains(name)) {
            return true;
        }
        return false;
    }

}