import axios from "axios";

import type {
  InternalAxiosRequestConfig,
} from "axios";


// =========================================================
// Konfiguration
// =========================================================

// Beispiel in .env.development:
//
// VITE_API_BASE_URL=http://localhost:8080/api
//
// Ein abschließender Schrägstrich wird entfernt,
// damit keine URLs mit doppeltem Schrägstrich entstehen.

const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ??
  "http://localhost:8080/api"
).replace(/\/+$/, "");


// Maximale Wartezeit eines Requests:
// 30 Sekunden

const REQUEST_TIMEOUT = 30_000;


// =========================================================
// Axios-Instanz
// =========================================================

// Die baseURL enthält bereits "/api".
//
// Die einzelnen API-Module verwenden deshalb:
//
// /companies
// /customers
// /products
// /offers

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,

  timeout: REQUEST_TIMEOUT,

  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});


// =========================================================
// Request-Interceptor
// =========================================================

// Requests werden ausschließlich im Entwicklungsmodus
// in der Browser-Konsole protokolliert.
//
// Fehler werden unverändert weitergegeben.

axiosInstance.interceptors.request.use(
  (
    config: InternalAxiosRequestConfig,
  ) => {
    if (import.meta.env.DEV) {
      const method =
        config.method?.toUpperCase() ??
        "GET";

      const url =
        `${config.baseURL ?? ""}${config.url ?? ""}`;

      console.debug(
        `[API] ${method} ${url}`,
      );
    }

    return config;
  },

  (error: unknown) => {
    if (import.meta.env.DEV) {
      console.error(
        "[API REQUEST ERROR]",
        error,
      );
    }

    return Promise.reject(error);
  },
);


// =========================================================
// Response-Interceptor
// =========================================================

// Erfolgreiche Antworten werden unverändert
// an das aufrufende API-Modul zurückgegeben.
//
// Fehler werden im Entwicklungsmodus protokolliert
// und anschließend unverändert weitergereicht.
//
// Dadurch kann axiosErrorHelper.ts später auf
// error.response.data, error.response.status,
// error.code und error.message zugreifen.

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },

  (error: unknown) => {
    if (
      import.meta.env.DEV &&
      axios.isAxiosError(error)
    ) {
      const method =
        error.config?.method?.toUpperCase() ??
        "UNKNOWN";

      const url =
        `${error.config?.baseURL ?? ""}${error.config?.url ?? ""}`;

      console.error(
        "[API RESPONSE ERROR]",
        {
          status: error.response?.status,
          method,
          url,
          code: error.code,
          message: error.message,
          data: error.response?.data,
        },
      );
    } else if (import.meta.env.DEV) {
      console.error(
        "[API UNKNOWN ERROR]",
        error,
      );
    }

    return Promise.reject(error);
  },
);


export default axiosInstance;
