import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/*
    BuñuScript

    ANALIZADOR LÉXICO Y SINTÁCTICO
    Lenguajes y Autómatas I

    Equipo:
    - Bañuelos Ibarra Jesús Manuel
    - Padilla Zamudio Eduardo Guadalupe
    - Ruelas Gutiérrez Maia Paulina
    - Velázquez Anaya Alexsandra
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Leemos el archivo en el que está el código y lo convertimos en una cadena
            File archivo = new File("D:\\Tecnológico\\Lenguajes y Autómatas I\\BunuScript\\Código de Prueba - BunuScript.txt");
            Scanner scanner = new Scanner(archivo);
            String contenido = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Llamamos al método en el que se realizarán los analisis léxico y sintáctico
            Analizar(contenido);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void Analizar(String _codigo) {

        // Realizamos el análisis léxico y guardamos los tokens en un ArrayList
        ArrayList<Token> tokens = Lexico.Analisis(_codigo);

        // Imprimimos los tokens
        List<String> identificadores = new ArrayList<>();
        List<Token> errores = new ArrayList<>();

        // Verificamos si hay erroes léxicos y si hay los imprimimos
        for (Token token : tokens) {
            if (token.getTipo() == Token.Tipos.ERROR)
                errores.add(token);
        }
        if(!errores.isEmpty()) {
            System.out.println();
            for (Token token : errores) {
                System.out.println(" ERROR LÉXICO EN: " + token.getValor() + " , EN LÍNEA: " + token.getLinea());
            }
            return;
        }
        // Realizamos el análisis sintáctico
        Sintactico.Analisis(tokens, _codigo);

        // Si no hay errores léxicos ni sintácticos imprimimos la tabla de símbolos
        System.out.println("\n     --= TABLA DE SÍMBOLOS =--\n");
        for (Token token : tokens) {
            if(token.getTipo() == Token.Tipos.IDENTIFICADOR && !identificadores.contains(token.getValor()))
                System.out.printf(" %12s: %-20s%n", token.getTipo(), token.getValor());
                // System.out.printf(" %12s: %-20s, ASIGNACIÓN: %-20s%n", token.getTipo(), token.getValor(), token.getAsignacion());
            identificadores.add(token.getValor());
        }
        System.out.println();


    }
}