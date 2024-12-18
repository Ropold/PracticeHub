import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import { useEffect, useState } from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";

type HomeProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    activeRooms: RoomModel[];
    showSearch: boolean;
};

export default function Home(props: Readonly<HomeProps>) {
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>([]);
    const [currentPage, setCurrentPage] = useState<number>(1);
    const roomsPerPage = 9; // Anzahl der Karten pro Seite

    useEffect(() => {
        if (!props.showSearch) {
            setSearchQuery("");  // Setzt den searchQuery auf einen leeren String
        }
    }, [props.showSearch]);

    useEffect(() => {
        const filtered = props.activeRooms.filter((room) => {
            const lowerQuery = searchQuery.toLowerCase();
            return (
                room.name.toLowerCase().includes(lowerQuery) ||
                room.address.toLowerCase().includes(lowerQuery) ||
                room.description.toLowerCase().includes(lowerQuery)
            );
        });
        setFilteredRooms(filtered);
    }, [props.activeRooms, searchQuery]);

    // Berechne die Karten für die aktuelle Seite
    const indexOfLastRoom = currentPage * roomsPerPage;
    const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
    const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

    // Berechne die Gesamtzahl der Seiten
    const totalPages = Math.ceil(filteredRooms.length / roomsPerPage);

    // Funktion für die Seitenumstellung
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

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
                {currentRooms.map((r) => (
                    <RoomCard
                        key={r.id}
                        room={r}
                        user={props.user}
                        favorites={props.favorites}
                        toggleFavorite={props.toggleFavorite}
                    />
                ))}
            </div>

            <div className="button-group">
                {Array.from({ length: totalPages }, (_, index) => (
                    <button
                        key={index + 1}
                        onClick={() => paginate(index + 1)}
                        className={index + 1 === currentPage ? "active" : ""}
                    >
                        {index + 1}
                    </button>
                ))}
            </div>
        </>
    );
}
