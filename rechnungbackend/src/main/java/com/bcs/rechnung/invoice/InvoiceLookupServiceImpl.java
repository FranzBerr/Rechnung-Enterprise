//package com.bcs.rechnung.invoice;
//
//import com.bcs.rechnung.exception.ResourceNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//
////---------------------------------------------
////    Hole die Rechnungsnummer nur einmal ab im
////    InvoiceXmlServiceImpl und im InvoicePdfServiceImpl
////---------------------------------------------
//
//@Service
//@RequiredArgsConstructor
//public class InvoiceLookupServiceImpl
//    implements InvoiceLookupService {
//
//    private final InvoiceRepository invoiceRepository;
//
//    @Override
//    public InvoiceEntity getInvoice(
//        Long invoiceId
//    ) {
//        return invoiceRepository.findById(invoiceId)
//            .orElseThrow(() ->
//                new ResourceNotFoundException(
//                    "Rechnung nicht gefunden: "
//                        + invoiceId
//                )
//            );
//    }
//
//    @Override
//    public String getInvoiceNumber(
//        Long invoiceId
//    ) {
//        return getInvoice(invoiceId)
//            .getInvoiceNumber();
//    }
//}
