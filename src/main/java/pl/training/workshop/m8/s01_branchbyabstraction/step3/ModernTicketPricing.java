package pl.training.workshop.m8.s01_branchbyabstraction.step3;

import pl.training.workshop.m8.s01_branchbyabstraction.BookingRequest;
import pl.training.workshop.m8.s01_branchbyabstraction.Screening;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: nowa implementacja obok starej - Money, nazwane reguły, jawne kody.
 * Powstaje i jest testowana za tą samą abstrakcją, zanim ktokolwiek jej użyje.
 */
public final class ModernTicketPricing implements TicketPricing {
    private static final Money MORNING_DISCOUNT = Money.of("5.00");
    private static final Money VIP_SURCHARGE = Money.of("10.00");
    private static final Money GLASSES_3D = Money.of("3.00");
    private static final Money ONLINE_FEE = Money.of("2.00");
    private static final int GROUP_SIZE = 10;

    @Override
    public Money total(BookingRequest request) {
        Money tickets = Money.ZERO;
        for (int i = 0; i < request.seats().size(); i++) {
            tickets = tickets.plus(ticket(request, request.seats().get(i), request.types().get(i)));
        }
        int count = request.seats().size();
        if (count >= GROUP_SIZE) {
            tickets = tickets.minus(tickets.percent(10));
        }
        Money fees = request.web() ? ONLINE_FEE.times(count) : Money.ZERO;
        return tickets.plus(fees);
    }

    private Money ticket(BookingRequest request, String seat, String type) {
        Screening screening = request.screening();
        Money base = basePrice(screening.format());
        Money price = base.minus(base.percent(discountPercent(type)));
        if (screening.start().getHour() < 12) {
            price = price.minus(MORNING_DISCOUNT);
        }
        if (Integer.parseInt(seat.substring(1)) >= screening.vipFromRow()) {
            price = price.plus(VIP_SURCHARGE);
        }
        if (screening.format() == 2 && !request.ownGlasses()) {
            price = price.plus(GLASSES_3D);
        }
        return price;
    }

    private static Money basePrice(int format) {
        return switch (format) {
            case 1 -> Money.of("25.00");
            case 2 -> Money.of("32.00");
            case 3 -> Money.of("40.00");
            default -> throw new IllegalArgumentException("Nieznany format: " + format);
        };
    }

    private static int discountPercent(String type) {
        return switch (type) {
            case "S" -> 25;
            case "E" -> 30;
            case "C" -> 40;
            default -> 0;
        };
    }
}
