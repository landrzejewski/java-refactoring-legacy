package pl.training.workshop.m5.s12_bridgemethods.start;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.Ticket;

/**
 * Start: rejestr "frameworkowy" - dla każdej metody o nazwie apply bierze typ parametru jako klucz.
 * Działa, dopóki w klasie reguły jest dokładnie jedna metoda apply.
 */
public final class RuleRegistry {
    private final Map<Class<?>, Object> rules = new HashMap<>();

    public RuleRegistry(Object... rules) {
        for (Object rule : rules) {
            for (Method method : rule.getClass().getDeclaredMethods()) {
                if (method.getName().equals("apply")) {
                    this.rules.put(method.getParameterTypes()[0], rule);
                }
            }
        }
    }

    public Money price(Ticket ticket) {
        Object rule = rules.get(ticket.getClass());
        try {
            return (Money) rule.getClass().getMethod("apply", ticket.getClass()).invoke(rule, ticket);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("brak reguły dla " + ticket.getClass().getSimpleName(), e);
        }
    }

    public List<String> supportedTypes() {
        return rules.keySet().stream().map(Class::getSimpleName).sorted().toList();
    }

    public static RuleRegistry standard() {
        return new RuleRegistry(new StandardRule(), new StudentRule());
    }
}
