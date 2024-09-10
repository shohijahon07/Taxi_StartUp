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
    console.log(a);
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
        <li onClick={()=>setRoute('1')}>Yo'nalishlar</li>
        <li  onClick={()=>setRoute('2')}>Haydovchilar</li>
        <li  onClick={()=>setRoute('3')}>Bildirishnomalar {drivers.length > 0 && <sup>{drivers.length}</sup>}</li>
        <li  onClick={()=>setRoute('4')}>Hisobot</li>
        <li><button className='logout'  onClick={functionDeleteToken}>log out</button></li>
        <li>
        <div className="rightUserHeader120">
                <select className='form-select' value={language} onChange={(e) => changeLanguage(e.target.value)}>
                    <option value="1">O'zbek</option>
                    <option value="2">Rus</option>
                </select>
              
            </div>
        </li>
      </ul>

      {
        openRoute==="1"?<RoutesAdmin/>:openRoute==="2"?<DriversAdmin/>:openRoute==="3"?<AdminNotification/>:openRoute==="4"?<Statistics/>:""
      }
    </div>
  )
}

export default AdminHeder
