export interface InvoiceItemRequest {
  productId: number;

  description?: string;

  quantity: number;

  unitPriceNet?: number;

  discountPercent?: number;

  vatRate?: number;
}

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
