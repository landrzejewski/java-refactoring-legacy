package pl.training.workshop.m3.s13_boundarycheck.step1.adapter;

import java.sql.Timestamp;

/** Adapter bazy: wiersz tabeli seansów (typy JDBC). */
public record ScreeningRow(String table, String title, Timestamp start) {
}
