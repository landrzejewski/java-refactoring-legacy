package pl.training.module3;

import java.math.BigDecimal;

public record InvoiceLine(String sku, int quantity, BigDecimal unitPrice) {
}
