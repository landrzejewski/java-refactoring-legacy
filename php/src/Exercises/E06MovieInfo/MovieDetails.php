<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

use BcMath\Number;

/** Stabilny kontrakt sceny: dane filmu w języku kina (minuty, ocena 0-10, kategoria wiekowa kina). */
final readonly class MovieDetails
{
    public function __construct(
        public string $title,
        public int $year,
        public int $runtimeMinutes,
        public Number $rating,
        public string $ageCategory,
    ) {
    }

    public function describe(): string
    {
        return $this->title . ' (' . $this->year . '), ' . $this->runtimeMinutes . ' min, ocena ' . $this->rating
            . '/10, kategoria ' . $this->ageCategory;
    }
}
