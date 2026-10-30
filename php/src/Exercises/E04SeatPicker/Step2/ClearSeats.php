<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/** Krok 2: CLEAR - opróżnienie listy miejsc; cofnięcie dopisuje zapamiętane miejsca. */
final class ClearSeats implements Command
{
    /** @var list<Seat> */
    private array $before = [];

    public function __construct(private readonly Cart $cart)
    {
    }

    public function execute(): void
    {
        $this->before = $this->cart->seats();
        $this->cart->clear();
    }

    public function undo(): void
    {
        foreach ($this->before as $seat) {
            $this->cart->insert(count($this->cart->seats()), $seat);
        }
    }
}
