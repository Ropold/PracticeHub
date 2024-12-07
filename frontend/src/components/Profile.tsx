import "./styles/Profile.css"

type UserDetailsProps={
    userDetails: any;
}

export default function Profile(props: Readonly<UserDetailsProps>) {
    const userDetails = props.userDetails;

    return (
        <div className="profile-container">
            <h2>GitHub Profile</h2>
            {userDetails ? (
                <div>
                    <p>Username: {userDetails.login}</p>
                    <p>Name: {userDetails.name || "No name provided"}</p>
                    <p>Location: {userDetails.location || "No location provided"}</p>
                    {userDetails.bio && <p>Bio: {userDetails.bio}</p>}
                    <p>Followers: {userDetails.followers}</p>
                    <p>Following: {userDetails.following}</p>
                    <p>Public Repositories: {userDetails.public_repos}</p>
                    <p>GitHub Profile: <a href={userDetails.html_url} target="_blank" rel="noopener noreferrer">Visit Profile</a></p>
                    <img src={userDetails.avatar_url} alt={`${userDetails.login}'s avatar`} />
                    <p>Account Created: {new Date(userDetails.created_at).toLocaleDateString()}</p>
                    <p>Last Updated: {new Date(userDetails.updated_at).toLocaleDateString()}</p>
                </div>
            ) : (
                <p className="loading">Loading...</p>
            )}
        </div>
    );
}