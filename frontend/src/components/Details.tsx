import "./styles/Details.css";
import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useEffect, useState } from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import MapBox from "./MapBox.tsx";

type DetailsProps = {
    favorites: string[];
    user: string;
    toggleFavorite: (roomId: string) => void;
}

const defaultRoom: RoomModel = {
    id: "",
    name: "Loading....",
    address: "",
    category: "",
    description: "",
    appUserGitbubId: "",
    imageUrl: "",
};

export default function Details(props: Readonly<DetailsProps>) {
    const [room, setRoom] = useState<RoomModel>(defaultRoom);

    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const fetchRoomDetails = () => {
        if (!id) return;
        axios
            .get(`/api/practice-hub/${id}`)
            .then((response) => setRoom(response.data))
            .catch((error) => console.error("Error fetching room details", error));
    };

    useEffect(() => {
        fetchRoomDetails();
    }, [id]);

    const isFavorite = props.favorites.includes(room.id);

    return (
        <div className="details-container">
            <div className="room-details">
                <h2>{room.name}</h2>
                <p><strong>Address: </strong> {room.address}</p>
                <p><strong>Category: </strong> {room.category}</p>
                <p><strong>Description: </strong> {room.description}</p>
                <p><strong>Added by Github-User: </strong> {room.appUserGitbubId}</p>
                {room.imageUrl && (
                    <img
                        src={room.imageUrl}
                        alt={room.name}
                        className="room-card-image"
                    />
                )}
                {props.user !== "anonymousUser" && (
                    <div className="wishlist-container">
                        <button
                            id="button-favorite-details-card"
                            onClick={() => props.toggleFavorite(room.id)}
                            className={isFavorite ? "favorite-on" : "favorite-off"}
                        >
                            ♥
                        </button>
                    </div>
                )}
                <MapBox address={room.address} />
            </div>
        </div>
    );
}