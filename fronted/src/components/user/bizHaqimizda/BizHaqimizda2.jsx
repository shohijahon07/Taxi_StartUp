import React from "react";
import "./bizhaqimizda.css";
import b1 from "../../../pictures/b1.svg";
import b2 from "../../../pictures/b2.svg";
import b3 from "../../../pictures/b3.svg";
import b4 from "../../../pictures/b4.svg";
import logo from "../../../pictures/Group (1).svg";

const BizHaqimizda2 = () => {
 
  return (
      <div className="bizhaqimizda-modal-content">
        <div className="bizhaqimizda-modal-header">
          <img src={logo} alt="Logo" className="bizhaqimizda-modal-logo" />
          <h2 className="bizhaqimizda-h2">Biz haqimizda</h2>
  
        </div>
        <div className="bizhaqimizda-modal-body">
         
            <div className="bizhaqimizda-info-item">
              <img src={b1} alt="Info 1" />
              <h5>Надежная сервисная компания</h5>
              <p>Клиентов обслуживает наш собственный сервисный центр Tripinsurance. Мы отслеживаем качество оказаннной медицинской помощи, отбираем лучшие клиники для наших клиентов.</p>
           
            </div>
            <div className="bizhaqimizda-info-item">
              <img src={b2} alt="Info 2" />
              <h5>Надежная сервисная компания</h5>
              <p>Клиентов обслуживает наш собственный сервисный центр Tripinsurance. Мы отслеживаем качество оказаннной медицинской помощи, отбираем лучшие клиники для наших клиентов.</p>
           
            </div>
            <div className="bizhaqimizda-info-item">
              <img src={b3} alt="Info 3" />
              <h5>Надежная сервисная компания</h5>
              <p>Клиентов обслуживает наш собственный сервисный центр Tripinsurance. Мы отслеживаем качество оказаннной медицинской помощи, отбираем лучшие клиники для наших клиентов.</p>
           
            </div>
            <div className="bizhaqimizda-info-item">
              <img src={b4} alt="Info 4" />
              <h5>Надежная сервисная компания</h5>
              <p>Клиентов обслуживает наш собственный сервисный центр Tripinsurance. Мы отслеживаем качество оказаннной медицинской помощи, отбираем лучшие клиники для наших клиентов.</p>
           
           
          </div>
        </div>
      </div>

  );
};

export default BizHaqimizda2;
