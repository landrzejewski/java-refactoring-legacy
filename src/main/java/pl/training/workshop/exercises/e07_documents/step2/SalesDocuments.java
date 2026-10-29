package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Channel;
import pl.training.workshop.exercises.e07_documents.PrintOptions;
import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: punkt wejścia zna tylko walidację, wybór rodziny i składanie dokumentu.
 * Nie ma tu ani jednego switch-a po kanale ani obliczeń żadnej sekcji.
 */
public final class SalesDocuments {
    public String print(Sale sale, Channel channel, PrintOptions options) {
        if (sale == null) {
            throw new IllegalArgumentException("sale must not be null");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        if (sale.ticketPrice().compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("ticket price must not be negative");
        }
        if (sale.barPrice().compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("bar price must not be negative");
        }
        return new DocumentComposer(DocumentFactories.forChannel(channel)).compose(sale, options).render();
    }
}
