Decaf Sugar
=============

A scanner and parser for Decaf written in Java

#### Note

This project is using ANTLR4, NOT ANTLR2, which cause some of the tests to fail..although the result isn't necessarily incorrect.

ANTLR4 better handles arbitrary lookahead and left recursion. :-)

For looking at the CFG: 
RUN FROM ECLIPSE 
dot -Tpng -o foo.png foo.dot
