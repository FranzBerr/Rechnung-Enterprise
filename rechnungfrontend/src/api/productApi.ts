import axiosInstance from "./axiosInstance";

import type {
  ProductDto,
} from "../features/product/dto/ProductDto";

import type {
  ProductRequest,
} from "../features/product/dto/ProductRequest";


// =========================================================
// Basis-URL
// =========================================================

// axiosInstance enthält bereits:
//
// http://localhost:8080/api
//
// Daraus wird:
//
// http://localhost:8080/api/products

const BASE_URL = "/products";


// =========================================================
// Alle Produkte laden
// =========================================================

// Backend:
// GET /api/products

export async function getProducts(): Promise<ProductDto[]> {
  const response =
    await axiosInstance.get<ProductDto[]>(
      BASE_URL,
    );

  return response.data;
}


// =========================================================
// Aktive Produkte laden
// =========================================================

// Backend:
// GET /api/products/active

export async function getActiveProducts(): Promise<ProductDto[]> {
  const response =
    await axiosInstance.get<ProductDto[]>(
      `${BASE_URL}/active`,
    );

  return response.data;
}


// =========================================================
// Produkt anhand der ID laden
// =========================================================

// Backend:
// GET /api/products/{id}

export async function getProductById(
  id: number,
): Promise<ProductDto> {
  const response =
    await axiosInstance.get<ProductDto>(
      `${BASE_URL}/${id}`,
    );

  return response.data;
}


// =========================================================
// Produkte einer Firma laden
// =========================================================

// Backend:
// GET /api/products/company/{companyId}

export async function getProductsByCompanyId(
  companyId: number,
): Promise<ProductDto[]> {
  const response =
    await axiosInstance.get<ProductDto[]>(
      `${BASE_URL}/company/${companyId}`,
    );

  return response.data;
}


// =========================================================
// Produkt erstellen
// =========================================================

// Backend:
// POST /api/products

export async function createProduct(
  request: ProductRequest,
): Promise<ProductDto> {
  const response =
    await axiosInstance.post<ProductDto>(
      BASE_URL,
      request,
    );

  return response.data;
}


// =========================================================
// Produkt aktualisieren
// =========================================================

// Backend:
// PUT /api/products/{id}

export async function updateProduct(
  id: number,
  request: ProductRequest,
): Promise<ProductDto> {
  const response =
    await axiosInstance.put<ProductDto>(
      `${BASE_URL}/${id}`,
      request,
    );

  return response.data;
}


// =========================================================
// Produkt aktivieren
// =========================================================

// Backend:
// PUT /api/products/{id}/activate

export async function activateProduct(
  id: number,
): Promise<ProductDto> {
  const response =
    await axiosInstance.put<ProductDto>(
      `${BASE_URL}/${id}/activate`,
    );

  return response.data;
}


// =========================================================
// Produkt deaktivieren
// =========================================================

// Backend:
// PUT /api/products/{id}/deactivate

export async function deactivateProduct(
  id: number,
): Promise<ProductDto> {
  const response =
    await axiosInstance.put<ProductDto>(
      `${BASE_URL}/${id}/deactivate`,
    );

  return response.data;
}


// =========================================================
// Produkt löschen
// =========================================================

// Backend:
// DELETE /api/products/{id}
//
// Erfolgreich:
// HTTP 204 NO CONTENT

export async function deleteProduct(
  id: number,
): Promise<void> {
  await axiosInstance.delete(
    `${BASE_URL}/${id}`,
  );
}
