package pl.training.module2;

import java.time.LocalDate;

public record Subscription(String email, LocalDate renewalDate) {
}
