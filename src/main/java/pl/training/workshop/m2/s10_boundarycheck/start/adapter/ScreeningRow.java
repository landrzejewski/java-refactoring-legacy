package pl.training.workshop.m2.s10_boundarycheck.start.adapter;

import java.sql.Timestamp;

/** Adapter bazy: wiersz tabeli seansów (typy JDBC). */
public record ScreeningRow(String table, String title, Timestamp start) {
}
