import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


class SSLF {

    private static String program = "";

    public static void loadProgramFile(String file) {
        try {
            File programFile = new File(file);
            FileReader fileReader = new FileReader(programFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String programLine;
            while ((programLine = reader.readLine()) != null) {
                program += programLine;
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void execute() {
        Lexer SSLFlexer= new Lexer(program);
        ArrayList<Token> list = SSLFlexer.lex();

        Parser SSLFparser = new Parser(list);
        Interpreter SSLFinterpreter = new Interpreter(SSLFparser.getStatements());

        SSLFparser.parse();
        SSLFinterpreter.interpret();
    }

}

public class Main {

    public static void main(String[] args) {

        new GUI();

        /*
        if(args.length<1) {
            System.out.println("input your filename");
            System.exit(-1);
        }
        SSLF.loadProgramFile(args[0]);
        SSLF.execute();
        */
    }

}