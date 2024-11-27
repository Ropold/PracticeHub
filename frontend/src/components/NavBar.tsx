import "./styles/NavBar.css"
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {useEffect, useState} from "react";


export default function NavBar() {
    const [user, setUser] = useState<string>("anonymousUser");
    const navigate = useNavigate();

    useEffect(() => {
        getUser()
    }, [])

    function loginWithGithub() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + '/oauth2/authorization/github', '_self')
    }

    function logoutFromGithub() {
        axios
            .post("/api/users/logout")
            .then(() => {
                getUser();
                navigate("/");
            })
            .catch((error) => {
                console.error("Logout failed:", error);
            });
    }

    function getUser() {
        axios.get("/api/users/me")
            .then((response) => {
                console.log(response.data)
                setUser(response.data)
            })
            .catch((error) => {
                console.error(error);
                setUser("anonymousUser");
            });
    }

    return (
        <nav className="navbar">
            <button onClick={() => navigate("/")}>Home</button>
            {user !== "anonymousUser" ? (
                <>
                    <button onClick={() => navigate("/wishlist")}>Wishlist</button>
                    <button onClick={() => navigate("/addroom")}>Add Room</button>
                    <button onClick={() => navigate("/profile")}>Profile</button>
                    <button onClick={logoutFromGithub}>Logout</button>
                </>
            ) : (
                <button onClick={loginWithGithub}>Login with GitHub</button>
            )}
        </nav>
    );
}
