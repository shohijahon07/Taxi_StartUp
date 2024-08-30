import { Route, Routes } from "react-router-dom"
import Login from "./components/auth/Login"
import Landing from "./components/user/Landing"
import DriverLanding from "./components/driver/DriverLanding"
import DriverHeader from "./components/driver/headerD/DriverHeader"
import AdminHeder from "./components/admin/adminHeader/AdminHeder"
import AboutDriversOne from "./components/admin/AboutDriversOne"
import RoutesUser from "./components/user/RoutesUser"
import UserDiverOne from "./components/user/UserDiverOne"
import { LanguageProvider } from "./components/language/LanguageContext"


function App() {


  return (
    <LanguageProvider>
      
    <Routes>
                  <Route path='/login' element={<Login/>} />
                  <Route path='/' element={<RoutesUser/>} />
                  <Route path='/header' element={<Landing/>} />
                  <Route path='/landing' element={<DriverHeader/>} />
                  <Route path='/bosh_sahifa' element={<AdminHeder/>} />
                  <Route path='/bir_haydovchi/:userName' element={<AboutDriversOne/>} />
                  <Route path="/id_haydovchi/:userName" element={<UserDiverOne/>} />

    </Routes>
    </LanguageProvider>
    
  )
}

export default App
