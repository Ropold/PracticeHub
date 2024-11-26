import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useNavigate } from "react-router-dom";
import axios from "axios";

type RoomCardProps = {
    room: RoomModel;
    onStatusChange?: (updatedRoom: RoomModel) => void;
};

export default function RoomCard(props: Readonly<RoomCardProps>) {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/room/${props.room.id}`);
    };

    const handleToggleWishlist = () => {
        const updatedStatus =
            props.room.wishlistStatus === "ON_WISHLIST" ? "NOT_ON_WISHLIST" : "ON_WISHLIST";

        const updatedRoom = { ...props.room, wishlistStatus: updatedStatus };

        axios
            .put(`/api/practice-hub/${props.room.id}`, updatedRoom)
            .then((response) => {
                if (props.onStatusChange) {
                    props.onStatusChange(response.data);
                }
            })
            .catch((error) => console.error("Error updating wishlist status:", error));
    };

    return (
        <>
            <div className="room-card" onClick={handleCardClick}>
                <h2>{props.room.name}</h2>
                <p><strong>Address: </strong>{props.room.address}</p>
                <p><strong>Category: </strong>{props.room.category}</p>
                <p><strong>Description: </strong>{props.room.description}</p>
            </div>

            <button id="button-wishlist" onClick={handleToggleWishlist}
                    style={{color: props.room.wishlistStatus === "ON_WISHLIST" ? "red" : "black"}}
            >♥</button>
        </>
    );
}
