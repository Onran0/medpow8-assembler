package com.github.onran0.medpow8.assembler.token;

import com.github.onran0.medpow8.assembler.AssemblyException;
import com.github.onran0.medpow8.util.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

import static com.github.onran0.medpow8.assembler.token.TokenType.*;

public class Tokenizer {
    private static final String IGNORABLE = "\t\r ";
    private static final String LETTERS = "qwertyuiopasdfghjklzxcvbnm";
    private static final char REGISTER_DECLARATION = 'r';
    private static final char FLAG_E_DECLARATION = 'e';
    private static final char FLAG_L_DECLARATION = 'l';
    private static final char FLAG_H_DECLARATION = 'h';
    private static final String FLAGS = "" + FLAG_E_DECLARATION + FLAG_L_DECLARATION + FLAG_H_DECLARATION;
    private static final char LABEL_DECLARATION = ':';
    private static final String SP_REGISTER = "sp";
    private static final String DIGITS = "0123456789";
    private static final char POINTER = '%';
    private static final char COMMA = ',';
    private static final char COMMENT_START = ';';
    private static final Map<String, TokenType> COMMAND_NAME_TO_TYPE = new HashMap<>();

    static {
        for(TokenType type : TokenType.values())
            COMMAND_NAME_TO_TYPE.put(type.getName(), type);
    }

    private final Stack<Character> chars;

    public Tokenizer(InputStream in) throws IOException {
        this(IO.readStream(in));
    }

    public Tokenizer(Reader reader) throws IOException {
        this(IO.readFromReader(reader));
    }

    public Tokenizer(String input) {
        this(new StringBuilder(input));
    }

    public Tokenizer(StringBuilder input) {
        chars = new Stack<>();

        for(int i = input.length() - 1; i >= 0; i--)
            chars.push(input.charAt(i));
    }

    public Tokenizer(Stack<Character> chars) {
        this.chars = chars;
    }

    public List<Token> tokenize() throws AssemblyException {
        return tokenize(chars);
    }

    public static List<Token> tokenize(Stack<Character> chars) throws AssemblyException {
        final List<Token> tokens = new ArrayList<>();

        boolean parsingRegister = false,
                parsingNum = false,
                parsingCommandOrLabel = false,
                parsingComment = false;

        StringBuilder buffer = new StringBuilder();

        int line = 1;
        int column = 1;

        boolean prevIsJmp = false;

        while(!chars.isEmpty()) {
            char c = chars.pop();

            Character next = null;

            if (!chars.isEmpty())
                next = chars.get(chars.size() - 1);

            if (parsingComment || c == COMMENT_START) {
                if (!parsingComment)
                    parsingComment = true;
                else
                    buffer.append(c);

                if (next == null || next == '\n') {
                    parsingComment = false;
                    tokens.add(new Token(COMMENT, buffer.toString(), line, column));
                    buffer.setLength(0);
                }
            } else if (parsingRegister) {
                if (DIGITS.indexOf(c) == -1)
                    throw new AssemblyException("digit expected", line, column);
                 else {
                    int num = DIGITS.indexOf(c);

                    if (num > 3)
                        throw new AssemblyException("invalid register number", line, column);

                    TokenType type = switch (num) {
                        case 0 -> TokenType.REG0;
                        case 1 -> TokenType.REG1;
                        case 2 -> TokenType.REG2;
                        case 3 -> TokenType.REG3;
                        default -> null;
                    };

                    parsingRegister = false;
                    tokens.add(new Token(type, null, line, column));
                }
            } else if (parsingNum || DIGITS.indexOf(c) != -1) {
                if (!parsingNum)
                    parsingNum = true;

                buffer.append(c);

                if (next == null || DIGITS.indexOf(next) == -1) {
                    parsingNum = false;
                    tokens.add(new Token(CONST, buffer.toString(), line, column));
                    buffer.setLength(0);
                }
            } else if(!parsingCommandOrLabel && (c == SP_REGISTER.charAt(0) || FLAGS.indexOf(c) != -1)) {
                if(c == SP_REGISTER.charAt(0)) {
                    next = chars.pop();

                    if(next == null)
                        throw new AssemblyException("unexpected end", line, column);

                    if(next == SP_REGISTER.charAt(1))
                        tokens.add(new Token(REG_SP, null, line, column));
                    else
                        throw new AssemblyException("'sp' expected", line, column);
                } else if(FLAGS.indexOf(c) != -1) {
                    TokenType type = switch(c) {
                        case FLAG_E_DECLARATION -> TokenType.FLAG_E;
                        case FLAG_L_DECLARATION -> TokenType.FLAG_L;
                        case FLAG_H_DECLARATION -> TokenType.FLAG_H;
                        default -> null;
                    };

                    tokens.add(new Token(type, null, line, column));
                }
            } else if (parsingCommandOrLabel || LETTERS.indexOf(c) != -1) {
                if (c == REGISTER_DECLARATION && next != null && DIGITS.indexOf(next) != -1)
                    parsingRegister = true;
                else {
                    if (!parsingCommandOrLabel)
                        parsingCommandOrLabel = true;

                    buffer.append(c);

                    if (next == null || (LETTERS.indexOf(next) == -1) && next != LABEL_DECLARATION) {
                        parsingCommandOrLabel = false;

                        TokenType type = COMMAND_NAME_TO_TYPE.get(buffer.toString());

                        if (type == null && !prevIsJmp)
                            throw new AssemblyException("undefined command:" + buffer, line, column);
                        else if(prevIsJmp) {
                            tokens.add(new Token(LABEL_REFERENCE, buffer.toString(), line, column));

                            buffer.setLength(0);
                        } else {
                            tokens.add(new Token(type, null, line, column));

                            prevIsJmp = type.isJmp();

                            buffer.setLength(0);
                        }
                    } else if(next == LABEL_DECLARATION) {
                        chars.pop();

                        parsingCommandOrLabel = false;

                        prevIsJmp = false;

                        tokens.add(new Token(TokenType.LABEL_DECLARATION, buffer.toString(), line, column));

                        buffer.setLength(0);
                    }
                }
            } else if (c == POINTER) {
                tokens.add(new Token(TokenType.POINTER, null, line, column));
            } else if (c == COMMA) {
                tokens.add(new Token(TokenType.COMMA, null, line, column));
            } else if(c == '\n') {
                line++;
                column = 1;
            } else if(IGNORABLE.indexOf(c) == -1)
                throw new AssemblyException("invalid token: '" + c + "'", line, column);

            column++;
        }

        return tokens;
    }
}