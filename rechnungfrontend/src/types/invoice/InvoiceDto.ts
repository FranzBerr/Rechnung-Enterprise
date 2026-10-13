import type { InvoiceStatus } from "./InvoiceStatus";
import type { InvoiceItemDto } from "./InvoiceItemDto";

export interface InvoiceDto {
  id: number;

  invoiceNumber?: string;

  customerNumber?: string;

  customerName?: string;

  invoiceDate: string;

  serviceDate?: string;

  customerId: number;

  bankAccountId: number;

  bankName: string;

  iban: string;

  bic: string;

  status: InvoiceStatus;

  paymentTermDays: number;

  dueDate: string;

  discountPercent: number;

  discountDays: number;

  notes?: string;

  subtotalNet: number;

  taxAmount: number;

  totalGross: number;

  items: InvoiceItemDto[];
}
