import axiosInstance from "./axiosInstance";

import type {
  OfferDto,
} from "../features/offer/dto/OfferDto";

import type {
  OfferRequest,
} from "../features/offer/dto/OfferRequest";


// =========================================================
// Basis-URL
// =========================================================

const BASE_URL =
  "/offers";


// =========================================================
// Alle Angebote laden
// =========================================================

export async function getOffers():
  Promise<OfferDto[]> {

  const response =
    await axiosInstance.get<OfferDto[]>(
      BASE_URL,
    );

  return response.data;
}


// =========================================================
// Angebot über ID laden
// =========================================================

export async function getOfferById(
  id: number,
): Promise<OfferDto> {

  const response =
    await axiosInstance.get<OfferDto>(
      `${BASE_URL}/${id}`,
    );

  return response.data;
}


// =========================================================
// Angebot erstellen
// =========================================================

export async function createOffer(
  request: OfferRequest,
): Promise<OfferDto> {

  const response =
    await axiosInstance.post<OfferDto>(
      BASE_URL,
      request,
    );

  return response.data;
}


// =========================================================
// Angebot ändern
// =========================================================

export async function updateOffer(
  id: number,
  request: OfferRequest,
): Promise<OfferDto> {

  const response =
    await axiosInstance.put<OfferDto>(
      `${BASE_URL}/${id}`,
      request,
    );

  return response.data;
}


// =========================================================
// Angebot löschen
// =========================================================

export async function deleteOffer(
  id: number,
): Promise<void> {

  await axiosInstance.delete(
    `${BASE_URL}/${id}`,
  );
}


// =========================================================
// Aktuelles Angebots-PDF herunterladen
// =========================================================

export async function downloadOfferPdf(
  id: number,
): Promise<Blob> {

  const response =
    await axiosInstance.get<Blob>(
      `${BASE_URL}/${id}/pdf`,
      {
        responseType:
          "blob",

        headers: {
          Accept:
            "application/pdf",
        },
      },
    );

  return response.data;
}


// =========================================================
// Angebot archivieren
// =========================================================

export async function archiveOffer(
  id: number,
): Promise<OfferDto> {

  const response =
    await axiosInstance.post<OfferDto>(
      `${BASE_URL}/${id}/archive`,
    );

  return response.data;
}


// =========================================================
// Archiviertes Angebots-PDF herunterladen
// =========================================================

export async function downloadArchivedOfferPdf(
  id: number,
): Promise<Blob> {

  const response =
    await axiosInstance.get<Blob>(
      `${BASE_URL}/${id}/archive/pdf`,
      {
        responseType:
          "blob",

        headers: {
          Accept:
            "application/pdf",
        },
      },
    );

  return response.data;
}


// =========================================================
// Archivierung zurücksetzen
// =========================================================

export async function undoOfferArchive(
  id: number,
): Promise<void> {

  await axiosInstance.delete(
    `${BASE_URL}/${id}/archive`,
  );
}
