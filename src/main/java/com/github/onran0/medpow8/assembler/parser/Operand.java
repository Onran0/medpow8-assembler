package com.github.onran0.medpow8.assembler.parser;

public final class Operand {
    private final boolean pointer;
    private final int value;
    private OperandType type;

    public Operand(final boolean pointer, final int value, final OperandType type) {
        this.pointer = pointer;
        this.value = value;
        this.type = type;
    }

    public boolean isPointer() {
        return pointer;
    }

    public boolean isFlag() {
        return OperandType.FLAG == type;
    }

    public boolean isRegister() {
        return OperandType.REGISTER == type;
    }

    public boolean isConstant() {
        return OperandType.CONST == type;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        if(pointer)
            str.append('%');

        if(isRegister())
            str.append('r');

        str.append(value);

        return str.toString();
    }
}