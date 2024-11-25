import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";

type RoomCardProps = {
    room: RoomModel;
}

export default function RoomCard(props: Readonly<RoomCardProps>) {
    return (
        <div className="room-card">
            <h2>RoomCard</h2>
            <h3>{props.room.name}</h3>
            <p>{props.room.address}</p>
            <p>{props.room.category}</p>
        </div>
    );
}
