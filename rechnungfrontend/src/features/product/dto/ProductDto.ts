export interface ProductDto {
  id: number;
  companyId: number;
  productNumber: string;
  name: string;
  description: string;
  unit: string;
  netPrice: number;
  vatRate: number;
  active: boolean;
}
