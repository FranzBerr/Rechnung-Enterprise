import axiosInstance from "./axiosInstance";

import type {
  InvoiceDto,
  InvoiceRequest,
} from "../types/invoice";

export const invoiceApi = {
  async findAll(): Promise<InvoiceDto[]> {
    const response =
      await axiosInstance.get<InvoiceDto[]>(
        "/invoices"
      );

    return response.data;
  },

  async findById(
    id: number
  ): Promise<InvoiceDto> {
    const response =
      await axiosInstance.get<InvoiceDto>(
        `/invoices/${id}`
      );

    return response.data;
  },

  async create(
    request: InvoiceRequest
  ): Promise<InvoiceDto> {
    const response =
      await axiosInstance.post<
        InvoiceDto
      >(
        "/invoices",
        request
      );

    return response.data;
  },

  async update(
    id: number,
    request: InvoiceRequest
  ): Promise<InvoiceDto> {
    const response =
      await axiosInstance.put<
        InvoiceDto
      >(
        `/invoices/${id}`,
        request
      );

    return response.data;
  },

  async delete(
    id: number
  ): Promise<void> {
    await axiosInstance.delete(
      `/invoices/${id}`
    );
  },
};
