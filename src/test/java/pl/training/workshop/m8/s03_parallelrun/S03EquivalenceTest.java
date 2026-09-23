package pl.training.workshop.m8.s03_parallelrun;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.m8.s03_parallelrun.step4.MigrationMode;
import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: klient dostaje tę samą cenę w każdym kroku i w każdym trybie migracji. */
final class S03EquivalenceTest {
    static final TicketQuery IMAX_NORMAL = new TicketQuery("IMAX", "NORMAL", LocalTime.of(20, 0), 5);
    static final TicketQuery IMAX_STUDENT_VIP = new TicketQuery("IMAX", "STUDENT", LocalTime.of(20, 0), 10);
    static final TicketQuery MORNING_3D_CHILD = new TicketQuery("3D", "CHILD", LocalTime.of(10, 30), 3);
    static final TicketQuery MORNING_3D_NORMAL_VIP = new TicketQuery("3D", "NORMAL", LocalTime.of(10, 30), 12);
    static final TicketQuery EVENING_2D_SENIOR = new TicketQuery("2D", "SENIOR", LocalTime.of(18, 0), 1);
    static final TicketQuery MORNING_2D_STUDENT = new TicketQuery("2D", "STUDENT", LocalTime.of(9, 0), 2);
    static final TicketQuery UNKNOWN_4DX = new TicketQuery("4DX", "NORMAL", LocalTime.of(20, 0), 1);

    @TestFactory
    Stream<DynamicTest> customerPaysTheSameInEveryStep() {
        return Scene.<TicketQuery, Money>variants()
                .variant("start", new pl.training.workshop.m8.s03_parallelrun.start.PriceService()::price)
                .variant("step1", new pl.training.workshop.m8.s03_parallelrun.step1.PriceService()::price)
                .variant("step2", new pl.training.workshop.m8.s03_parallelrun.step2.PriceService()::price)
                .variant("step3", new pl.training.workshop.m8.s03_parallelrun.step3.PriceService()::price)
                .variant("step4 SHADOW", new pl.training.workshop.m8.s03_parallelrun.step4.PriceService()::price)
                .variant("step4 CANDIDATE", new pl.training.workshop.m8.s03_parallelrun.step4.PriceService(
                        MigrationMode.CANDIDATE, new pl.training.workshop.m8.s03_parallelrun.step4.VerificationReport())::price)
                .variant("step4 LEGACY", new pl.training.workshop.m8.s03_parallelrun.step4.PriceService(
                        MigrationMode.LEGACY, new pl.training.workshop.m8.s03_parallelrun.step4.VerificationReport())::price)
                .expect("IMAX normalny wieczorem", IMAX_NORMAL, Money.of("40.00"))
                .expect("IMAX student na VIP", IMAX_STUDENT_VIP, Money.of("40.00"))
                .expect("3D dziecko rano", MORNING_3D_CHILD, Money.of("17.20"))
                .expect("3D normalny rano na VIP", MORNING_3D_NORMAL_VIP, Money.of("40.00"))
                .expect("2D senior wieczorem", EVENING_2D_SENIOR, Money.of("17.50"))
                .expect("2D student rano", MORNING_2D_STUDENT, Money.of("13.75"))
                .tests();
    }
}
