import axios from "axios";

export default function (url,method,data){
    return axios({
        baseURL: "https:/api",
        url,
        method,
        data,
      })
}
