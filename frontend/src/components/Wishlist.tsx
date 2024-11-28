import "./styles/Wishlist.css";
import { useEffect, useState } from "react";
import { RoomModel } from "./model/RoomModel.ts";
import RoomCard from "./RoomCard.tsx";
import axios from "axios";

export default function Wishlist() {
    const [wishlistRooms, setWishlistRooms] = useState<RoomModel[]>([]);

    useEffect(() => {
        axios
            .get("/api/practice-hub")
            .then((response) => {
                const rooms = response.data;
                setWishlistRooms(rooms.filter((room: RoomModel) => room.wishlistStatus === "ON_WISHLIST"));
            })
            .catch((error) => {
                console.error(error);
            });
    }, []);

    const handleStatusChange = (updatedRoom: RoomModel) => {
        setWishlistRooms((prevWishlistRooms) =>
            prevWishlistRooms.filter((room) => room.id !== updatedRoom.id)
        );
    };

    return (
        <div>
            <h2>Wishlist</h2>
            {wishlistRooms.length > 0 ? (
                wishlistRooms.map((room) => (
                    <RoomCard key={room.id} room={room} onStatusChange={handleStatusChange} />
                ))
            ) : (
                <p>No rooms on the wishlist.</p>
            )}
        </div>
    );
}
