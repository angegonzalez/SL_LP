package Lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public Lexer() throws FileNotFoundException {
    }

    public enum TOKEN_TYPES {
        TK_ASIGNACION, TK_COMA, TK_CORCHETE_IZQUIERDO, TK_CORCHETE_DERECHO, TK_DISTINTO_DE, TK_DIVISION, TK_DOS_PUNTOS,
        TK_IGUAL_QUE, TK_LLAVE_DERECHA, TK_LLAVE_IZQUIERDA, TK_MAYOR, TK_MAYOR_IGUAL, TK_MENOR, TK_MENOR_IGUAL, TK_MODULO, TK_MULTIPLICACION,
        TK_PARENTESIS_DERECHO, TK_PARENTESIS_IZQUIERDO, TK_POTENCIACION, TK_PUNTO, TK_PUNTO_Y_COMA, TK_RESTA, TK_SUMA, EOF, ID, TK_CADENA,
        TK_NUMERO, TK_ERROR
    }

    public enum STATES {
        IN_INICIO, IN_HECHO, IN_ASIGNACION, IN_DIVISION, IN_MAYOR, IN_MENOR, IN_PUNTO, IN_IDENTIFICADOR,
        IN_NUMERO, IN_NUMERO_2, IN_E, IN_MENOS_MAS_NUMERO, IN_NUMERO_PUNTO, IN_COMENTARIO, IN_FIN_COMENTARIO, IN_CADENA,
        IN_CADENA_SIMPLE, IN_COMENTARIO_COMENTARIO_DIVISION
    }

    public List<String> tokenReserved = new ArrayList<>(
            Arrays.asList("and", "constantes", "hasta", "matriz", "paso", "registro", "sino", "vector", "archivo",
                    "desde", "inicio", "mientras", "subrutina", "repetir", "tipos", "eval", "lib", "not",
                    "programa", "retorna", "var", "const", "fin", "libext", "or", "ref", "si", "variables",
                    "TRUE", "FALSE", "SI", "NO", "numerico", "cadena", "logico", "dim", "imprimir", "cls",
                    "leer", "set_ifs", "abs", "arctan", "ascii", "cos", "dec", "eof", "exp", "get_ifs",
                    "inc", "int", "log", "lower", "mem", "ord", "paramval", "pcount", "pos", "random", "sec",
                    "set_stdin", "set_stdout", "sin", "sqrt", "str", "strdup", "strlen", "substr", "tan", "upper","caso",
                    "val","alen"));

    public class Token {
        public TOKEN_TYPES tipo;
        public String lexema = "";
        public int linea;
        public int posicion;

        @Override
        public String toString() {
            if (tokenReserved.contains(this.lexema)) {
                return "<" + this.lexema + "," + this.linea + "," + this.posicion + ">";
            }
            if (Objects.equals(this.tipo.toString(), "ID") || Objects.equals(this.tipo.toString(), "TK_CADENA")
                    || Objects.equals(this.tipo.toString(), "TK_NUMERO")) {
                return "<" + this.tipo.toString().toLowerCase() + "," + this.lexema + "," + this.linea + "," + this.posicion + ">";
            }
            return "<" + this.tipo.toString().toLowerCase() + "," + this.linea + "," + this.posicion + ">";
        }
    }

    public int nline = 0;
    public int ncol = 0;
    // public Scanner reader = new Scanner(System.in);
    public Scanner reader = new Scanner(new File("code.txt"));
    public int lineLength = 0;
    public String line = "";
    public int idCounter = 0;
    public int auxiliar = 0;


    public char getChar() {
        if (ncol == 0) {
            if (reader.hasNextLine()) {
                line = reader.nextLine();
                lineLength = line.length();
                ncol = 0;
                nline += 1;
            } else {
                return '\u001a';
            }
        } else if (ncol == lineLength) {
            auxiliar = ncol;
            ncol = 0;
            return '\n';
        }
        if (lineLength == 0) {
            return ' ';
        }
        char c = line.charAt(ncol);
        ncol++;
        return c;
    }

    public void unGetChar() {
        ncol--;
    }

    public boolean isDelim(char c) {
        char[] delim = new char[]{
                ' ', '\t', '\n'
        };
        for (char del : delim) {
            if (del == c) {
                return true;
            }
        }
        return false;
    }

    public Token getToken() {
        char c;
        STATES state = STATES.IN_INICIO;
        Token token = new Token();

        while (state != STATES.IN_HECHO) {
            switch (state) {
                case IN_INICIO: {
                    c = getChar();
                    while (isDelim(c)) {
                        c = getChar();
                    }
                    if (Character.isLetter(c) || c == '_') {
                        state = STATES.IN_IDENTIFICADOR;
                        idCounter = 1;
                    } else if (Character.isDigit(c)) {
                        state = STATES.IN_NUMERO;
                    } else if (c == '\'') {
                        state = STATES.IN_CADENA_SIMPLE;
                    } else if (c == '\"') {
                        state = STATES.IN_CADENA;
                    } else if (c == '.') {
                        token.tipo = TOKEN_TYPES.TK_PUNTO;
                        state = STATES.IN_HECHO;
                    } else if (c == '/') {
                        token.tipo = TOKEN_TYPES.TK_DIVISION;
                        state = STATES.IN_DIVISION;
                    } else if (c == '<') {
                        token.tipo = TOKEN_TYPES.TK_MENOR;
                        state = STATES.IN_MENOR;
                    } else if (c == '>') {
                        token.tipo = TOKEN_TYPES.TK_MAYOR;
                        state = STATES.IN_MAYOR;
                    } else if (c == '=') {
                        token.tipo = TOKEN_TYPES.TK_ASIGNACION;
                        state = STATES.IN_ASIGNACION;
                    } else if (c == ',') {
                        token.tipo = TOKEN_TYPES.TK_COMA;
                        state = STATES.IN_HECHO;
                    } else if (c == '[') {
                        token.tipo = TOKEN_TYPES.TK_CORCHETE_IZQUIERDO;
                        state = STATES.IN_HECHO;
                    } else if (c == ']') {
                        token.tipo = TOKEN_TYPES.TK_CORCHETE_DERECHO;
                        state = STATES.IN_HECHO;
                    } else if (c == ':') {
                        token.tipo = TOKEN_TYPES.TK_DOS_PUNTOS;
                        state = STATES.IN_HECHO;
                    } else if (c == '}') {
                        token.tipo = TOKEN_TYPES.TK_LLAVE_DERECHA;
                        state = STATES.IN_HECHO;
                    } else if (c == '{') {
                        token.tipo = TOKEN_TYPES.TK_LLAVE_IZQUIERDA;
                        state = STATES.IN_HECHO;
                    } else if (c == '%') {
                        token.tipo = TOKEN_TYPES.TK_MODULO;
                        state = STATES.IN_HECHO;
                    } else if (c == '*') {
                        token.tipo = TOKEN_TYPES.TK_MULTIPLICACION;
                        state = STATES.IN_HECHO;
                    } else if (c == '(') {
                        token.tipo = TOKEN_TYPES.TK_PARENTESIS_IZQUIERDO;
                        state = STATES.IN_HECHO;
                    } else if (c == ')') {
                        token.tipo = TOKEN_TYPES.TK_PARENTESIS_DERECHO;
                        state = STATES.IN_HECHO;
                    } else if (c == '^') {
                        token.tipo = TOKEN_TYPES.TK_POTENCIACION;
                        state = STATES.IN_HECHO;
                    } else if (c == ';') {
                        token.tipo = TOKEN_TYPES.TK_PUNTO_Y_COMA;
                        state = STATES.IN_HECHO;
                    } else if (c == '-') {
                        token.tipo = TOKEN_TYPES.TK_RESTA;
                        state = STATES.IN_HECHO;
                    } else if (c == '+') {
                        token.tipo = TOKEN_TYPES.TK_SUMA;
                        state = STATES.IN_HECHO;
                    } else if (c == '\u001a') {
                        token.tipo = TOKEN_TYPES.EOF;
                        state = STATES.IN_HECHO;
                    } else {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        state = STATES.IN_HECHO;
                    }
                    token.lexema += c;
                    token.linea = nline;
                    token.posicion = ncol;
                    break;
                }
                case IN_IDENTIFICADOR: {
//                    if (idCounter > 32) {
//                        token.tipo = TOKEN_TYPES.TK_ERROR;
//                        token.linea = nline;
//                        token.posicion = ncol;
//                        state = STATES.IN_HECHO;
//                        break;
//                    }
                    c = getChar();
                    Pattern pattern = Pattern.compile("[À-ÐÒ-ðò-ÿ]");
                    Matcher matcher = pattern.matcher(Character.toString(c));

                    if (matcher.find()) {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        token.linea = nline;
                        token.posicion = ncol;
                        state = STATES.IN_HECHO;
                        break;
                    }

                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.ID;
                        state = STATES.IN_HECHO;
                    } else if (!(Character.isLetterOrDigit(c) || c == '_')) {
                        token.tipo = TOKEN_TYPES.ID;
                        state = STATES.IN_HECHO;
                        unGetChar();
                    } else {
                        token.lexema += c;
                        idCounter += 1;
                    }
                    break;
                }
                case IN_NUMERO: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                    } else if (c == '.') {
                        state = STATES.IN_PUNTO;
                        token.lexema += c;
                    } else if (c == 'E' || c == 'e') {
                        state = STATES.IN_E;
                        token.lexema += c;
                    } else if (!Character.isDigit(c)) {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                        unGetChar();
                    } else {
                        token.lexema += c;
                    }
                    break;
                }
                case IN_CADENA_SIMPLE: {
                    c = getChar();
                    if (c == '\n') {
                        ncol = 0;
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                    } else if (c == '\'') {
                        token.tipo = TOKEN_TYPES.TK_CADENA;
                        state = STATES.IN_HECHO;
                    } else if (c == '\u001a') {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        state = STATES.IN_HECHO;
                    }
                    token.lexema += c;
                    break;
                }
                case IN_CADENA: {
                    c = getChar();
                    if (c == '\n') {
                        ncol = 0;
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        token.linea = nline;
                        state = STATES.IN_HECHO;
                    } else if (c == '\"') {
                        token.tipo = TOKEN_TYPES.TK_CADENA;
                        state = STATES.IN_HECHO;
                    } else if (c == '\u001a') {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        state = STATES.IN_HECHO;
                    }
                    token.lexema += c;
                    break;
                }
                case IN_PUNTO: {
                    c = getChar();
                    if (!(Character.isDigit(c))) {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                        unGetChar();
                        token.lexema = token.lexema.substring(0, token.lexema.length() - 1);
                        unGetChar();
                    } else {
                        state = STATES.IN_NUMERO_2;
                        token.lexema += c;
                    }
                    break;
                }
                case IN_NUMERO_2: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                    } else if (c == 'E' || c == 'e') {
                        state = STATES.IN_E;
                        token.lexema += c;
                    } else if (!(Character.isDigit(c))) {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                        unGetChar();
                    } else {
                        token.lexema += c;
                    }
                    break;
                }
                case IN_E: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        token.lexema = token.lexema.substring(0, token.lexema.length() - 1);
                        ncol = auxiliar - 1;
                        state = STATES.IN_HECHO;
                    } else if (Character.isDigit(c)) {
                        state = STATES.IN_NUMERO_PUNTO;
                        token.lexema += c;
                    } else if (c == '-' || c == '+') {
                        state = STATES.IN_MENOS_MAS_NUMERO;
                        token.lexema += c;
                    } else {
                        token.lexema = token.lexema.substring(0, token.lexema.length() - 1);
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        unGetChar();
                        unGetChar();
                        state = STATES.IN_HECHO;
                    }
                    break;
                }
                case IN_NUMERO_PUNTO: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                    } else if (!(Character.isDigit(c))) {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        state = STATES.IN_HECHO;
                        unGetChar();
                    } else {
                        token.lexema += c;
                    }
                    break;
                }
                case IN_MENOS_MAS_NUMERO: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        token.lexema = token.lexema.substring(0, token.lexema.length() - 2);
                        ncol = token.lexema.length();
                        state = STATES.IN_HECHO;
                    } else if (!Character.isDigit(c)) {
                        token.tipo = TOKEN_TYPES.TK_NUMERO;
                        token.lexema = token.lexema.substring(0, token.lexema.length() - 2);
                        // System.out.println("ncol" + ncol);
                        state = STATES.IN_HECHO;
                        unGetChar();
                        unGetChar();
                        unGetChar();
                    } else {
                        state = STATES.IN_NUMERO_PUNTO;
                        token.lexema += c;
                    }
                    break;
                }

                case IN_DIVISION: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_DIVISION;
                        state = STATES.IN_HECHO;
                    } else if (c == '/') {
                        ncol = 0;
                        state = STATES.IN_INICIO;
                        token = new Token();
                    } else if (c == '*') {
                        state = STATES.IN_COMENTARIO;
                        token.lexema += c;
                    } else {
                        state = STATES.IN_HECHO;
                        unGetChar();
                    }
                    break;
                }
                case IN_COMENTARIO: {
                    c = getChar();
                    if (c == '*') {
                        state = STATES.IN_FIN_COMENTARIO;
                    } else if (c == '/') {
                        state = STATES.IN_COMENTARIO_COMENTARIO_DIVISION;
                    } else if (c == '\u001a') {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
//                        token.linea = nline;
//                        token.posicion = auxiliar;
                        state = STATES.IN_HECHO;
                    }
                    break;
                }
                case IN_COMENTARIO_COMENTARIO_DIVISION: {
                    c = getChar();
                    if (c == '*') {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        token.linea = nline;
                        // token.posicion = ncol;
                        state = STATES.IN_HECHO;
                    } else if (c == '\u001a') {
                        token.tipo = TOKEN_TYPES.TK_ERROR;
                        token.linea = nline;
                        token.posicion = auxiliar;
                        state = STATES.IN_HECHO;
                    } else {
                        state = STATES.IN_COMENTARIO;
                    }
                    break;
                }
                case IN_FIN_COMENTARIO: {
                    c = getChar();
                    if (c == '/') {
//                        ncol = 0;
                        token = new Token();
                        state = STATES.IN_INICIO;
                    } else {
                        token.tipo = TOKEN_TYPES.TK_MULTIPLICACION;
                        state = STATES.IN_COMENTARIO;
                        unGetChar();
                    }
                    break;
                }

                case IN_MENOR: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_MENOR;
                        state = STATES.IN_HECHO;
                    } else if (c == '=') {
                        token.tipo = TOKEN_TYPES.TK_MENOR_IGUAL;
                        token.lexema += c;
                        state = STATES.IN_HECHO;
                    } else if (c == '>') {
                        token.tipo = TOKEN_TYPES.TK_DISTINTO_DE;
                        token.lexema += c;
                        state = STATES.IN_HECHO;
                    } else {
                        state = STATES.IN_HECHO;
                        unGetChar();
                    }
                    break;
                }
                case IN_MAYOR: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_MAYOR;
                        state = STATES.IN_HECHO;
                    } else if (c == '=') {
                        token.tipo = TOKEN_TYPES.TK_MAYOR_IGUAL;
                        token.lexema += c;
                        state = STATES.IN_HECHO;
                    } else {
                        state = STATES.IN_HECHO;
                        unGetChar();
                    }
                    break;
                }
                case IN_ASIGNACION: {
                    c = getChar();
                    if (c == '\n') {
                        token.tipo = TOKEN_TYPES.TK_ASIGNACION;
                        state = STATES.IN_HECHO;
                    } else if (c == '=') {
                        token.tipo = TOKEN_TYPES.TK_IGUAL_QUE;
                        state = STATES.IN_HECHO;
                        token.lexema += c;
                    } else {
                        state = STATES.IN_HECHO;
                        unGetChar();
                    }
                    break;
                }
                default: {
                    state = STATES.IN_HECHO;
                    break;
                }
            }
        }
        return token;
    }
}
