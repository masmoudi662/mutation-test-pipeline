java
package zemberek.normalization;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import zemberek.core.ScoredItem;

public class CharacterGraphDecoderTest {

  @Test
  public void testGetSuggestionsWithScores_emptyInput() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("");
    assertTrue(suggestions.isEmpty());
  }

  @Test
  public void testGetSuggestionsWithScores_simpleInput() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("abc");
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_nullInput() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    try {
      List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      // Expected
    }
  }

  @Test
  public void testGetSuggestionsWithScores_turkishCharacters() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("ışğüöç");
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_numbers() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("123");
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_specialCharacters() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("!@#");
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_mixedCharacters() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("a1b2c");
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_longInput() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    String longInput = "abcdefghijklmnopqrstuvwxyz";
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores(longInput);
    assertNotNull(suggestions);
  }

  @Test
  public void testGetSuggestionsWithScores_whitespaceInput() {
    CharacterGraphDecoder decoder = new CharacterGraphDecoder();
    List<ScoredItem<String>> suggestions = decoder.getSuggestionsWithScores("   ");
    assertNotNull(suggestions);
  }

}