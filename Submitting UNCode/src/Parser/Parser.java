package Parser;

import Lexer.Lexer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    public Lexer.Token token;
    public Lexer lexer;

//    public List<String> tokenReserved = new ArrayList<>(
//            Arrays.asList("and", "constantes", "hasta", "matriz", "paso", "registro", "sino", "vector", "archivo",
//                    "desde", "inicio", "mientras", "subrutina", "repetir", "tipos", "eval", "lib", "not",
//                    "programa", "retorna", "var", "const", "fin", "libext", "or", "ref", "si", "variables",
//                    "TRUE", "FALSE", "SI", "NO", "numerico", "cadena", "logico", "dim", "imprimir", "cls",
//                    "leer", "set_ifs", "abs", "arctan", "ascii", "cos", "dec", "eof", "exp", "get_ifs",
//                    "inc", "int", "log", "lower", "mem", "ord", "paramval", "pcount", "pos", "random", "sec",
//                    "set_stdin", "set_stdout", "sin", "sqrt", "str", "strdup", "strlen", "substr", "tan", "upper", "caso",
//                    "val", "alen"));


    //token.tipo.equals(Lexer.Lexer.TOKEN_TYPES.TK_NUMERO)

    public Parser(Lexer lexer, Lexer.Token token) throws FileNotFoundException {
        this.token = token;
        this.lexer = lexer;
        // this.Program();
    }

    private void doMatch(String expectedToken) {
        if ((
                token.tipo.equals(Lexer.TOKEN_TYPES.ID) && expectedToken.equals("id"))
                || (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) && (expectedToken.equals("num")))
                || (token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) && expectedToken.equals("cadena_")
        )) {
            token = lexer.getToken();
            return;
        }
        if (token.lexema.equals(expectedToken)) {
            token = lexer.getToken();
        } else {
            ArrayList<String> expectedTokenArr = new ArrayList<>(Arrays.asList(expectedToken));
            syntaxError(expectedTokenArr);
        }
    }

    private void syntaxError(ArrayList<String> expectedTokens) {
        StringBuilder errorMessage = new StringBuilder(String.format("<%s:%s> Error sintactico: se encontro: '%s'; se esperaba:", token.linea, token.posicion, token.lexema));
        int i = 0;
        for (String token : expectedTokens) {
            if (i < expectedTokens.size() - 1) {
                errorMessage.append(String.format(" '%s',", token));
            } else {
                errorMessage.append(String.format(" '%s'.", token));
            }
            i++;
        }
        System.out.print(errorMessage);
        throw new RuntimeException();
    }

    // ListaConst
    public void Programa() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("programa") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            ProgramaFirma();
            BloqueDeclaraciones();
            doMatch("inicio");
            BloqueSentencias();
            ListaSentencias();
            doMatch("fin");
            SubrutinasLista();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("const", "var", "programa", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void ProgramaFirma() {
        if (token.lexema.equals("programa")) {
            doMatch("programa");
            doMatch("id");
        } else if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("programa", "const", "var", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaDeclaraciones() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos")) {
            Declaraciones();
            ListaDeclaraciones();
        } else if (token.lexema.equals("inicio")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueDeclaraciones() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos")) {
            Declaraciones();
            ListaDeclaraciones();
        } else if (token.lexema.equals("inicio")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void Declaraciones() {
        if (token.lexema.equals("const")) {
            doMatch("const");
            AsignacionConst();
            FinSentencia();
            ListaConst();
        } else if (token.lexema.equals("tipos")) {
            doMatch("tipos");
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            FinSentencia();
            ListaVarTipo();
        } else if (token.lexema.equals("var")) {
            doMatch("var");
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            FinSentencia();
            ListaVarTipo();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("const", "tipos", "var"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaConst() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            // doMatch("ε");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            AsignacionConst();
            FinSentencia();
            ListaConst();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id", "const", "var", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaVarTipo() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos") || token.lexema.equals("}")) {
            // doMatch("ε");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            FinSentencia();
            ListaVarTipo();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id", "const", "var", "inicio", "tipos", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoDato() {
        if (token.lexema.equals("vector") || token.lexema.equals("matriz") || token.lexema.equals("registro")) {
            TipoComplejo();
        } else if (token.lexema.equals("logico") || token.lexema.equals("cadena") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("numerico")) {
            TipoBasico();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("logico", "cadena", "id", "numerico", "vector", "matriz", "registro"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoBasico() {
        if (token.lexema.equals("cadena")) {
            doMatch("cadena");
        } else if (token.lexema.equals("logico")) {
            doMatch("logico");
        } else if (token.lexema.equals("numerico")) {
            doMatch("numerico");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("cadena", "id", "logico", "numerico"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoComplejo() {
        if (token.lexema.equals("matriz")) {
            doMatch("matriz");
            doMatch("[");
            MatrizTamanio();
            doMatch("]");
            TipoBasico();
        } else if (token.lexema.equals("registro")) {
            doMatch("registro");
            doMatch("{");
            ListaVarTipo();
            doMatch("}");
        } else if (token.lexema.equals("vector")) {
            doMatch("vector");
            doMatch("[");
            VectorTamanio();
            doMatch("]");
            TipoBasico();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("matriz", "registro", "vector"));
            syntaxError(expectedTokens);
        }
    }

    private void VectorTamanio() {

        if (token.lexema.equals("NO") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            VectorValor();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "num", "(", "TRUE", "*", "+", "-", "not", "cadena_", "SI", "FALSE", "{", "id"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanio() {

        if (token.lexema.equals("NO") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            VectorValor();
            MatrizTamanioLista();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "num", "(", "TRUE", "*", "+", "-", "not", "cadena_", "SI", "FALSE", "{", "id"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanioLista() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            MatrizTamanio();
        } else if (token.lexema.equals("]")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void VectorValor() {
        if (token.lexema.equals("*")) {
            doMatch("*");
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("*", "NO", "not", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaArgumentos() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            Argumento();
            ListaArgumentos();
        } else if (token.lexema.equals(")")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(",", ")"));
            syntaxError(expectedTokens);
        }
    }

    private void CuerpoSi() {

        if (token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir") || token.lexema.equals("}")) {
            ListaSentencias();
            SinoSentenciaLista();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna", "sino", "desde", "eval", "si", "mientras", "id", "repetir", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void SinoSiSentencia() {
        if (token.lexema.equals("si")) {
            doMatch("si");
            doMatch("(");
            Expresion();
            doMatch(")");
            ListaSentencias();
        } else if (token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir") || token.lexema.equals("}")) {
            ListaSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna", "sino", "desde", "eval", "si", "mientras", "id", "repetir", "}", "si"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaSentencias() {
        if (token.lexema.equals("hasta") || token.lexema.equals("sino") || token.lexema.equals("caso") || token.lexema.equals("fin") || token.lexema.equals("}")) {
            // doMatch("ε");
        } else if (token.lexema.equals("retorna") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir")) {
            BloqueSentencias();
            ListaSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna", "desde", "eval", "si", "mientras", "id", "repetir", "hasta", "sino", "caso", "fin", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueSentencias() {

        if (token.lexema.equals("retorna") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir")) {
            Sentencia();
            FinSentencia();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna", "desde", "eval", "si", "mientras", "id", "repetir"));
            syntaxError(expectedTokens);
        }
    }

    private void FinSentencia() {
        if (token.lexema.equals(";")) {
            doMatch(";");
        } else if (token.lexema.equals("hasta") || token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("tipos") || token.lexema.equals("repetir") || token.lexema.equals("eval") || token.lexema.equals("caso") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(";", "hasta", "retorna", "sino", "desde", "const", "var", "inicio", "fin", "tipos", "repetir", "eval", "caso", "si", "mientras", "id", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void Sentencia() {
        if (token.lexema.equals("desde")) {
            SentenciaDesde();
        } else if (token.lexema.equals("eval")) {
            SentenciaEval();
        } else if (token.lexema.equals("mientras")) {
            SentenciaMientras();
        } else if (token.lexema.equals("repetir")) {
            SentenciaRepetirHasta();
        } else if (token.lexema.equals("retorna")) {
            SentenciaRetorna();
        } else if (token.lexema.equals("si")) {
            SentenciaSi();
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID) && !token.lexema.equals("and")) {
            SentenciaAsignFunc();

        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id", "desde", "eval", "mientras", "repetir", "retorna", "si"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaSi() {
        if (token.lexema.equals("si")) {
            doMatch("si");
            doMatch("(");
            Expresion();
            doMatch(")");
            doMatch("{");
            CuerpoSi();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("si"));
            syntaxError(expectedTokens);
        }
    }

    private void SinoSentenciaLista() {
        if (token.lexema.equals("sino")) {
            SinoSentencia();
            SinoSentenciaLista();
        } else if (token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("sino", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void SinoSentencia() {
        if (token.lexema.equals("sino")) {
            doMatch("sino");
            SinoSiSentencia();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("sino"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaMientras() {
        if (token.lexema.equals("mientras")) {
            doMatch("mientras");
            doMatch("(");
            Expresion();
            doMatch(")");
            doMatch("{");
            ListaSentencias();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("mientras"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaRepetirHasta() {
        if (token.lexema.equals("repetir")) {
            doMatch("repetir");
            ListaSentencias();
            doMatch("hasta");
            doMatch("(");
            Expresion();
            doMatch(")");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("repetir"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaEval() {
        if (token.lexema.equals("eval")) {
            doMatch("eval");
            doMatch("{");
            EvalCuerpo();
            ListaCasosEval();
            SinoEval();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("eval"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaCasosEval() {
        if (token.lexema.equals("caso")) {
            EvalCuerpo();
            ListaCasosEval();
        } else if (token.lexema.equals("sino") || token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("caso", "sino", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void EvalCuerpo() {
        if (token.lexema.equals("caso")) {
            doMatch("caso");
            doMatch("(");
            Expresion();
            doMatch(")");
            ListaSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("caso"));
            syntaxError(expectedTokens);
        }
    }

    private void SinoEval() {
        if (token.lexema.equals("sino")) {
            doMatch("sino");
            ListaSentencias();
        } else if (token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("sino", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaDesde() {
        if (token.lexema.equals("desde")) {
            doMatch("desde");
            SentenciaAsignFunc();
            doMatch("hasta");
            Expresion();
            OpcionalPaso();
            doMatch("{");
            ListaSentencias();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("desde"));
            syntaxError(expectedTokens);
        }
    }

    private void OpcionalPaso() {
        if (token.lexema.equals("paso")) {
            doMatch("paso");
            Expresion();
        } else if (token.lexema.equals("{")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("paso", "{"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaRetorna() {
        if (token.lexema.equals("retorna")) {
            doMatch("retorna");
            doMatch("(");
            Expresion();
            doMatch(")");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna"));
            syntaxError(expectedTokens);
        }
    }

    private void AsignacionConst() {

        if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            Id();
            doMatch("=");
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaAsignFunc() {

        if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            Id();
            SentenciaId();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaId() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            Argumento();
            ListaArgumentos();
            doMatch(")");
        } else if (token.lexema.equals("=")) {
            doMatch("=");
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("(", "="));
            syntaxError(expectedTokens);
        }
    }

    private void Argumento() {
        if (token.lexema.equals(")") || token.lexema.equals(",")) {
            // doMatch("ε");
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "not", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-", ")", ","));
            syntaxError(expectedTokens);
        }
    }

    private void Elemento() {
        if (token.lexema.equals(",") || token.lexema.equals("}")) {
            // doMatch("ε");
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "not", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-", ",", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void Expresion() {

        if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            NegacionOpcional();
            ExpresionTerminal();
            ExpresionOperador();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "not", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionOperador() {
        if (token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("%") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.lexema.equals("<") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=")) {
            Operador();
            ExpresionFin();
        } else if (token.lexema.equals("hasta") || token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals(")") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("repetir") || token.lexema.equals("eval") || token.lexema.equals("caso") || token.lexema.equals("si") || token.lexema.equals("paso") || token.lexema.equals("mientras") || token.lexema.equals("{") || token.lexema.equals(";") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("]") || token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("==", "<=", "<>", "or", "%", "*", "+", "-", "/", "and", "<", ">", "^", ">=", "hasta", "retorna", "sino", "desde", "const", "var", ")", "inicio", "fin", "tipos", ",", "repetir", "eval", "caso", "si", "paso", "mientras", "{", ";", "id", "]", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionFin() {

        if (token.lexema.equals("NO") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            ExpresionTerminal();
            ExpresionOperador();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("NO", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionTerminal() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            Expresion();
            doMatch(")");
        } else if (token.lexema.equals("FALSE")) {
            doMatch("FALSE");
        } else if (token.lexema.equals("NO")) {
            doMatch("NO");
        } else if (token.lexema.equals("{")) {
            Objeto();
        } else if (token.lexema.equals("SI")) {
            doMatch("SI");
        } else if (token.lexema.equals("TRUE")) {
            doMatch("TRUE");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA)) {
            doMatch("cadena_");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Signo();
            NumIdTerminal();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("(", "FALSE", "NO", "{", "SI", "num", "+", "id", "-", "TRUE", "cadena_"));
            syntaxError(expectedTokens);
        }
    }

    private void NumIdTerminal() {
        if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO)) {
            doMatch("num");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            Id();
            FuncionId();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void FuncionId() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            Argumento();
            ListaArgumentos();
            doMatch(")");
        } else if (token.lexema.equals("<=") || token.lexema.equals("retorna") || token.lexema.equals("<>") || token.lexema.equals("const") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("repetir") || token.lexema.equals("caso") || token.lexema.equals("and") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("]") || token.lexema.equals("^") || token.lexema.equals("==") || token.lexema.equals("hasta") || token.lexema.equals("sino") || token.lexema.equals("or") || token.lexema.equals("desde") || token.lexema.equals("%") || token.lexema.equals("var") || token.lexema.equals(")") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("eval") || token.lexema.equals("paso") || token.lexema.equals("{") || token.lexema.equals(";") || token.lexema.equals("<") || token.lexema.equals("}") || token.lexema.equals(">") || token.lexema.equals(">=")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("(", "<=", "retorna", "<>", "const", "inicio", "fin", "repetir", "caso", "and", "si", "mientras", "id", "]", "^", "==", "hasta", "sino", "or", "desde", "%", "var", ")", "*", "+", "tipos", ",", "-", "/", "eval", "paso", "{", ";", "<", "}", ">", ">="));
            syntaxError(expectedTokens);
        }
    }

    private void Signo() {
        if (token.lexema.equals("+")) {
            doMatch("+");
        } else if (token.lexema.equals("-")) {
            doMatch("-");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("+", "-", "num", "id"));
            syntaxError(expectedTokens);
        }
    }

    private void NegacionOpcional() {
        if (token.lexema.equals("not")) {
            doMatch("not");
        } else if (token.lexema.equals("NO") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("{") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("not", "NO", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "{", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void Operador() {
        if (token.lexema.equals("%")) {
            doMatch("%");
        } else if (token.lexema.equals("*")) {
            doMatch("*");
        } else if (token.lexema.equals("+")) {
            doMatch("+");
        } else if (token.lexema.equals("-")) {
            doMatch("-");
        } else if (token.lexema.equals("/")) {
            doMatch("/");
        } else if (token.lexema.equals("<")) {
            doMatch("<");
        } else if (token.lexema.equals("<=")) {
            doMatch("<=");
        } else if (token.lexema.equals("<>")) {
            doMatch("<>");
        } else if (token.lexema.equals("==")) {
            doMatch("==");
        } else if (token.lexema.equals(">")) {
            doMatch(">");
        } else if (token.lexema.equals(">=")) {
            doMatch(">=");
        } else if (token.lexema.equals("^")) {
            doMatch("^");
        } else if (token.lexema.equals("and")) {
            doMatch("and");
        } else if (token.lexema.equals("or")) {
            doMatch("or");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("%", "*", "+", "-", "/", "<", "<=", "<>", "==", ">", ">=", "^", "and", "or"));
            syntaxError(expectedTokens);
        }
    }

    private void Objeto() {
        if (token.lexema.equals("{")) {
            doMatch("{");
            Elemento();
            ListaObjetos();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("{"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaObjetos() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            Expresion();
            ListaObjetos();
        } else if (token.lexema.equals("}")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(",", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void Id() {

        if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
            IdCompuesto();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("id"));
            syntaxError(expectedTokens);
        }
    }

    private void IdCompuesto() {
        if (token.lexema.equals(".")) {
            doMatch(".");
            Id();
        } else if (token.lexema.equals("[")) {
            IdValores();
        } else if (token.lexema.equals("<=") || token.lexema.equals("retorna") || token.lexema.equals("<>") || token.lexema.equals("const") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("repetir") || token.lexema.equals("caso") || token.lexema.equals("and") || token.lexema.equals("si") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("]") || token.lexema.equals("^") || token.lexema.equals("==") || token.lexema.equals("hasta") || token.lexema.equals("sino") || token.lexema.equals("or") || token.lexema.equals("desde") || token.lexema.equals("%") || token.lexema.equals("var") || token.lexema.equals("(") || token.lexema.equals(")") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("eval") || token.lexema.equals("paso") || token.lexema.equals("{") || token.lexema.equals(";") || token.lexema.equals("<") || token.lexema.equals("=") || token.lexema.equals("}") || token.lexema.equals(">") || token.lexema.equals(">=")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(".", "[", "<=", "retorna", "<>", "const", "inicio", "fin", "repetir", "caso", "and", "si", "mientras", "id", "]", "^", "==", "hasta", "sino", "or", "desde", "%", "var", "(", ")", "*", "+", "tipos", ",", "-", "/", "eval", "paso", "{", ";", "<", "=", "}", ">", ">="));
            syntaxError(expectedTokens);
        }
    }

    private void IdValores() {
        if (token.lexema.equals("[")) {
            doMatch("[");
            Expresion();
            ListaValoresMatriz();
            doMatch("]");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("["));
            syntaxError(expectedTokens);
        }
    }

    private void ListaValoresMatriz() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            Expresion();
            ListaValoresMatriz();
        } else if (token.lexema.equals("]")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void SubrutinasLista() {
        if (token.lexema.equals("subrutina")) {
            BloqueSubrutinas();
            SubrutinasLista();
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.EOF)) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("subrutina", "$"));
            syntaxError(expectedTokens);
        }
    }

    private void ArgumentosSubrutina() {
        if (token.lexema.equals(")")) {
            // doMatch("ε");
        } else if (token.lexema.equals("ref") || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            RefIndicador();
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            MasArgumentosSubrutina();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("ref", "id", ")"));
            syntaxError(expectedTokens);
        }
    }

    private void MasArgumentosSubrutina() {
        if (token.lexema.equals(";")) {
            doMatch(";");
            RefIndicador();
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            MasArgumentosSubrutina();
        } else if (token.lexema.equals(")")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(";", ")"));
            syntaxError(expectedTokens);
        }
    }

    private void RefIndicador() {
        if (token.lexema.equals("ref")) {
            doMatch("ref");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("ref", "id"));
            syntaxError(expectedTokens);
        }
    }

    private void RetornoOpcional() {
        if (token.lexema.equals("retorna")) {
            doMatch("retorna");
            TipoDato();
        } else if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("retorna", "const", "var", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueSubrutinas() {
        if (token.lexema.equals("subrutina")) {
            doMatch("subrutina");
            doMatch("id");
            doMatch("(");
            ArgumentosSubrutina();
            doMatch(")");
            RetornoOpcional();
            BloqueDeclaraciones();
            doMatch("inicio");
            BloqueSentencias();
            ListaSentencias();
            doMatch("fin");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList("subrutina"));
            syntaxError(expectedTokens);
        }
    }

    private void MasTiposVar() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            doMatch("id");
            MasTiposVar();
        } else if (token.lexema.equals(":")) {
            // doMatch("ε");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(Arrays.asList(",", ":"));
            syntaxError(expectedTokens);
        }
    }
}