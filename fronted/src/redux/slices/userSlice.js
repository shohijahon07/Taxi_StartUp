import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";

export const addPessenger = createAsyncThunk('DriverSlice/addPessenger', async ({ pessen, chatId }) => {
  const response = await apicall1(`/user/pessenger`, "POST", { ...pessen, driverChatId: chatId });
  return response.data;
});

const PessengerSlice = createSlice({
  name: 'pessenger',
  initialState: {
    pessen: { name: "", phoneNumber: "", driverChatId: "" },
    status: 'idle',
    error: null,
  },
  reducers: {
    setPesenger(state, action) {
      // Merge the new pessen data into the existing state
      state.pessen = { ...state.pessen, ...action.payload };
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(addPessenger.fulfilled, (state, action) => {
        // Update the state with the response data
        state.pessen = { ...state.pessen, ...action.payload };
        state.status = 'succeeded';
      })
      .addCase(addPessenger.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(addPessenger.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });
  },
});

export const { setPesenger } = PessengerSlice.actions;

export default PessengerSlice.reducer;
