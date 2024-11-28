import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useNavigate } from "react-router-dom";
import axios from "axios";

type RoomCardProps = {
    room: RoomModel;
    user: string;
    getUser: () => void;
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
            <div className="room-card" onClick={handleCardClick} style={{ cursor: "pointer" }}>
                <div className="room-card-content">
                    <h2>{props.room.name}</h2>
                    <p><strong>Address: </strong>{props.room.address}</p>
                    <p><strong>Category: </strong>{props.room.category}</p>
                </div>
                {/* Herz-Button nur anzeigen, wenn der Benutzer nicht "anonymousUser" ist */}
                {props.user !== "anonymousUser" && (
                    <button
                        id="button-wishlist"
                        onClick={(event) => {
                            event.stopPropagation(); // Verhindert die Weitergabe des Klicks an die Karte
                            handleToggleWishlist();
                        }}
                        className={props.room.wishlistStatus === "ON_WISHLIST" ? "wishlist-on" : "wishlist-off"}
                    >
                        ♥
                    </button>
                )}
            </div>
            );

}

// return (
//     <div className="room-card" onClick={handleCardClick} style={{cursor: "pointer"}}>
//         <div className="room-card-content">
//             <h2>{props.room.name}</h2>
//             <p><strong>Address: </strong>{props.room.address}</p>
//             <p><strong>Category: </strong>{props.room.category}</p>
//         </div>
//         <button
//             id="button-wishlist"
//             onClick={(event) => {
//                 event.stopPropagation(); // Verhindert die Weitergabe des Klicks an die Karte
//                 handleToggleWishlist();}}
//             className={props.room.wishlistStatus === "ON_WISHLIST" ? "wishlist-on" : "wishlist-off"}
//         >♥</button>
//     </div>
// );
