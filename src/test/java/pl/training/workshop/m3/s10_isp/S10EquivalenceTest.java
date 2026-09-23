package pl.training.workshop.m3.s10_isp;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Ten sam dzień pracy kasy, raportu i tablicy seansów w każdym wariancie. */
final class S10EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> clientsBehaveTheSame() {
        return Scene.<Integer, String>variants()
                .variant("start", dune -> {
                    var office = new pl.training.workshop.m3.s10_isp.start.InMemoryBackOffice();
                    var board = new pl.training.workshop.m3.s10_isp.start.ScheduleBoard(office);
                    var desk = new pl.training.workshop.m3.s10_isp.start.CashDesk(office);
                    var report = new pl.training.workshop.m3.s10_isp.start.RevenueReport(office);
                    board.plan("Diuna", LocalTime.of(20, 0));
                    board.plan("Amator", LocalTime.of(18, 0));
                    board.plan("Kraina Lodu", LocalTime.of(10, 0));
                    board.cancel("Kraina Lodu");
                    StringBuilder log = new StringBuilder();
                    for (int seat = 1; seat <= dune; seat++) {
                        log.append(desk.sell("Diuna", seat)).append("; ");
                    }
                    log.append(desk.sell("Amator", 7)).append("; ");
                    if (dune > 0) {
                        log.append(desk.refund("T-1")).append("; ");
                    }
                    return log + board.board() + " | " + report.summary("Diuna");
                })
                .variant("step1", dune -> {
                    var office = new pl.training.workshop.m3.s10_isp.step1.InMemoryBackOffice();
                    var board = new pl.training.workshop.m3.s10_isp.step1.ScheduleBoard(office);
                    var desk = new pl.training.workshop.m3.s10_isp.step1.CashDesk(office);
                    var report = new pl.training.workshop.m3.s10_isp.step1.RevenueReport(office);
                    board.plan("Diuna", LocalTime.of(20, 0));
                    board.plan("Amator", LocalTime.of(18, 0));
                    board.plan("Kraina Lodu", LocalTime.of(10, 0));
                    board.cancel("Kraina Lodu");
                    StringBuilder log = new StringBuilder();
                    for (int seat = 1; seat <= dune; seat++) {
                        log.append(desk.sell("Diuna", seat)).append("; ");
                    }
                    log.append(desk.sell("Amator", 7)).append("; ");
                    if (dune > 0) {
                        log.append(desk.refund("T-1")).append("; ");
                    }
                    return log + board.board() + " | " + report.summary("Diuna");
                })
                .variant("step2", dune -> {
                    var office = new pl.training.workshop.m3.s10_isp.step2.InMemoryBackOffice();
                    var board = new pl.training.workshop.m3.s10_isp.step2.ScheduleBoard(office);
                    var desk = new pl.training.workshop.m3.s10_isp.step2.CashDesk(office);
                    var report = new pl.training.workshop.m3.s10_isp.step2.RevenueReport(office);
                    board.plan("Diuna", LocalTime.of(20, 0));
                    board.plan("Amator", LocalTime.of(18, 0));
                    board.plan("Kraina Lodu", LocalTime.of(10, 0));
                    board.cancel("Kraina Lodu");
                    StringBuilder log = new StringBuilder();
                    for (int seat = 1; seat <= dune; seat++) {
                        log.append(desk.sell("Diuna", seat)).append("; ");
                    }
                    log.append(desk.sell("Amator", 7)).append("; ");
                    if (dune > 0) {
                        log.append(desk.refund("T-1")).append("; ");
                    }
                    return log + board.board() + " | " + report.summary("Diuna");
                })
                .expect("trzy bilety na Diune, jeden zwrocony", 3,
                        "bilet T-1: Diuna, miejsce 1; bilet T-2: Diuna, miejsce 2; bilet T-3: Diuna, miejsce 3; "
                                + "bilet T-4: Amator, miejsce 7; zwrot T-1; "
                                + "18:00 Amator, 20:00 Diuna | Diuna: 2 biletow, dzien: 75.00")
                .expect("bez biletow na Diune", 0,
                        "bilet T-1: Amator, miejsce 7; 18:00 Amator, 20:00 Diuna | Diuna: 0 biletow, dzien: 25.00")
                .tests();
    }
}
