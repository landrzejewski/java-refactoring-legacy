package pl.training.workshop.exercises.e07_documents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.exercises.e07_documents.step2.ChannelDocument;
import pl.training.workshop.exercises.e07_documents.step2.CodeRenderer;
import pl.training.workshop.exercises.e07_documents.step2.Document;
import pl.training.workshop.exercises.e07_documents.step2.DocumentComposer;
import pl.training.workshop.exercises.e07_documents.step2.DocumentFactories;
import pl.training.workshop.exercises.e07_documents.step2.DocumentFactory;
import pl.training.workshop.exercises.e07_documents.step2.LoyaltyFooter;
import pl.training.workshop.exercises.e07_documents.step2.PromoBanner;
import pl.training.workshop.exercises.e07_documents.step2.ReceiptLayout;
import pl.training.workshop.exercises.e07_documents.step2.TicketLayout;
import pl.training.workshop.exercises.e07_documents.step2.VatSection;
import pl.training.workshop.shared.Money;

/**
 * Pułapka start (zestaw da się pomieszać) i kontrakt rozwiązania: spójna rodzina na kanał,
 * nowy kanał jedną klasą, sekcje owijane w kolejności zapisanej w jednym miejscu.
 */
final class E07SolutionTest {
    private static final String START = "pl.training.workshop.exercises.e07_documents.start.";
    private static final String STEP2 = "pl.training.workshop.exercises.e07_documents.step2.";
    private static final Sale SALE = new Sale("S-1001", "Diuna", "R10-5", Money.of("40.00"), Money.ZERO, 96);

    /** Pomiar w start: trzy luźne części można pomieszać; po "warsztat.sh jump" test jest pominięty. */
    @Test
    void startLetsYouMixTicketCodeAndReceiptOfDifferentChannels() throws ReflectiveOperationException {
        Constructor<?> loose;
        try {
            loose = Class.forName(START + "DocumentComposer").getConstructor(
                    Class.forName(START + "TicketLayout"),
                    Class.forName(START + "CodeRenderer"),
                    Class.forName(START + "ReceiptLayout"));
        } catch (ReflectiveOperationException exception) {
            loose = null;
        }
        assumeTrue(loose != null, "start nie przyjmuje już trzech luźnych części");
        Object mixed = loose.newInstance(
                Class.forName(START + "KioskTicketLayout").getConstructor().newInstance(),
                Class.forName(START + "MobileCodeRenderer").getConstructor().newInstance(),
                Class.forName(START + "BoxOfficeReceiptLayout").getConstructor().newInstance());
        String document = (String) mixed.getClass().getMethod("compose", Sale.class).invoke(mixed, SALE);
        assertTrue(document.contains("********") && document.contains("<qr>") && document.contains("PARAGON FISKALNY"),
                "kiosk + QR aplikacji + paragon A4 w jednym dokumencie:\n" + document);
    }

    @Test
    void solutionHidesConcreteDocumentsBehindFamilies() throws ClassNotFoundException {
        for (String prefix : List.of("Kiosk", "Mobile", "BoxOffice")) {
            for (String product : List.of("TicketLayout", "CodeRenderer", "ReceiptLayout")) {
                Class<?> concrete = Class.forName(STEP2 + prefix + product);
                assertFalse(Modifier.isPublic(concrete.getModifiers()), concrete.getSimpleName() + " jest publiczna");
            }
            assertTrue(Modifier.isPublic(Class.forName(STEP2 + prefix + "Documents").getModifiers()));
        }
        assertTrue(Modifier.isPublic(DocumentFactory.class.getModifiers()));
    }

    @Test
    void everyFamilyDeliversPartsOfOneChannelOnly() {
        for (Channel channel : Channel.values()) {
            DocumentFactory family = DocumentFactories.forChannel(channel);
            String prefix = family.ticketLayout().getClass().getSimpleName().replace("TicketLayout", "");
            assertEquals(prefix + "CodeRenderer", family.codeRenderer().getClass().getSimpleName());
            assertEquals(prefix + "ReceiptLayout", family.receiptLayout().getClass().getSimpleName());
        }
    }

    @Test
    void newChannelIsOneFamilyWithoutTouchingTheComposer() {
        DocumentFactory email = new DocumentFactory() {
            @Override
            public TicketLayout ticketLayout() {
                return sale -> "Bilet " + sale.number() + " na " + sale.movie();
            }

            @Override
            public CodeRenderer codeRenderer() {
                return sale -> "[" + sale.number() + "]";
            }

            @Override
            public ReceiptLayout receiptLayout() {
                return new ReceiptLayout() {
                    @Override
                    public String receipt(Sale sale) {
                        return "Razem " + sale.ticketPrice().plus(sale.barPrice());
                    }

                    @Override
                    public String section(List<String> lines) {
                        return "> " + String.join(" | ", lines);
                    }
                };
            }
        };
        String document = new DocumentComposer(email).compose(SALE, PrintOptions.ALL).render();
        assertEquals("""
                Bilet S-1001 na Diuna
                [S-1001]
                Razem 40.00
                > VAT 8% bilet: 2.96
                > Kod KINO10: -10% na nastepny bilet online
                > Punkty: 96 + 4 = 100 | Odbierz darmowy bilet 2D""", document);
    }

    @Test
    void wrappingOrderIsBehaviourAndIsFixedInTheComposer() {
        DocumentFactory kiosk = DocumentFactories.forChannel(Channel.KIOSK);
        ReceiptLayout layout = kiosk.receiptLayout();
        Document core = new ChannelDocument(SALE, kiosk);
        Document vatThenLoyalty = new LoyaltyFooter(new VatSection(core, layout, SALE), layout, SALE);
        Document loyaltyThenVat = new VatSection(new LoyaltyFooter(core, layout, SALE), layout, SALE);
        assertNotEquals(vatThenLoyalty.render(), loyaltyThenVat.render(), "kolejność owijania zmienia dokument");

        String composed = new DocumentComposer(kiosk).compose(SALE, new PrintOptions(true, false, true)).render();
        assertEquals(vatThenLoyalty.render(), composed, "composer owija w kolejności VAT, promocja, stopka");
    }

    @Test
    void everySectionWrapsExactlyOneInnerDocument() {
        for (Class<?> section : List.of(VatSection.class, PromoBanner.class, LoyaltyFooter.class)) {
            assertTrue(Document.class.isAssignableFrom(section), section.getSimpleName() + " ma kontrakt dokumentu");
            long inner = Arrays.stream(section.getDeclaredFields())
                    .map(Field::getType)
                    .filter(Document.class::equals)
                    .count();
            assertEquals(1, inner, section.getSimpleName());
        }
        assertEquals(0, Arrays.stream(ChannelDocument.class.getDeclaredFields())
                .map(Field::getType).filter(Document.class::equals).count(), "rdzeń niczego nie owija");
    }
}
