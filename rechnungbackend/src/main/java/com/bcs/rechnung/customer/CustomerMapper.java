package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;
import org.springframework.stereotype.Component;

/**
 * Mapper für die Konvertierung zwischen
 * CustomerEntity, CustomerDto und CustomerRequest.
 *
 * <p>
 * Der Mapper übernimmt ausschließlich die Übertragung
 * der Daten zwischen den verschiedenen Customer-Modellen.
 * Geschäftslogik und Validierung gehören in den Service.
 * </p>
 */
@Component
public class CustomerMapper {

    /**
     * Konvertiert eine CustomerEntity in ein CustomerDto.
     *
     * <p>
     * Diese Methode wird beispielsweise beim Lesen von Kunden
     * verwendet, bevor die Daten über den REST-Controller
     * an das Frontend zurückgegeben werden.
     * </p>
     *
     * @param entity CustomerEntity
     * @return CustomerDto oder null, wenn entity null ist
     */
    public CustomerDto toDto(
        CustomerEntity entity
    ) {

        if (entity == null) {
            return null;
        }

        return new CustomerDto(

            // -------------------------------------------------
            // Technische Daten
            // -------------------------------------------------

            entity.getId(),

            entity.getCompany() != null
                ? entity.getCompany().getId()
                : null,

            // -------------------------------------------------
            // Kundenstammdaten
            // -------------------------------------------------

            entity.getCustomerNumber(),
            entity.getName(),
            entity.getContactPerson(),

            entity.getStreet(),
            entity.getZipCode(),
            entity.getCity(),
            entity.getCountry(),

            entity.getPhone(),
            entity.getEmail(),
            entity.getVatId(),

            entity.isActive(),

            // -------------------------------------------------
            // XRechnung / elektronische Rechnungsstellung
            // -------------------------------------------------

            entity.getLeitwegId(),
            entity.getElectronicAddress(),
            entity.getElectronicAddressScheme(),
            entity.getBusinessProcessId(),
            entity.getBuyerReference(),
            entity.getDepartment()
        );
    }


    /**
     * Erstellt aus einem CustomerRequest eine neue
     * CustomerEntity.
     *
     * <p>
     * Die zugehörige Firma wird bereits im CustomerServiceImpl
     * geladen und dem Mapper als CompanyEntity übergeben.
     * </p>
     *
     * @param request Daten des neu anzulegenden Kunden
     * @param company zugeordnete Firma
     * @return neue CustomerEntity
     */
    public CustomerEntity toEntity(
        CustomerRequest request,
        CompanyEntity company
    ) {

        if (request == null) {
            return null;
        }

        CustomerEntity entity =
            new CustomerEntity();

        // -----------------------------------------------------
        // Firma
        // -----------------------------------------------------

        entity.setCompany(company);

        // -----------------------------------------------------
        // Kundenstammdaten
        // -----------------------------------------------------

        entity.setCustomerNumber(
            request.customerNumber()
        );

        entity.setName(
            request.name()
        );

        entity.setContactPerson(
            request.contactPerson()
        );

        entity.setStreet(
            request.street()
        );

        entity.setZipCode(
            request.zipCode()
        );

        entity.setCity(
            request.city()
        );

        entity.setCountry(
            request.country()
        );

        entity.setPhone(
            request.phone()
        );

        entity.setEmail(
            request.email()
        );

        entity.setVatId(
            request.vatId()
        );

        entity.setActive(
            request.active()
        );

        // -----------------------------------------------------
        // XRechnung / elektronische Rechnungsstellung
        // -----------------------------------------------------

        entity.setLeitwegId(
            request.leitwegId()
        );

        entity.setElectronicAddress(
            request.electronicAddress()
        );

        entity.setElectronicAddressScheme(
            request.electronicAddressScheme()
        );

        entity.setBusinessProcessId(
            request.businessProcessId()
        );

        entity.setBuyerReference(
            request.buyerReference()
        );

        entity.setDepartment(
            request.department()
        );

        return entity;
    }


    /**
     * Aktualisiert eine bestehende CustomerEntity anhand
     * eines CustomerRequest.
     *
     * <p>
     * Die ID der bestehenden Entity wird ausdrücklich
     * nicht verändert.
     * </p>
     *
     * <p>
     * Die zugeordnete Firma wird durch die vom Service
     * bereits geladene CompanyEntity ersetzt.
     * </p>
     *
     * @param entity bestehende CustomerEntity
     * @param request neue Kundendaten
     * @param company zugeordnete Firma
     */
    public void updateEntity(
        CustomerEntity entity,
        CustomerRequest request,
        CompanyEntity company
    ) {

        if (entity == null || request == null) {
            return;
        }

        // -----------------------------------------------------
        // Firma
        // -----------------------------------------------------

        entity.setCompany(company);

        // -----------------------------------------------------
        // Kundenstammdaten
        // -----------------------------------------------------

        entity.setCustomerNumber(
            request.customerNumber()
        );

        entity.setName(
            request.name()
        );

        entity.setContactPerson(
            request.contactPerson()
        );

        entity.setStreet(
            request.street()
        );

        entity.setZipCode(
            request.zipCode()
        );

        entity.setCity(
            request.city()
        );

        entity.setCountry(
            request.country()
        );

        entity.setPhone(
            request.phone()
        );

        entity.setEmail(
            request.email()
        );

        entity.setVatId(
            request.vatId()
        );

        entity.setActive(
            request.active()
        );

        // -----------------------------------------------------
        // XRechnung / elektronische Rechnungsstellung
        // -----------------------------------------------------

        entity.setLeitwegId(
            request.leitwegId()
        );

        entity.setElectronicAddress(
            request.electronicAddress()
        );

        entity.setElectronicAddressScheme(
            request.electronicAddressScheme()
        );

        entity.setBusinessProcessId(
            request.businessProcessId()
        );

        entity.setBuyerReference(
            request.buyerReference()
        );

        entity.setDepartment(
            request.department()
        );
    }
}
