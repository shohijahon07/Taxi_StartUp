// src/components/user/Landing.jsx
import React, { useContext, useEffect, useState } from 'react';
import "./user.css";
import logo from "../../pictures/Group (1).svg";
import { useNavigate } from 'react-router-dom';
import { LanguageContext } from '../language/LanguageContext';
import telegram from "../../pictures/telegram.svg";
import BizHaqimizdaModal from './bizHaqimizda/BizHaqimizdaModal';
import Boglanish from './Boglanish';

function Landing() {
    const { language, changeLanguage } = useContext(LanguageContext);
    const navigate = useNavigate();

    function goLanding() {
        navigate("/");
    }
    const [isModalOpen, setIsModalOpen] = useState(false);
    
    const [isModalOpen1, setIsModalOpen1] = useState(false);

    const openModal1 = () => setIsModalOpen1(true);
    const closeModal1 = () => setIsModalOpen1(false);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

    useEffect(() => { 
        console.log("Current language:", language);
    }, [language]);

    return (
        <div className="fatherNavbar">
              <div className="navbar">
            <div className="leftUserHeader">
                <img src={logo} className='logoImg'  onClick={goLanding} />
            </div>
            <div className="middleUserHeader">
                <ul className='navbarUl'>
                    <li className='list-group-item'>Bosh Sahifa</li>
                    <li className='list-group-item'  onClick={openModal}>Biz Haqimizda</li>
                    <li className='list-group-item'  onClick={openModal1}>Bog'lanish</li>
                </ul>
            </div>
            <div className="rightUserHeader">
                <select className='form-select' value={language} onChange={(e) => changeLanguage(e.target.value)}>
                    <option value="1">O'zbek</option>
                    <option value="2">Rus</option>
                </select>
                <img src={telegram} className='imgTelegram' alt="" />
            </div>
        </div>
        <BizHaqimizdaModal isOpen={isModalOpen} onClose={closeModal} />
        <Boglanish isOpen1={isModalOpen1} onClose={closeModal1} />
        </div>
      
    );
}

export default Landing;
