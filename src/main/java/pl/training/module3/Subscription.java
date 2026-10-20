package pl.training.module3;

import java.time.LocalDate;

public record Subscription(String email, LocalDate renewalDate) {
}
