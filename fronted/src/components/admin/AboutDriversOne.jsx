import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { fetchDriverOne } from '../../redux/slices/DriverSlice';
import { useNavigate, useParams } from 'react-router-dom';
import "./routesAdmin.css"
import { fetchRoutesByDriver } from '../../redux/slices/routeDriver';
function AboutDriversOne() {
    const {driverOne} = useSelector((state) => state.driver);
    const {routesByDriver} = useSelector((state) => state.routes);
const dispatch = useDispatch();
const navigate=useNavigate()
let { userName } = useParams();    

useEffect(() => {
 dispatch(fetchDriverOne(userName)) 
 dispatch(fetchRoutesByDriver(userName)) 
 console.log(routesByDriver);
  }, [userName]);

  function BackPage(){
        navigate(`/bosh_sahifa`)
  }
  return (
    <div className='begin1'>
        
        <div className='w-100'>
    <div className='driverOneHaeder' onClick={BackPage}>
        <button className='back'>Orqaga</button>
    </div>
    {
        driverOne.map((item) => (
            <ul className='form-control ulDriversOne' key={item.id}>
                <li className='list-group-item'>{item.fullName}</li>
                <li className='list-group-item'>{item.phoneNumber}</li>
                <li className='list-group-item'>
                    <img className="imageTable" style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.carImg}`} alt="#" />
                </li>
            </ul>
        ))
    }
    {
        routesByDriver.length > 0 ? (
          routesByDriver.map((itm) => (
                <div key={itm.id}>
                    <h2></h2>
                    <p className='pTextDriversOne'>Yo'nalish nomi: {itm.fromCity} - {itm.toCity}</p>
                </div>
            ))
        ) : (
            <p>No routes available.</p>
        )
    }
</div>

      <div>
        <p className='pTextDriversOne1'>Mijozlar Fikri</p>
      </div>
      
    </div>
  )
}

export default AboutDriversOne
