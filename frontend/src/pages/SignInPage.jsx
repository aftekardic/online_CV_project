import React from "react";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import LockOpenOutlinedIcon from "@mui/icons-material/LockOpenOutlined";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { toast } from "react-toastify";

function SignInPage() {
  const navigate = useNavigate();

  const handleSignIn = async (event) => {
    event.preventDefault();
    const values = new FormData(event.currentTarget);

    const email = values.get("email");
    const password = values.get("password");

    if (!email || !password) {
      toast.error("Email and password are required.", { autoClose: 1500 });
      return;
    }

    try {
      const response = await api.post("/auth/sign-in", {
        email: email,
        password: password,
      });

      const { data } = response;
      if (data.status === "SUCCESS") {
        localStorage.setItem("accessToken", data.message);
        localStorage.setItem("userEmail", email);

        toast.success("Login successful!", {
          onClose: () => {
            navigate("/dashboard");
          },
          autoClose: 1000,
        });
      } else {
        toast.error("Login failed.", { autoClose: 1500 });
      }
    } catch (error) {
      toast.error(`Login failed. ${error}`, { autoClose: 1500 });
    }
  };

  return (
    <Box
      sx={{
        marginTop: 8,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
        <LockOpenOutlinedIcon />
      </Avatar>
      <Typography component="h1" variant="h5">
        Sign In
      </Typography>
      <Box component="form" onSubmit={handleSignIn} noValidate sx={{ mt: 1 }}>
        <TextField
          margin="normal"
          required
          fullWidth
          id="email"
          label="Email Address"
          name="email"
          autoComplete="email"
          autoFocus
        />
        <TextField
          margin="normal"
          required
          fullWidth
          name="password"
          label="Password"
          type="password"
          id="password"
          autoComplete="current-password"
        />
        <Button
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
        >
          Sign In
        </Button>
        <Grid item>
          <Link href="/" variant="body2">
            {"Don't have an account? Sign Up"}
          </Link>
        </Grid>
      </Box>
    </Box>
  );
}

export default SignInPage;
