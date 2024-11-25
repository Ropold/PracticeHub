import React, { useState, useEffect, ChangeEvent } from "react";
import "../styles/SearchBar.css";
import { RoomModel } from "../model/RoomModel";

type SearchBarProps = {
    value: string;
    onChange: (value: string) => void;
    rooms: RoomModel[];
    setFilteredRooms: (rooms: RoomModel[]) => void;
};

const SearchBar: React.FC<SearchBarProps> = ({value, onChange, rooms, setFilteredRooms}) => {
    const [filterType, setFilterType] = useState<"name" | "address" | "category" | "all">("name");

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        onChange(event.target.value);
    };

    useEffect(() => {
        const filtered = rooms.filter((room) => {
            const lowerQuery = value.toLowerCase();

            if (filterType === "all") {
                return (
                    room.name.toLowerCase().includes(lowerQuery) ||
                    room.address.toLowerCase().includes(lowerQuery) ||
                    room.category.toLowerCase().includes(lowerQuery) ||
                    room.description.toLowerCase().includes(lowerQuery)
                );
            }

            switch (filterType) {
                case "name":
                    return room.name.toLowerCase().includes(lowerQuery);
                case "address":
                    return room.address.toLowerCase().includes(lowerQuery);
                case "category":
                    return room.category.toLowerCase().includes(lowerQuery);
            }
        });

        setFilteredRooms(filtered);
    }, [value, filterType, rooms, setFilteredRooms]);

    return (
        <div className="search-bar">
            <input
                type="text"
                placeholder="Search PracticeHub..."
                value={value}
                onChange={handleInputChange}
            />
            <div className="filter-buttons">
                <button onClick={() => setFilterType("name")}
                        className={filterType === "name" ? "active" : ""}>Name</button>
                <button onClick={() => setFilterType("address")}
                        className={filterType === "address" ? "active" : ""}>Address</button>
                <button onClick={() => setFilterType("category")}
                        className={filterType === "category" ? "active" : ""}>Category</button>
                <button onClick={() => setFilterType("all")}
                        className={filterType === "all" ? "active" : ""}>All</button>
            </div>
        </div>
    );
};

export default SearchBar;