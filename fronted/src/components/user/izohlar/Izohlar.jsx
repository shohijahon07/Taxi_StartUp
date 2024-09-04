import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux';
import { fetchDriverOne } from '../../../redux/slices/DriverSlice';
import "./izohlar.css"
import { fetchComments } from '../../../redux/slices/CommentSlice';

const Izohlar = ({ isOpen, onClose, userName }) => {
    const dispatch = useDispatch();
    const { driverOne } = useSelector((state) => state.driver);
    const { comments } = useSelector((state) => state.comment);
    const [active, setActive] = useState(false);

    useEffect(() => {
        if (isOpen) {
            dispatch(fetchDriverOne(userName));
            dispatch(fetchComments(userName));
            setActive(true);
        } else {
            setActive(false);
        }
    }, [dispatch, isOpen, userName]);

    const handleClose = () => {
        setActive(false);
        setTimeout(onClose, 1000); 
    };

    if (!isOpen) return null;

    return (
        <div className={`izohlar-modal ${active ? 'active' : ''}`}>
            <div className={`izohlar-modal-child ${active ? 'active' : ''}`}>
                <button className='btn btn-close exitBtn' onClick={handleClose}></button>
                <div className="text">
                    {driverOne?.length > 0 && driverOne.map((item) => (
                        <li className='list-group-item' key={item.id}>
                            Haydovchi o’zi haqida: {item.about}
                        </li>
                    ))}
                    {comments?.length > 0 && comments.map((item) => (
                        <li key={item.id} className='list-group-item userComment'>
                            <p>{item.name}</p>: <p>{item.text}</p>
                        </li>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default Izohlar;
