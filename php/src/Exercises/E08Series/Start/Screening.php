<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Start;

use ArrayObject;
use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Start: seans z konstruktorem o dziewięciu parametrach. Wywołujący przekazuje {@code null}
 * tam, gdzie "ma być domyślnie", a klasa część domyślnych wartości uzupełnia sama
 * (pusta mapa dopłat, cena bazowa z formatu w {@see describe()}), część zostawia planerowi.
 * Mapa dopłat ({@code ArrayObject}, czyli obiekt, a nie kopiowana tablica) jest współdzielona
 * z tym, kto ją przekazał.
 */
final readonly class Screening
{
    /** @var ArrayObject<string, Money> */
    private ArrayObject $surcharges;

    /** @param ArrayObject<string, Money>|null $surcharges */
    public function __construct(
        private string $title,
        private string $format,
        private ?string $hall,
        private DateTimeImmutable $start,
        private ?Money $basePrice,
        ?ArrayObject $surcharges,
        private bool $subtitles,
        private bool $dubbing,
        private int $ageLimit,
    ) {
        $this->surcharges = $surcharges ?? new ArrayObject();
    }

    public function title(): string
    {
        return $this->title;
    }

    public function format(): string
    {
        return $this->format;
    }

    public function hall(): ?string
    {
        return $this->hall;
    }

    public function start(): DateTimeImmutable
    {
        return $this->start;
    }

    public function basePrice(): ?Money
    {
        return $this->basePrice;
    }

    /** @return ArrayObject<string, Money> */
    public function surcharges(): ArrayObject
    {
        return $this->surcharges;
    }

    public function subtitles(): bool
    {
        return $this->subtitles;
    }

    public function dubbing(): bool
    {
        return $this->dubbing;
    }

    public function ageLimit(): int
    {
        return $this->ageLimit;
    }

    public function describe(): string
    {
        $price = $this->basePrice ?? self::defaultPrice($this->format);
        $hallName = $this->hall ?? 'A';
        $language = $this->dubbing ? 'dubbing' : ($this->subtitles ? 'napisy' : 'oryginal');
        $sorted = $this->surcharges->getArrayCopy();
        ksort($sorted, SORT_STRING);
        $surcharges = implode(', ', array_map(fn (string $name, Money $amount) => $name . '=' . $amount, array_keys($sorted), $sorted));
        return $this->title . ' ' . $this->format . ' sala ' . $hallName . ' ' . $this->start->format('Y-m-d\TH:i') . ' cena ' . $price
            . ' doplaty {' . $surcharges . '} ' . $language . ' ' . $this->ageLimit . '+';
    }

    private static function defaultPrice(string $format): Money
    {
        return match ($format) {
            '2D' => Money::of('25.00'),
            '3D' => Money::of('32.00'),
            'IMAX' => Money::of('40.00'),
            default => throw new InvalidArgumentException('unknown format: ' . $format),
        };
    }
}
