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

function SettingsPage() {
  const [count, setCount] = useState(0);
  const [formData, setFormData] = useState({
    email: "",
    firstName: "",
    lastName: "",
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
    if (count % 2 == 0) {
      setChangePw(true);
    } else {
      setChangePw(false);
    }
  };
  const handleSubmit = (e) => {
    e.preventDefault();
    // Güncelleme işlemleri burada yapılabilir
    console.log("Updated information:", formData);
  };
  useEffect(() => {
    const getUserInfo = async () => {
      const response = await api.get("/api/v1/user/info");

      const { data } = response;
      if (data.status == "SUCCESS") {
        setFormData({
          email: data.email,
          firstName: data.given_name,
          lastName: data.family_name,
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
                id="firstName"
                label="First Name"
                name="firstName"
                autoComplete="given-name"
                value={formData.firstName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="lastName"
                label="Last Name"
                name="lastName"
                autoComplete="family-name"
                value={formData.lastName}
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
