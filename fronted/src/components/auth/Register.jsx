import React, { useEffect, useState } from 'react';
import "./Register.css";
import apicall1 from "../../apicall/apicall1";
import PhoneInput from "react-phone-input-2";
import fileicon from "../../pictures/fileicon.jpg";
import ReactModal from 'react-modal'; // Import React Modal

ReactModal.setAppElement('#root'); // Bind modal to your appElement (important for accessibility)

function Register() {
  const [chatId, setChatId] = useState(null);
  const [form, setForm] = useState({
    fullName: "",
    count: "",
    phoneNumber: "",
    carType: "",
    carImg: "",
    driverImg: "",
    cardDocument: "",
    about: ""
  });
  const [selectFile1, setSelectFile1] = useState(null);
  const [selectFile2, setSelectFile2] = useState(null);
  const [selectFile3, setSelectFile3] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalClass, setModalClass] = useState('');
  const [carImgPreview, setCarImgPreview] = useState(null);
  const [driverImgPreview, setDriverImgPreview] = useState(null);
  const [cardDocPreview, setCardDocPreview] = useState(null);

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const chatIdParam = urlParams.get('chatId');
    if (chatIdParam) {
      setChatId(chatIdParam);
    }
  }, []);

  const validateForm = () => {
    if (!form.fullName || !form.phoneNumber || !form.carType || !form.about || !form.count || !selectFile1 || !selectFile2 || !selectFile3) {
      return false;
    }
    return true;
  };

  const save = async () => {
    if (!validateForm()) {
      setModalMessage('Barcha ma’lumotlarni to’ldiring. Har bir maydonni to’ldirishingiz kerak.');
      setModalClass('modal_rg error');
      setShowModal(true);
      return;
    }

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
      await apicall1("/user/save", "POST", updatedForm)
    .then((response) => {
      setForm({ fullName: "", count: "", phoneNumber: "", about: "", carType: "", carImg: "", driverImg: "", cardDocument: "" });
      setSelectFile1(null);
      setSelectFile2(null);
      setSelectFile3(null);
      setCarImgPreview(null);
      setDriverImgPreview(null);
      setCardDocPreview(null);
      setModalMessage('Ma’lumotlaringiz tekshirilmoqda iltimos kuting biz o’zimiz telegram bot orqali xabar yuboramiz');
      setModalClass('modal_rg success');
      setShowModal(true);
        console.log(response.data);
    })
    .catch((err) => {
        if (err.response && err.response.data) {
            alert(err.response.data); // Show the error message from the server
        } else {
            alert("An unknown error occurred"); // Fallback error message
        }
    });
      
      
    } catch (error) {
      console.error('Error during saving:', error);
      setModalMessage('Xatolik yuz berdi: ' + error.message);
      setModalClass('modal_rg error');
      setShowModal(true);
    }
  };

  const handleFileChange = (e, setFile, setPreview) => {
    const file = e.target.files[0];
    setFile(file);
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className='register_con'>
      <h3 className='text_rg'>Ro’yxatdan o’tish uchun quyidagi ma’lumotlarni kiriting</h3>
      <div className="body-con">
        <PhoneInput
          country={"uz"}
          onChange={(e) => setForm({ ...form, phoneNumber: '+' + e })}
          value={form.phoneNumber}
          placeholder={"Iltimos telefon raqamingizni kiriting"}
        />
        <input
          type="text"
          placeholder='Ism familyasi'
          className='form-control my-2 hy-5'
          value={form.fullName}
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
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
        <select className='form-control' value={form.count} style={{ marginBottom: "10px" }} onChange={(e) => setForm({ ...form, count: e.target.value })}>
          <option className='form-control' value={""}>Tanlang</option>
          <option className='form-control' value={true}>Yengil mashina</option>
          <option className='form-control' value={false}>Mikro aftobus</option>
        </select>
        <div className="custom-file-upload">
          <input
            type="file"
            id="file1" accept="image/*"
            onChange={(e) => handleFileChange(e, setSelectFile1, setCarImgPreview)}
          />
          <label htmlFor="file1">
            <img src={fileicon} alt="Upload Icon" className="upload-icon" />
            Mashina rasmini yuklang
          {carImgPreview && <img src={carImgPreview} alt="Car Preview" className="file_rg" />}

          </label>
        </div>

        <div className="custom-file-upload">
          <input
            type="file"
            id="file2" accept="image/*"
            onChange={(e) => handleFileChange(e, setSelectFile2, setDriverImgPreview)}
          />
          <label htmlFor="file2">
            <img src={fileicon} alt="Upload Icon" className="upload-icon" />
            Haydovchilik guvohnoma
          {driverImgPreview && <img src={driverImgPreview} alt="Driver Preview" className="file_rg" />}

          </label>
        </div>

        <div className="custom-file-upload">
          <input
            type="file"
            id="file3" accept="image/*"
            onChange={(e) => handleFileChange(e, setSelectFile3, setCardDocPreview)}
          />
          <label htmlFor="file3">
            <img src={fileicon} alt="Upload Icon" className="upload-icon" />
            Texpasportni yuklang
          {cardDocPreview && <img  src={cardDocPreview} alt="Card Document Preview" className="file_rg" />}

          </label>
        </div>

        <button className='btn_rg' onClick={save}>Yuborish</button>
      </div>

      {/* Modal with custom "X" close button */}
      <ReactModal
        isOpen={showModal}
        onRequestClose={() => setShowModal(false)}
        contentLabel="Muvaffaqiyatli"
        className="modal-content"
        overlayClassName="modal-overlay"
      >
        <div>
          <button className="modal-close-btn" onClick={() => setShowModal(false)}>&times;</button>
          <p className={modalClass}>{modalMessage}</p>
        </div>
      </ReactModal>
    </div>
  );
}

export default Register;
