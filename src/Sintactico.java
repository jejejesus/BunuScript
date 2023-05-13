import java.util.ArrayList;

public class Sintactico {

    static boolean est_CadenaCerrada = false;
    static boolean est_DeclarandoVariable = false;
    static boolean est_IgualDeVariable = false;
    static boolean est_VariableCadena = false;
    static boolean est_ParentencisAbiertoImprimir = false;
    static ArrayList<Boolean> est_ParentecisAbierto = new ArrayList<Boolean>();
    static boolean est_DentroImprimir = false;
    static boolean est_EnCadena = false;
    static ArrayList<Integer> est_PorAbierto = new ArrayList<Integer>();
    static int est_EstadoCondicion = 1;
    static ArrayList<Boolean> est_DentroPor = new ArrayList<Boolean>();
    static boolean est_DentroCondicion = false;
    static boolean est_SigueCondicionPor = false;
    static ArrayList<Integer> est_LlavesAbiertas = new ArrayList<Integer>();
    static ArrayList<Integer> est_LlavesCerradas = new ArrayList<Integer>();
    static ArrayList<Boolean> est_DentroSi = new ArrayList<Boolean>();
    static ArrayList<Integer> est_SiAbierto = new ArrayList<Integer>();
    static ArrayList<Boolean> est_DentroMientras = new ArrayList<Boolean>();
    static ArrayList<Integer> est_MientrasAbierto = new ArrayList<Integer>();

    public static void Analisis(ArrayList<Token> _tokens, String _codigo) {
        Token tokenAnterior = null;
        boolean error = false, dentroPrograma = false, sintaxisPrograma = false;

        for(Token token : _tokens) {

            if(token.getTipo() == Token.Tipos.LLAVE_ABRIR) {
                est_LlavesAbiertas.add(1);
            }
            else if(token.getTipo() == Token.Tipos.LLAVE_CERRAR) {
                est_LlavesCerradas.add(1);
            }

            if (tokenAnterior != null) {

                if (sintaxisPrograma) {
                    if (tokenAnterior.getValor().equals("programa")) {
                        if (!(token.getTipo() == Token.Tipos.IDENTIFICADOR)) {
                            error = true;
                        }
                    } else if (tokenAnterior.getTipo() == Token.Tipos.IDENTIFICADOR) {
                        if (!(token.getTipo() == Token.Tipos.LLAVE_ABRIR)) {
                            error = true;
                        } else {
                            sintaxisPrograma = false;
                            dentroPrograma = true;
                            tokenAnterior = null;
                        }
                    }
                }
                else if(dentroPrograma) {

                    if(!(token.getTipo() == Token.Tipos.IDENTIFICADOR || token.getTipo() == Token.Tipos.PALABRA_RESERVADA || !est_DentroPor.isEmpty() || !est_DentroSi.isEmpty() || !est_DentroMientras.isEmpty() || est_DeclarandoVariable || est_DentroImprimir || token.getTipo() == Token.Tipos.LLAVE_CERRAR)) {
                        error = true;
                    }
                    else if(!est_EnCadena && token.getTipo() == Token.Tipos.IDENTIFICADOR && token.getValor().equals("programa")) {
                        error = true;
                    }
                    else if(!est_DentroImprimir && token.getTipo() == Token.Tipos.IDENTIFICADOR && !est_DentroCondicion && !est_SigueCondicionPor && !est_IgualDeVariable) {
                        if(est_DeclarandoVariable) {
                            error = true;
                        } else {
                            est_DeclarandoVariable = true;
                        }
                    }
                    else if(est_DeclarandoVariable) {
                        error = sintaxisIdentificador(token, tokenAnterior, error);
                    }
                    else if(token.getTipo() == Token.Tipos.PALABRA_RESERVADA && token.getValor().equals("imprimir")) {
                        est_DentroImprimir = true;
                    }
                    else if(!est_DentroImprimir && token.getTipo() == Token.Tipos.IDENTIFICADOR || token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.NUMERO) {
                        est_DentroCondicion = true;
                        error = sintaxisCondicional(token, tokenAnterior, error);
                    }
                    else if(est_DentroCondicion) {
                        error = sintaxisCondicional(token, tokenAnterior, error);
                    }
                    else if(est_DentroImprimir) {
                        error = sintaxisImprimir(token, tokenAnterior, error);
                    }
                    else if(token.getTipo() == Token.Tipos.PALABRA_RESERVADA && token.getValor().equals("por")) {
                        error = sintaxisPor(token, tokenAnterior, error);
                    }
                    else if(!est_DentroPor.isEmpty()) {
                        error = sintaxisPor(token, tokenAnterior, error);
                    }
                    else if(token.getTipo() == Token.Tipos.PALABRA_RESERVADA && token.getValor().equals("si")) {
                        error = sintaxisSi(token, tokenAnterior, error);
                    }
                    else if(!est_DentroSi.isEmpty()) {
                        error = sintaxisSi(token, tokenAnterior, error);
                    }
                    else if(token.getTipo() == Token.Tipos.PALABRA_RESERVADA && token.getValor().equals("mientras")) {
                        error = sintaxisMientras(token, tokenAnterior, error);
                    }
                    else if(!est_DentroMientras.isEmpty()) {
                        error = sintaxisMientras(token, tokenAnterior, error);
                    }
                    else if(token.getTipo() == Token.Tipos.LLAVE_CERRAR) {
                        dentroPrograma = false;
                    }

                } else{
                    if(!token.getValor().equals("programa")) {
                        error = true;
                    } else{
                        sintaxisPrograma = true;
                    }
                }

            } else {
                if(!token.getValor().equals("programa")) {
                    error = true;
                } else {
                    sintaxisPrograma = true;
                }
            }
            if(error) {
                if(tokenAnterior != null) {
                    throw new RuntimeException(" ERROR SINTÁCTICO EN: " + tokenAnterior.getValor() +
                            "\n EN LÍNEA: " + tokenAnterior.getLinea());
                } else {
                    throw new RuntimeException(" ERROR SINTÁCTICO EN: " + _tokens.get(0).getValor() +
                            "\n EN LÍNEA: " + _tokens.get(0).getLinea());
                }
            }

            tokenAnterior = token;
        }

        // Verificamos si hay llaves sin cerrar
        if(est_LlavesAbiertas.size() != est_LlavesCerradas.size()) {
            System.out.print("ERROR SINTÁCTICO EN : {");
            int contarUnaVez = 1;
            for (int i = 0; i < _codigo.length(); i++) {
                char caracter = _codigo.charAt(i);
                if(caracter == '\n') {
                    contarUnaVez++;
                }
            }
            throw new RuntimeException(" ERROR SINTÁCTICO EN: {" +
                    "\n EN LÍNEA: " + contarUnaVez + ", SE ESPERABA }");
        }

        if(!error) {
            System.out.println(" COMPILADO SIN ERRORES :)");
        }
    }

    private static boolean sintaxisIdentificador(Token token, Token tokenAnterior, boolean error) {
        if(token.getTipo() == Token.Tipos.OPERADOR && token.getValor().equals("=") && est_EstadoCondicion == 1) {
            if(!est_IgualDeVariable) {
                est_IgualDeVariable = true;
            } else {
                error = true;
            }

        }
        else if(token.getTipo() == Token.Tipos.CONDICION && !est_EnCadena && est_IgualDeVariable) {
            error = true;
        }
        else if(est_IgualDeVariable) {
            if(token.getValor().equals("'") && !est_EnCadena) {
                if(tokenAnterior.getValor().equals("=")) {
                    est_VariableCadena = true;
                }else if(est_CadenaCerrada) {
                    error = true;
                }
                else{
                    error = true;
                }
            }
            if(!est_VariableCadena) {
                if(token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.DIGITO  || token.getTipo() == Token.Tipos.OPERADOR || token.getTipo() == Token.Tipos.PARENTESIS_ABRIR || token.getTipo() == Token.Tipos.PARENTESIS_CERRAR || token.getTipo() == Token.Tipos.IDENTIFICADOR) {
                    if(token.getTipo() == Token.Tipos.OPERADOR) {
                        if(token.getValor().equals("=")) {
                            error = true;
                        }
                        else if(!(tokenAnterior.getTipo() == Token.Tipos.NUMERO || tokenAnterior.getTipo() == Token.Tipos.DIGITO || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR || tokenAnterior.getTipo() == Token.Tipos.IDENTIFICADOR)) {
                            error = true;
                        }
                    }
                    else if(token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.IDENTIFICADOR) {
                        if(!(tokenAnterior.getTipo() == Token.Tipos.OPERADOR || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR)) {
                            error = true;
                        }
                    }
                    else if(token.getTipo() == Token.Tipos.PARENTESIS_ABRIR) {
                        est_ParentecisAbierto.add(true);
                        if(!(tokenAnterior.getTipo() == Token.Tipos.OPERADOR)) {
                            error = true;
                        }
                    } else {
                        if(est_ParentecisAbierto.isEmpty()) {
                            error = true;
                        }
                        else if((tokenAnterior.getTipo() == Token.Tipos.OPERADOR)) {
                            error = true;
                        } else {
                            int cantpar = est_ParentecisAbierto.size() - 1;
                            est_ParentecisAbierto.remove(cantpar);
                        }
                    }
                }
                else if(token.getTipo() == Token.Tipos.CONDICION) {
                    error = true;
                }
                else if(!token.getValor().equals(";")) {
                    error = true;
                }
            } else {
                if(token.getValor().equals("'") && !est_EnCadena) {
                    if(est_CadenaCerrada) {
                        error = true;
                    } else {
                        est_EnCadena = true;
                    }
                }
                else if(token.getValor().equals("'") && est_EnCadena) {
                    est_EnCadena = false;
                    est_CadenaCerrada = true;
                }
                else if(est_CadenaCerrada && !token.getValor().equals(";")) {
                    error = true;
                }
            }
            if(token.getValor().equals(";")) {
                if(est_EnCadena || tokenAnterior.getTipo() == Token.Tipos.OPERADOR || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR) {
                    error = true;
                }
                else if(!est_ParentecisAbierto.isEmpty()) {
                    error = true;
                }
                else if(!est_PorAbierto.isEmpty() && est_PorAbierto.get(est_PorAbierto.size()-1) == 1) {
                    est_SigueCondicionPor = true;
                    est_PorAbierto.set(est_PorAbierto.size()-1, 2);
                    est_CadenaCerrada = false;
                    est_DeclarandoVariable = false;
                    est_IgualDeVariable = false;
                    est_VariableCadena = false;
                }
                else if(!est_PorAbierto.isEmpty() && est_PorAbierto.get(est_PorAbierto.size()-1) == 3) {
                    est_PorAbierto.set(est_PorAbierto.size()-1, 4);
                    est_CadenaCerrada = false;
                    est_DeclarandoVariable = false;
                    est_IgualDeVariable = false;
                    est_VariableCadena = false;
                } else {
                    est_CadenaCerrada = false;
                    est_DeclarandoVariable = false;
                    est_IgualDeVariable = false;
                    est_VariableCadena = false;
                }
            }
        } else {
            if(token.getTipo() == Token.Tipos.CONDICION) {
                est_DentroCondicion = true;
                if(est_EstadoCondicion == 1) {
                    est_EstadoCondicion = 2;

                }
                error = sintaxisCondicional(token, tokenAnterior, error);
            }
            else if(est_DentroCondicion) {
                error = sintaxisCondicional(token, tokenAnterior, error);
            } else {
                error = true;
            }

        }
        
        return error;
    }

    private static boolean sintaxisImprimir(Token token, Token tokenAnterior, boolean error) {
        if(token.getTipo() == Token.Tipos.PARENTESIS_ABRIR && tokenAnterior.getValor().equals("imprimir") && !est_ParentencisAbiertoImprimir){
            est_ParentencisAbiertoImprimir = true;
        }else if(token.getValor().equals("'") && !est_EnCadena && est_ParentecisAbierto.isEmpty()){
            if(est_CadenaCerrada){
                error = true;
            }else if(!(tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR)){
                error = true;
            }else{
                est_EnCadena = true;
                est_VariableCadena = true;
            }
        }else if(token.getValor().equals("'") && est_EnCadena){
            est_EnCadena = false;
            est_CadenaCerrada = true;
        }else if(est_CadenaCerrada && !token.getValor().equals(")") && tokenAnterior.getValor().equals("'")){
            error = true;
        }else if (token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && !est_ParentencisAbiertoImprimir){
            error = true;
        }else if(!est_EnCadena && token.getTipo() == Token.Tipos.PARENTESIS_ABRIR && est_ParentencisAbiertoImprimir){
            est_ParentecisAbierto.add(true);
        }else if (!est_VariableCadena && tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR && !(token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.IDENTIFICADOR || token.getTipo() == Token.Tipos.PARENTESIS_ABRIR)){
            error = true;
        }else if(!est_VariableCadena && token.getValor().equals("'")){
            error = true;
        }else if(!est_VariableCadena && token.getTipo() == Token.Tipos.OPERADOR && (tokenAnterior.getTipo() == Token.Tipos.OPERADOR || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR)){
            error = true;
        }else if(!est_EnCadena && token.getValor().equals("=")){
            error = true;
        }else if(!est_EnCadena && (token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.IDENTIFICADOR) && (tokenAnterior.getTipo() == Token.Tipos.DIGITO || tokenAnterior.getTipo() == Token.Tipos.NUMERO || tokenAnterior.getTipo() == Token.Tipos.IDENTIFICADOR  || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR)){
            error = true;
        } else if(!est_EnCadena && token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && est_ParentencisAbiertoImprimir && !est_ParentecisAbierto.isEmpty()){
            int cantpar = est_ParentecisAbierto.size() - 1;
            est_ParentecisAbierto.remove(cantpar);
        }else if(!est_EnCadena && token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && !(tokenAnterior.getTipo() == Token.Tipos.DIGITO || tokenAnterior.getTipo() == Token.Tipos.NUMERO || tokenAnterior.getTipo() == Token.Tipos.IDENTIFICADOR  || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR || tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_ABRIR || tokenAnterior.getValor().equals("'"))){
            error = true;
        }else if(!est_EnCadena && token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && est_ParentencisAbiertoImprimir && est_ParentecisAbierto.isEmpty()){
            est_ParentencisAbiertoImprimir = false;
        }else if(!est_EnCadena && token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && !est_ParentencisAbiertoImprimir && est_ParentecisAbierto.isEmpty()){
            error = true;
        }
        else if(token.getValor().equals(";")){
            if(est_EnCadena){
                error = true;
            }else if( !(tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR)){
                error = true;
            }else if(!est_ParentecisAbierto.isEmpty()){
                error = true;
            }else if(est_ParentencisAbiertoImprimir){
                error = true;
            }else{
                est_CadenaCerrada = false;
                est_DentroImprimir = false;
                est_VariableCadena = false;
            }
        }
        return error;
    }

    private static boolean sintaxisSi(Token token, Token tokenAnterior, boolean error) {
        /*
            LOS ESTADOS DE IF SON:
                0 - NO SE HA HABIERTO EL PARENTESIS
                1 - ENTRE PARENTESIS ABRIR Y CONDICION
                2 - ENTRE CONDICION Y PARENTESIS CERRAR
                3 - SE CERRO EL PARENTESIS
                4 - SE ABRIO LLAVE
            EL ESTADO SE ELIMINA CUANDO SE CIERRA LA LLAVE
         */
        if(!est_EnCadena && token.getValor().equals("si")) {
            if(est_SiAbierto.isEmpty()) {
                est_SiAbierto.add(0);
                est_DentroSi.add(true);
                return false;
            } else {
                if(est_SiAbierto.get(est_SiAbierto.size()-1) == 4) {
                    est_SiAbierto.add(0);
                    est_DentroSi.add(true);
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            if(est_SiAbierto.get(est_SiAbierto.size()-1) == 4) {
                if(token.getValor().equals("si") && !est_EnCadena) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }else if(token.getTipo() == Token.Tipos.LLAVE_CERRAR) {
                    est_DentroSi.remove(est_DentroSi.size()-1);
                    est_SiAbierto.remove(est_SiAbierto.size()-1);
                }else if(!est_DentroSi.isEmpty()) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }else if(token.getValor().equals("for") && !est_EnCadena) {
                    error = sintaxisPor(token, tokenAnterior, error);
                }else if(!est_DentroPor.isEmpty()) {
                    error = sintaxisPor(token, tokenAnterior, error);
                }else if(token.getValor().equals("while") && !est_EnCadena) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }else if(!est_DentroMientras.isEmpty()) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }
            }else if(est_SiAbierto.get(est_SiAbierto.size()-1) == 0) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_ABRIR && tokenAnterior.getValor().equals("si")) {
                    est_SiAbierto.set(est_SiAbierto.size()-1, 1);
                } else {
                    return true;
                }
            }else if(est_SiAbierto.get(est_SiAbierto.size()-1) == 2) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && tokenAnterior.getValor().equals(";")) {
                    est_SiAbierto.set(est_SiAbierto.size()-1,3);
                } else {
                    return true;
                }
            }else if(est_SiAbierto.get(est_SiAbierto.size()-1) == 3) {
                if(tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR && token.getTipo() == Token.Tipos.LLAVE_ABRIR) {
                    est_SiAbierto.set(est_SiAbierto.size()-1,4);
                } else {
                    return true;
                }
            }
        }
        
        return error;
    }

    private static boolean sintaxisCondicional(Token token, Token tokenAnterior, boolean error) {
        /*
            ESTADO CONDICIÓN:
                0 - CONDICIÓN VACÍA
                1 - INICIADA
                2 - PRIMER VALOR LEÍDO
                3 - COMPARADOR LEÍDO
                4 - SEGUNDO VALOR LEÍDO
         */
        if(est_EstadoCondicion == 1) {
            if(token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.IDENTIFICADOR) {
                est_EstadoCondicion = 2;
            } else {
                return true;
            }
        }else if(est_EstadoCondicion == 2) {
            if(token.getTipo() == Token.Tipos.CONDICION) {
                est_EstadoCondicion = 3;
            } else {
                return true;
            }
        }else if(est_EstadoCondicion == 3) {
            if(token.getTipo() == Token.Tipos.NUMERO || token.getTipo() == Token.Tipos.DIGITO || token.getTipo() == Token.Tipos.IDENTIFICADOR) {
                est_EstadoCondicion = 4;
            } else {
                return true;
            }
        }else if(est_EstadoCondicion == 4) {
            if(token.getValor().equals(";")) {
                est_EstadoCondicion = 1;
                est_DeclarandoVariable = false;
                est_DentroCondicion = false;
                if(est_SigueCondicionPor &&  est_PorAbierto.get(est_PorAbierto.size()-1) == 2) {
                    est_SigueCondicionPor = false;
                    est_PorAbierto.set(est_PorAbierto.size()-1, 3);
                }else if(!est_SiAbierto.isEmpty() && est_SiAbierto.get(est_SiAbierto.size()-1) == 1) {
                    est_SiAbierto.set(est_SiAbierto.size()-1,2);
                }
                else if(!est_MientrasAbierto.isEmpty() && est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 1) {
                    est_MientrasAbierto.set(est_MientrasAbierto.size()-1,2);
                }
            } else {
                return true;
            }
        }
        return error;
    }

    private static boolean sintaxisPor(Token token, Token tokenAnterior, boolean error) {
        /*
             LOS ESTADOS DE POR SON:
                 0 - NO SE HA ABIERTO EL PARÉNTESIS
                 1 - SE ABRIÓ EL PARÉNTESIS PERO ES ANTES DEL PRIMER ;
                 2 - ENTRE EL PRIMER Y SEGUNDO ;
                 3 - ENTRE EL ULTIMO ; Y EL PARÉNTESIS
                 4 - SE REALIZÓ LA OPERACIÓN
                 5 - SE CERRO EL PARÉNTESIS
                 6 - SE ABRIÓ LLAVE
             EL ESTADO SE ELIMINA CUANDO SE CIERRA LA LLAVE
         */
        if(!est_EnCadena && token.getValor().equals("por")) {
            if(est_PorAbierto.isEmpty()) {
                est_PorAbierto.add(0);
                est_DentroPor.add(true);
                return false;
            } else {
                if(est_PorAbierto.get(est_PorAbierto.size()-1) == 6) {
                    est_PorAbierto.add(0);
                    est_DentroPor.add(true);
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            if(est_PorAbierto.get(est_PorAbierto.size()-1) == 6) {
                if(token.getValor().equals("si") && !est_EnCadena) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }else if(!est_DentroSi.isEmpty()) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }else if(token.getValor().equals("mientras") && !est_EnCadena) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }else if(!est_DentroMientras.isEmpty()) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }else if(token.getTipo() == Token.Tipos.LLAVE_CERRAR) {
                    est_DentroPor.remove(est_DentroPor.size()-1);
                    est_PorAbierto.remove(est_PorAbierto.size()-1);
                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 0) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_ABRIR && tokenAnterior.getValor().equals("por")) {
                    est_PorAbierto.set(est_PorAbierto.size()-1, 1);
                } else {
                    return true;
                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 1) {
                if(token.getTipo() == Token.Tipos.IDENTIFICADOR && tokenAnterior.getTipo() == Token.Tipos.LLAVE_ABRIR) {
                    est_DeclarandoVariable = true;
                    est_PorAbierto.set(est_PorAbierto.size()-1,2);
                } else {
                    return true;
                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 2) {
                if(est_EstadoCondicion != 4) {
                    return sintaxisCondicional(token, tokenAnterior, error);
                } else {
                    est_DentroCondicion = false;
                    est_EstadoCondicion = 1;
                    est_PorAbierto.set(est_PorAbierto.size()-1,3);

                    if(!token.getValor().equals(";")) {
                        return true;
                    } else {
                        est_SigueCondicionPor = false;
                    }

                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 3) {
                if(token.getTipo() == Token.Tipos.IDENTIFICADOR && tokenAnterior.getValor().equals(";")) {
                    est_DeclarandoVariable = true;
                    est_PorAbierto.set(est_PorAbierto.size()-1,4);
                } else {
                    return true;
                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 4) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_CERRAR) {
                    est_PorAbierto.set(est_PorAbierto.size()-1,5);
                } else {
                    return true;
                }
            }else if(est_PorAbierto.get(est_PorAbierto.size()-1) == 5) {
                if(token.getTipo() == Token.Tipos.LLAVE_ABRIR && tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR) {
                    est_PorAbierto.set(est_PorAbierto.size()-1,6);
                } else {
                    return true;
                }
            }
        }

        return error;
    }

    private static boolean sintaxisMientras(Token token, Token tokenAnterior, boolean error) {
        //LOS ESTADOS DE IF SON:
        //0 - NO SE HA HABIERTO EL PARENTESIS
        //1 - ENTRE PARENTESIS ABRIR Y CONDICION
        //2 - ENTRE CONDICION Y PARENTESIS CERRAR
        //3 - SE CERRO EL PARENTESIS
        //4 - SE ABRIO LLAVE
        // SE ELIMINA CUANDO SE CIERRA LA LLAVE
        if(!est_EnCadena && token.getValor().equals("while")) {
            if(est_MientrasAbierto.isEmpty()) {
                est_MientrasAbierto.add(0);
                est_DentroMientras.add(true);
                return false;
            } else {

                if(est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 4) {
                    est_MientrasAbierto.add(0);
                    est_DentroMientras.add(true);
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            if(est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 4) {
                if(token.getValor().equals("while") && !est_EnCadena) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }
                else if(token.getTipo() == Token.Tipos.LLAVE_CERRAR) {
                    est_DentroMientras.remove(est_DentroMientras.size()-1);
                    est_MientrasAbierto.remove(est_MientrasAbierto.size()-1);
                }
                else if(!est_DentroMientras.isEmpty()) {
                    error = sintaxisMientras(token, tokenAnterior, error);
                }
                else if(token.getValor().equals("for") && !est_EnCadena) {
                    error = sintaxisPor(token, tokenAnterior, error);
                }
                else if(!est_DentroPor.isEmpty()) {
                    error = sintaxisPor(token, tokenAnterior, error);
                }
                else if(token.getValor().equals("si") && !est_EnCadena) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }
                else if(!est_DentroSi.isEmpty()) {
                    error = sintaxisSi(token, tokenAnterior, error);
                }
            }
            else if(est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 0) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_ABRIR && tokenAnterior.getValor().equals("while")) {
                    est_MientrasAbierto.set(est_MientrasAbierto.size()-1, 1);
                } else {
                    return true;
                }
            }
            else if(est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 2) {
                if(token.getTipo() == Token.Tipos.PARENTESIS_CERRAR && tokenAnterior.getValor().equals(";")) {
                    est_MientrasAbierto.set(est_MientrasAbierto.size()-1,3);
                } else {
                    return true;
                }
            }
            else if(est_MientrasAbierto.get(est_MientrasAbierto.size()-1) == 3) {
                if(tokenAnterior.getTipo() == Token.Tipos.PARENTESIS_CERRAR && token.getTipo() == Token.Tipos.LLAVE_ABRIR) {
                    est_MientrasAbierto.set(est_MientrasAbierto.size()-1,4);
                } else {
                    return true;
                }
            }
        }
        return error;
    }
}
