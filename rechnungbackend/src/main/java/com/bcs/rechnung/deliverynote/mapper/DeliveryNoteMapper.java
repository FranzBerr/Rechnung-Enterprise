package com.bcs.rechnung.deliverynote.mapper;

import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import com.bcs.rechnung.deliverynote.DeliveryNoteItemEntity;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteDto;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteItemDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DeliveryNoteMapper {

    public DeliveryNoteDto toDto(
        DeliveryNoteEntity entity
    ) {
        if (entity == null) {
            return null;
        }

        return new DeliveryNoteDto(
            entity.getId(),
            entity.getDeliveryNoteNumber(),

            entity.getCustomer() != null
                ? entity.getCustomer().getId()
                : null,

            entity.getCustomer() != null
                ? entity.getCustomer().getCustomerNumber()
                : null,

            entity.getCustomer() != null
                ? entity.getCustomer().getName()
                : null,

            entity.getDeliveryDate(),

            entity.getDeliveryName(),
            entity.getDeliveryContactPerson(),
            entity.getDeliveryDepartment(),
            entity.getDeliveryStreet(),
            entity.getDeliveryZipCode(),
            entity.getDeliveryCity(),
            entity.getDeliveryCountry(),

            entity.getOrderReference(),
            entity.getShippingMethod(),
            entity.getTrackingNumber(),
            entity.getNotes(),
            entity.isActive(),

            entity.isArchived(),
            entity.getArchivedAt(),
            entity.getArchivePath(),

            entity.getDeliveryDateSigned(),
            entity.getDeliveryPerson(),
            entity.getDeliverySignature(),

            entity.getReceiptDateSigned(),
            entity.getReceiptPerson(),
            entity.getReceiptSignature(),

            toItemDtos(entity.getItems())
        );
    }

    public DeliveryNoteItemDto toItemDto(
        DeliveryNoteItemEntity entity
    ) {
        if (entity == null) {
            return null;
        }

        return new DeliveryNoteItemDto(
            entity.getId(),
            entity.getPositionNumber(),

            entity.getProduct() != null
                ? entity.getProduct().getId()
                : null,

            entity.getProductNumber(),
            entity.getName(),
            entity.getDescription(),
            entity.getQuantity(),
            entity.getUnit(),
            entity.getNotes()
        );
    }

    public List<DeliveryNoteDto> toDtos(
        List<DeliveryNoteEntity> entities
    ) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities
            .stream()
            .map(this::toDto)
            .toList();
    }

    public List<DeliveryNoteItemDto> toItemDtos(
        List<DeliveryNoteItemEntity> entities
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
