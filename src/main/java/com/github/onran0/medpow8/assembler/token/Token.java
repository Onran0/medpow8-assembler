package com.github.onran0.medpow8.assembler.token;

public record Token(TokenType type, String value, int line, int column) {
    @Override
    public String toString() {
        return "( " + type + (value != null ? "='" + value + "'" : "") + " at " + line + ":" + column + " )";
    }
}