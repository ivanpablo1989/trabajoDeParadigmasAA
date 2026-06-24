README – Intérprete MiniLang (ANTLR4 + Java)
 Integrantes
    • Integrante 1: Iván Tolaba
    • Integrante 2: Santiago Agarzua
enlace a video de Iván :Tolaba:
enlace a video de Santiago Agarzua
 Variante asignada
Variante 4: repeat-until
Iteración con condición de corte invertida:
repeat {
    instrucciones;
} until (condición);
 Descripción del lenguaje
MiniLang es un lenguaje de programación imperativo simple diseñado para ejecutar programas con:
    • Tipos de datos básicos
    • Variables
    • Expresiones aritméticas, relacionales y lógicas
    • Estructuras de control
    • Impresión por consola
El lenguaje es interpretado utilizando ANTLR4 y Java.
 Características del lenguaje
 Tipos de datos
    • int → enteros
    • float → números decimales
    • string → texto
    • bool → booleanos (true, false)
 Comentarios
// comentario de una línea
 Variables
int a = 5;
float b = 2.5;
string s = "hola";
bool x = true;
Reglas:
    • No se puede usar una variable sin declarar
    • No se permite redeclaración
    • Se valida compatibilidad de tipos
Instrucción de salida
print(a + b);
print("hola");
 Condicional
if (a < b) {
    print("menor");
} else {
    print("mayor");
}
 Iteración (variante implementada)
repeat {
    print(n);
    n = n + 1;
} until (n == 5);
 Diseño técnico del intérprete
El sistema está dividido en 4 etapas:
1 Análisis léxico (Lexer)
Generado por ANTLR4.
Responsable de:
    • reconocer tokens
    • palabras clave
    • operadores
    • literales
Análisis sintáctico (Parser)
Generado por ANTLR4.
Construye el árbol de derivación (Parse Tree) a partir de la gramática.
 Análisis semántico
Implementado con Visitor.
Valida:
    • variables declaradas
    • tipos compatibles
    • asignaciones correctas
    • divisiones por cero
    • uso de variables no inicializadas
 Intérprete
Recorre el árbol sintáctico y ejecuta el programa.
Soporta:
    • evaluación de expresiones
    • control de flujo
    • impresión por consola
    • asignaciones dinámicas
 Decisiones de diseño
    • Se utilizó Visitor Pattern para separar lógica del AST
    • Se separó:
        ◦ SemanticAnalyzer (validación)
        ◦ Interpreter (ejecución)
    • Se implementó tabla de símbolos con HashMap
    • Evaluación de tipos en runtime (dinámica controlada)
    • Comparaciones numéricas convertidas a double
 Estructura del proyecto
tp-interprete/
│
├── src/
│   ├── main/java/
│   │   ├── main/
│   │   ├── parser/        (ANTLR generado)
│   │   ├── semantic/
│   │   ├── interpreter/
│   │
│   └── main/antlr4/
│       └── MiniLang.g4
│
├── programa.txt
├── pom.xml
└── README.md
 Compilación y ejecución
 Requisitos
    • Java 17
    • Maven 3+
    • ANTLR 4.13.1 (manejado por Maven)
 Compilar proyecto
mvn clean install
 Ejecutar intérprete
mvn exec:java
 Archivo de entrada
El programa lee automáticamente:
programa.txt
 Ejemplo de programa
int a = 5;
int b = 10;

print(a + b);

if (a < b) {
    print("a es menor");
} else {
    print("b es menor");
}

int n = 1;

repeat {
    print(n);
    n = n + 1;
} until (n == 5);
