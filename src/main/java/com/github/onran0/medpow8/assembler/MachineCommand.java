package com.github.onran0.medpow8.assembler;

public record MachineCommand(byte command, byte op1, byte op2) {

    public byte getOp(int index) {
        return switch (index) {
            case 0 -> op1;
            case 1 -> op2;
            default -> -1;
        };
    }
}