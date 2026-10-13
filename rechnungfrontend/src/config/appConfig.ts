// export const appConfig = {
//   apiUrl: import.meta.env.VITE_API_URL,
//   applicationName: import.meta.env.VITE_APP_NAME,
//   applicationVersion:
//   import.meta.env.VITE_APP_VERSION,
//   requestTimeout:
//     Number(
//       import.meta.env.VITE_REQUEST_TIMEOUT
//     ) || 30000,
// };


export const appConfig = {
  apiUrl:
    import.meta.env.VITE_API_URL ??
    "http://localhost:8080/api",

  requestTimeout: 30000,
} as const;
