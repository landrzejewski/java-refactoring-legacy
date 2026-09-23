package pl.training.workshop.m6.s16_templatemethod;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Oba raporty (CSV i HTML) identyczne w każdym kroku - także sortowanie i escapowanie. */
final class S16EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepRendersBothReportsTheSame() {
        return Scene.<List<Sale>, String>variants()
                .variant("start", s -> new pl.training.workshop.m6.s16_templatemethod.start.CsvSalesReport().render(s)
                        + new pl.training.workshop.m6.s16_templatemethod.start.HtmlSalesReport().render(s))
                .variant("step1", s -> new pl.training.workshop.m6.s16_templatemethod.step1.CsvSalesReport().render(s)
                        + new pl.training.workshop.m6.s16_templatemethod.step1.HtmlSalesReport().render(s))
                .variant("step2", s -> new pl.training.workshop.m6.s16_templatemethod.step2.CsvSalesReport().render(s)
                        + new pl.training.workshop.m6.s16_templatemethod.step2.HtmlSalesReport().render(s))
                .expect("sprzedaż dnia (nieposortowana na wejściu)", List.of(
                        new Sale(LocalTime.of(18, 0), "Diuna", 3, Money.of("120.00")),
                        new Sale(LocalTime.of(10, 0), "Kraina Lodu", 2, Money.of("54.00")),
                        new Sale(LocalTime.of(20, 30), "Szybcy & Wsciekli; reedycja", 1, Money.of("25.00"))), """
                        godzina;film;bilety;kwota
                        10:00;Kraina Lodu;2;54.00
                        18:00;Diuna;3;120.00
                        20:30;"Szybcy & Wsciekli; reedycja";1;25.00
                        SUMA;;6;199.00
                        <table>
                        <tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
                        <tr><td>10:00</td><td>Kraina Lodu</td><td>2</td><td>54.00</td></tr>
                        <tr><td>18:00</td><td>Diuna</td><td>3</td><td>120.00</td></tr>
                        <tr><td>20:30</td><td>Szybcy &amp; Wsciekli; reedycja</td><td>1</td><td>25.00</td></tr>
                        <tr><td colspan="2">Suma</td><td>6</td><td>199.00</td></tr>
                        </table>
                        """)
                .expect("brak sprzedaży", List.of(), """
                        godzina;film;bilety;kwota
                        SUMA;;0;0.00
                        <table>
                        <tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>
                        <tr><td colspan="2">Suma</td><td>0</td><td>0.00</td></tr>
                        </table>
                        """)
                .tests();
    }
}
