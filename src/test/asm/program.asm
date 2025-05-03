test:
    mov %2, r0
    mov sp, %r2
    mov e, %23
    mov h, r3
    mov r0, r2
    jmp blyat

blyat:
    mov %2, %65
    jmp test