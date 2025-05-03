package com.github.onran0.medpow8.assembler;

public final class Operand {
    private final boolean pointer;
    private final int intValue;
    private final String stringValue;
    private final OperandType type;

    public Operand(final boolean pointer, final int intValue, final String stringValue, final OperandType type) {
        this.pointer = pointer;
        this.intValue = intValue;
        this.stringValue = stringValue;
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
        return OperandType.REGISTER == type && intValue < 4;
    }

    public boolean isConstant() {
        return OperandType.CONST == type;
    }

    public boolean isLabelReference() { return OperandType.LABEL_REFERENCE == type; }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        if(pointer)
            str.append('%');

        if(isRegister()) {
            if(intValue < 4)
                str.append('r');
            else
                str.append("sp");
        }

        if(isConstant() || isRegister() && intValue < 4)
            str.append(intValue);

        if (isFlag()) {
            if(intValue == 0)
                str.append('e');
            else
                str.append(intValue == 1 ? 'l' : 'h');
        }

        if(isLabelReference())
            str.append(stringValue);

        return str.toString();
    }
}