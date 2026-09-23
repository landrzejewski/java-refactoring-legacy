package pl.training.workshop.m3.s13_boundarycheck.step2.adapter;

import java.sql.Timestamp;

/** Adapter bazy: wiersz tabeli seansów (typy JDBC). */
public record ScreeningRow(String table, String title, Timestamp start) {
}
