using Training.Workshop.Shared;

namespace Training.Workshop.M5.S15Compatibility.Start;

/// <summary>Start: publiczne API biblioteki kasowej, z którego korzystają SKOMPILOWANE wtyczki partnerów.</summary>
public sealed class BoxOfficeApi
{
    public Money Quote(StudentTicket ticket)
    {
        return ticket.Price();
    }
}
