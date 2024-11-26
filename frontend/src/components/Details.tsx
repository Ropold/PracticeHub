import "./styles/Details.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

const defaultRoom: RoomModel = {
    id: "",
    name: "",
    address: "",
    category: "",
    description: "",
    wishlistStatus: "NOT_ON_WISHLIST",
};

export default function Details() {
    const [room, setRoom] = useState<RoomModel>(defaultRoom);
    const [editRoomId, setEditRoomId] = useState<string | null>(null);
    const [editData, setEditData] = useState<RoomModel>(defaultRoom);

    const { id } = useParams<{ id: string }>();

    const fetchRoomDetails = () => {
        if (!id) return;
        axios
            .get(`/api/practice-hub/${id}`)
            .then((response) => setRoom(response.data))
            .catch((error) => console.error("Error fetching room details", error));
    };

    useEffect(() => {
        fetchRoomDetails();
    }, [id]);

    const handleEditToggle = () => {
        if (room) {
            setEditRoomId(room.id);
            setEditData({
                id: room.id,
                name: room.name,
                address: room.address,
                category: room.category,
                description: room.description,
                wishlistStatus: room.wishlistStatus,
            });
        }
    };

    const handleEditChange = (field: string, value: string) => {
        setEditData((prevData) => ({ ...prevData, [field]: value }));
    };

    const handleCancelEdit = () => setEditRoomId(null);

    const handleSaveEdit = () => {
        if (!editRoomId) return;
        axios
            .put(`/api/practice-hub/${editRoomId}`, editData)
            .then((response) => {
                setRoom(response.data);
                setEditRoomId(null);
            })
            .catch((error) => console.error("Error saving room edits:", error));
    }


    return (
        <div className="details-container">
            {editRoomId ? (
                <div className="edit-form">
                    <h2>Edit Room Details</h2>
                    <label>Name: <input className="input-small" type="text" value={editData.name}
                                        onChange={(e) => handleEditChange("name", e.target.value)} /></label>
                    <label>Address: <input className="input-small" type="text" value={editData.address}
                                           onChange={(e) => handleEditChange("address", e.target.value)} /></label>
                    <label>Category: <input className="input-small" type="text" value={editData.category}
                                            onChange={(e) => handleEditChange("category", e.target.value)} /></label>
                    <label>Description: <textarea className="textarea-large" value={editData.description}
                                                  onChange={(e) => handleEditChange("description", e.target.value)} /></label>
                    <div className="button-group">
                        <button onClick={handleSaveEdit}>Save</button>
                        <button onClick={handleCancelEdit}>Cancel</button>
                    </div>
                </div>
            ) : (
                <div className="room-details">
                    <h2>{room.name}</h2>
                    <p><strong>Address: </strong> {room.address}</p>
                    <p><strong>Category: </strong> {room.category}</p>
                    <p><strong>Description: </strong> {room.description}</p>
                    <div className="button-group">
                        <button onClick={handleEditToggle}>Edit</button>
                        <button>Delete</button>
                    </div>
                </div>
            )}
        </div>
    );
}
