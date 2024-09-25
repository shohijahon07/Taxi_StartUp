import React, { useEffect, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { saveUserData, setChatId, setLanguage, updateForm, updateFile, resetForm, setModal } from '../../redux/slices/Register';
import { Modal, Button } from 'antd';
import fileicon from "../../pictures/fileicon.svg";
import "./Register.css"
function Register() {
  const dispatch = useDispatch();
  const { form, selectFiles, chatId, language, isModalVisible, modalMessage, modalType } = useSelector(state => state.register);

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const chatIdParam = urlParams.get('chatId');
    const languageParam = urlParams.get('language'); 

    if (chatIdParam) {
      dispatch(setChatId(chatIdParam));
    }

    if (languageParam) {
      dispatch(setLanguage(languageParam));
    }
  }, [dispatch]);

  const validateForm = useCallback(() => {
    return form.carType && form.about && form.count && selectFiles.every(file => file);
  }, [form, selectFiles]);

  const showModal = useCallback((message, type) => {
    dispatch(setModal({ isVisible: true, message, type }));
  }, [dispatch]);

  const handleOk = useCallback(() => {
    dispatch(setModal({ isVisible: false, message: '', type: '' }));
  }, [dispatch]);

  const save = useCallback(async () => {
    if (!validateForm()) {
      showModal(
        language === 'uz' ? 'Barcha ma’lumotlarni to’ldiring. Har bir maydonni to’ldirishingiz kerak.' : 'Пожалуйста, заполните все поля. Вы должны заполнить каждое поле.',
        'error'
      );
      return;
    }

    showModal(
      language === 'uz' ? 'Ma’lumotlaringiz tekshirilmoqda. Iltimos kuting, biz o’zimiz Telegram bot orqali xabar yuboramiz.' : 'Ваша информация проверяется. Пожалуйста, подождите, мы отправим сообщение через Telegram-бот.',
      'success'
    );

    try {
     
      await dispatch(saveUserData({ form, selectFiles, chatId }));
      dispatch(resetForm());
    } catch (error) {
      console.error('Error during saving:', error);
      showModal(
        language === 'uz' ? 'Xatolik yuz berdi: ' + error.message : 'Произошла ошибка: ' + error.message,
        'error'
      );
    }
  }, [validateForm, showModal, dispatch, form, selectFiles, chatId, language]);

  const handleFileChange = useCallback((e, index) => {
    const file = e.target.files[0];

    dispatch(updateFile({ index, file }));
  }, [dispatch]);

  const fileLabels = [
    {
      uz: "Mashina rasmini yuklash",
      ru: "Загрузить изображение машины"
    },
    {
      uz: "Haydovchini guvohnomasi",
      ru: "Загрузить водительское удостоверение"
    },
    {
      uz: "Texpasportni yuklash",
      ru: "Загрузить техпаспорт"
    }
  ];

  return (
    <div className='register_con'>
      <h3 className='text_rg'>
        {language === "uz" ? "Ro’yxatdan o’tish uchun quyidagi ma’lumotlarni kiriting" : "Введите следующую информацию для регистрации"}
      </h3>
      <div className="body-con">
        <input
          type="text"
          placeholder={language === "uz" ? "Mashina rusumi" : "Марка машины"}
          className='form-control my-2'
          value={form.carType}
          onChange={(e) => dispatch(updateForm({ carType: e.target.value }))}
        />
        <input
          type="text"
          placeholder={language === "uz" ? "Ozingiz haqingizda yozing" : "Напишите о себе"}
          className='form-control my-2'
          value={form.about}
          onChange={(e) => dispatch(updateForm({ about: e.target.value }))}
        />
        <select className='form-control' value={form.count} style={{ marginBottom: "10px" }} onChange={(e) => dispatch(updateForm({ count: e.target.value }))}>
          <option className='form-control' value={""}>{language === "uz" ? "Tanlang" : "Выберите"}</option>
          <option className='form-control' value={true}>{language === "uz" ? "Yengil mashina" : "Легковой автомобиль"}</option>
          <option className='form-control' value={false}>{language === "uz" ? "Mikro avtobus" : "Микроавтобус"}</option>
        </select>
        
        {fileLabels.map((label, index) => (
          <div className="custom-file-upload" key={index}>
            <input
              type="file"
              id={`file${index + 1}`}
              accept="image/*"
              onChange={(e) => handleFileChange(e, index)}
            />
            <label htmlFor={`file${index + 1}`}>
              <img src={fileicon} alt="Upload Icon" className="upload-icon" />
              <span>{selectFiles[index] ? selectFiles[index].name : (language === "uz" ? label.uz : label.ru)}</span>
            </label>
          </div>
        ))}
          <button type="primary" className='btn_rg' onClick={save}>
           {language === "uz" ? "Yuborish" : "Отправка"}
          </button>
      </div>

      {isModalVisible && (
  <Modal
    title={language === "uz" ? "Malumot" : "Информация"}
    visible={isModalVisible}
    onOk={handleOk}
    onCancel={handleOk}
    footer={[
      <Button key="ok" onClick={handleOk}>
        {language === "uz" ? "OK" : "OK"}
      </Button>,
    ]}
  >
    <h5
      style={{
        color: modalMessage ===
        (language === 'uz' ? 'Ma’lumotlaringiz tekshirilmoqda. Iltimos kuting, biz o’zimiz Telegram bot orqali xabar yuboramiz.' :
        'Ваша информация проверяется. Пожалуйста, подождите, мы отправим сообщение через Telegram-бот.')
        ? 'green' : 'red'
      }}
    >
      {modalMessage}
    </h5>
  </Modal>
)}

    </div>
  );
}

export default Register;
