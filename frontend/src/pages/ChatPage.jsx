import React, { useEffect, useRef, useState } from "react";
import {
  Box,
  TextField,
  Button,
  Typography,
  List,
  ListItem,
  ListItemText,
  Container,
  Paper,
} from "@mui/material";
import dayjs from "dayjs";
import api from "../services/api";

function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const userEmail = localStorage.getItem("userEmail");
  const [sended, setSended] = useState(false);

  const messagesEndRef = useRef(null);

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSendMessage();
    }
  };
  const handleSendMessage = async () => {
    if (newMessage.trim() !== "") {
      const newMsg = {
        message: newMessage,
        sender: userEmail,
      };

      try {
        const response = await api.post("/api/v1/chat/send", newMsg);

        if (response.status === 200) {
          setSended(true);
          setNewMessage("");
        } else {
          console.error("Error sending message:", response.statusText);
        }
      } catch (error) {
        console.error("Error sending message:", error);
      }
    }
  };

  const getAllSortedMessages = async () => {
    const response = await api.get("/api/v1/chat/all");

    if (response.status === 200) {
      const formattedMessages = response.data.map((element) => ({
        message: element.message,
        sender: element.sender,
        timestamp: dayjs(element.createdDate).format("DD/MM/YYYY HH:mm:ss"),
      }));
      setMessages(formattedMessages);
    }
  };

  useEffect(() => {
    getAllSortedMessages();
    setSended(false);
  }, [sended]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <Container sx={{ mt: 4 }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          height: "75vh",
          p: 2,
        }}
      >
        <Typography variant="h4" gutterBottom>
          Chat Messages
        </Typography>
        <Box sx={{ flexGrow: 1, overflowY: "auto", mb: 2 }}>
          <List>
            {messages.map((message, index) => (
              <ListItem
                key={index}
                sx={{
                  justifyContent:
                    message.sender === userEmail ? "flex-end" : "flex-start",
                }}
              >
                <Paper
                  sx={{
                    p: 1,
                    bgcolor:
                      message.sender === userEmail ? "lightblue" : "lightgrey",
                    maxWidth: "70%",
                  }}
                >
                  <ListItemText
                    primary={message.message}
                    secondary={`${message.sender} - ${message.timestamp}`}
                  />
                </Paper>
              </ListItem>
            ))}
            <div ref={messagesEndRef} />
          </List>
        </Box>
        <Box
          sx={{
            display: "flex",
            gap: 1,
            position: "fixed",
            bottom: 16,
            left: 0,
            width: "100%",
            p: 2,
            bgcolor: "background.paper",
          }}
        >
          <TextField
            fullWidth
            variant="outlined"
            label="Type your message"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <Button
            variant="contained"
            color="primary"
            onClick={handleSendMessage}
          >
            Send
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default ChatPage;
