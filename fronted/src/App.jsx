import { Route, Routes } from "react-router-dom"
import Login from "./components/auth/Login"
import Landing from "./components/user/Landing"


function App() {


  return (
    <Routes>
                  <Route path='/login' element={<Login/>} />
                  <Route path='/' element={<Landing/>} />


    </Routes>
  )
}

export default App
