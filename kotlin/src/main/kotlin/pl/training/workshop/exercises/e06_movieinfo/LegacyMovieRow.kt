package pl.training.workshop.exercises.e06_movieinfo

/** Obcy format (nie zmieniamy): wiersz starej tabeli MOVIES - ocena pomnożona przez 10, kod wieku jako tekst. */
data class LegacyMovieRow(val title: String, val year: Int, val minutes: Int, val ratingTimesTen: Int, val ageCode: String)
