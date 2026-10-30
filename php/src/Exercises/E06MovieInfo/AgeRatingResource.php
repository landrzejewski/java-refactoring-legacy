<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

use LogicException;

/**
 * Infrastruktura (nie zmieniamy): plik z mapowaniem obcych kodów wieku na kategorie kina,
 * czytany z katalogu zasobów ({@code resources}). Odczyt jest kosztowny (dysk, parsowanie), więc go
 * liczymy - tak jak w produkcji liczy się czas i operacje wejścia-wyjścia.
 */
final class AgeRatingResource
{
    private const string PATH = '/exercises/e06_movieinfo/age-ratings.csv';

    private static int $reads = 0;

    private function __construct()
    {
    }

    /** @return list<string> */
    public static function readLines(): array
    {
        self::$reads++;
        $file = dirname(__DIR__, 3) . '/resources' . self::PATH;
        $lines = is_readable($file) ? file($file, FILE_IGNORE_NEW_LINES) : false;
        if ($lines === false) {
            throw new LogicException('missing resource ' . self::PATH);
        }
        return $lines;
    }

    /** Ile razy plik został wczytany od startu procesu PHP. */
    public static function reads(): int
    {
        return self::$reads;
    }
}
