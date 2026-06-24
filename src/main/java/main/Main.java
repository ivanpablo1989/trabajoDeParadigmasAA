package main;

import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import parser.MiniLangLexer;
import parser.MiniLangParser;

import semantic.SemanticAnalyzer;
import interpreter.Interpreter;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Path file = Path.of("programa.txt");

        if (!Files.exists(file))
        {
            System.out.println("Error: no existe programa.txt");
            return;
        }

        String code = Files.readString(file);

        System.out.println("\n======================");
        System.out.println("CODIGO FUENTE");
        System.out.println("======================");
        System.out.println(code);

        // =========================
        // LEXER + PARSER
        // =========================
        System.out.println("\n======================");
        System.out.println("LEXER + PARSER");
        System.out.println("======================");

        MiniLangLexer lexer = new MiniLangLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniLangParser parser = new MiniLangParser(tokens);

        ParseTree tree = parser.program();

        System.out.println("\nARBOL:");
        System.out.println(tree.toStringTree(parser));

        // =========================
        // ANALISIS SEMANTICO
        // =========================
        System.out.println("\n======================");
        System.out.println("ANALISIS SEMANTICO");
        System.out.println("======================");

        try
        {
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            Object result = analyzer.visit(tree);

            System.out.println("OK - Analisis semantico completado");
            System.out.println("Resultado semantic visit: " + result);
        }
        catch (RuntimeException e)
        {
            System.out.println("ERROR SEMANTICO DETECTADO:");
            System.out.println(e.getMessage());
            return;
        }

        // =========================
        // INTERPRETE
        // =========================
        System.out.println("\n======================");
        System.out.println("EJECUCION (INTERPRETE)");
        System.out.println("======================");

        try
        {
            Interpreter interpreter = new Interpreter();
            Object result = interpreter.visit(tree);

            System.out.println("\nEJECUCION FINALIZADA");
            System.out.println("Resultado interpreter visit: " + result);
        }
        catch (RuntimeException e)
        {
            System.out.println("ERROR EN EJECUCION:");
            System.out.println(e.getMessage());
        }
    }
}
