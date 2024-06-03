import React, { useState } from "react";
import { Document, Page } from "react-pdf";
import { pdfjs } from "react-pdf";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import DownloadIcon from "@mui/icons-material/Download";
import Container from "@mui/material/Container";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/legacy/build/pdf.worker.min.mjs`;

const PdfPreview = ({ cvData }) => {
  const [numPages, setNumPages] = useState(null);
  const [pageNumber, setPageNumber] = useState(1);

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
  };
  return (
    <Container
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        mt: 4,
        width: "100%",
      }}
    >
      <Box
        sx={{
          border: "1px solid #ccc",
          boxShadow: 1,
          mb: 2,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          width: "100%",
          maxWidth: 800,
          minHeight: 920,
          maxHeight: 1280,
          overflow: "auto",
        }}
      >
        <Document file={cvData.filePath} onLoadSuccess={onDocumentLoadSuccess}>
          <Page pageNumber={pageNumber} />
        </Document>
      </Box>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: "100%",
          maxWidth: 800,
          mb: 2,
        }}
      >
        <IconButton
          onClick={() => setPageNumber(pageNumber > 1 ? pageNumber - 1 : 1)}
          disabled={pageNumber <= 1}
        >
          <ArrowBackIcon />
        </IconButton>
        <Typography>
          Page {pageNumber} of {numPages}
        </Typography>
        <IconButton
          onClick={() =>
            setPageNumber(pageNumber < numPages ? pageNumber + 1 : numPages)
          }
          disabled={pageNumber >= numPages}
        >
          <ArrowForwardIcon />
        </IconButton>
      </Box>
      <Button
        variant="contained"
        color="primary"
        startIcon={<DownloadIcon />}
        href={cvData.filePath}
        download
      >
        Download PDF
      </Button>
    </Container>
  );
};

export default PdfPreview;
