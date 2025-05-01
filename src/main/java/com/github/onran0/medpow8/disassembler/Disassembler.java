package com.github.onran0.medpow8.disassembler;

import com.github.onran0.medpow8.assembler.*;
import com.github.onran0.medpow8.assembler.token.Token;
import com.github.onran0.medpow8.assembler.token.TokenType;

import java.util.*;

public class Disassembler {

    private static int getOperandsCount(int pattern) {
        Integer code = Patterns.getCodeByPattern(pattern);

        if(code == null) return -1;

        return switch(TokenType.values()[pattern & 0b111111]) {
            case NOP, HLT, IPORT -> 0;
            case JMP, JE, JL, JH, JEL, JEH, RPORT, WPORT, CPORT, PUSH, POP, DIAL -> 1;
            case NOT -> (code == 55 || code == 59) ? 1 : 2;
            default -> 2;
        };
    }

    public static String disassemble(byte[] machineCode) throws DisassemblyException {
        StringBuilder builder = new StringBuilder();

        for(Command command : disassembleToList(machineCode)) {
            builder.append(command);
            builder.append('\n');
        }

        return builder.toString();
    }

    public static List<Command> disassembleToList(byte[] machineCode) throws DisassemblyException {
        if(machineCode.length % 3 != 0)
            throw new DisassemblyException("invalid machine code length: each command must occupy exactly 24 bits");

        List<MachineCommand> machineCommands = new ArrayList<>();

        for(int i = 0;i < machineCode.length / 3;i++) {
            machineCommands.add(
                    new MachineCommand(
                        machineCode[i * 3],
                        machineCode[i * 3 + 1],
                        machineCode[i * 3 + 2]
                    )
            );
        }

        return disassembleToList(machineCommands);
    }

    public static List<Command> disassembleToList(List<MachineCommand> machineCommands) throws DisassemblyException {
        List<Command> commands = new ArrayList<>();

        int i = 0;

        for(MachineCommand mCmd : machineCommands) {
            List<Operand> operands = new ArrayList<>();

            Integer pattern = Patterns.getPatternByCode(mCmd.command() & 0xFF);

            if(pattern == null)
                throw new DisassemblyException("undefined command code (perhaps you have an old version of the disassembler?)", i);

            Token token = new Token(TokenType.values()[pattern & 0b111111], null, i, 0);

            boolean allRegs = (pattern >> 8 & 0b11) == 0b11;
            boolean hasReg = (pattern >> 8 & 0b11) != 0;

            for(int j = 0;j < getOperandsCount(pattern);j++) {
                boolean isReg = ((pattern >> (8 + j)) & 1) == 1;

                operands.add(
                        new Operand(
                            ((pattern >> (6 + j)) & 1) == 1,
                            !allRegs ? (mCmd.getOp(hasReg ? (isReg ? 0 : 1) : j) & 0xFF) : (mCmd.getOp(0) & 0xFF) >> (j * 2) & 0b11,
                            isReg ? OperandType.REGISTER : OperandType.CONST
                        )
                );
            }

            commands.add(new Command(token, operands));

            i++;
        }

        return commands;
    }
}