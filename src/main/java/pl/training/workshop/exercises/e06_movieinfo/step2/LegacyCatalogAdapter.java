package pl.training.workshop.exercises.e06_movieinfo.step2;

import java.math.BigDecimal;

import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRow;
import pl.training.workshop.exercises.e06_movieinfo.LegacyMovieRows;
import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;

/** Krok 2: adapter starej bazy bez zmian. */
public final class LegacyCatalogAdapter implements MovieCatalog {
    private final LegacyMovieRows rows;

    public LegacyCatalogAdapter(LegacyMovieRows rows) {
        this.rows = rows;
    }

    @Override
    public MovieDetails find(String id) {
        LegacyMovieRow row = rows.find(id);
        if (row == null) {
            throw new IllegalArgumentException("unknown movie: " + id);
        }
        AgeRatingTable table = AgeRatingTable.load();
        return new MovieDetails(
                row.title(),
                row.year(),
                row.minutes(),
                BigDecimal.valueOf(row.ratingTimesTen(), 1),
                table.categoryFor("LEGACY", row.ageCode()));
    }
}
