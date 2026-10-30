<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step1;

use InvalidArgumentException;

/** Krok 1: import CSV to już tylko rozpoznanie komentarza i podział wiersza na cztery pola. */
final class CsvRepertoireImporter extends RepertoireImporter
{
    protected function isComment(string $line): bool
    {
        return str_starts_with($line, '#');
    }

    protected function parseLine(string $line, int $number): RawScreening
    {
        $fields = explode(';', $line);
        if (count($fields) !== 4) {
            throw new InvalidArgumentException('line ' . $number . ': expected 4 fields, got ' . count($fields));
        }
        return new RawScreening(trim($fields[0]), trim($fields[1]), trim($fields[2]), trim($fields[3]));
    }
}
