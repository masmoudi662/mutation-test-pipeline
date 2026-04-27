java
package com.taxonic.carml.rdf_mapper.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.carml.rdfmapper.TypeDecider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarmlMapperTest {

  private CarmlMapper carmlMapper;

  @Mock private TypeDecider typeDecider;

  @BeforeEach
  void setUp() {
    carmlMapper =
        new CarmlMapper(
            new DynamicModelFactory(),
            Collections.emptySet(),
            Collections.emptySet(),
            typeDecider,
            null);
  }

  @Test
  void map_multipleInterfaces_noException() {
    Model model = mock(Model.class);
    Resource resource = mock(Resource.class);
    Set<Type> types = new HashSet<>();
    types.add(SomeInterface.class);
    types.add(AnotherInterface.class);

    when(typeDecider.decide(model, resource, types)).thenReturn(SomeInterface.class);

    carmlMapper.map(model, resource, types);
  }

  @Test
  void map_singleConcreteClass_noException() {
    Model model = mock(Model.class);
    Resource resource = mock(Resource.class);
    Set<Type> types = new HashSet<>();
    types.add(SomeConcreteClass.class);

    carmlMapper.map(model, resource, types);
  }

  interface SomeInterface {}

  interface AnotherInterface {}

  static class SomeConcreteClass {}
}