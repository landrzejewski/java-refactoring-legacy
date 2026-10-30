<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Start;

use BcMath\Number;
use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Start: serwis zna wszystko naraz - surowy format odpowiedzi OMDb ("155 min", "8.0/10"),
 * konwencje starej tabeli (ocena x10), zasadę "tylko personel", pamięć podręczną i tabelę
 * kategorii wiekowych. Sprawdzenie uprawnień i cache są powtórzone w każdej metodzie
 * publicznej, a nowe źródło danych albo nowa reguła dostępu oznacza edycję tej klasy.
 */
final class MovieInfoService
{
    /** @var array<string, MovieDetails> */
    private array $cache = [];

    public function __construct(private readonly OmdbLikeClient $omdb, private readonly LegacyMovieRows $legacy)
    {
    }

    public function lookup(User $user, MovieSource $source, string $id): MovieDetails
    {
        if (!$user->staff) {
            throw new LogicException('brak uprawnien: ' . $user->name);
        }
        $key = $source->name . ':' . $id;
        if (array_key_exists($key, $this->cache)) {
            return $this->cache[$key];
        }
        $details = $this->fetch($source, $id);
        $this->cache[$key] = $details;
        return $details;
    }

    public function runtime(User $user, MovieSource $source, string $id): int
    {
        if (!$user->staff) {
            throw new LogicException('brak uprawnien: ' . $user->name);
        }
        $key = $source->name . ':' . $id;
        if (array_key_exists($key, $this->cache)) {
            return $this->cache[$key]->runtimeMinutes;
        }
        return $this->fetch($source, $id)->runtimeMinutes;
    }

    private function fetch(MovieSource $source, string $id): MovieDetails
    {
        if ($source === MovieSource::OMDB) {
            $raw = $this->omdb->byId($id);
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
        $row = $this->legacy->find($id);
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
