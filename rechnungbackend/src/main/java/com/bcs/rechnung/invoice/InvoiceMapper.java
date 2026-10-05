package com.bcs.rechnung.invoice;

import com.bcs.rechnung.invoice.dto.InvoiceDto;
import com.bcs.rechnung.invoice.dto.InvoiceItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(
        InvoiceEntity entity
    ) {
        List<InvoiceItemDto> items =
            entity.getItems()
                .stream()
                .map(this::toItemDto)
                .toList();

        return new InvoiceDto(
            entity.getId(),
            entity.getInvoiceNumber(),
            entity.getInvoiceDate(),
            entity.getServiceDate(),
            entity.getDueDate(),
            entity.getStatus(),
            entity.getCompany().getId(),
            entity.getCustomer().getId(),
            entity.getBankAccount().getId(),
            entity.getCustomerNumber(),
            entity.getCustomerName(),
            entity.getCustomerStreet(),
            entity.getCustomerZipCode(),
            entity.getCustomerCity(),
            entity.getCustomerCountry(),
            entity.getCustomerVatId(),
            entity.getBankName(),
            entity.getIban(),
            entity.getBic(),
            entity.getPaymentTermDays(),
            entity.getDiscountPercent(),
            entity.getDiscountDays(),
            entity.getSubtotalNet(),
            entity.getTaxAmount(),
            entity.getTotalGross(),
            entity.getNotes(),
            items
        );
    }

    private InvoiceItemDto toItemDto(
        InvoiceItemEntity entity
    ) {
        return new InvoiceItemDto(
            entity.getId(),
            entity.getPositionNumber(),
            entity.getProductId(),
            entity.getProductNumber(),
            entity.getDescription(),
            entity.getQuantity(),
            entity.getUnit(),
            entity.getUnitPriceNet(),
            entity.getDiscountPercent(),
            entity.getVatRate(),
            entity.getLineNet(),
            entity.getTaxAmount(),
            entity.getLineGross()
        );
    }
}
