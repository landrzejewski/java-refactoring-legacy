package pl.training.workshop.exercises.e04_seatpicker

import java.lang.reflect.Modifier

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumingThat
import org.junit.jupiter.api.Test

import pl.training.workshop.exercises.e04_seatpicker.step2.AutoArrange
import pl.training.workshop.exercises.e04_seatpicker.step2.Cart
import pl.training.workshop.exercises.e04_seatpicker.step2.ClearSeats
import pl.training.workshop.exercises.e04_seatpicker.step2.Command
import pl.training.workshop.exercises.e04_seatpicker.step2.DeselectSeat
import pl.training.workshop.exercises.e04_seatpicker.step2.SelectSeat
import pl.training.workshop.exercises.e04_seatpicker.step2.SetGlasses
import pl.training.workshop.exercises.e04_seatpicker.step2.SetVoucher
import pl.training.workshop.exercises.e04_seatpicker.step2.SwapSeats

/**
 * Kontrakt rozwiązania: każde polecenie cofa dokładnie siebie, migawka koszyka jest nieprzezroczysta,
 * a pełna migawka naprawia zgubiony voucher (świadoma zmiana zachowania względem start).
 */
class E04SolutionTest {
    @Test
    fun everyCommandUndoesExactlyItself() {
        val cart = Cart(HallPlan.standard())
        SelectSeat(cart, Seat(6, 4)).execute()
        SetVoucher(cart, "KINO10").execute()
        val before = cart.state()

        val commands: List<Command> = listOf(
            SelectSeat(cart, Seat(6, 7)),
            SwapSeats(cart, Seat(6, 4), Seat(6, 8)),
            DeselectSeat(cart, Seat(6, 4)),
            SetGlasses(cart, true),
            ClearSeats(cart),
            AutoArrange(cart, 2)
        )
        for (command in commands) {
            command.execute()
            command.undo()
            assertEquals(before, cart.state(), command.javaClass.simpleName)
        }
    }

    @Test
    fun mementoIsOpaqueOutsideTheCart() {
        // Różnica względem Javy: w Kotlinie klasa zewnętrzna nie widzi prywatnych składowych klasy
        // zagnieżdżonej, więc Cart.Memento jest pustym (sealed) interfejsem, a dane migawki trzyma
        // prywatna klasa koszyka. Sprawdzamy to samo: publiczny typ migawki nie ma metod ani pól,
        // a jej treść (i konstruktor) są dostępne tylko w koszyku.
        val memento = Cart.Memento::class.java
        val methods = memento.declaredMethods.count { !it.isSynthetic }
        assertEquals(0, methods, "migawka nie ma metod")
        assertEquals(0, memento.declaredFields.count { !it.isSynthetic }, "migawka nie ma pol")
        val contents = Cart::class.java.declaredClasses.filter { it != memento && memento.isAssignableFrom(it) }
        assertTrue(contents.isNotEmpty(), "koszyk ma wlasna tresc migawki")
        for (content in contents) {
            assertTrue(Modifier.isPrivate(content.modifiers), "tylko koszyk tworzy migawkę")
        }
    }

    @Test
    fun commandsCarryTheirOwnUndoDataAndShareNothing() {
        for (command in listOf(
            SelectSeat::class.java, DeselectSeat::class.java, SwapSeats::class.java,
            SetGlasses::class.java, SetVoucher::class.java, ClearSeats::class.java, AutoArrange::class.java
        )) {
            for (field in command.declaredFields) {
                if (Modifier.isStatic(field.modifiers)) {
                    assertTrue(Modifier.isFinal(field.modifiers), "${command.simpleName}.${field.name} to współdzielony stan")
                }
            }
        }
    }

    @Test
    fun failedAutoUsedToLoseTheVoucherAndNowRestoresIt() {
        val run = PickerRun.almostFull("VOUCHER KINO10", "SELECT 1-1", "AUTO 2")
        val lost = "ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: - | suma: 35.00"
        val kept = "ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50"
        assumingThat(startHasNoMemento()) {
            assertEquals(lost, pl.training.workshop.exercises.e04_seatpicker.start.SeatPickerSession(run.plan).run(run.script))
        }
        assertEquals(lost, pl.training.workshop.exercises.e04_seatpicker.step1.SeatPickerSession(run.plan).run(run.script))
        assertEquals(kept, pl.training.workshop.exercises.e04_seatpicker.step2.SeatPickerSession(run.plan).run(run.script))
    }

    @Test
    fun undoAfterAutoUsedToLoseTheVoucherAndNowRestoresIt() {
        val run = PickerRun.standard("VOUCHER KINO10", "SELECT 6-4", "AUTO 2", "UNDO")
        val lost = "miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        val kept = "miejsca: 6-4 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50"
        assumingThat(startHasNoMemento()) {
            assertEquals(lost, pl.training.workshop.exercises.e04_seatpicker.start.SeatPickerSession(run.plan).run(run.script))
        }
        assertEquals(kept, pl.training.workshop.exercises.e04_seatpicker.step2.SeatPickerSession(run.plan).run(run.script))
    }

    /** Pułapka start (zgubiony voucher) jest sprawdzana tylko, dopóki w start nie ma pełnej migawki (po "warsztat.sh jump 2"). */
    private fun startHasNoMemento(): Boolean = try {
        Class.forName("pl.training.workshop.exercises.e04_seatpicker.start.Cart\$Memento")
        false
    } catch (exception: ClassNotFoundException) {
        true
    }
}
