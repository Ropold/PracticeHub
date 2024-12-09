import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import {useEffect, useState} from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";

type HomeProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    activeRooms: RoomModel[];
    showSearch: boolean;
    resetSearchQuery: string;
}

export default function Home(props: Readonly<HomeProps>) {
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);

    // Effekt, der searchQuery zurücksetzt, wenn resetSearchQuery den Wert "reset" hat
    useEffect(() => {
        if (props.resetSearchQuery === "reset") {
            setSearchQuery("");  // Setzt den searchQuery auf einen leeren String
        }
    }, [props.resetSearchQuery]);  // Reagiert auf Änderungen von resetSearchQuery


    useEffect(() => {
        const filtered = props.activeRooms.filter((room) => {
            return room.name.toLowerCase().includes(searchQuery.toLowerCase());
        });
        setFilteredRooms(filtered);
    }, [props.activeRooms, searchQuery]);

    return (
        <>
            {props.showSearch && (
                <SearchBar
                    value={searchQuery}
                    onChange={setSearchQuery}
                    rooms={props.activeRooms}
                    setFilteredRooms={setFilteredRooms}
                />
            )}
            <div className="room-card-container">
                {filteredRooms.map((r) => (
                    <RoomCard
                        key={r.id}
                        room={r}
                        user={props.user}
                        favorites={props.favorites}
                        toggleFavorite={props.toggleFavorite}
                    />
                ))}
            </div>
        </>
    );
}
