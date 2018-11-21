// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Simply sum R0 exactly R1 times


// PSEUDO-CODE
//
//  i = 0
//  sum = 0
//
//  LOOP:
//    if i >= R1 goto STOP
//    sum += R0
//    i++
//    goto LOOP
//  STOP:
//    R2 = sum

  @i
  M=0 // i = 0

  @sum
  M=0 // sum = 0

(LOOP)
  @i
  D=M
  @R1
  D=D-M // D is holding i - R1

  @STOPLOOP
  D;JGE // i - R1 >= 0

  @R0
  D=M
  @sum
  M=M+D // sum += R0

  @i
  M=M+1 // i++

  @LOOP
  0;JMP


(STOPLOOP)
  @sum
  D=M
  @R2
  M=D // R2 = sum

(END)
  @END
  0;JMP