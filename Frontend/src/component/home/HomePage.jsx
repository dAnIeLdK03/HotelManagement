import React from "react";
// import RoomResult from "../common/RoomResult.jsx";
// import RoomSearch from "../common/RoomSearch.jsx";

const HomePage = () => {
    // const [roomSearchResults, setRoomSearchResults] = useState([]);
    
    // const handleSearchResult = (result) => {
    //     setRoomSearchResults(result);
    // }
    
    return(
        <div className="home">
            {/*HEADER ROOM SECTION*/}
            <section>
                <header className="header-banner">
                    <img src="./assets/images/Hotel-Tips.jpg" alt="Hotel" className="header-image"/>
                    <div className="overlay"></div>
                    <div className="animated-texts overlay-content">
                        <h1>
                            Welcome to <span className="color-change">Hotel</span>
                        </h1><br/>
                        <h3>Step into a haven of comfort and relaxation</h3>
                    </div>
                </header>
            </section>

            {/* SERVICES SECTION */}
            <section className="service-section">
                <div className="service-card">
                    <img src="./assets/images/Hyatt Regency Malta Opens.jpg" alt="Air Conditioning" />
                    <div className="service-details">
                        <h3 className="service-title">Air Conditioning</h3>
                        <p className="service-description">Stay cool and comfortable throughout your stay with our individually controlled in-room air conditioning.</p>
                    </div>
                </div>
                <div className="service-card">
                    <img src="./assets/images/btv-mini-bar-artic-01.jpg" alt="Mini Bar" />
                    <div className="service-details">
                        <h3 className="service-title">Mini Bar</h3>
                        <p className="service-description">Enjoy a convenient selection of beverages and snacks stocked in your room's mini bar with no additional cost.</p>
                    </div>
                </div>
                <div className="service-card">
                    <img src="./assets/images/parking.jpg" alt="Parking" />
                    <div className="service-details">
                        <h3 className="service-title">Parking</h3>
                        <p className="service-description">We offer on-site parking for your convenience . Please inquire about valet parking options if available.</p>
                    </div>
                </div>
                <div className="service-card">
                    <img src="./assets/images/_wifi.png" alt="WiFi" />
                    <div className="service-details">
                        <h3 className="service-title">WiFi</h3>
                        <p className="service-description">Stay connected throughout your stay with complimentary high-speed Wi-Fi access available in all guest rooms and public areas.</p>
                    </div>
                </div>
            </section>
            
            {/* AVAILABLE ROOMS SECTION */}
            <section>
            </section>
        </div>
    )
}
export default HomePage;
