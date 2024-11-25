
import './App.css'
import Home from "./components/Home/Home.tsx";
import Details from "./components/Details.tsx";
import WishList from "./components/Wishlist.tsx";
import NavBar from "./components/NavBar.tsx";
import Footer from "./components/Footer.tsx";
import AddRoom from "./components/AddRoom.tsx";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {RoomModel} from "./components/model/RoomModel.ts";
import {useEffect, useState} from "react";
import axios from "axios";


function App() {

    const [rooms, setRooms] = useState<RoomModel[]>([])

    const getAllRooms = () => {
        axios.get("/api/practice-hub").then(
            (response) => {
                setRooms(response.data)
            }
        ).catch((error) => {
            console.error(error)
        })
    }
    useEffect(getAllRooms, [])
    console.log("Rooms in state:", rooms);

  return (
        <Router>
            <NavBar/>
            <Routes>
                <Route path="/" element={<Home rooms={rooms} />} />
                <Route path="/room/:id" element={<Details />} />
                <Route path="/wishlist" element={<WishList />} />
                <Route path="/addroom" element={<AddRoom />} />
            </Routes>
            <Footer/>
        </Router>
  )
}

export default App

