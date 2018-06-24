import java.util.Stack;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Symbol<T> {

    protected T value;

    public Symbol(T sym) {
        this.value = sym;
    }

    public T getValue() {
        return this.value;
    }
}

class Value<T> extends Symbol<T> implements IExpression {

    public Value(T val) {
        super(val);
    }

    public Value evaluate() {
        return this;
    }
}

abstract class Evaluator<T> extends Symbol<T> implements IExpression {

    protected Stack<IExpression> args;

    public Evaluator() {
        super(null);
        this.args = new Stack<IExpression>();
    }

    public void setOperand(IExpression arg) {
        this.args.push(arg);
    }

    public abstract Value evaluate();
}

abstract class Function extends Evaluator {

    protected int argsCount;

    public Function(int argsCount) {
        this.argsCount = argsCount;
    }

    public int getArgumentsCount() {
        return this.argsCount;
    }
}

abstract class Operator extends Evaluator {

    private int argsCount;

    public Operator(int argsCount) {
        this.argsCount = argsCount;
    }

    public int getArgumentsCount() {
        return this.argsCount;
    }

}

class BooleanValue extends Value<Boolean> implements IExpression {

    public BooleanValue(Boolean val) {
        super(val);
    }

    public Value evaluate() {
        return this;
    }
}

class NumberValue extends Value<Double> implements IExpression {


    public NumberValue(Double val) {
        super(val);
    }

    public Value evaluate() {
        return this;
    }
}

class StringValue extends Value<String> implements IExpression {

    public StringValue(String val) {
        super(val);
    }

    public Value evaluate() {
        return this;
    }
}

class Variable extends Symbol<String> implements IExpression {

    public Variable(String name, Value value) {
        this(name);
        Program.Get().setVal(this.value, value);
    }

    public Variable(String name) {
        super(name);
    }

    public Value evaluate() {
        return Program.Get().getVar(this.value);
    }

    public void setValue(Value val) {
        Program.Get().setVal(this.value, val);
    }

}


/**** Build in Operators ****/

class And extends Operator {

    public And() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Boolean arg1 = (Boolean)((IExpression) this.args.pop()).evaluate().getValue();
        Boolean arg2 = (Boolean)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 && arg2);
    }
}

class Colon extends Operator {

    public Colon() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class Comma extends Operator {

    public Comma() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class Division extends Operator {

    public Division() {
        super(2);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(arg1 / arg2);
    }
}

class Equals extends Operator {

    public Equals() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class GreaterThan extends Operator {

    public GreaterThan() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 > arg2);
    }
}

class GreaterThanOrEqual extends Operator {

    public GreaterThanOrEqual() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 >= arg2);
    }
}

class IsEqual extends Operator {

    public IsEqual() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Object arg1 = (Object)((IExpression) this.args.pop()).evaluate().getValue();
        Object arg2 = (Object)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1.equals(arg2));
    }
}

class LeftParenthesis extends Operator {

    public LeftParenthesis() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class LessThan extends Operator {

    public LessThan() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 < arg2);
    }
}

class LessThanOrEqual extends Operator {

    public LessThanOrEqual() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 <= arg2);
    }
}

class Minus extends Operator {

    public Minus() {
        super(2);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(arg1 - arg2);
    }
}

class Modulus extends Operator {

    public Modulus() {
        super(2);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(arg1 % arg2);
    }
}

class Multiplication extends Operator {

    public Multiplication() {
        super(2);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(arg1 * arg2);
    }
}

class Not extends Operator {

    public Not() {
        super(1);
    }

    public Value<Boolean> evaluate() {
        Boolean arg1 = (Boolean)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(!arg1);
    }
}

class NotEquals extends Operator {

    public NotEquals() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Object arg1 = (Object)((IExpression) this.args.pop()).evaluate().getValue();
        Object arg2 = (Object)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(!arg1.equals(arg2));
    }
}

class Or extends Operator {

    public Or() {
        super(2);
    }

    public Value<Boolean> evaluate() {
        Boolean arg1 = (Boolean)((IExpression) this.args.pop()).evaluate().getValue();
        Boolean arg2 = (Boolean)((IExpression) this.args.pop()).evaluate().getValue();
        return new BooleanValue(arg1 || arg2);
    }
}

class Plus extends Operator {

    public Plus() {
        super(2);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        Double arg2 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(arg1 + arg2);
    }
}

class Quote extends Operator {

    public Quote() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class RightParenthesis extends Operator {

    public RightParenthesis() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class Semicolons extends Operator {

    public Semicolons() {
        super(0);
    }

    public Value<Boolean> evaluate() {
        return null;
    }
}

class UnaryMinus extends Operator {

    public UnaryMinus() {
        super(1);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(- arg1);
    }
}

class UnaryPlus extends Operator {

    public UnaryPlus() {
        super(1);
    }

    public Value<Double> evaluate() {
        Double arg1 = (Double)((IExpression) this.args.pop()).evaluate().getValue();
        return new NumberValue(+ arg1);
    }
}


/**** Builtin Functions ****/

class Print implements IExpression {

    private IExpression toPrint;

    public Print(IExpression toPrint) {
        this.toPrint = toPrint;
    }

    public Value evaluate() {
        Value result = this.toPrint.evaluate();
        System.out.print(result.getValue());
        return result;
    }

}

class Read extends Symbol implements IExpression {

    public Read() {
        super(null);
    }

    public Value evaluate() {
        try {
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(reader);
            String result = in.readLine();
            return this.getParse(result);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Value getParse(String str) {
        IExpression expr;
        if (str.matches("(true|false)")) {
            if (str.equals("true")) {
                return new BooleanValue(true);
            }
            return new BooleanValue(false);
        }
        return new NumberValue(Double.parseDouble(str));
    }
}

class CustomFunction extends Function {
    private ArrayList<Variable> funcArgs;
    private ArrayList<IStatement> statements;

    public CustomFunction() {
        super(0);
        this.funcArgs = null;
        this.statements = null;
    }

    public Value evaluate() {
        HashMap<String, Value> vars = new HashMap<>();
        Value result = null;
        Value value;
        String name;
        int current = 0;
        while (!this.args.isEmpty()) {
            value = ((IExpression)this.args.pop()).evaluate();
            name = this.funcArgs.get(current).getValue();
            vars.put(name, value);
            current += 1;
        }

        Program.Get().pushScope(vars);

        try {
            Interpreter interpreter = new Interpreter(this.statements);
            interpreter.interpret();
        } catch (ReturnStatementException e) {
            result = e.getResult();
        }

        Program.Get().popScope();

        return result;
    }

    public void setArguments(ArrayList<Variable> args) {
        this.funcArgs = args;
        this.argsCount = args.size();
    }

    public void setStatements(ArrayList<IStatement> statements) {
        this.statements = statements;
    }

}