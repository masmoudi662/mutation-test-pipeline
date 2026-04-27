java
package com.ccsw.coedevon.codingdojo.rockpaperscissorslizardspock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RockPaperTest {

  @Test
  public void testDraw() {

    RockPaper game = new RockPaper();
    assertEquals(0, game.play("ROCK", "ROCK"));
  }

  @Test
  public void testPaperWinsRock() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testScissorsWinPaper() {

    RockPaper game = new RockPaper();
    assertEquals(1, game.play("SCISSORS", "PAPER"));
  }

  @Test
  public void testRockWinsLizard() {

    RockPaper game = new RockPaper();
    assertEquals(1, game.play("ROCK", "LIZZARD"));
  }

  @Test
  public void testLizardWinsSpock() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testSpockWinsScissors() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testScissorsWinsLizard() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testLizardWinsPaper() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testPaperWinsSpock() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("SPOCK", "PAPER"));
  }

  @Test
  public void testSpockWinsRock() {

    RockPaper game = new RockPaper();
    assertEquals(2, game.play("ROCK", "PAPER"));
  }

  @Test
  public void testPaperWinsPlayerOne() {
    RockPaper game = new RockPaper();
    assertEquals(1, game.play("PAPER", "ROCK"));
  }

}