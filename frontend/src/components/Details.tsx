import "./styles/Details.css";
import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useEffect, useState } from "react";
import {useParams} from "react-router-dom";
import axios from "axios";
import MapBox from "./MapBox.tsx";
import {getCategoryDisplayName} from "../utils/GetCategoryDisplayName.ts";

type DetailsProps = {
    favorites: string[];
    user: string;
    toggleFavorite: (roomId: string) => void;
}

const defaultRoom: RoomModel = {
    id: "",
    name: "Loading....",
    address: "",
    category: "SOLO_DUO_ROOM",
    description: "",
    appUserGithubId: "",
    appUserUsername: "",
    appUserAvatarUrl: "",
    appUserGithubUrl: "",
    isActive: true,
    imageUrl: "",
};

export default function Details(props: Readonly<DetailsProps>) {
    const [room, setRoom] = useState<RoomModel>(defaultRoom);


    const { id } = useParams<{ id: string }>();

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
                <p><strong>Category: </strong> {getCategoryDisplayName(room.category)}</p>
                <p><strong>Description: </strong> {room.description}</p>
                {room.imageUrl && (
                    <img
                        src={room.imageUrl}
                        alt={room.name}
                        className="room-card-image"
                    />
                )}

                {props.user !== "anonymousUser" && (
                    <div className="button-group">
                        <button
                            onClick={() => props.toggleFavorite(room.id)}
                            className={isFavorite ? "favorite-on" : "favorite-off"}
                        >                            ♥
                        </button>
                    </div>
                )}

                <MapBox address={room.address}/>

                <div className="profile-container">
                    <h3>Room added by GitHub User</h3>
                    <div>
                        <p><strong>Username: </strong> {room.appUserUsername}</p>
                        <p><strong>GitHub Profile: </strong>
                            <a href={room.appUserGithubUrl} target="_blank" rel="noopener noreferrer">Visit Profile</a>
                        </p>
                        <img
                            src={room.appUserAvatarUrl}
                            alt={`${room.appUserUsername}'s avatar`}
                            className="user-avatar"
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}