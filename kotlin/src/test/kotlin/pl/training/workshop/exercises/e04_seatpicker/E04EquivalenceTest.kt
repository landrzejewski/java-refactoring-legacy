package pl.training.workshop.exercises.e04_seatpicker

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.support.Scene

/**
 * Skrypty kasjera na koszyku miejsc: każda operacja, jej cofnięcie, błędy i stan po błędzie.
 * Skrypty z voucherem i AUTO są celowo poza tym testem - to zachowanie zmienia się w kroku 2
 * (patrz E04SolutionTest).
 */
class E04EquivalenceTest {
    @TestFactory
    fun everyStepRunsTheScriptTheSame(): List<DynamicTest> = Scene.variants<PickerRun, String>()
        .variant("start") { r -> pl.training.workshop.exercises.e04_seatpicker.start.SeatPickerSession(r.plan).run(r.script) }
        .variant("step1") { r -> pl.training.workshop.exercises.e04_seatpicker.step1.SeatPickerSession(r.plan).run(r.script) }
        .variant("step2") { r -> pl.training.workshop.exercises.e04_seatpicker.step2.SeatPickerSession(r.plan).run(r.script) }
        .expect("pusty koszyk", PickerRun.standard(), EMPTY)
        .expect(
            "dwa miejsca", PickerRun.standard("SELECT 6-4", "SELECT 6-7"),
            "miejsca: 6-4 6-7 | okulary: wypozyczone | voucher: - | suma: 70.00"
        )
        .expect(
            "VIP z wlasnymi okularami", PickerRun.standard("SELECT 10-1", "GLASSES own"),
            "miejsca: 10-1 | okulary: wlasne | voucher: - | suma: 42.00"
        )
        .expect(
            "voucher KINO10", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "VOUCHER KINO10"),
            "miejsca: 6-4 6-7 | okulary: wypozyczone | voucher: KINO10 | suma: 63.00"
        )
        .expect(
            "mala litera operacji", PickerRun.standard("select 6-4"),
            "miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect(
            "deselect", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "DESELECT 6-4"),
            "miejsca: 6-7 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect(
            "swap zachowuje kolejnosc", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "SWAP 6-4 6-8"),
            "miejsca: 6-8 6-7 | okulary: wypozyczone | voucher: - | suma: 70.00"
        )
        .expect(
            "undo select", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "UNDO"),
            "miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect(
            "undo deselect wraca na pozycje", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "DESELECT 6-4", "UNDO"),
            "miejsca: 6-4 6-7 | okulary: wypozyczone | voucher: - | suma: 70.00"
        )
        .expect(
            "undo swap", PickerRun.standard("SELECT 6-4", "SWAP 6-4 6-8", "UNDO"),
            "miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect("undo okularow i vouchera", PickerRun.standard("GLASSES own", "VOUCHER KINO10", "UNDO", "UNDO"), EMPTY)
        .expect(
            "undo clear", PickerRun.standard("SELECT 6-4", "SELECT 6-7", "CLEAR", "UNDO"),
            "miejsca: 6-4 6-7 | okulary: wypozyczone | voucher: - | suma: 70.00"
        )
        .expect(
            "auto omija zajete miejsca w srodkowym rzedzie", PickerRun.standard("AUTO 3"),
            "miejsca: 6-1 6-2 6-3 | okulary: wypozyczone | voucher: - | suma: 105.00"
        )
        .expect(
            "auto czysci koszyk, undo go przywraca", PickerRun.standard("SELECT 10-1", "GLASSES own", "AUTO 2", "UNDO"),
            "miejsca: 10-1 | okulary: wlasne | voucher: - | suma: 42.00"
        )
        .expect(
            "auto w prawie pelnej sali bierze srodkowy rzad", PickerRun.almostFull("AUTO 1"),
            "miejsca: 2-2 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect(
            "auto zawodzi i przywraca koszyk", PickerRun.almostFull("SELECT 1-1", "GLASSES own", "AUTO 2"),
            "ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wlasne | voucher: - | suma: 32.00"
        )
        .expect(
            "limit 4 miejsc", PickerRun.standard("SELECT 6-1", "SELECT 6-2", "SELECT 6-3", "SELECT 6-4", "SELECT 6-7"),
            "ERROR: limit 4 miejsc @ miejsca: 6-1 6-2 6-3 6-4 | okulary: wypozyczone | voucher: - | suma: 140.00"
        )
        .expect("auto ponad limit", PickerRun.standard("AUTO 5"), "ERROR: limit 4 miejsc @ $EMPTY")
        .expect("miejsce zajete", PickerRun.standard("SELECT 6-5"), "ERROR: miejsce 6-5 jest zajete @ $EMPTY")
        .expect(
            "miejsce juz w koszyku", PickerRun.standard("SELECT 6-4", "SELECT 6-4"),
            "ERROR: miejsce 6-4 jest juz w koszyku @ miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect("nie ma takiego miejsca", PickerRun.standard("SELECT 13-1"), "ERROR: nie ma miejsca 13-1 @ $EMPTY")
        .expect(
            "swap na miejsce spoza koszyka", PickerRun.standard("SELECT 6-4", "SWAP 6-7 6-8"),
            "ERROR: miejsca 6-7 nie ma w koszyku @ miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00"
        )
        .expect("nie ma czego cofnac", PickerRun.standard("UNDO"), "ERROR: nie ma czego cofnac @ $EMPTY")
        .expect("nieznana operacja", PickerRun.standard("FOO"), "ERROR: nieznana operacja: FOO @ $EMPTY")
        .expect("nieznany voucher", PickerRun.standard("VOUCHER XYZ"), "ERROR: nieznany voucher: XYZ @ $EMPTY")
        .expect("brak argumentu", PickerRun.standard("SELECT"), "ERROR: brak argumentu operacji SELECT @ $EMPTY")
        .tests()

    private companion object {
        const val EMPTY = "miejsca: - | okulary: wypozyczone | voucher: - | suma: 0.00"
    }
}
