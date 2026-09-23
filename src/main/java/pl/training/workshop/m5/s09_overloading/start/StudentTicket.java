package pl.training.workshop.m5.s09_overloading.start;

import pl.training.workshop.shared.Money;

/** Start: bilet studencki - zniżka zapisana nie tutaj, lecz w przeciążeniu PriceList.price(StudentTicket). */
public class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }
}
