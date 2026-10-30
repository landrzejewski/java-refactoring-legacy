<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker;

/**
 * Stabilny kontrakt sceny: układ sali na jeden seans (Kraina Lodu 3D): liczba rzędów i miejsc,
 * pierwszy rząd VIP oraz miejsca już sprzedane.
 */
final readonly class HallPlan
{
    /** @param list<Seat> $taken */
    public function __construct(public int $rows, public int $seatsPerRow, public int $vipFromRow, public array $taken)
    {
    }

    /** Sala 12 x 10, VIP od rzędu 10, kilka miejsc sprzedanych. */
    public static function standard(): self
    {
        return new self(12, 10, 10, [
            new Seat(6, 5), new Seat(6, 6), new Seat(7, 1), new Seat(7, 2), new Seat(10, 3),
        ]);
    }

    /** Mała, prawie pełna sala 3 x 4: wolne są tylko pojedyncze miejsca, nigdzie dwa obok siebie. */
    public static function almostFull(): self
    {
        return new self(3, 4, 3, [
            new Seat(1, 2), new Seat(1, 4),
            new Seat(2, 1), new Seat(2, 3),
            new Seat(3, 2), new Seat(3, 3),
        ]);
    }

    public function exists(Seat $seat): bool
    {
        return $seat->row >= 1 && $seat->row <= $this->rows && $seat->number >= 1 && $seat->number <= $this->seatsPerRow;
    }

    public function isTaken(Seat $seat): bool
    {
        return in_array($seat, $this->taken);
    }

    public function isVip(Seat $seat): bool
    {
        return $seat->row >= $this->vipFromRow;
    }

    /**
     * Rzędy w kolejności preferencji: od środka sali na zewnątrz, np. 6, 7, 5, 8, 4, ...
     *
     * @return list<int>
     */
    public function rowsFromMiddle(): array
    {
        $order = [];
        $middle = intdiv($this->rows + 1, 2);
        for ($offset = 0; count($order) < $this->rows; $offset++) {
            if ($middle + $offset <= $this->rows && !in_array($middle + $offset, $order, true)) {
                $order[] = $middle + $offset;
            }
            if ($middle - $offset >= 1 && !in_array($middle - $offset, $order, true)) {
                $order[] = $middle - $offset;
            }
        }
        return $order;
    }
}
