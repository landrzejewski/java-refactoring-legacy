<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Exercises\E01Import\ScreeningEntry;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: szkielet importu bez zmian, ale krok zależny od formatu to teraz osobny obiekt (parser),
 * który tworzy metoda wytwórcza {@see createLineParser()}. Podklasa importera mówi tylko,
 * jaki parser tworzy i jak się podpisuje; wybór importera po rozszerzeniu jest w {@see Importers}.
 */
abstract class RepertoireImporter
{
    private const string MORNING_DISCOUNT = '5.00';
    private const string NOON = '12:00';

    /**
     * @param list<string> $lines
     * @return list<ScreeningEntry>
     */
    final public function importLines(array $lines): array
    {
        $parser = $this->createLineParser();
        $entries = [];
        $number = 0;
        foreach ($lines as $raw) {
            $number++;
            $line = trim($raw);
            if ($line === '' || $parser->isComment($line)) {
                continue;
            }
            $entries[] = self::toEntry($number, $parser->parse($line, $number));
        }
        usort($entries, fn (ScreeningEntry $a, ScreeningEntry $b) => [$a->start, $a->title] <=> [$b->start, $b->title]);
        return $entries;
    }

    abstract public function label(): string;

    abstract protected function createLineParser(): LineParser;

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
