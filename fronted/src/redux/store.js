import { configureStore } from '@reduxjs/toolkit';
import routesReducer from './slices/routeDriver'
import driverReducer from './slices/DriverSlice'
import toCityReducer from './slices/toCity'
import fromCityReducer from './slices/fromCity'
const store = configureStore({
    reducer: {
       
        routes: routesReducer,
        driver: driverReducer,
        toCity: toCityReducer,
        fromCity: fromCityReducer,

    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {

                ignoredActions: ['driver/setSelectedFile','driver/setSelectedFile1','driver/setSelectedFile2'],
                ignoredPaths: ['driver.selectedFile','driver.selectedFile1','driver.selectedFile2',]


            },
        }),
});

export default store;