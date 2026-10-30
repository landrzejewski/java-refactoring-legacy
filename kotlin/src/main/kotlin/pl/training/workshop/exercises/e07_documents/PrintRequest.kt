package pl.training.workshop.exercises.e07_documents

/**
 * Wejście testu równoważności: co, dla jakiego kanału i z jakimi sekcjami drukujemy.
 * Pola są nullable, bo test sprawdza też odrzucenie brakujących danych (jak `null` w Javie).
 */
data class PrintRequest(val sale: Sale?, val channel: Channel?, val options: PrintOptions?)
