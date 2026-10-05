//package com.bcs.rechnung.invoice;
//
//import com.bcs.rechnung.bank_account.BankAccountRepository;
//import com.bcs.rechnung.customer.*;
//import com.bcs.rechnung.invoice.dto.*;
//import com.bcs.rechnung.product.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class InvoiceServiceTest {
//
//    @Mock InvoiceRepository invoices;
//    @Mock CustomerRepository customers;
//    @Mock
//    BankAccountRepository banks;
//    @Mock ProductRepository products;
//    @InjectMocks InvoiceService service;
//
//    @Test
//    void calculatesInvoiceTotals() {
//        CustomerEntity customer = new CustomerEntity();
//        customer.setId(1L);
//        customer.setCompanyName("Testkunde");
//        BankAccount bank = new BankAccount();
//        bank.setId(1L);
//        bank.setBankName("Sparkasse");
//        ProductEntity product = new ProductEntity();
//        product.setId(1L);
//        product.setDescription("Beratung");
//        product.setUnit("STUNDE");
//        product.setNetPrice(new BigDecimal("100"));
//        product.setVatRate(new BigDecimal("19"));
//
//        when(customers.findById(1L)).thenReturn(Optional.of(customer));
//        when(banks.findById(1L)).thenReturn(Optional.of(bank));
//        when(products.findById(1L)).thenReturn(Optional.of(product));
//        when(invoices.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        InvoiceRequest request = new InvoiceRequest(
//                LocalDate.of(2026, 9, 29), null, 1L, 1L, 14,
//                BigDecimal.ZERO, 0, "Test",
//                List.of(new InvoiceItemRequest(
//                        1L, null, new BigDecimal("2"), null,
//                        new BigDecimal("10"), null)));
//
//        InvoiceEntity result = service.create(request);
//
//        assertThat(result.getSubtotalNet()).isEqualByComparingTo("180.00");
//        assertThat(result.getTaxAmount()).isEqualByComparingTo("34.20");
//        assertThat(result.getTotalGross()).isEqualByComparingTo("214.20");
//    }
//}
