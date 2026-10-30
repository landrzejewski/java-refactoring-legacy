<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Closure;
use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;
use Training\Workshop\Exercises\E04SeatPicker\Seat;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: koszyk sam robi migawkę całego swojego stanu i sam ją przywraca. Migawka jest nieprzezroczysta
 * dla innych klas (prywatne pola, brak metod), więc nikt poza koszykiem nie zna ani nie kopiuje jego pól.
 * Nowe pole koszyka wystarczy dopisać w jednym miejscu: tu.
 */
final class Cart
{
    private const string BASE = '32.00';
    private const string VIP = '10.00';
    private const string GLASSES = '3.00';
    private const int LIMIT = 4;

    /** @var list<Seat> */
    private array $seats = [];
    private bool $ownGlasses = false;
    private ?string $voucher = null;

    public function __construct(private readonly HallPlan $plan)
    {
    }

    public function plan(): HallPlan
    {
        return $this->plan;
    }

    public function snapshot(): Memento
    {
        $create = Closure::bind(
            static fn (array $seats, bool $ownGlasses, ?string $voucher) => new Memento($seats, $ownGlasses, $voucher),
            null,
            Memento::class,
        );
        return $create($this->seats, $this->ownGlasses, $this->voucher);
    }

    public function restore(Memento $memento): void
    {
        [$this->seats, $this->ownGlasses, $this->voucher] =
            (fn () => [$this->seats, $this->ownGlasses, $this->voucher])->call($memento);
    }

    /** @return list<Seat> */
    public function seats(): array
    {
        return $this->seats;
    }

    public function add(Seat $seat): void
    {
        $this->checkFree($seat);
        if (count($this->seats) >= self::LIMIT) {
            throw new LogicException('limit ' . self::LIMIT . ' miejsc');
        }
        $this->seats[] = $seat;
    }

    /** Usuwa miejsce i zwraca jego pozycję, żeby cofnięcie mogło ją przywrócić. */
    public function remove(Seat $seat): int
    {
        $index = array_search($seat, $this->seats);
        if ($index === false) {
            throw new InvalidArgumentException('miejsca ' . $seat . ' nie ma w koszyku');
        }
        array_splice($this->seats, $index, 1);
        return $index;
    }

    public function insert(int $index, Seat $seat): void
    {
        array_splice($this->seats, $index, 0, [$seat]);
    }

    /** Zamienia miejsce na inne na tej samej pozycji i zwraca tę pozycję. */
    public function replace(Seat $from, Seat $to): int
    {
        $index = array_search($from, $this->seats);
        if ($index === false) {
            throw new InvalidArgumentException('miejsca ' . $from . ' nie ma w koszyku');
        }
        $this->checkFree($to);
        $this->seats[$index] = $to;
        return $index;
    }

    public function replaceAt(int $index, Seat $seat): void
    {
        $this->seats[$index] = $seat;
    }

    public function clear(): void
    {
        $this->seats = [];
    }

    public function ownGlasses(): bool
    {
        return $this->ownGlasses;
    }

    public function setOwnGlasses(bool $ownGlasses): void
    {
        $this->ownGlasses = $ownGlasses;
    }

    public function voucher(): ?string
    {
        return $this->voucher;
    }

    public function setVoucher(?string $voucher): void
    {
        $this->voucher = $voucher;
    }

    private function checkFree(Seat $seat): void
    {
        if (!$this->plan->exists($seat)) {
            throw new InvalidArgumentException('nie ma miejsca ' . $seat);
        }
        if ($this->plan->isTaken($seat)) {
            throw new LogicException('miejsce ' . $seat . ' jest zajete');
        }
        if (in_array($seat, $this->seats)) {
            throw new LogicException('miejsce ' . $seat . ' jest juz w koszyku');
        }
    }

    public function state(): string
    {
        $list = $this->seats === [] ? '-' : implode(' ', array_map(strval(...), $this->seats));
        return 'miejsca: ' . $list
            . ' | okulary: ' . ($this->ownGlasses ? 'wlasne' : 'wypozyczone')
            . ' | voucher: ' . ($this->voucher ?? '-')
            . ' | suma: ' . $this->total();
    }

    private function total(): Money
    {
        $sum = Money::zero();
        foreach ($this->seats as $seat) {
            $price = Money::of(self::BASE);
            if ($this->plan->isVip($seat)) {
                $price = $price->plus(Money::of(self::VIP));
            }
            if (!$this->ownGlasses) {
                $price = $price->plus(Money::of(self::GLASSES));
            }
            $sum = $sum->plus($price);
        }
        if ($this->voucher === 'KINO10') {
            $sum = $sum->minus($sum->percent(10));
        }
        return $sum;
    }
}
