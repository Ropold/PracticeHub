import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useNavigate } from "react-router-dom";
import handleToggleWishlist from "../utils/handleToggleWishlist.ts";

type RoomCardProps = {
    room: RoomModel;
    user: string;
    onStatusChange: (updatedRoom: RoomModel) => void;
};

export default function RoomCard(props: Readonly<RoomCardProps>) {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/room/${props.room.id}`);
    };


    return (

        <div className="room-card" onClick={handleCardClick}>
            <div className="room-card-content">
                <h2>{props.room.name}</h2>
                <p><strong>Address: </strong>{props.room.address}</p>
                <p><strong>Category: </strong>{props.room.category}</p>
            </div>
            {props.user !== "anonymousUser" && (
                <button
                    id="button-wishlist"
                    onClick={(event) => {
                        event.stopPropagation(); // Verhindert die Weitergabe des Klicks an die Karte
                        handleToggleWishlist(props.room, props.onStatusChange);
                    }}
                    className={props.room.wishlistStatus === "ON_WISHLIST" ? "wishlist-on" : "wishlist-off"}
                >
                    ♥
                </button>
            )}
        </div>
    );
}

