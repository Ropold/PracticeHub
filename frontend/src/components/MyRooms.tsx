import {RoomModel} from "./model/RoomModel.ts";
import RoomCard from "./RoomCard.tsx";
import {useEffect, useState} from "react";
import axios from "axios";
import { Category } from './model/Category.ts';

type MyRoomsProps = {
    user: string;
    favorites: string[];
    toggleFavorite: (roomId: string) => void;
    rooms: RoomModel[];
}

export default function MyRooms(props: Readonly<MyRoomsProps>) {

    const [rooms, setRooms] = useState<RoomModel[]>(props.rooms); // Zustand für alle Räume
    const [userRooms, setUserRooms] = useState<RoomModel[]>([]); // Zustand für gefilterte Räume des Benutzers
    const [isEditing, setIsEditing] = useState<boolean>(false);  // Ob der Raum bearbeitet wird
    const [editData, setEditData] = useState<RoomModel | null>(null);  // Daten des Raums, der bearbeitet wird
    const [image, setImage] = useState<File | null>(null);// Für das Bild
    const [category, setCategory] = useState<Category>("SOLO_DUO_ROOM");  // Kategorie des Raums


    // Filtere die Räume des aktuellen Benutzers und speichere sie im Zustand
    useEffect(() => {
        setUserRooms(rooms.filter((room) => room.appUserGithubId === props.user));
    }, [rooms, props.user,isEditing]); // Dieser Effekt wird jedes Mal ausgeführt, wenn rooms oder user geändert werden

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
                        const file = new File([blob], "current-image.jpg", { type: blob.type });
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

        data.append("roomModelDto", new Blob([JSON.stringify(updatedRoomData)], { type: "application/json" }));

        axios
            .put(`/api/practice-hub/${editData.id}`, data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            })
            .then((response) => {
                console.log("Antwort vom Server:", response.data);
                setRooms((prevRooms) =>
                    prevRooms.map((room) =>
                        room.id === editData.id ? { ...room, ...response.data } : room
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


    const handleDelete = (id: string) => {
        const isConfirmed = window.confirm("Are you sure you want to delete this room?");

        if (isConfirmed) {
            axios
                .delete(`/api/practice-hub/${id}`)
                .then(() => {
                    setRooms((prevRooms) => prevRooms.filter((room) => room.id !== id));
                })
                .catch((error) => {
                    console.error("Error deleting room:", error);
                    alert("An error occurred while deleting the room.");
                });
        }
    };

    return (
        <div>
            <h3>My Rooms of User {props.user}</h3>

            {isEditing ? (
                <div className="details-container">
                    <div className="edit-form">
                        <h2>Edit Room</h2>
                        <form onSubmit={handleSaveEdit}>
                            <label>Title:
                                <input
                                    className="input-small"
                                    type="text"
                                    value={editData?.name || ""}
                                    onChange={(e) => setEditData({ ...editData!, name: e.target.value })}
                                />
                            </label>
                            <label>Address:
                                <input
                                    className="input-small"
                                    type="text"
                                    value={editData?.address || ""}
                                    onChange={(e) => setEditData({ ...editData!, address: e.target.value })}
                                />
                            </label>
                            <label>Category:
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
                            <label>Description:
                                <textarea
                                    className="textarea-large"
                                    value={editData?.description || ""}
                                    onChange={(e) => setEditData({...editData!, description: e.target.value })}
                                />
                            </label>
                            <input type="file" onChange={onFileChange} />
                            {image && <img src={URL.createObjectURL(image)} className="room-card-image" />}
                            <div className="button-group">
                                <button type="submit">Save Changes</button>
                                <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            ) : (
                // Show the room cards if not in edit mode
                <div className="my-rooms-list">
                    {userRooms.length > 0 ? (
                        userRooms.map((room) => (
                            <div key={room.id} className="room-card-container">
                                <RoomCard
                                    room={room}
                                    user={props.user}
                                    favorites={props.favorites}
                                    toggleFavorite={props.toggleFavorite}
                                />
                                <div className="button-group">
                                    <button onClick={() => handleEditToggle(room.id)}>Edit</button>
                                    <button id="button-delete" onClick={() => handleDelete(room.id)}>Delete</button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No rooms found for this user.</p>
                    )}
                </div>
            )}
        </div>
    );
}
