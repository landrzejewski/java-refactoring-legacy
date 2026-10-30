<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/** Krok 2: SWAP - zamiana miejsca na inne bez zmiany kolejności; cofnięcie przywraca stare. */
final class SwapSeats implements Command
{
    private int $index = 0;

    public function __construct(private readonly Cart $cart, private readonly Seat $from, private readonly Seat $to)
    {
    }

    public function execute(): void
    {
        $this->index = $this->cart->replace($this->from, $this->to);
    }

    public function undo(): void
    {
        $this->cart->replaceAt($this->index, $this->from);
    }
}
