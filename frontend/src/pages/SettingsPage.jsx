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
import { useNavigate } from "react-router-dom";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";

function SettingsPage() {
  const navigate = useNavigate();
  const [count, setCount] = useState(0);
  const [formData, setFormData] = useState({
    email: "",
    given_name: "",
    family_name: "",
    password: "",
    old_email: "",
    birthday: null,
    salary: "",
  });
  const [changePw, setChangePw] = useState(false);
  const handleChange = (e) => {
    let { name, value } = e.target;
    value = name === "salary" ? parseFloat(value) : value;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleDateChanged = (date) => {
    setFormData({
      ...formData,
      birthday: dayjs(date).toDate(),
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
      await api.post("/auth/logout", {
        refreshToken: localStorage.getItem("refreshToken"),
      });
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("userRoles");
      localStorage.removeItem("userEmail");

      toast.update(toastId, {
        autoClose: 1000,
        type: response.status === 200 ? "success" : "error",
        render: response.data,
        onClose: () => {
          navigate("/sign-in");
        },
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
          old_email: data.email,
          birthday: data.birthday,
          salary: data.salary,
        });
      }
    };
    if (
      localStorage.getItem("userRoles").includes("ADMIN") ||
      localStorage.getItem("userRoles").includes("USER")
    ) {
      getUserInfo();
    }
  }, []);

  return (
    <Container sx={{ mt: 4 }}>
      {localStorage.getItem("userRoles").includes("ADMIN") ||
      localStorage.getItem("userRoles").includes("USER") ? (
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
              <Grid item xs={12} sm={6}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DatePicker
                    label="Birthday"
                    sx={{ width: "100%" }}
                    value={dayjs(formData.birthday)}
                    onChange={handleDateChanged}
                    format="DD/MM/YYYY"
                    required
                  />
                </LocalizationProvider>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  autoComplete="given-name"
                  name="salary"
                  required
                  fullWidth
                  id="salary"
                  label="Current Salary"
                  value={formData.salary}
                  onChange={handleChange}
                  type="number"
                />
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
      ) : (
        <Typography>Please contact to admin for permission...</Typography>
      )}
    </Container>
  );
}

export default SettingsPage;
