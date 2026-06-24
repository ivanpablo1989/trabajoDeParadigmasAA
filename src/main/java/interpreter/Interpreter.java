package interpreter;

import parser.MiniLangBaseVisitor;
import parser.MiniLangParser;
import semantic.SymbolTable;


public class Interpreter extends MiniLangBaseVisitor<Object>
{
    private SymbolTable tabla = new SymbolTable();

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
        Object valor = visit(ctx.expr());

        tabla.asignar(id, valor);
        return null;
    }

    // =========================
    // PRINT
    // =========================
    @Override
    public Object visitPrintStmt(MiniLangParser.PrintStmtContext ctx)
    {
        Object valor = visit(ctx.expr());
        System.out.println(valor);
        return null;
    }

    // =========================
    // IF
    // =========================
    @Override
    public Object visitIfStmt(MiniLangParser.IfStmtContext ctx)
    {
        Object cond = visit(ctx.expr());

        if (!(cond instanceof Boolean))
        {
            throw new RuntimeException("Error: la condición del if debe ser booleana");
        }

        if ((Boolean) cond)
        {
            visit(ctx.block(0));
        }
        else if (ctx.block().size() > 1)
        {
            visit(ctx.block(1));
        }

        return null;
    }

    // =========================
    // REPEAT UNTIL
    // =========================
    @Override
    public Object visitRepeatStmt(MiniLangParser.RepeatStmtContext ctx)
    {
        do
        {
            visit(ctx.block());
        }
        while (!(Boolean) visit(ctx.expr()));

        return null;
    }

    // =========================
    // LOGICAL (&& ||)
    // =========================
    @Override
    public Object visitLogicalExpr(MiniLangParser.LogicalExprContext ctx)
    {
        Object result = visit(ctx.relationalExpr(0));

        for (int i = 1; i < ctx.relationalExpr().size(); i++)
        {
            Object right = visit(ctx.relationalExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();

            if (!(result instanceof Boolean) || !(right instanceof Boolean))
            {
                throw new RuntimeException("Error: operadores lógicos requieren booleanos");
            }

            if (op.equals("&&"))
            {
                result = (Boolean) result && (Boolean) right;
            }
            else
            {
                result = (Boolean) result || (Boolean) right;
            }
        }

        return result;
    }

    // =========================
    // RELACIONAL
    // =========================
    @Override
    public Object visitRelationalExpr(MiniLangParser.RelationalExprContext ctx)
    {
        Object left = visit(ctx.additiveExpr(0));

        for (int i = 1; i < ctx.additiveExpr().size(); i++)
        {
            Object right = visit(ctx.additiveExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();

            double a = toDouble(left);
            double b = toDouble(right);

            switch (op)
            {
                case ">": left = a > b; break;
                case "<": left = a < b; break;
                case ">=": left = a >= b; break;
                case "<=": left = a <= b; break;
                case "==": left = a == b; break;
                case "!=": left = a != b; break;
            }
        }

        return left;
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

            if (result instanceof String || right instanceof String)
            {
                if (op.equals("+"))
                {
                    result = String.valueOf(result) + String.valueOf(right);
                }
                else
                {
                    throw new RuntimeException("Error: no se puede restar strings");
                }
            }
            else
            {
                double a = toDouble(result);
                double b = toDouble(right);

                result = op.equals("+") ? a + b : a - b;
            }
        }

        return result;
    }

    // =========================
    // MULTIPLICACION / DIVISION
    // =========================
    @Override
    public Object visitMultiplicativeExpr(MiniLangParser.MultiplicativeExprContext ctx)
    {
        Object result = visit(ctx.primaryExpr(0));

        for (int i = 1; i < ctx.primaryExpr().size(); i++)
        {
            Object right = visit(ctx.primaryExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();

            double a = toDouble(result);
            double b = toDouble(right);

            if (op.equals("/") && b == 0)
            {
                throw new RuntimeException("Error: división por cero");
            }

            result = op.equals("*") ? a * b : a / b;
        }

        return result;
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
    // UTILIDAD
    // =========================
    private double toDouble(Object v)
    {
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        if (v instanceof Double) return (Double) v;

        throw new RuntimeException("Error: valor no numérico");
    }
}