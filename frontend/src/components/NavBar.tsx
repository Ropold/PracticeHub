import "./styles/NavBar.css"
import {useNavigate} from "react-router-dom";
import axios from "axios";

type NavbarProps = {
    user: string;
    getUser: () => void;
    getAllActiveRooms: () => void;
    getAllRooms: () => void;
}

export default function NavBar(props: Readonly<NavbarProps>) {
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
            <div
                className="clickable-header"
                onClick={() => {
                    props.getAllActiveRooms();
                    navigate("/");
                }}
            >
                <h2 className="header-title">PracticeHub</h2>
                <img
                    src="/PracticeHub-Logo.png"
                    alt="PracticeHub Logo"
                    className="logo-image"
                />
            </div>


            {props.user !== "anonymousUser" ? (
                <>
                    <button onClick={() => navigate(`/${props.user}/favorites`)}>Favorites</button>
                    <button onClick={() => navigate(`/${props.user}/add-room`)}>Add Room</button>
                    <button onClick={() => {
                        props.getAllRooms();
                        navigate(`/${props.user}/my-rooms`)
                    }}>My Rooms
                    </button>
                    <button onClick={() => navigate(`/${props.user}/profile`)}>Profile</button>
                    <button onClick={logoutFromGithub}>Logout</button>
                </>
            ) : (
                <button onClick={loginWithGithub}>Login with GitHub</button>
            )}
        </nav>
    );
}

