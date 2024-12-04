// import axios from "axios";
// import {RoomModel} from "../components/model/RoomModel.ts";
//
// type HandleUpdateRoomType = (updatedRoom: RoomModel) => void;
//
// export default function handleToggleWishlist(room: RoomModel, handleUpdateRoom: HandleUpdateRoomType): void {
//     const updatedStatus = room.wishlistStatus === "ON_WISHLIST" ? "NOT_ON_WISHLIST" : "ON_WISHLIST";
//
//     const formData = new FormData();
//     const updatedRoomData = { ...room, wishlistStatus: updatedStatus };
//
//     formData.append(
//         "roomModelDto",
//         new Blob([JSON.stringify(updatedRoomData)], { type: "application/json" })
//     );
//
//     axios
//         .put(`/api/practice-hub/${room.id}`, formData, {
//             headers: {
//                 "Content-Type": "multipart/form-data",
//             },
//         })
//         .then((response) => {
//             handleUpdateRoom(response.data);
//         })
//         .catch((error) => console.error("Error updating wishlist status:", error));
// }

import axios from 'axios';

// Diese Funktion übernimmt das Hinzufügen oder Entfernen der Raum-ID von den Favoriten des Benutzers
type HandleToggleWishlistType = (roomId: string, userId: string, favorites: string[]) => void;

const handleToggleWishlist: HandleToggleWishlistType = async (roomId, userId, favorites) => {
    // Prüfe, ob die Raum-ID bereits im Array der Favoriten enthalten ist
    const isFavorite = favorites.includes(roomId);

    if (isFavorite){
        axios.delete(`/api/practice-hub/favorites/${userId}/${roomId}`)
        .then((response) => {
            console.log(response.data);
        })
    } else{
        axios.post(`/api/practice-hub/favorites/${userId}/${roomId}`)
        .then((response) => {
            console.log(response.data);
        })
    }

    //
    // // Das Array der Favoriten entsprechend aktualisieren
    // const updatedFavorites = isFavorite
    //     ? favorites.filter((id) => id !== roomId)  // Wenn der Raum bereits Favorit ist, entfernen
    //     : [...favorites, roomId];                   // Wenn nicht, hinzufügt
    //
    // try {
    //     // PUT-Anfrage, um die Liste der Favoriten auf dem Server zu aktualisieren
    //     await axios.put(`/api/practice-hub/favorites/${userId}`, { favorites: updatedFavorites });
    //
    //     // Lokalen Zustand (Favorites) mit dem neuen Array aktualisieren
    //     setFavorites(updatedFavorites);
    // } catch (error) {
    //     console.error('Error updating favorites:', error);
    // }
};

export default handleToggleWishlist;
