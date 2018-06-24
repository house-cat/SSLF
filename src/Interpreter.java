import java.util.ArrayList;
import java.util.Iterator;

public class Interpreter {
    private ArrayList<IStatement> statements;

    public Interpreter(ArrayList<IStatement> statements) {
        this.statements = statements;
    }

    public void interpret() {
        Iterator<IStatement> iter = this.statements.iterator();
        IStatement current;
        while (iter.hasNext()) {
            current = iter.next();
            current.execute();
        }
    }
}