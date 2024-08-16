import { Route, Routes } from "react-router-dom"
import Login from "./components/auth/Login"
import Landing from "./components/user/Landing"
import DriverLanding from "./components/driver/DriverLanding"
import DriverHeader from "./components/driver/headerD/DriverHeader"
import AdminHeder from "./components/admin/adminHeader/AdminHeder"
import AboutDriversOne from "./components/admin/AboutDriversOne"


function App() {


  return (
    <Routes>
                  <Route path='/login' element={<Login/>} />
                  <Route path='/' element={<Landing/>} />
                  <Route path='/landing' element={<DriverHeader/>} />
                  <Route path='/bosh_sahifa' element={<AdminHeder/>} />
                  <Route path='/bosh_sahifa/bir_haydovchi/:userName' element={<AboutDriversOne/>} />


    </Routes>
  )
}

export default App
