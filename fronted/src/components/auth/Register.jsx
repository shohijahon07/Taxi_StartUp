import React, { useEffect, useState } from 'react';
import "./Register.css";
import apicall1 from "../../apicall/apicall1";

function Register() {
  useEffect(() => {
    // Retrieve chatId from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const chatIdParam = urlParams.get('chatId');
    if (chatIdParam) {
      setChatId(chatIdParam);
    }
  }, []);
  
  const [form, setForm] = useState({ fullName: "", carType: "", carImg: "", driverImg: "", carDocument: "" });
  const [selectFile1, setSelectFile1] = useState(null);
  const [selectFile2, setSelectFile2] = useState(null);
  const [selectFile3, setSelectFile3] = useState(null);
  async function save() {
    let newImg = form.carImg;
    let newImg1 = form.driverImg;
    let newImg2 = form.carDocument;

    if (selectFile1) {
      const formData1 = new FormData();
      formData1.append('file', selectFile1);
      const res1 = await apicall1('/fileController/photo', "POST", formData1);
      newImg = res1.data;
    }

    if (selectFile2) {
      const formData2 = new FormData();
      formData2.append('file', selectFile2);
      const res2 = await apicall1('/fileController/photo', "POST", formData2);
      newImg1 = res2.data;
    }

    if (selectFile3) {
      const formData3 = new FormData();
      formData3.append('file', selectFile3);
      const res3 = await apicall1('/fileController/photo', "POST", formData3);
      newImg2 = res3.data;
    }

    // Include chatId in the fouserrm data
    const updatedForm = {
      ...form,
      carImg: newImg,
      driverImg: newImg1,
      carDocument: newImg2,
      chatId: chatId // Include chatId in the data being sent to the backend
    };

    await apicall1("/user/save", "POST", updatedForm)
      .then(() => {
        alert("Qo'shildi");
        setForm({ fullName: "", carType: "", carImg: "", driverImg: "", carDocument: "" });
        setSelectFile1(null);
        setSelectFile2(null);
        setSelectFile3(null);
      })
      .catch(error => {
        console.error('Error during saving:', error);
      });
  }

  return (
    <div className='register_con'>
      <h3>Ro’yxatdan o’tish uchun quyidagi ma’lumotlarni kiriting</h3>
      <div className="body-con">
        <input
          type="text"
          placeholder='Ism familyasi'
          className='form-control my-2 hy-5'
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
        />
        <input
          type="text"
          placeholder='Mashina rusumi'
          className='form-control my-2'
          onChange={(e) => setForm({ ...form, carType: e.target.value })}
        />
        <input
          type="file"
          placeholder='Mashina rasmi'
          className='form-control my-2'
          onChange={(e) => setSelectFile1(e.target.files[0])}
        />
        <input
          type="file"
          placeholder='Haydovchilik guvohnomasi'
          className='form-control my-2'
          onChange={(e) => setSelectFile2(e.target.files[0])}
        />
        <input
          type="file"
          placeholder='Mashina texpasporti'
          className='form-control my-2'
          onChange={(e) => setSelectFile3(e.target.files[0])}
        />
        <button className='btn btn-success' onClick={save}>Saqlash</button>
      </div>
    </div>
  );
}

export default Register;
