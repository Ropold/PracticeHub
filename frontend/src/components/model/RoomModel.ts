import {WishlistStatus} from "./WishlistStatus.ts";

export type RoomModel = {
    id: string;
    name: string;
    address: string;
    category: string;
    description: string;
    wishlistStatus: WishlistStatus;
}
