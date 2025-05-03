package com.github.onran0.medpow8.assembler;

import com.github.onran0.medpow8.assembler.parser.Parser;
import com.github.onran0.medpow8.assembler.token.TokenType;
import com.github.onran0.medpow8.assembler.token.Tokenizer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {

    private static byte getCommandMachineCode(Command command) throws AssemblyException {
        int pattern = command.getType().ordinal();

        Operand op1 = !command.getOperands().isEmpty() ? command.getOperands().get(0) : null;
        Operand op2 = command.getOperands().size() >= 2 ? command.getOperands().get(1) : null;

        pattern |= (op1 != null && op1.isPointer() ? 1 : 0) << 6;
        pattern |= (op2 != null && op2.isPointer() ? 1 : 0) << 7;

        pattern |= (op1 != null && op1.isRegisterAndNotSP() ? 1 : 0) << 8;
        pattern |= (op2 != null && op2.isRegisterAndNotSP() ? 1 : 0) << 9;

        pattern |= (op1 != null && op1.isFlag() && op1.getIntValue() == 0 ? 1 : 0) << 10;
        pattern |= (op1 != null && op1.isFlag() && op1.getIntValue() == 1 ? 1 : 0) << 11;
        pattern |= (op1 != null && op1.isFlag() && op1.getIntValue() == 2 ? 1 : 0) << 12;
        pattern |= (op1 != null && op1.isRegister() && op1.getIntValue() == 4 ? 1 : 0) << 13;

        Integer code = Patterns.getCodeByPattern(pattern);

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
        commands = new ArrayList<>(commands);

        final Map<String, Integer> labelsMap = new HashMap<>();

        if(commands.size() > 255)
            System.err.println("WARNING: The number of commands is more than 255. You will not be able to run the program without an emulator.");

        List<MachineCommand> machineCommands = new ArrayList<>();

        for(int i = 0;i < commands.size();i++) {
            Command command = commands.get(i);

            if(command.getType() == TokenType.LABEL_DECLARATION) {
                labelsMap.put(command.getToken().value(), i);
                commands.remove(i--);
            }
        }

        for(Command command : commands) {
            if(command.getOperands().size() > 2)
                throw new AssemblyException("invalid operands count", command.getToken());

            byte code = getCommandMachineCode(command);
            byte op1 = 0, op2 = 0;

            if(command.getOperands().size() == 2) {
                Operand opi1 = command.getOperands().get(0);
                Operand opi2 = command.getOperands().get(1);

                if (opi2.isFlag() || (opi2.isRegister() && opi2.getIntValue() == 4))
                    throw new AssemblyException("flag or sp register at second operand", command.getToken());

                if (opi1.isFlag() || (opi1.isRegister() && opi1.getIntValue() == 4))
                    op1 = (byte) opi2.getIntValue();
                else {
                    if(opi1.isRegisterAndNotSP() && opi2.isRegisterAndNotSP()) {
                        op1 = (byte) opi1.getIntValue();
                        op1 = (byte) (op1 & 0xFF | opi2.getIntValue() << 2);
                    } else {
                        if(opi2.isRegister()) {
                            op1 = (byte) opi2.getIntValue();
                            op2 = (byte) opi1.getIntValue();
                        } else {
                            op1 = (byte) opi1.getIntValue();
                            op2 = (byte) opi2.getIntValue();
                        }
                    }
                }
            } else if(command.getOperands().size() == 1) {
                Operand opi1 = command.getOperands().get(0);

                if(opi1.isLabelReference()) {
                    Integer commandAtLabel = labelsMap.get(opi1.getStringValue());

                    if(commandAtLabel == null)
                        throw new AssemblyException("undefined label: '" + opi1.getStringValue() + "'", command.getToken());
                    else
                        op1 = commandAtLabel.byteValue();
                } else
                    op1 = (byte) opi1.getIntValue();
            }

            machineCommands.add(new MachineCommand(code, op1, op2));
        }

        if(!labelsMap.isEmpty() && machineCommands.isEmpty())
            machineCommands.add(new MachineCommand((byte) 0, (byte) 0, (byte) 0));

        return machineCommands;
    }
}