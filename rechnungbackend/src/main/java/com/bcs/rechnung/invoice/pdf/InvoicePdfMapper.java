package com.bcs.rechnung.invoice.pdf;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceItemEntity;
import com.bcs.rechnung.invoice.pdf.dto.InvoicePdfDto;
import com.bcs.rechnung.invoice.pdf.dto.InvoicePdfItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoicePdfMapper {

    public InvoicePdfDto toDto(
        InvoiceEntity invoice
    ) {
        if (invoice == null) {
            throw new IllegalArgumentException(
                "Die Rechnung darf nicht null sein."
            );
        }

        CompanyEntity company =
            invoice.getCompany();

        if (company == null) {
            throw new IllegalArgumentException(
                "Die Firmendaten fehlen."
            );
        }

        List<InvoicePdfItemDto> items =
            invoice.getItems()
                .stream()
                .map(this::toItemDto)
                .toList();

        return new InvoicePdfDto(
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate(),
            invoice.getServiceDate(),
            invoice.getDueDate(),
            invoice.getCustomerNumber(),
            invoice.getCustomerName(),
            invoice.getCustomerStreet(),
            invoice.getCustomerZipCode(),
            invoice.getCustomerCity(),
            invoice.getCustomerCountry(),
            company.getName(),
            company.getStreet(),
            company.getZipCode(),
            company.getCity(),
            company.getCountry(),
            company.getVatId(),
            company.getTaxNumber(),
            invoice.getBankName(),
            invoice.getIban(),
            invoice.getBic(),
            invoice.getPaymentReference(),
            invoice.getOrderReference(),
            invoice.getNotes(),
            invoice.getSubtotalNet(),
            invoice.getTaxAmount(),
            invoice.getTotalGross(),
            items
        );
    }

    private InvoicePdfItemDto toItemDto(
        InvoiceItemEntity item
    ) {
        return new InvoicePdfItemDto(
            item.getPositionNumber(),
            item.getProductNumber(),
            item.getDescription(),
            item.getQuantity(),
            item.getUnit(),
            item.getUnitPriceNet(),
            item.getVatRate(),
            item.getLineNet(),
            item.getTaxAmount(),
            item.getLineGross()
        );
    }
}
