package fpJava.ch12_state;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.Random;
import java.util.function.BiFunction;

public sealed interface RNG {
    Tuple2<RNG, Integer> nextInt();
    Tuple2<RNG, Integer> nextPositiveInt(int limit);

    static RNG with(long seed) {
        return new JRng(seed);
    }

    static Tuple2<List<Integer>, RNG> integers(RNG rng, int length) {
        Tuple2<List<Integer>, RNG> z = Tuple.of(List.empty(), rng);
        BiFunction<Tuple2<List<Integer>, RNG>, Integer, Tuple2<List<Integer>, RNG>> combiner = (acc, _) -> {
            List<Integer> xs = acc._1;
            RNG r = acc._2;
            Tuple2<RNG, Integer> next = r.nextInt();
            return Tuple.of(xs.prepend(next._2), next._1);
        };
        return List.range(0, length).foldLeft(z, combiner);
    }

    final class JRng implements RNG {
        private final Random random;

        public JRng(long seed) {
            this.random = new Random(seed);
        }

        @Override
        public Tuple2<RNG, Integer> nextInt() {
            return new Tuple2<>(this, random.nextInt());
        }

        @Override
        public Tuple2<RNG, Integer> nextPositiveInt(int limit) {
            return new Tuple2<>(this, random.nextInt(limit));
        }
    }
}
