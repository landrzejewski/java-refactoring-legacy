<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use BcMath\Number;
use InvalidArgumentException;
use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/** Krok 3: adapter starej bazy z tabelą kategorii wstrzykniętą przez konstruktor. */
final readonly class LegacyCatalogAdapter implements MovieCatalog
{
    public function __construct(private LegacyMovieRows $rows, private AgeRatings $ratings = AgeRatingTable::INSTANCE)
    {
    }

    public function find(string $id): MovieDetails
    {
        $row = $this->rows->find($id);
        if ($row === null) {
            throw new InvalidArgumentException('unknown movie: ' . $id);
        }
        return new MovieDetails(
            $row->title,
            $row->year,
            $row->minutes,
            (new Number($row->ratingTimesTen))->div(10, 1),
            $this->ratings->categoryFor('LEGACY', $row->ageCode),
        );
    }
}
