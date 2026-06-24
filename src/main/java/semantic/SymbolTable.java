package semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable
{
    public static class Symbol
    {
        private String tipo;
        private Object valor;
        private boolean inicializada;

        public Symbol(String tipo, Object valor, boolean inicializada)
        {
            this.tipo = tipo;
            this.valor = valor;
            this.inicializada = inicializada;
        }

        public String getTipo() { return tipo; }
        public Object getValor() { return valor; }

        public void setValor(Object valor)
        {
            this.valor = valor;
            this.inicializada = true;
        }

        public boolean estaInicializada()
        {
            return inicializada;
        }
    }

    private final Map<String, Symbol> tabla = new HashMap<>();

    public void declarar(String id, String tipo, Object valor)
    {
        if (tabla.containsKey(id))
            throw new RuntimeException("Error semántico: variable redeclarada '" + id + "'");

        tabla.put(id, new Symbol(tipo, valor, valor != null));
    }

    public Symbol obtener(String id)
    {
        Symbol s = tabla.get(id);

        if (s == null)
            throw new RuntimeException("Error semántico: variable no declarada '" + id + "'");

        return s;
    }

    public Object obtenerValor(String id)
    {
        Symbol s = obtener(id);

        if (!s.estaInicializada())
            throw new RuntimeException("Error semántico: variable no inicializada '" + id + "'");

        return s.getValor();
    }

    public void asignar(String id, Object valor)
    {
        obtener(id).setValor(valor);
    }
}
