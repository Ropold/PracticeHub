import "./styles/NavBar.css"
import {useNavigate} from "react-router-dom";
import axios from "axios";


export default function NavBar() {

    const navigate = useNavigate();

    function loginWithGithub() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080': window.location.origin

        window.open(host + '/oauth2/authorization/github', '_self')
    }

    function getUser() {
        axios.get("/api/user/me")
            .then((response) => {
                console.log(response.data)
            })
            .catch((error) => {
                console.error(error);
            });
    }

    return(
        <>
            <button onClick={() => navigate("/")}>Home</button>
            <button onClick={() => navigate("/wishlist")}>Wishlist</button>
            <button onClick={() => navigate("/addroom")}>Add Room</button>
            <button onClick={loginWithGithub}>login with github</button>
            <button>logout</button>
            <button onClick={getUser}>Me</button>
        </>
    )
}