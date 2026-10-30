<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use BcMath\Number;
use InvalidArgumentException;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;

/**
 * Krok 3: adapter dostaje tabelę kategorii przez konstruktor. Konstruktor z jednym argumentem
 * zachowuje dotychczasowe zachowanie (INSTANCE), test może podać inną tabelę bez globalnego stanu.
 */
final readonly class OmdbCatalogAdapter implements MovieCatalog
{
    public function __construct(private OmdbLikeClient $client, private AgeRatings $ratings = AgeRatingTable::INSTANCE)
    {
    }

    public function find(string $id): MovieDetails
    {
        $raw = $this->client->byId($id);
        if (($raw['Response'] ?? null) === 'False') {
            throw new InvalidArgumentException('unknown movie: ' . $id);
        }
        $rating = $raw['imdbRating'];
        return new MovieDetails(
            $raw['Title'],
            (int) $raw['Year'],
            (int) str_replace(' min', '', $raw['Runtime']),
            new Number(substr($rating, 0, strpos($rating, '/'))),
            $this->ratings->categoryFor('OMDB', $raw['Rated']),
        );
    }
}
