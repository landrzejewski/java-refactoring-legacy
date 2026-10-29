package pl.training.workshop.exercises.e06_movieinfo.step2;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;

/** Krok 2: adapter internetowej bazy bez zmian. */
public final class OmdbCatalogAdapter implements MovieCatalog {
    private final OmdbLikeClient client;

    public OmdbCatalogAdapter(OmdbLikeClient client) {
        this.client = client;
    }

    @Override
    public MovieDetails find(String id) {
        Map<String, String> raw = client.byId(id);
        if ("False".equals(raw.get("Response"))) {
            throw new IllegalArgumentException("unknown movie: " + id);
        }
        AgeRatingTable table = AgeRatingTable.load();
        String rating = raw.get("imdbRating");
        return new MovieDetails(
                raw.get("Title"),
                Integer.parseInt(raw.get("Year")),
                Integer.parseInt(raw.get("Runtime").replace(" min", "")),
                new BigDecimal(rating.substring(0, rating.indexOf('/'))),
                table.categoryFor("OMDB", raw.get("Rated")));
    }
}
