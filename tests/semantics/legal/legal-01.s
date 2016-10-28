block0:
partition:
    push 0
    push 0
    mov %r15, %r14
    push %r14
    push 0
    sub 1, %r14
    push %r14
    push 0
    mov %r14, %r15
    push %r15
    push 0
    add 1, %r15
    push %r15
    push 0
    mov %r15, %r14
    push %r14
    push 0
    mov 0, %r14
    push %r14
block1:
    jne block11
block2:
    push 0
    sub 1, %r14
    push %r14
    push 0
    mov %r14, %r15
    push %r15
    push 0
    mov 0, %r15
    push %r15
block3:
    jne block5
block4:
    jne block24
block5:
    push 0
    add 1, %r15
    push %r15
    push 0
    mov %r15, %r14
    push %r14
block6:
    jne block9
block7:
    jne block23
block8:
    push 0
    push 0
    mov %r14, %r15
    push %r15
block9:
    jne block11
block10:
    push 0
    push 0
    mov %r15, %r14
    push %r14
    push 0
    push 0
    mov %r14, %r15
    push %r15
    push 0
    push 0
    mov %r15, %r14
    push %r14
    push 0
    add 1, %r14
    push %r14
    mov %r14, %r15
    push %r15
block11:
main:
    push 0
    mov 10, %r15
    push %r15
    push 0
    mov 0, %r15
    push %r15
block12:
    jne block14
block13:
    push 0
    push 0
    mov %r15, %r14
    push %r14
    push 0
    push 0
    mov %r14, %r15
    push %r15
    push 0
    add 1, %r15
    push %r15
    mov %r15, %r14
    push %r14
block14:
    push 0
    mov 0, %r14
    push %r14
block15:
    jne block17
block16:
    push 0
    add 1, %r14
    push %r14
    mov %r14, %r15
    push %r15
block17:
    push 0
    mov 0, %r15
    push %r15
block18:
    jne block20
block19:
    push 0
    add 1, %r15
    push %r15
    mov %r15, %r14
    push %r14
block20:
quicksort:
    jne block22
block21:
    push 0
    push 0
    mov %r14, %r15
    push %r15
block22:
block23:
    push 0
    add 1, %r15
    push %r15
    mov %r15, %r14
    push %r14
block24:
    push 0
    sub 1, %r14
    push %r14
    push 0
    mov %r14, %r15
    push %r15
    push 0
    add 1, %r15
    push %r15
    mov %r15, %r14
    push %r14
