package pl.training.workshop.exercises.e05_giftcard;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.TUESDAY;

import java.time.DayOfWeek;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Dwie tabele decyzji: pełna tabela przejść karty (każdy status x każda operacja, walidacja przed
 * statusem) oraz reguły voucherów (każde pojęcie, negacja, błędy składni, moment błędu).
 */
final class E05EquivalenceTest {
    private static final String RULE = "format=3D AND day IN MON,TUE AND NOT premiere";

    record CardScript(String initialBalance, List<String> operations) {
    }

    record VoucherCase(String rule, ScreeningFacts facts) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepRunsTheCardTheSame() {
        return Scene.<CardScript, String>variants()
                .variant("start", s -> run(() -> pl.training.workshop.exercises.e05_giftcard.start.GiftCards
                        .simulate(Money.of(s.initialBalance()), s.operations())))
                .variant("step1", s -> run(() -> pl.training.workshop.exercises.e05_giftcard.step1.GiftCards
                        .simulate(Money.of(s.initialBalance()), s.operations())))
                .variant("step2", s -> run(() -> pl.training.workshop.exercises.e05_giftcard.step2.GiftCards
                        .simulate(Money.of(s.initialBalance()), s.operations())))
                .expect("wydana, bez operacji", script("100.00"), "ISSUED 100.00")
                .expect("aktywacja i dwa obciążenia", script("100.00", "activate", "charge 25.00", "charge 32.00"),
                        "ACTIVE 43.00")
                .expect("wyczerpanie do zera", script("50.00", "activate", "charge 50.00"), "EMPTY 0.00")
                .expect("obciążenie przed aktywacją", script("50.00", "charge 10.00"), "ERROR: card not activated")
                .expect("druga aktywacja", script("50.00", "activate", "activate"), "ERROR: card already active")
                .expect("brak środków", script("30.00", "activate", "charge 40.00"), "ERROR: insufficient balance")
                .expect("kwota ujemna", script("30.00", "activate", "charge -1.00"), "ERROR: amount must be positive")
                .expect("kwota ujemna na wygasłej - walidacja przed statusem", script("30.00", "expire", "charge -1.00"),
                        "ERROR: amount must be positive")
                .expect("blokada wydanej", script("30.00", "block"), "BLOCKED 30.00")
                .expect("blokada aktywnej, potem obciążenie", script("30.00", "activate", "block", "charge 5.00"),
                        "ERROR: card is blocked")
                .expect("aktywacja zablokowanej", script("30.00", "block", "activate"), "ERROR: card is blocked")
                .expect("podwójna blokada", script("30.00", "block", "block"), "ERROR: card is blocked")
                .expect("blokada pustej", script("30.00", "activate", "charge 30.00", "block"), "ERROR: card is empty")
                .expect("aktywacja pustej", script("30.00", "activate", "charge 30.00", "activate"),
                        "ERROR: card is empty")
                .expect("wygaśnięcie zablokowanej", script("30.00", "block", "expire"), "EXPIRED 30.00")
                .expect("wygaśnięcie pustej", script("30.00", "activate", "charge 30.00", "expire"), "EXPIRED 0.00")
                .expect("wygaśnięcie wydanej", script("30.00", "expire"), "EXPIRED 30.00")
                .expect("aktywacja wygasłej", script("30.00", "expire", "activate"), "ERROR: card has expired")
                .expect("blokada wygasłej", script("30.00", "expire", "block"), "ERROR: card has expired")
                .expect("podwójne wygaśnięcie", script("30.00", "expire", "expire"), "ERROR: card has expired")
                .expect("saldo początkowe zero", script("0.00"), "ERROR: initial balance must be positive")
                .expect("nieznana operacja", script("30.00", "activate", "refund"), "ERROR: unknown operation: refund")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> everyStepReadsVoucherRulesTheSame() {
        return Scene.<VoucherCase, String>variants()
                .variant("start", c -> run(() -> pl.training.workshop.exercises.e05_giftcard.start.Vouchers
                        .applies(c.rule(), c.facts()) + " / "
                        + pl.training.workshop.exercises.e05_giftcard.start.Vouchers.describe(c.rule())))
                .variant("step1", c -> run(() -> pl.training.workshop.exercises.e05_giftcard.step1.Vouchers
                        .applies(c.rule(), c.facts()) + " / "
                        + pl.training.workshop.exercises.e05_giftcard.step1.Vouchers.describe(c.rule())))
                .variant("step2", c -> run(() -> pl.training.workshop.exercises.e05_giftcard.step2.Vouchers
                        .applies(c.rule(), c.facts()) + " / "
                        + pl.training.workshop.exercises.e05_giftcard.step2.Vouchers.describe(c.rule())))
                .expect("pełna reguła, Kraina Lodu 3D w poniedziałek", rule(RULE, "3D", MONDAY, false),
                        "true / format 3D oraz dzień: MON, TUE oraz nie premiera")
                .expect("pełna reguła, zły dzień", rule(RULE, "3D", FRIDAY, false),
                        "false / format 3D oraz dzień: MON, TUE oraz nie premiera")
                .expect("pełna reguła, premiera", rule(RULE, "3D", TUESDAY, true),
                        "false / format 3D oraz dzień: MON, TUE oraz nie premiera")
                .expect("pełna reguła, zły format", rule(RULE, "IMAX", MONDAY, false),
                        "false / format 3D oraz dzień: MON, TUE oraz nie premiera")
                .expect("samo premiere", rule("premiere", "IMAX", FRIDAY, true), "true / premiera")
                .expect("negacja formatu", rule("NOT format=IMAX", "IMAX", FRIDAY, false), "false / nie format IMAX")
                .expect("weekend", rule("day IN SAT,SUN", "2D", SUNDAY, false), "true / dzień: SAT, SUN")
                .expect("dwa pojęcia, drugie fałszywe", rule("format=2D AND premiere", "2D", MONDAY, false),
                        "false / format 2D oraz premiera")
                .expect("odstępy wokół przecinków", rule("day IN MON , TUE", "2D", TUESDAY, false),
                        "true / dzień: MON, TUE")
                .expect("nieznane pojęcie", rule("hour<12", "2D", MONDAY, false), "ERROR: unknown rule term: hour<12")
                .expect("nieznane pojęcie po fałszywym - błąd mimo fałszu",
                        rule("format=IMAX AND hour<12", "2D", MONDAY, false), "ERROR: unknown rule term: hour<12")
                .expect("podwójne NOT nie jest obsługiwane", rule("NOT NOT premiere", "2D", MONDAY, true),
                        "ERROR: unknown rule term: NOT premiere")
                .expect("nieznany dzień", rule("day IN MON,XYZ", "2D", MONDAY, false), "ERROR: unknown day: XYZ")
                .expect("pusta reguła", rule("   ", "2D", MONDAY, false), "ERROR: rule must not be empty")
                .tests();
    }

    private static CardScript script(String initialBalance, String... operations) {
        return new CardScript(initialBalance, List.of(operations));
    }

    private static VoucherCase rule(String rule, String format, DayOfWeek day, boolean premiere) {
        return new VoucherCase(rule, new ScreeningFacts(format, day, premiere));
    }

    static String run(Supplier<String> action) {
        try {
            return action.get();
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
