import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import { useEffect, useState } from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";
import axios from "axios";

type HomeProps = {
    user: string;
    favorites: string[];
}

export default function Home(props: Readonly<HomeProps>) {
    const [rooms, setRooms] = useState<RoomModel[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);

    const getAllRooms = () => {
        axios
            .get("/api/practice-hub")
            .then((response) => {
                setRooms(response.data);
            })
            .catch((error) => {
                console.error(error);
            });
    };
    useEffect(getAllRooms, []);

    const handleStatusChange = (updatedRoom: RoomModel) => {
        setRooms((prevRooms) =>
            prevRooms.map((room) => (room.id === updatedRoom.id ? updatedRoom : room))
        );
    };

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
                <RoomCard key={r.id} room={r} user={props.user} favorites={props.favorites} onStatusChange={handleStatusChange} />
            ))}
        </>
    );
}
