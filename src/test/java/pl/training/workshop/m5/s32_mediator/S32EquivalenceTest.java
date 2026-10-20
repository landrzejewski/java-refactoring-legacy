package pl.training.workshop.m5.s32_mediator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Scenariusze klikania w ekranie wyboru miejsc: każda akcja użytkownika musi odświeżyć te same
 * komponenty w tej samej kolejności. Wynik to komunikaty przycisku i stan ekranu (render).
 */
final class S32EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepCoordinatesTheScreenTheSame() {
        return Scene.<List<String>, String>variants()
                .variant("start", session(() -> {
                    var screen = new pl.training.workshop.m5.s32_mediator.start.SeatSelectionScreen();
                    return new Ui(screen.seatMap()::click, screen.basket()::remove, screen.basket()::clear,
                            screen.priceSummary()::applyVoucher, screen.payButton()::press, screen::render);
                }))
                .variant("step1", session(() -> {
                    var screen = new pl.training.workshop.m5.s32_mediator.step1.SeatSelectionScreen();
                    return new Ui(screen.seatMap()::click, screen.basket()::remove, screen.basket()::clear,
                            screen.priceSummary()::applyVoucher, screen.payButton()::press, screen::render);
                }))
                .variant("step2", session(() -> {
                    var screen = new pl.training.workshop.m5.s32_mediator.step2.SeatSelectionScreen();
                    return new Ui(screen.seatMap()::click, screen.basket()::remove, screen.basket()::clear,
                            screen.priceSummary()::applyVoucher, screen.payButton()::press, screen::render);
                }))
                .variant("step3", session(() -> {
                    var screen = new pl.training.workshop.m5.s32_mediator.step3.SeatSelectionScreen();
                    return new Ui(screen.seatMap()::click, screen.basket()::remove, screen.basket()::clear,
                            screen.priceSummary()::applyVoucher, screen.payButton()::press, screen::render);
                }))
                .expect("ekran startowy", List.of("render", "pay"), """
                        wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                        Nieaktywny: Wybierz miejsca""")
                .expect("wybór miejsc: koszyk w kolejności kliknięć, VIP w rzędzie C", List.of(
                        "click C2", "click A1", "render"), """
                        wybrane=[A1, C2] sprzedane=[B2] koszyk=[C2, A1] suma=60.00 przycisk=Zaplac 60.00""")
                .expect("drugie kliknięcie odznacza", List.of("click A1", "click A2", "click A1", "render"), """
                        wybrane=[A2] sprzedane=[B2] koszyk=[A2] suma=25.00 przycisk=Zaplac 25.00""")
                .expect("sprzedane i spoza sali są ignorowane", List.of("click B2", "click Z9", "render"), """
                        wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca""")
                .expect("usunięcie z koszyka odznacza na mapie", List.of(
                        "click A1", "click C1", "remove A1", "remove A3", "render"), """
                        wybrane=[C1] sprzedane=[B2] koszyk=[C1] suma=35.00 przycisk=Zaplac 35.00""")
                .expect("limit 4 miejsc wyłącza przycisk", List.of(
                        "click A1", "click A2", "click A3", "click A4", "click C1", "render", "pay",
                        "remove C1", "render"), """
                        wybrane=[A1, A2, A3, A4, C1] sprzedane=[B2] koszyk=[A1, A2, A3, A4, C1] suma=135.00 \
                        przycisk=Maksymalnie 4 miejsca
                        Nieaktywny: Maksymalnie 4 miejsca
                        wybrane=[A1, A2, A3, A4] sprzedane=[B2] koszyk=[A1, A2, A3, A4] suma=100.00 przycisk=Zaplac 100.00""")
                .expect("voucher przelicza sumę i przycisk", List.of(
                        "click C1", "click C2", "voucher KINO10", "render", "voucher XYZ", "render"), """
                        wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=63.00 przycisk=Zaplac 63.00
                        wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=70.00 przycisk=Zaplac 70.00""")
                .expect("płatność sprzedaje miejsca i czyści ekran", List.of(
                        "click A1", "click C4", "voucher KINO10", "pay", "render", "click A1", "click A2", "render"), """
                        Zaplacono 54.00 za A1,C4
                        wybrane=[] sprzedane=[A1, B2, C4] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                        wybrane=[A2] sprzedane=[A1, B2, C4] koszyk=[A2] suma=22.50 przycisk=Zaplac 22.50""")
                .expect("wyczyszczenie koszyka", List.of("click A1", "click C3", "clear", "render"), """
                        wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca""")
                .tests();
    }

    /** Akcje użytkownika na komponentach ekranu. */
    record Ui(Consumer<String> click, Consumer<String> remove, Runnable clear, Consumer<String> voucher,
              Supplier<String> pay, Supplier<String> render) {
        /** Zwraca komunikat dla "pay" i "render", null dla akcji bez komunikatu. */
        String handle(String line) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "click" -> click.accept(parts[1]);
                case "remove" -> remove.accept(parts[1]);
                case "clear" -> clear.run();
                case "voucher" -> voucher.accept(parts[1]);
                case "pay" -> {
                    return pay.get();
                }
                case "render" -> {
                    return render.get();
                }
                default -> throw new IllegalArgumentException(line);
            }
            return null;
        }
    }

    private static Function<List<String>, String> session(Supplier<Ui> screen) {
        return lines -> {
            Ui ui = screen.get();
            return String.join("\n", lines.stream().map(ui::handle).filter(Objects::nonNull).toList());
        };
    }
}
