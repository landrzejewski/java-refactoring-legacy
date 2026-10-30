<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series\Step2;

use Closure;
use DateTimeImmutable;
use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Budowniczy: wymagane tytuł, format i start; reszta domyślna. Walidacja w {@see build()}.
 * Odpowiednik zagnieżdżonej klasy {@code Screening.Builder} z Javy - PHP nie ma klas zagnieżdżonych,
 * więc budowniczy jest osobną klasą, a prywatny konstruktor seansu wywołuje w zasięgu klasy
 * {@see Screening} ({@code Closure::bind}). Tworzy go {@see Screening::builder()}.
 */
final class ScreeningBuilder
{
    private const array FORMATS = ['2D', '3D', 'IMAX'];
    private const string VIP = '10.00';
    private const string GLASSES_3D = '3.00';
    private const string DEFAULT_HALL = 'A';
    private const int DEFAULT_AGE_LIMIT = 12;

    private ?string $title = null;
    private ?string $format = null;
    private ?string $hall = null;
    private ?DateTimeImmutable $start = null;
    private ?Money $basePrice = null;
    /** @var array<string, Money> */
    private array $extraSurcharges = [];
    private bool $dubbing = false;
    private ?int $ageLimit = null;

    public function title(string $title): self
    {
        $this->title = $title;
        return $this;
    }

    public function format(string $format): self
    {
        $this->format = $format;
        return $this;
    }

    public function hall(string $hall): self
    {
        $this->hall = $hall;
        return $this;
    }

    public function start(DateTimeImmutable $start): self
    {
        $this->start = $start;
        return $this;
    }

    public function basePrice(Money $basePrice): self
    {
        $this->basePrice = $basePrice;
        return $this;
    }

    public function surcharge(string $name, Money $amount): self
    {
        $this->extraSurcharges[$name] = $amount;
        return $this;
    }

    public function dubbing(bool $dubbing): self
    {
        $this->dubbing = $dubbing;
        return $this;
    }

    public function ageLimit(int $ageLimit): self
    {
        $this->ageLimit = $ageLimit;
        return $this;
    }

    public function build(): Screening
    {
        if ($this->title === null || trim($this->title) === '') {
            throw new InvalidArgumentException('title must not be blank');
        }
        if ($this->format === null || !in_array($this->format, self::FORMATS, true)) {
            throw new InvalidArgumentException('unknown format: ' . ($this->format ?? 'null'));
        }
        if ($this->start === null) {
            throw new InvalidArgumentException('start is required');
        }
        $price = $this->basePrice ?? self::defaultPrice($this->format);
        if ($price->compareTo(Money::zero()) < 0) {
            throw new InvalidArgumentException('base price must not be negative');
        }
        $surcharges = [];
        $surcharges['VIP'] = Money::of(self::VIP);
        if ($this->format === '3D') {
            $surcharges['OKULARY'] = Money::of(self::GLASSES_3D);
        }
        foreach ($this->extraSurcharges as $name => $amount) {
            $surcharges[$name] = $amount;
        }
        $fields = [$this->title, $this->format, $this->hall ?? self::DEFAULT_HALL, $this->start, $price, $surcharges,
            !$this->dubbing, $this->dubbing, $this->ageLimit ?? self::DEFAULT_AGE_LIMIT];
        $create = Closure::bind(static fn (): Screening => new Screening(...$fields), null, Screening::class);
        return $create();
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
