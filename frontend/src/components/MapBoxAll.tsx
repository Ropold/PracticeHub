import { RoomModel } from "./model/RoomModel.ts";
import { useRef, useEffect, useState } from "react";
import axios from "axios";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";

type MapBoxAllProps = {
    favorites: string[];
    activeRooms: RoomModel[];
}

export default function MapBoxAll(props: Readonly<MapBoxAllProps>) {
    const mapRef = useRef<mapboxgl.Map | null>(null); // Referenz für die Karte
    const mapContainerRef = useRef<HTMLDivElement | null>(null); // Referenz für den Map Container
    const [geocodeError, setGeocodeError] = useState<string | null>(null); // Fehlerzustand für Geocoding
    const [mapboxConfig, setMapboxConfig] = useState<string | null>(null); // Zustand für das Mapbox-Token
    const [searchQuery, setSearchQuery] = useState<string>(""); // Zustand für die Suchabfrage

    // Funktion zum Abrufen des MapBox-Konfigurationstokens
    function fetchMapBoxConfig() {
        axios.get("/api/mapbox/72c81498-f6b2-4a8a-911c-cd217a65e0da")
            .then((response) => {
                const resp = response.data; // Holen des Tokens aus der Antwort
                setMapboxConfig(resp); // Token speichern
                mapboxgl.accessToken = resp; // Mapbox-Access-Token setzen
            })
            .catch((error) => {
                console.error("Error fetching MapBox configuration:", error);
                setGeocodeError("Failed to fetch MapBox configuration");
            });
    }

    // Funktion zum Geocodieren der Adresse
    const geocodeAddress = (address: string): Promise<[number, number] | null> => {
        if (!mapboxConfig) return Promise.resolve(null); // Warten auf das Mapbox-Token

        const geocodeUrl = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(address)}.json?access_token=${mapboxConfig}`;

        return fetch(geocodeUrl)
            .then((response) => response.json())
            .then((data) => {
                if (data.features && data.features.length > 0) {
                    return data.features[0].geometry.coordinates; // Gibt [longitude, latitude] zurück
                }
                return null; // Kein Ergebnis für die Adresse
            })
            .catch((error) => {
                console.error("Error geocoding address:", error);
                setGeocodeError("Error geocoding address.");
                return null;
            });
    };


    // Funktion zur Initialisierung der Karte und zum Hinzufügen der Marker
    useEffect(() => {
        fetchMapBoxConfig(); // API-Aufruf beim Laden der Komponente
    }, []);

    useEffect(() => {
        if (!mapboxConfig || !props.activeRooms.length) return; // Wenn kein Mapbox-Token oder keine Räume vorhanden sind, nichts tun

        // Karte initialisieren
        if (mapContainerRef.current) {
            // Sicherstellen, dass die Karte nur einmal initialisiert wird
            if (!mapRef.current) {
                mapRef.current = new mapboxgl.Map({
                    container: mapContainerRef.current,
                    style: "mapbox://styles/mapbox/streets-v11", // Stil der Karte
                    center: [6.960279, 50.937531], // Standard-Zentrum (Köln)
                    zoom: 10, // Zoom-Level
                });
            }

            // Marker für jedes Zimmer hinzufügen
            props.activeRooms.forEach((room) => {
                geocodeAddress(room.address).then((coordinates) => {
                    if (coordinates) {
                        const [longitude, latitude] = coordinates;
                        if (mapRef.current) {
                            new mapboxgl.Marker()
                                .setLngLat([longitude, latitude])
                                .addTo(mapRef.current);
                        }
                    } else {
                        setGeocodeError(`Address not found: ${room.address}`);
                    }
                });
            });

        }

        // Bereinigung der Karte, wenn das Component unmontiert wird oder die Räume geändert werden
        return () => {
            if (mapRef.current) {
                mapRef.current.remove();
            }
        };
    }, [props.activeRooms, mapboxConfig]); // Reagiere auf Änderungen von activeRooms und mapboxConfig

    // Funktion zum Suchen eines Ortes und die Karte darauf zu zentrieren
    const handleSearch = () => {
        geocodeAddress(searchQuery).then((coordinates) => {
            if (coordinates && mapRef.current) {
                const [longitude, latitude] = coordinates;
                // Karte auf die gefundenen Koordinaten zentrieren
                mapRef.current.setCenter([longitude, latitude]);
                mapRef.current.setZoom(10); // Optional: Zoom-Level anpassen
                // Optional: Marker für den gefundenen Ort hinzufügen
            } else {
                setGeocodeError("Address not found.");
            }
        });
    };

    return (
        <div>
            <h1>MapBoxAll</h1>
            {geocodeError && <div>{geocodeError}</div>} {/* Zeige Fehlernachricht an */}
            <div>
                {/* Suchfeld */}
                <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Search for a place..."
                />
                <button onClick={handleSearch}>Search</button>
            </div>

            <div id="map-container" ref={mapContainerRef} style={{ width: "100%", height: "400px" }} />
        </div>
    );
}
