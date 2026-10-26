import type {
  ReactNode,
} from "react";

import {
  Box,
} from "@mui/material";

import Header from "./Header";
import Sidebar from "./Sidebar";

const HEADER_HEIGHT = 64;
const SIDEBAR_WIDTH = 260;

interface AppLayoutProps {
  children: ReactNode;
}

export default function AppLayout({
                                    children,
                                  }: AppLayoutProps) {
  return (
    <Box
      sx={{
        minHeight: "100vh",
        bgcolor: "background.default",
      }}
    >
      <Header
        height={HEADER_HEIGHT}
      />

      <Sidebar
        headerHeight={HEADER_HEIGHT}
        width={SIDEBAR_WIDTH}
      />

      <Box
        component="main"
        sx={{
          ml: `${SIDEBAR_WIDTH}px`,
          pt: `${HEADER_HEIGHT}px`,
          minHeight: "100vh",
          bgcolor: "background.default",
        }}
      >
        <Box
          sx={{
            p: {
              xs: 2,
              md: 3,
            },
          }}
        >
          {children}
        </Box>
      </Box>
    </Box>
  );
}
