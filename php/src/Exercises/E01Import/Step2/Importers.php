<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

use InvalidArgumentException;

/**
 * Krok 2: jedyne miejsce, które zna rozszerzenia plików (prosta fabryka statyczna, nie wzorzec GoF).
 * Nowy format to nowy parser, nowy importer i jeden wiersz tutaj - klient i szkielet bez zmian.
 */
final class Importers
{
    private function __construct()
    {
    }

    public static function forFile(string $fileName): RepertoireImporter
    {
        if (str_ends_with($fileName, '.csv')) {
            return new CsvRepertoireImporter();
        } elseif (str_ends_with($fileName, '.jsonl')) {
            return new JsonLinesRepertoireImporter();
        }
        throw new InvalidArgumentException('unsupported file: ' . $fileName);
    }
}
