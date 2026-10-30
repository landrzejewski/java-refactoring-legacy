<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step1;

use BcMath\Number;
use InvalidArgumentException;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;

/**
 * Krok 1: adapter internetowej bazy. Jako jedyny zna format odpowiedzi OMDb: "155 min", "8.0/10",
 * rok jako tekst, kody wieku G/PG/PG-13/R i odmowę przez Response=False. Tłumaczy nie tylko nazwy,
 * ale też jednostki i sposób zgłaszania błędu.
 */
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
