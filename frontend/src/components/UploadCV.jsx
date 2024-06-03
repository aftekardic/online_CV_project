import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  TextField,
  CircularProgress,
  Typography,
} from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import api from "../services/api";
import { toast } from "react-toastify";
import PdfPreview from "./PdfPreview"; // PdfPreview bileşenini ekleyin

function UploadCV() {
  const [cvFile, setCvFile] = useState(null);
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(false);
  const [cvData, setCvData] = useState(null);
  const userEmail = localStorage.getItem("userEmail");

  const handleFileChange = (e) => {
    setCvFile(e.target.files[0]);
    setName(e.target.files[0]?.name);
  };

  const getCV = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/api/v1/cv/info/${userEmail}`);
      setCvData(response.data);
    } catch (error) {
      console.error("Error fetching CV:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("file", cvFile);
    formData.append("name", name);
    formData.append("userEmail", userEmail);

    const toastId = toast.info("Uploading...", {
      autoClose: false,
    });

    setLoading(true);

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

      getCV();
    } catch (error) {
      toast.update(toastId, {
        render: "Error uploading file",
        type: "error",
        autoClose: 1000,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getCV();
  }, [userEmail]);

  return (
    <Box sx={{ p: 4, display: "flex", flexDirection: "column" }}>
      <Button component="label" sx={{ width: "50%" }}>
        {name?.length > 0 && name != undefined ? name : "Upload File"}
        <input onChange={handleFileChange} type="file" hidden />
      </Button>
      <Button
        variant="contained"
        color="primary"
        startIcon={<CloudUploadIcon />}
        sx={{ mt: 2, width: "50%" }}
        onClick={handleSubmit}
        disabled={!cvFile || !name || loading}
      >
        Upload
      </Button>
      {loading && <CircularProgress sx={{ mt: 2 }} />}
      {cvData ? (
        <Box sx={{ mt: 4, width: "100%" }}>
          <Typography variant="h6">Last Uploaded CV Preview</Typography>
          <PdfPreview cvData={cvData} />
        </Box>
      ) : (
        <Typography sx={{ mt: 4 }}>
          You does not upload a CV before...
        </Typography>
      )}
    </Box>
  );
}

export default UploadCV;
