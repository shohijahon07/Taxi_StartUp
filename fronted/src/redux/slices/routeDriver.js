import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";
import apicall from "../../apicall/apicall";

export const fetchRoutes = createAsyncThunk('RouteDriverSlice/fetchRoutes', async () => {
  const response = await apicall1(`/driver`, "GET");
  return response.data;
});

export const fetchRoutesByDate = createAsyncThunk('RouteDriverSlice/fetchRoutesByDate', async (driverRout) => {
  console.log(driverRout);
  const response = await apicall1(`/driver/byDate`, "PUT",driverRout);
  console.log(response.data);
  return response.data;
});

export const fetchRoutesByDriver = createAsyncThunk('RouteDriverSlice/fetchRoutesByDriver', async (userName) => {
  const response = await apicall1(`/driver/bydriver?id=${userName}`, "GET");
  console.log(response.data);
  return response.data;
});

export const addRoute = createAsyncThunk('RouteDriverSlice/addRoute', async ({ driverRout, userName }) => {
  const response = await apicall1(`/driver`, "POST", { ...driverRout, userId: userName });
  return response.data;  // Kerakli ma'lumotni qaytaramiz
});

export const editRoute = createAsyncThunk('RouteDriverSlice/editRoute', async ({ EditButtonId, driverRout }) => {
  const response = await apicall(`/driver?id=${EditButtonId}`, "PUT", driverRout);
  return { id: EditButtonId, ...driverRout };  // ID va yangi qiymatlarni qaytaramiz
});

export const deleteRoutes = createAsyncThunk('RouteDriverSlice/deleteRoutes', async ({ id }) => {
  await apicall(`/driver?id=${id}`, "DELETE", null);
  return id;
});

const RouteDriverSlice = createSlice({
  name: 'routes',
  initialState: {
    driverRoutes: [],
    allRoutes: [],
    driverRoutesByDate: [],
    status: 'idle',
    error: null,
    day:"",
    img: '',
    EditButtonId: null,
    driverRout: { fromCity: '', toCity: '', countSide: "", price: "", day: "", hour: "", userId: "" },
    selectedFile: null,
  },
  reducers: {
    setEditButtonId(state, action) {
      state.EditButtonId = action.payload;
    },
    setDay(state, action) {
      state.day = action.payload;
    },
    setKoranCourse(state, action) {
      state.driverRout = action.payload;
    },
    setSelectedFile(state, action) {
      state.selectedFile = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRoutesByDriver.pending, (state) => {
        state.status = 'loading';
      })
   
      .addCase(fetchRoutes.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchRoutesByDriver.fulfilled, (state, action) => {
        console.log(action.payload);
        state.status = 'succeeded';
        state.driverRoutes = action.payload;  // Null yoki undefined qiymatlarga e'tibor bering
      })
      .addCase(fetchRoutesByDate.fulfilled, (state, action) => {
        state.allRoutes = action.payload; 
      })
      .addCase(fetchRoutes.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.allRoutes = action.payload; 
      })
      .addCase(fetchRoutesByDate.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(fetchRoutes.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(addRoute.fulfilled, (state, action) => {
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
      });
  },
});

export const { setEditButtonId, setKoranCourse, setSelectedFile,setDay } = RouteDriverSlice.actions;

export default RouteDriverSlice.reducer;
