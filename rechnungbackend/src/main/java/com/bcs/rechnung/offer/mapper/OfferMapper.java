package com.bcs.rechnung.offer.mapper;

import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.OfferItemEntity;
import com.bcs.rechnung.offer.dto.OfferDto;
import com.bcs.rechnung.offer.dto.OfferItemDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OfferMapper {

    public OfferDto toDto(
        OfferEntity entity
    ) {
        if (entity == null) {
            return null;
        }

        return new OfferDto(
            entity.getId(),
            entity.getOfferNumber(),
            entity.getOfferDate(),
            entity.getValidUntil(),
            entity.getStatus(),

            entity.getCompany() != null
                ? entity.getCompany().getId()
                : null,

            entity.getCustomer() != null
                ? entity.getCustomer().getId()
                : null,

            entity.getCustomerNumber(),
            entity.getCustomerName(),
            entity.getCustomerContactPerson(),
            entity.getCustomerDepartment(),
            entity.getCustomerStreet(),
            entity.getCustomerZipCode(),
            entity.getCustomerCity(),
            entity.getCustomerCountry(),
            entity.getCustomerVatId(),

            entity.getCurrencyCode(),
            entity.getPaymentTermDays(),

            entity.getSubtotalNet(),
            entity.getTaxAmount(),
            entity.getTotalGross(),

            entity.getIntroductionText(),
            entity.getClosingText(),
            entity.getNotes(),

            entity.isActive(),

            entity.isArchived(),
            entity.getArchivedAt(),
            entity.getArchivePath(),

            toItemDtos(
                entity.getItems()
            )
        );
    }

    public OfferItemDto toItemDto(
        OfferItemEntity entity
    ) {
        if (entity == null) {
            return null;
        }

        return new OfferItemDto(
            entity.getId(),
            entity.getPositionNumber(),

            entity.getProduct() != null
                ? entity.getProduct().getId()
                : null,

            entity.getProductNumber(),
            entity.getProductName(),
            entity.getDescription(),

            entity.getQuantity(),
            entity.getUnit(),

            entity.getUnitPriceNet(),
            entity.getDiscountPercent(),
            entity.getVatRate(),

            entity.getLineNet(),
            entity.getTaxAmount(),
            entity.getLineGross(),

            entity.isOptional(),

            entity.getNotes()
        );
    }

    public List<OfferDto> toDtos(
        List<OfferEntity> entities
    ) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities
            .stream()
            .map(this::toDto)
            .toList();
    }

    public List<OfferItemDto> toItemDtos(
        List<OfferItemEntity> entities
    ) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities
            .stream()
            .map(this::toItemDto)
            .toList();
    }
}
