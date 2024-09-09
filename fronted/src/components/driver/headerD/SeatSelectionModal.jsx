import React, { useState, useEffect } from 'react';
import './seatModal.css'; // CSS fayl nomi o'zgarmadi

function SeatSelectionModal({ onSelect, onClose, isTrue }) {
    const [selectedSeat, setSelectedSeat] = useState(null);
    const [seatCount, setSeatCount] = useState(isTrue ? 6 : 12);
    const seats = Array.from({ length: seatCount }, (_, index) => index + 1);

    useEffect(() => {
        setSeatCount(isTrue ? 6 : 12);
    }, [isTrue]);

    const handleSeatSelect = (seatNumber) => {
        setSelectedSeat(seatNumber);
        onSelect(seatNumber);
        onClose();
        console.log(seats);
        console.log(isTrue);
    };

    return (
        <div className="modal-overlay1" onClick={onClose}>
            <div className="modal-content1" onClick={(e) => e.stopPropagation()}>
                <div className="close-icon-container1">
                    <button className="close-button1 " onClick={onClose}>x</button>
                </div>
                <div className="modal-header1">
                    <p>O'rindiqlar sonini tanlang!</p>
                </div>
                <div className="seat-container1" style={{ marginTop: '15px', display: 'flex', flexWrap: 'wrap' }}>
                    {seats.map((seatNumber) => (
                        <button
                            key={seatNumber}
                            className={`seat-button ${selectedSeat === seatNumber ? 'seat-selected' : ''}`}
                            onClick={() => handleSeatSelect(seatNumber)}
                        >
                            {seatNumber}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default SeatSelectionModal;
