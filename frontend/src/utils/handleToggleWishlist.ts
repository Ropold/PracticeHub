import axios from "axios";
import {RoomModel} from "../components/model/RoomModel.ts";


type HandleUpdateRoomType = (updatedRoom: RoomModel) => void;


export default function handleToggleWishlist(room:RoomModel, handleUpdateRoom:HandleUpdateRoomType):void {
    const updatedStatus =
        room.wishlistStatus === "ON_WISHLIST" ? "NOT_ON_WISHLIST" : "ON_WISHLIST";

    const updatedRoom = { ...room, wishlistStatus: updatedStatus };

    axios
        .put(`/api/practice-hub/${room.id}`, updatedRoom)
        .then((response) => {
            handleUpdateRoom(response.data); // Update room state with new data
        })
        .catch((error) => console.error("Error updating wishlist status:", error));
};