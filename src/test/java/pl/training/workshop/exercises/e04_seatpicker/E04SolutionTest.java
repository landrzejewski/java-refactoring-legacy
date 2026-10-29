package pl.training.workshop.exercises.e04_seatpicker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e04_seatpicker.step2.AutoArrange;
import pl.training.workshop.exercises.e04_seatpicker.step2.Cart;
import pl.training.workshop.exercises.e04_seatpicker.step2.ClearSeats;
import pl.training.workshop.exercises.e04_seatpicker.step2.Command;
import pl.training.workshop.exercises.e04_seatpicker.step2.DeselectSeat;
import pl.training.workshop.exercises.e04_seatpicker.step2.SelectSeat;
import pl.training.workshop.exercises.e04_seatpicker.step2.SetGlasses;
import pl.training.workshop.exercises.e04_seatpicker.step2.SetVoucher;
import pl.training.workshop.exercises.e04_seatpicker.step2.SwapSeats;

/**
 * Kontrakt rozwiązania: każde polecenie cofa dokładnie siebie, migawka koszyka jest nieprzezroczysta,
 * a pełna migawka naprawia zgubiony voucher (świadoma zmiana zachowania względem start).
 */
final class E04SolutionTest {
    @Test
    void everyCommandUndoesExactlyItself() {
        Cart cart = new Cart(HallPlan.standard());
        new SelectSeat(cart, new Seat(6, 4)).execute();
        new SetVoucher(cart, "KINO10").execute();
        String before = cart.state();

        List<Command> commands = List.of(
                new SelectSeat(cart, new Seat(6, 7)),
                new SwapSeats(cart, new Seat(6, 4), new Seat(6, 8)),
                new DeselectSeat(cart, new Seat(6, 4)),
                new SetGlasses(cart, true),
                new ClearSeats(cart),
                new AutoArrange(cart, 2));
        for (Command command : commands) {
            command.execute();
            command.undo();
            assertEquals(before, cart.state(), command.getClass().getSimpleName());
        }
    }

    @Test
    void mementoIsOpaqueOutsideTheCart() {
        Class<?> memento = Cart.Memento.class;
        long methods = Arrays.stream(memento.getDeclaredMethods()).filter(method -> !method.isSynthetic()).count();
        assertEquals(0, methods, "migawka nie ma metod");
        for (Field field : memento.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                assertTrue(Modifier.isPrivate(field.getModifiers()), field.getName() + " jest prywatne");
            }
        }
        for (var constructor : memento.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()), "tylko koszyk tworzy migawkę");
        }
    }

    @Test
    void commandsCarryTheirOwnUndoDataAndShareNothing() {
        for (Class<?> command : List.of(SelectSeat.class, DeselectSeat.class, SwapSeats.class,
                SetGlasses.class, SetVoucher.class, ClearSeats.class, AutoArrange.class)) {
            for (Field field : command.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    assertTrue(Modifier.isFinal(field.getModifiers()),
                            command.getSimpleName() + "." + field.getName() + " to współdzielony stan");
                }
            }
        }
    }

    @Test
    void failedAutoUsedToLoseTheVoucherAndNowRestoresIt() {
        PickerRun run = PickerRun.almostFull("VOUCHER KINO10", "SELECT 1-1", "AUTO 2");
        String lost = "ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: - | suma: 35.00";
        String kept = "ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50";
        assertEquals(lost, new pl.training.workshop.exercises.e04_seatpicker.start.SeatPickerSession(run.plan()).run(run.script()));
        assertEquals(lost, new pl.training.workshop.exercises.e04_seatpicker.step1.SeatPickerSession(run.plan()).run(run.script()));
        assertEquals(kept, new pl.training.workshop.exercises.e04_seatpicker.step2.SeatPickerSession(run.plan()).run(run.script()));
    }

    @Test
    void undoAfterAutoUsedToLoseTheVoucherAndNowRestoresIt() {
        PickerRun run = PickerRun.standard("VOUCHER KINO10", "SELECT 6-4", "AUTO 2", "UNDO");
        String lost = "miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00";
        String kept = "miejsca: 6-4 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50";
        assertEquals(lost, new pl.training.workshop.exercises.e04_seatpicker.start.SeatPickerSession(run.plan()).run(run.script()));
        assertEquals(kept, new pl.training.workshop.exercises.e04_seatpicker.step2.SeatPickerSession(run.plan()).run(run.script()));
    }
}
