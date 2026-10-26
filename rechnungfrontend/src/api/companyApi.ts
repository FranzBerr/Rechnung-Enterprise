import axiosInstance from "./axiosInstance";

import type {
  CompanyDto,
} from "../features/company/dto/CompanyDto";

import type {
  CompanyRequest,
} from "../features/company/dto/CompanyRequest";


// =========================================================
// Basis-URL
// =========================================================

// axiosInstance enthält bereits:
//
// http://localhost:8080/api
//
// Daraus wird:
//
// http://localhost:8080/api/companies

const BASE_URL = "/companies";


// =========================================================
// Alle Firmen laden
// =========================================================

// Backend:
// GET /api/companies

export async function getCompanies(): Promise<CompanyDto[]> {
  const response =
    await axiosInstance.get<CompanyDto[]>(
      BASE_URL,
    );

  return response.data;
}


// =========================================================
// Aktive Firmen laden
// =========================================================

// Backend:
// GET /api/companies/active

export async function getActiveCompanies(): Promise<CompanyDto[]> {
  const response =
    await axiosInstance.get<CompanyDto[]>(
      `${BASE_URL}/active`,
    );

  return response.data;
}


// =========================================================
// Firma anhand der ID laden
// =========================================================

// Backend:
// GET /api/companies/{id}

export async function getCompanyById(
  id: number,
): Promise<CompanyDto> {
  const response =
    await axiosInstance.get<CompanyDto>(
      `${BASE_URL}/${id}`,
    );

  return response.data;
}


// =========================================================
// Firma erstellen
// =========================================================

// Backend:
// POST /api/companies

export async function createCompany(
  request: CompanyRequest,
): Promise<CompanyDto> {
  const response =
    await axiosInstance.post<CompanyDto>(
      BASE_URL,
      request,
    );

  return response.data;
}


// =========================================================
// Firma aktualisieren
// =========================================================

// Backend:
// PUT /api/companies/{id}

export async function updateCompany(
  id: number,
  request: CompanyRequest,
): Promise<CompanyDto> {
  const response =
    await axiosInstance.put<CompanyDto>(
      `${BASE_URL}/${id}`,
      request,
    );

  return response.data;
}


// =========================================================
// Firma aktivieren
// =========================================================

// Backend:
// PUT /api/companies/{id}/activate

export async function activateCompany(
  id: number,
): Promise<CompanyDto> {
  const response =
    await axiosInstance.put<CompanyDto>(
      `${BASE_URL}/${id}/activate`,
    );

  return response.data;
}


// =========================================================
// Firma deaktivieren
// =========================================================

// Backend:
// PUT /api/companies/{id}/deactivate

export async function deactivateCompany(
  id: number,
): Promise<CompanyDto> {
  const response =
    await axiosInstance.put<CompanyDto>(
      `${BASE_URL}/${id}/deactivate`,
    );

  return response.data;
}


// =========================================================
// Firma löschen
// =========================================================

// Backend:
// DELETE /api/companies/{id}
//
// Erfolgreiche Antwort:
// HTTP 204 NO CONTENT

export async function deleteCompany(
  id: number,
): Promise<void> {
  await axiosInstance.delete(
    `${BASE_URL}/${id}`,
  );
}
