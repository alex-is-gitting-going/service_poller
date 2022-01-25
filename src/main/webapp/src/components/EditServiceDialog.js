import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import { useState } from "react";
import { updatePolledURL } from "../api/servicePollerApi";
import { validateName, validateURL } from "./PolledServices";

const EditServiceDialog = (props) => {
  const {
    isOpen,
    serviceID,
    serviceName,
    setServiceName,
    serviceURL,
    setServiceURL,
    getMonitoredServicesData,
    closeDialog,
  } = props;
  const [updatedServiceNameError, setUpdatedServiceNameError] = useState("");
  const [updatedServiceURLError, setUpdatedServiceURLError] = useState("");

  const validateUpdateServiceInput = () => {
    return (
      validateName(
        serviceName,
        updatedServiceNameError,
        setUpdatedServiceNameError
      ) &
      validateURL(serviceURL, updatedServiceURLError, setUpdatedServiceURLError)
    );
  };

  const submitEditService = () => {
    if (!validateUpdateServiceInput()) return;
    updatePolledURL(serviceID, serviceName, serviceURL)
      .then(getMonitoredServicesData)
      .catch((error) => {});
    closeDialog();
  };

  const cancelEditService = () => {
    closeDialog();
    setUpdatedServiceURLError("");
    setUpdatedServiceNameError("");
  };

  return (
    <Dialog open={isOpen} fullWidth maxWidth="md">
      <DialogTitle>Update Monitored Service</DialogTitle>
      <DialogContent style={{ display: "flex", flexDirection: "column" }}>
        <TextField
          style={styles.updateServiceName}
          label={"Updated Service Name"}
          value={serviceName}
          helperText={updatedServiceNameError}
          error={updatedServiceNameError !== ""}
          onChange={(event) => setServiceName(event.target.value)}
        />
        <TextField
          style={styles.updateServiceName}
          label={"Updated Service URL"}
          value={serviceURL}
          helperText={updatedServiceURLError}
          error={updatedServiceURLError !== ""}
          onChange={(event) => setServiceURL(event.target.value)}
          onSelect={() => {
            if (!serviceURL) setServiceURL("https://");
          }}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={cancelEditService}>Cancel</Button>
        <Button type="submit" onClick={() => submitEditService()}>
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditServiceDialog;

const styles = {
  updateServiceName: {
    flex: 1,
    margin: 15,
  },
};
