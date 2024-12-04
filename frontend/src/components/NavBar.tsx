import "./styles/NavBar.css"
import {useNavigate} from "react-router-dom";
import axios from "axios";


type NavbarProps = {
    user: string;
    getUser: () => void;
}

export default function NavBar(props: NavbarProps) {
    const navigate = useNavigate();


    function loginWithGithub() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + '/oauth2/authorization/github', '_self')
    }

    function logoutFromGithub() {
        axios
            .post("/api/users/logout")
            .then(() => {
                props.getUser();
                navigate("/");
            })
            .catch((error) => {
                console.error("Logout failed:", error);
            });
    }



    return (
        <nav className="navbar">
            <button onClick={() => navigate("/")}>Home</button>
            {props.user !== "anonymousUser" ? (
                <>
                    <button onClick={() => navigate(`/favorites/${props.user}`)}>Wishlist</button>
                    <button onClick={() => navigate("/add-room")}>Add Room</button>
                    <button onClick={() => navigate("/profile")}>Profile</button>
                    <button onClick={logoutFromGithub}>Logout</button>
                </>
            ) : (
                <button onClick={loginWithGithub}>Login with GitHub</button>
            )}
        </nav>
    );
}

