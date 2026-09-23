package pl.training.workshop.m6.s10_implicittree.step3;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: render też na Composite. BarMenu tylko mapuje stary format na drzewo - format
 * trwały (zagnieżdżone listy) zmienimy osobnym krokiem, jeśli w ogóle.
 */
public final class BarMenu {
    public Money price(List<?> combo) {
        return MenuMapper.fromNested(combo).price();
    }

    public String render(List<?> combo) {
        StringBuilder text = new StringBuilder();
        MenuMapper.fromNested(combo).render(0, text);
        return text.toString();
    }
}
