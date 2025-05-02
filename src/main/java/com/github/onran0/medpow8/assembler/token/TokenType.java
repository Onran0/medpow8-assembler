package com.github.onran0.medpow8.assembler.token;

public enum TokenType {
    // Commands
    NOP("nop", true),
    HLT("hlt", true),
    MOV("mov", true),
    ADD("add", true),
    SUB("sub", true),
    MUL("mul", true),
    DIV("div", true),
    MOD("mod", true),
    NOT("not", true),
    OR("or", true),
    AND("and", true),
    XOR("xor", true),
    LSH("lsh", true),
    RSH("rsh", true),
    SUBL("subl", true),
    DIVL("divl", true),
    MODL("modl", true),
    LSHL("lshl", true),
    RSHL("rshl", true),
    CMP("cmp", true),
    JMP("jmp", true),
    JE("je", true),
    JL("jl", true),
    JH("jh", true),
    JEL("jel", true),
    JEH("jeh", true),
    PUSH("push", true),
    POP("pop", true),
    DIAL("dial", true),
    DISPL("displ", true),
    CPORT("cport", true),
    WPORT("wport", true),
    RPORT("rport", true),
    IPORT("iport", true),
    // Other
    REG0("r0", false),
    REG1("r1", false),
    REG2("r2", false),
    REG3("r3", false),
    CONST(null, false),
    COMMA(",", false),
    COMMENT(";", false),
    POINTER("%", false),
    FLAG_E("e", false),
    FLAG_L("l", false),
    FLAG_H("h", false),
    REG_SP("sp", false),
    LABEL(":", false);
    private final boolean isCommand;
    private final String name;

    TokenType(String name, boolean isCommand) {
        this.name = name;
        this.isCommand = isCommand;
    }

    public String getName() {
        return name;
    }

    public boolean isCommand() {
        return isCommand;
    }
}