package fpJava.ch12_state;

import fpJava.ch12_state.StateMachine.Condition;
import fpJava.ch12_state.StateMachine.StateTransition;
import fpJava.ch12_state.StateMachine.Transition;
import io.vavr.collection.List;

public final class Atm {
    private Atm() {}

    public static StateMachine<Outcome, Input> create() {
        // process operation:
        //   - if the operation is a deposit
        //     add the amount to the account and add the operation to the operation list
        //     process next operation
        //   - if the operation is a withdraw and the amount is less than the account balance
        //     remove the amount from the account and add the operation to the operation list
        //     process next operation
        //   - else
        //     do not change account nor operation list

        Condition<Outcome, Input> c1 = oi -> oi.value().isDeposit();
        Transition<Outcome, Input> t1 =
            oi -> new Outcome(
                oi.state().account + oi.value().amount(),
                oi.state().operations.prepend(oi.value().amount())
            );

        Condition<Outcome, Input> c2 =
            oi -> oi.value().isWithdraw() && oi.state().account > oi.value().amount();
        Transition<Outcome, Input> t2 =
            oi -> new Outcome(
                oi.state().account - oi.value().amount(),
                oi.state().operations.prepend(- oi.value().amount())
            );

        Condition<Outcome, Input> c3 = oi -> true;
        Transition<Outcome, Input> t3 = StateMachine.SA::state;

        List<StateTransition<Outcome, Input>> transitions = List.of(
            new StateTransition<>(c1, t1),
            new StateTransition<>(c2, t2),
            new StateTransition<>(c3, t3)
        );

        return new StateMachine<>(transitions);
    }

    public sealed interface Input {
        Type type();

        boolean isDeposit();

        default boolean isWithdraw() {return !isDeposit();}

        int amount();

        enum Type {DEPOSIT, WITHDRAW}
    }

    public record Deposit(int amount) implements Input {
        @Override
        public Type type() {return Type.DEPOSIT;}

        @Override
        public boolean isDeposit() {return true;}
    }

    public record Withdraw(int amount) implements Input {
        @Override
        public Type type() {return Type.WITHDRAW;}

        @Override
        public boolean isDeposit() {return false;}
    }

    public record Outcome(int account, List<Integer> operations) {}
}
