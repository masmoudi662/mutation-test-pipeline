java
package io.jaegertracing.spark.dependencies;

import io.jaegertracing.spark.dependencies.model.Dependency;
import io.jaegertracing.spark.dependencies.model.KeyValue;
import io.jaegertracing.spark.dependencies.model.Reference;
import io.jaegertracing.spark.dependencies.model.Span;
import io.opentracing.tag.Tags;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.spark.api.java.function.FlatMapFunction;

public class SpansToDependencyLinks implements FlatMapFunction<Iterable<Span>, Dependency> {

  @Override
  public java.util.Iterator<Dependency> call(Iterable<Span> spans) throws Exception {
    return null;
  }

  protected Optional<Dependency> sharedSpanDependency(Set<Span> sharedSpans) {
    String clientService = null;
    String serverService = null;
    for (Span span : sharedSpans) {
      for (KeyValue tag : span.getTags()) {
        if (Tags.SPAN_KIND_CLIENT.equals(tag.getValueString())
            || Tags.SPAN_KIND_PRODUCER.equals(tag.getValueString())) {
          clientService = span.getProcess().getServiceName();
        } else if (Tags.SPAN_KIND_SERVER.equals(tag.getValueString())
            || Tags.SPAN_KIND_CONSUMER.equals(tag.getValueString())) {
          serverService = span.getProcess().getServiceName();
        }

        if (clientService != null && serverService != null) {
          return Optional.of(new Dependency(clientService, serverService));
        }
      }
    }
    return Optional.empty();
  }
}