<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

/** Krok 2: wpis przyjmuje odwiedzającego i sam wybiera jego metodę (double dispatch). */
interface DrawerEntry
{
    /**
     * @template R
     * @param DrawerEntryVisitor<R> $visitor
     * @return R
     */
    public function accept(DrawerEntryVisitor $visitor): mixed;
}
