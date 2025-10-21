import axios from "axios";

const apicall = async (url, method, data) => {
  try {
    const response = await axios({
        baseURL: "/api",
      url,
      method,
      data,
      headers: { Authorization: localStorage.getItem("access_token") },
    });
    return response.data;
  } catch (error) {

               const refreshResponse = await axios({
          url: `/api/auth/refresh`,
          method: "POST",
          headers: { refreshToken: localStorage.getItem("refresh_token") },
        });

        localStorage.setItem("access_token", refreshResponse.data);

        const retryResponse = await axios({
          baseURL: "/api",
          url,
          method,
          data,
          headers: { Authorization: localStorage.getItem("access_token") },
        });
        return retryResponse.data;

       
      
  }
};

export default apicall;
