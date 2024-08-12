import React, { useEffect, useState } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import "./login.scss";
import PhoneInput from "react-phone-input-2";

function Login() {
    const [user, setUser] = useState({ phoneNumber: '', password: '' });
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false); // State to toggle password visibility
    const navigate = useNavigate();

   
    
    function loginUser() {
        if (user.phoneNumber !== '' && user.password !== '') {
            setLoading(true);
            console.log(user);
            axios({
                url: 'http://localhost:8080/api/auth/login',
                method: 'POST',
                data: user,
            })
                .then((res) => {
                    setLoading(false);
                    if (res.data) {
                        localStorage.setItem('access_token', res.data.access_token);
                        localStorage.setItem('refresh_token', res.data.refresh_token);
                        setUser({ phoneNumber: '', password: '' });
                        navigate("/landing");
                    } else {
                        toast.error("Yaroqsiz parol yoki yaroqsiz username.");
                    }
                })
                .catch(() => {
                    setLoading(false);
                    toast.error("Tizimga kirishda xatolik yuz berdi.");
                });
        } else {
            toast.warning("Iltimos, barcha maydonlarni toʻldiring.");
        }
    }

    return (
        <div className={"big_mean"} >
            <div className="card">
                <ToastContainer toastStyle={{ backgroundColor: 'white', color: 'black' }} autoClose={1000} />

                <h1 style={{ textAlign: "center", fontSize: "40px", marginBottom: "10px" }}>Login</h1>
                <div className="input-field">
                    <PhoneInput
                        inputClass={"input-field"}
                        country={"uz"}
                        onChange={(e) => setUser({ ...user, phoneNumber: '+' + e })}
                        value={user.phoneNumber}
                    />
                </div>
                <div className="input-field">
                    <input
                        onChange={(e) => setUser({ ...user, password: e.target.value })}
                        value={user.password}
                        placeholder="Parol..."
                        type={showPassword ? "text" : "password"} // Toggle between text and password
                    />
                    <span
                        className="eye-icon"
                        onClick={() => setShowPassword(!showPassword)}
                    >
                        {showPassword ? '🙈' : '👁️'}
                    </span>
                </div>
                <button onClick={loginUser} className="buttonS">
                    {loading ? "Loading..." : "Login to your Account!"}
                </button>
            </div>
        </div>
    );
}

export default Login;
