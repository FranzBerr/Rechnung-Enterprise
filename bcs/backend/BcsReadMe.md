# backend:

Konfiguration:

* Java 21
* SQL Server
* Spring Boot
* OpenAPI/Swagger
* Lombok
* JPA
* Validation
* optional Flyway
bereinigten Dependency-Konflikten
minimierten Security-Warnungen
```Bash
rechnungbackend
│
├── pom.xml
│
├── src
│   ├── main
│   │
│   ├── java
│   │   └── com
│   │       └── bcs
│   │           └── rechnung
│   │
│   │               ├── RechnungApplication.java
│   │
│   │               ├── config
│   │               │   ├── OpenApiConfig.java
│   │               │   ├── JacksonConfig.java
│   │               │   └── SecurityConfig.java
│   │               │
│   │               ├── exception
│   │               │   ├── ResourceNotFoundException.java
│   │               │   ├── BusinessException.java
│   │               │   └── GlobalExceptionHandler.java
│   │               │
│   │               ├── bank
│   │               │   ├── BankEntity.java
│   │               │   ├── BankRepository.java
│   │               │   ├── BankService.java
│   │               │   ├── BankServiceImpl.java
│   │               │   ├── BankController.java
│   │               │   ├── BankMapper.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── BankDto.java
│   │               │       └── BankRequest.java
│   │               │
│   │               ├── bank_account
│   │               │   ├── BankAccountEntity.java
│   │               │   ├── BankAccountRepository.java
│   │               │   ├── BankAccountService.java
│   │               │   ├── BankAccountServiceImpl.java
│   │               │   ├── BankAccountController.java
│   │               │   ├── BankAccountMapper.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── BankAccountDto.java
│   │               │       └── BankAccountRequest.java
│   │               │
│   │               ├── company
│   │               │   ├── CompanyEntity.java
│   │               │   ├── CompanyRepository.java
│   │               │   ├── CompanyService.java
│   │               │   ├── CompanyServiceImpl.java
│   │               │   ├── CompanyController.java
│   │               │   ├── CompanyMapper.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── CompanyDto.java
│   │               │       └── CompanyRequest.java
│   │               │
│   │               ├── customer
│   │               │   ├── CustomerEntity.java
│   │               │   ├── CustomerRepository.java
│   │               │   ├── CustomerService.java
│   │               │   ├── CustomerServiceImpl.java
│   │               │   ├── CustomerController.java
│   │               │   ├── CustomerMapper.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── CustomerDto.java
│   │               │       └── CustomerRequest.java
│   │               │
│   │               ├── product
│   │               │   ├── ProductEntity.java
│   │               │   ├── ProductRepository.java
│   │               │   ├── ProductService.java
│   │               │   ├── ProductServiceImpl.java
│   │               │   ├── ProductController.java
│   │               │   ├── ProductMapper.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── ProductDto.java
│   │               │       └── ProductRequest.java
│   │               │
│   │               ├── invoice
│   │               │   ├── InvoiceEntity.java
│   │               │   ├── InvoiceItemEntity.java
│   │               │   ├── InvoiceStatus.java
│   │               │   ├── InvoiceRepository.java
│   │               │   ├── InvoiceItemRepository.java
│   │               │   ├── InvoiceService.java
│   │               │   ├── InvoiceController.java
│   │               │   │
│   │               │   └── dto
│   │               │       ├── InvoiceDto.java
│   │               │       ├── InvoiceRequest.java
│   │               │       ├── InvoiceItemDto.java
│   │               │       └── InvoiceItemRequest.java
│   │               │
│   │               ├── xrechnung
│   │               │   ├── XRechnungService.java
│   │               │   ├── XmlGenerator.java
│   │               │   └── XmlValidator.java
│   │               │
│   │               └── common
│   │                   ├── BaseEntity.java
│   │                   ├── AuditEntity.java
│   │                   └── Constants.java
│   │
│   └── resources
│       ├── application.yml
│       ├── application-local.yml
│       │
│       └── db
│           └── migration
│               ├── V1__company.sql
│               ├── V2__customer.sql
│               ├── V3__product.sql
│               ├── V4__bank.sql
│               ├── V5__bank_account.sql
│               ├── V6__invoice.sql
│               └── V7__invoice_item.sql
│
└── target
```


# Passende URLs
 Bank:
```Code
GET     /api/banks
GET     /api/banks/1
POST    /api/banks
PUT     /api/banks/1
DELETE  /api/banks/1
GET     /api/banks/active
```
Beispiel POST
```Code
{
  "bankCode": "70020270",
  "bankName": "HypoVereinsbank",
  "bic": "HYVEDEMMXXX",
  "country": "DE",
  "active": true
}
```
Beispiel Response
```Code
{
  "id": 1,
  "bankCode": "70020270",
  "bankName": "HypoVereinsbank",
  "bic": "HYVEDEMMXXX",
  "country": "DE",
  "active": true
}
```

```Code
```

```Code
```

```Code
```
