import "./styles/Details.css";
import "./styles/RoomCard.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useEffect, useState } from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import handleToggleWishlist from "../utils/handleToggleWishlist.ts";
import MapBox from "./MapBox.tsx";

type DetailsProps = {
    favorites: string[];
    user: string;
    toggleFavorite: (roomId: string) => void;
}

const defaultRoom: RoomModel = {
    id: "",
    name: "Loading....",
    address: "",
    category: "",
    description: "",
    appUserGitbubId: "",
    imageUrl: "",
};

export default function Details(props: Readonly<DetailsProps>) {
    const [room, setRoom] = useState<RoomModel>(defaultRoom);
    const [editRoomId, setEditRoomId] = useState<string | null>(null);
    const [editData, setEditData] = useState<RoomModel>(defaultRoom);
    const [image, setImage] = useState<File | null>(null);

    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

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
                appUserGitbubId: room.appUserGitbubId,
                imageUrl: room.imageUrl,
            });
        }
    };


    const handleEditChange = (field: string, value: string) => {
        setEditData((prevData) => ({ ...prevData, [field]: value }));
    };

    const handleCancelEdit = () => setEditRoomId(null);

    const handleSaveEdit = () => {
        if (!editRoomId) return;

        const data = new FormData();

        if (image) {
            data.append("image", image);
        }

        const updatedRoomData = {
            ...editData,
            wishlistStatus: editData.wishlistStatus,
            imageUrl: ""
        };

        data.append("roomModelDto", new Blob([JSON.stringify(updatedRoomData)], { type: "application/json" }));

        axios
            .put(`/api/practice-hub/${editRoomId}`, data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                }
            })
            .then((response) => {
                setRoom(response.data);
                setEditRoomId(null); // Bearbeiten beenden
            })
            .catch((error) => {
                console.error("Error saving room edits:", error);
            });
    };

    const handleDelete = (id: string) => {
        const isConfirmed = window.confirm("Are you sure you want to delete this room?");

        if (isConfirmed) {
            axios
                .delete(`/api/practice-hub/${id}`)
                .then(() => {
                    setRoom(defaultRoom);
                    navigate("/");
                })
                .catch((error) => {
                    console.error("Error deleting room:", error);
                });
        }
    };

    const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setImage(e.target.files[0]);
        }
    };

    return (
        <div className="details-container">
            {editRoomId ? (
                <div className="edit-form">
                    <h2>Edit Room Details</h2>
                    <label>Name: <input className="input-small" type="text" value={editData.name}
                                        onChange={(e) => handleEditChange("name", e.target.value)}/></label>
                    <label>Address: <input className="input-small" type="text" value={editData.address}
                                           onChange={(e) => handleEditChange("address", e.target.value)}/></label>
                    <label>Category: <input className="input-small" type="text" value={editData.category}
                                            onChange={(e) => handleEditChange("category", e.target.value)}/></label>
                    <label>Description: <textarea className="textarea-large" value={editData.description}
                                                  onChange={(e) => handleEditChange("description", e.target.value)}/></label>
                    <label>Image:
                        <input type="file" onChange={onFileChange}/>
                        {image && <img src={URL.createObjectURL(image)} className={"room-card-image"} alt="Preview"/>}
                        {!image && editData.imageUrl &&
                            <img src={editData.imageUrl} className={"room-card-image"} alt="Current"/>}
                    </label>
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
                    <p><strong>Added by Github-User: </strong> {room.appUserGitbubId}</p>
                    {room.imageUrl ? (
                        <img
                            src={room.imageUrl}
                            alt={room.name}
                            className="room-card-image"
                        />
                    ) : null}
                    {props.user !== "anonymousUser" && (
                        <div>
                            <div className="button-group">
                                <button onClick={()=>handleToggleWishlist(room, setRoom)}
                                        className={room.wishlistStatus === "ON_WISHLIST" ? "wishlist-on" : "wishlist-off"}
                                >♥
                                </button>
                                <button onClick={handleEditToggle}>Edit</button>
                                <button id="button-delete" onClick={() => handleDelete(room.id)}>Delete</button>
                            </div>
                        </div>
                    )}
                    <MapBox address={room.address}/>
                </div>
            )}
        </div>
    );
}