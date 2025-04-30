package com.github.onran0.medpow8.assembler;

import com.github.onran0.medpow8.assembler.token.Token;

public class AssemblyException extends Exception {
    private int line, column;

    public AssemblyException(final String message, final Token token) {
        this(message, token.line(), token.column());
    }

    public AssemblyException(final String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public AssemblyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return super.toString() + " at line " + line + ", column " + column;
    }
}