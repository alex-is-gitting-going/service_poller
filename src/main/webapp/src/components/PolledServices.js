import { Paper, Typography } from "@mui/material";
import Button from "@mui/material/Button";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import TextField from "@mui/material/TextField";
import MUIDataTable from "mui-datatables";
import { useCallback, useEffect, useState } from "react";
import {
  getPolledURLs,
  pollNewURL,
  stopPollingURL,
} from "../api/servicePollerApi";
import EditServiceDialog from "./EditServiceDialog";

export const validateName = (name, currentNameInputError, setInputError) => {
  if (!name) {
    setInputError("Required!");
    return false;
  }
  if (name.length > 255) {
    setInputError("Name too long (max 255 chars)");
    return false;
  }
  if (currentNameInputError) {
    setInputError("");
  }
  return true;
};

export const isValidURL = (string) => {
  let url;

  try {
    url = new URL(string);
  } catch (_) {
    return false;
  }

  return url.protocol === "http:" || url.protocol === "https:";
};

export const validateURL = (url, currentInputError, setInputError) => {
  if (!isValidURL(url)) {
    setInputError("Invalid URL (must include protocol - http:// or https://)");
    return false;
  }
  if (url.length > 2000) {
    setInputError("URL too long (max 2000 chars)");
    return false;
  }
  if (currentInputError) {
    setInputError("");
  }
  return true;
};

const PolledServices = ({ selectedUsername, setSelectedUsername }) => {
  const [firstLoadDone, setFirstLoadDone] = useState(false);
  const [serviceData, setServiceData] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  const [newURLToPollName, setNewURLToPollName] = useState("");
  const [newURLToPoll, setNewURLToPoll] = useState("");
  const [newURLToPollNameError, setNewURLToPollNameError] = useState("");
  const [newURLToPollError, setNewURLToPollError] = useState("");

  const [updateServiceDialogOpen, setUpdateServiceDialogOpen] = useState(false);
  const [updateServiceID, setUpdateServiceID] = useState("");
  const [updateServiceName, setUpdateServiceName] = useState("");
  const [updateServiceURL, setUpdateServiceURL] = useState("");

  const getMonitoredServicesData = useCallback(() => {
    getPolledURLs(selectedUsername)
      .then((data) => {
        if (!firstLoadDone) {
          setFirstLoadDone(true);
        }
        setServiceData(data);
      })
      .catch(() => {});
  }, [selectedUsername, firstLoadDone, setServiceData]);

  useEffect(() => {
    if (selectedUsername) {
      getMonitoredServicesData();
      const interval = setInterval(() => {
        getMonitoredServicesData();
      }, 5000);
      return () => clearInterval(interval);
    }
  }, [selectedUsername, getMonitoredServicesData]);

  const deleteRow = (id) => {
    stopPollingURL(id)
      .then(() => {
        getMonitoredServicesData();
      })
      .catch((error) => {});
  };

  const validateNewServiceInput = () => {
    return (
      validateName(
        newURLToPollName,
        newURLToPollNameError,
        setNewURLToPollNameError
      ) & validateURL(newURLToPoll, newURLToPollError, setNewURLToPollError)
    );
  };

  const addNewService = () => {
    if (!validateNewServiceInput()) return;

    pollNewURL(selectedUsername, newURLToPollName, newURLToPoll).then(() => {
      getMonitoredServicesData();
    });
    setNewURLToPoll("");
    setNewURLToPollName("");
  };

  const openUpdateServiceWindow = (serviceID, rowInfo) => {
    setUpdateServiceID(serviceID);
    setUpdateServiceName(rowInfo.rowData[0]);
    setUpdateServiceURL(rowInfo.rowData[1]);
    setUpdateServiceDialogOpen(true);
  };

  const searchValueChanged = (newVal) => {
    if (newVal && !isSearching) {
      setIsSearching(true);
    } else if (!newVal && isSearching) {
      setIsSearching(false);
    }
  };

  const logout = () => {
    setSelectedUsername("");
    setServiceData([]);
  };

  const getNoMatchText = useCallback(() => {
    return isSearching
      ? "There are no services being monitored that match your search."
      : firstLoadDone
      ? "There are no services being monitored, please add a service below."
      : "Loading monitored services, please wait.";
  }, [isSearching, firstLoadDone]);

  const tableOptions = {
    filterType: "dropdown",
    selectableRows: "none",
    print: false,
    viewColumns: false,
    jumpToPage: true,
    textLabels: {
      body: {
        noMatch: getNoMatchText(),
      },
    },
    onSearchChange: searchValueChanged,
    setTableProps: () => {
      return {
        size: "small",
      };
    },
  };

  const columns = [
    {
      label: "Service Name",
      name: "name",
      options: {
        filter: false,
      },
    },
    {
      label: "Service URL",
      name: "url",
      options: {
        filter: false,
      },
    },
    {
      label: "Status",
      name: "status",
      options: {
        filter: true,
      },
    },
    {
      label: "Date Added",
      name: "createDate",
      options: {
        customBodyRender: (value) => new Date(value).toLocaleString(),
        filter: false,
      },
    },
    {
      label: "Last Updated",
      name: "lastPolledDate",
      options: {
        customBodyRender: (value) =>
          value ? new Date(value).toLocaleString() : "",
        filter: false,
      },
    },
    {
      label: " ",
      name: "id",
      options: {
        customHeadRender: (item) => (
          <th
            key={"id"}
            style={{
              borderBottom: "1px solid rgba(224, 224, 224, 1)",
            }}
          ></th>
        ),
        customBodyRender: (serviceID, rowInfo, updateValue) => {
          return (
            <div
              style={{
                display: "flex",
                flex: 1,
                justifyContent: "space-evenly",
              }}
            >
              <Button
                onClick={() => openUpdateServiceWindow(serviceID, rowInfo)}
              >
                Edit
              </Button>
              <Button onClick={() => deleteRow(serviceID)}>Delete</Button>
            </div>
          );
        },
        filter: false,
      },
    },
  ];

  const closeUpdateServiceDialog = useCallback(() => {
    setUpdateServiceDialogOpen(false);
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <div style={{ flex: 1, padding: 20, minWidth: 950 }}>
        <MUIDataTable
          title={`${selectedUsername}'s Monitored Services`}
          data={serviceData}
          columns={columns}
          options={tableOptions}
        />
        <Paper
          elevation={4}
          style={{
            marginTop: 15,
            justifyContent: "space-between",
            display: "flex",
            borderTopLeftRadius: 0,
            borderTopRightRadius: 0,
            flexDirection: "column",
          }}
        >
          <Typography
            variant="h6"
            style={{
              backgroundColor: "#002845",
              color: "white",
              paddingTop: 5,
              paddingBottom: 5,
            }}
          >
            Register New Service
          </Typography>

          <div
            style={{
              display: "flex",
              flex: 1,
              justifyContent: "space-between",
              padding: 10,
            }}
          >
            <TextField
              autoComplete="off"
              style={styles.newServiceName}
              label={"New Service Name"}
              size="small"
              value={newURLToPollName}
              helperText={newURLToPollNameError}
              error={newURLToPollNameError !== ""}
              onChange={(event) => setNewURLToPollName(event.target.value)}
              onKeyPress={(event) => {
                if (event.code === "Enter") addNewService();
              }}
            />
            <TextField
              style={styles.newServiceURL}
              label={"New Service URL"}
              size="small"
              value={newURLToPoll}
              helperText={newURLToPollError}
              error={newURLToPollError !== ""}
              onChange={(event) => setNewURLToPoll(event.target.value)}
              onSelect={() => {
                if (!newURLToPoll) setNewURLToPoll("https://");
              }}
              onKeyPress={(event) => {
                if (event.code === "Enter") addNewService();
              }}
            />
            <Button
              type="submit"
              style={styles.newServiceButton}
              onClick={addNewService}
            >
              Save
            </Button>
          </div>
        </Paper>
      </div>
      <EditServiceDialog
        isOpen={updateServiceDialogOpen}
        serviceID={updateServiceID}
        serviceName={updateServiceName}
        setServiceName={setUpdateServiceName}
        serviceURL={updateServiceURL}
        setServiceURL={setUpdateServiceURL}
        closeDialog={closeUpdateServiceDialog}
        getMonitoredServicesData={getMonitoredServicesData}
      />
      <Button onClick={logout} style={styles.logoutButton}>
        Logout
      </Button>
    </ThemeProvider>
  );
};

export default PolledServices;

const theme = createTheme({
  components: {
    MUIDataTableToolbar: {
      styleOverrides: {
        root: {
          backgroundColor: "#002845",
          color: "white",
          "& .MuiInputBase-root:before": {
            borderColor: "white",
          },
          "& .MuiInputBase-root:after": {
            borderColor: "white",
          },
          "& .MuiInput-input": {
            color: "white ",
          },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          color: "white",
        },
      },
    },
    MUIDataTableSearch: {
      styleOverrides: {
        searchIcon: {
          color: "white",
        },
        main: {
          color: "white",
        },
      },
    },
    MUIDataTableHeadCell: {
      styleOverrides: {
        contentWrapper: {
          justifyContent: "left",
        },
      },
    },

    MuiButton: {
      styleOverrides: {
        root: {
          paddingLeft: 0,
        },
      },
    },
  },
});

const styles = {
  newServiceName: {
    flex: 1,
    marginLeft: 15,
    marginRight: 15,
    marginTop: 15,
  },
  newServiceURL: {
    flex: 2,
    marginLeft: 15,
    marginRight: 15,
    marginTop: 15,
  },
  newServiceButton: {
    marginLeft: 15,
    marginRight: 15,
  },
  updateServiceName: {
    flex: 1,
    margin: 15,
  },
  logoutButton: {
    width: "100%",
  },
};
