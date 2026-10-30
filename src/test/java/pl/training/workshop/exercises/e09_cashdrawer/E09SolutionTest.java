package pl.training.workshop.exercises.e09_cashdrawer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e09_cashdrawer.step2.CardSale;
import pl.training.workshop.exercises.e09_cashdrawer.step2.CashSale;
import pl.training.workshop.exercises.e09_cashdrawer.step2.Deposit;
import pl.training.workshop.exercises.e09_cashdrawer.step2.DiscountSplit;
import pl.training.workshop.exercises.e09_cashdrawer.step2.DiscountSplits;
import pl.training.workshop.exercises.e09_cashdrawer.step2.DrawerEntry;
import pl.training.workshop.exercises.e09_cashdrawer.step2.DrawerEntryVisitor;
import pl.training.workshop.exercises.e09_cashdrawer.step2.GroupDiscount;
import pl.training.workshop.exercises.e09_cashdrawer.step2.Refund;
import pl.training.workshop.shared.Money;

/**
 * Kontrakt rozwiązania: każdy wariant podziału sumuje się co do grosza, nowa operacja na wpisach
 * to jedna klasa, warianty podziału są bezstanowe, a moment wyboru wariantu zmienia moment błędu.
 */
final class E09SolutionTest {
    @Test
    void everySplitSumsExactlyToTheDiscountOnRandomGroups() {
        Random random = new Random(2026);
        for (int round = 0; round < 200; round++) {
            List<Money> tickets = randomTickets(random);
            Money discount = sum(tickets).percent(10);
            for (String mode : List.of("PROPORTIONAL", "LAST", "EQUAL")) {
                List<Money> shares = DiscountSplits.forMode(mode).split(discount, tickets);
                assertEquals(tickets.size(), shares.size(), mode + " liczba udzialow");
                assertEquals(discount, sum(shares), mode + " suma udzialow dla " + tickets);
                assertTrue(shares.stream().noneMatch(share -> share.compareTo(Money.ZERO) < 0), mode + " ujemny udzial");
            }
        }
    }

    @Test
    void newOperationIsOneVisitorWithoutTouchingTheEntries() {
        DrawerEntryVisitor<String> paymentMethod = new DrawerEntryVisitor<>() {
            @Override
            public String visitCashSale(CashSale sale) {
                return "gotowka";
            }

            @Override
            public String visitCardSale(CardSale sale) {
                return "karta";
            }

            @Override
            public String visitRefund(Refund refund) {
                return "gotowka";
            }

            @Override
            public String visitDeposit(Deposit deposit) {
                return "gotowka";
            }
        };
        List<DrawerEntry> entries = List.of(new CashSale("T-1", Money.of("25.00")),
                new CardSale("T-2", Money.of("32.00"), "4321"), new Refund("T-1", Money.of("25.00"), "choroba"),
                new Deposit(Money.of("200.00"), "pogotowie"));
        assertEquals(List.of("gotowka", "karta", "gotowka", "gotowka"),
                entries.stream().map(entry -> entry.accept(paymentMethod)).toList());
    }

    @Test
    void splitsAreSharedBecauseTheyAreStateless() {
        assertSame(DiscountSplits.forMode("EQUAL"), DiscountSplits.forMode("EQUAL"));
        assertSame(DiscountSplits.forMode("PROPORTIONAL"), DiscountSplits.forMode("PROPORTIONAL"));
    }

    @Test
    void newSplitIsJustAnotherVariantWithoutTouchingTheRule() {
        DiscountSplit firstTicket = (discount, tickets) -> {
            List<Money> shares = new ArrayList<>(List.of(discount));
            for (int i = 1; i < tickets.size(); i++) {
                shares.add(Money.ZERO);
            }
            return shares;
        };
        GroupDiscount rule = new GroupDiscount(firstTicket);
        GroupDiscount.Allocation allocation = rule.allocate(E09EquivalenceTest.tickets(10, "25.00")).orElseThrow();
        assertEquals(Money.of("25.00"), allocation.discount());
        assertEquals(Money.of("25.00"), allocation.shares().getFirst());
        assertFalse(rule.allocate(E09EquivalenceTest.tickets(9, "25.00")).isPresent(), "grupa za mala");
    }

    @Test
    void choosingTheSplitUpFrontMovesTheUnknownModeErrorToSmallGroupsToo() {
        DrawerDay smallGroup = new DrawerDay(List.of(), E09EquivalenceTest.tickets(3, "25.00"), "RANDOM");
        assumingThat(startHasNoSplits(), () -> {
            String before = new pl.training.workshop.exercises.e09_cashdrawer.start.CashDrawerReport().render(smallGroup);
            assertTrue(before.endsWith("Rabat grupowy: brak (3 biletow)\n"), "start nie sprawdza trybu dla malej grupy");
        });

        var error = assertThrows(IllegalArgumentException.class,
                () -> new pl.training.workshop.exercises.e09_cashdrawer.step2.CashDrawerReport().render(smallGroup));
        assertEquals("unknown split mode: RANDOM", error.getMessage());
    }

    /** Pułapka start (tryb sprawdzany dopiero dla dużej grupy) - tylko dopóki w start nie ma wariantów podziału (po "warsztat.sh jump 2"). */
    private static boolean startHasNoSplits() {
        try {
            Class.forName("pl.training.workshop.exercises.e09_cashdrawer.start.DiscountSplit");
            return false;
        } catch (ClassNotFoundException exception) {
            return true;
        }
    }

    private static List<Money> randomTickets(Random random) {
        int count = 10 + random.nextInt(30);
        List<Money> tickets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tickets.add(new Money(BigDecimal.valueOf(1000 + random.nextInt(5000), 2)));
        }
        return tickets;
    }

    private static Money sum(List<Money> amounts) {
        Money total = Money.ZERO;
        for (Money amount : amounts) {
            total = total.plus(amount);
        }
        return total;
    }
}
