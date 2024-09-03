import React, { useEffect, useState } from 'react';
import "./Register.css";
import apicall1 from "../../apicall/apicall1";
import PhoneInput from "react-phone-input-2";

function Register() {
  const [chatId, setChatId] = useState(null);
  const [form, setForm] = useState({ fullName: "",phoneNumber:"", carType: "", carImg: "", driverImg: "", cardDocument: "",about:"" });
  const [selectFile1, setSelectFile1] = useState(null);
  const [selectFile2, setSelectFile2] = useState(null);
  const [selectFile3, setSelectFile3] = useState(null);

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const chatIdParam = urlParams.get('chatId');
    if (chatIdParam) {
      setChatId(chatIdParam);
    }
  }, []);

  const save = async () => {
    try {
      let newImg = form.carImg;
      let newImg1 = form.driverImg;
      let newImg2 = form.cardDocument;

      if (selectFile1) {
        const formData1 = new FormData();
        formData1.append('file', selectFile1);
        const res1 = await apicall1('/fileController/photo', 'POST', formData1);
        newImg = res1.data;
      }

      if (selectFile2) {
        const formData2 = new FormData();
        formData2.append('file', selectFile2);
        const res2 = await apicall1('/fileController/photo', 'POST', formData2);
        newImg1 = res2.data;
      }

      if (selectFile3) {
        const formData3 = new FormData();
        formData3.append('file', selectFile3);
        const res3 = await apicall1('/fileController/photo', 'POST', formData3);
        newImg2 = res3.data;
      }

      const updatedForm = {
        ...form,
        carImg: newImg,
        driverImg: newImg1,
        cardDocument: newImg2,
        chatId: chatId
      };

      const res = await apicall1("/user/save", "POST", updatedForm);
      alert("Qo'shildi");
      setForm({ fullName: "", phoneNumber:"",about:"",carType: "", carImg: "", driverImg: "", cardDocument: "" });
      setSelectFile1(null);
      setSelectFile2(null);
      setSelectFile3(null);
    } catch (error) {
      console.error('Error during saving:', error);
      alert('Error during saving: ' + error.message);
    }
  };

  return (
    <div className='register_con'>
      <h3>Ro’yxatdan o’tish uchun quyidagi ma’lumotlarni kiriting</h3>
      <div className="body-con">
        <input
          type="text"
          placeholder='Ism familyasi'
          className='form-control my-2 hy-5'
          value={form.fullName}
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
        />
          <PhoneInput
                        country={"uz"}
                        onChange={(e) => setForm({ ...form, phoneNumber: '+' + e })}
                        value={form.phoneNumber}
                    />
        
        <input
          type="text"
          placeholder='Mashina rusumi'
          className='form-control my-2'
          value={form.carType}
          onChange={(e) => setForm({ ...form, carType: e.target.value })}
        />
         <input
          type="text"
          placeholder='O`zingiz haqingizda yozing'
          className='form-control my-2'
          value={form.about}
          onChange={(e) => setForm({ ...form, about: e.target.value })}
        />
        <input
          type="file"
          className='form-control my-2'
          onChange={(e) => setSelectFile1(e.target.files[0])}
        />
        <input
          type="file"
          className='form-control my-2'
          onChange={(e) => setSelectFile2(e.target.files[0])}
        />
        <input
          type="file"
          className='form-control my-2'
          onChange={(e) => setSelectFile3(e.target.files[0])}
        />
        <button className='btn btn-success' onClick={save}>Saqlash</button>

      </div>
    </div>
  );
}

export default Register;
