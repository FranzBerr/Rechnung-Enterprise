import { createTheme } from "@mui/material/styles";

export const appTheme = createTheme({
  palette: {
    mode: "light",

    primary: {
      main: "#1565c0",
      light: "#5e92f3",
      dark: "#003c8f",
      contrastText: "#ffffff",
    },

    secondary: {
      main: "#455a64",
      light: "#718792",
      dark: "#1c313a",
      contrastText: "#ffffff",
    },

    success: {
      main: "#2e7d32",
    },

    warning: {
      main: "#ed6c02",
    },

    error: {
      main: "#d32f2f",
    },

    info: {
      main: "#0288d1",
    },

    background: {
      default: "#f4f6f8",
      paper: "#ffffff",
    },
  },

  shape: {
    borderRadius: 8,
  },

  typography: {
    fontFamily: [
      "Roboto",
      "Helvetica",
      "Arial",
      "sans-serif",
    ].join(","),

    h1: {
      fontWeight: 700,
    },

    h2: {
      fontWeight: 700,
    },

    h3: {
      fontWeight: 700,
    },

    h4: {
      fontWeight: 600,
    },

    h5: {
      fontWeight: 600,
    },

    h6: {
      fontWeight: 600,
    },

    button: {
      textTransform: "none",
      fontWeight: 600,
    },
  },

  components: {
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow:
            "0px 1px 4px rgba(0,0,0,.12)",
        },
      },
    },

    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight:
            "1px solid #e0e0e0",
        },
      },
    },

    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow:
            "0px 2px 8px rgba(0,0,0,.08)",
        },
      },
    },

    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },

    MuiButton: {
      defaultProps: {
        variant: "contained",
      },

      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: "none",
        },
      },
    },

    MuiTextField: {
      defaultProps: {
        fullWidth: true,
        size: "small",
      },
    },

    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 12,
        },
      },
    },

    MuiToolbar: {
      styleOverrides: {
        root: {
          minHeight: "64px",
        },
      },
    },
  },
});
