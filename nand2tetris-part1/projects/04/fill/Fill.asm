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
//  KEYBOARD_LOOP:
//    screen_color = black
//    if (any key pressed) goto COLOR_SCREEN
//    screen_color = white
//    if (no key pressed) COLOR_SCREEN
//    goto KEYBOARD_LOOP
// 
//  COLOR_SCREEN:
//
//  row = 0
//  col = 0
//  word = @SCREEN // pointer to location
//
//  OUTER_SCREEN_LOOP:
//    if row >= 256 goto OUTER_SCREEN_STOP
//    INNER_SCREEN_LOOP:
//      if col >= 32 goto INNER_SCREEN_STOP
//      col += 1
//      SCREEN[word] = screen_color
//      word += 1
//      goto INNER_SCREEN_LOOP
//    INNER_SCREEN_STOP:
//      col = 0
//      row += 1  
//      goto OUTER_SCREEN_LOOP
//  OUTER_SCREEN_STOP:
//    goto KEYBOARD_LOOP

(KEYBOARD_LOOP)
  
  @screen_color
  M=-1

  @KBD
  D=M
  @COLOR_SCREEN
  D;JGT

  @screen_color
  M=0

  @KBD
  D=M
  @COLOR_SCREEN
  D;JEQ

  @KEYBOARD_LOOP
  0;JMP


(COLOR_SCREEN)

  @row
  M=0

  @col
  M=0

  @SCREEN
  D=A
  @word
  M=D

(OUTER_SCREEN_LOOP)
  @row
  D=M
  @256
  D=D-A // D holding 'row - 256'
  @OUTER_SCREEN_STOP
  D;JGE // row - 256 >= 0

  (INNER_SCREEN_LOOP)
    @col
    D=M
    @32
    D=D-A // D holding 'col - 32'
    @INNER_SCREEN_STOP
    D;JGE // col - 32 >= 0

    @col
    M=M+1

    // make word the desired screen color
    @screen_color
    D=M
    @word
    A=M
    M=D

    @word
    M=M+1

    @INNER_SCREEN_LOOP
    0;JMP

  (INNER_SCREEN_STOP)
    @col
    M=0

    @row
    M=M+1
    
    @OUTER_SCREEN_LOOP
    0;JMP

(OUTER_SCREEN_STOP)
  @KEYBOARD_LOOP
  0;JMP
