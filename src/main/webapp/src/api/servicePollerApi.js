import axios from "axios";

const endpoint = "/api/v1/monitoredservices";

export const getPolledURLs = (username) => {
  return new Promise((resolve, reject) => {
    axios
      .get(endpoint, {
        params: {
          username: username.trim().toUpperCase(),
        },
      })
      .then((response) => {
        resolve(response.data);
      })
      .catch((error) => {
        console.log(error);
        reject();
      });
  });
};

export const stopPollingURL = (serviceID) => {
  return new Promise((resolve, reject) => {
    axios
      .delete(endpoint, { data: { id: serviceID } })
      .then(resolve)
      .catch((err) => {
        console.log(err);
        reject();
      });
  });
};

export const pollNewURL = (username, name, url) => {
  return new Promise((resolve, reject) => {
    axios
      .post(endpoint, { username: username.trim().toUpperCase(), name, url })
      .then(() => {
        resolve();
      })
      .catch((error) => {
        console.log(error);
        reject();
      });
  });
};

export const updatePolledURL = (id, name, url) => {
  return new Promise((resolve, reject) => {
    axios
      .patch(endpoint, { id, name, url })
      .then(() => {
        resolve();
      })
      .catch((error) => {
        console.log(error);
        reject();
      });
  });
};
