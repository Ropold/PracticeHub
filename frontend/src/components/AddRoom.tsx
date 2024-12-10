import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {RoomModel} from "./model/RoomModel.ts";

type AddRoomProps = {
    user: string;
    handleSubmit: (room: RoomModel) => void;
    userDetails: any;
}

export default function AddRoom(props: Readonly<AddRoomProps>) {

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
            data.append("image", image);
        }

        const roomData = {
            name,
            address,
            category: category as any, // Wir casten den String zu Category
            description,
            appUserGithubId: props.user,
            appUserUsername: props.userDetails.login,
            appUserAvatarUrl: props.userDetails.avatar_url,
            appUserGithubUrl: props.userDetails.html_url,
            isActive: true,
            imageUrl: "",
        };

        data.append("roomModelDto", new Blob([JSON.stringify(roomData)], {type: "application/json"}));

        console.log("roomData:", roomData);

        axios
            .post("/api/practice-hub", data, {
                headers: {
                    "Content-Type": "multipart/form-data",
                }
            })
            .then((response) => {
                console.log("Antwort vom Server:", response.data);
                navigate(`/room/${response.data.id}`);
                props.handleSubmit(response.data);
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
        if (e.target.files) {
            setImage(e.target.files[0]);
        }
    }

    return (
            <div className="edit-form">
                <h2>Add New Room</h2>
                <form onSubmit={handleSubmit}>
                    <label>Title: <input className="input-small" type="text" value={name}
                                         onChange={(e) => setName(e.target.value)}/></label>
                    <label>Address: <input className="input-small" type="text" value={address}
                                           onChange={(e) => setAddress(e.target.value)}/></label>
                    <label>Category: <select className="input-small" value={category}
                                             onChange={(e) => setCategory(e.target.value)} required>
                        <option value="" disabled>*Choose a category*</option>
                        <option value="SOLO_DUO_ROOM">Solo/Duo Room</option>
                        <option value="BAND_ROOM">Band Room</option>
                        <option value="STUDIO_ROOM">Studio Room</option>
                        <option value="ORCHESTER_HALL">Orchestra Hall</option>
                    </select>
                    </label>
                    <label>Description: <textarea className="textarea-large" value={description}
                                                  onChange={(e) => setDescription(e.target.value)}/></label>
                    <input type={"file"} onChange={onFileChange}/>
                    {image && <img src={URL.createObjectURL(image)} className={"room-card-image"}/>}
                    <div className="button-group">
                        <button type="submit">Add Room</button>
                    </div>
                </form>
            </div>
    );
}
