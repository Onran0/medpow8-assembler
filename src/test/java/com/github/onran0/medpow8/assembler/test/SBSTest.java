package com.github.onran0.medpow8.assembler.test;

import com.github.onran0.medpow8.assembler.Assembler;
import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.assembler.Command;
import com.github.onran0.medpow8.assembler.MachineCommand;
import com.github.onran0.medpow8.assembler.parser.Parser;
import com.github.onran0.medpow8.assembler.token.Token;
import com.github.onran0.medpow8.assembler.token.Tokenizer;
import com.github.onran0.medpow8.disassembler.Disassembler;
import com.github.onran0.medpow8.disassembler.DisassemblyException;
import com.github.onran0.medpow8.util.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class SBSTest {
    private static void printBin(byte b) {
        System.out.print(ensureLen(Integer.toBinaryString(b & 0xFF), 8));
    }

    private static String ensureLen(String str, int len) {
        if(str.length() >= len)
            return str;
        else
            return "0".repeat(len - str.length()) + str;
    }

    public static void main(String[] args) throws AssemblyException, DisassemblyException, IOException {
        String scriptName = "fibonacci";

        String code = IO.readASMScript(scriptName + ".asm");

        System.out.println("Source \n");

        System.out.println(code);

        System.out.println("\n Tokens \n");

        List<Token> tokens;

        System.out.println(
                Arrays.toString(
                        (tokens = new Tokenizer(code).tokenize()).toArray(new Token[0])
                )
        );

        System.out.println("\n Commands \n");

        List<Command> commands;

        System.out.println(
                Arrays.toString(
                        (commands = Parser.parse(tokens)).toArray(new Command[0])
                )
        );

        System.out.println("\n Machine Code \n");

        for(MachineCommand cmd : Assembler.assembleToList(commands)) {
            System.out.print(ensureLen(String.valueOf(cmd.command() & 0xFF), 3));
            System.out.print(" ");
            printBin(cmd.getOp(0));
            System.out.print(" ");
            printBin(cmd.getOp(1));
            System.out.println();
        }

        System.out.println("\n Disassemble \n");

        byte[] bs = Assembler.assemble(code);

        OutputStream c = new FileOutputStream(scriptName + ".bin");

        c.write(bs);

        c.close();

        System.out.println(Disassembler.disassemble(bs));
    }
}