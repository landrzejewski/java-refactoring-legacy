package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Channel;

/**
 * Krok 1: jedyny switch po kanale. Fabryki są bezstanowe, więc współdzielimy jedne instancje.
 * Nowy kanał to jedna nowa klasa fabryki i jeden wpis tutaj.
 */
public final class DocumentFactories {
    private static final DocumentFactory KIOSK = new KioskDocuments();
    private static final DocumentFactory MOBILE = new MobileDocuments();
    private static final DocumentFactory BOX_OFFICE = new BoxOfficeDocuments();

    private DocumentFactories() {
    }

    public static DocumentFactory forChannel(Channel channel) {
        return switch (channel) {
            case KIOSK -> KIOSK;
            case MOBILE -> MOBILE;
            case BOX_OFFICE -> BOX_OFFICE;
        };
    }
}
