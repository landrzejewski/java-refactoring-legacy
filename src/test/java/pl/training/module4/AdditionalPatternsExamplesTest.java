package pl.training.module4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import pl.training.module4.behavioral.iterator.Playlist;
import pl.training.module4.behavioral.iterator.Song;
import pl.training.module4.behavioral.memento.accounts.Account;
import pl.training.module4.behavioral.state.OrderStatus;
import pl.training.module4.behavioral.strategy.movies.MovieType;
import pl.training.module4.behavioral.strategy.movies.Order;
import pl.training.module4.creational.abstractfactory.ftp.FtpConnection;
import pl.training.module4.structural.adapter.AirConditioningController;
import pl.training.module4.structural.adapter.TemperatureControllerAdapter;
import pl.training.module4.structural.flyweight.TreeFactory;

@ResourceLock(Resources.SYSTEM_OUT)
final class AdditionalPatternsExamplesTest {
    @Test
    void convertsTemperatureDifferencesWithoutAnAbsoluteScaleOffset() {
        var controller = new RecordingAirConditioningController();
        var adapter = new TemperatureControllerAdapter(controller);

        adapter.temperatureUp(10);
        adapter.temperatureDown(10);

        assertEquals(List.of(18.0, -18.0), controller.deltas);
    }

    @Test
    void doesNotSubtractAChargeDuringTheFreeRentalPeriod() {
        assertEquals(2.0,
                new Order(MovieType.REGULAR).getTotalValue(1));
        assertEquals(1.5,
                new Order(MovieType.CHILDREN).getTotalValue(1));
        assertEquals(3.0,
                new Order(MovieType.NEW_RELEASE).getTotalValue(1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(MovieType.REGULAR).getTotalValue(-1));
    }

    @Test
    void usesTheRegisteredFtpControlPort() {
        assertEquals(21, new FtpConnection().getPort());
    }

    @Test
    void restoresTheBalanceRecordedInTheMemento() {
        var account = new Account(UUID.randomUUID());
        var memento = account.createMemento();

        account.deposit(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, account.getBalance());

        account.restoreMemento(memento);
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void sharesOneFlyweightInstancePerIntrinsicState() {
        assertSame(
                TreeFactory.getTreeType("Oak", "green"),
                TreeFactory.getTreeType("Oak", "green"));
    }

    @Test
    void orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions() {
        var order = new pl.training.module4.behavioral.state.Order();
        assertEquals(OrderStatus.NEW, order.getState());

        order.pay();
        assertEquals(OrderStatus.PAID, order.getState());

        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getState());

        assertThrows(IllegalStateException.class, order::cancel);
        assertEquals(OrderStatus.SHIPPED, order.getState());

        var cancelled = new pl.training.module4.behavioral.state.Order();
        cancelled.cancel();
        assertEquals(OrderStatus.CANCELLED, cancelled.getState());
        assertThrows(IllegalStateException.class, cancelled::pay);
    }

    @Test
    void functionalStateDemoAlternatesTransitions() {
        assertEquals(
                List.of(
                        "Locked → unlocking",
                        "Unlocked → locking",
                        "Locked → unlocking"),
                outputOf(() -> pl.training.module4.fn
                        .FunctionalStyleState.main(new String[0])));
    }

    @Test
    void iteratorDemoWalksThePlaylistForwardAndBackward() {
        assertEquals(
                List.of(
                        "Playing: Intro (90s)",
                        "Playing: Main Theme (225s)",
                        "Playing: Outro (130s)",
                        "Reversed: Outro",
                        "Reversed: Main Theme",
                        "Reversed: Intro"),
                outputOf(() -> pl.training.module4.behavioral.iterator
                        .Application.main(new String[0])));
    }

    @Test
    void playlistIteratorIsExhaustedAfterTheLastSong() {
        var playlist = new Playlist();
        playlist.add(new Song("Only", 60));
        var iterator = playlist.iterator();

        assertEquals(new Song("Only", 60), iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertFalse(new Playlist().reversed().iterator().hasNext());
    }

    @Test
    void bridgeCombinesEveryMessageKindWithEverySender() {
        assertEquals(
                List.of(
                        "Email to anna: Server restarted",
                        "SMS to anna: Server restarted",
                        "Email to anna: [URGENT] SERVER RESTARTED",
                        "SMS to anna: [URGENT] SERVER RESTARTED"),
                outputOf(() -> pl.training.module4.structural.bridge
                        .Application.main(new String[0])));
    }

    @Test
    void mediatorDeliversMessagesToEveryoneExceptTheSender() {
        assertEquals(
                List.of(
                        "Alice sends: Hello everyone",
                        "Bob received from Alice: Hello everyone",
                        "Carol received from Alice: Hello everyone",
                        "Bob sends: Hi Alice",
                        "Alice received from Bob: Hi Alice",
                        "Carol received from Bob: Hi Alice"),
                outputOf(() -> pl.training.module4.behavioral.mediator
                        .Application.main(new String[0])));
    }

    private static List<String> outputOf(Runnable action) {
        synchronized (System.class) {
            PrintStream original = System.out;
            var output = new ByteArrayOutputStream();
            try (var replacement = new PrintStream(
                    output, true, StandardCharsets.UTF_8)) {
                System.setOut(replacement);
                action.run();
            } finally {
                System.setOut(original);
            }
            return output.toString(StandardCharsets.UTF_8).lines().toList();
        }
    }

    private static final class RecordingAirConditioningController
            extends AirConditioningController {
        private final List<Double> deltas = new ArrayList<>();

        @Override
        public void changeTemperature(double deltaInFahrenheit) {
            deltas.add(deltaInFahrenheit);
        }
    }
}
