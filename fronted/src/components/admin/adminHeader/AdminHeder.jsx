import React, { useContext, useEffect, useState } from 'react'
import "./adminHeader.css"
import RoutesAdmin from '../RoutesAdmin';
import DriversAdmin from '../DriversAdmin';
import AdminNotification from '../AdminNotification';
import Statistics from '../Statistics';
import { fetchDrivers } from '../../../redux/slices/DriverSlice';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { LanguageContext } from '../../language/LanguageContext';
import Connection from '../Connection.jsx';

function AdminHeder() {
  const navigate=useNavigate()
  const [openRoute, setOpenRoute] = useState("1");
  const {drivers} = useSelector((state) => state.driver);
  const [isDriver, setIsDriver] = useState(false);
  const { language, changeLanguage } = useContext(LanguageContext);

  const dispatch=useDispatch()
  useEffect(() => {
   dispatch(fetchDrivers(isDriver)) 
  
    }, []);
  function setRoute(a){
    setOpenRoute(a)
  }
  function functionDeleteToken() {
    navigate("/")
    localStorage.removeItem("access_token")
    localStorage.removeItem("refresh_token")
}
  return (
    <div className='beginAdmin'>
      <ul className='ul_admin'>
        <li style={{
          color:openRoute==="1"?"#303383":""
        }} onClick={()=>setRoute('1')}>{language==="1"?"Yo'nalishlar":"Направления"}</li>
        <li style={{
          color:openRoute==="2"?"#303383":""
        }}  onClick={()=>setRoute('2')}>{language==="1"?"Haydovchilar":"Драйверы"}</li>
        <li style={{
          color:openRoute==="3"?"#303383":""
        }} onClick={()=>setRoute('3')}>{language==="1"?"Bildirishnomalar":"Уведомления"} {drivers && drivers.length > 0 && <sup>{drivers.length}</sup>}</li>
        <li style={{
          color:openRoute==="4"?"#303383":""
        }} onClick={()=>setRoute('4')}>{language==="1"?"Hisobot":"Отчет"}</li>
        <li style={{
          color:openRoute==="5"?"#303383":""
        }}  onClick={()=>setRoute('5')}>{language==="1"?"Bog'lanish":"Связь"}</li>
        <li>
        <div className="rightUserHeader120">
                <select className='form-select' value={language} onChange={(e) => changeLanguage(e.target.value)}>
                    <option value="1">O'zbek</option>
                    <option value="2">Rus</option>
                </select>
              
            </div>
        </li>
        <li><button className='logout'  onClick={functionDeleteToken}>{language==="1"?"chiqish":"Выход"}</button></li>
      </ul>

      {
        openRoute==="1"?<RoutesAdmin/>:openRoute==="2"?<DriversAdmin/>:openRoute==="3"?<AdminNotification/>:openRoute==="4"?<Statistics/>:openRoute==="5"?<Connection/>:""
      }
    </div>
  )
}

export default AdminHeder
