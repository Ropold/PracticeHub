import React, { useState, useEffect, ChangeEvent } from "react";
import "../styles/SearchBar.css";
import { RoomModel } from "../model/RoomModel.ts";
import "../styles/SearchBar.css";

type SearchBarProps = {
    value: string;
    onChange: (value: string) => void;
    rooms: RoomModel[];
    setFilteredRooms: (rooms: RoomModel[]) => void;
};

const SearchBar: React.FC<SearchBarProps> = ({ value, onChange, rooms, setFilteredRooms }) => {
    const [filterType, setFilterType] = useState<"name" | "address" | "category" | "all">("name");
    const [selectedCategory, setSelectedCategory] = useState<RoomModel["category"] | "">("");

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        onChange(event.target.value);
    };

    const handleCategoryChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const selectedValue = event.target.value as RoomModel["category"] | "";
        setSelectedCategory(selectedValue);
    };

    useEffect(() => {
        const filtered = rooms.filter((room) => {
            const lowerQuery = value.toLowerCase();

            // Prüfen, ob die Kategorie passt (wenn ausgewählt)
            const matchesCategory = selectedCategory ? room.category === selectedCategory : true;

            // Prüfen, ob Name oder Adresse passt
            const matchesName = filterType === "name" && room.name.toLowerCase().includes(lowerQuery);
            const matchesAddress = filterType === "address" && room.address.toLowerCase().includes(lowerQuery);

            // Für "all": Überprüfen auf Name, Adresse oder Beschreibung
            const matchesAll =
                filterType === "all" &&
                (room.name.toLowerCase().includes(lowerQuery) ||
                    room.address.toLowerCase().includes(lowerQuery) ||
                    room.description.toLowerCase().includes(lowerQuery));

            // Zusammenführen: Kategorie und spezifische Filter
            return matchesCategory && (matchesName || matchesAddress || matchesAll);
        });


        setFilteredRooms(filtered);
    }, [value, filterType, rooms, selectedCategory, setFilteredRooms]);

    return (
        <div className="search-bar">
            <input
                type="text"
                placeholder="Search PracticeHub..."
                value={value}
                onChange={handleInputChange}
            />
            <div className="filter-buttons">
                <button
                    onClick={() => {
                        setFilterType("name");
                    }}
                    className={filterType === "name" ? "active" : ""}
                >Name
                </button>
                <button
                    onClick={() => {
                        setFilterType("address");
                    }}
                    className={filterType === "address" ? "active" : ""}
                >Address
                </button>
                <button
                    onClick={() => {
                        setFilterType("all");
                        setSelectedCategory("");
                        onChange('');
                    }}
                    className={filterType === "all" && selectedCategory === "" ? "active" : ""}
                >No Filter
                </button>
                <label>
                    <select
                        className="input-small"
                        value={selectedCategory}
                        onChange={handleCategoryChange}
                    >
                        <option value="">Filter by a category</option>
                        <option value="SOLO_DUO_ROOM">Solo/Duo Room</option>
                        <option value="BAND_ROOM">Band Room</option>
                        <option value="STUDIO_ROOM">Studio Room</option>
                        <option value="ORCHESTER_HALL">Orchestra Hall</option>
                    </select>
                </label>
            </div>
        </div>
    );
};

export default SearchBar;
