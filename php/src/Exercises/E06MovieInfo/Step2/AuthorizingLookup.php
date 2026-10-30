<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use LogicException;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 2: strażnik dostępu jako zastępca serwisu. Reguła "tylko personel" jest w jednym miejscu,
 * a odrzucone pytanie nigdy nie dociera do serwisu ani do źródeł danych.
 */
final readonly class AuthorizingLookup implements MovieLookup
{
    public function __construct(private MovieLookup $target)
    {
    }

    public function lookup(User $user, MovieSource $source, string $id): MovieDetails
    {
        self::requireStaff($user);
        return $this->target->lookup($user, $source, $id);
    }

    public function runtime(User $user, MovieSource $source, string $id): int
    {
        self::requireStaff($user);
        return $this->target->runtime($user, $source, $id);
    }

    private static function requireStaff(User $user): void
    {
        if (!$user->staff) {
            throw new LogicException('brak uprawnien: ' . $user->name);
        }
    }
}
