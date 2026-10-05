import { axiosInstance } from "./axios";
import type {
  Customer,
  CustomerRequest,
} from "../types/customer";

import type {
  PageResponse,
} from "../types/common";

export interface CustomerSearchRequest {

  customerNumber?: string;

  companyName?: string;

  city?: string;

  active?: boolean;

  page?: number;

  size?: number;

  sort?: string;
}

class CustomerApi {

  private readonly baseUrl =
    "/customers";

  async findAll(): Promise<Customer[]> {

    const response =
      await axiosInstance.get<Customer[]>(
        this.baseUrl
      );

    return response.data;
  }

  async findById(
    id: number
  ): Promise<Customer> {

    const response =
      await axiosInstance.get<Customer>(
        `${this.baseUrl}/${id}`
      );

    return response.data;
  }

  async create(
    request: CustomerRequest
  ): Promise<Customer> {

    const response =
      await axiosInstance.post<Customer>(
        this.baseUrl,
        request
      );

    return response.data;
  }

  async update(
    id: number,
    request: CustomerRequest
  ): Promise<Customer> {

    const response =
      await axiosInstance.put<Customer>(
        `${this.baseUrl}/${id}`,
        request
      );

    return response.data;
  }

  async delete(
    id: number
  ): Promise<void> {

    await axiosInstance.delete(
      `${this.baseUrl}/${id}`
    );
  }

  async search(
    request: CustomerSearchRequest
  ): Promise<PageResponse<Customer>> {

    const response =
      await axiosInstance.post<
        PageResponse<Customer>
      >(
        `${this.baseUrl}/search`,
        request
      );

    return response.data;
  }

  async activate(
    id: number
  ): Promise<Customer> {

    const response =
      await axiosInstance.put<Customer>(
        `${this.baseUrl}/${id}/activate`
      );

    return response.data;
  }

  async deactivate(
    id: number
  ): Promise<Customer> {

    const response =
      await axiosInstance.put<Customer>(
        `${this.baseUrl}/${id}/deactivate`
      );

    return response.data;
  }
}

export const customerApi =
  new CustomerApi();
