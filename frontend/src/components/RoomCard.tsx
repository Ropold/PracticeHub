import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useNavigate } from "react-router-dom";
import {getCategoryDisplayName} from "../utils/GetCategoryDisplayName.ts";

type RoomCardProps = {
    room: RoomModel;
    favorites: string[];
    user: string;
    toggleFavorite: (roomId: string) => void;
};

export default function RoomCard(props: Readonly<RoomCardProps>) {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/room/${props.room.id}`);
    };

    const isFavorite = props.favorites.includes(props.room.id);

    return (
        <div className="room-card" onClick={handleCardClick}>
            <div className={`room-card-text ${!props.room.imageUrl ? 'no-image' : ''}`}>
                <h2>{props.room.name}</h2>
                <p><strong>Address: </strong>{props.room.address}</p>
                <p><strong>Category: </strong>{getCategoryDisplayName(props.room.category)}</p>
            </div>

            <div>
                {props.room.imageUrl ? (
                    <img
                        src={props.room.imageUrl}
                        alt={props.room.name}
                        className="room-card-image"
                    />
                ) : null}
            </div>

            {props.user !== "anonymousUser" && (
                <button
                    id="button-favorite-room-card"
                    onClick={(event) => {
                        event.stopPropagation(); // Verhindert die Weitergabe des Klicks an die Karte
                        props.toggleFavorite(props.room.id);
                    }}
                    className={isFavorite ? "favorite-on" : "favorite-off"}
                >
                    ♥
                </button>
            )}
        </div>
    );
}