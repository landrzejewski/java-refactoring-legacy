package pl.training.workshop.exercises.e03_notifications;

/** Stabilny kontrakt sceny: zmiana w repertuarze, ktora kino oglasza klientom. */
public sealed interface Change permits Move, Cancel {
}
