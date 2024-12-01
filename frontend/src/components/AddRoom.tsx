import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";

export default function AddRoom() {
    const [name, setName] = useState<string>("");
    const [address, setAddress] = useState<string>("");
    const [category, setCategory] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const [status, setStatus] = useState<"ON_WISHLIST" | "NOT_ON_WISHLIST">("NOT_ON_WISHLIST");
    const [image, setImage] = useState<File | null>(null);

    const navigate = useNavigate();

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const data = new FormData();

        if (image) {
            console.log("Gesendetes Bild:", image);
            data.append("image", image);
        }

        let imageUrl = "";
        const roomData = { name, address, category, description, wishlistStatus: status, imageUrl };

        data.append("json", JSON.stringify(roomData));

        axios
            .post("/api/practice-hub", data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                withCredentials: true, // Cookies werden mitgesendet, falls nötig
            })
            .then((response) => {
                console.log("Antwort vom Server:", response.data);
                navigate(`/room/${response.data.id}`); // Weiterleitung auf den neu erstellten Raum
            })
            .catch((error) => {
                // Wenn ein Fehler auftritt, prüfen wir, ob es sich um einen Validierungsfehler handelt
                if (error.response && error.response.status === 400 && error.response.data) {
                    const errorMessages = error.response.data;
                    let alertMessage = "Please fix the following errors:\n";

                    // Fehler durchlaufen und in die Alert-Nachricht einfügen
                    Object.keys(errorMessages).forEach((field) => {
                        alertMessage += `${field}: ${errorMessages[field]}\n`;
                    });

                    // Fehler als Alert anzeigen
                    alert(alertMessage);
                } else {
                    console.error("Error adding room:", error);
                    alert("An unexpected error occurred. Please try again.");
                }
            });
    };



    const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files){
            setImage(e.target.files[0]);
        }
    }

    return (
        <div className="details-container">
            <div className="edit-form">
                <h2>Add New Room</h2>
                <form onSubmit={handleSubmit}>
                    <label>Name: <input className="input-small" type="text" value={name}
                            onChange={(e) => setName(e.target.value)}/></label>
                    <label>Address: <input className="input-small" type="text" value={address}
                            onChange={(e) => setAddress(e.target.value)}/></label>
                    <label>Category: <input className="input-small" type="text" value={category}
                            onChange={(e) => setCategory(e.target.value)}/></label>
                    <label>Description: <textarea className="textarea-large" value={description}
                            onChange={(e) => setDescription(e.target.value)}/></label>
                    <label>Status: <select value={status}
                            onChange={(e) => setStatus(e.target.value as "ON_WISHLIST" | "NOT_ON_WISHLIST")}>
                            <option value="ON_WISHLIST">On Wishlist</option>
                            <option value="NOT_ON_WISHLIST">Not on Wishlist</option></select></label>
                    <input type={"file"} onChange={onFileChange}/>
                    {image && <img src={URL.createObjectURL(image)} className={"room-card-image"}/>}
                    <div className="button-group"><button type="submit">Add Room</button></div>
                </form>
            </div>
        </div>
    );
}