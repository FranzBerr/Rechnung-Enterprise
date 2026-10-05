import { BaseApiClient } from "./baseClient";
import type { Product } from "../types/product";

class ProductApi
  extends BaseApiClient<Product> {

  constructor() {
    super("/products");
  }
}

export const productApi =
  new ProductApi();
