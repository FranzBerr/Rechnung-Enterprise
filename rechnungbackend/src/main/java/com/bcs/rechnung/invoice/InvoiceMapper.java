package com.bcs.rechnung.invoice;

import com.bcs.rechnung.invoice.dto.InvoiceDto;
import com.bcs.rechnung.invoice.dto.InvoiceItemDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(
        InvoiceEntity invoice
    ) {
        if (invoice == null) {
            return null;
        }

        Long companyId =
            invoice.getCompany() != null
                ? invoice.getCompany().getId()
                : null;

        Long customerId =
            invoice.getCustomer() != null
                ? invoice.getCustomer().getId()
                : null;

        Long bankAccountId =
            invoice.getBankAccount() != null
                ? invoice.getBankAccount().getId()
                : null;

        return new InvoiceDto(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate(),
            invoice.getServiceDate(),
            invoice.getDueDate(),
            invoice.getStatus(),

            companyId,
            customerId,
            bankAccountId,

            invoice.getCustomerNumber(),
            invoice.getCustomerName(),
            invoice.getCustomerStreet(),
            invoice.getCustomerZipCode(),
            invoice.getCustomerCity(),
            invoice.getCustomerCountry(),
            invoice.getCustomerVatId(),

            invoice.getBankName(),
            invoice.getIban(),
            invoice.getBic(),

            invoice.getPaymentTermDays(),
            invoice.getDiscountPercent(),
            invoice.getDiscountDays(),

            invoice.getSubtotalNet(),
            invoice.getTaxAmount(),
            invoice.getTotalGross(),

            invoice.getNotes(),

            invoice.getArchived(),
            invoice.getArchivedAt(),

            toItemDtos(
                invoice.getItems()
            )
        );
    }

    public List<InvoiceDto> toDtos(
        List<InvoiceEntity> invoices
    ) {
        if (invoices == null
            || invoices.isEmpty()) {

            return new ArrayList<>();
        }

        return invoices.stream()
            .map(
                this::toDto
            )
            .toList();
    }

    public InvoiceItemDto toItemDto(
        InvoiceItemEntity item
    ) {
        if (item == null) {
            return null;
        }

        Long productId =
            item.getProduct() != null
                ? item.getProduct().getId()
                : null;

        return new InvoiceItemDto(
            item.getId(),
            item.getPositionNumber(),

            productId,

            item.getProductNumber(),
            item.getDescription(),

            item.getQuantity(),
            item.getUnit(),
            item.getUnitPriceNet(),

            item.getDiscountPercent(),

            item.getVatRate(),

            item.getLineNet(),
            item.getTaxAmount(),
            item.getLineGross()
        );
    }

    public List<InvoiceItemDto> toItemDtos(
        List<InvoiceItemEntity> items
    ) {
        if (items == null
            || items.isEmpty()) {

            return new ArrayList<>();
        }

        return items.stream()
            .map(
                this::toItemDto
            )
            .toList();
    }
}
