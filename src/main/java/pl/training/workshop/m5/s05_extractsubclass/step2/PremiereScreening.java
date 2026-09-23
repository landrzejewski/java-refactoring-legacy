package pl.training.workshop.m5.s05_extractsubclass.step2;

/** Krok 2: nowa podklasa - na razie pusta, tylko przekazuje flagę i gościa do bazy. */
public final class PremiereScreening extends Screening {
    PremiereScreening(String title, String format, String guest) {
        super(title, format, true, guest);
    }
}
