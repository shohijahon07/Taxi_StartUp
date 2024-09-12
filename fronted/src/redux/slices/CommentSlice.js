import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";

export const fetchComments = createAsyncThunk('CommentSlice/fetchComments', async ({language,userName}) => {
  console.log(userName);
  const response = await apicall1(`/comment?language=${language}&id=${userName}`, "GET");
  console.log(response.data);
  return response.data;
});

const CommentSlice = createSlice({
  name: 'comment',
  initialState: {
    comments: [],
    id:"",
    minDate:'',
    maxDate:'',
    chatId:"",
    active:false,
    isModalOpen1:false,
    isModalOpen:false,
    openModal:false,
    seatCount:null,
    openModal1:false,
    status: 'idle',
    error: null,
  },
  reducers: {
    setId(state, action) {
      state.id = action.payload;
    },
    setChatId(state, action) {
      state.chatId = action.payload;
    },
    setMinDate(state, action) {
      state.minDate = action.payload;
    },
    setMaxDate(state, action) {
      state.maxDate = action.payload;
    },
    setActive(state, action) {
      state.active = action.payload;
    },
    setIsModalOpen1(state, action) {
      state.isModalOpen1 = action.payload;
    },
    setIsModalOpen(state, action) {
      state.isModalOpen = action.payload;
    },
    setOpenModal(state, action) {
      state.openModal = action.payload;
    },
    setSeatCount(state, action) {
      state.seatCount = action.payload;
    },
    setOpenModal1(state, action) {
      state.openModal1 = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchComments.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchComments.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.comments = action.payload;
      })
      .addCase(fetchComments.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });
  },
});
export const { setId,setChatId,setMinDate,setMaxDate,setActive,setIsModalOpen1,setIsModalOpen,setOpenModal,setSeatCount,setOpenModal1} = CommentSlice.actions;

export default CommentSlice.reducer;
