import axiosInstance from "./axiosInstance";

import type {
  BankDto,
} from "../bank/dto/BankDto";
import {BankRequest} from "../types";

const BASE_URL = "/banks";

async function findAll(): Promise<BankDto[]> {
  const response =
    await axiosInstance.get<BankDto[]>(
      BASE_URL,
    );

  return response.data;
}

async function findById(
  id: number,
): Promise<BankDto> {
  const response =
    await axiosInstance.get<BankDto>(
      `${BASE_URL}/${id}`,
    );

  return response.data;
}

async function create(
  request: BankRequest,): Promise<BankDto> {
  const response =
    await axiosInstance.post<BankDto>(
      BASE_URL,
      request,
    );

  return response.data;
}

async function update(
  id: number,
  bank: Omit<BankDto, "id">,
): Promise<BankDto> {
  const response =
    await axiosInstance.put<BankDto>(
      `${BASE_URL}/${id}`,
      bank,
    );

  return response.data;
}

async function remove(
  id: number,
): Promise<void> {
  await axiosInstance.delete(
    `${BASE_URL}/${id}`,
  );
}

export const bankApi = {
  findAll,
  findById,
  create,
  update,
  remove,
};
