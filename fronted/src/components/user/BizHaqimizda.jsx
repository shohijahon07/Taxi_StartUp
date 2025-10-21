import React from 'react'
import "./boglanish.css";
import rasm1 from "../../pictures/a4.svg";
import rasm2 from "../../pictures/a5.svg";
import rasm3 from "../../pictures/a6.svg";
function BizHaqimizda() {
  
  return (
      <div className="boglanishCard">
        <div className="card">
        <div className="card-header">
          <div className="imgboglanish">

          <img src={rasm1} style={{objectFit:"cover",width:"100%",height:"100%"}}  alt="image"/>
          </div>
        </div>
      
        <div className="card-footer">
          <h4>Bu qanday xizmat?</h4>
          <p>Ushbu xizmat yordamida osongina va tezda O’zbekiston bo’ylab qatnovlarni topish hamda ushbu xizmatdan foydalanish</p>
        </div>
        </div>
        <div className="card">
        <div className="card-header">
          <div className="imgboglanish">

          <img src={rasm2} style={{objectFit:"cover",width:"100%",height:"100%"}}  alt="image"/>
          </div>
        </div>
      
        <div className="card-footer">
          <h4>Qanday foydalaniladi?</h4>
          <p>Qayerdan qayerga ketishingizni hamda sanani tanlaysiz va biz o’zimizda  mavjud bo’lgan qatnovlarni sizga taqdim etamiz.</p>
        </div>
        </div>
        <div className="card">
        <div className="card-header">
          <div className="imgboglanish">

          <img src={rasm3} style={{objectFit:"cover",width:"100%",height:"100%"}}  alt="image"/>
          </div>
        </div>
      
        <div className="card-footer">
          <h4> yordam berish</h4>
          <p>Agar siz saytdan yokida telegram botimizni foydalanishda muammoga duch kelsangiz biz bilan bog’laning</p>
        </div>
        </div>
      </div>
   
  )
}

export default BizHaqimizda
