package pl.training.workshop.m5.s12_bridgemethods.step2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.Ticket;

/** Krok 2: rejestr bez refleksji - typ z kontraktu, rzutowanie przez Class.cast w jednym miejscu. */
public final class RuleRegistry {
    private final Map<Class<?>, PriceRule<?>> rules = new HashMap<>();

    public RuleRegistry(PriceRule<?>... rules) {
        for (PriceRule<?> rule : rules) {
            this.rules.put(rule.ticketType(), rule);
        }
    }

    public Money price(Ticket ticket) {
        PriceRule<?> rule = rules.get(ticket.getClass());
        if (rule == null) {
            throw new IllegalStateException("brak reguły dla " + ticket.getClass().getSimpleName());
        }
        return apply(rule, ticket);
    }

    private static <T extends Ticket> Money apply(PriceRule<T> rule, Ticket ticket) {
        return rule.apply(rule.ticketType().cast(ticket));
    }

    public List<String> supportedTypes() {
        return rules.keySet().stream().map(Class::getSimpleName).sorted().toList();
    }

    public static RuleRegistry standard() {
        return new RuleRegistry(new StandardRule(), new StudentRule());
    }
}
