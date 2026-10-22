package com.bcs.rechnung.deliverynote.service;

import com.bcs.rechnung.customer.CustomerEntity;
import com.bcs.rechnung.customer.CustomerRepository;
import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import com.bcs.rechnung.deliverynote.DeliveryNoteItemEntity;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteDto;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteItemRequest;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteRequest;
import com.bcs.rechnung.deliverynote.mapper.DeliveryNoteMapper;
import com.bcs.rechnung.deliverynote.repository.DeliveryNoteRepository;
import com.bcs.rechnung.product.ProductEntity;
import com.bcs.rechnung.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryNoteServiceImpl
    implements DeliveryNoteService {

    private final DeliveryNoteRepository deliveryNoteRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final DeliveryNoteMapper deliveryNoteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryNoteDto> findAll() {
        return deliveryNoteMapper.toDtos(
            deliveryNoteRepository
                .findAllByOrderByDeliveryDateDesc()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryNoteDto findById(Long id) {
        return deliveryNoteMapper.toDto(
            findEntityById(id)
        );
    }

    @Override
    public DeliveryNoteDto create(
        DeliveryNoteRequest request
    ) {
        validateRequest(request);

        validateDeliveryNoteNumber(
            null,
            request.deliveryNoteNumber()
        );

        val customer =
            findCustomer(request.customerId());

        val entity =
            new DeliveryNoteEntity();

        entity.setDeliveryNoteNumber(
            request.deliveryNoteNumber()
        );

        entity.setDeliveryDate(
            request.deliveryDate()
        );

        entity.setCustomer(customer);

        applyHeader(
            entity,
            request
        );

        applyAddress(
            entity,
            request,
            customer
        );

        replaceItems(
            entity,
            request.items()
        );

        val saved =
            deliveryNoteRepository.save(entity);

        return deliveryNoteMapper.toDto(saved);
    }

    @Override
    public DeliveryNoteDto update(
        Long id,
        DeliveryNoteRequest request
    ) {
        validateRequest(request);

        val entity =
            findEntityById(id);

        validateNotArchived(entity);

        validateDeliveryNoteNumber(
            id,
            request.deliveryNoteNumber()
        );

        val customer =
            findCustomer(request.customerId());

        entity.setDeliveryNoteNumber(
            request.deliveryNoteNumber()
        );

        entity.setDeliveryDate(
            request.deliveryDate()
        );

        entity.setCustomer(customer);

        applyHeader(
            entity,
            request
        );

        applyAddress(
            entity,
            request,
            customer
        );

        replaceItems(
            entity,
            request.items()
        );

        val saved =
            deliveryNoteRepository.save(entity);

        return deliveryNoteMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        val entity =
            findEntityById(id);

        validateNotArchived(entity);

        deliveryNoteRepository.delete(entity);
    }

    private void applyHeader(
        DeliveryNoteEntity entity,
        DeliveryNoteRequest request
    ) {
        entity.setOrderReference(
            request.orderReference()
        );

        entity.setShippingMethod(
            request.shippingMethod()
        );

        entity.setTrackingNumber(
            request.trackingNumber()
        );

        entity.setNotes(
            request.notes()
        );

        entity.setActive(
            request.active()
        );

        entity.setDeliveryDateSigned(
            request.deliveryDateSigned()
        );

        entity.setDeliveryPerson(
            request.deliveryPerson()
        );

        entity.setDeliverySignature(
            request.deliverySignature()
        );

        entity.setReceiptDateSigned(
            request.receiptDateSigned()
        );

        entity.setReceiptPerson(
            request.receiptPerson()
        );

        entity.setReceiptSignature(
            request.receiptSignature()
        );
    }

    private void applyAddress(
        DeliveryNoteEntity entity,
        DeliveryNoteRequest request,
        CustomerEntity customer
    ) {
        entity.setDeliveryName(
            fallback(
                request.deliveryName(),
                customer.getName()
            )
        );

        entity.setDeliveryContactPerson(
            fallback(
                request.deliveryContactPerson(),
                customer.getContactPerson()
            )
        );

        entity.setDeliveryDepartment(
            fallback(
                request.deliveryDepartment(),
                customer.getDepartment()
            )
        );

        entity.setDeliveryStreet(
            fallback(
                request.deliveryStreet(),
                customer.getStreet()
            )
        );

        entity.setDeliveryZipCode(
            fallback(
                request.deliveryZipCode(),
                customer.getZipCode()
            )
        );

        entity.setDeliveryCity(
            fallback(
                request.deliveryCity(),
                customer.getCity()
            )
        );

        entity.setDeliveryCountry(
            fallback(
                request.deliveryCountry(),
                customer.getCountry()
            )
        );
    }

    private void replaceItems(
        DeliveryNoteEntity entity,
        List<DeliveryNoteItemRequest> requests
    ) {
        entity.clearItems();

        if (requests == null) {
            return;
        }

        requests.stream()
            .map(this::createItem)
            .forEach(entity::addItem);
    }

    private DeliveryNoteItemEntity createItem(
        DeliveryNoteItemRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "DeliveryNoteItemRequest darf nicht null sein."
            );
        }

        val item =
            new DeliveryNoteItemEntity();

        item.setPositionNumber(
            request.positionNumber()
        );

        item.setQuantity(
            request.quantity()
        );

        item.setNotes(
            request.notes()
        );

        if (request.productId() != null) {
            applyProduct(
                item,
                request
            );
        } else {
            applyFreeItem(
                item,
                request
            );
        }

        return item;
    }

    private void applyProduct(
        DeliveryNoteItemEntity item,
        DeliveryNoteItemRequest request
    ) {
        val product =
            findProduct(request.productId());

        item.setProduct(product);

        item.setProductNumber(
            product.getProductNumber()
        );

        item.setName(
            product.getName()
        );

        item.setDescription(
            fallback(
                request.description(),
                product.getDescription()
            )
        );

        item.setUnit(
            fallback(
                request.unit(),
                product.getUnit()
            )
        );
    }

    private void applyFreeItem(
        DeliveryNoteItemEntity item,
        DeliveryNoteItemRequest request
    ) {
        item.setProduct(null);

        item.setProductNumber(
            request.productNumber()
        );

        item.setName(
            request.name()
        );

        item.setDescription(
            request.description()
        );

        item.setUnit(
            request.unit()
        );
    }

    private DeliveryNoteEntity findEntityById(
        Long id
    ) {
        validateId(
            id,
            "Lieferschein-ID"
        );

        return deliveryNoteRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Lieferschein mit ID "
                        + id
                        + " nicht gefunden."
                )
            );
    }

    private CustomerEntity findCustomer(
        Long id
    ) {
        validateId(
            id,
            "Kunden-ID"
        );

        return customerRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Kunde mit ID "
                        + id
                        + " nicht gefunden."
                )
            );
    }

    private ProductEntity findProduct(
        Long id
    ) {
        validateId(
            id,
            "Produkt-ID"
        );

        return productRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException(
                    "Produkt mit ID "
                        + id
                        + " nicht gefunden."
                )
            );
    }

    private void validateRequest(
        DeliveryNoteRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "DeliveryNoteRequest darf nicht null sein."
            );
        }

        if (request.customerId() == null) {
            throw new IllegalArgumentException(
                "customerId darf nicht null sein."
            );
        }

        if (request.deliveryDate() == null) {
            throw new IllegalArgumentException(
                "deliveryDate darf nicht null sein."
            );
        }
    }

    private void validateDeliveryNoteNumber(
        Long currentId,
        String number
    ) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException(
                "Lieferscheinnummer darf nicht leer sein."
            );
        }

        deliveryNoteRepository
            .findByDeliveryNoteNumber(number)
            .filter(existing ->
                currentId == null
                    || !existing
                    .getId()
                    .equals(currentId)
            )
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Lieferscheinnummer "
                        + number
                        + " existiert bereits."
                );
            });
    }

    private void validateNotArchived(
        DeliveryNoteEntity entity
    ) {
        if (entity.isArchived()) {
            throw new IllegalStateException(
                "Der Lieferschein "
                    + entity.getDeliveryNoteNumber()
                    + " ist bereits archiviert und darf "
                    + "nicht mehr geändert oder gelöscht werden."
            );
        }
    }

    private void validateId(
        Long id,
        String fieldName
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                fieldName
                    + " muss größer als 0 sein."
            );
        }
    }

    private String fallback(
        String value,
        String fallback
    ) {
        return value == null
            || value.isBlank()
            ? fallback
            : value;
    }
}
