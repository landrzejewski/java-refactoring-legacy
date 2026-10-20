package pl.training.workshop.m5.s31_anticorruptionlayer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Klient systemu dystrybutora (nie zmieniamy go): zwraca rekord albo null, gdy filmu nie zna. */
public class DistributorFeed {
    private final Map<String, DistributorRecord> records = new LinkedHashMap<>();

    public DistributorFeed(List<DistributorRecord> records) {
        records.forEach(record -> this.records.put(record.filmCode(), record));
    }

    public DistributorRecord fetch(String filmCode) {
        return records.get(filmCode);
    }
}
