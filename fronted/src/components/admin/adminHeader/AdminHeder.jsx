import React, { useState } from 'react'
import "./adminHeader.css"
import RoutesAdmin from '../RoutesAdmin';
import DriversAdmin from '../DriversAdmin';
import AdminNotification from '../AdminNotification';
import Statistics from '../Statistics';

function AdminHeder() {
  const [openRoute, setOpenRoute] = useState("1");

  function setRoute(a){
    console.log(a);
    setOpenRoute(a)
  }
  return (
    <div className='beginAdmin'>
      <ul className='ul_admin'>
        <li onClick={()=>setRoute('1')}>Yo'nalishlar</li>
        <li  onClick={()=>setRoute('2')}>Haydovchilar</li>
        <li  onClick={()=>setRoute('3')}>Bildirishnomalar</li>
        <li  onClick={()=>setRoute('4')}>Hisobot</li>
      </ul>

      {
        openRoute==="1"?<RoutesAdmin/>:openRoute==="2"?<DriversAdmin/>:openRoute==="3"?<AdminNotification/>:openRoute==="4"?<Statistics/>:""
      }
    </div>
  )
}

export default AdminHeder
