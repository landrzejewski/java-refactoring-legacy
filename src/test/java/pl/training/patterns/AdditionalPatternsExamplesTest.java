package pl.training.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.patterns.behavioral.state.OrderStatus;
import pl.training.patterns.behavioral.strategy.movies.MovieType;
import pl.training.patterns.behavioral.strategy.movies.Order;
import pl.training.patterns.creational.abstractfactory.ftp.FtpConnection;
import pl.training.patterns.structural.adapter.AirConditioningController;
import pl.training.patterns.structural.adapter.TemperatureControllerAdapter;

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
    void orderStateMovesThroughItsLifecycleAndRejectsInvalidTransitions() {
        var order = new pl.training.patterns.behavioral.state.Order();
        assertEquals(OrderStatus.NEW, order.getState());

        order.pay();
        assertEquals(OrderStatus.PAID, order.getState());

        order.ship();
        assertEquals(OrderStatus.SHIPPED, order.getState());

        assertThrows(IllegalStateException.class, order::cancel);
        assertEquals(OrderStatus.SHIPPED, order.getState());

        var cancelled = new pl.training.patterns.behavioral.state.Order();
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
                outputOf(() -> pl.training.patterns.fn
                        .FunctionalStyleState.main(new String[0])));
    }

    @Test
    void iteratorDemoHasDeterministicOrder() {
        assertEquals(
                List.of("1", "2", "3", "4", "5"),
                outputOf(() -> pl.training.patterns.behavioral.iterator
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
