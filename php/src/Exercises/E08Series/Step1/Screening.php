<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Step1;

use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: seans sam umie się skopiować. {@see copy()} robi niezależną kopię mapy dopłat,
 * a {@see withStart()}, {@see withHall()} i {@see withSurcharge()} to "kopia z jedną zmianą".
 * Mapa dopłat nie jest już współdzielona: seans trzyma zwykłą tablicę PHP (kopiowaną przy przekazaniu)
 * zamiast {@code ArrayObject}, a getter zwraca kopię, której zmiana nie dotyka seansu.
 * Konstruktor o dziewięciu parametrach i rozsiane wartości domyślne zostają do kroku 2.
 */
final readonly class Screening
{
    /** @var array<string, Money> */
    private array $surcharges;

    /** @param array<string, Money>|null $surcharges */
    public function __construct(
        private string $title,
        private string $format,
        private ?string $hall,
        private DateTimeImmutable $start,
        private ?Money $basePrice,
        ?array $surcharges,
        private bool $subtitles,
        private bool $dubbing,
        private int $ageLimit,
    ) {
        $this->surcharges = $surcharges ?? [];
    }

    /** Pełna, niezależna kopia seansu (prototyp). */
    public function copy(): self
    {
        return new self($this->title, $this->format, $this->hall, $this->start, $this->basePrice, $this->surcharges,
            $this->subtitles, $this->dubbing, $this->ageLimit);
    }

    public function withStart(DateTimeImmutable $newStart): self
    {
        return new self($this->title, $this->format, $this->hall, $newStart, $this->basePrice, $this->surcharges,
            $this->subtitles, $this->dubbing, $this->ageLimit);
    }

    public function withHall(string $newHall): self
    {
        return new self($this->title, $this->format, $newHall, $this->start, $this->basePrice, $this->surcharges,
            $this->subtitles, $this->dubbing, $this->ageLimit);
    }

    public function withSurcharge(string $name, Money $amount): self
    {
        $extended = $this->surcharges;
        $extended[$name] = $amount;
        return new self($this->title, $this->format, $this->hall, $this->start, $this->basePrice, $extended,
            $this->subtitles, $this->dubbing, $this->ageLimit);
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

    /** @return array<string, Money> */
    public function surcharges(): array
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
        $sorted = $this->surcharges;
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
