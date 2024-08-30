import React, { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import "./date.css";

function DateModal({ minDate, maxDate, onDateSelect, onClose }) {
    const [selectedDate, setSelectedDate] = useState(null);
    const [active, setActive] = useState(false);

    useEffect(() => {
        setActive(true);
    }, []);

    const handleChange = (date) => {
        if (date) {
            const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
            setSelectedDate(dateOnly);
            onDateSelect(dateOnly);
            handleClose();
            console.log(date);
        }
    };

    const handleClose = () => {
        setActive(false);
        setTimeout(onClose, 1000); // 1 second delay to allow the closing animation
    };

    return (
        <div className={`date-modal-overlay ${active ? 'active' : ''}`} onClick={handleClose}>
            <div className="date-modal" onClick={(e) => e.stopPropagation()}>
            <div className="btnCloseIcon" style={{paddingLeft:"8px"}}>

            <button className="btn btn-close" onClick={handleClose}></button>
            </div>
                <h3>Select a Date</h3>
                <DatePicker
                    selected={selectedDate}
                    onChange={handleChange}
                    minDate={new Date(minDate)}
                    maxDate={new Date(maxDate)}
                    dateFormat="yyyy/MM/dd"
                    inline
                />
            </div>
        </div>
    );
}

export default DateModal;
