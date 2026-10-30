<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use Training\Workshop\Exercises\E06MovieInfo\AgeRatingResource;

/** Krok 2: tabela kategorii wiekowych bez zmian - nadal wczytywana przy każdym wywołaniu. */
final readonly class AgeRatingTable
{
    /** @param array<string, string> $categories */
    private function __construct(private array $categories)
    {
    }

    public static function load(): self
    {
        $parsed = [];
        foreach (AgeRatingResource::readLines() as $line) {
            if (trim($line) === '' || str_starts_with($line, '#')) {
                continue;
            }
            $parts = explode(';', $line);
            $parsed[$parts[0] . ':' . $parts[1]] = $parts[2];
        }
        return new self($parsed);
    }

    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    public function categoryFor(string $system, string $code): string
    {
        return $this->categories[$system . ':' . $code] ?? 'brak';
    }
}
