export interface Invoice {
  id: number;
  invoiceNumber?: string;
  invoiceDate: string;
  customerName: string;
  status: string;
  totalGross: number;
}

export interface InvoiceItem {
  productId: number;
  description: string;
  quantity: number;
  unitPriceNet: number;
  discountPercent: number;
  vatRate: number;
}
