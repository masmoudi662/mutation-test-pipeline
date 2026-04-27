java
package com.google.devtools.build.bfg;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReferencedClassesParserTest {

  @Test
  public void testExtractClassNameFromQualifiedName_simpleClassName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("MyClass"))
        .isEqualTo("MyClass");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_qualifiedClassName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.example.MyClass"))
        .isEqualTo("com.example.MyClass");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_nestedClassName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.example.MyClass.InnerClass"))
        .isEqualTo("com.example.MyClass.InnerClass");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_classPrefixedWithPackage() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("java.util.List"))
        .isEqualTo("java.util.List");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_emptyString() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("")).isEqualTo("");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_lowercasePackageName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.example.lower.MyClass"))
        .isEqualTo("com.example.lower.MyClass");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_lowercaseClassName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.example.myclass"))
        .isEqualTo("");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_mixedCasePackageName() {
      assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.Example.MyClass"))
          .isEqualTo("com.Example.MyClass");
  }

  @Test
  public void testExtractClassNameFromQualifiedName_numberInClassName() {
    assertThat(ReferencedClassesParser.extractClassNameFromQualifiedName("com.example.MyClass123"))
        .isEqualTo("com.example.MyClass123");
  }
}