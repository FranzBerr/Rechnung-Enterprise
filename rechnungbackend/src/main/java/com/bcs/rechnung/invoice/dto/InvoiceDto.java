package com.bcs.rechnung.invoice.dto;

import com.bcs.rechnung.invoice.InvoiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
    name = "InvoiceDto",
    description = "Rechnung"
)
public record InvoiceDto(

    Long id,

    String invoiceNumber,

    LocalDate invoiceDate,

    LocalDate serviceDate,

    LocalDate dueDate,

    InvoiceStatus status,

    Long companyId,

    Long customerId,

    Long bankAccountId,

    String customerNumber,

    String customerName,

    String customerStreet,

    String customerZipCode,

    String customerCity,

    String customerCountry,

    String customerVatId,

    String bankName,

    String iban,

    String bic,

    Integer paymentTermDays,

    BigDecimal discountPercent,

    Integer discountDays,

    BigDecimal subtotalNet,

    BigDecimal taxAmount,

    BigDecimal totalGross,

    String notes,

    Boolean archived,

    LocalDateTime archivedAt,

    List<InvoiceItemDto> items
) {
}
