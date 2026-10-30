<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Step2;

use DateTimeImmutable;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: seans powstaje tylko przez {@see ScreeningBuilder}. Pola wymagane to tytuł, format i start;
 * reszta ma wartości domyślne kina ustalane w jednym miejscu ({@see ScreeningBuilder::build()}):
 * sala A, cena bazowa z formatu, dopłata VIP, okulary dla 3D, napisy, limit wieku 12.
 * Konstruktor jest prywatny, więc nie da się już zbudować seansu z {@code null}-ami,
 * a {@see describe()} nie musi niczego uzupełniać. Kopie z kroku 1 zostają.
 */
final readonly class Screening
{
    /** @param array<string, Money> $surcharges */
    private function __construct(
        private string $title,
        private string $format,
        private string $hall,
        private DateTimeImmutable $start,
        private Money $basePrice,
        private array $surcharges,
        private bool $subtitles,
        private bool $dubbing,
        private int $ageLimit,
    ) {
    }

    public static function builder(): ScreeningBuilder
    {
        return new ScreeningBuilder();
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

    public function hall(): string
    {
        return $this->hall;
    }

    public function start(): DateTimeImmutable
    {
        return $this->start;
    }

    public function basePrice(): Money
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
        $language = $this->dubbing ? 'dubbing' : ($this->subtitles ? 'napisy' : 'oryginal');
        $sorted = $this->surcharges;
        ksort($sorted, SORT_STRING);
        $surcharges = implode(', ', array_map(fn (string $name, Money $amount) => $name . '=' . $amount, array_keys($sorted), $sorted));
        return $this->title . ' ' . $this->format . ' sala ' . $this->hall . ' ' . $this->start->format('Y-m-d\TH:i') . ' cena ' . $this->basePrice
            . ' doplaty {' . $surcharges . '} ' . $language . ' ' . $this->ageLimit . '+';
    }
}
