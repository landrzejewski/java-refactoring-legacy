package pl.training.workshop.exercises.e06_movieinfo;

/** Obcy format (nie zmieniamy): wiersz starej tabeli MOVIES - ocena pomnożona przez 10, kod wieku jako tekst. */
public record LegacyMovieRow(String title, int year, int minutes, int ratingTimesTen, String ageCode) {
}
