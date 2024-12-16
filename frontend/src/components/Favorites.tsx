import { useEffect, useState } from "react";
import { RoomModel } from "./model/RoomModel.ts";
import RoomCard from "./RoomCard.tsx";
import axios from "axios";

type FavoritesProps = {
    favorites: string[];
    user: string;
    toggleFavorite: (roomId: string) => void;
};

export default function Favorites(props: Readonly<FavoritesProps>) {
    const [favoritesRooms, setFavoritesRooms] = useState<RoomModel[]>([]);

    useEffect(() => {
        axios
            .get(`/api/practice-hub/favorites`)
            .then((response) => {
                setFavoritesRooms(response.data);
            })
            .catch((error) => {
                console.error(error);
            });
    }, [props.user, props.favorites]);

    return (
        <div className={"room-card-container"}>
            {favoritesRooms.length > 0 ? (
                favoritesRooms.map((room) => (
                    <RoomCard key={room.id} room={room} user={props.user} favorites={props.favorites} toggleFavorite={props.toggleFavorite} />
                ))
            ) : (
                <p>No rooms is in favorites.</p>
            )}
        </div>
    );
}