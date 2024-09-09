import React, { useEffect, useState } from 'react';
import DriverHeader from './DriverHeader';
import './PathDriver.css';
import Modal from 'react-modal';
import { useSelector, useDispatch } from 'react-redux';
import { fetchToCity } from '../../../redux/slices/toCity';
import { fetchFromCity } from '../../../redux/slices/fromCity';
import b7 from "../../../pictures/b7.svg";
import b12 from "../../../pictures/b12.svg";
import calendar from "../../../pictures/calendar_org.svg";
import 'react-datepicker/dist/react-datepicker.css';
import { addRoute, deleteRoutes, editRoute, fetchRoutesByDriver, setEditButtonId, setKoranCourse } from '../../../redux/slices/routeDriver';
import axios from 'axios';
import b8 from "../../../pictures/b8.svg";
import { LanguageContext } from '../../language/LanguageContext';
import { useContext } from 'react';
import b9 from "../../../pictures/b9.svg";
import b11 from "../../../pictures/b11.svg";
import OptionModal from '../../user/OptionModal';
import DateModal from '../../user/DateModal';
import TimeSelectorModal from './TimeSelectorModal';
import { ImMan } from "react-icons/im";
import SeatSelectionModal from './SeatSelectionModal';
import { ToastContainer, toast } from 'react-toastify';

import { MdAttachMoney } from "react-icons/md";
function PathDriver() { 
  const [openModal, setOpenModal] = useState(false);
  const [seatCount, setSeatCount] = useState(false);
  const [openModal1, setOpenModal1] = useState(false);
  const { driverRout,routesByDriver,EditButtonId} = useSelector((state) => state.routes);
  const [showFromCityModal, setShowFromCityModal] = useState(false);
  const [showToCityModal, setShowToCityModal] = useState(false);
  const [showDateModal, setShowDateModal] = useState(false);
  const [selectedDate, setSelectedDate] = useState('');
  const [hours, setHours] = useState('');
  const [currentOptionType, setCurrentOptionType] = useState('');
  const [userName, setUserName] = useState("");
  const { language } = useContext(LanguageContext);
  const [translatedFromCities, setTranslatedFromCities] = useState([]);
  const [translatedToCities, setTranslatedToCities] = useState([]);
  const { fromCities } = useSelector((state) => state.fromCity);
  const { toCities } = useSelector((state) => state.toCity);

  const dispatch = useDispatch();
  const [minDate, setMinDate] = useState('');
  const [maxDate, setMaxDate] = useState('');
  useEffect(() => {
    dispatch(fetchToCity());
    dispatch(fetchFromCity());
    dispatch(fetchRoutesByDriver(userName));
    getDriver()
    const today = new Date();
    const dayAfterTomorrow = new Date(today);
    dayAfterTomorrow.setDate(today.getDate() + 2);
    const formatDateForInput = (date) => date.toISOString().split('T')[0];
    setMinDate(formatDateForInput(today));
    setMaxDate(formatDateForInput(dayAfterTomorrow));
  }, [userName,dispatch]);

  const cityTranslations = {
    'Uzbekistan': {
        'Toshkent': 'Ташкент',
        'Samarqand': 'Самарканд',
        'Andijon': 'Андижан',
        'Buxoro': 'Бухара',
        "Farg'ona": 'Фергана',
        'Namangan': 'Наманган',
        "Qaraqalpog'iston": 'Каракалпакстан',
        'Surxondaryo': 'Сурхандарьинская',
        'Xorazm': 'Хорезм',
        'Jizzax': 'Джизак',
        'Qashqadaryo': 'Кашкадарья',
        'Sirdaryo': 'Сырдарья',
        'Navoiy': 'Навои',
        'Nukus': 'Нукус',
        'Urganch': 'Урганч',
        'Termiz': 'Термиз',
        'Denov': 'Денов',
        'Xiva': 'Хива',
        'Qarshi': 'Қарши',
        "Qo'qon": 'Қўқон',
    },
    'Russia': {
        'Ташкент': 'Toshkent',
        'Самарканд': 'Samarqand',
        'Андижан': 'Andijon',
        'Бухара': 'Buxoro',
        'Фергана': "Farg'ona",
        'Каракалпакстан': "Qaraqalpog'iston",
        'Наманган': 'Namangan',
        'Хорезм': 'Xorazm',
        'Джизак': 'Jizzax',
        'Кашкадарья': 'Qashqadaryo',
        'Сырдарья': 'Sirdaryo',
        'Навои': 'Navoiy',
        'Сурхандарьинская': 'Surxondaryo',
        'Нукус': 'Nukus',
        'Урганч': 'Urganch',
        'Термиз': 'Termiz',
        'Денов': 'Denov',
        'Хива': 'Xiva',
        "Қўқон": "Qo'qon",
        'Қарши': 'Qarshi',

    }
};
  function getDriver(){
    axios({
      url:"http://localhost:8080/api/auth/name",
      method:"get",
      headers:{Authorization:localStorage.getItem("refresh_token")}
  }).then((res)=>{
    console.log(res.data);
    setUserName(res.data.id)
    setSeatCount(res.data.count)
  })
  }
  const { fromCity, toCity, day,countSide,hour } = driverRout;
  const isFormValid = fromCity && toCity && day &&countSide && hour;

  const monthsUzbek = [
    "yanvar", "fevral", "mart", "aprel", "may", "iyun",
    "iyul", "avgust", "sentabr", "oktabr", "noyabr", "dekabr"
];

const monthsRussian = [
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря"
];

const translateCity = (cityName) => {
    if (language === '2') {
        return cityTranslations['Uzbekistan'][cityName] || cityName;
    }
    return cityName;
};
const translateCity1 = (cityName) => {
    if (language === '2') {
        return cityTranslations['Russia'][cityName] || cityName;
    }
    return cityName;
};
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = date.getDate();
    const monthIndex = date.getMonth();
    const month = language === '2' ? monthsRussian[monthIndex] : monthsUzbek[monthIndex];
    return `${day}-${month}`;
};
const handleDateSelect = (date) => {
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  const formattedDate = date.toLocaleDateString('en-CA', options); 
  setSelectedDate(formattedDate);
  dispatch(setKoranCourse({ ...driverRout, day: formattedDate }))
};
useEffect(() => {
  setTranslatedFromCities(fromCities.map(city => ({
      ...city,
      name: translateCity(city.name)
  })));
  setTranslatedToCities(toCities.map(city => ({
      ...city,
      name: translateCity(city.name)
  })));
}, [fromCities, toCities, language]);


const handleOptionSelect = (option) => {
  console.log(translateCity1(option));
  if (currentOptionType === 'from') {
      dispatch(setKoranCourse({ ...driverRout, fromCity: translateCity1(option) }));
      setShowFromCityModal(false);
  } else if (currentOptionType === 'to') {
      dispatch(setKoranCourse({ ...driverRout, toCity: translateCity1(option) }));
      setShowToCityModal(false);
  }
};
const openModal3 = () => {
    setOpenModal(true);
  console.log("Time modal opened");
};
const openModal4 = () => {
    setOpenModal1(true);
  console.log("Time modal opened");
};

const handleTimeSelect = (selectedTime) => {
    const formattedHour = selectedTime.hour.toString().padStart(2, '0');
    const formattedMinute = selectedTime.minute.toString().padStart(2, '0');
    const formattedTime = `${formattedHour}:${formattedMinute}`;
    dispatch(setKoranCourse({ ...driverRout, hour: formattedTime })); 
    setOpenModal(false); 
  };
  
  const handleSeatSelect = (seatCount) => {
    dispatch(setKoranCourse({ ...driverRout, countSide: seatCount })); 
    setOpenModal1(false);
};
const saveDepartment = () => {
    
      if (EditButtonId) {
        dispatch(editRoute({ EditButtonId, driverRout }))
          .unwrap()
          .then(() => {
            toast.success('Malumot muvaffaqiyatli tahrirlandi!');
            dispatch(fetchRoutesByDriver(userName));
          })
          .catch((err) =>console.log(err));
      } else {
        if (routesByDriver.length >= 1) {
      toast.error("Siz ayni damda faqat 1 ta yo'nalishda ishlay olasiz!")
       
    }else{
        dispatch(addRoute({driverRout,userName }))
          .unwrap()
          .then(() => {
            toast.success("Malumot muvaffaqiyatli qo'shildi!");
            dispatch(fetchRoutesByDriver(userName));
          })
          .catch((err) => toast.error(toast.error("xatolik yuz berdi!")));
      }
    }
    
    
    
    setSelectedDate("")
    dispatch(setEditButtonId(null));
    dispatch(setKoranCourse( { fromCity: '', toCity: '', countSide: '',price:"" ,day:"",hour:"",userId:""}));
    

  };
  const deleteItem = (id) => {
    if (EditButtonId) {
      toast.error('Xatolik yuz berdi!');
    } else {
      dispatch(deleteRoutes({ id }))
        .unwrap()
        .then(() => {
          toast.success("Malumot muvaffaqiyatli o'chirildi!");
          dispatch(fetchRoutesByDriver(userName));
        })
        .catch(() => toast.error('Xatolik yuz berdi!'));
    }
  };
  const [className, setClassName] = useState('inputs'); 

  useEffect(() => {
    const updateClassName = () => {
      if (window.innerWidth <= 720) {
        setClassName('inputs');
      } else if (window.innerWidth > 720 && window.innerWidth <= 1300) {
        setClassName('inputs11');
      } else if (window.innerWidth > 1300 && window.innerWidth <= 1920) {
        setClassName('inputs');
      } else {
        setClassName('inputs'); 
      }
    };

    updateClassName();

    window.addEventListener('resize', updateClassName);

    return () => {
      window.removeEventListener('resize', updateClassName);
    };
  }, []);
  const EditItem = (item) => {
    const specificGroupArea = document.getElementById('specificGroupArea');
    specificGroupArea.scrollIntoView({ behavior: 'smooth' });
    dispatch(setEditButtonId(item.id));
    dispatch(setKoranCourse({ fromCity: item.fromCity, toCity: item.toCity, countSide: item.countSide,price:item.price,day:item.day,hour:item.hour,userId:userName }));
   setSelectedDate(item.day)
  };
  return (
    <div>
      <DriverHeader />
      <div className='path_dr_con'>
        <div className=".path_dr_con2">

          <div className="h1RouterUser">
            <h1 id="specificGroupArea" className={"header_dr"}>Har kuni O'zbekiston bo'ylab qatnovlar</h1>
          </div>

          <div className={className} >
                 <div className="inputChild">
                    <button 
                        type="button"
                        className="btnCity1"
                        onClick={() => {
                            setCurrentOptionType('from');
                            setShowFromCityModal(true);
                        }}
                    >
                        {driverRout.fromCity ? (
                            <span style={{ color: '#303383',fontWeight:"600",letterSpacing:"1.5px" }}>{translateCity(driverRout.fromCity)}</span>
                        ) : (
                            <span style={{ fontWeight: 'normal' }}>
                                {language === "1" ? "Qayerdan  " : "Откуда"}
                            </span>
                        )} 
                        <img src={b7} className='icons' alt="" />
                    </button>
                            </div>

                            <div className="inputChild">
                            <button 
                                type="button"
                                className="btnCity1"
                                onClick={() => {
                                    setCurrentOptionType('to');
                                    setShowToCityModal(true);
                                }}
                                disabled={!driverRout.fromCity} 
                                style={{
                                    opacity: !driverRout.fromCity ? 0.5 : 1, 
                                    cursor: !driverRout.fromCity ? 'not-allowed' : 'pointer' 
                                }}
                            > 
                             {driverRout.toCity ? (
                            <span style={{ color: '#303383',fontWeight:"600",letterSpacing:"1.5px" }}>{translateCity(driverRout.toCity)}</span>
                        ) : (
                            <span style={{ fontWeight: 'normal' }}>
                                { language === "1" ? "Qayerga" : "Куда"}
                            </span>
                        )} 

                        <img src={b7} className='icons' alt="" />
                            </button>
                        </div>

                <div className="inputChild">
                    <button
                        type="button"
                        className="btnCity1"
                        value={formatDate(driverRout.day)}
                        onClick={() => setShowDateModal(true)} 
                        disabled={!driverRout.toCity} 
                        style={{
                            opacity: !driverRout.toCity ? 0.5 : 1, 
                            cursor: !driverRout.toCity ? 'not-allowed' : 'pointer' 
                        }}
                    >

                    {selectedDate? (
                            <span style={{ color: '#303383',fontWeight:"600",letterSpacing:"1.5px" }}>{formatDate(selectedDate)}</span>
                        ) : (
                            <span style={{ fontWeight: 'normal' }}>
                                { language === "1" ? "Qachon" : "Когда"}
                            </span>
                        )} 
                        <img src={calendar} className='icons' alt="" />
                    </button>
                </div>
                <div className="inputChild">
                    <button
                        type="button"
                        className="btnCity1"
                        onClick={openModal3} 
                       
                        
                    >
                        {driverRout.hour ? (
                        <span style={{ color: '#303383', fontWeight: "600", letterSpacing: "1.5px" }}>{driverRout.hour}</span>
                        ) : (
                        <span style={{ fontWeight: 'normal' }}>
                            {language === "1" ? "Vaqt" : "Время"}
                        </span>
                        )}
                        <img src={b12} className='icons' alt="" />
                    </button>
                    </div>
                <div className="inputChild">
                    <button
                        type="button"
                        className="btnCity1"
                        onClick={openModal4} 
                       
                        
                    >
                        {driverRout.countSide ? (
                        <span style={{ color: '#303383', fontWeight: "600", letterSpacing: "1.5px" }}>{driverRout.countSide} ta </span>
                        ) : (
                        <span style={{ fontWeight: 'normal' }}>
                            {language === "1" ? "O'rindiq" : "Сиденье"}
                        </span>
                        )}
                       <ImMan className='icons' />
                    </button>
                    </div>
                <div className="inputChild">
                    <input type='number'
                    value={driverRout.price}
                        className="btnCity1"
                       placeholder={language === "1" ? `Narxi`  : "Цена"}
                        onChange={(e)=> dispatch(setKoranCourse({ ...driverRout, price:e.target.value }))}
                    />
                        
                    
                    </div>

                <div className="inputChild">
                <button
                            className='qidirish1'
                            onClick={saveDepartment}
                           
                        >
                            {language === "1" ? "Saqlash" : "Сохранять"}
                        </button>
                </div>
            </div>

          <div className=""  >
          {routesByDriver.map((item) => (
                   <div className="mapRoutes" style={{backgroundColor:"white"}} >
                    <li className='list-group-item li1'><div className="l1Child1"><p> {formatDate(item.day)}</p> <p>{item.hour}</p></div> <div className="li1Child2"> <p>{item.fromCity}</p> <img src={b8} alt="" /> <p>{item.toCity}</p></div> <div className="li1Child3"><p>{item.price} {language==="1"?"So’m":"Сум"} </p></div></li>
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


                    <li className='list-group-item li3'><div className="l3Child1"><h3>{item.user.fullName}</h3><div className="liDriverPhone"><img src={b11} alt="" /> <p>{item.user.phoneNumber}</p></div> </div><div className="li3Child3"><button onClick={()=>EditItem(item)}>  {language==="1"?"Tahirlash":"Бронирование"}</button> <button onClick={()=>deleteItem(item.id)}>{language==="1"?"O'chirish":"Примечания"}</button>  <p className='carType'>  {item.user.carType}</p></div></li>
                   </div> 
                ))}
</div>



        </div>
      </div>
      {showFromCityModal && (
                <OptionModal
                    options={translatedFromCities}
                    onSelect={handleOptionSelect}
                    onClose={() => setShowFromCityModal(false)}
                />
            )}
            {showToCityModal && (
                <OptionModal
                    options={translatedToCities}
                    onSelect={handleOptionSelect}
                    onClose={() => setShowToCityModal(false)}
                />
            )}
            {showDateModal && (
                <DateModal
                    minDate={minDate}
                    maxDate={maxDate}
                    onDateSelect={handleDateSelect}
                    onClose={() => setShowDateModal(false)}
                />
            )}
          {openModal && (
  <TimeSelectorModal
    options={hours}
    onSelect={handleTimeSelect}
    onClose={() => setOpenModal(false)} 
  />
)}
 {openModal1 && (
                <SeatSelectionModal
                    onSelect={handleSeatSelect}
                    isTrue={seatCount}
                    onClose={() => setOpenModal1(false)}
                />
            )}

<ToastContainer toastStyle={{
          backgroundColor: 'white',
          color: 'black',
        }} autoClose={1000} />
    </div>
  );
}

export default PathDriver;
 