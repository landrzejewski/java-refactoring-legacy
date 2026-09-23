package pl.training.workshop.m5.s12_bridgemethods.start;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StandardTicket;

/** Start: reguła cenowa bez wspólnego typu - rejestr znajduje ją refleksją po nazwie "apply". */
public final class StandardRule {
    public Money apply(StandardTicket ticket) {
        return ticket.basePrice();
    }
}
