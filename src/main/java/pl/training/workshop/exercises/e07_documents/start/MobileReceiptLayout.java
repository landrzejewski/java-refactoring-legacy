package pl.training.workshop.exercises.e07_documents.start;

import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/** Start: paragon w aplikacji mobilnej. */
public final class MobileReceiptLayout implements ReceiptLayout {
    @Override
    public String receipt(Sale sale) {
        StringBuilder out = new StringBuilder("<receipt>");
        out.append("<li>Bilet ").append(sale.ticketPrice()).append("</li>");
        if (sale.barPrice().isGreaterThan(Money.ZERO)) {
            out.append("<li>Bar ").append(sale.barPrice()).append("</li>");
        }
        out.append("<li><b>Razem ").append(sale.ticketPrice().plus(sale.barPrice())).append("</b></li>");
        return out.append("</receipt>").toString();
    }

    @Override
    public String section(List<String> lines) {
        return lines.stream().collect(Collectors.joining("</p><p>", "<section><p>", "</p></section>"));
    }
}
