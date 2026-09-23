package pl.training.workshop.m8.s04_shadowlimits.step3;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: wynik czystego obliczenia - odpowiedź dla klienta i lista efektów DO wykonania.
 * Efekty to dane: można je porównać, zalogować albo wykonać, ale plan sam niczego nie robi.
 */
public record BookingPlan(String result, List<Effect> effects) {
    public sealed interface Effect {
        String describe();

        void applyTo(Effects port);
    }

    public record Charge(String card, Money amount) implements Effect {
        public String describe() {
            return "CHARGE " + card + ": " + amount;
        }

        public void applyTo(Effects port) {
            port.charge(card, amount);
        }
    }

    public record Save(String row) implements Effect {
        public String describe() {
            return "SAVE " + row;
        }

        public void applyTo(Effects port) {
            port.save(row);
        }
    }

    public record SendMail(String to, String text) implements Effect {
        public String describe() {
            return "MAIL " + to + ": " + text;
        }

        public void applyTo(Effects port) {
            port.sendMail(to, text);
        }
    }
}
