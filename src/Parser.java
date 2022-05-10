import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public Lexer.Token token;
    public Lexer lexer;

    //token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO)

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
            ArrayList<String> expectedTokenArr = new ArrayList<>(List.of(token.lexema));
            syntaxError(expectedTokenArr);
        }
    }

    private void syntaxError(ArrayList<String> expectedTokens) {
        if (expectedTokens.size() == 1) {
            if (expectedTokens.get(0).equals(token.lexema)) {
                return;
            }
        }
        StringBuilder errorMessage = new StringBuilder(String.format("<%s,%s> Error sintactico: se encontro: %s; se esperaba:", token.linea, token.posicion, token.lexema));
        int i = 0;
        for (String token : expectedTokens) {
            if (i < expectedTokens.size() - 1) {
                errorMessage.append(String.format(" '%s',", token));
            } else {
                errorMessage.append(String.format(" '%s'.", token));
            }
            i++;
        }
        System.out.println(errorMessage);
//        throw new RuntimeException("Help!  Somebody debug me!  I'm crashing!");
    }

    public void Programa() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            BloqueDeclaraciones();
            doMatch("inicio");
            BloqueSentencias();
            doMatch("fin");
            doMatch("BloqueSubrutinas");
        } else if (token.lexema.equals("programa")) {
            doMatch("programa");
            doMatch("id");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("const", "var", "inicio", "tipos", "programa"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueDeclaraciones() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos")) {
            Declaraciones();
            ListaDeclaraciones();
        } else if (token.lexema.equals("inicio")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaDeclaraciones() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos")) {
            Declaraciones();
            ListaDeclaraciones();
        } else if (token.lexema.equals("inicio")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void Declaraciones() {
        if (token.lexema.equals("const")) {
            doMatch("const");
            SentenciaAsignFunc();
            ListaConst();
        } else if (token.lexema.equals("tipos")) {
            doMatch("tipos");
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            ListaVarTipo();
        } else if (token.lexema.equals("var")) {
            doMatch("var");
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            ListaVarTipo();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("const", "tipos", "var"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaConst() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos")) {
            // doMatch("?");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            SentenciaAsignFunc();
            ListaConst();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id", "const", "var", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaVarTipo() {
        if (token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos") || token.lexema.equals("}")) {
            // doMatch("?");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
            doMatch(":");
            TipoDato();
            ListaVarTipo();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id", "const", "var", "inicio", "tipos", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void MasTiposVar() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            doMatch("id");
            MasTiposVar();
        } else if (token.lexema.equals(":")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of(",", ":"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoDato() {
        if (token.lexema.equals("vector") || token.lexema.equals("matriz") || token.lexema.equals("registro")) {
            TipoComplejo();
        } else if (token.lexema.equals("numero") || token.lexema.equals("logico") || token.lexema.equals("cadena") || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            TipoBasico();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("numero", "logico", "cadena", "id", "vector", "matriz", "registro"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoBasico() {
        if (token.lexema.equals("cadena")) {
            doMatch("cadena");
        } else if (token.lexema.equals("logico")) {
            doMatch("logico");
        } else if (token.lexema.equals("numero")) {
            doMatch("numero");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("cadena", "id", "logico", "numero"));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("matriz", "registro", "vector"));
            syntaxError(expectedTokens);
        }
    }

    private void VectorTamanio() {
        if (token.lexema.equals("*")) {
            doMatch("*");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO)) {
            doMatch("num");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("*", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanio() {
        if (token.lexema.equals("*")) {
            doMatch("*");
            MatrizTamanioLista();
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO)) {
            doMatch("num");
            MatrizTamanioLista();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("*", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanioLista() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            MatrizTamanio();
        } else if (token.lexema.equals("]")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of(",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaArgumentos() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            Argumento();
            ListaArgumentos();
        } else if (token.lexema.equals(")")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of(",", ")"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaSentencias() {
        if (token.lexema.equals("hasta") || token.lexema.equals("sino") || token.lexema.equals("caso") || token.lexema.equals("fin") || token.lexema.equals("}")) {
            // doMatch("?");
        } else if (token.lexema.equals("retorna") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir")) {
            BloqueSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("retorna", "desde", "eval", "mientras", "id", "repetir", "hasta", "sino", "caso", "fin", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void FinSentencia() {
        if (token.lexema.equals(";")) {
            doMatch(";");
        } else if (token.lexema.equals("hasta") || token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("caso") || token.lexema.equals("mientras") || token.lexema.equals("fin") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir") || token.lexema.equals("}")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of(";", "hasta", "retorna", "sino", "desde", "eval", "caso", "mientras", "fin", "id", "repetir", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueSentencias() {
        if (token.lexema.equals("retorna") || token.lexema.equals("desde") || token.lexema.equals("eval") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("repetir")) {
            Sentencia();
            FinSentencia();
            ListaSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("retorna", "desde", "eval", "mientras", "id", "repetir"));
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
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            SentenciaAsignFunc();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id", "desde", "eval", "mientras", "repetir", "retorna"));
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
            BloqueSentencias();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("mientras"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaRepetirHasta() {
        if (token.lexema.equals("repetir")) {
            doMatch("repetir");
            BloqueSentencias();
            doMatch("hasta");
            doMatch("(");
            Expresion();
            doMatch(")");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("repetir"));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("eval"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaCasosEval() {
        if (token.lexema.equals("caso")) {
            EvalCuerpo();
            ListaCasosEval();
        } else if (token.lexema.equals("sino") || token.lexema.equals("}")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("caso", "sino", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void EvalCuerpo() {
        if (token.lexema.equals("caso")) {
            doMatch("caso");
            doMatch("(");
            Expresion();
            doMatch(")");
            BloqueSentencias();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("caso"));
            syntaxError(expectedTokens);
        }
    }

    private void SinoEval() {
        if (token.lexema.equals("sino")) {
            doMatch("sino");
            BloqueSentencias();
        } else if (token.lexema.equals("}")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("sino", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaDesde() {
        if (token.lexema.equals("desde")) {
            doMatch("desde");
            SentenciaAsignFunc();
            doMatch("hasta");
            Expresion();
            doMatch("{");
            BloqueSentencias();
            doMatch("}");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("desde"));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("retorna"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaAsignFunc() {
        if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
            SentenciaId();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id"));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("(", "="));
            syntaxError(expectedTokens);
        }
    }

    private void Argumento() {
        if (token.lexema.equals(")") || token.lexema.equals(",")) {
            // doMatch("?");
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("(") || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Expresion();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("NO", "not", "cadena_", "SI", "num", "(", "TRUE", "FALSE", "+", "id", "-", ")", ","));
            syntaxError(expectedTokens);
        }
    }

    private void Expresion() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            ExpresionTerminal();
            doMatch(")");
            ExpresionOperador();
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            ExpresionTerminal();
            ExpresionOperador();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("(", "NO", "not", "cadena_", "SI", "num", "TRUE", "FALSE", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionOperador() {
        if (token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("%") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.lexema.equals("<") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=")) {
            Operador();
            ExpresionOperadorAux();
        } else if (token.lexema.equals("hasta") || token.lexema.equals("retorna") || token.lexema.equals("sino") || token.lexema.equals("desde") || token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals(")") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("tipos") || token.lexema.equals("repetir") || token.lexema.equals(",") || token.lexema.equals("eval") || token.lexema.equals("caso") || token.lexema.equals("mientras") || token.lexema.equals("{") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals(";") || token.lexema.equals("]") || token.lexema.equals("}")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("==", "<=", "<>", "or", "%", "*", "+", "-", "/", "and", "<", ">", "^", ">=", "hasta", "retorna", "sino", "desde", "const", "var", ")", "inicio", "fin", "tipos", "repetir", ",", "eval", "caso", "mientras", "{", "id", ";", "]", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionOperadorAux() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            ExpresionTerminal();
            doMatch(")");
            ExpresionOperador();
        } else if (token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            ExpresionTerminal();
            ExpresionOperador();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("(", "NO", "not", "cadena_", "SI", "num", "TRUE", "FALSE", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionTerminal() {
        if (token.lexema.equals("FALSE")) {
            doMatch("FALSE");
        } else if (token.lexema.equals("NO")) {
            doMatch("NO");
        } else if (token.lexema.equals("SI")) {
            doMatch("SI");
        } else if (token.lexema.equals("TRUE")) {
            doMatch("TRUE");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA)) {
            doMatch("cadena_");
        } else if (token.lexema.equals("not")) {
            doMatch("not");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-")) {
            Signo();
            NumIdTerminal();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("FALSE", "NO", "SI", "num", "+", "id", "-", "TRUE", "cadena_", "not"));
            syntaxError(expectedTokens);
        }
    }

    private void NumIdTerminal() {
        if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO)) {
            doMatch("num");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            Id();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void Signo() {
        if (token.lexema.equals("+")) {
            doMatch("+");
        } else if (token.lexema.equals("-")) {
            doMatch("-");
        } else if (token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("+", "-", "num", "id"));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("%", "*", "+", "-", "/", "<", "<=", "<>", "==", ">", ">=", "^", "and", "or"));
            syntaxError(expectedTokens);
        }
    }

    private void Id() {
        if (token.tipo.equals(Lexer.TOKEN_TYPES.ID)) {
            doMatch("id");
            IdCompuesto();
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("id"));
            syntaxError(expectedTokens);
        }
    }

    private void IdCompuesto() {
        if (token.lexema.equals("(")) {
            doMatch("(");
            Argumento();
            ListaArgumentos();
            doMatch(")");
        } else if (token.lexema.equals(".")) {
            doMatch(".");
            Id();
        } else if (token.lexema.equals("[")) {
            IdValores();
        } else if (token.lexema.equals("<=") || token.lexema.equals("retorna") || token.lexema.equals("<>") || token.lexema.equals("const") || token.lexema.equals("inicio") || token.lexema.equals("fin") || token.lexema.equals("repetir") || token.lexema.equals("caso") || token.lexema.equals("and") || token.lexema.equals("mientras") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("]") || token.lexema.equals("^") || token.lexema.equals("==") || token.lexema.equals("hasta") || token.lexema.equals("sino") || token.lexema.equals("or") || token.lexema.equals("desde") || token.lexema.equals("%") || token.lexema.equals("var") || token.lexema.equals(")") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("eval") || token.lexema.equals("{") || token.lexema.equals(";") || token.lexema.equals("<") || token.lexema.equals("}") || token.lexema.equals(">") || token.lexema.equals(">=")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("(", ".", "[", "<=", "retorna", "<>", "const", "inicio", "fin", "repetir", "caso", "and", "mientras", "id", "]", "^", "==", "hasta", "sino", "or", "desde", "%", "var", ")", "*", "+", "tipos", ",", "-", "/", "eval", "{", ";", "<", "}", ">", ">="));
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
            ArrayList<String> expectedTokens = new ArrayList<>(List.of("["));
            syntaxError(expectedTokens);
        }
    }

    private void ListaValoresMatriz() {
        if (token.lexema.equals(",")) {
            doMatch(",");
            Expresion();
            ListaValoresMatriz();
        } else if (token.lexema.equals("]")) {
            // doMatch("?");
        } else {
            ArrayList<String> expectedTokens = new ArrayList<>(List.of(",", "]"));
            syntaxError(expectedTokens);
        }
    }

}
