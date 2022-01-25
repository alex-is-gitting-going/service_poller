import { Button, Paper, Typography } from "@mui/material";
import TextField from "@mui/material/TextField";
import { useState } from "react";

const LoginBox = ({ setSelectedUsername }) => {
  const [username, setUsername] = useState("");
  const [isUsernameBoxFocus, setIsUsernameBoxFocus] = useState("");
  const [usernameBoxError, setUsernameBoxError] = useState("");

  const submitUsername = () => {
    const trimmed = username.trim();
    if (!trimmed) {
      setUsernameBoxError("You need a name!");
      return;
    } else if (trimmed.length > 255) {
      setUsernameBoxError("Name too long!");
      return;
    }

    setUsernameBoxError("");
    setUsername(trimmed);
    setSelectedUsername(trimmed);
  };

  return (
    <>
      <img style={styles.logo} src="/kry_logo.jpg" alt="Kry logo" />
      <Paper style={styles.paper}>
        <Typography variant="h5">Welcome to the kry Service Poller</Typography>
        <TextField
          autoComplete={"off"}
          InputLabelProps={{
            style: {
              left: isUsernameBoxFocus || username ? "0px" : "42.5px",
            },
          }}
          error={usernameBoxError}
          helperText={usernameBoxError}
          value={username}
          label={"Username"}
          required
          onChange={(event) => setUsername(event.target.value)}
          inputProps={{
            min: 0,
            style: { textAlign: "center", fontSize: "large" },
          }}
          size="medium"
          style={styles.usernameInput}
          onKeyPress={(event) => {
            if (event.code === "Enter") submitUsername();
          }}
          onFocus={() => setIsUsernameBoxFocus(true)}
          onBlur={() => setIsUsernameBoxFocus(false)}
        />
        <Button onClick={submitUsername}>Enter</Button>
      </Paper>
    </>
  );
};

export default LoginBox;

const styles = {
  logo: {
    paddingTop: 40,
  },
  paper: {
    elevation: 4,
    maxWidth: 400,
    minWidth: 400,
    maxHeight: 200,
    position: "absolute",
    top: 0,
    bottom: 0,
    left: 0,
    right: 0,
    margin: "auto",
    display: "flex",
    flexDirection: "column",
    padding: 20,
    justifyContent: "space-evenly",
  },
  usernameInput: {
    alignSelf: "center",
    maxWidth: 200,
  },
};
