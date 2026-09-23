package pl.training.workshop.m5.s15_compatibility.step3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import pl.training.workshop.m5.s15_compatibility.Column;

/**
 * Krok 3: bez zmian.
 */
public final class TicketExporter {
    public String export(Ticket ticket) {
        StringBuilder row = new StringBuilder(ticket.title());
        Method[] candidates = ticket.getClass().getMethods();
        Arrays.stream(candidates)
                .filter(method -> method.isAnnotationPresent(Column.class))
                .sorted(Comparator.comparing((Method m) -> m.getAnnotation(Column.class).value()))
                .forEach(method -> row.append(';')
                        .append(method.getAnnotation(Column.class).value())
                        .append('=')
                        .append(read(method, ticket)));
        return row.toString();
    }

    private static Object read(Method method, Ticket ticket) {
        try {
            return method.invoke(ticket);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
