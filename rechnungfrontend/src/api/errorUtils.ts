import axios, { AxiosError } from "axios";

export function getApiErrorMessage(
  error: unknown
): string {

  if (axios.isAxiosError(error)) {

    const axiosError =
      error as AxiosError<any>;

    const data =
      axiosError.response?.data;

    if (data?.detail) {
      return data.detail;
    }

    if (data?.message) {
      return data.message;
    }

    if (data?.title) {
      return data.title;
    }

    return `Backendfehler (${axiosError.response?.status})`;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Unbekannter Fehler";
}
