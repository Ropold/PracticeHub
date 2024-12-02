import axios from "axios";
import {RoomModel} from "../components/model/RoomModel.ts";


type HandleUpdateRoomType = (updatedRoom: RoomModel) => void;


export default function handleToggleWishlist(room: RoomModel, handleUpdateRoom: HandleUpdateRoomType): void {
    const updatedStatus = room.wishlistStatus === "ON_WISHLIST" ? "NOT_ON_WISHLIST" : "ON_WISHLIST";

    const formData = new FormData();
    const updatedRoomData = { ...room, wishlistStatus: updatedStatus };

    formData.append(
        "roomModelDto",
        new Blob([JSON.stringify(updatedRoomData)], { type: "application/json" })
    );

    axios
        .put(`/api/practice-hub/${room.id}`, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        })
        .then((response) => {
            handleUpdateRoom(response.data);
        })
        .catch((error) => console.error("Error updating wishlist status:", error));
}
