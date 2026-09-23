package pl.training.workshop.m8.s04_shadowlimits.step3;

import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;
import pl.training.workshop.shared.Money;

/** Krok 3 (bez zmian): adapter portu na prawdziwą infrastrukturę. */
public final class RealEffects implements Effects {
    private final Infrastructure infra;

    public RealEffects(Infrastructure infra) {
        this.infra = infra;
    }

    @Override
    public void sendMail(String to, String text) {
        infra.sendMail(to, text);
    }

    @Override
    public void charge(String card, Money amount) {
        infra.charge(card, amount);
    }

    @Override
    public void save(String row) {
        infra.save(row);
    }
}
