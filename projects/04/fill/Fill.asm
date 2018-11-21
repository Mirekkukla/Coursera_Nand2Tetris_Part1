// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// PSEUDO-CODE
//
//
//  LOOP:
//    if (any key pressed) goto BLACKEN_SCREEN
//    (make screen white)
//    goto LOOP
//  BLACKEN_SCREEN:
//    (blacken the screen)
//    goto LOOP

//  PSEUDOCODE for darkening screen
//  row = 0
//  col = 0
//  word = @SCREEN // pointer to location
//
//  OUTER_LOOP:
//    if row >= 256 goto OUTER_STOP
//    INNER_LOOP:
//      if col >= 32 goto INNER_STOP
//      col += 1
//      SCREEN[word] = -1
//      word += 1
//      goto INNER_LOOP
//    INNER_STOP:
//      col = 0
//      row += 1  
//      goto OUTER_LOOP
//    goto OUTER_LOOP
//  OUTER_STOP:
//    //nothing for now, will return to keyboard loop

  @row
  M=0

  @col
  M=0

  @SCREEN
  D=A
  @word
  M=D

(OUTER_LOOP)
  @row
  D=M
  @256
  D=D-A // D holding 'row - 256'
  @OUTER_STOP
  D;JGE // row - 256 >= 0

  (INNER_LOOP)
    @col
    D=M
    @32
    D=D-A // D holding 'col - 32'
    @INNER_STOP
    D;JGE // col - 32 >= 0

    @col
    M=M+1

    // make word dark
    @word
    A=M
    M=-1

    @word
    M=M+1

    @INNER_LOOP
    0;JMP

  (INNER_STOP)
    @col
    M=0

    @row
    M=M+1
    
    @OUTER_LOOP
    0;JMP

(OUTER_STOP)
  // nothing for now

(END)
  @END
  0;JMP