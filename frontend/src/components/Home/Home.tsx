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
    const [filterType, setFilterType] = useState<"name" | "address" | "category" | "all">("name"); // Filtertyp
    const [selectedCategory, setSelectedCategory] = useState<RoomModel["category"] | "">(""); // Kategorie
    const roomsPerPage = 9;

    useEffect(() => {
        if (!props.showSearch) {
            setSearchQuery("");  // Setzt den searchQuery zurück
        }
    }, [props.showSearch]);

    useEffect(() => {
        const filtered = filterRooms(props.activeRooms, searchQuery, filterType, selectedCategory);
        setFilteredRooms(filtered);
    }, [props.activeRooms, searchQuery, filterType, selectedCategory]);

    // Funktion zur Filterung der Räume
    const filterRooms = (rooms: RoomModel[], query: string, filterType: string, category: string | "") => {
        const lowerQuery = query.toLowerCase();

        return rooms.filter((room) => {
            // Kategorie-Filter
            const matchesCategory = category ? room.category === category : true;

            // Filter für Name, Adresse oder alles
            const matchesName = filterType === "name" && room.name.toLowerCase().includes(lowerQuery);
            const matchesAddress = filterType === "address" && room.address.toLowerCase().includes(lowerQuery);
            const matchesAll =
                filterType === "all" &&
                (room.name.toLowerCase().includes(lowerQuery) ||
                    room.address.toLowerCase().includes(lowerQuery) ||
                    room.description.toLowerCase().includes(lowerQuery));

            return matchesCategory && (matchesName || matchesAddress || matchesAll);
        });
    };

    // Berechne die Karten für die aktuelle Seite
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    // Berechne die Gesamtzahl der Seiten und den aktuellen Raum-Schnitt
    const getPaginationData = (rooms: RoomModel[]) => {
        const indexOfLastRoom = currentPage * roomsPerPage;
        const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
        const currentRooms = rooms.slice(indexOfFirstRoom, indexOfLastRoom);
        const totalPages = Math.ceil(rooms.length / roomsPerPage);
        return { currentRooms, totalPages };
    };

    const { currentRooms, totalPages } = getPaginationData(filteredRooms);

    return (
        <>
            {props.showSearch && (
                <SearchBar
                    value={searchQuery}
                    onChange={setSearchQuery}
                    rooms={props.activeRooms}
                    setFilteredRooms={setFilteredRooms}
                    filterType={filterType}
                    setFilterType={setFilterType}
                    selectedCategory={selectedCategory}
                    setSelectedCategory={setSelectedCategory}
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
