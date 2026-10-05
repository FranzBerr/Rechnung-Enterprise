import axios, {
  AxiosError,
  AxiosInstance,
  AxiosResponse,
} from "axios";
import { appConfig } from "../config/appConfig";

export interface ApiError {
  timestamp?: string;
  title?: string;
  detail?: string;
  message?: string;
  status?: number;
  errors?: Record<string, string>;
}

const API_BASE_URL =
  import.meta.env.VITE_API_URL ??
  "http://localhost:8080/api";

export const axiosInstance = axios.create({
  baseURL: appConfig.apiUrl,
  timeout: appConfig.requestTimeout,
  headers: {
    "Content-Type": "application/json",
  },
});;

axiosInstance.interceptors.request.use(
  (config) => {

    console.log(
      `[API] ${config.method?.toUpperCase()} ${config.url}`,
      config.data
    );

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError<ApiError>) => {

    console.error(
      "[API ERROR]",
      error.response?.status,
      error.response?.data
    );

    return Promise.reject(error);
  }
);
