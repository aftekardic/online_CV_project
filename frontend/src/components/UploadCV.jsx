import React, { useState } from "react";
import { Box, Button, TextField } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import api from "../services/api";
import { toast } from "react-toastify";

function UploadCV() {
  const [cvFile, setCvFile] = useState(null);
  const [name, setName] = useState("");

  const handleFileChange = (e) => {
    setCvFile(e.target.files[0]);
    setName(e.target.value);
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("file", cvFile);
    formData.append("name", name);
    formData.append("userEmail", localStorage.getItem("userEmail"));

    const toastId = toast.info("Uploading...", {
      autoClose: false,
    });

    try {
      await api.post("/api/v1/cv/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      toast.update(toastId, {
        render: "File uploaded successfully...",
        type: "success",
        autoClose: 1000,
      });
    } catch (error) {
      toast.update(toastId, {
        render: "Error uploading file",
        type: "error",
        autoClose: 1000,
      });
    }
  };

  return (
    <Box sx={{ p: 4 }}>
      <TextField
        type="file"
        onChange={handleFileChange}
        sx={{ mb: 2 }}
        fullWidth
      />
      <Button
        variant="contained"
        color="primary"
        startIcon={<CloudUploadIcon />}
        sx={{ mt: 2 }}
        onClick={handleSubmit}
        disabled={!cvFile || !name}
      >
        Upload
      </Button>
    </Box>
  );
}

export default UploadCV;
