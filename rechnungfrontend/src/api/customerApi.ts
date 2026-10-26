import axiosInstance from "./axiosInstance";

import type {
  CustomerDto,
} from "../features/customer/dto/CustomerDto";

import type {
  CustomerRequest,
} from "../features/customer/dto/CustomerRequest";


// =========================================================
// Basis-URL
// =========================================================

// axiosInstance enthält bereits:
//
// http://localhost:8080/api
//
// Daraus wird:
//
// http://localhost:8080/api/customers

const BASE_URL = "/customers";


// =========================================================
// Alle Kunden laden
// =========================================================

// Backend:
// GET /api/customers

export async function getCustomers(): Promise<CustomerDto[]> {
  const response =
    await axiosInstance.get<CustomerDto[]>(
      BASE_URL,
    );

  return response.data;
}


// =========================================================
// Aktive Kunden laden
// =========================================================

// Backend:
// GET /api/customers/active

export async function getActiveCustomers(): Promise<CustomerDto[]> {
  const response =
    await axiosInstance.get<CustomerDto[]>(
      `${BASE_URL}/active`,
    );

  return response.data;
}


// =========================================================
// Kunde anhand der ID laden
// =========================================================

// Backend:
// GET /api/customers/{id}

export async function getCustomerById(
  id: number,
): Promise<CustomerDto> {
  const response =
    await axiosInstance.get<CustomerDto>(
      `${BASE_URL}/${id}`,
    );

  return response.data;
}


// =========================================================
// Kunden einer Firma laden
// =========================================================

// Backend:
// GET /api/customers/company/{companyId}

export async function getCustomersByCompanyId(
  companyId: number,
): Promise<CustomerDto[]> {
  const response =
    await axiosInstance.get<CustomerDto[]>(
      `${BASE_URL}/company/${companyId}`,
    );

  return response.data;
}


// =========================================================
// Kunde erstellen
// =========================================================

// Backend:
// POST /api/customers

export async function createCustomer(
  request: CustomerRequest,
): Promise<CustomerDto> {
  const response =
    await axiosInstance.post<CustomerDto>(
      BASE_URL,
      request,
    );

  return response.data;
}


// =========================================================
// Kunde aktualisieren
// =========================================================

// Backend:
// PUT /api/customers/{id}

export async function updateCustomer(
  id: number,
  request: CustomerRequest,
): Promise<CustomerDto> {
  const response =
    await axiosInstance.put<CustomerDto>(
      `${BASE_URL}/${id}`,
      request,
    );

  return response.data;
}


// =========================================================
// Kunde aktivieren
// =========================================================

// Backend:
// PUT /api/customers/{id}/activate

export async function activateCustomer(
  id: number,
): Promise<CustomerDto> {
  const response =
    await axiosInstance.put<CustomerDto>(
      `${BASE_URL}/${id}/activate`,
    );

  return response.data;
}


// =========================================================
// Kunde deaktivieren
// =========================================================

// Backend:
// PUT /api/customers/{id}/deactivate

export async function deactivateCustomer(
  id: number,
): Promise<CustomerDto> {
  const response =
    await axiosInstance.put<CustomerDto>(
      `${BASE_URL}/${id}/deactivate`,
    );

  return response.data;
}


// =========================================================
// Kunde löschen
// =========================================================

// Backend:
// DELETE /api/customers/{id}
//
// Erfolgreich:
// HTTP 204 NO CONTENT

export async function deleteCustomer(
  id: number,
): Promise<void> {
  await axiosInstance.delete(
    `${BASE_URL}/${id}`,
  );
}
