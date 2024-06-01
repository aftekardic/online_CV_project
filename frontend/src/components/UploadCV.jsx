import React, { useState } from "react";
import { Box, Button, TextField, Typography } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";

function UploadCV() {
  const [cvFile, setCvFile] = useState(null);

  const handleFileChange = (e) => {
    setCvFile(e.target.files[0]);
  };

  const handleSubmit = () => {
    // Implement the file upload logic here
    console.log(cvFile);
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
        disabled={!cvFile}
      >
        Upload
      </Button>
    </Box>
  );
}

export default UploadCV;
