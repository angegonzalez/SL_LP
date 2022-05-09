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
        if(expectedTokens.size() == 1 ){
            if(expectedTokens.get(0).equals(token.lexema)){
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
        if ( token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos") ) {
            BloqueDeclaraciones();
            doMatch("inicio");
            BloqueSentencias();
            doMatch("fin");
            doMatch("BloqueSubrutinas");
        }
        else if ( token.lexema.equals("programa") ) {
            doMatch("programa");
            doMatch("id");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "const", "var", "inicio", "tipos", "programa"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueDeclaraciones() {
        if ( token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos") ) {
            Declaraciones();
            ListaDeclaraciones();
        }
        else if ( token.lexema.equals("inicio") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaDeclaraciones() {
        if ( token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("tipos") ) {
            Declaraciones();
            ListaDeclaraciones();
        }
        else if ( token.lexema.equals("inicio") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "const", "var", "tipos", "inicio"));
            syntaxError(expectedTokens);
        }
    }

    private void Declaraciones() {
        if ( token.lexema.equals("const") ) {
            doMatch("const");
            SentenciaAsignFunc();
            ListaConst();
        }
        else if ( token.lexema.equals("tipos") ) {
            doMatch("tipos");
            doMatch("id");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            ListaVarTipo();
        }
        else if ( token.lexema.equals("var") ) {
            doMatch("var");
            doMatch("id,");
            MasTiposVar();
            doMatch(":");
            TipoDato();
            ListaVarTipo();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "const", "tipos", "var"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaConst() {
        if ( token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            SentenciaAsignFunc();
            ListaConst();
        }
        else if ( token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id", "const", "var", "inicio", "tipos"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaVarTipo() {
        if ( token.lexema.equals("id:") ) {
            doMatch("id:");
            TipoDato();
            ListaVarTipo();
        }
        else if ( token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals("tipos") || token.lexema.equals("}") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id:", "const", "var", "inicio", "tipos", "}"));
            syntaxError(expectedTokens);
        }
    }

    private void MasTiposVar() {
        if ( token.lexema.equals(",") ) {
            doMatch(",");
            doMatch("id");
            MasTiposVar();
        }
        else if ( token.lexema.equals(":") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ",", ":"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoDato() {
        if ( token.lexema.equals("numero") || token.lexema.equals("logico") || token.lexema.equals("cadena") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            TipoBasico();
        }
        else if ( token.lexema.equals("vector") || token.lexema.equals("matriz") || token.lexema.equals("registro") ) {
            TipoComplejo();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "numero", "logico", "cadena", "id", "vector", "matriz", "registro"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoBasico() {
        if ( token.lexema.equals("cadena") ) {
            doMatch("cadena");
        }
        else if ( token.lexema.equals("logico") ) {
            doMatch("logico");
        }
        else if ( token.lexema.equals("numero") ) {
            doMatch("numero");
        }
        else if (  token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            doMatch("id");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "cadena", "id", "logico", "numero"));
            syntaxError(expectedTokens);
        }
    }

    private void TipoComplejo() {
        if ( token.lexema.equals("matriz") ) {
            doMatch("matriz");
            doMatch("[");
            MatrizTamanio();
            doMatch("]");
            TipoBasico();
        }
        else if ( token.lexema.equals("registro") ) {
            doMatch("registro");
            doMatch("{");
            ListaVarTipo();
            doMatch("}");
        }
        else if ( token.lexema.equals("vector") ) {
            doMatch("vector");
            doMatch("[");
            VectorTamanio();
            doMatch("]");
            TipoBasico();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "matriz", "registro", "vector"));
            syntaxError(expectedTokens);
        }
    }

    private void VectorTamanio() {
        if ( token.lexema.equals("*") ) {
            doMatch("*");
        }
        else if ( token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) ) {
            doMatch("num");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "*", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanio() {
        if ( token.lexema.equals("*") ) {
            doMatch("*");
            MatrizTamanioLista();
        }
        else if ( token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) ) {
            doMatch("num");
            MatrizTamanioLista();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "*", "num"));
            syntaxError(expectedTokens);
        }
    }

    private void MatrizTamanioLista() {
        if ( token.lexema.equals(",") ) {
            doMatch(",");
            MatrizTamanio();
        }
        else if ( token.lexema.equals("]") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaArgumentos() {
        if ( token.lexema.equals(",") ) {
            doMatch(",");
            Argumento();
            ListaArgumentos();
        }
        else if ( token.lexema.equals(")") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ",", ")"));
            syntaxError(expectedTokens);
        }
    }

    private void ListaSentencias() {
        if ( token.lexema.equals(";") ) {
            doMatch(";");
            BloqueSentencias();
        }
        else if ( token.lexema.equals("fin") ) {
            // doMatch("?");
        }
        else if (  token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            BloqueSentencias();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ";", "id", "fin"));
            syntaxError(expectedTokens);
        }
    }

    private void BloqueSentencias() {
        if ( token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            Sentencia();
            ListaSentencias();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id"));
            syntaxError(expectedTokens);
        }
    }

    private void Sentencia() {
        if ( token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            SentenciaAsignFunc();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaAsignFunc() {
        if ( token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            doMatch("id");
            SentenciaId();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id"));
            syntaxError(expectedTokens);
        }
    }

    private void SentenciaId() {
        if ( token.lexema.equals("(") ) {
            doMatch("(");
            Argumento();
            ListaArgumentos();
            doMatch(")");
        }
        else if ( token.lexema.equals("=") ) {
            doMatch("=");
            Expresion();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "(", "="));
            syntaxError(expectedTokens);
        }
    }

    private void Argumento() {
        if ( token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-") ) {
            Expresion();
        }
        else if ( token.lexema.equals(")") || token.lexema.equals(",") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "NO", "not", "cadena_", "SI", "num", "TRUE", "FALSE", "+", "id", "-", ")", ","));
            syntaxError(expectedTokens);
        }
    }

    private void Expresion() {
        if ( token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-") ) {
            Termino();
            ExpresionAux();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "NO", "not", "cadena_", "SI", "num", "TRUE", "FALSE", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionAux() {
        if ( token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("%") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.lexema.equals("<") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=") ) {
            Operador();
            Termino();
            ExpresionAux();
        }
        else if (  token.lexema.equals("const") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals(")") || token.lexema.equals("fin") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("tipos") || token.lexema.equals(";") || token.lexema.equals(",") || token.lexema.equals("]") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "==", "<=", "<>", "or", "%", "*", "+", "-", "/", "and", "<", ">", "^", ">=", "const", "var", "inicio", ")", "fin", "id", "tipos", ";", ",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void Termino() {
        if ( token.lexema.equals("NO") || token.lexema.equals("not") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || token.lexema.equals("SI") || token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("TRUE") || token.lexema.equals("FALSE") || token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-") ) {
            ExpresionTerminal();
            TerminoAux();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "NO", "not", "cadena_", "SI", "num", "TRUE", "FALSE", "+", "id", "-"));
            syntaxError(expectedTokens);
        }
    }

    private void TerminoAux() {
        if ( token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("%") || token.lexema.equals("*") || token.lexema.equals("+") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.lexema.equals("<") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=") ) {
            Operador();
            ExpresionTerminal();
            TerminoAux();
        }
        else if (  token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("const") || token.lexema.equals("%") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals(")") || token.lexema.equals("*") || token.lexema.equals("fin") || token.lexema.equals("+") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals(";") || token.lexema.equals("<") || token.lexema.equals("]") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "==", "<=", "<>", "or", "%", "*", "+", "-", "/", "and", "<", ">", "^", ">=", "==", "<=", "<>", "or", "const", "%", "var", "inicio", ")", "*", "fin", "+", "tipos", ",", "-", "/", "and", "id", ";", "<", "]", ">", "^", ">="));
            syntaxError(expectedTokens);
        }
    }

    private void ExpresionTerminal() {
        if ( token.lexema.equals("FALSE") ) {
            doMatch("FALSE");
        }
        else if ( token.lexema.equals("NO") ) {
            doMatch("NO");
        }
        else if ( token.lexema.equals("SI") ) {
            doMatch("SI");
        }
        else if ( token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.lexema.equals("+") || token.lexema.equals("-") ) {
            Signo();
            doMatch("num");
        }
        else if ( token.lexema.equals("TRUE") ) {
            doMatch("TRUE");
        }
        else if ( token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) )  {
            doMatch("cadena_");
        }
        else if ( token.lexema.equals("not") ) {
            doMatch("not");
        }
        else if (  token.lexema.equals("+") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals("-") ) {
            Signo();
            Id();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "FALSE", "NO", "SI", "+", "id", "-", "num", "+", "-", "TRUE", "cadena_", "not"));
            syntaxError(expectedTokens);
        }
    }

    private void Signo() {
        if ( token.lexema.equals("+") ) {
            doMatch("+");
        }
        else if ( token.lexema.equals("-") ) {
            doMatch("-");
        }
        else if (  token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "+", "-", "num", "id"));
            syntaxError(expectedTokens);
        }
    }

    private void Operador() {
        if ( token.lexema.equals("%") ) {
            doMatch("%");
        }
        else if ( token.lexema.equals("*") ) {
            doMatch("*");
        }
        else if ( token.lexema.equals("+") ) {
            doMatch("+");
        }
        else if ( token.lexema.equals("-") ) {
            doMatch("-");
        }
        else if ( token.lexema.equals("/") ) {
            doMatch("/");
        }
        else if ( token.lexema.equals("<") ) {
            doMatch("<");
        }
        else if ( token.lexema.equals("<=") ) {
            doMatch("<=");
        }
        else if ( token.lexema.equals("<>") ) {
            doMatch("<>");
        }
        else if ( token.lexema.equals("==") ) {
            doMatch("==");
        }
        else if ( token.lexema.equals(">") ) {
            doMatch(">");
        }
        else if ( token.lexema.equals(">=") ) {
            doMatch(">=");
        }
        else if ( token.lexema.equals("^") ) {
            doMatch("^");
        }
        else if ( token.lexema.equals("and") ) {
            doMatch("and");
        }
        else if ( token.lexema.equals("or") ) {
            doMatch("or");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "%", "*", "+", "-", "/", "<", "<=", "<>", "==", ">", ">=", "^", "and", "or"));
            syntaxError(expectedTokens);
        }
    }

    private void Id() {
        if ( token.tipo.equals(Lexer.TOKEN_TYPES.ID) ) {
            doMatch("id");
            IdCompuesto();
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "id"));
            syntaxError(expectedTokens);
        }
    }

    private void IdCompuesto() {
        if ( token.lexema.equals(".") ) {
            doMatch(".");
            Id();
        }
        else if ( token.lexema.equals("[") ) {
            IdValores();
        }
        else if (  token.lexema.equals("==") || token.lexema.equals("<=") || token.lexema.equals("<>") || token.lexema.equals("or") || token.lexema.equals("const") || token.lexema.equals("%") || token.lexema.equals("var") || token.lexema.equals("inicio") || token.lexema.equals(")") || token.lexema.equals("*") || token.lexema.equals("fin") || token.lexema.equals("+") || token.lexema.equals("tipos") || token.lexema.equals(",") || token.lexema.equals("-") || token.lexema.equals("/") || token.lexema.equals("and") || token.tipo.equals(Lexer.TOKEN_TYPES.ID) || token.lexema.equals(";") || token.lexema.equals("<") || token.lexema.equals("]") || token.lexema.equals(">") || token.lexema.equals("^") || token.lexema.equals(">=") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ".", "[", "==", "<=", "<>", "or", "const", "%", "var", "inicio", ")", "*", "fin", "+", "tipos", ",", "-", "/", "and", "id", ";", "<", "]", ">", "^", ">="));
            syntaxError(expectedTokens);
        }
    }

    private void IdValores() {
        if ( token.lexema.equals("[") ) {
            doMatch("[");
            Expresion();
            ListaValoresMatriz();
            doMatch("]");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "["));
            syntaxError(expectedTokens);
        }
    }

    private void ListaValoresMatriz() {
        if ( token.lexema.equals(",") ) {
            doMatch(",");
            Expresion();
            ListaValoresMatriz();
        }
        else if ( token.lexema.equals("]") ) {
            // doMatch("?");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( ",", "]"));
            syntaxError(expectedTokens);
        }
    }

    private void LlamadoFuncion() {
        if ( token.lexema.equals("(") ) {
            doMatch("(");
            Argumento();
            ListaArgumentos();
            doMatch(")");
        }
        else {
            ArrayList<String> expectedTokens = new ArrayList<>( List.of( "("));
            syntaxError(expectedTokens);
        }
    }

}
