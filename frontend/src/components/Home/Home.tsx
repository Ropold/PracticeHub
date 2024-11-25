import "../styles/Home.css";
import { RoomModel } from "../model/RoomModel.ts";
import { useEffect, useState } from "react";
import SearchBar from "./SearchBar.tsx";
import RoomCard from "../RoomCard.tsx";

type HomeProps = {
    rooms: RoomModel[];
};

export default function Home(props: Readonly<HomeProps>) {
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filterType, setFilterType] = useState<"name" | "address" | "category" | "all">("name");
    const [filteredRooms, setFilteredRooms] = useState<RoomModel[]>(props.rooms);

    useEffect(() => {
        const filtered: RoomModel[] = props.rooms.filter((room) => {
            const lowerQuery = searchQuery.toLowerCase();

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
    }, [searchQuery, filterType, props.rooms]);

    return (
        <>
            <h1>PracticeHub</h1>
            <h2>Home</h2>
            <SearchBar onSearch={(query) => setSearchQuery(query)} />
            <div>
                <button onClick={() => setFilterType("name")}>Name</button>
                <button onClick={() => setFilterType("address")}>Address</button>
                <button onClick={() => setFilterType("category")}>Category</button>
                <button onClick={() => setFilterType("all")}>All</button>
            </div>

            {filteredRooms.map((r) => (
                <RoomCard
                key={r.id}
                room={r}
                />
            ))}
        </>
    );
}

