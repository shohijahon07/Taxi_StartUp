import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const MAX_FILE_SIZE = 19 * 1024 * 1024; // 19 MB

// Faylni yuklash uchun funksiya
const uploadFile = async (file, url, onProgressUpdate) => {
  if (file.size > MAX_FILE_SIZE) {
    throw new Error(`Fayl hajmi 19 MB dan oshmasligi kerak. Joriy hajm: ${(file.size / (1024 * 1024)).toFixed(2)} MB`);
  }

  const formData = new FormData();
  formData.append('file', file);

  const response = await axios.post(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 180000, // 3 daqiqa
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      onProgressUpdate(percentCompleted);
    }
  });

  return response.data;
};

// Formani saqlash uchun thunk
export const saveForm = createAsyncThunk(
  'register/saveForm',
  async (_, { getState, dispatch, rejectWithValue }) => {
    const state = getState().register;
    const { form, selectFile1, selectFile2, selectFile3, chatId, language } = state;

    const validateForm = () => {
      if (!form.carType || !form.about || !form.count || !selectFile1 || !selectFile2 || !selectFile3) {
        return false;
      }
      return true;
    };

    if (!validateForm()) {
      return rejectWithValue(language === 'uz' 
        ? "Barcha ma'lumotlarni to'ldiring. Har bir maydonni to'ldirishingiz kerak."
        : 'Пожалуйста, заполните все поля. Вы должны заполнить каждое поле.');
    }

    try {
      let newImg = form.carImg;
      let newImg1 = form.driverImg;
      let newImg2 = form.cardDocument;

      if (selectFile1) {
        newImg = await uploadFile(selectFile1, '/fileController/photo', 
          (progress) => dispatch(setUploadProgress({ file: 'file1', progress })));
      }

      if (selectFile2) {
        newImg1 = await uploadFile(selectFile2, '/fileController/photo', 
          (progress) => dispatch(setUploadProgress({ file: 'file2', progress })));
      }

      if (selectFile3) {
        newImg2 = await uploadFile(selectFile3, '/fileController/photo', 
          (progress) => dispatch(setUploadProgress({ file: 'file3', progress })));
      }

      const updatedForm = {
        ...form,
        carImg: newImg,
        driverImg: newImg1,
        cardDocument: newImg2,
        chatId: chatId
      };
      
      const response = await axios.post('/user/save', updatedForm, { timeout: 60000 });
      return response.data;
    } catch (error) {
      console.error('Formani saqlashda xato yuz berdi:', error);
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

const registerSlice = createSlice({
  name: 'register',
  initialState: {
    form: {
      carType: '',
      about: '',
      count: ''
    },
    selectFile1: null,
    selectFile2: null,
    selectFile3: null,
    carImgPreview: null,
    driverImgPreview: null,
    cardDocPreview: null,
    showModal: false,
    modalMessage: '',
    modalClass: '',
    chatId: '',
    language: 'uz',
    uploadProgress: {
      file1: 0,
      file2: 0,
      file3: 0
    }
  },
  reducers: {
    setCarImgPreview: (state, action) => {
      state.carImgPreview = action.payload;
    },
    setDriverImgPreview: (state, action) => {
      state.driverImgPreview = action.payload;
    },
    setCardDocPreview: (state, action) => {
      state.cardDocPreview = action.payload;
    },
    setSelectFile1: (state, action) => {
      state.selectFile1 = action.payload;
    },
    setSelectFile2: (state, action) => {
      state.selectFile2 = action.payload;
    },
    setSelectFile3: (state, action) => {
      state.selectFile3 = action.payload;
    },
    setShowModal: (state, action) => {
      state.showModal = action.payload;
    },
    setModalMessage: (state, action) => {
      state.modalMessage = action.payload;
    },
    setModalClass: (state, action) => {
      state.modalClass = action.payload;
    },
    setChatId: (state, action) => {
      state.chatId = action.payload;
    },
    setLanguage: (state, action) => {
      state.language = action.payload;
    },
    setForm: (state, action) => {
      state.form = action.payload;
    },
    setUploadProgress: (state, action) => {
      state.uploadProgress[action.payload.file] = action.payload.progress;
    }
  },
  extraReducers: {
    [saveForm.pending]: (state) => {
      state.showModal = true;
      state.modalMessage = state.language === 'uz' ? 'Yuklanyapti...' : 'Загрузка...';
      state.modalClass = 'info';
    },
    [saveForm.fulfilled]: (state, action) => {
      state.showModal = true;
      state.modalMessage = state.language === 'uz' ? 'Muvaffaqiyatli saqlandi' : 'Успешно сохранено';
      state.modalClass = 'success';
    },
    [saveForm.rejected]: (state, action) => {
      state.showModal = true;
      state.modalMessage = action.payload || (state.language === 'uz' ? 'Xatolik yuz berdi' : 'Произошла ошибка');
      state.modalClass = 'error';
    }
  }
});

export const { 
  setCarImgPreview, 
  setDriverImgPreview, 
  setCardDocPreview, 
  setSelectFile1, 
  setSelectFile2, 
  setSelectFile3, 
  setShowModal, 
  setModalMessage, 
  setModalClass, 
  setChatId, 
  setLanguage, 
  setForm,
  setUploadProgress
} = registerSlice.actions;

export default registerSlice.reducer;