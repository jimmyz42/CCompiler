block0:
partition:
    mov %r15, %r14
    sub 1, %r14
    mov %r14, %r15
    add 1, %r15
    mov %r15, %r14
    mov 0, %r14
block1:
    jne block11
block2:
    sub 1, %r14
    mov %r14, %r15
    mov 0, %r15
block3:
    jne block5
block4:
    jne block24
block5:
    add 1, %r15
    mov %r15, %r14
block6:
    jne block9
block7:
    jne block23
block8:
    mov %r14, %r15
block9:
    jne block11
block10:
    mov %r15, %r14
    mov %r14, %r15
    mov %r15, %r14
    add 1, %r14
    mov %r14, %r15
block11:
main:
    mov 10, %r15
    mov 0, %r15
block12:
    jne block14
block13:
    mov %r15, %r14
    mov %r14, %r15
    add 1, %r15
    mov %r15, %r14
block14:
    mov 0, %r14
block15:
    jne block17
block16:
    add 1, %r14
    mov %r14, %r15
block17:
    mov 0, %r15
block18:
    jne block20
block19:
    add 1, %r15
    mov %r15, %r14
block20:
quicksort:
    jne block22
block21:
    mov %r14, %r15
block22:
block23:
    add 1, %r15
    mov %r15, %r14
block24:
    sub 1, %r14
    mov %r14, %r15
    add 1, %r15
    mov %r15, %r14
