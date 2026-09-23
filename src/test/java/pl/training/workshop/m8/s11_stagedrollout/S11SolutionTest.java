package pl.training.workshop.m8.s11_stagedrollout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m8.s11_stagedrollout.step3.RolloutPolicy;

/** Test polityki wdrożenia etapowego z kroku 3 (procent, wyjątki, wyłącznik). */
final class S11SolutionTest {
    private static final List<String> CUSTOMERS =
            IntStream.range(0, 1000).mapToObj(i -> "klient" + i + "@kino.pl").toList();

    @Test
    void sameCustomerAlwaysGetsTheSamePath() {
        RolloutPolicy policy = new RolloutPolicy(30, Set.of(), false);
        for (String email : CUSTOMERS) {
            assertEquals(policy.allows(email), new RolloutPolicy(30, Set.of(), false).allows(email), email);
        }
        assertEquals(RolloutPolicy.bucket("ola@kino.pl"), RolloutPolicy.bucket("  Ola@Kino.PL "));
    }

    @Test
    void percentOfCustomersIsRoughlyRespected() {
        long included = CUSTOMERS.stream().filter(new RolloutPolicy(20, Set.of(), false)::allows).count();
        assertTrue(included > 150 && included < 250, "20% z 1000 klientów, było: " + included);
        assertEquals(0, CUSTOMERS.stream().filter(new RolloutPolicy(0, Set.of(), false)::allows).count());
        assertEquals(1000, CUSTOMERS.stream().filter(new RolloutPolicy(100, Set.of(), false)::allows).count());
    }

    @Test
    void increasingPercentNeverRemovesAnyone() {
        RolloutPolicy ten = new RolloutPolicy(10, Set.of(), false);
        RolloutPolicy twenty = new RolloutPolicy(20, Set.of(), false);
        assertTrue(CUSTOMERS.stream().filter(ten::allows).allMatch(twenty::allows));
    }

    @Test
    void allowListWorksAtZeroPercent() {
        RolloutPolicy policy = new RolloutPolicy(0, Set.of("anna@kino.pl"), false);
        assertTrue(policy.allows("Anna@Kino.pl"));
        assertFalse(policy.allows("ola@kino.pl"));
    }

    @Test
    void killSwitchOverridesPercentAndAllowList() {
        RolloutPolicy killed = new RolloutPolicy(100, Set.of("anna@kino.pl"), true);
        assertFalse(killed.allows("anna@kino.pl"));
        assertTrue(CUSTOMERS.stream().noneMatch(killed::allows));
    }

    @Test
    void invalidPercentIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RolloutPolicy(101, Set.of(), false));
        assertThrows(IllegalArgumentException.class, () -> new RolloutPolicy(-1, Set.of(), false));
    }

    @Test
    void startComparesEmailLiterally() {
        var router = new pl.training.workshop.m8.s11_stagedrollout.start.CheckoutRouter();
        assertFalse(router.useNewCheckout("Anna@kino.pl"), "stary if porównuje e-mail dosłownie");
    }
}
