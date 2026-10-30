package pl.training.workshop.exercises.e09_cashdrawer

import java.math.BigDecimal
import java.util.Random

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumingThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

import pl.training.workshop.exercises.e09_cashdrawer.step2.CardSale
import pl.training.workshop.exercises.e09_cashdrawer.step2.CashSale
import pl.training.workshop.exercises.e09_cashdrawer.step2.Deposit
import pl.training.workshop.exercises.e09_cashdrawer.step2.DiscountSplit
import pl.training.workshop.exercises.e09_cashdrawer.step2.DiscountSplits
import pl.training.workshop.exercises.e09_cashdrawer.step2.DrawerEntry
import pl.training.workshop.exercises.e09_cashdrawer.step2.DrawerEntryVisitor
import pl.training.workshop.exercises.e09_cashdrawer.step2.GroupDiscount
import pl.training.workshop.exercises.e09_cashdrawer.step2.Refund
import pl.training.workshop.shared.Money

/**
 * Kontrakt rozwiązania: każdy wariant podziału sumuje się co do grosza, nowa operacja na wpisach
 * to jedna klasa, warianty podziału są bezstanowe, a moment wyboru wariantu zmienia moment błędu.
 */
class E09SolutionTest {
    @Test
    fun everySplitSumsExactlyToTheDiscountOnRandomGroups() {
        val random = Random(2026)
        for (round in 0 until 200) {
            val tickets = randomTickets(random)
            val discount = sum(tickets).percent(10)
            for (mode in listOf("PROPORTIONAL", "LAST", "EQUAL")) {
                val shares = DiscountSplits.forMode(mode).split(discount, tickets)
                assertEquals(tickets.size, shares.size, "$mode liczba udzialow")
                assertEquals(discount, sum(shares), "$mode suma udzialow dla $tickets")
                assertTrue(shares.none { it < Money.ZERO }, "$mode ujemny udzial")
            }
        }
    }

    @Test
    fun newOperationIsOneVisitorWithoutTouchingTheEntries() {
        val paymentMethod = object : DrawerEntryVisitor<String> {
            override fun visitCashSale(sale: CashSale): String = "gotowka"

            override fun visitCardSale(sale: CardSale): String = "karta"

            override fun visitRefund(refund: Refund): String = "gotowka"

            override fun visitDeposit(deposit: Deposit): String = "gotowka"
        }
        val entries: List<DrawerEntry> = listOf(
            CashSale("T-1", Money.of("25.00")),
            CardSale("T-2", Money.of("32.00"), "4321"),
            Refund("T-1", Money.of("25.00"), "choroba"),
            Deposit(Money.of("200.00"), "pogotowie")
        )
        assertEquals(listOf("gotowka", "karta", "gotowka", "gotowka"), entries.map { it.accept(paymentMethod) })
    }

    @Test
    fun splitsAreSharedBecauseTheyAreStateless() {
        assertSame(DiscountSplits.forMode("EQUAL"), DiscountSplits.forMode("EQUAL"))
        assertSame(DiscountSplits.forMode("PROPORTIONAL"), DiscountSplits.forMode("PROPORTIONAL"))
    }

    @Test
    fun newSplitIsJustAnotherVariantWithoutTouchingTheRule() {
        val firstTicket = DiscountSplit { discount, tickets ->
            val shares = mutableListOf(discount)
            for (i in 1 until tickets.size) {
                shares += Money.ZERO
            }
            shares
        }
        val rule = GroupDiscount(firstTicket)
        // Różnica względem Javy: Optional<Allocation> to w Kotlinie Allocation?, więc
        // orElseThrow() i isPresent() zastępują `?: fail(...)` i assertNull.
        val allocation = rule.allocate(E09EquivalenceTest.tickets(10, "25.00")) ?: fail("grupa 10 biletow ma rabat")
        assertEquals(Money.of("25.00"), allocation.discount)
        assertEquals(Money.of("25.00"), allocation.shares.first())
        assertNull(rule.allocate(E09EquivalenceTest.tickets(9, "25.00")), "grupa za mala")
    }

    @Test
    fun choosingTheSplitUpFrontMovesTheUnknownModeErrorToSmallGroupsToo() {
        val smallGroup = DrawerDay(listOf(), E09EquivalenceTest.tickets(3, "25.00"), "RANDOM")
        assumingThat(startHasNoSplits()) {
            val before = pl.training.workshop.exercises.e09_cashdrawer.start.CashDrawerReport().render(smallGroup)
            assertTrue(before.endsWith("Rabat grupowy: brak (3 biletow)\n"), "start nie sprawdza trybu dla malej grupy")
        }

        val error = assertThrows<IllegalArgumentException> {
            pl.training.workshop.exercises.e09_cashdrawer.step2.CashDrawerReport().render(smallGroup)
        }
        assertEquals("unknown split mode: RANDOM", error.message)
    }

    private companion object {
        /** Pułapka start (tryb sprawdzany dopiero dla dużej grupy) - tylko dopóki w start nie ma wariantów podziału (po "warsztat.sh jump 2"). */
        fun startHasNoSplits(): Boolean = try {
            Class.forName("pl.training.workshop.exercises.e09_cashdrawer.start.DiscountSplit")
            false
        } catch (exception: ClassNotFoundException) {
            true
        }

        fun randomTickets(random: Random): List<Money> {
            val count = 10 + random.nextInt(30)
            val tickets = mutableListOf<Money>()
            for (i in 0 until count) {
                tickets += Money(BigDecimal.valueOf((1000 + random.nextInt(5000)).toLong(), 2))
            }
            return tickets
        }

        fun sum(amounts: List<Money>): Money {
            var total = Money.ZERO
            for (amount in amounts) {
                total += amount
            }
            return total
        }
    }
}
