import axiosInstance from "./axiosInstance";

import type {
  BankAccount,
  BankAccountRequest,
} from "../types";
import {BankAccountDto} from "../bankAccount/dto/BankAccountDto";


class BankAccountApi {

  private readonly baseUrl =
    "/bank-accounts";

  async findAll(): Promise<BankAccountDto[]> {

    const response =
      await axiosInstance.get<BankAccountDto[]>(
        this.baseUrl
      );

    return response.data;
  }

  async findById(
    id: number
  ): Promise<BankAccountDto> {

    const response =
      await axiosInstance.get<BankAccountDto>(
        `${this.baseUrl}/${id}`
      );

    return response.data;
  }

  async create(
    request: BankAccountRequest
  ): Promise<BankAccountDto> {

    const response =
      await axiosInstance.post<BankAccountDto>(
        this.baseUrl,
        request
      );

    return response.data;
  }

  async update(
    id: number,
    request: BankAccountRequest
  ): Promise<BankAccountDto> {

    const response =
      await axiosInstance.put<BankAccountDto>(
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

  async findDefault(): Promise<BankAccountDto> {

    const response =
      await axiosInstance.get<BankAccountDto>(
        `${this.baseUrl}/default`
      );

    return response.data;
  }

  async activate(
    id: number
  ): Promise<BankAccountDto> {

    const response =
      await axiosInstance.put<BankAccountDto>(
        `${this.baseUrl}/${id}/activate`
      );

    return response.data;
  }

  async deactivate(
    id: number
  ): Promise<BankAccountDto> {

    const response =
      await axiosInstance.put<BankAccountDto>(
        `${this.baseUrl}/${id}/deactivate`
      );

    return response.data;
  }
}

export const bankAccountApi =
  new BankAccountApi();
