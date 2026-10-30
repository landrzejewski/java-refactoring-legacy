<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

/**
 * Obca biblioteka (nie zmieniamy): klient internetowej bazy filmów w stylu OMDb. Odpowiada surową
 * mapą pól tekstowych: czas trwania jako "155 min", ocena jako "8.0/10", rok jako tekst, kategoria
 * wiekowa w kodach amerykańskich (G, PG, PG-13, R, Not Rated). Nieznany film to odpowiedź
 * z polem Response=False, jak w prawdziwym API. Każde wywołanie liczy się jak zapytanie sieciowe.
 */
final class OmdbLikeClient
{
    private const array MOVIES = [
        'tt1160419' => ['Title' => 'Dune', 'Year' => '2021', 'Runtime' => '155 min',
            'imdbRating' => '8.0/10', 'Rated' => 'PG-13', 'Response' => 'True'],
        'tt4520988' => ['Title' => 'Frozen II', 'Year' => '2019', 'Runtime' => '103 min',
            'imdbRating' => '6.8/10', 'Rated' => 'PG', 'Response' => 'True'],
        'tt15398776' => ['Title' => 'Oppenheimer', 'Year' => '2023', 'Runtime' => '180 min',
            'imdbRating' => '8.3/10', 'Rated' => 'R', 'Response' => 'True'],
        'tt0077269' => ['Title' => 'Amator', 'Year' => '1979', 'Runtime' => '117 min',
            'imdbRating' => '7.8/10', 'Rated' => 'Not Rated', 'Response' => 'True'],
    ];

    private int $calls = 0;

    /** @return array<string, string> */
    public function byId(string $imdbId): array
    {
        $this->calls++;
        return self::MOVIES[$imdbId] ?? ['Response' => 'False', 'Error' => 'Movie not found!'];
    }

    /** Liczba zapytań do zewnętrznego API - w produkcji każde kosztuje czas i limit dzienny. */
    public function calls(): int
    {
        return $this->calls;
    }
}
