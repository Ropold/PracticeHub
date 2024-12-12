import {RoomModel} from "./model/RoomModel.ts";
import RoomCard from "./RoomCard.tsx";
import {useEffect, useState} from "react";
import axios from "axios";
import { Category } from './model/Category.ts';
import "./styles/RoomCard.css";

type MyRoomsProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    rooms: RoomModel[];
    userDetails: any;
    setRooms: React.Dispatch<React.SetStateAction<RoomModel[]>>;
}

export default function MyRooms(props: Readonly<MyRoomsProps>) {

    const [userRooms, setUserRooms] = useState<RoomModel[]>([]); // Zustand für gefilterte Räume des Benutzers
    const [isEditing, setIsEditing] = useState<boolean>(false);  // Ob der Raum bearbeitet wird
    const [editData, setEditData] = useState<RoomModel | null>(null);  // Daten des Raums, der bearbeitet wird
    const [image, setImage] = useState<File | null>(null);// Für das Bild
    const [category, setCategory] = useState<Category>("SOLO_DUO_ROOM");  // Kategorie des Raums
    const [showPopup, setShowPopup] = useState(false);
    const [roomToDelete, setRoomToDelete] = useState<string | null>(null);


    // Filtere die Räume des aktuellen Benutzers und speichere sie im Zustand
    useEffect(() => {
        setUserRooms(props.rooms.filter((room) => room.appUserGithubId === props.user));
    }, [props.rooms, props.user, isEditing]); // Dieser Effekt wird jedes Mal ausgeführt, wenn rooms oder user geändert werden

    const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setCategory(e.target.value as Category);  // Setze den Wert und zwinge TypeScript, ihn als Category zu behandeln
    };

    // Start editing the room
    const handleEditToggle = (roomId: string) => {
        const roomToEdit = props.rooms.find((room) => room.id === roomId);
        if (roomToEdit) {
            setEditData(roomToEdit);
            setIsEditing(true);

            // Wenn der Raum ein Bild hat, setze es in den State
            if (roomToEdit.imageUrl) {
                fetch(roomToEdit.imageUrl)
                    .then((response) => response.blob())
                    .then((blob) => {
                        const file = new File([blob], "current-image.jpg", {type: blob.type});
                        setImage(file);
                    })
                    .catch((error) => console.error("Error loading current image:", error));
            } else {
                setImage(null); // Kein Bild vorhanden
            }
        }
    };

    // Handle form submission to save the changes
    const handleSaveEdit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!editData) return;

        const data = new FormData();
        if (image) {
            data.append("image", image);
        }

        const updatedRoomData = {
            ...editData,
            imageUrl: "",  // You may want to update this after uploading the image
        };

        data.append("roomModelDto", new Blob([JSON.stringify(updatedRoomData)], {type: "application/json"}));

        axios
            .put(`/api/practice-hub/${editData.id}`, data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            })
            .then((response) => {
                console.log("Antwort vom Server:", response.data);
                props.setRooms((prevRooms) =>
                    prevRooms.map((room) =>
                        room.id === editData.id ? {...room, ...response.data} : room
                    )
                );
                setIsEditing(false);  // Exit edit mode
            })
            .catch((error) => {
                console.error("Error saving room edits:", error);
                alert("An unexpected error occurred. Please try again.");
            });
    };

    const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setImage(e.target.files[0]);
        }
    };


    const handleConfirmDelete = () => {
        if (roomToDelete) {
            axios
                .delete(`/api/practice-hub/${roomToDelete}`)
                .then(() => {
                    props.setRooms((prevRooms) => prevRooms.filter((room) => room.id !== roomToDelete));
                })
                .catch((error) => {
                    console.error("Error deleting room:", error);
                    alert("An error occurred while deleting the room.");
                });
        }
        setShowPopup(false);
        setRoomToDelete(null);
    };

    const handleToggleActiveStatus = (roomId: string) => {
        axios
            .put(`/api/practice-hub/${roomId}/toggle-active`)
            .then(() => {
                // Sobald die Antwort kommt, aktualisiere den Status der Räume
                props.setRooms((prevRooms) =>
                    prevRooms.map((room) =>
                        room.id === roomId ? {...room, isActive: !room.isActive} : room
                    )
                );
            })
            .catch((error) => {
                console.error("Error during Toggle Offline/Active", error);
                alert("An Error while changing the status of Active/Offline.");
            });
    };

    const handleDeleteClick = (id: string) => {
        setRoomToDelete(id);
        setShowPopup(true);
    };

    const handleCancel = () => {
        setShowPopup(false);
        setRoomToDelete(null);
    };

    return (
        <div>
            {isEditing ? (
                <div className="edit-form">
                    <h2>Edit Room</h2>
                    <form onSubmit={handleSaveEdit}>
                        <label>
                            Title:
                            <input
                                className="input-small"
                                type="text"
                                value={editData?.name || ""}
                                onChange={(e) => setEditData({...editData!, name: e.target.value})}
                            />
                        </label>

                        <label>
                            Address:
                            <input
                                className="input-small"
                                type="text"
                                value={editData?.address || ""}
                                onChange={(e) => setEditData({...editData!, address: e.target.value})}
                            />
                        </label>

                        <label>
                            Category:
                            <select
                                className="input-small"
                                value={category}
                                onChange={handleCategoryChange}
                            >
                                <option value="SOLO_DUO_ROOM">Solo/Duo Room</option>
                                <option value="BAND_ROOM">Band Room</option>
                                <option value="STUDIO_ROOM">Studio Room</option>
                                <option value="ORCHESTER_HALL">Orchestra Hall</option>
                            </select>
                        </label>
                        <label>
                            Description:
                            <textarea
                                className="textarea-large"
                                value={editData?.description || ""}
                                onChange={(e) => setEditData({...editData!, description: e.target.value})}
                            />
                        </label>
                        <label>
                            Visibility:
                            <select
                                className="input-small"
                                value={editData?.isActive ? "true" : "false"}
                                onChange={(e) => setEditData({...editData!, isActive: e.target.value === "true"})}
                            >
                                <option value="true">Active</option>
                                <option value="false">Inactive</option>
                            </select>
                        </label>

                        <label>
                            Image:
                            <input type="file" onChange={onFileChange}/>
                            {image && <img src={URL.createObjectURL(image)} className="room-card-image"/>}
                        </label>

                        <div className="button-group">
                            <button type="submit">Save Changes</button>
                            <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    </form>
                </div>
            ) : (
                <div className="room-card-container">
                    {userRooms.length > 0 ? (
                        userRooms.map((room) => (
                            <div key={room.id}>
                                <RoomCard
                                    room={room}
                                    user={props.user}
                                    favorites={props.favorites}
                                    toggleFavorite={props.toggleFavorite}
                                />
                                <div className="button-group">
                                    <button
                                        id={room.isActive ? "active-button" : "inactive-button"}
                                        onClick={() => handleToggleActiveStatus(room.id)} // Event-Handler für das toggeln
                                    >
                                        {room.isActive ? "Active" : "Offline"}
                                    </button>
                                    <button onClick={() => handleEditToggle(room.id)}>Edit</button>
                                    <button id="button-delete" onClick={() => handleDeleteClick(room.id)}>Delete
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No rooms found for this user.</p>
                    )}
                </div>
            )}

            {/* Popup für Löschbestätigung */}
            {showPopup && (
                <div className="popup-overlay">
                    <div className="popup-content">
                        <h3>Confirm Deletion</h3>
                        <p>Are you sure you want to delete this room?</p>
                        <div className="popup-actions">
                            <button onClick={handleConfirmDelete} className="popup-confirm">
                                Yes, Delete
                            </button>
                            <button onClick={handleCancel} className="popup-cancel">
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}