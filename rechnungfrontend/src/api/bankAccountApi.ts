import { axiosInstance } from "./axios";

import type {
  BankAccount,
  BankAccountRequest,
} from "../types/bankAccount";

class BankAccountApi {

  private readonly baseUrl =
    "/bank-accounts";

  async findAll(): Promise<BankAccount[]> {

    const response =
      await axiosInstance.get<BankAccount[]>(
        this.baseUrl
      );

    return response.data;
  }

  async findById(
    id: number
  ): Promise<BankAccount> {

    const response =
      await axiosInstance.get<BankAccount>(
        `${this.baseUrl}/${id}`
      );

    return response.data;
  }

  async create(
    request: BankAccountRequest
  ): Promise<BankAccount> {

    const response =
      await axiosInstance.post<BankAccount>(
        this.baseUrl,
        request
      );

    return response.data;
  }

  async update(
    id: number,
    request: BankAccountRequest
  ): Promise<BankAccount> {

    const response =
      await axiosInstance.put<BankAccount>(
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

  async findDefault(): Promise<BankAccount> {

    const response =
      await axiosInstance.get<BankAccount>(
        `${this.baseUrl}/default`
      );

    return response.data;
  }

  async activate(
    id: number
  ): Promise<BankAccount> {

    const response =
      await axiosInstance.put<BankAccount>(
        `${this.baseUrl}/${id}/activate`
      );

    return response.data;
  }

  async deactivate(
    id: number
  ): Promise<BankAccount> {

    const response =
      await axiosInstance.put<BankAccount>(
        `${this.baseUrl}/${id}/deactivate`
      );

    return response.data;
  }
}

export const bankAccountApi =
  new BankAccountApi();
