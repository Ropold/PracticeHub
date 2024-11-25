import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";

type RoomCardProps = {
    room: RoomModel;
}

export default function RoomCard(props: Readonly<RoomCardProps>) {
    return (
        <div className="room-card">
            <h3>{props.room.name}</h3>
            <p>{props.room.address}</p>
            <p>{props.room.category}</p>
            <p>{props.room.description}</p>
        </div>
    );
}
