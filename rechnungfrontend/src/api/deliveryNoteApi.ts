import axios from "axios";

export interface DeliveryNoteItemDto {
  id: number;
  positionNumber: number;

  productId: number | null;
  productNumber: string | null;
  productName: string;
  description: string | null;

  quantity: number;
  unit: string;

  notes: string | null;
}

export interface DeliveryNoteDto {
  id: number;

  deliveryNoteNumber: string;

  deliveryNoteDate: string;

  companyId: number;
  customerId: number;

  customerNumber: string;
  customerName: string;

  customerStreet: string | null;
  customerZipCode: string | null;
  customerCity: string | null;
  customerCountry: string | null;

  notes: string | null;

  archived: boolean;
  archivedAt: string | null;
  archivePath: string | null;

  items: DeliveryNoteItemDto[];
}

export interface DeliveryNoteItemRequest {
  positionNumber: number;

  productId?: number | null;
  productNumber?: string | null;
  productName?: string | null;
  description?: string | null;

  quantity: number;
  unit?: string | null;

  notes?: string | null;
}

export interface DeliveryNoteRequest {
  deliveryNoteNumber: string;

  deliveryNoteDate: string;

  companyId: number;
  customerId: number;

  notes?: string | null;

  items: DeliveryNoteItemRequest[];
}

const api = axios.create({
  baseURL: "/api/delivery-notes",
});

export async function findAllDeliveryNotes():
  Promise<DeliveryNoteDto[]> {

  const response =
    await api.get<DeliveryNoteDto[]>("");

  return response.data;
}

export async function findDeliveryNoteById(
  id: number
): Promise<DeliveryNoteDto> {

  const response =
    await api.get<DeliveryNoteDto>(
      `/${id}`
    );

  return response.data;
}

export async function createDeliveryNote(
  request: DeliveryNoteRequest
): Promise<DeliveryNoteDto> {

  const response =
    await api.post<DeliveryNoteDto>(
      "",
      request
    );

  return response.data;
}

export async function updateDeliveryNote(
  id: number,
  request: DeliveryNoteRequest
): Promise<DeliveryNoteDto> {

  const response =
    await api.put<DeliveryNoteDto>(
      `/${id}`,
      request
    );

  return response.data;
}

export async function deleteDeliveryNote(
  id: number
): Promise<void> {

  await api.delete(
    `/${id}`
  );
}

export async function downloadDeliveryNotePdf(
  id: number
): Promise<Blob> {

  const response =
    await api.get(
      `/${id}/pdf`,
      {
        responseType: "blob",
      }
    );

  return response.data;
}

export async function archiveDeliveryNote(
  id: number
): Promise<DeliveryNoteDto> {

  const response =
    await api.post<DeliveryNoteDto>(
      `/${id}/archive`
    );

  return response.data;
}

export async function downloadArchivedDeliveryNotePdf(
  id: number
): Promise<Blob> {

  const response =
    await api.get(
      `/${id}/archive`,
      {
        responseType: "blob",
      }
    );

  return response.data;
}

export async function getDeliveryNoteArchiveStatus(
  id: number
): Promise<boolean> {

  const response =
    await api.get<boolean>(
      `/${id}/archive/status`
    );

  return response.data;
}

export async function undoDeliveryNoteArchive(
  id: number
): Promise<void> {

  await api.delete(
    `/${id}/archive`
  );
}
