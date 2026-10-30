<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

use InvalidArgumentException;

/** Krok 2: parser wiersza CSV {@code tytuł;format;sala;start}, komentarze od {@code #}. */
final class CsvLineParser implements LineParser
{
    public function isComment(string $line): bool
    {
        return str_starts_with($line, '#');
    }

    public function parse(string $line, int $number): RawScreening
    {
        $fields = explode(';', $line);
        if (count($fields) !== 4) {
            throw new InvalidArgumentException('line ' . $number . ': expected 4 fields, got ' . count($fields));
        }
        return new RawScreening(trim($fields[0]), trim($fields[1]), trim($fields[2]), trim($fields[3]));
    }
}
