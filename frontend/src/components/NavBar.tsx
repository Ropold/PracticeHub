import "./styles/NavBar.css"
import {useNavigate} from "react-router-dom";


export default function NavBar() {

    const navigate = useNavigate();

    return(
        <>
            <button>login with github</button>
            <button onClick={() => navigate("/")}>Home</button>
            <button onClick={() => navigate("/wishlist")}>Wishlist</button>
            <button onClick={() => navigate("/addroom")}>Add Room</button>
            <button>logout</button>
        </>
    )
}