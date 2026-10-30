package pl.training.workshop.exercises.e05_giftcard

import java.lang.reflect.Modifier
import java.time.DayOfWeek

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import pl.training.workshop.exercises.e05_giftcard.step2.ActiveState
import pl.training.workshop.exercises.e05_giftcard.step2.And
import pl.training.workshop.exercises.e05_giftcard.step2.BlockedState
import pl.training.workshop.exercises.e05_giftcard.step2.CardState
import pl.training.workshop.exercises.e05_giftcard.step2.DayIn
import pl.training.workshop.exercises.e05_giftcard.step2.EmptyState
import pl.training.workshop.exercises.e05_giftcard.step2.ExpiredState
import pl.training.workshop.exercises.e05_giftcard.step2.FormatIs
import pl.training.workshop.exercises.e05_giftcard.step2.GiftCard
import pl.training.workshop.exercises.e05_giftcard.step2.IsPremiere
import pl.training.workshop.exercises.e05_giftcard.step2.IssuedState
import pl.training.workshop.exercises.e05_giftcard.step2.Not
import pl.training.workshop.exercises.e05_giftcard.step2.Or
import pl.training.workshop.exercises.e05_giftcard.step2.VoucherRule
import pl.training.workshop.exercises.e05_giftcard.step2.VoucherRuleParser
import pl.training.workshop.exercises.e05_giftcard.step2.Vouchers
import pl.training.workshop.shared.Money

/** Kontrakt rozwiązania: tabela przejść w stanach, karta tylko deleguje, drzewo reguły składa się bez parsera. */
class E05SolutionTest {
    @Test
    fun transitionTableLivesInTheStatesNotInTheCard() {
        // Różnica względem Javy: stany to `object`, więc zamiast `ActiveState.INSTANCE` piszemy `ActiveState`.
        assertSame(ActiveState, IssuedState.activate())
        assertSame(BlockedState, ActiveState.block())
        assertSame(ExpiredState, BlockedState.expire())
        assertSame(ExpiredState, EmptyState.expire())

        val blocked = assertThrows<IllegalStateException> { BlockedState.activate() }
        assertEquals("card is blocked", blocked.message)
        val expired = assertThrows<IllegalStateException> { ExpiredState.expire() }
        assertEquals("card has expired", expired.message)
    }

    @Test
    fun cardHoldsAStateObjectAndNoStatusText() {
        val fieldTypes = GiftCard::class.java.declaredFields.map { it.type }
        assertEquals(
            listOf(CardState::class.java, Money::class.java), fieldTypes,
            "karta ma tylko stan i saldo - bez tekstowego statusu"
        )
    }

    @Test
    fun statesAreStatelessAndShared() {
        for (state in listOf(
            IssuedState::class.java, ActiveState::class.java, EmptyState::class.java,
            BlockedState::class.java, ExpiredState::class.java
        )) {
            val instanceFields = state.declaredFields.count { !Modifier.isStatic(it.modifiers) }
            assertEquals(0, instanceFields, "${state.simpleName} nie trzyma danych karty")
        }
        val first = GiftCard(Money.of("10.00"))
        val second = GiftCard(Money.of("20.00"))
        first.activate()
        second.activate()
        first.charge(Money.of("10.00"))
        assertEquals("EMPTY 0.00", first.status() + " " + first.balance())
        assertEquals("ACTIVE 20.00", second.status() + " " + second.balance(), "stan współdzielony, saldo nie")
    }

    @Test
    fun parserBuildsTheSameTreeYouWouldBuildByHand() {
        val byHand: VoucherRule = And(
            And(FormatIs("3D"), DayIn(listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))),
            Not(IsPremiere)
        )
        assertEquals(byHand, VoucherRuleParser.parse("format=3D AND day IN MON,TUE AND NOT premiere"))
    }

    @Test
    fun newRuleKindIsComposedWithoutTouchingTheParserOrVouchers() {
        val imaxOrPremiere: VoucherRule = Or(FormatIs("IMAX"), IsPremiere)
        val premiere2d = ScreeningFacts("2D", DayOfWeek.FRIDAY, true)
        val plain2d = ScreeningFacts("2D", DayOfWeek.FRIDAY, false)

        assertTrue(imaxOrPremiere.applies(premiere2d))
        assertFalse(imaxOrPremiere.applies(plain2d))
        assertEquals("format IMAX lub premiera", imaxOrPremiere.describe())

        val error = assertThrows<IllegalArgumentException> { Vouchers.applies("premiere OR format=IMAX", premiere2d) }
        assertEquals(
            "unknown rule term: premiere OR format=IMAX", error.message,
            "składnia tekstowa jeszcze nie zna OR - to świadoma, osobna decyzja"
        )
    }
}
