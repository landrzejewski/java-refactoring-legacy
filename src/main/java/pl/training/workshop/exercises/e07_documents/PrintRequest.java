package pl.training.workshop.exercises.e07_documents;

/** Wejście testu równoważności: co, dla jakiego kanału i z jakimi sekcjami drukujemy. */
public record PrintRequest(Sale sale, Channel channel, PrintOptions options) {
}
