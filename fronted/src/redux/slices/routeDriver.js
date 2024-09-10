import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";
import apicall from "../../apicall/apicall";
import { toast } from 'react-toastify';

export const fetchRoutes = createAsyncThunk('RouteDriverSlice/fetchRoutes', async () => {
  const response = await apicall1(`/driver`, "GET");
  return response.data;
});

export const fetchRoutesByDate = createAsyncThunk('RouteDriverSlice/fetchRoutesByDate', async (driverRout, { getState }) => {
  const response = await apicall1(`/driver/byDate`, "PUT", driverRout);
  
  // const { routes: { allRoutes } } = getState(); 
  
  if (!response.data || response.data.length === 0) {
    return { data: response.data, result: true };  
  } else {
    toast.success("Yo'nalishlar muvaffaqiyatli topildi!");
    return { data: response.data, result: false };  // Yo'nalishlar mavjud - true
  }
  
  // return response.data;
});


export const fetchRoutesByDriver = createAsyncThunk('RouteDriverSlice/fetchRoutesByDriver', async (userName) => {
  const response = await apicall1(`/driver/bydriver?id=${userName}`, "GET");

  return response.data;
});
export const fetchRoutesByDay = createAsyncThunk('RouteDriverSlice/fetchRoutesByDay', async (day) => {
  const response = await apicall1(`/driver/byDay?day=${day}`, "GET");
  
  if (!response.data || response.data.length === 0) {
    return { data: response.data, result: true };  
  } else {
    toast.success("Yo'nalishlar muvaffaqiyatli topildi!");
    return { data: response.data, result: false };  // Yo'nalishlar mavjud - true
  }
});



export const addRoute = createAsyncThunk('RouteDriverSlice/addRoute', async ({ driverRout, userName }) => {
  const response = await apicall1(`/driver`, "POST", { ...driverRout, userId: userName });
  return response.data;  
});

export const editRoute = createAsyncThunk('RouteDriverSlice/editRoute', async ({ EditButtonId, driverRout }) => {
  const response = await apicall(`/driver?id=${EditButtonId}`, "PUT", driverRout);
  return { id: EditButtonId, ...driverRout };  
});

export const deleteRoutes = createAsyncThunk('RouteDriverSlice/deleteRoutes', async ({ id }) => {
  await apicall(`/driver?id=${id}`, "DELETE", null);
  return id;
});
export const deleteRoutesByDay = createAsyncThunk('RouteDriverSlice/deleteRoutesByDay', async ( item1 ) => {
 
  await apicall(`/driver/bydel?day=${item1.day}&hour=${item1.hour}`, "DELETE", null);
  return item1.id;
});

const RouteDriverSlice = createSlice({
  name: 'routes',
  initialState: {
    driverRoutes: [],
    allRoutes: [],
    routesByDriver:[],
    driverRoutesByDate: [],
    status: 'idle',
    error: null,
    day:"",
    img: '',
    EditButtonId: null,
    driverRout: { fromCity: '', toCity: '', countSide: "", price: "", day: "", hour: "", userId: "" },
    selectedFile: null,
    item1:null,
    day12:null,
    isAvailable:false
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
    setClearItem1(state, action) {
      state.item1 = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRoutesByDriver.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchRoutesByDay.pending, (state) => {
        state.status = 'loading';
      })
   
      .addCase(fetchRoutes.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchRoutesByDriver.fulfilled, (state, action) => {
        
        state.status = 'succeeded';
        state.routesByDriver = action.payload;  

          action.payload.map((item)=>{
             state.item1=item
          })

       
      })
      .addCase(fetchRoutesByDay.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.allRoutes = action.payload.data; 
        state.isAvailable = action.payload.result; 
      })
      
      .addCase(fetchRoutesByDate.fulfilled, (state, action) => {
        state.allRoutes = action.payload.data; 
        state.isAvailable = action.payload.result; 
        for (let index = 0; index < action.payload.data.length; index++) {
            state.day12=action.payload.data[index].day
        }
      })
      .addCase(fetchRoutes.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.allRoutes = action.payload; 
      })
      .addCase(fetchRoutesByDate.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(fetchRoutesByDay.rejected, (state, action) => {
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
      })
      .addCase(deleteRoutesByDay.fulfilled, (state, action) => {
        state.driverRoutes = state.driverRoutes.filter(driverRoute => driverRoute.id !== action.payload);  
      });
  },
});

export const { setEditButtonId, setKoranCourse, setSelectedFile,setDay, setClearItem1} = RouteDriverSlice.actions;

export default RouteDriverSlice.reducer;
