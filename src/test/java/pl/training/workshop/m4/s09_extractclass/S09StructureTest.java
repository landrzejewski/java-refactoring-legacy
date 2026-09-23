package pl.training.workshop.m4.s09_extractclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Struktura po każdym kroku: gdzie są pola i gdzie zachowanie (w tym "worek" z kroku 1). */
final class S09StructureTest {
    @Test
    void startBookingHoldsCustomerAndPaymentFields() {
        assertEquals(List.of("amount", "cardNumber", "customerEmail", "customerName", "customerPhone",
                "id", "paymentStatus"), fields(pl.training.workshop.m4.s09_extractclass.start.Booking.class));
    }

    @Test
    void step1CustomerIsADataBagWithoutBehaviour() {
        assertEquals(List.of(), behaviour(pl.training.workshop.m4.s09_extractclass.step1.Customer.class),
                "tylko akcesory rekordu - logika kontaktu została w Booking");
    }

    @Test
    void step3BookingComposesCustomerAndPayment() {
        assertEquals(List.of("amount", "customer", "id", "payment"),
                fields(pl.training.workshop.m4.s09_extractclass.step3.Booking.class));
        assertEquals(List.of("contactLine"),
                behaviour(pl.training.workshop.m4.s09_extractclass.step3.Customer.class));
    }

    /** Nazwy pól posortowane - kolejność z refleksji nie jest gwarantowana. */
    private static List<String> fields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).filter(f -> !f.isSynthetic()).map(Field::getName).sorted().toList();
    }

    /** Publiczne metody poza akcesorami rekordu i metodami z Object. */
    private static List<String> behaviour(Class<? extends Record> type) {
        List<String> accessors = Arrays.stream(type.getRecordComponents()).map(RecordComponent::getName).toList();
        return Arrays.stream(type.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .map(Method::getName)
                .filter(name -> !accessors.contains(name))
                .filter(name -> !List.of("equals", "hashCode", "toString").contains(name))
                .sorted()
                .toList();
    }
}
