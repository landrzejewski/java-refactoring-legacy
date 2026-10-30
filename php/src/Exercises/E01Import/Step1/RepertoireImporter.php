<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step1;

use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Exercises\E01Import\ScreeningEntry;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: wspólny szkielet importu (pętla, numeracja wierszy, pomijanie komentarzy, walidacja,
 * sortowanie) jest w jednym miejscu i jest final. Podklasa dostarcza tylko to, co zależy od formatu:
 * rozpoznanie komentarza i rozbiór jednego wiersza na surowe pola. Wybór importera po rozszerzeniu
 * nadal jest tutaj - i nadal powtarza się u klienta.
 */
abstract class RepertoireImporter
{
    private const string MORNING_DISCOUNT = '5.00';
    private const string NOON = '12:00';

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    public static function importFile(string $fileName, array $lines): array
    {
        if (str_ends_with($fileName, '.csv')) {
            return (new CsvRepertoireImporter())->importLines($lines);
        } elseif (str_ends_with($fileName, '.jsonl')) {
            return (new JsonLinesRepertoireImporter())->importLines($lines);
        }
        throw new InvalidArgumentException('unsupported file: ' . $fileName);
    }

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    final public function importLines(array $lines): array
    {
        $entries = [];
        $number = 0;
        foreach ($lines as $raw) {
            $number++;
            $line = trim($raw);
            if ($line === '' || $this->isComment($line)) {
                continue;
            }
            $entries[] = self::toEntry($number, $this->parseLine($line, $number));
        }
        usort($entries, fn (ScreeningEntry $a, ScreeningEntry $b) => [$a->start, $a->title] <=> [$b->start, $b->title]);
        return $entries;
    }

    abstract protected function isComment(string $line): bool;

    abstract protected function parseLine(string $line, int $number): RawScreening;

    private static function toEntry(int $number, RawScreening $raw): ScreeningEntry
    {
        if ($raw->title === '') {
            throw new InvalidArgumentException('line ' . $number . ': empty title');
        }
        $base = match ($raw->format) {
            '2D' => Money::of('25.00'),
            '3D' => Money::of('32.00'),
            'IMAX' => Money::of('40.00'),
            default => throw new InvalidArgumentException('line ' . $number . ': unknown format: ' . $raw->format),
        };
        if ($raw->hall === '') {
            throw new InvalidArgumentException('line ' . $number . ': empty hall');
        }
        $time = DateTimeImmutable::createFromFormat('!H:i', $raw->start);
        if ($time === false || $time->format('H:i') !== $raw->start) {
            throw new InvalidArgumentException('line ' . $number . ': bad start time: ' . $raw->start);
        }
        $price = $time->format('H:i') < self::NOON ? $base->minus(Money::of(self::MORNING_DISCOUNT)) : $base;
        return new ScreeningEntry($time, $raw->title, $raw->format, $raw->hall, $price);
    }
}
