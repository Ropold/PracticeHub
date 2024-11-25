import "../styles/SearchBar.css";
import {ChangeEvent} from "react";

type SearchBarProps = {
    onSearch: (query: string) => void;
}

export default function SearchBar({ onSearch }: Readonly<SearchBarProps>) {

    const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
        onSearch(event.target.value);
    }

    return(
        <>

        <h2>SearchBar</h2>
            <div>
                <input
                    type="text"
                    placeholder="Search PracticeHub..."
                    onChange={handleSearchChange}
                />
            </div>
        </>
    )
}