<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use BcMath\Number;
use InvalidArgumentException;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;

/** Krok 2: adapter internetowej bazy bez zmian. */
final readonly class OmdbCatalogAdapter implements MovieCatalog
{
    public function __construct(private OmdbLikeClient $client)
    {
    }

    public function find(string $id): MovieDetails
    {
        $raw = $this->client->byId($id);
        if (($raw['Response'] ?? null) === 'False') {
            throw new InvalidArgumentException('unknown movie: ' . $id);
        }
        $table = AgeRatingTable::load();
        $rating = $raw['imdbRating'];
        return new MovieDetails(
            $raw['Title'],
            (int) $raw['Year'],
            (int) str_replace(' min', '', $raw['Runtime']),
            new Number(substr($rating, 0, strpos($rating, '/'))),
            $table->categoryFor('OMDB', $raw['Rated']),
        );
    }
}
