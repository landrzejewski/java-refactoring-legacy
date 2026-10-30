<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\AgeRatingResource;

/**
 * Krok 3: jedna instancja tabeli na proces. Bezpieczna, bo tabela jest niemutowalna i każda jej kopia
 * byłaby równoważna; enum daje jedyną instancję bez prywatnego konstruktora i statycznego pola.
 * Enum w PHP nie ma pól, więc tabela jest wczytywana leniwie przy pierwszym użyciu i trzymana
 * w zmiennej statycznej metody {@see categories()} - raz na proces, jak pole enuma w Javie.
 * To domyślna, nie jedyna implementacja {@see AgeRatings} - o cyklu życia decyduje korzeń
 * kompozycji, a test może podać własną tabelę.
 */
enum AgeRatingTable implements AgeRatings
{
    case INSTANCE;

    public function categoryFor(string $system, string $code): string
    {
        return self::categories()[$system . ':' . $code] ?? 'brak';
    }

    /** @return array<string, string> */
    private static function categories(): array
    {
        static $categories = null;
        if ($categories === null) {
            $parsed = [];
            foreach (AgeRatingResource::readLines() as $line) {
                if (trim($line) === '' || str_starts_with($line, '#')) {
                    continue;
                }
                $parts = explode(';', $line);
                $parsed[$parts[0] . ':' . $parts[1]] = $parts[2];
            }
            $categories = $parsed;
        }
        return $categories;
    }
}
