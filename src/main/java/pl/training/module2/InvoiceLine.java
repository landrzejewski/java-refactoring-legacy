package pl.training.module2;

import java.math.BigDecimal;

public record InvoiceLine(String sku, int quantity, BigDecimal unitPrice) {
}
