import React, { useEffect, useState } from 'react'
import DriverHeader from './headerD/DriverHeader'
import "./driverLanding.css"
function DriverLanding() {
  const [minDate, setMinDate] = useState('');

  const [maxDate, setMaxDate] = useState('');

  useEffect(() => {
    const today = new Date();
    const tomorrow = new Date(today);
    const dayAfterTomorrow = new Date(today);

    // Set tomorrow and dayAfterTomorrow dates
    tomorrow.setDate(today.getDate() + 1);
    dayAfterTomorrow.setDate(today.getDate() + 2);

    // Format dates as YYYY-MM-DD
    const formatDate = (date) => date.toISOString().split('T')[0];

    setMinDate(formatDate(today));
    setMaxDate(formatDate(dayAfterTomorrow));
  }, []);

  return (
    <div>
      <div className="begin">
        <div className="inputs">
        <div className="inputChild">
          <p>Qayerdan</p>
          <input type="text" placeholder='Qayerdan' className='form-control' />
        </div>
        <div className="inputChild">
          <p>Qayerga</p>
          <input type="text" placeholder='Qayerga' className='form-control' />
        </div>
        <div className="inputChild">
        <p>Qachon</p>
      <input
        type="date"
        placeholder="Qachon"
        className="form-control"
        min={minDate}
        max={maxDate}
      />
        </div>
        <div className="inputChild">
        <p>Soat va minut</p>
      <input
        type="time"
        className="form-control"
      />
        </div>
        <div className="inputChild">
          <p>Joylar Soni</p>
          <input type="text" placeholder='Joylar Soni' className='form-control' />
        </div>
        <div className="inputChild">
          <p>Narxi</p>
          <input type="text" placeholder='Narxi' className='form-control' />
        </div>
      </div>
      <div className="buttonSave">
        <button className='saqlash'>Saqlash</button>
      </div>
      </div>
      
    </div>
  )
}

export default DriverLanding
