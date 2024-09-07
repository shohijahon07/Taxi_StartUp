import React, { useEffect, useState } from 'react';
import DriverHeader from './DriverHeader';
import './PathDriver.css';
import Modal from 'react-modal';
import { useSelector, useDispatch } from 'react-redux';
import { fetchToCity } from '../../../redux/slices/toCity';
import { fetchFromCity } from '../../../redux/slices/fromCity';
import b7 from "../../../pictures/b7.svg";
import calendar from "../../../pictures/calendar_org.svg";
import time from "../../../pictures/Vector.svg"
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { addRoute, deleteRoutes, editRoute, fetchRoutesByDriver, setEditButtonId, setKoranCourse } from '../../../redux/slices/routeDriver';
import axios from 'axios';
import b8 from "../../../pictures/b8.svg";
import { LanguageContext } from '../../language/LanguageContext';
import { useContext } from 'react';
import b9 from "../../../pictures/b9.svg";
import b11 from "../../../pictures/b11.svg";
import uzbekistan from "../../../pictures/uzbekistan.svg"
import DriverFooter from './DriverFooter';
Modal.setAppElement('#root'); 

function PathDriver() {
  const [modalFromIsOpen, setModalFromIsOpen] = useState(false);
  const [modalToIsOpen, setModalToIsOpen] = useState(false);
  const [modalDateIsOpen, setModalDateIsOpen] = useState(false);
  const [modalTimeIsOpen, setModalTimeIsOpen] = useState(false); 
  const { EditButtonId, driverRout,driverRoutes} = useSelector((state) => state.routes);
  const [userName, setUserName] = useState("");
  const { language } = useContext(LanguageContext);

  const { fromCities } = useSelector((state) => state.fromCity);
  const { toCities } = useSelector((state) => state.toCity);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchToCity());
    dispatch(fetchFromCity());
    dispatch(fetchRoutesByDriver(userName));
    getDriver()

  }, [userName,dispatch]);
  function getDriver(){
    axios({
      url:"http://localhost:8080/api/auth/name",
      method:"get",
      headers:{Authorization:localStorage.getItem("refresh_token")}
  }).then((res)=>{
    setUserName(res.data.id)
  })
  }
  const openFromModal = () => setModalFromIsOpen(true);
  const closeFromModal = () => setModalFromIsOpen(false);

  const openToModal = () => setModalToIsOpen(true);
  const closeToModal = () => setModalToIsOpen(false);

  const openDateModal = () => setModalDateIsOpen(true);
  const closeDateModal = () => setModalDateIsOpen(false);

  const openTimeModal = () => setModalTimeIsOpen(true); // New function to open Time Modal
  const closeTimeModal = () => setModalTimeIsOpen(false); // New function to close Time Modal
console.log(driverRoutes);
  return (
    <div>
      <DriverHeader />
      <div className='path_dr_con'>
        <div className=".path_dr_con2">

          <div className="h1RouterUser">
            <h1 className={"header_dr"}>Har kuni O'zbekiston bo'ylab qatnovlar</h1>
          </div>

          <div className="inputs">
            <div className="inputChild">
              <button className={"btn_from"} onClick={openFromModal}>
                Qayerdan
                <img src={b7} className='locationImg' alt="" />
              </button>
            </div>
            <div className="inputChild">
              <button className={"btn_from"} onClick={openToModal}>
                Qayerga
                <img src={b7} className='locationImg' alt="" />
              </button>
            </div>
            <div className="inputChild">
              <button className="btn_from" onClick={openDateModal}>
                Sana 
                <img src={calendar} className='locationImg' alt="" />
              </button>
            </div>
            <div className="inputChild">
              <button className="btn_from" onClick={openTimeModal}>
                Soat 
                <img src={time} className='locationImg' alt="" />
              </button>
            </div>
            <div className='inputChild'>
<button className='save_btn'>Saqlash</button>
            </div>
          </div>

          <div className=""  >
          {driverRoutes.map((item, index) => (
                   <div className="mapRoutes"  style={{backgroundColor:"white"}}>
                    <li className='list-group-item li1'><div className="l1Child1"><p> {item.day}</p> <p>{item.hour}</p></div> <div className="li1Child2"> <p>{item.fromCity}</p> <img src={b8} alt="" /> <p>{item.toCity}</p></div> <div className="li1Child3"><p>{item.price} {language==="1"?"So’m":"Сум"} </p></div></li>
                    <li className="list-group-item li2">
                                <div className="l2Child1">
                                    {Array.from({ length: Math.min(item.countSide, 6) }).map((_, index) => (
                                    <img key={index} src={b9} alt="" />
                                    ))}
                                </div>
                                <div className="li2Child2">
                                    <p>{language==="1"?" Bo'sh O'rindiqlar soni:":"Количество свободных мест:"}</p>
                                    <h6>{item.countSide}</h6>
                                </div>
                                <div className="li2Child3">
                                    <h6>{language==="1"?"Mashina rusumi":"Модель автомобиля"}</h6>
                                    <p>{item.user.carType}</p>
                                </div>
                                </li>


                    <li className='list-group-item li3'><div className="l3Child1"><h3>{item.user.fullName}</h3><div className="liDriverPhone"><img src={b11} alt="" /> <p>{item.user.phoneNumber}</p></div> </div><div className="li3Child3"><button onClick={()=>openModal1(item.user.chatId)}>  {language==="1"?"Tahirlash":"Бронирование"}</button> <button onClick={()=>openIzohlar(item.user.id)}>{language==="1"?"O'chirish":"Примечания"}</button>  <p className='carType'>  {item.user.carType}</p></div></li>
                   </div> 
                ))}
</div>



        </div>
      </div>
 <DriverFooter/>
      {/* Modal for "Qayerdan" */}
      <Modal
        isOpen={modalFromIsOpen}
        onRequestClose={closeFromModal}
        contentLabel="Qayerdan Modal"
        style={{
          content: {
            width: "600px",
            height: "360px",
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            backgroundColor: "#E1E5F1",
          },
          overlay: {
            backgroundColor: 'rgba(0, 0, 0, 0.75)',
          },
        }}
      >
        <button className="modal-close-btn" onClick={closeFromModal}>&times;</button>
        <h2 className='text-center'>Hududni tanlang!</h2>
        <ul style={{ marginTop: "15px", display: "flex", flexWrap: "wrap" }} className="option-list">
          {fromCities.map((option) => (
            <li className='fromCityLi' key={option.name}>{option.name}</li>
          ))}
        </ul>
      </Modal>

      {/* Modal for "Qayerga" */}
      <Modal
        isOpen={modalToIsOpen}
        onRequestClose={closeToModal}
        contentLabel="Qayerga Modal"
        style={{
          content: {
            width: "600px",
            height: "360px",
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            backgroundColor: "#E1E5F1",
          },
          overlay: {
            backgroundColor: 'rgba(0, 0, 0, 0.75)',
          },
        }}
      >
        <button className="modal-close-btn" onClick={closeToModal}>&times;</button>
        <h2 className='text-center'>Hududni tanlang!</h2>
        <ul style={{ marginTop: "15px", display: "flex", flexWrap: "wrap" }} className="option-list">
          {toCities.map((option) => (
            <li className='fromCityLi' key={option.name}>{option.name}</li>
          ))}
        </ul>
      </Modal>

      {/* Modal for "Sana" */}
      <Modal
        isOpen={modalDateIsOpen}
        onRequestClose={closeDateModal}
        contentLabel="Sana Modal"
        style={{
          content: {
            width: "600px",
            height: "360px",
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            backgroundColor: "#E1E5F1",
          },
          overlay: {
            backgroundColor: 'rgba(0, 0, 0, 0.75)',
          },
        }}
      >
        <div className={'active'} onClick={closeDateModal}>
          <div className="date-modal" onClick={(e) => e.stopPropagation()}>
            <div className="btnCloseIcon" style={{paddingLeft:"8px"}}>
              <button className="btn btn-close" onClick={closeDateModal}></button>
            </div>
            <h3>Select a Date</h3>
            <DatePicker
              inline
            />
          </div>
        </div>
      </Modal>

      {/* Modal for "Soat" */}
      <Modal
        isOpen={modalTimeIsOpen} // Time Modal state
        onRequestClose={closeTimeModal}
        contentLabel="Soat Modal"
        style={{
          content: {
            width: "600px",
            height: "360px",
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            backgroundColor: "#E1E5F1",
          },
          overlay: {
            backgroundColor: 'rgba(0, 0, 0, 0.75)',
          },
        }}
      >
        <div className={'active'} onClick={closeTimeModal}>
          <div className="time-modal" onClick={(e) => e.stopPropagation()}>
            <div className="btnCloseIcon" style={{paddingLeft:"8px"}}>
              <button className="btn btn-close" onClick={closeTimeModal}></button>
            </div>
            <h3>Select a Time</h3>
            <DatePicker
              selected={null} // Placeholder for selected time
              onChange={(time) => console.log(time)} // Handle time selection
              showTimeSelect
              showTimeSelectOnly
              timeIntervals={15}
              timeCaption="Time"
              dateFormat="HH:mm"
              inline
            />
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default PathDriver;
 