export interface Product {

  id: number;

  productNumber: string;

  name: string;

  description: string;

  unit: string;

  netPrice: number;

  vatRate: number;

  active: boolean;
}

export interface ProductRequest {

  productNumber: string;

  name: string;

  description: string;

  unit: string;

  netPrice: number;

  vatRate: number;

  active: boolean;
}
