<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/** Krok 2: DESELECT - usunięcie miejsca; cofnięcie wstawia je na tę samą pozycję. */
final class DeselectSeat implements Command
{
    private int $index = 0;

    public function __construct(private readonly Cart $cart, private readonly Seat $seat)
    {
    }

    public function execute(): void
    {
        $this->index = $this->cart->remove($this->seat);
    }

    public function undo(): void
    {
        $this->cart->insert($this->index, $this->seat);
    }
}
