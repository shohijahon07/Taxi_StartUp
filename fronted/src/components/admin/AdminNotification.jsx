import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { deleteDriver, editDriverIsDriving, fetchDrivers, setDriverIsDriver } from '../../redux/slices/DriverSlice';
import { useNavigate } from 'react-router-dom';
import { FaCheck } from "react-icons/fa6";
import { ToastContainer, toast } from 'react-toastify';
function AdminNotification() {
    const {drivers} = useSelector((state) => state.driver);
const dispatch = useDispatch();
const { driverIsdriving} = useSelector((state) => state.driver);

const [isDriver, setIsDriver] = useState(false);
useEffect(() => {
 dispatch(fetchDrivers(isDriver)) 

  }, []);



   function changeIsDriving(id){
    const min = 100000; 
    const max = 999999;
    const randomNum = Math.floor(Math.random() * (max - min + 1)) + min;
    dispatch(editDriverIsDriving({ id,driverIsdriving,randomNum }))
    .unwrap()
    .then(() => {
      toast.success('Malumot muvaffaqiyatli tahrirlandi!');
    //   window.location.reload();
      dispatch(fetchDrivers(isDriver));
    })
    .catch((err) =>console.log(err))

    
   }
   function deleteDrive(id){
    dispatch(deleteDriver({ id}))
    .unwrap()
    .then(() => {
      toast.success("Malumot muvaffaqiyatli o'chirildi!");
      dispatch(fetchDrivers(isDriver)) 
    })
    .catch((err) =>console.log(err));
  }
  return (
    <div>
       <table className='table table-success'>
        <thead>
          <tr>
            <th>N#</th>
            <th>Mashina Rasmi</th>
            <th>Haydovchilik Guvohnomasi</th>
            <th>Mashina Texpasporti</th>
            <th>Ism Familiya</th>
            <th>Telefon Raqam</th>
            <th>Mashina Rusumi</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {
            drivers.map((item,i)=>{
              return <tr key={item.id}>
                <td>{i+1}</td>
                <td>
                <img className="imageTable" style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.carImg}`} alt="#" />
                  </td> 
                <td>
            <img className="imageTable" style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.driverImg}`} alt="#" />
                  </td> 
                <td>
            <img className="imageTable" style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.cardDocument}`} alt="#" />
               </td> 
                <td>{item.fullName}</td> 
                <td>{item.phoneNumber}</td> 
                <td>{item.carType}</td> 
               <td>
                <button className='saqlash1' onClick={()=>changeIsDriving(item.id)} > <FaCheck style={{border:"none"}} size={"25px"}/></button>
                <button className='deleteButton' onClick={()=>deleteDrive(item.id)} >O'chirish</button>
               </td>
                </tr>
            })
          }
        </tbody>
      </table>
      <ToastContainer toastStyle={{
          backgroundColor: 'white',
          color: 'black',
        }} autoClose={1000} />
    </div>
  )
}

export default AdminNotification
