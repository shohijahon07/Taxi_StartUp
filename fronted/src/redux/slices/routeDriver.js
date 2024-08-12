import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";
import apicall from "../../apicall/apicall";

export const fetchRoutes = createAsyncThunk('RouteDriverSlice/fetchRoutes', async (language) => {
  const response = await apicall1(`/driver`, "GET");
  return response.data;
});



export const addRoute = createAsyncThunk('RouteDriverSlice/addRoute', async ({ driverRoute }) => {
  const response = await apicall(`/driver`, "POST", driverRoute);
  return response;
});

export const editRoute= createAsyncThunk('RouteDriverSlice/editRoute', async ({ driverRoute, EditButtonId }) => {

  await apicall(`/driver?id=${EditButtonId}`, "PUT", driverRoute);
  return ;
});

export const deleteRoutes = createAsyncThunk('RouteDriverSlice/deleteTeachers', async ({ id}) => {
  await apicall(`/driver?id=${id}`, "DELETE", null);
  return id;
});

const RouteDriverSlice = createSlice({
  name: 'routes',
  initialState: {
    driverRoutes: [],
    status: 'idle',
   
    error: null,
    img: '',
    EditButtonId: null,
    driverRoute: { fromCity: '', toCity: '', countSide: '',price:0 ,day:"",hour:"",userId:""},
    selectedFile: null,
  },
  reducers: {
   
    setEditButtonId(state, action) {
      state.EditButtonId = action.payload;
    },
    setKoranCourse(state, action) {
      state.driverRoute = action.payload;
    },
    setSelectedFile(state, action) {
      state.selectedFile = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRoutes.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchRoutes.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.driverRoutes = action.payload;
      })
      .addCase(fetchRoutes.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(addTeachers.fulfilled, (state, action) => {
        state.driverRoutes.push(action.payload);
      })
      .addCase(editRoute.fulfilled, (state, action) => {
        const index = state.driverRoutes.findIndex(driverRoute => driverRoute.id === action.payload.id);
        if (index !== -1) {
          state.driverRoutes[index] = action.payload;
        }
      })
      .addCase(deleteRoutes.fulfilled, (state, action) => {
        state.driverRoutes = state.driverRoutes.filter(driverRoute => driverRoute.id !== action.payload);
      })
    
  },
});

export const { setImg, setEditButtonId, setKoranCourse, setSelectedFile } = RouteDriverSlice.actions;

export default RouteDriverSlice.reducer;
