import java.util.ArrayList;
import java.util.Iterator;

interface IStatement {
    void execute();
}

public class Statement implements IStatement {

    private ArrayList<IExpression> exprs;

    public Statement(IExpression expr) {
        this.exprs = new ArrayList<>();
        this.exprs.add(expr);
    }

    public void execute() {
        Iterator<IExpression> iter = this.exprs.iterator();
        IExpression currentExpr;
        while (iter.hasNext()) {
            currentExpr = iter.next();
            currentExpr.evaluate();
        }
    }

}


class AssignmentStatement implements IStatement {

    private Variable var;
    private IExpression expression;

    public AssignmentStatement(Variable var, IExpression expr) {
        this.var = var;
        this.expression = expr;
    }

    public void execute() {
        this.var.setValue(this.expression.evaluate());
    }
}


class IfStatement implements IStatement {

    private IExpression condition;
    private ArrayList<IStatement> statements;
    private ArrayList<IStatement> elseStatements;

    public IfStatement(IExpression condition, ArrayList<IStatement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    public IfStatement(IExpression condition, ArrayList<IStatement> statements, ArrayList<IStatement> elseStatements) {
        this(condition, statements);
        this.elseStatements = elseStatements;
    }

    public void execute() {
        BooleanValue result = (BooleanValue)condition.evaluate();
        ArrayList<IStatement> toExecute;
        if (result.getValue()) {
            toExecute = this.statements;
        } else {
            toExecute = this.elseStatements;
        }
        if (toExecute != null) {
            Interpreter interpreter = new Interpreter(toExecute);
            interpreter.interpret();
        }
    }
}

class ReturnStatement implements IStatement {

    private IExpression expr;
    private Value result;

    public ReturnStatement(IExpression expr) {
        this.expr = expr;
    }

    public void execute() {
        this.result = this.expr.evaluate();
        throw new ReturnStatementException(result);
    }
}

class ReturnStatementException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Value returnValue;

    public ReturnStatementException(Value returnValue) {
        this.returnValue = returnValue;
    }

    public Value getResult() {
        return this.returnValue;
    }
}

class WhileStatement implements IStatement {

    private ArrayList<IStatement> statements;
    private IExpression condition;

    public WhileStatement(IExpression condition, ArrayList<IStatement> statements) {
        this.statements = statements;
        this.condition = condition;
    }

    public void execute() {
        BooleanValue currentCondition = (BooleanValue)condition.evaluate();
        while (currentCondition.getValue()) {
            Iterator<IStatement> inter = statements.iterator();
            IStatement current;
            while (inter.hasNext()) {
                current = inter.next();
                current.execute();
            }
            currentCondition = (BooleanValue)condition.evaluate();
        }
    }
}
