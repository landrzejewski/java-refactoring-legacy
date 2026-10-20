package pl.training.workshop.m5.s30_facade;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Ścieżki, na których obie kopie orkiestracji zgadzają się ze sobą: zakup w obu kanałach,
 * konflikt miejsc między kanałami, odmowa w sklepie, nieznany typ biletu. Odmowa w kasie
 * celowo jest poza tą tabelą - tam start ma błąd, a krok 2 go naprawia (S30BehaviourChangeTest).
 */
final class S30EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepSellsTheSameOnTheCorrectPaths() {
        return Scene.<List<String>, String>variants()
                .variant("start", session(() -> {
                    var cinema = new pl.training.workshop.m5.s30_facade.start.Cinema();
                    return channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell, cinema::state);
                }))
                .variant("step1", session(() -> {
                    var cinema = new pl.training.workshop.m5.s30_facade.step1.Cinema();
                    return channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell, cinema::state);
                }))
                .variant("step2", session(() -> {
                    var cinema = new pl.training.workshop.m5.s30_facade.step2.Cinema();
                    return channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell, cinema::state);
                }))
                .variant("step3", session(() -> {
                    var cinema = new pl.training.workshop.m5.s30_facade.step3.Cinema();
                    return channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell, cinema::state);
                }))
                .expect("sklep: zakup z opłatą rezerwacyjną", List.of(
                        "online S1 A1,A2 N 4111 anna@example.com", "state"), """
                        OK 54.00
                        miejsca={S1=[A1, A2]} platnosci=[CHARGED 4111 54.00] \
                        maile=[anna@example.com: Bilety S1 A1,A2 = 54.00]""")
                .expect("kasa: zakup bez opłaty", List.of("desk S1 B1 S 4111 jan@example.com", "state"), """
                        OK 18.75
                        miejsca={S1=[B1]} platnosci=[CHARGED 4111 18.75] maile=[jan@example.com: Bilety S1 B1 = 18.75]""")
                .expect("miejsce zajęte przez drugi kanał: nic się nie zmienia", List.of(
                        "online S1 A1 N 4111 anna@example.com", "desk S1 A2,A1 C 4111 jan@example.com", "state"), """
                        OK 27.00
                        ERROR seat taken A1
                        miejsca={S1=[A1]} platnosci=[CHARGED 4111 27.00] maile=[anna@example.com: Bilety S1 A1 = 27.00]""")
                .expect("sklep: odmowa płatności zwalnia miejsca", List.of(
                        "online S1 A1,A2 E 40000 anna@example.com", "state", "desk S1 A1 N 4111 jan@example.com"), """
                        ERROR payment declined
                        miejsca={S1=[]} platnosci=[DECLINED 40000 39.00] maile=[]
                        OK 25.00""")
                .expect("nieznany typ biletu: przed rezerwacją", List.of(
                        "online S1 A1 X 4111 anna@example.com", "desk S1 A1 X 4111 jan@example.com", "state"), """
                        ERROR unknown ticket type: X
                        ERROR unknown ticket type: X
                        miejsca={} platnosci=[] maile=[]""")
                .expect("różne seanse się nie blokują", List.of(
                        "online S2 A1 C 4111 anna@example.com", "desk S1 A1 N 4111 jan@example.com", "state"), """
                        OK 17.00
                        OK 25.00
                        miejsca={S1=[A1], S2=[A1]} platnosci=[CHARGED 4111 17.00, CHARGED 4111 25.00] \
                        maile=[anna@example.com: Bilety S2 A1 = 17.00, jan@example.com: Bilety S1 A1 = 25.00]""")
                .tests();
    }

    /** Wiersz: "online|desk SEANS MIEJSCA,PO,PRZECINKU TYP KARTA EMAIL" albo "state". */
    static Function<String, String> channels(Function<TicketOrder, String> online, Function<TicketOrder, String> desk,
                                             Supplier<String> state) {
        return line -> {
            String[] parts = line.split(" ");
            if (parts[0].equals("state")) {
                return state.get();
            }
            TicketOrder order = new TicketOrder(parts[1], Arrays.asList(parts[2].split(",")), parts[3], parts[4], parts[5]);
            try {
                return (parts[0].equals("online") ? online : desk).apply(order);
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }

    private static Function<List<String>, String> session(Supplier<Function<String, String>> console) {
        return lines -> {
            Function<String, String> handle = console.get();
            return String.join("\n", lines.stream().map(handle).toList());
        };
    }
}
