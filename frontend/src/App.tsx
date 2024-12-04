import './App.css'
import Home from "./components/Home/Home.tsx";
import Details from "./components/Details.tsx";
import WishList from "./components/Wishlist.tsx";
import NavBar from "./components/NavBar.tsx";
import Footer from "./components/Footer.tsx";
import AddRoom from "./components/AddRoom.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Profile from "./components/Profile.tsx";
import {useEffect, useState} from "react";
import axios from "axios";

export default function App() {
    const [user, setUser] = useState<string>("anonymousUser");

    function getUser() {
        axios.get("/api/users/me")
            .then((response) => {
                console.log("Github-userId: " + response.data)
                setUser(response.data.id)
            })
            .catch((error) => {
                console.error(error);
                setUser("anonymousUser");
            });
    }

    useEffect(() => {
        getUser()
    }, []);

  return (
        <BrowserRouter>
            <NavBar user={user} getUser={getUser}/>
            <Routes>
                <Route path="/" element={<Home user={user}/>} />
                <Route path="/room/:id" element={<Details user={user} />} />
                <Route element={<ProtectedRoute user={user} />}>
                    <Route path="/wishlist/:id" element={<WishList user={user}/>} />
                    <Route path="/add-room" element={<AddRoom />} />
                    <Route path={"/profile"} element={<Profile />} />
                </Route>
            </Routes>
            <Footer/>
        </BrowserRouter>
  )
}

