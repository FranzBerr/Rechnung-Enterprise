import axiosInstance from "./axiosInstance";

import type {
  Company,
  CompanyRequest,
} from "../types/company";

class CompanyApi {

  private readonly baseUrl =
    "/companies";

  async findAll(): Promise<Company[]> {

    const response =
      await axiosInstance.get<Company[]>(
        this.baseUrl
      );

    return response.data;
  }

  async findById(
    id: number
  ): Promise<Company> {

    const response =
      await axiosInstance.get<Company>(
        `${this.baseUrl}/${id}`
      );

    return response.data;
  }

  async create(
    request: CompanyRequest
  ): Promise<Company> {

    const response =
      await axiosInstance.post<Company>(
        this.baseUrl,
        request
      );

    return response.data;
  }

  async update(
    id: number,
    request: CompanyRequest
  ): Promise<Company> {

    const response =
      await axiosInstance.put<Company>(
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
}

export const companyApi =
  new CompanyApi();
