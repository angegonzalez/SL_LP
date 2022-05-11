import Parser.Parser;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
//        Lexer.Lexer.Token token;
//        Lexer.Lexer lexer = new Lexer.Lexer();
//        token = lexer.getToken();
//        while (token.tipo != Lexer.Lexer.TOKEN_TYPES.EOF) {
//            if (token.tipo == Lexer.Lexer.TOKEN_TYPES.TK_ERROR) {
//                System.out.println(">>> Error lexico(linea:" + token.linea + ",posicion:" + token.posicion + ")");
//                break;
//            }
//            System.out.println(token.lexema);
//            token = lexer.getToken();
//        }
        // new ParserHelper();
        Lexer.Lexer.Token token;
        Lexer.Lexer lexer = new Lexer.Lexer();
        token = lexer.getToken();
        Parser parser = new Parser(lexer, token);
        try {
            parser.Programa();
            System.out.println("El analisis sintactico ha finalizado exitosamente.");
        } catch (RuntimeException ignored) {
        }


//        if (token.tipo.equals(Lexer.Lexer.TOKEN_TYPES.TK_ERROR)) {
//            System.out.println("hola");
//        }
        // System.out.println("El analisis sintactico ha finalizado exitosamente.");


        // parser.firstSetNoTerminals();
        // parser.verifyLL1Condition();
        //String[] parts = {"A", "tres"};
        // HashSet<String> result = parser.firstStar(parts);
        // System.out.println(Arrays.toString(result.toArray()));
        // parser.calculateFirstsSet();
//        String test = "Programa\t\t->BloqueDeclaraciones";
//        System.out.println(Arrays.toString(test.split("\t\t->")));
    }
}
