type Category = "SOLO_DUO_ROOM" | "BAND_ROOM" | "STUDIO_ROOM" | "ORCHESTER_HALL";

export function getCategoryDisplayName(category: Category): string {
    const categoryDisplayNames: Record<Category, string> = {
        SOLO_DUO_ROOM: "Solo/Duo Room",
        BAND_ROOM: "Band Room",
        STUDIO_ROOM: "Studio Room",
        ORCHESTER_HALL: "Orchestra Hall",
    };
    return categoryDisplayNames[category];
}
