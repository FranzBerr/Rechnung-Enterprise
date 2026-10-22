import {useEffect, useState} from "react";
import {Customer} from "../../types";
import {customerApi} from "../../api";
import {Button, Stack, Typography} from "@mui/material";

export default function CustomerListPage() {

  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    customerApi.findAll()
      .then((data) => {
        setCustomers(data);
        console.log(data)
      })
      .catch((reason: unknown) => {
        if (reason instanceof Error) {
          setError(reason.message);
          console.log(reason);
        } else {
          setError(String(reason));
        }
      })
      .finally(() => {
        setLoading(false);
      })
  }, []);

  return (
    <Stack spacing={3}>
      <Stack
        direction="row"
        sx={{
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography variant="h4">
          Kunden
        </Typography>
        <Button
          variant="contained"
          color="primary" >
          Neuer Kunde
        </Button>

      </Stack>
    </Stack>
  )
}
