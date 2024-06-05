import React, { useEffect, useState } from "react";
import {
  Container,
  TextField,
  Button,
  Grid,
  Box,
  Typography,
  IconButton,
} from "@mui/material";
import LockOpenOutlinedIcon from "@mui/icons-material/LockOpenOutlined";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import api from "../services/api";
import { toast } from "react-toastify";

function SettingsPage() {
  const [count, setCount] = useState(0);
  const [formData, setFormData] = useState({
    email: "",
    given_name: "",
    family_name: "",
    password: "",
  });
  const [changePw, setChangePw] = useState(false);
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleChangeStatusOfChangePw = (e) => {
    setCount(count + 1);
    if (count % 2 === 0) {
      setChangePw(true);
    } else {
      setChangePw(false);
      setFormData((prevFormData) => {
        const { password, ...updatedFormData } = prevFormData;
        return updatedFormData;
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const toastId = toast("Updating...", { autoClose: false });
    try {
      const response = await api.put("/api/v1/user/update", formData);

      toast.update(toastId, {
        autoClose: 1000,
        type: response.status === 200 ? "success" : "error",
        render: response.data,
      });
    } catch (error) {
      toast.update(toastId, {
        autoClose: 1000,
        type: "error",
        render: error.response.data,
      });
    }
  };

  useEffect(() => {
    const getUserInfo = async () => {
      const response = await api.get("/api/v1/user/info");

      const { data } = response;

      if (data.status === "SUCCESS") {
        setFormData({
          sub: data.sub,
          email: data.email,
          given_name: data.given_name,
          family_name: data.family_name,
        });
      }
    };
    getUserInfo();
  }, []);

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Typography component="h1" variant="h5">
          Account Settings
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                value={formData.email}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="given_name"
                label="First Name"
                name="given_name"
                autoComplete="given-name"
                value={formData.given_name}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="family_name"
                label="Last Name"
                name="family_name"
                autoComplete="family-name"
                value={formData.family_name}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} display={"flex"} gap={2}>
              <TextField
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="new-password"
                value={formData.password}
                onChange={handleChange}
                disabled={!changePw}
              />
              <IconButton
                sx={{ textTransform: "none" }}
                onClick={handleChangeStatusOfChangePw}
              >
                {changePw ? <LockOpenOutlinedIcon /> : <LockOutlinedIcon />}
              </IconButton>
            </Grid>
          </Grid>
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Update
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default SettingsPage;
