package pl.training.workshop.m6.s19_visitor;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Paragon (linie, suma, VAT) identyczny: instanceof, Visitor i switch po sealed. */
final class S19EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPrintsTheSameReceipt() {
        return Scene.<String, String>variants()
                .variant("start", code -> new pl.training.workshop.m6.s19_visitor.start.ReceiptPrinter()
                        .print(new pl.training.workshop.m6.s19_visitor.start.SampleOrders().find(code)))
                .variant("step1", code -> new pl.training.workshop.m6.s19_visitor.step1.ReceiptPrinter()
                        .print(new pl.training.workshop.m6.s19_visitor.step1.SampleOrders().find(code)))
                .variant("step2", code -> new pl.training.workshop.m6.s19_visitor.step2.ReceiptPrinter()
                        .print(new pl.training.workshop.m6.s19_visitor.step2.SampleOrders().find(code)))
                .variant("step3", code -> new pl.training.workshop.m6.s19_visitor.step3.ReceiptPrinter()
                        .print(new pl.training.workshop.m6.s19_visitor.step3.SampleOrders().find(code)))
                .expect("wieczór: bilet, bar, voucher", "evening", """
                        Bilet Diuna IMAX 40.00
                        Popcorn L 18.00
                        Cola 9.00
                        Voucher KINO20 -20.00
                        Razem: 47.00
                        VAT: 8.01
                        """)
                .expect("voucher prawie pokrywa bilet", "voucher", """
                        Bilet Amator 2D 25.00
                        Voucher KINO20 -20.00
                        Razem: 5.00
                        VAT: 1.85
                        """)
                .expect("puste zamówienie", "empty", "Razem: 0.00\nVAT: 0.00\n")
                .tests();
    }
}
