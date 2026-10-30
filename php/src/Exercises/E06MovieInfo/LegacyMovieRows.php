<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

/**
 * Stara baza kina (nie zmieniamy): tabela MOVIES ze swoimi konwencjami. Ocena jest liczbą całkowitą
 * pomnożoną przez 10 (80 = 8.0), czas w minutach, kod wieku to tekst "0", "7", "12", "16".
 * Nieznany identyfikator to {@code null}. Każde wywołanie liczy się jak zapytanie do bazy.
 */
final class LegacyMovieRows
{
    private int $calls = 0;

    public function find(string $id): ?LegacyMovieRow
    {
        $this->calls++;
        return self::rows()[$id] ?? null;
    }

    /** Liczba zapytań do starej bazy. */
    public function calls(): int
    {
        return $this->calls;
    }

    /** @return array<string, LegacyMovieRow> */
    private static function rows(): array
    {
        return [
            'M-001' => new LegacyMovieRow('Diuna', 2021, 155, 80, '12'),
            'M-002' => new LegacyMovieRow('Kraina Lodu II', 2019, 103, 68, '7'),
            'M-003' => new LegacyMovieRow('Amator', 1979, 117, 78, '0'),
            'M-004' => new LegacyMovieRow('Oppenheimer', 2023, 180, 83, '16'),
            'M-005' => new LegacyMovieRow('Seans testowy', 2026, 5, 50, '99'),
        ];
    }
}
