package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.math.BigDecimal;
import java.util.Objects;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRow;
import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/** Krok 3: adapter starej bazy z tabelą kategorii wstrzykniętą przez konstruktor. */
public final class LegacyCatalogAdapter implements MovieCatalog {
    private final LegacyMovieRows rows;
    private final AgeRatings ratings;

    public LegacyCatalogAdapter(LegacyMovieRows rows) {
        this(rows, AgeRatingTable.INSTANCE);
    }

    public LegacyCatalogAdapter(LegacyMovieRows rows, AgeRatings ratings) {
        this.rows = rows;
        this.ratings = Objects.requireNonNull(ratings, "ratings");
    }

    @Override
    public MovieDetails find(String id) {
        LegacyMovieRow row = rows.find(id);
        if (row == null) {
            throw new IllegalArgumentException("unknown movie: " + id);
        }
        return new MovieDetails(
                row.title(),
                row.year(),
                row.minutes(),
                BigDecimal.valueOf(row.ratingTimesTen(), 1),
                ratings.categoryFor("LEGACY", row.ageCode()));
    }
}
