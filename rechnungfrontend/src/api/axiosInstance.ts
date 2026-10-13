import axios, {
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 30000,

  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use(
  (
    config: InternalAxiosRequestConfig
  ) => {
    console.log(
      `[API] ${config.method?.toUpperCase()} ${config.url}`
    );

    return config;
  },
  (error: unknown) => {
    console.error(
      "[REQUEST ERROR]",
      error
    );

    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (
    response: AxiosResponse
  ) => {
    return response;
  },
  (
    error: AxiosError
  ) => {
    console.error(
      "[RESPONSE ERROR]",
      error.response?.status,
      error.response?.data
    );

    return Promise.reject(error);
  }
);

export default axiosInstance;
