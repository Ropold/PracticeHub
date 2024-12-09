import {Category} from "./Category.ts";

export type RoomModel = {
    id: string;
    name: string;
    address: string;
    category: Category;
    description: string;
    appUserGithubId: string;
    appUserUsername: string;
    appUserAvatarUrl: string;
    appUserGithubUrl: string;
    isActive: boolean;
    imageUrl: string;
}
