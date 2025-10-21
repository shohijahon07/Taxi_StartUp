import axios from "axios";

export default function (url,method,data){
    return axios({
        baseURL: "/api",
        url,
        method,
        data,
      })
}
