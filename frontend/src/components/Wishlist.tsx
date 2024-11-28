import { useEffect, useState } from "react";
import { RoomModel } from "./model/RoomModel.ts";
import RoomCard from "./RoomCard.tsx";
import axios from "axios";

type WishlistProps = {
    user: string;
    onStatusChange?: (updatedRoom: RoomModel) => void;
};

export default function Wishlist(props: Readonly<WishlistProps>) {
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
                    <RoomCard key={room.id} room={room} user={props.user} onStatusChange={handleStatusChange} />
                ))
            ) : (
                <p>No rooms on the wishlist.</p>
            )}
        </div>
    );
}
