import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apicall1 from "../../apicall/apicall1";
import apicall from "../../apicall/apicall";

export const fetchDrivers = createAsyncThunk('DriverSlice/fetchRoutes', async (isDriver) => {
  const response = await apicall1(`/user/drivers?isDriver=${isDriver}`, "GET");
  return response.data;
});
export const countDriversAll = createAsyncThunk('DriverSlice/countDriversAll', async () => {
  const response = await apicall1(`/user/countD`, "GET");
 
  return response.data;
});
export const countUsersAll = createAsyncThunk('DriverSlice/countUsersAll', async () => {
  const response = await apicall1(`/user/countU`, "GET");
  return response.data;
});

export const fetchDriverOne = createAsyncThunk('DriverSlice/fetchDriverOne', async (userName) => {
  const response = await apicall1(`/user?id=${userName}`, "GET");
  console.log(response.data);
  return response.data;
});

export const addDriver = createAsyncThunk('DriverSlice/addDriver', async ({ driverRout, userName }) => {
  const response = await apicall1(`/user`, "POST", { ...driverRout, userId: userName });
  return response.data;  
});
export const addDriverAbout = createAsyncThunk('DriverSlice/addDriverAbout', async ({ about, userName }) => {
  try {
      console.log(userName);
      console.log(about);
    const response = await apicall(`/user?id=${userName}&text=${about}`, "POST",  null );
    return response; 
  } catch (error) {
    console.error("Error in addDriverAbout:", error);
    throw error;
  }
});

export const editDriver = createAsyncThunk('DriverSlice/editDriver', async ({ EditButtonId, driverObject, selectedFile, selectedFile1, selectedFile2, img, img1, img2 }) => {
    let newImg = img;
    let newImg1 = img1;   
    let newImg2 = img2;

    if (selectedFile) {
      const formData = new FormData();
      formData.append('file', selectedFile);
      const res = await apicall1('/fileController/photo', "POST", formData);
      newImg = res.data;
    }
    if (selectedFile1) {
      const formData = new FormData();
      formData.append('file', selectedFile1);  // Bu yerda selectedFile1 ishlatilmoqda
      const res = await apicall1('/fileController/photo', "POST", formData);
      newImg1 = res.data;
    }
    if (selectedFile2) {
      const formData = new FormData();
      formData.append('file', selectedFile2);  // Bu yerda selectedFile2 ishlatilmoqda
      const res = await apicall1('/fileController/photo', "POST", formData);
      newImg2 = res.data;
    }
 
    const updatedCourse = { ...driverObject, carImg: newImg, driverImg: newImg1, cardDocument: newImg2 };
    console.log(updatedCourse);
    const response = await apicall(`/user?id=${EditButtonId}`, "PUT", updatedCourse);
    return { id: EditButtonId, ...updatedCourse };
  });
export const editDriverIsDriving = createAsyncThunk('DriverSlice/editDriverIsDriving', async ({ id, driverIsdriving,randomNum}) => {
  console.log(driverIsdriving);
    const response = await apicall(`/user/isDrive?id=${id}`, "PUT", {...driverIsdriving,password:randomNum});
    return {  id, ...driverIsdriving };
  });
  
export const deleteDriver = createAsyncThunk('DriverSlice/deleteDriver', async ({ id }) => {
  console.log(id);
  await apicall(`/user?id=${id}`, "DELETE", null);
  return id;
});

const DriverSlice = createSlice({
  name: 'driver',
  initialState: {
    drivers: [],
    driverOne: [],
    status: 'idle',
    countDriver:"",
    countUser:"",
    error: null,
    about:"",
    EditButtonId: null,
    driverObject: { fullName: '', phoneNumber: '', carType: "", carImg: "", driverImg: "", cardDocument: "", about: "" },
    driverIsdriving: { isDriver:true,password:""},
    selectedFile: null,
    selectedFile1: null,
    selectedFile2: null,
    img: '',
    img1: '',
    img2: '',

  },
  reducers: {
    setEditButtonId(state, action) {
        state.EditButtonId = action.payload;
      },
    setAbout(state, action) {
        state.about = action.payload;
      },
      setDriver(state, action) {
        state.driverObject = action.payload;
      },
      setDriverIsDriver(state, action) {
        state.driverIsdriving = action.payload;
      },
      setSelectedFile(state, action) {
        state.selectedFile = action.payload;
      },
      setSelectedFile1(state, action) {
        state.selectedFile1 = action.payload;
      },
      setSelectedFile2(state, action) {
        state.selectedFile2 = action.payload;
      },
      setImg(state, action) {
        state.img = action.payload;
      },
      setImg1(state, action) {
        state.img1 = action.payload;
      },
      setImg2(state, action) {
        state.img2 = action.payload;
      },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDriverOne.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchDrivers.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchDriverOne.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.driverOne = action.payload;  // Null yoki undefined qiymatlarga e'tibor bering
      })
      .addCase(fetchDrivers.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.drivers = action.payload;  // Null yoki undefined qiymatlarga e'tibor bering
      })
      .addCase(countDriversAll.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.countDriver = action.payload;  // Null yoki undefined qiymatlarga e'tibor bering
      })
      .addCase(countUsersAll.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.countUser = action.payload;  // Null yoki undefined qiymatlarga e'tibor bering
      })
      .addCase(fetchDriverOne.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(fetchDrivers.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      })
      .addCase(addDriver.fulfilled, (state, action) => {
        state.drivers.push(action.payload);  // Yangi yo'nalishni ro'yxatga qo'shamiz
      })
      // .addCase(addDriverAbout.fulfilled, (state, action) => {
      //   state.drivers.push(action.payload);  // Yangi yo'nalishni ro'yxatga qo'shamiz
      // })
      .addCase(editDriver.fulfilled, (state, action) => {
        const index = state.drivers.findIndex(driver => driver.id === action.payload.id);
        if (index !== -1) {
          state.drivers[index] = action.payload;  // Tahrirlangan yo'nalishni yangilaymiz
        }
      })
      .addCase(editDriverIsDriving.fulfilled, (state, action) => {
        const index = state.drivers.findIndex(driver => driver.id === action.payload.id);
        if (index !== -1) {
          state.drivers[index] = action.payload;  // Tahrirlangan yo'nalishni yangilaymiz
        }
      })
      .addCase(addDriverAbout.fulfilled, (state, action) => {
        const index = state.drivers.findIndex(driver => driver.id === action.payload.id);
        if (index !== -1) {
          state.drivers[index] = action.payload;  // Tahrirlangan yo'nalishni yangilaymiz
        }
      })
      .addCase(deleteDriver.fulfilled, (state, action) => {
        state.drivers = state.drivers.filter(driver => driver.id !== action.payload);  // Yo'nalishni ro'yxatdan o'chiramiz
      });
  },
});

export const { setEditButtonId, setDriver,setDriverIsDriver, setSelectedFile,setImg,setImg1,setImg2,setSelectedFile1,setSelectedFile2 ,setAbout} = DriverSlice.actions;

export default DriverSlice.reducer;
