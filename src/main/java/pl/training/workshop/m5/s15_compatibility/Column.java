package pl.training.workshop.m5.s15_compatibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Stabilny kontrakt sceny: adnotacja kolumny eksportu (czytana refleksją, jak w ORM/JSON/CSV). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Column {
    String value();
}
