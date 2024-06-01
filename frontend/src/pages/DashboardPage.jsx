import React from "react";
import UploadCV from "../components/UploadCV";
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Container,
} from "@mui/material";
import UpdateIcon from "@mui/icons-material/Update";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";

function DashboardPage() {
  return (
    <Container sx={{ mt: 4 }}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="div">
            Upload CV
          </Typography>
          <Typography sx={{ mb: 1.5 }} color="text.secondary">
            Upload your CV to the system
          </Typography>
          <UploadCV />
        </CardContent>
      </Card>
    </Container>
  );
}

export default DashboardPage;
