package pl.training.workshop.m5.s13_sealed.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: nowy wariant ChildTicket dopisany do permits - kompilacja PriceCalculator od razu się wywraca. */
public sealed interface Ticket permits StandardTicket, StudentTicket, SeniorTicket, ChildTicket {
    Money basePrice();
}
