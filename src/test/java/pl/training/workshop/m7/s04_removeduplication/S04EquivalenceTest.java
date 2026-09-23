package pl.training.workshop.m7.s04_removeduplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności dla przypadków, w których kasa i sklep od zawsze się zgadzają.
 * Przypadek brzegowy zaokrąglenia jest w S04RoundingDecisionTest.
 */
final class S04EquivalenceTest {
    static final List<BigDecimal> TWO_3D = prices("32.00", 2);
    static final List<BigDecimal> TEN_2D = prices("25.00", 10);

    @TestFactory
    Stream<DynamicTest> boxOfficeNeverChanges() {
        return Scene.<List<BigDecimal>, String>variants()
                .variant("start", p -> new pl.training.workshop.m7.s04_removeduplication.start.BoxOffice().total(p).toPlainString())
                .variant("step1", p -> new pl.training.workshop.m7.s04_removeduplication.step1.BoxOffice().total(p).toPlainString())
                .variant("step2", p -> new pl.training.workshop.m7.s04_removeduplication.step2.BoxOffice().total(p).toPlainString())
                .variant("step3", p -> new pl.training.workshop.m7.s04_removeduplication.step3.BoxOffice().total(p).toPlainString())
                .expect("dwa bilety 3D", TWO_3D, "64.00")
                .expect("9 biletow - jeszcze bez rabatu", prices("25.00", 9), "225.00")
                .expect("10 biletow 2D", TEN_2D, "225.00")
                .expect("rabat z koncowka 5: 231.25 -> 208.12", S04RoundingDecisionTest.EDGE, "208.12")
                .expect("pusty koszyk", List.of(), "0.00")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> webShopAgreesOnOrdinaryBaskets() {
        return Scene.<List<BigDecimal>, String>variants()
                .variant("start", p -> new pl.training.workshop.m7.s04_removeduplication.start.WebShop().total(p).toPlainString())
                .variant("step1", p -> new pl.training.workshop.m7.s04_removeduplication.step1.WebShop().total(p).toPlainString())
                .variant("step2", p -> new pl.training.workshop.m7.s04_removeduplication.step2.WebShop().total(p).toPlainString())
                .variant("step3", p -> new pl.training.workshop.m7.s04_removeduplication.step3.WebShop().total(p).toPlainString())
                .expect("dwa bilety 3D + oplaty", TWO_3D, "68.00")
                .expect("10 biletow 2D + oplaty", TEN_2D, "245.00")
                .expect("pusty koszyk", List.of(), "0.00")
                .tests();
    }

    static List<BigDecimal> prices(String price, int count) {
        return new ArrayList<>(Collections.nCopies(count, new BigDecimal(price)));
    }
}
