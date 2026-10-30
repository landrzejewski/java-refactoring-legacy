<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Start;

use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Exercises\E01Import\ScreeningEntry;
use Training\Workshop\Shared\Money;

/**
 * Start: import repertuaru z CSV ({@code tytuł;format;sala;start}, komentarze od {@code #})
 * i z JSON Lines (jeden obiekt na wiersz, komentarze od {@code //}). Obie metody powstały przez
 * kopiowanie: pętla po wierszach, pomijanie pustych i komentarzy, numerowanie wierszy do komunikatów,
 * wspólna walidacja i sortowanie są w dwóch egzemplarzach. Różni się tylko sposób rozbioru wiersza.
 * Gdy dystrybutor doda trzeci format (XML), powstanie trzecia kopia.
 */
final class RepertoireImporter
{
    private const string JSON_FIELD = '/"(\w+)"\s*:\s*"([^"]*)"/';
    private const string MORNING_DISCOUNT = '5.00';
    private const string NOON = '12:00';

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    public function importFile(string $fileName, array $lines): array
    {
        if (str_ends_with($fileName, '.csv')) {
            return $this->importCsv($lines);
        } elseif (str_ends_with($fileName, '.jsonl')) {
            return $this->importJsonLines($lines);
        }
        throw new InvalidArgumentException('unsupported file: ' . $fileName);
    }

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    public function importCsv(array $lines): array
    {
        $entries = [];
        $number = 0;
        foreach ($lines as $raw) {
            $number++;
            $line = trim($raw);
            if ($line === '' || str_starts_with($line, '#')) {
                continue;
            }
            $fields = explode(';', $line);
            if (count($fields) !== 4) {
                throw new InvalidArgumentException('line ' . $number . ': expected 4 fields, got ' . count($fields));
            }
            $entries[] = self::toEntry($number, trim($fields[0]), trim($fields[1]), trim($fields[2]), trim($fields[3]));
        }
        usort($entries, fn (ScreeningEntry $a, ScreeningEntry $b) => [$a->start, $a->title] <=> [$b->start, $b->title]);
        return $entries;
    }

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    public function importJsonLines(array $lines): array
    {
        $entries = [];
        $number = 0;
        foreach ($lines as $raw) {
            $number++;
            $line = trim($raw);
            if ($line === '' || str_starts_with($line, '//')) {
                continue;
            }
            if (!str_starts_with($line, '{') || !str_ends_with($line, '}')) {
                throw new InvalidArgumentException('line ' . $number . ': not a JSON object');
            }
            $fields = [];
            preg_match_all(self::JSON_FIELD, $line, $matches, PREG_SET_ORDER);
            foreach ($matches as $match) {
                $fields[$match[1]] = $match[2];
            }
            foreach (['title', 'format', 'hall', 'start'] as $required) {
                if (!array_key_exists($required, $fields)) {
                    throw new InvalidArgumentException('line ' . $number . ': missing field: ' . $required);
                }
            }
            $entries[] = self::toEntry($number, $fields['title'], $fields['format'], $fields['hall'], $fields['start']);
        }
        usort($entries, fn (ScreeningEntry $a, ScreeningEntry $b) => [$a->start, $a->title] <=> [$b->start, $b->title]);
        return $entries;
    }

    private static function toEntry(int $number, string $title, string $format, string $hall, string $start): ScreeningEntry
    {
        if ($title === '') {
            throw new InvalidArgumentException('line ' . $number . ': empty title');
        }
        $base = match ($format) {
            '2D' => Money::of('25.00'),
            '3D' => Money::of('32.00'),
            'IMAX' => Money::of('40.00'),
            default => throw new InvalidArgumentException('line ' . $number . ': unknown format: ' . $format),
        };
        if ($hall === '') {
            throw new InvalidArgumentException('line ' . $number . ': empty hall');
        }
        $time = DateTimeImmutable::createFromFormat('!H:i', $start);
        if ($time === false || $time->format('H:i') !== $start) {
            throw new InvalidArgumentException('line ' . $number . ': bad start time: ' . $start);
        }
        $price = $time->format('H:i') < self::NOON ? $base->minus(Money::of(self::MORNING_DISCOUNT)) : $base;
        return new ScreeningEntry($time, $title, $format, $hall, $price);
    }
}
