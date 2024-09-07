import React, { useContext, useEffect, useState } from 'react'
import "./header.css"
import axios from 'axios';
import { LanguageContext } from '../../language/LanguageContext';
import DriverLanding from '../DriverLanding';
import DriverAbout from '../DriverAbout';
import telegram from "../../../pictures/telegram.svg";
import { useNavigate } from 'react-router-dom';
import group from "../../../pictures/Group (1).svg"
function DriverHeader() {
  const { language, changeLanguage } = useContext(LanguageContext);
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
    function navigatePath(){
navigate("/yo'nalish")
    }
    function navigateMyselft(){
      navigate("/o'zim_haqqimda")
          }
  return (

    <div className="begin1">
       <div className='haederDriver'>
        <div className="headerLeft">
          <img src={group} className='img_dr_logo' />
        </div>
        <div className="center">
          <ul className='listUl'>
          <li className='list-group-item1' onClick={navigatePath} >Yo'nalish</li>
            
            <li className='list-group-item2'onClick={navigateMyselft} >Men haqimda</li>

            
          </ul>

        </div>
        <ul className='listUl2'>
<li className='list-group-item3'>
            <select className='form-control language_dr' value={language} onChange={(e) => changeLanguage(e.target.value)}>
                    <option className='option_dr' value="1">O'zbek</option>
                    <option className='option_dr' value="2">Rus</option>
                </select>
            </li>
            <li className='list-group-item2'>
            <img src={telegram}className='imgTelegram' alt="" />
            </li>
</ul>


    </div>
    {/* {
      open?<DriverAbout/>:<DriverLanding/>
    } */}
    </div>
   
  )
}

export default DriverHeader
