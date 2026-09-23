package pl.training.workshop.m6.s16_templatemethod.start;

import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/** Start: raport HTML - ten sam szkielet co CSV, napisany trochę inaczej. */
public final class HtmlSalesReport {
    public String render(List<Sale> sales) {
        StringBuilder html = new StringBuilder();
        html.append("<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n");
        Money sum = Money.ZERO;
        int count = 0;
        for (Sale sale : sales.stream().sorted(Comparator.comparing(Sale::time)).toList()) {
            String title = sale.title().replace("&", "&amp;").replace("<", "&lt;");
            html.append("<tr><td>").append(sale.time()).append("</td><td>").append(title)
                    .append("</td><td>").append(sale.tickets()).append("</td><td>").append(sale.amount())
                    .append("</td></tr>\n");
            sum = sum.plus(sale.amount());
            count += sale.tickets();
        }
        html.append("<tr><td colspan=\"2\">Suma</td><td>").append(count).append("</td><td>").append(sum)
                .append("</td></tr>\n</table>\n");
        return html.toString();
    }
}
