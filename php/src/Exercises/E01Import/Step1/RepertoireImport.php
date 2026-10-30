<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E01Import\ImportRequest;

/**
 * Krok 1: klient bez zmian poza wywołaniem statycznej metody importu. Rozpoznanie rozszerzenia
 * dla podpisu raportu nadal jest tutaj, drugi raz w importerze.
 */
final class RepertoireImport
{
    public function load(ImportRequest $request): string
    {
        if (str_ends_with($request->fileName, '.csv')) {
            $label = 'CSV';
        } elseif (str_ends_with($request->fileName, '.jsonl')) {
            $label = 'JSONL';
        } else {
            throw new InvalidArgumentException('unsupported file: ' . $request->fileName);
        }
        $entries = RepertoireImporter::importFile($request->fileName, $request->lines);
        $report = '[' . $label . '] pozycji: ' . count($entries);
        foreach ($entries as $entry) {
            $report .= "\n" . $entry;
        }
        return $report;
    }
}
