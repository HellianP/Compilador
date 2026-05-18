package compiler.analise_de_contexto;

import compiler.Token;
import compiler.visitor.nodeOperador;

public class Type {
    public byte kind;

    public static final byte BOOL = 0, INT = 1, REAL = 2;

    public Type(byte kind) {
        this.kind = kind;
    }

    public boolean equals(Object other) {
        Type otherType = (Type) other;
        return (this.kind == otherType.kind);
    }

    public static String convertTypeToString(byte tipo) {
        switch (tipo) {
            case BOOL:
                return "BOOLEAN";
            case INT:
                return "INTEGER";
            case REAL:
                return "REAL";
            default:
                return "null";
        }
    }

    public static Type evaluate(Type tipo1, Type tipo2, nodeOperador operador) {
        if (tipo1 == null) {
            System.out.println("Tipo1 é NULL!");
        }
        if (tipo2 == null) {
            System.out.println("Tipo2 é NULL!");
        }

        byte kind1 = tipo1.kind;
        byte kind2 = tipo2.kind;

        if (kind1 == kind2 && operador.operador == Token.RELATIONALOPERATOR) {
            return new Type(Type.BOOL);
        }

        if (kind1 == Type.BOOL || kind2 == Type.BOOL) {
            return null;
        }

        if (operador.operador == Token.RELATIONALOPERATOR) {
            return new Type(Type.BOOL);
        }

        if (kind1 == Type.REAL || kind2 == Type.REAL) {
            return new Type(Type.REAL);
        }

        if (kind1 == kind2) {
            return new Type(kind1);
        }

        return null;
    }

    public static Type evaluateString(String tipoString) {
        Type tipo = null;

        if (tipoString.equals("boolean")) {
            tipo = new Type(Type.BOOL);
        } else if (tipoString.equals("integer")) {
            tipo = new Type(Type.INT);
        } else if (tipoString.equals("real")) {
            tipo = new Type(Type.REAL);
        }

        return tipo;
    }
}