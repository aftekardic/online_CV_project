import React from "react";
import UploadCV from "../components/UploadCV";
import { Card, CardContent, Typography, Container } from "@mui/material";

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
