import React, { useContext, useEffect, useState } from 'react';
import './optionModal.css';
import { LanguageContext } from '../language/LanguageContext';
import { useDispatch, useSelector } from 'react-redux';
import { setActive } from '../../redux/slices/CommentSlice';

const OptionModal = ({ options, onSelect, onClose }) => {
    const dispatch=useDispatch()
    const { active } = useSelector((state) => state.comment);
    const { language } = useContext(LanguageContext);
    useEffect(() => {
        dispatch(setActive(true));
    }, []);

    const handleClose = () => {
        dispatch(setActive(false));
        setTimeout(onClose, 1000); 
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
