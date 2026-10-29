package pl.training.workshop.exercises.e05_giftcard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e05_giftcard.step2.ActiveState;
import pl.training.workshop.exercises.e05_giftcard.step2.And;
import pl.training.workshop.exercises.e05_giftcard.step2.BlockedState;
import pl.training.workshop.exercises.e05_giftcard.step2.CardState;
import pl.training.workshop.exercises.e05_giftcard.step2.DayIn;
import pl.training.workshop.exercises.e05_giftcard.step2.EmptyState;
import pl.training.workshop.exercises.e05_giftcard.step2.ExpiredState;
import pl.training.workshop.exercises.e05_giftcard.step2.FormatIs;
import pl.training.workshop.exercises.e05_giftcard.step2.GiftCard;
import pl.training.workshop.exercises.e05_giftcard.step2.IsPremiere;
import pl.training.workshop.exercises.e05_giftcard.step2.IssuedState;
import pl.training.workshop.exercises.e05_giftcard.step2.Not;
import pl.training.workshop.exercises.e05_giftcard.step2.Or;
import pl.training.workshop.exercises.e05_giftcard.step2.VoucherRule;
import pl.training.workshop.exercises.e05_giftcard.step2.VoucherRuleParser;
import pl.training.workshop.exercises.e05_giftcard.step2.Vouchers;
import pl.training.workshop.shared.Money;

/** Kontrakt rozwiązania: tabela przejść w stanach, karta tylko deleguje, drzewo reguły składa się bez parsera. */
final class E05SolutionTest {
    @Test
    void transitionTableLivesInTheStatesNotInTheCard() {
        assertSame(ActiveState.INSTANCE, IssuedState.INSTANCE.activate());
        assertSame(BlockedState.INSTANCE, ActiveState.INSTANCE.block());
        assertSame(ExpiredState.INSTANCE, BlockedState.INSTANCE.expire());
        assertSame(ExpiredState.INSTANCE, EmptyState.INSTANCE.expire());

        var blocked = assertThrows(IllegalStateException.class, BlockedState.INSTANCE::activate);
        assertEquals("card is blocked", blocked.getMessage());
        var expired = assertThrows(IllegalStateException.class, ExpiredState.INSTANCE::expire);
        assertEquals("card has expired", expired.getMessage());
    }

    @Test
    void cardHoldsAStateObjectAndNoStatusText() {
        List<Class<?>> fieldTypes = Arrays.stream(GiftCard.class.getDeclaredFields())
                .map(Field::getType)
                .toList();
        assertEquals(List.of(CardState.class, Money.class), fieldTypes,
                "karta ma tylko stan i saldo - bez tekstowego statusu");
    }

    @Test
    void statesAreStatelessAndShared() {
        for (Class<?> state : List.of(IssuedState.class, ActiveState.class, EmptyState.class,
                BlockedState.class, ExpiredState.class)) {
            long instanceFields = Arrays.stream(state.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .count();
            assertEquals(0, instanceFields, state.getSimpleName() + " nie trzyma danych karty");
        }
        GiftCard first = new GiftCard(Money.of("10.00"));
        GiftCard second = new GiftCard(Money.of("20.00"));
        first.activate();
        second.activate();
        first.charge(Money.of("10.00"));
        assertEquals("EMPTY 0.00", first.status() + " " + first.balance());
        assertEquals("ACTIVE 20.00", second.status() + " " + second.balance(), "stan współdzielony, saldo nie");
    }

    @Test
    void parserBuildsTheSameTreeYouWouldBuildByHand() {
        VoucherRule byHand = new And(
                new And(new FormatIs("3D"), new DayIn(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))),
                new Not(new IsPremiere()));
        assertEquals(byHand, VoucherRuleParser.parse("format=3D AND day IN MON,TUE AND NOT premiere"));
    }

    @Test
    void newRuleKindIsComposedWithoutTouchingTheParserOrVouchers() {
        VoucherRule imaxOrPremiere = new Or(new FormatIs("IMAX"), new IsPremiere());
        ScreeningFacts premiere2d = new ScreeningFacts("2D", DayOfWeek.FRIDAY, true);
        ScreeningFacts plain2d = new ScreeningFacts("2D", DayOfWeek.FRIDAY, false);

        assertTrue(imaxOrPremiere.applies(premiere2d));
        assertFalse(imaxOrPremiere.applies(plain2d));
        assertEquals("format IMAX lub premiera", imaxOrPremiere.describe());

        var error = assertThrows(IllegalArgumentException.class,
                () -> Vouchers.applies("premiere OR format=IMAX", premiere2d));
        assertEquals("unknown rule term: premiere OR format=IMAX", error.getMessage(),
                "składnia tekstowa jeszcze nie zna OR - to świadoma, osobna decyzja");
    }
}
