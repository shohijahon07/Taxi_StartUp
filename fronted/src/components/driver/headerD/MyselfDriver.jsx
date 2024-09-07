import React, { useEffect, useRef, useState } from 'react';
import DriverHeader from './DriverHeader';
import "../headerD/Myselft.css";
import uzbekistan from "../../../pictures/uzbekistan.svg";
import { addDriverAbout, editDriver, fetchDriverOne, setAbout, setDriver, setEditButtonId, setImg, setImg1, setImg2, setSelectedFile, setSelectedFile1, setSelectedFile2 } from '../../../redux/slices/DriverSlice';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import DriverFooter from './DriverFooter';
import fileicon from "../../../pictures/fileicon.jpg";
import "../../auth/Register.css";
import edit from "../../../pictures/edit.svg"
function MyselfDriver() {
  const [userName, setUserName] = useState("");
  const [isEditing, setIsEditing] = useState(false); // Track editing mode

  useEffect(() => {
    getDriver();
    dispatch(fetchDriverOne(userName));
  }, [userName]);

  const dispatch = useDispatch();
  const { EditButtonId, driverObject, driverOne, selectedFile, selectedFile1, selectedFile2, img, img1, img2, about } = useSelector((state) => state.driver);
  const fileInputRef1 = useRef(null);
  const fileInputRef2 = useRef(null);
  const fileInputRef3 = useRef(null);

  function getDriver() {
    axios({
      url: "http://localhost:8080/api/auth/name",
      method: "get",
      headers: { Authorization: localStorage.getItem("refresh_token") }
    }).then((res) => {
      setUserName(res.data.id);
    });
  }

  const saveDepartment = () => {
    dispatch(editDriver({ EditButtonId, driverObject, img, img1, img2, selectedFile, selectedFile1, selectedFile2 }))
      .unwrap()
      .then(() => {
        toast.success('Malumot muvaffaqiyatli tahrirlandi!');
        dispatch(fetchDriverOne(userName));
      })
      .catch((err) => console.log(err));

    dispatch(setEditButtonId(null));
    dispatch(setDriver({ fullName: '', phoneNumber: '', carType: "", carImg: "", driverImg: "", cardDocument: "", about: "" }));
    if (fileInputRef1.current) fileInputRef1.current.value = '';
    if (fileInputRef2.current) fileInputRef2.current.value = '';
    if (fileInputRef3.current) fileInputRef3.current.value = '';

    setIsEditing(false); // Reset editing mode
  };

  const EditItem = (item) => {
    console.log("salom")
    dispatch(setEditButtonId(item.id));
    dispatch(setImg(item.carImg));
    dispatch(setImg1(item.driverImg));
    dispatch(setImg2(item.cardDocument));
    dispatch(setDriver({
      fullName: item.fullName, phoneNumber: item.phoneNumber,
      carType: item.carType, carImg: item.carImg,
      driverImg: item.driverImg, cardDocument: item.cardDocument, about: item.about
    }));

    setIsEditing(true); // Enable editing mode
  };

  return (
    <div>
      <DriverHeader />
      <div className="container_myself">
        {driverOne.map((item, i) => (
          <div key={i} className="body_myself_img">
            <h1 className='about_text'>O'zim haqimda</h1>
            <div className='about_div'>
              {EditButtonId ?
                <input type="text" className='form-control w-100 h-100' value={driverObject.about}
                  onChange={(e) => dispatch(setDriver({ ...driverObject, about: e.target.value }))}
                /> :
                <div className={"editFather"}>
 <h1>{item.about}</h1>
 <img src={edit} className='btnedit' alt="" />
                </div>
               
              }
            </div>
            <div className="box">
              <div className="body2_center">
                <div className='box_body'>
                  {EditButtonId ?
                    <input type="text" className='form-control w-100 h-100' value={driverObject.fullName}
                      onChange={(e) => dispatch(setDriver({ ...driverObject, fullName: e.target.value }))}
                    /> : 
                    <div className="editFather">
                    <h1>{item.fullName} </h1>

                    <img src={edit} className='btnedit' alt="" />

                    </div>
                  }
                </div>
                <div className='box_body'>
                  {EditButtonId ?
                    <input type="text" className='form-control w-100 h-100' value={driverObject.phoneNumber}
                      onChange={(e) => dispatch(setDriver({ ...driverObject, phoneNumber: e.target.value }))}
                    /> : <div className="editFather">
                    <h1>{item.phoneNumber}</h1>


                    <img src={edit} className='btnedit' alt="" />

                    </div>
                    
                  }
                </div>
                <div className='box_body'>
                  {EditButtonId ?
                    <input type="text" className='form-control w-100 h-100' value={driverObject.carType}
                      onChange={(e) => dispatch(setDriver({ ...driverObject, carType: e.target.value }))}
                    /> : <div className="editFather">
                    <h1>{item.carType}</h1>


                    <img src={edit} className='btnedit' alt="" />

                    </div>
                    
                  }
                </div>
              </div>
              <div className="body3">
                <div className='box_body'>
                  {EditButtonId ?
                     <div className="custom-file-upload2">
                     <input
                       type="file"
                       id="file3" accept="image/*"  ref={fileInputRef1} onChange={(e) => dispatch(setSelectedFile(e.target.files[0]))} 
                     />
                     <label htmlFor="file3">
                       <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                       Mashinani rasmini yuklash
           
                     </label>
                   </div>
                    :
                    <div >
                      
                           <h1>Mashinani rasmi</h1>
                           <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.carImg}`} alt="#" />

                    </div>
                  }
                </div>
                <div className='box_body'>
                  {EditButtonId ?
                   <div className="custom-file-upload2">
                   <input
                     type="file"
                     id="file3" accept="image/*" ref={fileInputRef2} onChange={(e) => dispatch(setSelectedFile1(e.target.files[0]))} 
                   />
                   <label htmlFor="file3">
                     <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                     Haydovchilik guvohnomasi yuklash
         
                   </label>
                 </div>
                 
                 : <div> <h1>Haydovchilik guvohnomasi</h1>
                 <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.driverImg}`} alt="#" />
               </div>
               }
                </div>
                <div className='box_body' >
                  {EditButtonId ?
                  
                  <div className="custom-file-upload2">
                  <input
                    type="file"
                    id="file3" 
                    accept="image/*" ref={fileInputRef3} onChange={(e) => dispatch(setSelectedFile2(e.target.files[0]))} 
                  />
                  <label htmlFor="file3">
                    <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                    Texxpasport rasmi
        
                  </label>
                </div>

                    : <div>
                  <h1>Texxpasport rasmi</h1>
                  <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.cardDocument}`} alt="#" />

                    </div>
                    
                    
                  }
                </div>
                <div className="box_btn">
                  {isEditing ?
                     <button onClick={saveDepartment}>Saqlash</button> :
                    <button onClick={() => EditItem(item)}>Tahrirlash</button>
                  }
                </div>
              </div>
{/* telefon ucun rasmni inputi */}
<div className="imginput">
                <div className='box-body'>
                  {EditButtonId ?
                     <div className="custom-file-upload2">
                     <input
                       type="file"
                       id="file3" accept="image/*"  ref={fileInputRef1} onChange={(e) => dispatch(setSelectedFile(e.target.files[0]))} 
                     />
                     <label htmlFor="file3">
                       <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                       Mashinani rasmini yuklash
           
                     </label>
                   </div>
                    :
                    <div className={"inp_child"} >
                           <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.carImg}`} alt="#" />
          
                           <h1>Mashinani rasmi</h1>
                           
                           <img src={edit} className='btnedit' alt="" />

                    </div>
                  }
                </div>
                <div className='box_body'>
                  {EditButtonId ?
                   <div className="custom-file-upload2">
                   <input
                     type="file"
                     id="file3" accept="image/*" ref={fileInputRef2} onChange={(e) => dispatch(setSelectedFile1(e.target.files[0]))} 
                   />
                   <label htmlFor="file3">
                     <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                     Haydovchilik guvohnomasi yuklash
         
                   </label>
                 </div>
                 
                 : <div className='inp_child'>
                 <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.driverImg}`} alt="#" />
                  
                   <h1>Haydovchilik guvohnomasi</h1>
                 <img src={edit} className='btnedit' alt="" />

</div>
               }
                </div>
                <div className='box_body' >
                  {EditButtonId ?
                  
                  <div className="custom-file-upload2">
                  <input
                    type="file"
                    id="file3" 
                    accept="image/*" ref={fileInputRef3} onChange={(e) => dispatch(setSelectedFile2(e.target.files[0]))} 
                  />
                  <label htmlFor="file3">
                    <img src={fileicon} alt="Upload Icon" className="upload-icon2"  />
                    Texxpasport rasmi
        
                  </label>
                </div>

                    : <div className='inp_child'>
                  <img style={{ objectFit: "cover" }} src={`http://localhost:8080/api/fileController/photo?img=${item.cardDocument}`} alt="#" />

                  <h1>Texxpasport rasmi</h1>
                  <img src={edit} className='btnedit' alt="" />

                    </div>
                    
                    
                  }
                </div>
                <div className="box_btn">
                  {isEditing ?
                     <button onClick={saveDepartment}>Saqlash</button> :
                    <button onClick={() => EditItem(item)}>Tahrirlash</button>
                  }
                </div>
              </div>

              {/* pastga ishlaydigan button  */}
              <div className="box_btn2">  
                  {isEditing ?
                     <button onClick={saveDepartment}>Saqlash</button> :
                    <button onClick={() => EditItem(item)}>Tahrirlash</button>
                  }
                </div>
            </div>
          </div>
        ))}
      </div>
      {/* <DriverFooter/> */}
    </div>
  );
}

export default MyselfDriver;
