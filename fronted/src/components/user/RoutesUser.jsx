import React, { useContext, useEffect, useState } from 'react';
import Landing from './Landing';
import { useDispatch, useSelector } from 'react-redux';
import { fetchToCity } from '../../redux/slices/toCity';
import { fetchFromCity } from '../../redux/slices/fromCity';
import { fetchRoutes, fetchRoutesByDate, setKoranCourse } from '../../redux/slices/routeDriver';
import "./user.css";
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import { LanguageContext } from '../language/LanguageContext';
import DateModal from './DateModal';
import OptionModal from './OptionModal'; // Import the OptionModal
import calendar from "../../pictures/calendar.png";
import logo from "../../pictures/Group (1).svg";
import Boglanish from './Boglanish';
import BizHaqimizda from './BizHaqimizda';
import Footer from './Footer';


function RoutesUser() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { language } = useContext(LanguageContext);
    const { toCities } = useSelector((state) => state.toCity);
    const { fromCities } = useSelector((state) => state.fromCity);
    const { allRoutes, driverRout } = useSelector((state) => state.routes);
    const [minDate, setMinDate] = useState('');
    const [maxDate, setMaxDate] = useState('');
    const [translatedFromCities, setTranslatedFromCities] = useState([]);
    const [translatedToCities, setTranslatedToCities] = useState([]);
    const [showFromCityModal, setShowFromCityModal] = useState(false);
    const [showToCityModal, setShowToCityModal] = useState(false);
    const [showDateModal, setShowDateModal] = useState(false); // Ensure this state is defined
    const [selectedDate, setSelectedDate] = useState('');
    const [currentOptionType, setCurrentOptionType] = useState('');
    const [loading, setLoading] = useState(false);
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
        }
    };

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
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const day = date.getDate();
        const monthIndex = date.getMonth();
        const month = language === '2' ? monthsRussian[monthIndex] : monthsUzbek[monthIndex];
        return `${day}-${month}`;
    };

    const handleDateSelect = (date) => {
        // Format: YYYY-MM-DD
        const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
        const formattedDate = date.toLocaleDateString('en-CA', options); // 'en-CA' locale for YYYY-MM-DD format
        setSelectedDate(formattedDate);
        setShowDateModal(false); // Close the date modal
        dispatch(setKoranCourse({ ...driverRout, day: formattedDate }));
    };
    

    const handleOptionSelect = (option) => {
        if (currentOptionType === 'from') {
            dispatch(setKoranCourse({ ...driverRout, fromCity: option }));
            setShowFromCityModal(false);
        } else if (currentOptionType === 'to') {
            dispatch(setKoranCourse({ ...driverRout, toCity: option }));
            setShowToCityModal(false);
        }
    };

    useEffect(() => {
        dispatch(fetchToCity());
        dispatch(fetchFromCity());
        setLoading(true); // Ma'lumotlar yuklanishini boshlash
    
        // dispatch(fetchRoutes())
        //     .unwrap()
        //     .then(() => {
        //         setLoading(false); // Ma'lumotlar yuklandi
        //     })
        //     .catch((err) => {
        //         setLoading(false); // Xato bo'lsa ham
        //         console.log(err);
        //     });
    
        const today = new Date();
        const dayAfterTomorrow = new Date(today);
        dayAfterTomorrow.setDate(today.getDate() + 2);
        const formatDateForInput = (date) => date.toISOString().split('T')[0];
        setMinDate(formatDateForInput(today));
        setMaxDate(formatDateForInput(dayAfterTomorrow));
    }, [dispatch, language]);
    

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

    function getRouteOne() {
        if (!driverRout.fromCity || !driverRout.toCity || !driverRout.day) {
            toast.error('Iltimos, barcha maydonlarni to\'ldiring!');
            return; // Funksiyani to'xtatish
        }
    
        setLoading(true); // Yuklanishni boshlash
    
        dispatch(fetchRoutesByDate(driverRout))
            .unwrap()
            .then(() => {
                toast.success('Malumot muvaffaqiyatli tahrirlandi!');
            })
            .catch((err) => {
                console.log(err);
                toast.error('Ma\'lumotlarni olishda xatolik yuz berdi!');
            })
            .finally(() => {
                setLoading(false); // Loading holatini to'xtatish
            });
    
        // Clear the form fields
        dispatch(setKoranCourse({ fromCity: '', toCity: '', countSide: '', price: "", day: "", hour: "", userId: "" }));
    }
    

    function goDriverOne(userName) {
        navigate(`/id_haydovchi/${userName}`);
    }

    return (
        <div>
            <Landing />
            <div className="routUser">
                <div className="imageBacground">
                <div className="h1RouterUser">
                <h1 className=''>Har kuni O’zbekiston bo’ylab qatnovlar</h1>

                </div>
                 <div className="inputs" >
                <div className="inputChild">
                    <button 
                        type="button"
                        className="btnCity"
                        onClick={() => {
                            setCurrentOptionType('from');
                            setShowFromCityModal(true);
                        }}
                    >
                        {driverRout.fromCity || (language === "1" ? "Qayerdan" : "Откуда")}
                    </button>
                </div>
                <div className="inputChild">
                    <button 
                        type="button"
                        className="btnCity"
                        onClick={() => {
                            setCurrentOptionType('to');
                            setShowToCityModal(true);
                        }}
                    > 
                        {driverRout.toCity || (language === "1" ? "Qayerga" : "Куда")}
                    </button>
                </div>
                <div className="inputChild">
                    <button
                        type="button"
                        className="btnCity"
                        onClick={() => setShowDateModal(true)} // Ensure this triggers the modal
                    >
                        {selectedDate || (language === "1" ? "Qachon" : "Когда")}
                        
                        <img src={calendar} className='imgCalendar' alt="" />
                    </button>
                </div>
                <div className="inputChild">
                    <p style={{ height: "20px" }}></p>
                    <button className='qidirish' onClick={getRouteOne}>{language==="1"?"Qidirish":"Поиск"}</button>
                </div>
            </div>
            <div className="allRoutes" >
                {
                // loading ? (
                //     <div className="loading-container">
                //         <img src={logo} alt="Loading..." className="loading-image" />
                //     </div>
                // ) : (
                allRoutes.map((item, index) => (
                    <li className='list-group-item' key={index}>
                        <div className="ketish">
                            <span>{language==="1"?"Ketish:":"Отправление:"}</span>
                            <p>{formatDate(item.day)} {item.hour}</p>
                        </div>
                        <div className="routes12">
                            <span>{language==="1"?"Yo'nalish nomi:":"Название маршрута:"}</span>
                            <p>{translateCity(item.fromCity)} - {translateCity(item.toCity)}</p>
                        </div>
                        <div className="joylar">
                            <span>{language==="1"?"Joylar Soni:":"Количество мест:"}</span>
                            <p>{item.countSide}</p>
                        </div>
                        <div className="userButtons">
                            <button className='saqlash3'>{language==="1"?"Band Qilish":"Бронирование"}</button>
                            <button className='saqlash3' onClick={() => goDriverOne(item.user.id)}>{language==="1"?"Haydovchi Haqida":"О водителе"}</button>
                        </div>
                    </li>
                // )
                ))}
                <BizHaqimizda/>
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
                    onClose={() => setShowDateModal(false)} // Ensure this function is defined
                />
            )}
            <ToastContainer toastStyle={{ backgroundColor: 'white', color: 'black' }} autoClose={1000} />
            <Boglanish/>
            {/* <Footer/> */}
            </div>
            
           
        </div>
    );
}

export default RoutesUser;
