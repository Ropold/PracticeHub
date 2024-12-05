

import axios from 'axios';


type HandleToggleWishlistType = (roomId: string, userId: string, favorites: string[]) => void;

const handleToggleWishlist: HandleToggleWishlistType = async (roomId, userId, favorites) => {
    // Prüfe, ob die Raum-ID bereits im Array der Favoriten enthalten ist
    const isFavorite = favorites.includes(roomId);

    if (isFavorite){
        axios.delete(`/api/practice-hub/favorites/${userId}/${roomId}`)
        .then((response) => {
            console.log(response.data);
        })
    } else{
        axios.post(`/api/practice-hub/favorites/${userId}/${roomId}`)
        .then((response) => {
            console.log(response.data);
        })
    }

};

export default handleToggleWishlist;
