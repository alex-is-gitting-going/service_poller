import { createTheme, ThemeProvider } from "@mui/material/styles";
import { useEffect, useState } from "react";
import "./App.css";
import LoginBox from "./components/LoginBox";
import PolledServices from "./components/PolledServices";

const theme = createTheme({
  shape: {
    borderRadius: 10,
  },
  palette: {
    primary: {
      main: "#002845",
    },
  },
});

function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function App() {
  const [selectedUsername, setSelectedUsername] = useState("");

  useEffect(() => {
    setSelectedUsername(localStorage.getItem("username"));
  }, []);

  const setUsername = (username) => {
    const fixedUsername = capitalizeFirstLetter(username);
    localStorage.setItem("username", fixedUsername);
    setSelectedUsername(fixedUsername);
  };

  return (
    <div className="App">
      <ThemeProvider key={selectedUsername} theme={theme}>
        {selectedUsername ? (
          <div className="_animateFadeIn">
            <PolledServices
              selectedUsername={selectedUsername}
              setSelectedUsername={setUsername}
            />
          </div>
        ) : (
          <div className="_animateFadeIn">
            <LoginBox setSelectedUsername={setUsername} />
          </div>
        )}
      </ThemeProvider>
    </div>
  );
}

export default App;
