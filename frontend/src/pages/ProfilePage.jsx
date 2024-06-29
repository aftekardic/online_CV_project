import { Box, Container } from "@mui/material";
import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
const createData = (
  firstName,
  lastName,
  email,
  birthDate,
  salary,
  uploadDate,
  cvPath
) => {
  return { firstName, lastName, email, birthDate, salary, uploadDate, cvPath };
};

const rows = [
  createData(
    "Ahmet",
    "Yılmaz",
    "ahmet@example.com",
    "1990-01-01",
    5000,
    "2023-06-01",
    "/path/to/cv1.pdf"
  ),
  createData(
    "Ayşe",
    "Kaya",
    "ayse@example.com",
    "1985-05-15",
    6000,
    "2023-06-02",
    "/path/to/cv2.pdf"
  ),
  createData(
    "Mehmet",
    "Demir",
    "mehmet@example.com",
    "1980-08-20",
    7000,
    "2023-06-03",
    "/path/to/cv3.pdf"
  ),
  // Daha fazla veri ekleyebilirsiniz
];

function ProfilePage() {
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
              {rows.map((row) => (
                <TableRow key={row.email}>
                  <TableCell>{row.firstName}</TableCell>
                  <TableCell>{row.lastName}</TableCell>
                  <TableCell>{row.email}</TableCell>
                  <TableCell>{row.birthDate}</TableCell>
                  <TableCell>{row.salary}</TableCell>
                  <TableCell>{row.uploadDate}</TableCell>
                  <TableCell>{row.cvPath}</TableCell>
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
