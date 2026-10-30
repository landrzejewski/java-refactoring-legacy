<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/** Krok 2: SELECT - dodanie miejsca; cofnięcie je usuwa. */
final class SelectSeat implements Command
{
    public function __construct(private readonly Cart $cart, private readonly Seat $seat)
    {
    }

    public function execute(): void
    {
        $this->cart->add($this->seat);
    }

    public function undo(): void
    {
        $this->cart->remove($this->seat);
    }
}
