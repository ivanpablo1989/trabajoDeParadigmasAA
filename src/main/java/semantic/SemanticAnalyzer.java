package semantic;

import parser.MiniLangBaseVisitor;
import parser.MiniLangParser;

public class SemanticAnalyzer extends MiniLangBaseVisitor<Object>
{
    private final SymbolTable tabla = new SymbolTable();

    // =========================
    // PROGRAMA
    // =========================
    @Override
    public Object visitProgram(MiniLangParser.ProgramContext ctx)
    {
        return visitChildren(ctx);
    }

    // =========================
    // DECLARACION
    // =========================
    @Override
    public Object visitDeclaration(MiniLangParser.DeclarationContext ctx)
    {
        String id = ctx.ID().getText();
        String tipo = ctx.type().getText();

        Object valor = null;

        if (ctx.expr() != null)
        {
            valor = visit(ctx.expr());

            if (valor == null)
                error("Expresión inválida en inicialización de '" + id + "'");

            if (!esAsignacionValida(tipo, valor))
                error("Tipo incompatible en inicialización de '" + id + "'");
        }

        tabla.declarar(id, tipo, valor);
        return null;
    }

    // =========================
    // ASIGNACION
    // =========================
    @Override
    public Object visitAssignment(MiniLangParser.AssignmentContext ctx)
    {
        String id = ctx.ID().getText();
        SymbolTable.Symbol sym = tabla.obtener(id);

        Object valor = visit(ctx.expr());

        if (valor == null)
            error("Expresión inválida en asignación a '" + id + "'");

        // 🔥 FIX 1: permitir int <- double (caso n = n + 1)
        if (sym.getTipo().equals("int") && valor instanceof Double)
        {
            valor = ((Double) valor).intValue();
        }

        if (!esAsignacionValida(sym.getTipo(), valor))
            error("Tipo incompatible en asignación a '" + id + "'");

        tabla.asignar(id, valor);
        return null;
    }

    // =========================
    // IDENTIFICADORES
    // =========================
    @Override
    public Object visitIdExpr(MiniLangParser.IdExprContext ctx)
    {
        return tabla.obtenerValor(ctx.ID().getText());
    }

    @Override
    public Object visitParenthesizedExpr(MiniLangParser.ParenthesizedExprContext ctx)
    {
        return visit(ctx.expr());
    }

    // =========================
    // LITERALES
    // =========================
    @Override
    public Object visitIntExpr(MiniLangParser.IntExprContext ctx)
    {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public Object visitFloatExpr(MiniLangParser.FloatExprContext ctx)
    {
        return Double.parseDouble(ctx.FLOAT().getText());
    }

    @Override
    public Object visitStringExpr(MiniLangParser.StringExprContext ctx)
    {
        String t = ctx.STRING().getText();
        return t.substring(1, t.length() - 1);
    }

    @Override
    public Object visitBoolExpr(MiniLangParser.BoolExprContext ctx)
    {
        return Boolean.parseBoolean(ctx.BOOL().getText());
    }

    // =========================
    // SUMA / RESTA
    // =========================
    @Override
    public Object visitAdditiveExpr(MiniLangParser.AdditiveExprContext ctx)
    {
        Object result = visit(ctx.multiplicativeExpr(0));

        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++)
        {
            Object right = visit(ctx.multiplicativeExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();

            if (result == null || right == null)
                error("Expresión inválida");

            if (result instanceof String || right instanceof String)
            {
                if (!op.equals("+"))
                    error("No se puede usar '-' con strings");

                result = String.valueOf(result) + String.valueOf(right);
            }
            else
            {
                double a = toDouble(result);
                double b = toDouble(right);

                double r = op.equals("+") ? a + b : a - b;

                // 🔥 FIX 2: normalizar int/double
                result = (r % 1 == 0) ? (int) r : r;
            }
        }

        return result;
    }

    // =========================
    // MULTIPLICATIVO
    // =========================
    @Override
    public Object visitMultiplicativeExpr(MiniLangParser.MultiplicativeExprContext ctx)
    {
        Object result = visit(ctx.primaryExpr(0));

        for (int i = 1; i < ctx.primaryExpr().size(); i++)
        {
            Object right = visit(ctx.primaryExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();

            if (!esNumero(result) || !esNumero(right))
                error("Operación inválida");

            double a = toDouble(result);
            double b = toDouble(right);

            if (op.equals("/") && b == 0)
                error("División por cero");

            double r = op.equals("*") ? a * b : a / b;

            result = (r % 1 == 0) ? (int) r : r;
        }

        return result;
    }

    // =========================
    // RELACIONAL / LOGICO (NO TOCAR)
    // =========================
    @Override
    public Object visitRelationalExpr(MiniLangParser.RelationalExprContext ctx)
    {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLogicalExpr(MiniLangParser.LogicalExprContext ctx)
    {
        return visitChildren(ctx);
    }

    // =========================
    // UTILIDADES
    // =========================
    private boolean esNumero(Object v)
    {
        return v instanceof Integer || v instanceof Double;
    }

    private double toDouble(Object v)
    {
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        if (v instanceof Double) return (Double) v;

        error("Valor no numérico");
        return 0;
    }

    private boolean esAsignacionValida(String tipo, Object valor)
    {
        if (valor == null) return true;

        switch (tipo)
        {
            case "int":
                return valor instanceof Integer || valor instanceof Double;

            case "float":
                return valor instanceof Integer || valor instanceof Double;

            case "string":
                return valor instanceof String;

            case "bool":
                return valor instanceof Boolean;

            default:
                return false;
        }
    }

    private void error(String msg)
    {
        throw new RuntimeException("Error semántico: " + msg);
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult)
    {
        return nextResult;
    }
}