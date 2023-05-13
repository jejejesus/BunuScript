import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexico {
    public static ArrayList<Token> Analisis(String input) {
        final ArrayList<Token> tokens = new ArrayList<Token>();
        final StringTokenizer st = new StringTokenizer(input);
        for (char caracter : input.toCharArray()) {
            if(caracter == 124){
                Token charerror = new Token();
                charerror.setValor("|");
                charerror.setTipo(Token.Tipos.ERROR);
                tokens.add(charerror);
            }
        }
        int comilla = 0;
        Token prevToken = new Token();
        while(st.hasMoreTokens()) {
            String palabra = st.nextToken();
            boolean matched = false;
            for (Token.Tipos tokenTipo : Token.Tipos.values()) {
                Pattern patron = Pattern.compile(tokenTipo.patron);
                Matcher matcher = patron.matcher(palabra);
                if(matcher.find()) {
                    Token tk = new Token();
                    tk.setTipo(tokenTipo);
                    tk.setValor(palabra);
                    if(tk.getTipo()==Token.Tipos.CADENA){
                        boolean caracteresExcluidos = false;
                        matched = true;
                        for (int x = 0; x < tk.getValor().length(); x++) {
                            char c = tk.getValor().charAt(x);
                            // Si no está entre a y z, ni entre A y Z, ni es un espacio
                            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú'|| c == 'ñ'|| c == 'Ñ')) {
                                caracteresExcluidos = true;
                            }
                        }
                        if(tk.getValor().contains("0") || tk.getValor().contains("1") || tk.getValor().contains("2") || tk.getValor().contains("3") || tk.getValor().contains("4") || tk.getValor().contains("5") || tk.getValor().contains("6") || tk.getValor().contains("7") || tk.getValor().contains("8") || tk.getValor().contains("9")){
                            matched = false;
                        }else if(!(tk.getValor().equalsIgnoreCase("programa") || tk.getValor().equalsIgnoreCase("imprimir")  || tk.getValor().equalsIgnoreCase("por") || tk.getValor().equalsIgnoreCase("si") || tk.getValor().equalsIgnoreCase("sino") || tk.getValor().equalsIgnoreCase("sino_entonces") || tk.getValor().equalsIgnoreCase("mientras"))){
                            if(caracteresExcluidos){
                                matched = false;
                            }else{
                                if(prevToken.getValor() != null && comilla == 0 && !(tk.getValor().contains("|"))){
                                    tk.setTipo(Token.Tipos.IDENTIFICADOR);
                                    tokens.add(tk);
                                    matched = true;
                                }else{
                                    tokens.add(tk);
                                    matched = true;
                                }

                            }
                        }

                    }else if(tk.getTipo() == Token.Tipos.OPERADOR){
                        if(tk.getValor().length()==1 && !tk.getValor().equals("|")){
                            tokens.add(tk);
                            matched = true;
                        }else{
                            if(!(tk.getValor().equals("|")))
                                matched = false;
                        }
                    }else if(tk.getTipo()==Token.Tipos.DIGITO){
                        if(!tk.getValor().contains(".")){
                            try{
                                Integer.parseInt(tk.getValor());
                                tokens.add(tk);
                                matched = true;
                            }catch(Exception e){
                                matched = false;
                            }
                        }else{
                            matched = true;
                        }

                    }else if(tk.getTipo() == Token.Tipos.NUMERO){
                        boolean decimal = false;
                        if((tk.getValor().contains("."))) decimal = true;
                        try{
                            if(decimal) {
                                Double.parseDouble(tk.getValor());
                                tokens.add(tk);
                                matched = true;
                            }
                        }catch(Exception e){
                            matched = false;
                        }
                    }else if(tk.getTipo() == Token.Tipos.LLAVE_ABRIR){
                        if(tk.getValor().length()>1){
                            matched = false;
                        }else{
                            if(!tk.getValor().equals("|")){
                                tokens.add(tk);
                                matched = true;
                            }
                        }
                    }else if(tk.getTipo() == Token.Tipos.LLAVE_CERRAR){
                        if(tk.getValor().length()>1){
                            matched = false;
                        }else{
                            if(!tk.getValor().equals("|")){
                                tokens.add(tk);
                                matched = true;
                            }
                        }
                    }else if(tk.getTipo() == Token.Tipos.PARENTESIS_ABRIR){
                        if(tk.getValor().length()>1){
                            matched = false;
                        }else{
                            if (!tk.getValor().equals("|")){
                                tokens.add(tk);
                                matched = true;
                            }

                        }
                    }else if(tk.getTipo() == Token.Tipos.PARENTESIS_CERRAR){
                        if(tk.getValor().length()>1){
                            matched = false;
                        }else{
                            if (!tk.getValor().equals("|")){
                                tokens.add(tk);
                                matched = true;
                            }

                        }
                    }else if(tk.getTipo() == Token.Tipos.CONDICION){
                        if(tk.getValor().equals("==") || tk.getValor().equals(">") || tk.getValor().equals("<") || tk.getValor().equals(">=") || tk.getValor().equals("<=") || tk.getValor().equals("!=")){
                            tokens.add(tk);
                            matched = true;
                        }else if(tk.getValor().equals("=")){
                            matched = true;
                        }else{
                            matched = false;
                        }
                    }else if(tk.getTipo() == Token.Tipos.PUNTUACION){
                        if(tk.getValor().length()>1){
                            matched = false;
                            try {
                                Double.parseDouble(tk.getValor());
                                matched = true;
                            } catch (Exception e) {

                            }
                        }else{
                            if(!(tk.getValor().equals("|"))){
                                tokens.add(tk);
                                matched = true;
                            }

                        }
                        if(tk.getValor().equals("'")){
                            if(comilla == 1){
                                comilla = 0;
                            }else{
                                comilla = 1;
                            }
                        }

                    }else if(tk.getTipo() == Token.Tipos.ERROR){
                        if(prevToken.getValor().equals(tk.getValor())){
                            matched = true;
                        }
                    }else{
                        tokens.add(tk);
                        matched = true;
                    }

                    if (!matched) {
                        if(!tk.getValor().equalsIgnoreCase("==") && !tk.getValor().equalsIgnoreCase(">=") && !tk.getValor().equalsIgnoreCase("<=") && !tk.getValor().equalsIgnoreCase("!=") && !tk.getValor().contains("|")){
                            tk.setTipo(Token.Tipos.ERROR);
                            tokens.add(tk);
                        }
                    }
                    prevToken.setTipo(tokenTipo);
                    prevToken.setValor(palabra);
                }
            }
        }

        ArrayList<Token> tokensaux2 = new ArrayList<Token>();
        ArrayList<Token> tokensaux3 = new ArrayList<Token>();
        tokensaux2.addAll(tokens);


        int tamanoArreglo = tokens.size()-1;
        for (int i = 0; i < tamanoArreglo; i++) {
            for (int j = 0; j < tamanoArreglo; j++) {
                if(tokens.get(i).getValor().equals(tokensaux2.get(j).getValor()) && tokens.get(i).getRepe() == tokensaux2.get(j).getRepe()){
                    tokens.get(i).setRepe(tokens.get(i).getRepe()+1);

                    Token tok = tokens.get(i);
                    tokensaux3.add(tok);
                }
            }
            for (Token token : tokensaux3) {
                tokensaux2.remove(token);
                tokensaux2.add(token);
            }
            tokensaux3.clear();
        }
        for (Token token : tokens) {
            int contador = 1, saltosdelinea = 1;
            String inputaux = input, aux = "";
            if(token.getValor().equals(";")){
                aux = " " + token.getValor();
            }else if(token.getTipo() == Token.Tipos.IDENTIFICADOR){
                aux = token.getValor() + " ";
            }else if(token.getTipo() == Token.Tipos.CADENA){
                aux = " " + token.getValor() + " ";
            }else{
                aux = token.getValor();
            }
            while (inputaux.indexOf(aux) > -1) {
                if(contador != token.getRepe()){
                    contador++;
                    for (int c = 0; c < inputaux.indexOf(aux); c++){
                        char caract = inputaux.charAt(c);
                        if(caract == '\n'){
                            saltosdelinea++;
                        }
                    }
                    inputaux = inputaux.substring(inputaux.indexOf(aux)+aux.length(),inputaux.length());
                }else{
                    break;
                }
            }
            for (int c = 0; c < inputaux.indexOf(aux); c++){
                char caract = inputaux.charAt(c);
                if(caract == '\n'){
                    saltosdelinea++;
                }
            }
            token.setLinea(saltosdelinea);
        }

        return tokens;
    }
}

class Token {

    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getRepe(){
        return repe;
    }

    public void setRepe(int repe){
        this.repe = repe;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) { this.linea = linea; }

    private Tipos tipo;
    private String valor;
    private int repe;
    private int linea;

    enum Tipos {
        DIGITO ("[0-9]"),
        BOOLEANO ("verdadero|falso"),
        OPERADOR("[*|/|+|-|=|"+String.valueOf((char)(45))+"]"),
        CADENA ("a|á|b|c|d|e|é|f|g|h|i|í|j|k|l|m|n|ñ|o|ó|p|q|r|s|t|u|ú|v|w|x|y|z|A|Á|B|C|D|E|É|F|G|H|I|Í|J|K|L|M|N|Ñ|O|Ó|P|Q|R|S|T|U|Ú|V|W|X|Y|Z"),
        LLAVE_ABRIR("[{]"),
        LLAVE_CERRAR("[}]"),
        PARENTESIS_ABRIR("[(]"),
        PARENTESIS_CERRAR("[)]"),
        CONDICION("==|>|<|>=|<=|!="),
        NUMERO("[0-9].[0-9]"),
        PUNTUACION("[ : | ; | ' | , | . ]"),
        GUION_BAJO("[_]"),
        PALABRA_RESERVADA("programa|imprimir|si|sino|sino_entonces|por|mientras"),
        IDENTIFICADOR("[ ]"),
        ERROR(127);

        public final String patron;
        Tipos(String s) {
            this.patron = s;
        }

        Tipos(int car){
            ArrayList<String> lista = new ArrayList<String>();
            for (int i = 0; i < car; i++) {
                char valor = (char)(i);
                if(!((valor >= 'a' && valor <= 'z') || (valor >= 'A' && valor <= 'Z') || (valor >= '0' && valor <= '9') || (valor == '*') || (valor == '/') || (valor == '+') || (valor == '-') || (valor == '=') || (valor == '{') || (valor == '}') || (valor == '(') || (valor == ')') || (valor == '<') || (valor == '>') || (valor == '!') || (valor == ',') || (valor == '.') || (valor == ';') || (valor == 39))){
                    lista.add(String.valueOf(valor));
                }
            }
            String todosCaracteres = "[";
            for (String string : lista) {
                todosCaracteres += string + "|";
            }
            todosCaracteres = todosCaracteres.substring(0,todosCaracteres.length()-1);
            todosCaracteres +="|"+(char)(124)+"]";
            this.patron = todosCaracteres;
        }
    }
}