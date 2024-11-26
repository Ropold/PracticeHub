import "./styles/Wishlist.css";
import { useEffect, useState } from "react";
import { RoomModel } from "./model/RoomModel.ts";
import axios from "axios";

export default function Wishlist() {
    const [rooms, setRooms] = useState<RoomModel[]>([]);
    const [wishlistRooms, setWishlistRooms] = useState<RoomModel[]>([]);

    useEffect(() => {
        axios
            .get("/api/practice-hub")
            .then((response) => {
                const rooms = response.data;
                setRooms(rooms);
                const roomsOnWishlist = rooms.filter(
                    (room:RoomModel) => room.wishlistStatus === "ON_WISHLIST"
                );
                setWishlistRooms(roomsOnWishlist);
            })
            .catch((error) => {
                console.error(error);
            });
    }, []);

    // axios
    //     .get<RoomModel[]>("/api/practice-hub") // Typ hinzugefügt
    //     .then(({ data }) => {
    //         setRooms(data); // Direktes Setzen der Räume
    //         setWishlistRooms(data.filter((room) => room.wishlistStatus === "ON_WISHLIST"));
    //     })
    //     .catch((error) => {
    //         console.error("API Error:", error);
    //     });

    return (
        <div>
            <h2>Wishlist</h2>
            {wishlistRooms.length > 0 ? (
                wishlistRooms.map((room) => (
                    <div key={room.id} className="wishlist-room">
                        <h3>{room.name}</h3>
                        <p>{room.address}</p>
                        <p>{room.category}</p>
                        <p>{room.description}</p>
                    </div>
                ))
            ) : (
                <p>No rooms on the wishlist.</p>
            )}
        </div>
    );
}
