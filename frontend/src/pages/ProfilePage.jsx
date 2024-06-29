import { Box, Container, Link } from "@mui/material";
import React, { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import api from "../services/api";

function ProfilePage() {
  const [formData, setFormData] = useState([]);
  useEffect(() => {
    const getUserInfo = async () => {
      const response = await api.get("/api/v1/user/all");
      const { data } = response;
      setFormData(data);
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
      {localStorage.getItem("userRoles").includes("ADMIN") ? (
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow sx={{ "& th": { fontWeight: "bold" } }}>
                <TableCell>İsim</TableCell>
                <TableCell>Soy İsim</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Doğum Tarihi</TableCell>
                <TableCell>Maaş</TableCell>
                <TableCell>CV Yükleme Tarihi</TableCell>
                <TableCell>CV Path</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {formData.map((row) => (
                <TableRow key={row.email}>
                  <TableCell>{row.firstName}</TableCell>
                  <TableCell>{row.lastName}</TableCell>
                  <TableCell>{row.email}</TableCell>
                  <TableCell>{row.birthday}</TableCell>
                  <TableCell>{row.salary}</TableCell>

                  <TableCell>{row.uploadDate}</TableCell>
                  <TableCell>
                    {row.cvPath != null ? (
                      <Link
                        href={`${row.cvPath}`}
                        target="_blank"
                        rel="noopener"
                      >
                        View CV
                      </Link>
                    ) : (
                      <div>There is no CV</div>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      ) : (
        <Box> You are not a permission for display this page</Box>
      )}
    </Container>
  );
}

export default ProfilePage;
