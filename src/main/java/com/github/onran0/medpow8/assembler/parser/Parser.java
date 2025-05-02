package com.github.onran0.medpow8.assembler.parser;

import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.assembler.Command;
import com.github.onran0.medpow8.assembler.Operand;
import com.github.onran0.medpow8.assembler.OperandType;
import com.github.onran0.medpow8.assembler.token.Token;
import com.github.onran0.medpow8.assembler.token.TokenType;

import java.util.*;

public class Parser {
    private final List<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Command> parse() throws AssemblyException {
        return parse(tokens);
    }

    public static List<Command> parse(List<Token> tokens) throws AssemblyException {
        List<Command> commands = new ArrayList<>();

        Token prevToken = null;

        Token command = null;

        List<Operand> operands = new ArrayList<>();

        boolean comma = false;

        int i = 0;

        for (Token token : tokens) {
            switch(token.type()) {
                case FLAG_E, FLAG_L, FLAG_H:
                    final int flagNum = switch(token.type()) {
                        case FLAG_E -> 0;
                        case FLAG_L -> 1;
                        case FLAG_H -> 2;
                        default -> -1;
                    };

                    if(prevToken == null || command == null)
                        throw new AssemblyException("unexpected flag", token);

                    if(!operands.isEmpty() && !comma)
                        throw new AssemblyException("comma expected", prevToken);

                    comma = false;

                    operands.add(new Operand(prevToken.type() == TokenType.POINTER, flagNum, OperandType.FLAG));
                    break;

                case REG0, REG1, REG2, REG3, REG_SP:
                    final int regNum = switch(token.type()) {
                        case REG0 -> 0;
                        case REG1 -> 1;
                        case REG2 -> 2;
                        case REG3 -> 3;
                        case REG_SP -> 4;
                        default -> -1;
                    };

                    if(prevToken == null || command == null)
                        throw new AssemblyException("unexpected register", token);

                    if(!operands.isEmpty() && !comma)
                        throw new AssemblyException("comma expected", prevToken);

                    comma = false;

                    operands.add(new Operand(prevToken.type() == TokenType.POINTER, regNum, OperandType.REGISTER));
                    break;

                case CONST:
                    if(prevToken == null || command == null)
                        throw new AssemblyException("unexpected constant", token);

                    int constant = Integer.parseInt(token.value());

                    if(constant > 255)
                        throw new AssemblyException("invalid constant", token);

                    if(!operands.isEmpty() && !comma)
                        throw new AssemblyException("comma expected", prevToken);

                    comma = false;

                    operands.add(new Operand(prevToken.type() == TokenType.POINTER, constant, OperandType.CONST));
                    break;

                case POINTER, COMMENT: break;

                case COMMA: comma = true; break;
            }

            if (command != null && (token.type().isCommand() || i == tokens.size() - 1)) {
                commands.add(new Command(command, new ArrayList<>(operands)));
                operands.clear();
            }

            if(token.type().isCommand()) {
                command = token;

                if(i == tokens.size() - 1)
                    commands.add(new Command(token, operands));
            }

            prevToken = token;

            i++;
        }

        return commands;
    }
}