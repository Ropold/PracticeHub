import "./styles/RoomCard.css";
import {RoomModel} from "./model/RoomModel.ts";
import {useNavigate} from "react-router-dom";

type RoomCardProps = {
    room: RoomModel;
    onToggleWishlist: (roomId: string) => void;
};

export default function RoomCard(props: Readonly<RoomCardProps>) {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/room/${props.room.id}`);
    };

    return (<>

            <div className="room-card" onClick={handleCardClick} style={{cursor: 'pointer'}}>
                <h2>{props.room.name}</h2>
                <p><strong>Address: </strong>{props.room.address}</p>
                <p><strong>Category: </strong> {props.room.category}</p>
                <p><strong>Description: </strong> {props.room.description}</p>
            </div>

            <button id="button-wishlist" onClick={() => props.onToggleWishlist(props.room.id)}
                style={{color: props.room.wishlistStatus === "ON_WISHLIST" ? "red" : "black"}}
            >♥</button>
        </>

    );
}

