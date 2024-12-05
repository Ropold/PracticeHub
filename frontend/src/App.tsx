import './App.css'
import Home from "./components/Home/Home.tsx";
import Details from "./components/Details.tsx";
import Favorites from "./components/Favorites.tsx";
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
    const [favorites, setFavorites] = useState<string[]>([]);

    function getUser() {
        axios.get("/api/users/me")
            .then((response) => {
                console.log("User Object:", JSON.stringify(response.data, null, 2));
                setUser(response.data.id)
            })
            .catch((error) => {
                console.error(error);
                setUser("anonymousUser");
            });
    }

    function getAppUserFavorites(){
        axios.get(`/api/practice-hub/favorites/${user}`)
            .then((response) => {
                setFavorites(response.data)
            })
            .catch((error) => {
                console.error(error);
            });
    }

    function toggleFavorite(roomId: string) {
        const isFavorite = favorites.includes(roomId);

        if (isFavorite) {
            axios.delete(`/api/practice-hub/favorites/${user}/${roomId}`)
                .then(() => {
                    setFavorites((prevFavorites) =>
                        prevFavorites.filter((id) => id !== roomId)
                    );
                })
                .catch((error) => console.error(error));
        } else {
            axios.post(`/api/practice-hub/favorites/${user}/${roomId}`)
                .then(() => {
                    setFavorites((prevFavorites) => [...prevFavorites, roomId]);
                })
                .catch((error) => console.error(error));
        }
    }

    useEffect(() => {
        getUser()
    }, []);

    useEffect(() => {
        if (user !== "anonymousUser") {
            getAppUserFavorites();
        }
    }, []); //user rein????

    return (
        <BrowserRouter>
            <NavBar user={user} getUser={getUser}/>
            <Routes>
                <Route path="/" element={<Home favorites={favorites} user={user} toggleFavorite={toggleFavorite}/>} />
                <Route path="/room/:id" element={<Details favorites={favorites} user={user} toggleFavorite={toggleFavorite}/>} />
                <Route element={<ProtectedRoute user={user} />}>
                    <Route path="/favorites/:id" element={<Favorites favorites={favorites} user={user} toggleFavorite={toggleFavorite}/>} />
                    <Route path="/add-room" element={<AddRoom user={user}/>} />
                    <Route path={"/profile"} element={<Profile />} />
                </Route>
            </Routes>
            <Footer/>
        </BrowserRouter>
  )
}

