<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step1;

use LogicException;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 1: serwis zależy wyłącznie od portu {@see MovieCatalog} - nie importuje już klienta OMDb
 * ani starej bazy. Zostały dwa zapachy: sprawdzenie uprawnień i pamięć podręczna, powtórzone
 * w każdej metodzie publicznej.
 */
final class MovieInfoService
{
    /** @var array<string, MovieDetails> */
    private array $cache = [];

    /** @param array<string, MovieCatalog> $catalogs katalog dla każdego źródła, klucz to {@code MovieSource->name} */
    public function __construct(private readonly array $catalogs)
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
        $details = $this->catalogs[$source->name]->find($id);
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
        return $this->catalogs[$source->name]->find($id)->runtimeMinutes;
    }
}
