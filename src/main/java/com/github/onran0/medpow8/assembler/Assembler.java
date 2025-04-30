package com.github.onran0.medpow8.assembler;

import com.github.onran0.medpow8.assembler.parser.Command;
import com.github.onran0.medpow8.assembler.parser.Operand;
import com.github.onran0.medpow8.assembler.parser.Parser;
import com.github.onran0.medpow8.assembler.token.Tokenizer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {
    private static final Map<Integer, Integer> PATTERN_TO_CODE_MAP = new HashMap<>();

    static {
        int[] patterns = {
                // nop; hlt
                0, 1,

                // data copy

                // 2 - 5
                0b11_00_000010,
                0b11_01_000010,
                0b11_10_000010,
                0b11_11_000010,

                // 6 - 10
                0b10_00_000010,
                0b10_01_000010,
                0b01_10_000010,
                0b01_11_000010,
                0b00_11_000010,

                // arithmetic operations

                // 11 - 15
                0b11_00_000011,
                0b11_00_000100,
                0b11_00_000101,
                0b11_00_000110,
                0b11_00_000111,

                // 16 - 20
                0b11_01_000011,
                0b11_01_000100,
                0b11_01_000101,
                0b11_01_000110,
                0b11_01_000111,

                // 21 - 25
                0b11_10_000011,
                0b11_10_000100,
                0b11_10_000101,
                0b11_10_000110,
                0b11_10_000111,

                // 26 - 30
                0b11_11_000011,
                0b11_11_000100,
                0b11_11_000101,
                0b11_11_000110,
                0b11_11_000111,

                // 31 - 35
                0b10_00_000011,
                0b10_00_000100,
                0b10_00_000101,
                0b10_00_000110,
                0b10_00_000111,

                // 36 - 38
                0b01_00_000100,
                0b01_00_000110,
                0b01_00_000111,

                // 39 - 43
                0b10_01_000011,
                0b10_01_000100,
                0b10_01_000101,
                0b10_01_000110,
                0b10_01_000111,

                // 44 - 46
                0b01_10_000100,
                0b01_10_000110,
                0b01_10_000111,

                // 47 - 51
                0b10_11_000011,
                0b10_11_000100,
                0b10_11_000101,
                0b10_11_000110,
                0b10_11_000111,

                // 52 - 54
                0b01_11_000100,
                0b01_11_000110,
                0b01_11_000111,

                // bitwise operations

                // 55 - 58
                0b1_00_001000,
                0b11_00_001001,
                0b11_00_001010,
                0b11_00_001011,

                // 59 - 62
                0b1_01_001000,
                0b11_01_001001,
                0b11_01_001010,
                0b11_01_001011,

                // 63 - 65
                0b11_10_001001,
                0b11_10_001010,
                0b11_10_001011,

                // 66 - 68
                0b11_11_001001,
                0b11_11_001010,
                0b11_11_001011,

                // 69 - 71
                0b10_00_001001,
                0b10_00_001010,
                0b10_00_001011,

                // 72 - 74
                0b10_01_001001,
                0b10_01_001010,
                0b10_01_001011,

                // 75 - 77
                0b10_10_001001,
                0b10_10_001010,
                0b10_10_001011,

                // 78 - 80
                0b10_11_001001,
                0b10_11_001010,
                0b10_11_001011,

                // 81 - 88
                0b11_01_001000,
                0b11_11_001000,
                0b11_00_001000,
                0b11_10_001000,
                0b01_10_001000,
                0b01_11_001000,
                0b10_01_001000,
                0b10_11_001000,

                // 89 - 100
                0b11_00_001100,
                0b11_01_001100,
                0b11_10_001100,
                0b11_11_001100,

                0b10_00_001100,
                0b10_01_001100,
                0b10_10_001100,
                0b10_11_001100,

                0b01_10_001100,
                0b01_11_001100,

                0b00_10_001100,
                0b00_11_001100,

                // 101 - 112
                0b11_00_001101,
                0b11_01_001101,
                0b11_10_001101,
                0b11_11_001101,

                0b10_00_001101,
                0b10_01_001101,
                0b10_10_001101,
                0b10_11_001101,

                0b01_10_001101,
                0b01_11_001101,

                0b00_10_001101,
                0b00_11_001101,

                // left non-commutative arithmetic operations

                // 113 - 115
                0b11_00_001110,
                0b11_00_001111,
                0b11_00_010000,

                // 116 - 118
                0b11_01_001110,
                0b11_01_001111,
                0b11_01_010000,

                // 119 - 121
                0b11_10_001110,
                0b11_10_001111,
                0b11_10_010000,

                // 122 - 124
                0b11_11_001110,
                0b11_11_001111,
                0b11_11_010000,

                // left non-commutative bitwise operations

                // 125 - 132
                0b11_00_010001,
                0b11_00_010010,
                0b11_01_010001,
                0b11_01_010010,

                0b11_10_010001,
                0b11_10_010010,
                0b11_11_010001,
                0b11_11_010010,

                // compare operation

                // 133 - 144
                0b11_00_010011,
                0b11_01_010011,
                0b11_10_010011,
                0b11_11_010011,

                0b10_00_010011,
                0b10_01_010011,
                0b10_10_010011,
                0b10_11_010011,

                0b01_00_010011,
                0b01_01_010011,
                0b01_10_010011,
                0b01_11_010011,

                // flags copy (not implemented yet)

                // 145 - 153

                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,

                // control transfer

                // 154 - 155
                0b00_00_010100,
                0b01_00_010100,

                // 156 - 165
                0b00_00_010101,
                0b01_00_010101,

                0b00_00_010110,
                0b01_00_010110,

                0b00_00_010111,
                0b01_00_010111,

                0b00_00_011000,
                0b01_00_011000,

                0b00_00_011001,
                0b01_00_011001,

                // stack

                // 166 - 169

                0b01_00_011010,
                0b01_01_011010,
                0b00_00_011010,
                0b00_01_011010,

                // 170 - 172
                0b01_00_011011,
                0b01_01_011011,
                0b00_01_011011,

                // dial

                // 173 - 176
                0b01_00_011100,
                0b01_01_011100,
                0b00_00_011100,
                0b00_01_011100,

                // displ

                // 177 - 180
                0b11_00_011101,
                0b11_01_011101,
                0b11_10_011101,
                0b11_11_011101,

                // 181 - 184
                0b10_00_011101,
                0b10_01_011101,
                0b10_10_011101,
                0b10_11_011101,

                // 184 - 188
                0b01_00_011101,
                0b01_01_011101,
                0b01_10_011101,
                0b01_11_011101,

                // cport

                // 189 - 192
                0b01_00_011110,
                0b01_01_011110,
                0b00_00_011110,
                0b00_01_011110,

                // sp register copy (not implemented yet)

                // 193 - 195

                0b111_11_11_111111,
                0b111_11_11_111111,
                0b111_11_11_111111,

                // wport

                // 196 - 199
                0b01_00_011111,
                0b01_01_011111,
                0b00_00_011111,
                0b00_01_011111,

                // rport

                // 200 - 202
                0b01_00_100000,
                0b01_01_100000,
                0b00_01_100000,

                // iport

                // 203

                0b00_00_100001
        };

        for(int i = 0;i < patterns.length;i++)
            PATTERN_TO_CODE_MAP.put(patterns[i], i);
    }

    private static byte getCommandMachineCode(Command command) throws AssemblyException {
        int pattern = command.getType().ordinal();

        Operand op1 = !command.getOperands().isEmpty() ? command.getOperands().get(0) : null;
        Operand op2 = command.getOperands().size() >= 2 ? command.getOperands().get(1) : null;

        pattern |= (op1 != null && op1.isPointer() ? 1 : 0) << 6;
        pattern |= (op2 != null && op2.isPointer() ? 1 : 0) << 7;

        pattern |= (op1 != null && op1.isRegister() ? 1 : 0) << 8;
        pattern |= (op2 != null && op2.isRegister() ? 1 : 0) << 9;

        pattern |= (op1 != null && op1.isFlag() && op1.getValue() == 0 ? 1 : 0) << 10;
        pattern |= (op1 != null && op1.isFlag() && op1.getValue() == 1 ? 1 : 0) << 11;
        pattern |= (op1 != null && op1.isFlag() && op1.getValue() == 2 ? 1 : 0) << 11;
        pattern |= (op1 != null && op1.isFlag() && op1.getValue() == 3 ? 1 : 0) << 12;

        Integer code = PATTERN_TO_CODE_MAP.get(pattern);

        if (code == null)
            throw new AssemblyException("undefined syntax (maybe it hasn't been implemented yet?): " + command, command.getToken());

        return code.byteValue();
    }

    public static byte[] assemble(String str) throws AssemblyException {
        return assembleToBytes(
                Parser.parse(
                        new Tokenizer(str).tokenize()
                )
        );
    }

    public static byte[] assembleToBytes(List<Command> commands) throws AssemblyException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (MachineCommand command : assembleToList(commands)) {
            out.write(command.command() & 0xFF);
            out.write(command.op1() & 0xFF);
            out.write(command.op2() & 0xFF);
        }

        return out.toByteArray();
    }

    public static List<MachineCommand> assembleToList(List<Command> commands) throws AssemblyException {
        if(commands.size() > 256)
            System.err.println("WARNING: The number of commands is more than 256. You will not be able to run the program without an emulator.");

        List<MachineCommand> machineCommands = new ArrayList<>();

        for(Command command : commands) {
            if(command.getOperands().size() > 3)
                throw new AssemblyException("invalid operands count", command.getToken());

            byte code = getCommandMachineCode(command);
            byte op1 = 0, op2 = 0;

            if(command.getOperands().size() == 2) {
                Operand opi1 = command.getOperands().get(0);
                Operand opi2 = command.getOperands().get(1);

                if(opi1.isRegister() && opi2.isRegister()) {
                    op1 = (byte) opi1.getValue();
                    op1 = (byte) (op1 & 0xFF | opi2.getValue() << 2);
                } else {
                    if(opi1.isRegister()) {
                        op1 = (byte) opi1.getValue();
                        op2 = (byte) opi2.getValue();
                    } else if(opi2.isRegister()) {
                        op1 = (byte) opi2.getValue();
                        op2 = (byte) opi1.getValue();
                    }
                }
            } else if(command.getOperands().size() == 1)
                op1 = (byte) command.getOperands().get(0).getValue();

            machineCommands.add(new MachineCommand(code, op1, op2));
        }

        return machineCommands;
    }
}