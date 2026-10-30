<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E05GiftCard;

use InvalidArgumentException;
use LogicException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionNamedType;
use ReflectionProperty;
use Training\Workshop\Exercises\E05GiftCard\DayOfWeek;
use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;
use Training\Workshop\Exercises\E05GiftCard\Step2\ActiveState;
use Training\Workshop\Exercises\E05GiftCard\Step2\AndRule;
use Training\Workshop\Exercises\E05GiftCard\Step2\BlockedState;
use Training\Workshop\Exercises\E05GiftCard\Step2\CardState;
use Training\Workshop\Exercises\E05GiftCard\Step2\DayIn;
use Training\Workshop\Exercises\E05GiftCard\Step2\EmptyState;
use Training\Workshop\Exercises\E05GiftCard\Step2\ExpiredState;
use Training\Workshop\Exercises\E05GiftCard\Step2\FormatIs;
use Training\Workshop\Exercises\E05GiftCard\Step2\GiftCard;
use Training\Workshop\Exercises\E05GiftCard\Step2\IsPremiere;
use Training\Workshop\Exercises\E05GiftCard\Step2\IssuedState;
use Training\Workshop\Exercises\E05GiftCard\Step2\Not;
use Training\Workshop\Exercises\E05GiftCard\Step2\OrRule;
use Training\Workshop\Exercises\E05GiftCard\Step2\VoucherRule;
use Training\Workshop\Exercises\E05GiftCard\Step2\VoucherRuleParser;
use Training\Workshop\Exercises\E05GiftCard\Step2\Vouchers;
use Training\Workshop\Shared\Money;

/** Kontrakt rozwiązania: tabela przejść w stanach, karta tylko deleguje, drzewo reguły składa się bez parsera. */
final class E05SolutionTest extends TestCase
{
    #[Test]
    public function transitionTableLivesInTheStatesNotInTheCard(): void
    {
        self::assertSame(ActiveState::instance(), IssuedState::instance()->activate());
        self::assertSame(BlockedState::instance(), ActiveState::instance()->block());
        self::assertSame(ExpiredState::instance(), BlockedState::instance()->expire());
        self::assertSame(ExpiredState::instance(), EmptyState::instance()->expire());

        try {
            BlockedState::instance()->activate();
            self::fail('oczekiwano LogicException');
        } catch (LogicException $blocked) {
            self::assertSame(LogicException::class, $blocked::class);
            self::assertSame('card is blocked', $blocked->getMessage());
        }
        $this->expectExceptionObject(new LogicException('card has expired'));
        ExpiredState::instance()->expire();
    }

    #[Test]
    public function cardHoldsAStateObjectAndNoStatusText(): void
    {
        $fieldTypes = array_map(
            fn (ReflectionProperty $field) => $field->getType() instanceof ReflectionNamedType ? $field->getType()->getName() : null,
            (new ReflectionClass(GiftCard::class))->getProperties(),
        );
        self::assertSame([CardState::class, Money::class], $fieldTypes, 'karta ma tylko stan i saldo - bez tekstowego statusu');
    }

    #[Test]
    public function statesAreStatelessAndShared(): void
    {
        foreach ([IssuedState::class, ActiveState::class, EmptyState::class, BlockedState::class, ExpiredState::class] as $state) {
            $instanceFields = array_filter(
                (new ReflectionClass($state))->getProperties(),
                fn (ReflectionProperty $field) => !$field->isStatic(),
            );
            self::assertCount(0, $instanceFields, (new ReflectionClass($state))->getShortName() . ' nie trzyma danych karty');
        }
        $first = new GiftCard(Money::of('10.00'));
        $second = new GiftCard(Money::of('20.00'));
        $first->activate();
        $second->activate();
        $first->charge(Money::of('10.00'));
        self::assertSame('EMPTY 0.00', $first->status() . ' ' . $first->balance());
        self::assertSame('ACTIVE 20.00', $second->status() . ' ' . $second->balance(), 'stan współdzielony, saldo nie');
    }

    #[Test]
    public function parserBuildsTheSameTreeYouWouldBuildByHand(): void
    {
        $byHand = new AndRule(
            new AndRule(new FormatIs('3D'), new DayIn([DayOfWeek::MONDAY, DayOfWeek::TUESDAY])),
            new Not(new IsPremiere()),
        );
        self::assertEquals($byHand, VoucherRuleParser::parse('format=3D AND day IN MON,TUE AND NOT premiere'));
    }

    #[Test]
    public function newRuleKindIsComposedWithoutTouchingTheParserOrVouchers(): void
    {
        /** @var VoucherRule $imaxOrPremiere */
        $imaxOrPremiere = new OrRule(new FormatIs('IMAX'), new IsPremiere());
        $premiere2d = new ScreeningFacts('2D', DayOfWeek::FRIDAY, true);
        $plain2d = new ScreeningFacts('2D', DayOfWeek::FRIDAY, false);

        self::assertTrue($imaxOrPremiere->applies($premiere2d));
        self::assertFalse($imaxOrPremiere->applies($plain2d));
        self::assertSame('format IMAX lub premiera', $imaxOrPremiere->describe());

        // składnia tekstowa jeszcze nie zna OR - to świadoma, osobna decyzja
        $this->expectException(InvalidArgumentException::class);
        $this->expectExceptionMessage('unknown rule term: premiere OR format=IMAX');
        Vouchers::applies('premiere OR format=IMAX', $premiere2d);
    }
}
