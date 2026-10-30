<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step1;

use BcMath\Number;
use InvalidArgumentException;
use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/**
 * Krok 1: adapter starej bazy. Jako jedyny wie, że ocena jest pomnożona przez 10, kod wieku to
 * tekst "0".."16", a nieznany film to {@code null} zamiast wyjątku.
 */
final readonly class LegacyCatalogAdapter implements MovieCatalog
{
    public function __construct(private LegacyMovieRows $rows)
    {
    }

    public function find(string $id): MovieDetails
    {
        $row = $this->rows->find($id);
        if ($row === null) {
            throw new InvalidArgumentException('unknown movie: ' . $id);
        }
        $table = AgeRatingTable::load();
        return new MovieDetails(
            $row->title,
            $row->year,
            $row->minutes,
            (new Number($row->ratingTimesTen))->div(10, 1),
            $table->categoryFor('LEGACY', $row->ageCode),
        );
    }
}
