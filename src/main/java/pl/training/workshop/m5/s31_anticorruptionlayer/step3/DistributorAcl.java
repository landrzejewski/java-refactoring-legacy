package pl.training.workshop.m5.s31_anticorruptionlayer.step3;

import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.m5.s31_anticorruptionlayer.DistributorFeed;

/**
 * Krok 3: Anti-Corruption Layer - adapter portu FilmLicenses nad klientem dystrybutora
 * plus translator modelu. Tylko ta warstwa widzi DistributorFeed i DistributorRecord.
 *
 * <p>Facade a ACL: fasada upraszcza dostęp do podsystemu, ale mówi jego językiem
 * (te same pojęcia, mniej wywołań). ACL tłumaczy cudzy model na własny i chroni domenę
 * przed cudzymi pojęciami - często jest zbudowana z fasady, adaptera i translatora naraz.
 */
public final class DistributorAcl implements FilmLicenses {
    private final DistributorFeed feed;
    private final DistributorTranslator translator = new DistributorTranslator();

    public DistributorAcl(DistributorFeed feed) {
        this.feed = Objects.requireNonNull(feed, "feed");
    }

    @Override
    public Optional<FilmLicense> find(String filmCode) {
        return Optional.ofNullable(feed.fetch(filmCode)).map(translator::toLicense);
    }
}
