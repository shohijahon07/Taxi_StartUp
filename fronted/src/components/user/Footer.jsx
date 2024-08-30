import React from 'react'
import "./footer.css"
import rasm from "../../pictures/5.svg";
function Footer() {
  return (
    <div className='footer'>
            <div className="line2"></div>
        <div className="footer3">
          <div className="textFooter">
              <ul className='foterUl'>
        <li className='list-group-item'>Bosh Sahifa</li>
        <li  className='list-group-item'>Bosh Haqimizda</li>
        <li  className='list-group-item'>Bog'lanish</li>
      </ul>
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum <br /> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum <br /> tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros <br /> elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in <br /> eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius <br /> enim in eros elementum tristique.</p>
          </div>
            <div className="footerYer">
                <img src={rasm} alt="" style={{objectFit:"cover",width:"100%",height:"100%"}} />
            </div>
        </div>
    

            <div className="line2"></div>
    </div>
  )
}

export default Footer
