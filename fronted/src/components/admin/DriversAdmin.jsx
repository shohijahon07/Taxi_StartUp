import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { deleteDriver, fetchDrivers1, fetchDriversByFullName } from '../../redux/slices/DriverSlice';
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import { FaSearch } from "react-icons/fa";
import "./routesAdmin.css";

function DriversAdmin() {
  const { drivers1  } = useSelector((state) => state.driver); 
  const dispatch = useDispatch();
  const [isDriver, setIsDriver] = useState(true);

  useEffect(() => {
    dispatch(fetchDrivers1(isDriver));
  }, [dispatch, isDriver]);

  const navigate = useNavigate();

  function goAboutDrivers(userName) {
    navigate(`/bir_haydovchi/${userName}`);
  }

  function getByName(name) {
    dispatch(fetchDriversByFullName(name))
      .unwrap()
      .then(() => {
      })
      .catch(() => {
        toast.error('Ma\'lumotlarni olishda xatolik yuz berdi!');
      });
  }

  function deleteDrive(id) {
    dispatch(deleteDriver({ id }))
      .unwrap()
      .then(() => {
        toast.success("Malumot muvaffaqiyatli o'chirildi!");
        dispatch(fetchDrivers1(isDriver));
      })
      .catch((err) => console.log(err));
  }

  return (
    <div>
      <div className="qidirishHeader">
        <div className="search-input-wrapper">
          <input
            type="text"
            className="form-control"
            placeholder="Qidirish"
            onChange={(e) => getByName(e.target.value)}
          />
          <FaSearch className="search-icon" />
        </div>
      </div>

      <table className="table table-success">
        <thead>
          <tr>
            <th>N#</th>
            <th>Ism Familiya</th>
            <th>Telefon Raqam</th>
            <th>Mashina Rusumi</th>
            <th>Mashina Rasmi</th>
            <th>Haydovchilik Guvohnomasi</th>
            <th>Mashina Texpasporti</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {
            drivers1.length > 0 ? (
              drivers1.map((item, i) => (
                <tr key={item.id}>
                  <td>{i + 1}</td>
                  <td>{item.fullName}</td>
                  <td>{item.phoneNumber}</td>
                  <td>{item.carType}</td>
                  <td>
                    <img
                      className="imageTable"
                      style={{ objectFit: "cover" }}
                      src={`http:/api/fileController/photo?img=${item.carImg}`}
                      alt="#"
                    />
                  </td>
                  <td>
                    <img
                      className="imageTable"
                      style={{ objectFit: "cover" }}
                      src={`http:/api/fileController/photo?img=${item.driverImg}`}
                      alt="#"
                    />
                  </td>
                  <td>
                    <img
                      className="imageTable"
                      style={{ objectFit: "cover" }}
                      src={`http:/api/fileController/photo?img=${item.cardDocument}`}
                      alt="#"
                    />
                  </td>
                  <td>
                    <button
                      className="saveButton"
                      onClick={() => goAboutDrivers(item.id)}
                    >
                      Haqida
                    </button>
                    <button
                      className="deleteBTN"
                      onClick={() => deleteDrive(item.id)}
                    >
                      O'chirish
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8">No drivers found</td>
              </tr>
            )
          }
        </tbody>
      </table>

      <ToastContainer
        toastStyle={{
          backgroundColor: 'white',
          color: 'black',
        }}
        autoClose={1000}
      />
    </div>
  );
}

export default DriversAdmin;
