
import React from 'react';
import rasm from "../../pictures/4.svg";
import "./boglanish.css"
function Boglanish({ isOpen1, onClose }) {
  if (!isOpen1) return null; // Don't render the modal if not open

  return (
    <div className="modal-background" onClick={onClose}>
      <div className="modal-body" onClick={(e) => e.stopPropagation()}>
        <button className="btn btn-close exitBtn" onClick={onClose}>
          
        </button>
        <div className="modal-header-section">
          <h1 className="modal-heading">Savollar Bo'lsa Murojaat qiling!</h1>
          <p className="modal-subheading">Qo’llab-quvvatlash xizmatimiz 09:00-20:00 aloqada</p>
        </div>
        <div className="contact-info-section">
          <div className="contact-image-container">
            <img src={rasm} alt="Contact" style={{objectFit:"cover",width:"100%",height:"100%"}}/>
          </div>
          <div className="contact-text">
            <p className="contact-label">Telefon raqam</p>
            <h4 className="contact-number">(+998) 88-300-03-03</h4>
          </div>
        </div>
        <div className="horizontal-divider"></div>
        <div className="yozmaSavollar">
        <h4 className='yozmaSavollarH4'>Yozma shaklda murojaatingizni qoldiring</h4>
        <div className="inpgroup">
    <div className="form-floating">
        <input type="text" className="form-control" id="name" placeholder="Ismizgiz"/>
        <label htmlFor="name">Ismizgiz</label>
    </div>
    <div className="form-floating">
        <input type="text" className="form-control" id="phone" placeholder="Telefon raqamingiz"/>
        <label htmlFor="phone">Telefon raqamingiz</label>
    </div>
    <div className="form-floating">
        <input type="text" className="form-control" id="message" placeholder="Xabar"/>
        <label htmlFor="message">Xabar</label>
    </div>
    <button className='yonalish'>Yo'nalish</button>
</div>


        
      </div>
      </div>
    </div>
  );
}

export default Boglanish;
