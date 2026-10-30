<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E09CashDrawer;

use BcMath\Number;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Random\Engine\Mt19937;
use Random\Randomizer;
use Training\Workshop\Exercises\E09CashDrawer\DrawerDay;
use Training\Workshop\Exercises\E09CashDrawer\Start;
use Training\Workshop\Exercises\E09CashDrawer\Step2;
use Training\Workshop\Exercises\E09CashDrawer\Step2\CardSale;
use Training\Workshop\Exercises\E09CashDrawer\Step2\CashSale;
use Training\Workshop\Exercises\E09CashDrawer\Step2\Deposit;
use Training\Workshop\Exercises\E09CashDrawer\Step2\DiscountSplit;
use Training\Workshop\Exercises\E09CashDrawer\Step2\DiscountSplits;
use Training\Workshop\Exercises\E09CashDrawer\Step2\DrawerEntry;
use Training\Workshop\Exercises\E09CashDrawer\Step2\DrawerEntryVisitor;
use Training\Workshop\Exercises\E09CashDrawer\Step2\GroupDiscount;
use Training\Workshop\Exercises\E09CashDrawer\Step2\Refund;
use Training\Workshop\Shared\Money;

/**
 * Kontrakt rozwiązania: każdy wariant podziału sumuje się co do grosza, nowa operacja na wpisach
 * to jedna klasa, warianty podziału są bezstanowe, a moment wyboru wariantu zmienia moment błędu.
 */
final class E09SolutionTest extends TestCase
{
    #[Test]
    public function everySplitSumsExactlyToTheDiscountOnRandomGroups(): void
    {
        // Różnica względem Javy: java.util.Random(2026) zastąpiony przez Randomizer z silnikiem Mt19937(2026) -
        // inne losowe grupy, ta sama własność (200 grup po 10-39 biletów w cenach 10.00-59.99).
        $random = new Randomizer(new Mt19937(2026));
        for ($round = 0; $round < 200; $round++) {
            $tickets = self::randomTickets($random);
            $discount = self::sum($tickets)->percent(10);
            foreach (['PROPORTIONAL', 'LAST', 'EQUAL'] as $mode) {
                $shares = DiscountSplits::forMode($mode)->split($discount, $tickets);
                self::assertCount(count($tickets), $shares, $mode . ' liczba udzialow');
                self::assertEquals($discount, self::sum($shares), $mode . ' suma udzialow dla [' . implode(', ', $tickets) . ']');
                self::assertEmpty(
                    array_filter($shares, fn (Money $share) => $share->compareTo(Money::zero()) < 0),
                    $mode . ' ujemny udzial',
                );
            }
        }
    }

    #[Test]
    public function newOperationIsOneVisitorWithoutTouchingTheEntries(): void
    {
        /** @var DrawerEntryVisitor<string> $paymentMethod */
        $paymentMethod = new class implements DrawerEntryVisitor {
            public function visitCashSale(CashSale $sale): string
            {
                return 'gotowka';
            }

            public function visitCardSale(CardSale $sale): string
            {
                return 'karta';
            }

            public function visitRefund(Refund $refund): string
            {
                return 'gotowka';
            }

            public function visitDeposit(Deposit $deposit): string
            {
                return 'gotowka';
            }
        };
        $entries = [
            new CashSale('T-1', Money::of('25.00')),
            new CardSale('T-2', Money::of('32.00'), '4321'),
            new Refund('T-1', Money::of('25.00'), 'choroba'),
            new Deposit(Money::of('200.00'), 'pogotowie'),
        ];
        self::assertSame(
            ['gotowka', 'karta', 'gotowka', 'gotowka'],
            array_map(fn (DrawerEntry $entry) => $entry->accept($paymentMethod), $entries),
        );
    }

    #[Test]
    public function splitsAreSharedBecauseTheyAreStateless(): void
    {
        self::assertSame(DiscountSplits::forMode('EQUAL'), DiscountSplits::forMode('EQUAL'));
        self::assertSame(DiscountSplits::forMode('PROPORTIONAL'), DiscountSplits::forMode('PROPORTIONAL'));
    }

    #[Test]
    public function newSplitIsJustAnotherVariantWithoutTouchingTheRule(): void
    {
        $firstTicket = new class implements DiscountSplit {
            public function split(Money $discount, array $tickets): array
            {
                $shares = [$discount];
                for ($i = 1; $i < count($tickets); $i++) {
                    $shares[] = Money::zero();
                }
                return $shares;
            }
        };
        $rule = new GroupDiscount($firstTicket);
        $allocation = $rule->allocate(E09EquivalenceTest::tickets(10, '25.00'));
        self::assertNotNull($allocation);
        self::assertEquals(Money::of('25.00'), $allocation->discount);
        self::assertEquals(Money::of('25.00'), $allocation->shares[0]);
        self::assertNull($rule->allocate(E09EquivalenceTest::tickets(9, '25.00')), 'grupa za mala');
    }

    #[Test]
    public function choosingTheSplitUpFrontMovesTheUnknownModeErrorToSmallGroupsToo(): void
    {
        $smallGroup = new DrawerDay([], E09EquivalenceTest::tickets(3, '25.00'), 'RANDOM');
        if (self::startHasNoSplits()) {
            $before = (new Start\CashDrawerReport())->render($smallGroup);
            self::assertStringEndsWith("Rabat grupowy: brak (3 biletow)\n", $before, 'start nie sprawdza trybu dla malej grupy');
        }

        $this->expectException(InvalidArgumentException::class);
        $this->expectExceptionMessage('unknown split mode: RANDOM');
        (new Step2\CashDrawerReport())->render($smallGroup);
    }

    /** @return list<Money> */
    private static function randomTickets(Randomizer $random): array
    {
        $count = 10 + $random->getInt(0, 29);
        $tickets = [];
        for ($i = 0; $i < $count; $i++) {
            $tickets[] = new Money((new Number(1000 + $random->getInt(0, 4999)))->div(100, 2));
        }
        return $tickets;
    }

    /** @param list<Money> $amounts */
    private static function sum(array $amounts): Money
    {
        $total = Money::zero();
        foreach ($amounts as $amount) {
            $total = $total->plus($amount);
        }
        return $total;
    }

    /** Pułapka start (tryb sprawdzany dopiero dla dużej grupy) - tylko dopóki w Start nie ma wariantów podziału (po "warsztat.sh jump 2"). */
    private static function startHasNoSplits(): bool
    {
        return !interface_exists(Start\DiscountSplit::class);
    }
}
