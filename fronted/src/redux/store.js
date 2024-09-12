import { configureStore } from '@reduxjs/toolkit';
import routesReducer from './slices/routeDriver'
import driverReducer from './slices/DriverSlice'
import toCityReducer from './slices/toCity'
import fromCityReducer from './slices/fromCity'
import commentReducer from './slices/CommentSlice'
import pessengerReducer from './slices/userSlice'
import connection from './slices/Connection'
const store = configureStore({
    reducer: {
       
        routes: routesReducer,
        driver: driverReducer,
        toCity: toCityReducer,
        fromCity: fromCityReducer,
        comment: commentReducer,
        pessenger: pessengerReducer,
        boglanish:connection

    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {

                ignoredActions: ['driver/setSelectedFile','driver/setSelectedFile1','driver/setSelectedFile2','toCity/setSelectedDate','boglanish/saveForm/fulfilled'],
                ignoredPaths: ['driver.selectedFile','driver.selectedFile1','driver.selectedFile2','toCity.selectedDate']


            },
        }),
});

export default store;