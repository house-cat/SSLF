import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    private ArrayList<IStatement> statements;
    private ArrayList<Token> tokens;

    private int currentToken;

    public Parser(ArrayList<Token> tokens) {
        this.statements = new ArrayList<>();
        this.tokens = tokens;
        this.currentToken = 0;
    }

    public ArrayList<IStatement> getStatements() {
        return this.statements;
    }

    public void parse() {
        int tokensCount = this.tokens.size();
        while (currentToken < tokensCount) {
            this.parseBlockLine(this.statements);
            currentToken += 1;
        }
    }

    private void parseBlockLine(ArrayList<IStatement> block) {
        Token current;
        current = this.tokens.get(currentToken);

        if (this.isVar(current)) {
            this.parseVar(block);
        }
        if (this.isFunction(current)) {
            this.parseFunction(block);
        }
        if (this.isStatement(current)) {
            this.parseStatement(block);
        }
    }

    private void parseStatement(ArrayList<IStatement> block) {
        KeyWordToken statement = (KeyWordToken)this.tokens.get(currentToken);
        if (statement.value().equals("while")) {
            this.parseWhile(block);
        } else if (statement.value().equals("if")) {
            this.parseIf(block);
        } else if (statement.value().equals("return")) {
            this.parseReturn(block);
        } else if (statement.value().equals("function")) {
            this.parseFunctionStatement();
        }
    }

    private void parseReturn(ArrayList<IStatement> block) {
        currentToken += 1;
        block.add(new ReturnStatement(this.parseExpression(Operators.SCL)));
    }

    private ArrayList<Variable> getFunctionArguments() {
        ArrayList<Variable> funcArgs = new ArrayList<>();

        Token current = this.tokens.get(currentToken);

        while (!current.value().equals(Operators.CB)) {
            currentToken += 1;
            current = this.tokens.get(currentToken);

            if (current instanceof NameToken) {
                funcArgs.add(new Variable(current.value().toString()));
            }
        }
        return funcArgs;
    }

    private ArrayList<IStatement> getFunctionStatements() {
        ArrayList<IStatement> statements = new ArrayList<>();
        int tokensCount = this.tokens.size();
        Token current = this.tokens.get(currentToken);
        while (currentToken < tokensCount && !current.value().equals("endfunction")) {
            currentToken += 1;
            current = this.tokens.get(currentToken);

            this.parseBlockLine(statements);
        }
        return statements;
    }

    private void parseFunctionStatement() {
        currentToken += 1;
        String name = this.tokens.get(currentToken).value().toString();

        CustomFunction function = new CustomFunction();
        Program.Get().addFunction(name, function);
        currentToken += 1;
        Token current = this.tokens.get(currentToken);
        if (!current.value().equals(Operators.OB)) {
            throw new RuntimeException("문법 그런식으로 쓰지 마");
        }

        ArrayList<Variable> funcArgs = this.getFunctionArguments();
        ArrayList<IStatement> statements = this.getFunctionStatements();

        function.setArguments(funcArgs);
        function.setStatements(statements);

        currentToken += 1;
        current = this.tokens.get(currentToken);
        if (!current.value().equals(Operators.SCL)) {
            throw new RuntimeException("문법 그런식으로 쓰지 마");
        }

    }

    private void parseWhile(ArrayList<IStatement> block) {
        Token current = this.tokens.get(currentToken);
        IExpression condition = null;
        ArrayList<IStatement> statements = new ArrayList<>();
        boolean conditionParsed = false;
        while (!current.value().equals("endwhile")) {
            currentToken += 1;
            current = this.tokens.get(currentToken);

            if (!conditionParsed) {
                condition = parseExpression(Operators.CLN);
                currentToken += 1;
                current = this.tokens.get(currentToken);
                conditionParsed = true;
            }
            this.parseBlockLine(statements);
        }
        block.add(new WhileStatement(condition, statements));
    }

    private void parseIf(ArrayList<IStatement> block) {
        Token current = this.tokens.get(currentToken);
        IExpression condition = null;
        ArrayList<IStatement> statements = new ArrayList<>();
        ArrayList<IStatement> elseStatements = new ArrayList<>();
        boolean conditionParsed = false;
        boolean parseElse = false;
        while (!current.value().equals("endif")) {
            currentToken += 1;
            current = this.tokens.get(currentToken);

            if (!conditionParsed) {
                condition = parseExpression(Operators.CLN);
                currentToken += 1;
                current = this.tokens.get(currentToken);
                conditionParsed = true;
            }

            if (current.value().equals("else")) {
                currentToken += 2;
                parseElse = true;
            }

            if (parseElse) {
                this.parseBlockLine(elseStatements);
            } else {
                this.parseBlockLine(statements);
            }
        }
        block.add(new IfStatement(condition, statements, elseStatements));
    }

    private void parseFunction(ArrayList<IStatement> block) {
        String funcName = ((FunctionToken)this.tokens.get(currentToken)).value();
        if (funcName.equals("print")) {
            currentToken += 1;
            block.add(new Statement(new Print(this.parseExpression(Operators.SCL))));
        }
    }

    private int getOperatorPriority(Symbol operator) {
        if (this.symbolIsOperator(operator)) {
            if (operator instanceof Plus || operator instanceof Minus || operator instanceof Or || operator instanceof And) {
                return 2;
            } else {
                return 3;
            }
        }
        return 4;
    }

    private IExpression parseExpression(Operators statementEnd) {
        Token current = this.tokens.get(currentToken);
        if (isString(current)) {
            return parseString(statementEnd);
        }
        return parseCalculationExpression(statementEnd);
    }

    private IExpression parseString(Operators statementEnd) {
        Token current = this.tokens.get(currentToken);
        currentToken += 1;
        Token temp = this.tokens.get(currentToken);

        if (!temp.value().equals(statementEnd)) {
            throw new RuntimeException("문법 그런식으로 쓰지 마");
        }
        return new StringValue((String)current.value());
    }

    private IExpression parseCalculationExpression(Operators statementEnd) {
        Token current = this.tokens.get(currentToken);

        Stack<Symbol> stack = new Stack<Symbol>();
        ArrayList<Symbol> result = new ArrayList<Symbol>();
        Symbol currentSym = null;

        while (!current.value().equals(statementEnd)) {
            currentSym = this.convertToken(current, currentSym);
            if (this.isVar(current) || this.isNumber(current) || this.isBoolean(current)) {
                result.add(currentSym);
            } else if (this.isFunction(current)) {
                stack.push(currentSym);
            } else if (this.isComma(current)) {
                while (!(stack.peek() instanceof LeftParenthesis)) {
                    if (stack.isEmpty()) {
                        throw new RuntimeException("문법 그런식으로 쓰지 마");
                    }
                    result.add(stack.pop());
                }
            } else if (this.isOperator(current) && !current.value().equals(Operators.OB) && !current.value().equals(Operators.CB)) {
                while (!stack.isEmpty() && this.symbolIsOperator(stack.peek()) && !(stack.peek() instanceof LeftParenthesis) && this.getOperatorPriority(currentSym) <= this.getOperatorPriority(stack.peek())) {
                    if (stack.peek() instanceof LeftParenthesis) {
                        stack.pop();
                    } else {
                        result.add(stack.pop());
                    }
                }
                stack.push(currentSym);
            } else if (currentSym instanceof LeftParenthesis) {
                stack.push(currentSym);
            } else if (currentSym instanceof RightParenthesis) {
                while (!stack.isEmpty() && this.symbolIsOperator(stack.peek()) && !(stack.peek() instanceof LeftParenthesis)) {
                    result.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek() instanceof LeftParenthesis) {
                    stack.pop();
                }
                if (!stack.isEmpty() && stack.peek() instanceof Evaluator &&
                        !(stack.peek() instanceof LeftParenthesis || stack.peek() instanceof RightParenthesis)) {
                    result.add(stack.pop());
                }
            }
            currentToken += 1;
            current = this.tokens.get(currentToken);
        }

        while (!stack.empty()) {
            result.add(stack.pop());
        }

        return new Expression(result);
    }

    private void parseVar(ArrayList<IStatement> block) {
        Token current = this.tokens.get(currentToken);
        String name = ((NameToken)current).value();
        currentToken += 1;
        current = this.tokens.get(currentToken);
        if (isOperator(current)) {
            Operators type = ((OperatorToken)current).value();
            if (type != Operators.EQ) {
                throw new RuntimeException("문법 그런식으로 쓰지 마");
            }
            currentToken++;
            current = this.tokens.get(currentToken);
            if (isFunction(current) && current.value().equals("read")) {
                block.add(new AssignmentStatement(new Variable(name, null), new Read()));
            } else {
                IExpression expr = this.parseExpression(Operators.SCL);
                block.add(new AssignmentStatement(new Variable(name, null), expr));
            }
        } else {
            throw new RuntimeException("문법 그런식으로 쓰지 마");
        }
    }

    private Symbol convertToken(Token token, Symbol lastSymbol) {
        if (this.isOperator(token)) {
            if (lastSymbol == null) {
                return this.getOperator((Operators)token.value(), true);
            } else {
                if (this.symbolIsOperator(lastSymbol) && !(lastSymbol instanceof RightParenthesis)) {
                    return this.getOperator((Operators)token.value(), true);
                }
            }
        }
        return this.convertToken(token);
    }

    private Symbol convertToken(Token token) {
        Symbol result = null;
        String name = token.value().toString();
        if (isVar(token) && !Program.Get().functionExists(name)) {
            return new Variable(name);
        } else if (isNumber(token)) {
            return new NumberValue(((NumberToken)token).value());
        } else if (isBoolean(token)) {
            return new BooleanValue(((BooleanToken)token).value());
        } else if (isOperator(token)) {
            return this.getOperator(((OperatorToken)token).value());
        } else if (isFunction(token)) {
            return this.getFunction(name);
        } else {
            throw new RuntimeException("문법 그런식으로 쓰지 마");
        }
    }

    private Symbol getOperator(Operators operator) {
        return this.getOperator(operator, false);
    }

    private Symbol getOperator(Operators operator, boolean unary) {
        switch (operator) {
            case PLU:
                if (unary) {
                    return new UnaryPlus();
                } else {
                    return new Plus();
                }
            case MIN:
                if (unary) {
                    return new UnaryMinus();
                } else {
                    return new Minus();
                }
            case OB:
                return new LeftParenthesis();
            case AND:
                return new And();
            case CB:
                return new RightParenthesis();
            case CLN:
                return new Colon();
            case CM:
                return new Comma();
            case DIV:
                return new Division();
            case EQ:
                return new Equals();
            case EQL:
                return new IsEqual();
            case GT:
                return new GreaterThan();
            case GTE:
                return new GreaterThanOrEqual();
            case LT:
                return new LessThan();
            case LTE:
                return new LessThanOrEqual();
            case MOD:
                return new Modulus();
            case MUL:
                return new Multiplication();
            case NEQ:
                return new NotEquals();
            case NOT:
                return new Not();
            case OR:
                return new Or();
            case QT:
                return new Quote();
            case SCL:
                return new Semicolons();
            default:
                throw new RuntimeException("문법 그런식으로 쓰지 마");
        }
    }

    private boolean symbolIsOperator(Symbol symbol) {
        if (symbol instanceof Operator) {
            return true;
        }
        return false;
    }

    private Symbol getFunction(String func) {
        if (func.equals("read")) {
            return new Read();
        } else {
            return Program.Get().getFunction(func);
        }
    }

    private boolean isComma(Token token) {
        if (this.isOperator(token)) {
            if (token.value().equals(Operators.CM)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOperator(Token token) {
        if (token instanceof OperatorToken) {
            return true;
        }
        return false;
    }

    private boolean isVar(Token token) {
        if (token instanceof NameToken && !Program.Get().functionExists(token.value().toString())) {
            return true;
        }
        return false;
    }

    private boolean isNumber(Token token) {
        if (token instanceof NumberToken) {
            return true;
        }
        return false;
    }

    private boolean isBoolean(Token token) {
        if (token instanceof BooleanToken) {
            return true;
        }
        return false;
    }

    private boolean isString(Token token) {
        if (token instanceof StringToken) {
            return true;
        }
        return false;
    }

    private boolean isFunction(Token token) {
        if ((token instanceof FunctionToken) || Program.Get().functionExists(token.value().toString())) {
            return true;
        }
        return false;
    }

    private boolean isStatement(Token token) {
        if (token instanceof KeyWordToken) {
            return true;
        }
        return false;
    }
}
