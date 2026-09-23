package pl.training.workshop.m6.s20_decisionmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s20_decisionmap.step2.DayPolicies;
import pl.training.workshop.m6.s20_decisionmap.step2.DayPolicy;
import pl.training.workshop.m6.s20_decisionmap.step2.ShowPricing;
import pl.training.workshop.shared.Money;

/** Kryterium wyboru: która zmiana jest tania w danej strukturze. */
final class S20SolutionTest {
    @Test
    void pathAMakesANewDayCampaignCheap() {
        DayPolicy seniorWednesday = base -> base.minus(Money.of("5.00"));
        ShowPricing pricing = new ShowPricing(day -> day == DayOfWeek.WEDNESDAY
                ? seniorWednesday : DayPolicies.standard(day));
        assertEquals(Money.of("35.00"), pricing.price(DayOfWeek.WEDNESDAY, "IMAX"));
        assertEquals(Money.of("28.00"), pricing.price(DayOfWeek.TUESDAY, "IMAX"));
    }

    @Test
    void pathBKeepsEverythingAboutAFormatInOnePlace() {
        var imax = pl.training.workshop.m6.s20_decisionmap.step3.Format.of("IMAX");
        assertEquals(Money.of("42.00"), imax.priceOn(DayOfWeek.SATURDAY));
    }
}
