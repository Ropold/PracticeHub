import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import { useState } from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";


type HomeProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    rooms: RoomModel[];
}

export default function Home(props: Readonly<HomeProps>) {
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);

    return (
        <>
            <h1>PracticeHub</h1>
            <SearchBar
                value={searchQuery}
                onChange={setSearchQuery}
                rooms={props.rooms}
                setFilteredRooms={setFilteredRooms}
            />
            {filteredRooms.map((r) => (
                <RoomCard key={r.id} room={r} user={props.user} favorites={props.favorites} toggleFavorite={props.toggleFavorite} />
            ))}
        </>
    );
}
