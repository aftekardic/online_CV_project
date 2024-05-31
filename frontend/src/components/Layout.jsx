import React, { useState } from "react";
import { AppBar, Tabs, Tab, Toolbar, Typography, Box } from "@mui/material";
import { Outlet, useNavigate } from "react-router-dom";
import { AuthProvider } from "../services/AuthProvider";
import LogoutIcon from "@mui/icons-material/Logout";
import api from "../services/api";
import { toast } from "react-toastify";

function Layout() {
  const [value, setValue] = useState(0);
  const navigate = useNavigate();
  const handleChange = (event, newValue) => {
    if (event.type !== "click" || event.type === "click") {
      setValue(newValue);
    }
  };

  const handleLogout = async () => {
    try {
      await api.post("/auth/logout", {
        refreshToken: localStorage.getItem("refreshToken"),
      });
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      toast.success("You are redirecting to sign page.", {
        onClose: () => {
          navigate("/");
        },
        autoClose: 1000,
      });
    } catch (error) {
      toast.error(`Logout failed.${error}`, { autoClose: 1500 });
    }
  };

  return (
    <AuthProvider>
      <AppBar position="static">
        <Typography
          variant="h3"
          noWrap
          component="div"
          sx={{ alignSelf: "center", padding: 2 }}
        >
          Welcome to Online CV Analyzer
        </Typography>
      </AppBar>
      <Box sx={{ width: "100%", display: "flex", justifyContent: "center" }}>
        <Tabs
          value={value}
          onChange={handleChange}
          aria-label="nav tabs example"
          role="navigation"
        >
          <Tab
            label="Dashboard"
            onClick={() => {
              navigate("/dashboard");
            }}
          />
          <Tab
            label="Settings"
            onClick={() => {
              navigate("/settings");
            }}
          />
          <Tab
            icon={<LogoutIcon />}
            aria-label="phone"
            onClick={handleLogout}
          />
        </Tabs>
      </Box>
      <Outlet />
    </AuthProvider>
  );
}

export default Layout;
