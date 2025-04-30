package com.github.onran0.medpow8;

import com.github.onran0.medpow8.assembler.Assembler;
import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.assembler.parser.Command;
import com.github.onran0.medpow8.assembler.parser.Parser;
import com.github.onran0.medpow8.assembler.token.Token;
import com.github.onran0.medpow8.assembler.token.Tokenizer;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws AssemblyException {
        String code =
        """
        mov r0, %5
        add r0, %r1
        push r2
        pop r3
        hlt
        """;

        System.out.println("\n Tokens \n");

        System.out.println(
                Arrays.toString(
                        new Tokenizer(code).tokenize().toArray(new Token[0])
                )
        );

        System.out.println("\n Commands \n");

        System.out.println(
                Arrays.toString(
                        Parser.parse(
                                new Tokenizer(code).tokenize()
                        ).toArray(new Command[0])
                )
        );

        System.out.println("\n Machine Code \n");

        System.out.println(Arrays.toString(Assembler.assemble(code)));
    }
}