package fpJava.ch12_state;

import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.function.Function;
import java.util.function.Predicate;

public final class StateMachine<S, A> {
    private final Function<A, State<S, Nothing>> fn;

    public StateMachine(List<StateTransition<S, A>> transitions) {
        fn = a -> State.sequence(m ->
                    Try.success(new SA<>(m, a)).
                        flatMap(t ->
                            transitions.filter(st -> st.condition().test(t))
                                .headOption()
                                .map(st -> st.transition().apply(t))
                                .toTry()).
                        getOrElse(m));
    }

    record SA<S, A>(S state, A value) {}
    interface Condition<S, I> extends Predicate<SA<S, I>> {}
    interface Transition<S, A> extends Function<SA<S, A>, S> {}

    public record StateTransition<S, A>(Condition<S, A> condition, Transition<S, A> transition) {}

    public State<S, S> process(List<A> inputs) {
        List<State<S, Nothing>> a = inputs.map(fn);
        State<S, List<Nothing>> b = State.compose(a);
        return b.flatMap(_ -> State.get());
    }
}
