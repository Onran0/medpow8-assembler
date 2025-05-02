package com.github.onran0.medpow8.assembler;

public final class Operand {
    private final boolean pointer;
    private final int value;
    private final OperandType type;

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

    public boolean isRegisterAndNotSP() {
        return OperandType.REGISTER == type && value < 4;
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

        if(isRegister()) {
            if(value < 4)
                str.append('r');
            else
                str.append("sp");
        }

        if(isConstant() || isRegister() && value < 4)
            str.append(value);

        if (isFlag()) {
            if(value == 0)
                str.append('e');
            else
                str.append(value == 1 ? 'l' : 'h');
        }

        return str.toString();
    }
}