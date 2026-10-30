<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

/** Obcy format (nie zmieniamy): wiersz starej tabeli MOVIES - ocena pomnożona przez 10, kod wieku jako tekst. */
final readonly class LegacyMovieRow
{
    public function __construct(
        public string $title,
        public int $year,
        public int $minutes,
        public int $ratingTimesTen,
        public string $ageCode,
    ) {
    }
}
