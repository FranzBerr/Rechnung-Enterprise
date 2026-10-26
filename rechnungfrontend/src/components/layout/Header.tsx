import {
  AppBar,
  Box,
  Toolbar,
  Typography,
} from "@mui/material";

interface HeaderProps {
  height: number;
}

export default function Header({
                                 height,
                               }: HeaderProps) {
  return (
    <AppBar
      position="fixed"
      elevation={1}
      sx={{
        zIndex: (theme) =>
          theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar
        sx={{
          minHeight: `${height}px !important`,
          px: 3,
          justifyContent: "space-between",
        }}
      >
        <Box>
          <Typography
            variant="h6"
            component="div"
            sx={{
              fontWeight: 700,
              lineHeight: 1.2,
            }}
          >
            Rechnung Enterprise
          </Typography>

          <Typography
            variant="caption"
            sx={{
              color:
                "rgba(255,255,255,0.75)",
            }}
          >
            Rechnungen, Angebote und Lieferscheine
          </Typography>
        </Box>

        <Typography
          variant="body2"
          sx={{
            fontWeight: 600,
          }}
        >
          BCS GmbH
        </Typography>
      </Toolbar>
    </AppBar>
  );
}
