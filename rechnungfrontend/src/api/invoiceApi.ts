import { axiosInstance } from "./axios";

import type {
  Invoice,
  InvoiceRequest,
} from "../types/invoice";

class InvoiceApi {

  async findAll(): Promise<Invoice[]> {

    const response =
      await axiosInstance.get<
        Invoice[]
      >("/invoices");

    return response.data;
  }

  async findById(
    id: number
  ): Promise<Invoice> {

    const response =
      await axiosInstance.get<
        Invoice
      >(`/invoices/${id}`);

    return response.data;
  }

  async create(
    request: InvoiceRequest
  ): Promise<Invoice> {

    const response =
      await axiosInstance.post<
        Invoice
      >(
        "/invoices",
        request
      );

    return response.data;
  }

  async complete(
    id: number
  ): Promise<Invoice> {

    const response =
      await axiosInstance.post<
        Invoice
      >(
        `/invoices/${id}/complete`
      );

    return response.data;
  }

  getPdfUrl(
    id: number
  ): string {

    return `${axiosInstance.defaults.baseURL}/invoices/${id}/pdf`;
  }
}

export const invoiceApi =
  new InvoiceApi();
