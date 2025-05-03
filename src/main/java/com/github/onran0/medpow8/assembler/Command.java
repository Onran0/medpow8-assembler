package com.github.onran0.medpow8.assembler;

import com.github.onran0.medpow8.assembler.token.Token;
import com.github.onran0.medpow8.assembler.token.TokenType;

import java.util.Collections;
import java.util.List;

public final class Command {
    private final List<Operand> operands;
    private final Token token;

    public Command(final Token token, final List<Operand> operands) {
        this.token = token;
        this.operands = Collections.unmodifiableList(operands);
    }

    public TokenType getType() {
        return token.type();
    }

    public Token getToken() {
        return token;
    }

    public List<Operand> getOperands() {
        return operands;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        if(token.type() != TokenType.LABEL_DECLARATION) {
            str.append(token.type().getName());

            str.append(' ');

            for(Operand operand : operands) {
                str.append(operand);
                str.append(", ");
            }

            if(!operands.isEmpty())
                str.setLength(str.length() - 2);
        } else {
            str.append(token.value());
            str.append(':');
        }

        return str.toString();
    }
}