import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import {useEffect, useState} from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";
import axios from "axios";


export default function Home() {
    const [rooms, setRooms] = useState<RoomModel[]>([])
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);

    const getAllRooms = () => {
        axios.get("/api/practice-hub").then(
            (response) => {
                setRooms(response.data)
            }
        ).catch((error) => {
            console.error(error)
        })
    }
    useEffect(getAllRooms, [])

    const handleToggleWishlist = (roomId: string) => {
        const room = rooms.find((r) => r.id === roomId)
        if (!room) return

        const updatedStatus = room.wishlistStatus === "ON_WISHLIST" ? "NOT_ON_WISHLIST" : "ON_WISHLIST"

        const updatedRoom = {...room, wishlistStatus: updatedStatus}

        axios.put(`/api/practice-hub/${roomId}`, updatedRoom).then(
            (response) => {
                setRooms((prevRooms) => prevRooms.map((r) => (r.id === roomId ? response.data : r))
                )
            }
        ).catch((error) => console.error("Error updating wishlist status:", error))
    }


    return (
        <>
            <h1>PracticeHub</h1>
            <SearchBar
                value={searchQuery}
                onChange={setSearchQuery}
                rooms={rooms}
                setFilteredRooms={setFilteredRooms}
            />
            {filteredRooms.map((r) => (
                <RoomCard
                    key={r.id}
                    room={r}
                    onToggleWishlist={handleToggleWishlist}
                />
            ))}
        </>
    );
}