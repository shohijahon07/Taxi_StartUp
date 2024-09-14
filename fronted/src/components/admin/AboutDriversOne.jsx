import React, { useContext, useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { fetchDriverOne } from '../../redux/slices/DriverSlice';
import { useNavigate, useParams } from 'react-router-dom';
import "./routesAdmin.css"
import { LanguageContext } from '../language/LanguageContext';
import { fetchRoutesByDriver } from '../../redux/slices/routeDriver';
import { fetchComments } from '../../redux/slices/CommentSlice';
function AboutDriversOne() {
    const {driverOne} = useSelector((state) => state.driver);
    const {routesByDriver} = useSelector((state) => state.routes);
  const { language } = useContext(LanguageContext);
  const {comments} = useSelector((state) => state.comment);
const dispatch = useDispatch();
const navigate=useNavigate()
let { userName } = useParams();    
console.log(userName);
useEffect(() => {
 dispatch(fetchDriverOne(userName)) 
 dispatch(fetchRoutesByDriver(userName)) 

 dispatch(fetchComments({language , userName}))
  }, [userName]);
  console.log(driverOne);

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
                    <img className="imageTable" style={{ objectFit: "cover" }} src={`http:/api/fileController/photo?img=${item.carImg}`} alt="#" />
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
        <p className='pTextDriversOne1'>
            Mijozlarni fikri
           
        </p>
        <table className='table table-success'>
          <thead>
            <tr>
              <th>Yo'lovchini ismi </th>
              <th>Izoh</th>
            </tr>
          </thead>
          <tbody>
            {comments.map((item, i) => (
              <tr key={item.id}>
                <td>{item.text}</td>
                <td>{item.name}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
    </div>
  )
}

export default AboutDriversOne
