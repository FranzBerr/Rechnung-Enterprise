
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import {
  CssBaseline,
  ThemeProvider,
} from "@mui/material";

import { BrowserRouter } from "react-router-dom";

import App from "./App";
import { appTheme } from "./theme/appTheme";

createRoot(
  document.getElementById("root")!
).render(
  <StrictMode>
    <ThemeProvider theme={appTheme}>
      <CssBaseline />

      <BrowserRouter>
        <App />
      </BrowserRouter>

    </ThemeProvider>
  </StrictMode>
);
