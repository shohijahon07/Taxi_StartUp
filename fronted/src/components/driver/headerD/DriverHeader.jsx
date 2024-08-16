import React, { useEffect, useState } from 'react'
import "./header.css"
import axios from 'axios';
import DriverLanding from '../DriverLanding';
import DriverAbout from '../DriverAbout';
import { useNavigate } from 'react-router-dom';
function DriverHeader() {
  let navigate = useNavigate();

  const [open, setOpen] = useState(false);

  const [userName, setUserName] = useState("");
  useEffect(() => {
  getDriver()
 
}, []);
function functionDeleteToken() {
  navigate("/")
  localStorage.removeItem("access_token")
  localStorage.removeItem("refresh_token")
}

    function getDriver(){
      axios({
        url:"http://localhost:8080/api/auth/name",
        method:"get",
        headers:{Authorization:localStorage.getItem("refresh_token")}
    }).then((res)=>{
      setUserName(res.data)
    })
    }
  return (
    <div className="begin1">
       <div className='haederDriver'>
        <div className="headerLeft">
          <h2 className='headerText'>Hurmatli <p className='text-primary'>{userName.fullName}! </p> </h2>
        </div>
        <div className="headerRight">
          <ul className='listUl'>
            <li className='list-group-item'onClick={()=>setOpen(true)} >Men Haqimda</li>
            <li className='list-group-item' onClick={()=>setOpen(false)} >Yo'nalish</li>
            <li className='list-group-item'>
              <button className='logOut'  onClick={()=>functionDeleteToken()}>Log Out</button>
            </li>
          </ul>
        </div>
    </div>
    {
      open?<DriverAbout/>:<DriverLanding/>
    }
    </div>
   
  )
}

export default DriverHeader
