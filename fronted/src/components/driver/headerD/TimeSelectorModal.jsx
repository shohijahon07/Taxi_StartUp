import React, { useState, useCallback, useContext } from 'react';
import './TimeSelectorModal.css';
import { LanguageContext } from '../../language/LanguageContext';

const TimeSelectorModal = ({ onClose, onSelect }) => {
  const [selectedHour, setSelectedHour] = useState(new Date().getHours());
  const [selectedMinute, setSelectedMinute] = useState(new Date().getMinutes());
  const { language } = useContext(LanguageContext);

  const hours = Array.from({ length: 24 }, (_, i) => i); // 0 to 23 hours
  const minutes = Array.from({ length: 60 }, (_, i) => i); // 0 to 59 minutes

  const handleConfirm = () => {
    onSelect({ hour: selectedHour, minute: selectedMinute });
    onClose();
  };

  // Function to wrap around the list
  const getWrappedIndex = (array, index) => {
    const length = array.length;
    return (index + length) % length; 
  };

  const handleSelect = (value, setSelected) => {
    setSelected(value);
  };

  return (
    <div className="time-selector-overlay" onClick={onClose}>
      <div className="time-selector-modal" onClick={(e) => e.stopPropagation()}>
        <div className="time-selector-header">
          <h2>{language==="1"?"Vaqtni tanlang!":"Выберите время!"}</h2>
          <button className=" btn btn-close exitBtn" onClick={onClose}></button>
        </div>
        <div className="time-selector-body">
          <div className="time-selector-picker">
            <div className="time-selector-column">
              <div className="spinner">
                {[-2, -1, 0, 1, 2].map((offset) => {
                  const hourIndex = getWrappedIndex(hours, selectedHour + offset);
                  const hour = hours[hourIndex];
                  return (
                    <div
                      key={hourIndex}
                      onClick={() => handleSelect(hour, setSelectedHour)}
                      className={`spinner-item ${selectedHour === hour ? 'selected' : ''}`}
                      style={{
                        fontSize: selectedHour === hour ? '24px' : '20px',
                        color: selectedHour === hour ? 'blue' : 'black',
                      }}
                    >
                      {hour < 10 ? `0${hour}` : hour}
                    </div>
                  );
                })}
              </div>
            </div>
            <div className="time-selector-divider">-</div>
            <div className="time-selector-column">
              <div className="spinner">
                {[-2, -1, 0, 1, 2].map((offset) => {
                  const minuteIndex = getWrappedIndex(minutes, selectedMinute + offset);
                  const minute = minutes[minuteIndex];
                  return (
                    <div
                      key={minuteIndex}
                      onClick={() => handleSelect(minute, setSelectedMinute)}
                      className={`spinner-item ${selectedMinute === minute ? 'selected' : ''}`}
                      style={{
                        fontSize: selectedMinute === minute ? '24px' : '20px',
                        color: selectedMinute === minute ? 'blue' : 'black',
                      }}
                    >
                      {minute < 10 ? `0${minute}` : minute}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
        <div className="time-selector-actions">
          <button className="btn-confirm" onClick={handleConfirm}>Saqlash</button>
        </div>
      </div>
    </div>
  );
};

export default TimeSelectorModal;
