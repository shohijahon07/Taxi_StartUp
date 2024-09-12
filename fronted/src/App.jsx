import { Route, Routes, useLocation, useNavigate } from "react-router-dom"
import Login from "./components/auth/Login"
import Landing from "./components/user/Landing"
import AdminHeder from "./components/admin/adminHeader/AdminHeder"
import AboutDriversOne from "./components/admin/AboutDriversOne"
import RoutesUser from "./components/user/RoutesUser"
import { LanguageProvider } from "./components/language/LanguageContext"
import Register from  "./components/auth/Register"
import PathDriver from "./components/driver/headerD/PathDriver"
import MyselfDriver from "./components/driver/headerD/MyselfDriver"
import { useEffect } from "react"
import axios from "axios"
import { deleteRoutesByTime } from "./redux/slices/routeDriver"
import { message } from "antd"
import { useDispatch } from "react-redux"


function App() {
    const dispatch=useDispatch()
  const {pathname}=useLocation();
    const navigate=useNavigate();
    useEffect(() => {
        delByTime()
        checkRoles();
        const interval = setInterval(() => {
            checkRoles();
        }, 60000);
        return () => clearInterval(interval);
    }, [pathname, navigate]);

    const  permission=[
        {path:"/bir_haydovchi/:userName",roles:"ROLE_ADMIN"},
        {path:"/yo'nalish",roles:"ROLE_ADMIN"},
        {path:"/bosh_sahifa",roles:"ROLE_ADMIN"},
        {path:"/o'zim_haqqimda",roles:"ROLE_DRIVER"},
        {path:"/yo'nalish",roles:"ROLE_DRIVER"},
       
        ]
        function check(){
            for (let i = 0; i < permission.length; i++) {
                if (pathname.startsWith(permission[i].path)){
                    return permission[i].roles
                }
            }
        return null;}

        function checkRoles(){
            if (check()!=null){
                axios ({
                    url:"http://localhost:8080/api/auth/check",
                    method:"get",
                    headers:{authorization:localStorage.getItem("refresh_token")}
                }).then((res)=>{
                       const rolePerm=check();
                    for (let i = 0; i < res.data.length; i++) {
                        if (rolePerm.includes(res.data[i].name
                          )){
                            return;
                        }
                    }

                    for (let index = 0; index < res.data.length; index++) {
                      res.data[index];
                      if ( res.data[index].name==="ROLE_ADMIN") {
                       navigate("/bosh_sahifa")
                       }else if(res.data[index].name==="ROLE_DRIVER"){

                       navigate("/yo'nalish")
                       }

                    }
                    
                       
                }).catch((e)=>{
                    navigate("/")
                    localStorage.removeItem("access_token")
                    localStorage.removeItem("refresh_token")
                })
            }
          }

          function delByTime(){
              dispatch(deleteRoutesByTime())
                .unwrap()
                .then((res) => {
                //  message.success(res)
                })
              
            
            
          }
  return (
    <LanguageProvider>
      
    <Routes>
                  <Route path='/login' element={<Login/>} />
                  <Route path='/' element={<RoutesUser/>} />
                  <Route path='/header' element={<Landing/>} />
                  {/* <Route path='/landing' element={<DriverHeader/>} /> */}
                  <Route path='/bosh_sahifa' element={<AdminHeder/>} />
                  <Route path='/bir_haydovchi/:userName' element={<AboutDriversOne/>} />
                  <Route path="/register" element={<Register/>} />
                  <Route path="/yo'nalish" element={<PathDriver/>} />
                  <Route path="/o'zim_haqqimda" element={<MyselfDriver/>} />

    </Routes>
    </LanguageProvider>
    
  )
}

export default App
