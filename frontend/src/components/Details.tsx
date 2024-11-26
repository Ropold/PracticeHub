import "./styles/Details.css";
import { RoomModel } from "./model/RoomModel.ts";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";



export default function Details() {
    const [room, setRoom] = useState<RoomModel>({
        id: "",
        name: "",
        address: "",
        category: "",
        description: "",
        wishlistStatus: "NOT_ON_WISHLIST",
    });
    const [editRoomId, setEditRoomId] = useState<string | null>(null);
    const [editData, setEditData] = useState<RoomModel>({
        id: "",
        name: "",
        address: "",
        category: "",
        description: "",
        wishlistStatus: "NOT_ON_WISHLIST",
    });

    const { id } = useParams<{ id: string }>();


    const fetchRoomDetails = () => {
        if (!id) return;
        axios
            .get(`/api/practice-hub/${id}`)
            .then((response) => setRoom(response.data))
            .catch((error) => console.error("Error fetching restaurant details", error));
    };

    useEffect(() => {
        fetchRoomDetails();
    }, [id]);

    const handleEditToggle = () => {
        if (room) {
            setEditRoomId(room.id);
            setEditData({
                id: room.id, //mhh??????
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
        <div>
            <h2>Details</h2>

            <div>
                <h2>{room.name}</h2>
                <p>{room.address}</p>
                <p>{room.category}</p>
                <p>{room.description}</p>
                <button>Edit</button>
                <button>Delete</button>
            </div>

        </div>
    );
}
