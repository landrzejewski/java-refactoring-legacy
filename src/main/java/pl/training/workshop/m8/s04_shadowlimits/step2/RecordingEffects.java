package pl.training.workshop.m8.s04_shadowlimits.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: przechwycenie efektów - nagrywa zamiary kandydata w formacie dziennika infrastruktury,
 * niczego nie wysyła, nie obciąża i nie zapisuje.
 */
public final class RecordingEffects implements Effects {
    private final List<String> recorded = new ArrayList<>();

    @Override
    public void sendMail(String to, String text) {
        recorded.add("MAIL " + to + ": " + text);
    }

    @Override
    public void charge(String card, Money amount) {
        recorded.add("CHARGE " + card + ": " + amount);
    }

    @Override
    public void save(String row) {
        recorded.add("SAVE " + row);
    }

    public List<String> recorded() {
        return List.copyOf(recorded);
    }
}
