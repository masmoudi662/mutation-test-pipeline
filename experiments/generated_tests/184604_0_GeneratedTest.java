java
package twitter4j.util;

import junit.framework.TestCase;

public class CharacterUtilTest extends TestCase {

    public void testCountEmptyString() {
        assertEquals(0, CharacterUtil.count(""));
    }

    public void testCountSingleCharacter() {
        assertEquals(1, CharacterUtil.count("a"));
    }

    public void testCountMultipleCharacters() {
        assertEquals(5, CharacterUtil.count("hello"));
    }

    public void testCountWithSpaces() {
        assertEquals(5, CharacterUtil.count("  abc"));
        assertEquals(5, CharacterUtil.count("abc  "));
        assertEquals(5, CharacterUtil.count("a bc "));
    }

    public void testCountWithUnicode() {
        assertEquals(1, CharacterUtil.count("你好"));
    }

    public void testCountWithEmoji() {
        assertEquals(1, CharacterUtil.count("😀"));
    }

    public void testCountWithMixedCharacters() {
        assertEquals(6, CharacterUtil.count("abc 你好"));
        assertEquals(6, CharacterUtil.count("abc😀你好"));
    }

    public void testCountWithSpecialCharacters() {
         assertEquals(5, CharacterUtil.count("!@#$%"));
    }

    public void testCountLongString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        assertEquals(1000, CharacterUtil.count(sb.toString()));

    }
}