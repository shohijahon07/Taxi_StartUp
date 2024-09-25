import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from '../../apicall/apicall1';

export const saveUserData = createAsyncThunk(
  'register/saveUserData',
  async ({ form, selectFiles, chatId }) => {
    let newImg = form.carImg;
    let newImg1 = form.driverImg;
    let newImg2 = form.cardDocument;

    const uploadFile = async (file) => {
      const formData = new FormData();
      formData.append('file', file);
      const response = await apicall1('/fileController/photo', 'POST', formData);
      return response.data;
    };

    if (selectFiles[0]) {
      newImg = await uploadFile(selectFiles[0]);
    }
    if (selectFiles[1]) {
      newImg1 = await uploadFile(selectFiles[1]);
    }
    if (selectFiles[2]) {
      newImg2 = await uploadFile(selectFiles[2]);
    }

    const updatedForm = {
      ...form,
      carImg: newImg,
      driverImg: newImg1,
      cardDocument: newImg2,
      chatId: chatId,
    };

    const response = await apicall1("/user/save", "POST", updatedForm);
    return response;
  }
);

const registerSlice = createSlice({
  name: 'register',
  initialState: {
    form: {
      count: "",
      carType: "",
      carImg: "",
      driverImg: "",
      cardDocument: "",
      about: ""
    },
    selectFiles: [null, null, null],
    chatId: null,
    language: "",
    isModalVisible: false,
    modalMessage: '',
    modalType: ''
  },
  reducers: {
    setChatId(state, action) {
      state.chatId = action.payload;
    },
    setLanguage(state, action) {
      state.language = action.payload;
    },
    updateForm(state, action) {
      state.form = { ...state.form, ...action.payload };
    },
    updateFile(state, action) {
      const { index, file } = action.payload;
      state.selectFiles[index] = file;
    },
    setModal(state, action) {
      const { isVisible, message, type } = action.payload;
      state.isModalVisible = isVisible;
      state.modalMessage = message;
      state.modalType = type;
    },
    resetForm(state) {
      state.form = {
        count: "",
        carType: "",
        carImg: "",
        driverImg: "",
        cardDocument: "",
        about: ""
      };
      state.selectFiles = [null, null, null];
      state.isModalVisible = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(saveUserData.fulfilled, (state) => {
        state.modalMessage = state.language === 'uz' ? 'Ma’lumotlaringiz tekshirilmoqda. Iltimos kuting, biz o’zimiz Telegram bot orqali xabar yuboramiz.' : 'Ваши данные проверяются. Пожалуйста, подождите, мы сами отправим вам сообщение через Telegram-бот.';
        state.modalType = 'success';
        state.isModalVisible = true;
      })
      .addCase(saveUserData.rejected, (state, action) => {
        state.modalMessage = state.language === 'uz' ? "Noma'lum xatolik yuz berdi" : "Произошла неизвестная ошибка";
        state.modalType = 'error';
        state.isModalVisible = true;
      });
  },
});

export const { setChatId, setLanguage, updateForm, updateFile, setModal, resetForm } = registerSlice.actions;
export default registerSlice.reducer;
