import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useNavigate } from "react-router-dom"; // Import useNavigate

type RoomCardProps = {
    room: RoomModel;
}

export default function RoomCard(props: Readonly<RoomCardProps>) {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/room/${props.room.id}`);
    };

    return (
        <div className="room-card" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
            <h3>{props.room.name}</h3>
            <p>{props.room.address}</p>
            <p>{props.room.category}</p>
            <p>{props.room.description}</p>
        </div>
    );
}
