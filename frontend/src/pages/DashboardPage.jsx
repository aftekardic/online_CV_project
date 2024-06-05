import React, { useEffect, useState } from "react";
import UploadCV from "../components/UploadCV";
import {
  Card,
  CardContent,
  Typography,
  Container,
  TextField,
  List,
  ListItem,
  ListItemText,
  Link,
  IconButton,
  Divider,
  Box,
} from "@mui/material";
import { Search, PictureAsPdf } from "@mui/icons-material";
import api from "../services/api";

function DashboardPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [cvFiles, setCvFiles] = useState([]);
  useEffect(() => {
    // CV dosyalarını backend'den çekme
    const fetchCVs = async () => {
      try {
        const response = await api.get("/api/v1/cv/list");
        setCvFiles(response.data);
      } catch (error) {
        console.error("Error fetching CVs:", error);
      }
    };

    if (localStorage.getItem("userRoles").includes("ADMIN")) {
      fetchCVs();
    }
  }, [localStorage.getItem("userRoles")]);

  const filteredCVs = cvFiles.filter((cv) =>
    cv.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Container sx={{ mt: 4 }}>
      {localStorage.getItem("userRoles").includes("USER") ? (
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
      ) : (
        <Card>
          <CardContent>
            <Typography variant="h6" component="div" sx={{ mb: 2 }}>
              Search CV with email
            </Typography>
            <TextField
              fullWidth
              variant="outlined"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: <Search position="start" />,
              }}
              sx={{ mb: 2 }}
            />
            <List>
              {filteredCVs.length > 0 ? (
                filteredCVs.map((cv, index) => (
                  <Box key={index}>
                    <ListItem
                      sx={{
                        border: "1px solid #e0e0e0",
                        borderRadius: "8px",
                        mb: 1,
                        boxShadow: 1,
                      }}
                    >
                      <PictureAsPdf color="action" sx={{ mr: 2 }} />
                      <ListItemText
                        primary={cv}
                        secondary={
                          <Link
                            href={`/cvs/${cv}`}
                            target="_blank"
                            rel="noopener"
                          >
                            View PDF
                          </Link>
                        }
                      />
                    </ListItem>
                  </Box>
                ))
              ) : (
                <Typography>No CVs found</Typography>
              )}
            </List>
          </CardContent>
        </Card>
      )}
    </Container>
  );
}

export default DashboardPage;
