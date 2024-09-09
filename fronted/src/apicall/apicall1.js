import axios from "axios";

export default function (url,method,data){
    return axios({
        baseURL: "http:/api",
        url,
        method,
        data,
      })
}
