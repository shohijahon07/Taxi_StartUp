import React, { useContext, useEffect, useState } from 'react';
import './optionModal.css';
import { LanguageContext } from '../language/LanguageContext';

const OptionModal = ({ options, onSelect, onClose }) => {
    const [active, setActive] = useState(false);
    const { language } = useContext(LanguageContext);

    useEffect(() => {
        setActive(true);
    }, []);

    const handleClose = () => {
        setActive(false);
        setTimeout(onClose, 1000); // 1 sekund kutish animatsiya tugashi uchun
    };

    return (
        <div className={`option-modal-overlay ${active ? 'active' : ''}`} onClick={handleClose}>
            <div className="option-modal" onClick={(e) => e.stopPropagation()}>
                <div className="btnCloseIcon" style={{paddingRight:"10px"}}>

                <button className="btn btn-close" onClick={handleClose}>x</button>
                </div>
                <div className="CityH1">

                <p>{language==="1"?"Hududni tanlang!":"Выбирайте регион!"}</p>
                </div>
                <ul style={{ marginTop: "15px",display:"flex",flexWrap:"wrap" }} className="option-list">
                    {options.map((option) => (
                        <li className='fromCityLi' key={option.name} onClick={() => onSelect(option.name)}>
                            {option.name}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default OptionModal;
