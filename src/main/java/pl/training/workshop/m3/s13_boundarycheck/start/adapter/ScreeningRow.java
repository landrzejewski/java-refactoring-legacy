package pl.training.workshop.m3.s13_boundarycheck.start.adapter;

import java.sql.Timestamp;

/** Adapter bazy: wiersz tabeli seansów (typy JDBC). */
public record ScreeningRow(String table, String title, Timestamp start) {
}
