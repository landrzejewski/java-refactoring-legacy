package pl.training.workshop.m6.s10_implicittree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s10_implicittree.step1.MenuMapper;

/**
 * Test różnicowy: stara reprezentacja (start) kontra nowa na 500 losowych drzewach
 * (stałe ziarno - wynik powtarzalny). Uzupełnia, a nie zastępuje niezależne oczekiwania.
 */
final class S10DifferentialTest {
    private final Function<List<?>, String> legacy = S10EquivalenceTest.safe(
            new pl.training.workshop.m6.s10_implicittree.start.BarMenu()::price,
            new pl.training.workshop.m6.s10_implicittree.start.BarMenu()::render);

    @Test
    void compositeFromMapperMatchesLegacyAlready() {
        Function<List<?>, String> composite = S10EquivalenceTest.safe(
                definition -> MenuMapper.fromNested(definition).price(),
                definition -> {
                    StringBuilder text = new StringBuilder();
                    MenuMapper.fromNested(definition).render(0, text);
                    return text.toString();
                });
        compare(composite);
    }

    @Test
    void finalBarMenuMatchesLegacy() {
        compare(S10EquivalenceTest.safe(
                new pl.training.workshop.m6.s10_implicittree.step3.BarMenu()::price,
                new pl.training.workshop.m6.s10_implicittree.step3.BarMenu()::render));
    }

    private void compare(Function<List<?>, String> candidate) {
        Random random = new Random(42);
        for (int i = 0; i < 500; i++) {
            List<Object> definition = randomCombo(random, 0);
            assertEquals(legacy.apply(definition), candidate.apply(definition), "drzewo: " + definition);
        }
    }

    private static List<Object> randomCombo(Random random, int depth) {
        List<Object> combo = new ArrayList<>();
        combo.add("Zestaw " + random.nextInt(100));
        int size = random.nextInt(4);
        for (int i = 0; i < size; i++) {
            int kind = random.nextInt(20);
            if (kind == 0) {
                combo.add(7);
            } else if (kind == 1) {
                combo.add("Bez ceny");
            } else if (kind < 6 && depth < 3) {
                combo.add(randomCombo(random, depth + 1));
            } else {
                combo.add("P" + random.nextInt(50) + "=" + random.nextInt(30) + "." + random.nextInt(10) + "0");
            }
        }
        return combo;
    }
}
