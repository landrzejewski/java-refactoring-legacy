package pl.training.workshop.exercises.e06_movieinfo.step3;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.OmdbLikeClient;

/**
 * Krok 3: adapter dostaje tabelę kategorii przez konstruktor. Konstruktor jednoargumentowy
 * zachowuje dotychczasowe zachowanie (INSTANCE), test może podać inną tabelę bez globalnego stanu.
 */
public final class OmdbCatalogAdapter implements MovieCatalog {
    private final OmdbLikeClient client;
    private final AgeRatings ratings;

    public OmdbCatalogAdapter(OmdbLikeClient client) {
        this(client, AgeRatingTable.INSTANCE);
    }

    public OmdbCatalogAdapter(OmdbLikeClient client, AgeRatings ratings) {
        this.client = client;
        this.ratings = Objects.requireNonNull(ratings, "ratings");
    }

    @Override
    public MovieDetails find(String id) {
        Map<String, String> raw = client.byId(id);
        if ("False".equals(raw.get("Response"))) {
            throw new IllegalArgumentException("unknown movie: " + id);
        }
        String rating = raw.get("imdbRating");
        return new MovieDetails(
                raw.get("Title"),
                Integer.parseInt(raw.get("Year")),
                Integer.parseInt(raw.get("Runtime").replace(" min", "")),
                new BigDecimal(rating.substring(0, rating.indexOf('/'))),
                ratings.categoryFor("OMDB", raw.get("Rated")));
    }
}
