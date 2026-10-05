export type InvoiceStatus =
  | "DRAFT"
  | "CREATED"
  | "SENT"
  | "PAID"
  | "CANCELLED";

// Position
export interface InvoiceItem {

  id?: number;

  positionNumber?: number;

  productId: number;

  productNumber?: string;

  description: string;

  quantity: number;

  unit?: string;

  unitPriceNet: number;

  discountPercent: number;

  vatRate: number;

  lineNet?: number;

  taxAmount?: number;

  lineGross?: number;
}

// Request Position
export interface InvoiceItemRequest {

  productId: number;

  description?: string;

  quantity: number;

  unitPriceNet?: number;

  discountPercent?: number;

  vatRate?: number;
}
// Invoice
export interface Invoice {

  id: number;

  invoiceNumber?: string;

  invoiceDate: string;

  serviceDate?: string;

  customerId: number;

  customerName: string;

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

  items: InvoiceItem[];
}

// Create Request
// import type {
//   InvoiceItemRequest
// } from "./invoice";

export interface InvoiceRequest {

  invoiceDate: string;

  serviceDate?: string;

  customerId: number;

  bankAccountId: number;

  paymentTermDays: number;

  discountPercent: number;

  discountDays: number;

  notes?: string;

  items: InvoiceItemRequest[];
}

// Create Request
// import type {
//   InvoiceItemRequest
// } from "./invoice";

export interface InvoiceRequest {

  invoiceDate: string;

  serviceDate?: string;

  customerId: number;

  bankAccountId: number;

  paymentTermDays: number;

  discountPercent: number;

  discountDays: number;

  notes?: string;

  items: InvoiceItemRequest[];
}
