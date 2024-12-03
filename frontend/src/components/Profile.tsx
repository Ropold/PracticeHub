import { useEffect, useState } from "react";
import axios from "axios";
import "./styles/Profile.css"

export default function Profile() {
    const [user, setUser] = useState<any>(null);

    function getUserDetails() {
        axios.get("/api/users/me/details")
            .then((response) => {
                //console.log("User details:", response.data);
                setUser(response.data);
            })
            .catch((error) => {
                console.error(error);
                setUser(null);
            });
    }

    useEffect(() => {
        getUserDetails();
    }, []);

    return (
        <div className="profile-container">
            <h2>GitHub Profile</h2>
            {user ? (
                <div>
                    <p>Username: {user.login}</p>
                    <p>Name: {user.name || "No name provided"}</p>
                    <p>Location: {user.location || "No location provided"}</p>
                    {user.bio && <p>Bio: {user.bio}</p>}
                    <p>Followers: {user.followers}</p>
                    <p>Following: {user.following}</p>
                    <p>Public Repositories: {user.public_repos}</p>
                    <p>GitHub Profile: <a href={user.html_url} target="_blank" rel="noopener noreferrer">Visit Profile</a></p>
                    <img src={user.avatar_url} alt={`${user.login}'s avatar`} />
                    <p>Account Created: {new Date(user.created_at).toLocaleDateString()}</p>
                    <p>Last Updated: {new Date(user.updated_at).toLocaleDateString()}</p>
                </div>
            ) : (
                <p className="loading">Loading...</p>
            )}
        </div>
    );
}