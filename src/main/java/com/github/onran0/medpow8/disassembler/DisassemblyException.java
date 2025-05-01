package com.github.onran0.medpow8.disassembler;

public class DisassemblyException extends Exception {
    private int index = -1;

    public DisassemblyException(String message) {
        super(message);
    }

    public DisassemblyException(final String message, int index) {
        super(message);
        this.index = index;
    }

    public DisassemblyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return super.toString() + (index != -1 ? (" at command " + index + "; at byte " + index * 3) : "");
    }
}