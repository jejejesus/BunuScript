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
        System.out.println("\n       --= TABLA DE SIMBOLOS =--\n");
        for (Token token : tokens) {
            if(token.getTipo() == Token.Tipos.IDENTIFICADOR && !identificadores.contains(token.getValor()))
            System.out.printf(" %17s: %-39s%n", token.getTipo(), token.getValor());
            identificadores.add(token.getValor());
        }
        System.out.println();

        // Realizamos el análisis sintáctico
        Sintactico.Analisis(tokens, _codigo);
    }
}