package pl.training.workshop.m5.s16_serializationproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Serializacja: dane zapisane przez start czytane przez nowsze kroki. Proxy: kiedy da się opakować serwis. */
final class S16SolutionTest {
    private static final String START = "pl.training.workshop.m5.s16_serializationproxy.start.";

    /**
     * "Plik .ser z v1": StudentTicket("Amator", "F3", "S-123") zapisany przez klasę ze start
     * (bez nadklasy). Zamrożony jako stała, żeby zmiany start na żywo nie zmieniały danych historycznych.
     */
    private static final String SAVED_BY_V1 = "rO0ABXNyAEJwbC50cmFpbmluZy53b3Jrc2hvcC5tNS5zMTZfc2VyaWFsaXphdGlvbnByb3h5"
            + "LnN0YXJ0LlN0dWRlbnRUaWNrZXQAAAAAAAAAAQIAA0wABHNlYXR0ABJMamF2YS9sYW5nL1N0cmluZztMAAlzdHVkZW50SWRx"
            + "AH4AAUwABXRpdGxlcQB+AAF4cHQAAkYzdAAFUy0xMjN0AAZBbWF0b3I=";

    @Test
    void pulledUpFieldsAreSilentlyLostWhenReadingOldData() throws Exception {
        byte[] oldData = Base64.getDecoder().decode(SAVED_BY_V1);
        var ticket = (pl.training.workshop.m5.s16_serializationproxy.step1.StudentTicket) readAs(oldData, "pl.training.workshop.m5.s16_serializationproxy.step1.");
        assertEquals("null null (legitymacja S-123)", ticket.describe(),
                "pułapka: ten sam serialVersionUID, brak wyjątku, utracone title i seat");
    }

    @Test
    void serializationProxyRejectsOldDataLoudly() throws Exception {
        byte[] oldData = Base64.getDecoder().decode(SAVED_BY_V1);
        assertThrows(InvalidClassException.class, () -> readAs(oldData, "pl.training.workshop.m5.s16_serializationproxy.step2."));
    }

    @Test
    void serializationProxyWritesFlatFormIndependentOfHierarchy() throws Exception {
        String stream = new String(serialize(new pl.training.workshop.m5.s16_serializationproxy.step2.StudentTicket("Amator", "F3", "S-1")),
                java.nio.charset.StandardCharsets.ISO_8859_1);
        assertTrue(stream.contains("SerializedForm"));
        assertFalse(stream.contains("step2.Ticket"), "poziom Ticket nie jest częścią formatu");
    }

    @Test
    void jdkProxyCannotWrapFinalClassWithoutInterface() {
        assertTrue(Modifier.isFinal(pl.training.workshop.m5.s16_serializationproxy.step2.TicketPricing.class.getModifiers()));
        assertEquals(0, pl.training.workshop.m5.s16_serializationproxy.step2.TicketPricing.class.getInterfaces().length);
        assertThrows(IllegalArgumentException.class, () -> Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {pl.training.workshop.m5.s16_serializationproxy.step2.TicketPricing.class}, (proxy, method, args) -> null));
    }

    @Test
    void extractedInterfaceAllowsDynamicProxy() {
        AtomicInteger calls = new AtomicInteger();
        pl.training.workshop.m5.s16_serializationproxy.step3.Pricing target = new pl.training.workshop.m5.s16_serializationproxy.step3.TicketPricing();
        InvocationHandler audit = (proxy, method, args) -> {
            calls.incrementAndGet();
            return method.invoke(target, args);
        };
        var pricing = (pl.training.workshop.m5.s16_serializationproxy.step3.Pricing) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {pl.training.workshop.m5.s16_serializationproxy.step3.Pricing.class}, audit);
        assertEquals(Money.of("24.00"), pricing.studentPrice(Money.of("32.00")));
        assertEquals(1, calls.get());
    }

    private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        return bytes.toByteArray();
    }

    /** Czyta bajty zapisane przez start tak, jakby klasy z tych samych nazw pochodziły z nowszej wersji. */
    private static Object readAs(byte[] data, String newPackage) throws Exception {
        try (ObjectInputStream in = new RenamingInputStream(new ByteArrayInputStream(data), newPackage)) {
            return in.readObject();
        }
    }

    private static final class RenamingInputStream extends ObjectInputStream {
        private final String newPackage;

        RenamingInputStream(InputStream in, String newPackage) throws IOException {
            super(in);
            this.newPackage = newPackage;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName().replace(START, newPackage);
            return Class.forName(name, false, S16SolutionTest.class.getClassLoader());
        }
    }
}
