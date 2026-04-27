java
package com.google.auto.common;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.testing.compile.CompilationRule;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AnnotationValuesTest {

  @Rule public final CompilationRule compilationRule = new CompilationRule();

  private AnnotationValue getAnnotationValue(Class<?> annotationClass, String valueName) {
    Elements elements = compilationRule.getElements();
    Types types = compilationRule.getTypes();
    AnnotationMirror annotation =
        MoreElements.getAnnotationMirror(
                elements.getTypeElement(annotationClass.getName()), TestAnnotation.class)
            .get();
    return MoreAnnotationMirrors.getAnnotationValue(annotation, valueName);
  }

  @Test
  public void getByte() {
    AnnotationValue value = getAnnotationValue(TestAnnotationHolder.class, "byteValue");
    assertThat(AnnotationValues.getByte(value)).isEqualTo((byte) 1);
  }

  @Test
  public void getChar() {
    AnnotationValue value = getAnnotationValue(TestAnnotationHolder.class, "charValue");
    assertThat(AnnotationValues.getChar(value)).isEqualTo('a');
  }

  @Test
  public void getDouble() {
    AnnotationValue value = getAnnotationValue(TestAnnotationHolder.class, "doubleValue");
    assertThat(AnnotationValues.getDouble(value)).isEqualTo(1.0d);
  }

  @Test
  public void getFloat() {
    AnnotationValue value = getAnnotationValue(TestAnnotationHolder.class, "floatValue");
    assertThat(AnnotationValues.getFloat(value)).isEqualTo(1.0f);
  }

  @Test
  public void getInt() {
    AnnotationValue value = getAnnotationValue(TestAnnotationHolder.class, "intValue");
    assertThat(AnnotationValues.getInt(value)).isEqualTo(1);
  }
}