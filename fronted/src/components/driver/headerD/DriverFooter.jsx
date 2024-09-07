import React from 'react'
import uzbekistan from "../../../pictures/uzbekistan.svg"
import left from "../../../pictures/leftBorder.svg"

function DriverFooter() {
  return (
    <div className="footer">
    <div className="footer-body">
    <img src={left} className='img_footer_dr' alt="#" />
  <div className="footer-left">
  <div className="footer-lf-header">
  <h1 className='pathText'>Yo'nalish</h1>
  <h1 className='pathText'>Men haqimda</h1>
  </div>
  <div className="footer-lf-body">
  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse varius enim in eros elementum tristique.
  
  </div>
  </div>
  <div className="footer-right">
    <img src={uzbekistan} alt="" />
  </div>
  
  
    <img src={left} className='img_footer_dr' alt="#" />
    </div>
    
  </div>
  )
}

export default DriverFooter