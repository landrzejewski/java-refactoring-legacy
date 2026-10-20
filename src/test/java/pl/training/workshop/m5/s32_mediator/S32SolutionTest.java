package pl.training.workshop.m5.s32_mediator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s32_mediator.step3.Basket;
import pl.training.workshop.m5.s32_mediator.step3.PayButton;
import pl.training.workshop.m5.s32_mediator.step3.PriceSummary;
import pl.training.workshop.m5.s32_mediator.step3.SeatMap;
import pl.training.workshop.m5.s32_mediator.step3.SelectionMediator;

/** Krok 3: komponenty znają tylko mediatora i dają się testować z atrapą mediatora. */
final class S32SolutionTest {
    private static final List<Class<?>> COMPONENTS =
            List.of(SeatMap.class, Basket.class, PriceSummary.class, PayButton.class);

    @Test
    void everyComponentKnowsOnlyTheMediator() {
        for (Class<?> component : COMPONENTS) {
            List<Class<?>> collaborators = Arrays.stream(component.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .<Class<?>>map(Field::getType)
                    .filter(type -> type.getPackageName().startsWith("pl.training.workshop.m5.s32_mediator"))
                    .toList();
            assertEquals(List.of(SelectionMediator.class), collaborators, component.getSimpleName());
        }
    }

    @Test
    void componentReportsWhatHappenedWithoutKnowingWhoReacts() {
        RecordingMediator mediator = new RecordingMediator();
        SeatMap map = new SeatMap(mediator);
        map.click("A1");
        map.click("B2");
        map.click("A1");
        new PriceSummary(mediator).applyVoucher("KINO10");
        new PayButton(mediator).press();
        assertEquals(List.of("seatToggled A1 true", "seatToggled A1 false", "voucherApplied"), mediator.events,
                "sprzedane B2 nie generuje zdarzenia; nieaktywny przycisk nie prosi o płatność");
    }

    private static final class RecordingMediator implements SelectionMediator {
        private final List<String> events = new ArrayList<>();

        @Override
        public void seatToggled(String seat, boolean selected) {
            events.add("seatToggled " + seat + " " + selected);
        }

        @Override
        public void seatRemoved(String seat) {
            events.add("seatRemoved " + seat);
        }

        @Override
        public void basketCleared() {
            events.add("basketCleared");
        }

        @Override
        public void voucherApplied() {
            events.add("voucherApplied");
        }

        @Override
        public String payRequested() {
            events.add("payRequested");
            return "ok";
        }
    }
}
