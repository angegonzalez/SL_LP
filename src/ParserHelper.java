import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.*;

public class ParserHelper {

    public ParserHelper() throws FileNotFoundException {
        this.readRules();
        this.getNonTerminals();
        this.calculateFirstSetNoTerminals();
        this.calculatePredictionSet();
        this.calculateLexerProgram();
    }

    // public Scanner reader = new Scanner(System.in);


    public static Map<String, HashSet<String>> firstsNoTerminals = new HashMap<>();
    public Map<String, HashSet<String>> followRules = new HashMap<>();
    public TreeMap<String, HashSet<String>> predictionSet = new TreeMap<>();
    public TreeMap<String, ArrayList<String>> nonTerminalsPredictionSet = new TreeMap<>();

    ArrayList<String> rules = new ArrayList<>();

    public ArrayList<String> nonTerminals = new ArrayList<>();

    String noTerminalRegex = "[A-Z]";
    Pattern pattern = Pattern.compile(noTerminalRegex);

    public void getNonTerminals() throws FileNotFoundException {
        Scanner reader = new Scanner(new File("grammar.txt"));
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] parts = line.split("->");
            if (!nonTerminals.contains(parts[0])) {
                nonTerminals.add(parts[0]);
            }
        }
        // System.out.println("cada no terminal:" + nonTerminals.toString());

    }

    private boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    private boolean isTerminal(String evaluate) {
        Matcher matcher = pattern.matcher(evaluate);
        return !matcher.find() || evaluate.equals("TRUE") || evaluate.equals("FALSE") || evaluate.equals("SI") || evaluate.equals("NO");
    }

    private boolean addToSet(Map<String, HashSet<String>> myHash, String key, HashSet<String> addSet) {
        if (!myHash.containsKey(key)) {
            // System.out.println("No se pudo encontrar la llave " + key);
            return false;
        }
        HashSet<String> previousSet = myHash.get(key);
        HashSet<String> concatSet = new HashSet<>() {{
            addAll(myHash.get(key));
        }};
        concatSet.addAll(addSet);
        myHash.put(key, concatSet);
        return myHash.get(key).size() > previousSet.size();
    }

    private HashSet<String> removeEpsilon(HashSet<String> set) {
        set.remove("ε");
        return set;
    }

    private void readRules() throws FileNotFoundException {
        Scanner reader = new Scanner(new File("grammar.txt"));
        while (reader.hasNextLine()) {
            rules.add(reader.nextLine());
        }
    }

    private void calculateFirstSetNoTerminals() {
        for (int i = rules.size() - 1; i >= 0; i--) {
            String[] ruleParts = rules.get(i).split("->");
            String noTerminal = ruleParts[0];
            String alpha = ruleParts[1];
            String[] rightPart = alpha.split(" ");
            for (String subpart : rightPart
            ) {
                // System.out.println(subpart);
                if (isTerminal(subpart.charAt(0) + "") || subpart.equals("NO") || subpart.equals("SI") || subpart.equals("TRUE") || subpart.equals("FALSE")) {
                    if (!firstsNoTerminals.containsKey(noTerminal)) {
                        firstsNoTerminals.put(noTerminal, new HashSet<>() {{
                            add(subpart);
                        }});
                    } else {
                        HashSet<String> firstsSet = firstsNoTerminals.get(noTerminal);
                        firstsSet.add(subpart);
                        firstsNoTerminals.put(noTerminal, firstsSet);
                    }
                    break;
                } else {
                    HashSet<String> firstsSetNoTerminal = firstsNoTerminals.get(subpart);
                    HashSet<String> firstsSetNoTerminalAdd = new HashSet<>() {{
                        addAll(firstsSetNoTerminal);
                    }};
                    firstsSetNoTerminalAdd.remove("ε");

                    if (!firstsNoTerminals.containsKey(noTerminal)) {
                        firstsNoTerminals.put(noTerminal, firstsSetNoTerminalAdd);
                    } else {
                        HashSet<String> firstsSet = firstsNoTerminals.get(noTerminal);
                        firstsSet.addAll(firstsSetNoTerminalAdd);
                        firstsNoTerminals.put(noTerminal, firstsSet);
                    }
                    if (!firstsSetNoTerminal.contains("ε")) {
                        break;
                    }
                }

            }

        }
//        System.out.println("Primeros de cada no terminal:");
//        firstsNoTerminals.forEach((key, value) -> System.out.println(key + ":" + Arrays.toString(value.toArray())));
    }

    private HashSet<String> firstStar(String[] alpha) {
        if (alpha.length == 0) {
            return new HashSet<>() {{
                add("ε");
            }};
        }
        String firstItem = alpha[0];
        if (isTerminal(firstItem)) {
            return new HashSet<>() {{
                add(firstItem);
            }};
        }
        // System.out.println(firstItem);
        HashSet<String> firstsNoTerminal = new HashSet<>() {{
            addAll(firstsNoTerminals.get(firstItem));
        }};

        if (!firstsNoTerminal.contains("ε")) {
            return firstsNoTerminals.get(firstItem);
        } else {
            HashSet<String> firstALessEpsilon = removeEpsilon(firstsNoTerminal);
            String[] rest = Arrays.copyOfRange(alpha, 1, alpha.length);
            HashSet<String> firstStarRest = firstStar(rest);
            firstALessEpsilon.addAll(firstStarRest);
            return firstALessEpsilon;
        }
    }

    private void calculateFollowSet() {
        String firstSymbol = nonTerminals.get(0);
        for (String noTerminal : nonTerminals) {
            followRules.put(noTerminal, new HashSet<>());
        }
        for (String A : nonTerminals) {
            for (String production : rules) {
                String[] words = production.split("->");
                // String B = words[0];
                String aAtw = words[1];
                boolean nonTerminalFound = false;
                String[] partsOfaAtw = aAtw.split(" ");

                int i = 0;
                for (; i < partsOfaAtw.length; i++) {
                    if (partsOfaAtw[i].equals(A)) {
                        nonTerminalFound = true;
                        break;
                    }
                }

                if (!nonTerminalFound || (i == partsOfaAtw.length - 1)) break;

                String t = partsOfaAtw[i + 1];

                if (isTerminal(t)) {
                    addToSet(followRules, A, new HashSet<>() {{
                        add(t);
                    }});
                }

            }
        }
        addToSet(followRules, firstSymbol, new HashSet<>() {{
            add("$");
        }});
        boolean changed = true;
        while (changed) {
            changed = false;
            boolean added;
            for (String production : rules) {
                String[] words = production.split("->");
                String B = words[0];
                String aAw = words[1];
                String[] aAwParts = aAw.split(" ");

                for (int termNum = 0; termNum < aAwParts.length; termNum++) {
                    String A = aAwParts[termNum];
                    if (!isNonTerminal(A)) {
                        continue;
                    }

                    String[] w = Arrays.copyOfRange(aAwParts, termNum + 1, aAwParts.length);
                    HashSet<String> firstStarOfw = firstStar(w);
                    HashSet<String> firstStarOfwAdd = new HashSet<>() {{
                        addAll(firstStarOfw);
                    }};
                    HashSet<String> firstStarOfwLessEpsilon = removeEpsilon(firstStarOfwAdd);
                    added = addToSet(followRules, A, firstStarOfwLessEpsilon);
                    if (added) changed = true;
                    if (firstStarOfw.contains("ε")) {
                        HashSet<String> followB = new HashSet<>() {{
                            addAll(followRules.get(B));
                        }};
                        added = addToSet(followRules, A, followB);
                        if (added) changed = true;
                    }
                }
                String A = aAwParts[aAwParts.length - 1];
                if (!isTerminal(A)) {
                    HashSet<String> followB = followRules.get(B);
                    added = addToSet(followRules, A, followB);
                    if (added) changed = true;
                }

            }
        }
        System.out.println("Siguientes de cada no terminal:");
        followRules.forEach((key, value) -> System.out.println(key + ":" + Arrays.toString(value.toArray())));
    }

    public void calculatePredictionSet() throws FileNotFoundException {
        calculateFollowSet();

        ArrayList<String> productions = new ArrayList<>();
        // Map<String, HashSet<String>> productionsPredictionSet = new HashMap<>();
        Scanner reader = new Scanner(new File("grammar.txt"));

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            productions.add(line);
        }
        for (String production : productions) {
            String[] ruleParts = production.split("->");
            String[] productionParts = ruleParts[1].split(" ");
            HashSet<String> firstRule = firstStar(productionParts);
            if (firstRule.contains("ε")) {
                firstRule.remove("ε");
                firstRule.addAll(followRules.get(ruleParts[0]));
                predictionSet.put(production, firstRule);
            } else {
                predictionSet.put(production, firstRule);
            }
        }

        for (String nonTerminal : nonTerminals) {
            ArrayList<String> nonTerminalSymbols = new ArrayList<>();
            predictionSet.forEach((key, value) -> {
                if (nonTerminal.equals(key.split("->")[0])) {
                    nonTerminalSymbols.addAll(value);
                }
            });
            nonTerminalsPredictionSet.put(nonTerminal, nonTerminalSymbols);
        }

        System.out.println("\nConjunto de prediccion: ");
         predictionSet.forEach((key, value) -> System.out.println(key + ":" + Arrays.toString(value.toArray())));

    }


    public void calculateLexerProgram() {
        for (String nonTerminal : nonTerminals) {
            generateParserFunction(nonTerminal);
        }
    }

    public void generateParserFunction(String nonTerminal) {
        AtomicReference<String> functionBody = new AtomicReference<>("");
        AtomicInteger count = new AtomicInteger();
        String functionSignature;
        if (nonTerminal.equals("Programa")) {
            functionSignature = String.format("\npublic void %s() {\n", nonTerminal);
        } else {
            functionSignature = String.format("\nprivate void %s() {\n", nonTerminal);
        }
        AtomicReference<String> idCondition = new AtomicReference<>("");
        functionBody.set(functionBody + functionSignature);
        predictionSet.forEach((key, value) -> {
            String[] ruleParts = key.split("->");
            String rightPart = ruleParts[0];
            String[] leftPart = ruleParts[1].split(" ");
            if (nonTerminal.equals(rightPart)) {
                if (count.get() == 0) {
                    functionBody.set(functionBody + "\tif ( ");
                    generateCondition(functionBody, value);
                    functionBody.set(functionBody + " {");
                    generateConditionBody(leftPart, functionBody);
                    functionBody.set(functionBody + "\n\t}");
                } else {
                    if (value.contains("id")) {
                        idCondition.set(idCondition + "\n\telse if ( " + " ");
                        generateCondition(idCondition, value);
                        idCondition.set(idCondition + " {");
                        generateConditionBody(leftPart, idCondition);
                        idCondition.set(idCondition + "\n\t}");
                    } else {
                        functionBody.set(functionBody + "\n\telse if ( ");
                        generateCondition(functionBody, value);
                        functionBody.set(functionBody + " {");
                        generateConditionBody(leftPart, functionBody);
                        functionBody.set(functionBody + "\n\t}");
                    }

                }
                count.getAndIncrement();
            }
        });
        if (!Objects.equals(idCondition.get(), "")) {
            functionBody.set(functionBody + idCondition.get());
        }
        generateErrorCondition(functionBody, nonTerminal);
        functionBody.set(functionBody + "\n}");
        System.out.println(functionBody.get());
    }

    private void generateCondition(AtomicReference<String> functionBody, HashSet<String> value) {
        int j = 0;
        for (String symbol : value) {
            String condition;
            if (j < value.size() - 1) {
                condition = switch (symbol) {
                    case "id" -> "token.tipo.equals(Lexer.TOKEN_TYPES.ID) || ";
                    case "num" -> "token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) || ";
                    case "cadena_" -> "token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) || ";
                    default -> String.format("token.lexema.equals(\"%s\") || ", symbol);
                };
            } else {
                condition = switch (symbol) {
                    case "id" -> "token.tipo.equals(Lexer.TOKEN_TYPES.ID) )";
                    case "num" -> "token.tipo.equals(Lexer.TOKEN_TYPES.TK_NUMERO) )";
                    case "cadena_" -> "token.tipo.equals(Lexer.TOKEN_TYPES.TK_CADENA) ) ";
                    default -> String.format("token.lexema.equals(\"%s\") )", symbol);
                };
            }
            functionBody.set(functionBody + condition);
            j++;
        }
    }

    private void generateConditionBody(String[] ruleParts, AtomicReference<String> functionBody) {
        for (String symbol : ruleParts) {
            if (isNonTerminal(symbol)) {
                String functionCall = String.format("\n\t\t%s();", symbol);
                functionBody.set(functionBody + functionCall);
            } else {
                String functionCall;
                if (symbol.equals("ε")) {
                    functionCall = String.format("\n\t\t// doMatch(\"%s\");", symbol);
                } else {
                    functionCall = String.format("\n\t\tdoMatch(\"%s\");", symbol);
                }

                functionBody.set(functionBody + functionCall);
            }
        }
    }

    private void generateErrorCondition(AtomicReference<String> functionBody, String nonTerminal) {
        String elseStatement = "\n\telse {\n\t\tArrayList<String> expectedTokens = new ArrayList<>( List.of(";
        functionBody.set(functionBody + elseStatement);
        int j = 0;
        ArrayList<String> symbols = nonTerminalsPredictionSet.get(nonTerminal);
        for (String symbol : symbols) {
            String condition;
            if (j < symbols.size() - 1) {
                condition = String.format(" \"%s\",", symbol);
            } else {
                condition = String.format(" \"%s\"));", symbol);
            }
            functionBody.set(functionBody + condition);
            j++;
        }
        String callErrorFunction = "\n\t\tsyntaxError(expectedTokens);\n\t}";
        functionBody.set(functionBody + callErrorFunction);
    }
}
