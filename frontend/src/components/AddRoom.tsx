import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";

type AddRoomProps = {
    user: string;
}

export default function AddRoom({ user }: AddRoomProps) {
    const [name, setName] = useState<string>("");
    const [address, setAddress] = useState<string>("");
    const [category, setCategory] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const [image, setImage] = useState<File | null>(null);

    const navigate = useNavigate();

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const data = new FormData();

        if (image) {
            //console.log("Gesendetes Bild:", image);
            data.append("image", image);
        }

        const roomData = { name, address, category, description, appUserGithubId: user, imageUrl: "" };

        data.append("roomModelDto", new Blob([JSON.stringify(roomData)], { type: "application/json" }));

        axios
            .post("/api/practice-hub", data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                }
            })
            .then((response) => {
                //console.log("Antwort vom Server:", response.data);
                navigate(`/room/${response.data.id}`);
            })
            .catch((error) => {
                if (error.response && error.response.status === 400 && error.response.data) {
                    const errorMessages = error.response.data;
                    let alertMessage = "Please fix the following errors:\n";

                    Object.keys(errorMessages).forEach((field) => {
                        alertMessage += `${field}: ${errorMessages[field]}\n`;
                    });

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
                    <label>Status: <select>
                            <option>On Wishlist</option>
                            <option>Not on Wishlist</option></select></label>
                    <input type={"file"} onChange={onFileChange}/>
                    {image && <img src={URL.createObjectURL(image)} className={"room-card-image"}/>}
                    <div className="button-group"><button type="submit">Add Room</button></div>
                </form>
            </div>
        </div>
    );
}