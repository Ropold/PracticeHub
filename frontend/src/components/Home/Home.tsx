import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import { useState } from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";


type HomeProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    activeRooms: RoomModel[];
}

export default function Home(props: Readonly<HomeProps>) {
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);
    const [showSearch, setShowSearch] = useState<boolean>(false);

    const toggleSearchBar = () => {
        setShowSearch(prevState => !prevState);
    };

    return (
        <>
            <button onClick={toggleSearchBar}>
                {showSearch ? "Hide Search" : "Show Search"}
            </button>

            {showSearch && (
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
