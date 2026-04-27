java
package ch.fhnw.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

public class Part02TransformTest {

    private final Part02Transform part02Transform = new Part02Transform();

    @Test
    public void testPairValues() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<Integer> flux2 = Flux.just(1, 2, 3);

        Flux<Tuple2<String, Integer>> result = part02Transform.pairValues(flux1, flux2);

        StepVerifier.create(result)
                .expectNextMatches(tuple -> tuple.getT1().equals("A") && tuple.getT2().equals(1))
                .expectNextMatches(tuple -> tuple.getT1().equals("B") && tuple.getT2().equals(2))
                .expectNextMatches(tuple -> tuple.getT1().equals("C") && tuple.getT2().equals(3))
                .verifyComplete();
    }

    @Test
    public void testPairValues_differentLengths() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<Integer> flux2 = Flux.just(1, 2, 3);

        Flux<Tuple2<String, Integer>> result = part02Transform.pairValues(flux1, flux2);

        StepVerifier.create(result)
                .expectNextMatches(tuple -> tuple.getT1().equals("A") && tuple.getT2().equals(1))
                .expectNextMatches(tuple -> tuple.getT1().equals("B") && tuple.getT2().equals(2))
                .verifyComplete();
    }

    @Test
    public void testPairValues_emptyFluxes() {
        Flux<String> flux1 = Flux.empty();
        Flux<Integer> flux2 = Flux.empty();

        Flux<Tuple2<String, Integer>> result = part02Transform.pairValues(flux1, flux2);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void testPairValues_oneEmptyFlux() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<Integer> flux2 = Flux.empty();

        Flux<Tuple2<String, Integer>> result = part02Transform.pairValues(flux1, flux2);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void testPairValues_nullFluxes() {
        Flux<String> flux1 = Flux.just("A", "B");
        Flux<Integer> flux2 = null;

        try {
            part02Transform.pairValues(flux1, flux2);
        } catch (NullPointerException e) {
            return;
        }

        Flux<String> flux3 = null;
        Flux<Integer> flux4 = Flux.just(1,2);

        try {
            part02Transform.pairValues(flux3, flux4);
        } catch (NullPointerException e) {
            return;
        }
    }
}