import './App.css'
import Home from "./components/Home/Home.tsx";
import Details from "./components/Details.tsx";
import WishList from "./components/Wishlist.tsx";
import NavBar from "./components/NavBar.tsx";
import Footer from "./components/Footer.tsx";
import AddRoom from "./components/AddRoom.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";

export default function App() {
  return (
        <BrowserRouter>
            <NavBar/>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/room/:id" element={<Details />} />
                <Route path="/wishlist" element={<WishList />} />
                <Route path="/addroom" element={<AddRoom />} />
            </Routes>
            <Footer/>
        </BrowserRouter>
  )
}