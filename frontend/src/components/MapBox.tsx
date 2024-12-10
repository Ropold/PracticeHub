import { useRef, useEffect, useState } from "react";
import axios from "axios";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css"; // Mapbox CSS importieren

type MapBoxProps = {
    address: string;
};

export default function MapBox(props: Readonly<MapBoxProps>) {
    const mapRef = useRef<mapboxgl.Map | null>(null); // Referenz für die Karte
    const mapContainerRef = useRef<HTMLDivElement | null>(null); // Referenz für den Map Container
    const [geocodeError, setGeocodeError] = useState<string | null>(null); // Fehlerzustand für Geocoding
    const [mapboxConfig, setMapboxConfig] = useState<string | null>(null); // Zustand für das Mapbox-Token

    // Funktion, um das Mapbox-Konfigurationstoken vom Backend zu holen
    function fetchMapBoxConfig() {
        axios.get("/api/mapbox/72c81498-f6b2-4a8a-911c-cd217a65e0da")
            .then((response) => {
                const resp = response.data; // Hier nehmen wir das Token aus der Antwort
                setMapboxConfig(resp); // Token speichern
                mapboxgl.accessToken = resp; // Setze das Access-Token für Mapbox
            })
            .catch((error) => {
                console.error("Error fetching MapBox configuration:", error);
                setGeocodeError("Failed to fetch MapBox configuration"); // Fehler setzen
            });
    }

    useEffect(() => {
        fetchMapBoxConfig(); // API-Aufruf beim Laden der Komponente
    }, []);

    // Geocoding und Map-Initialisierung
    useEffect(() => {
        if (!props.address || !mapboxConfig) return; // Warten, bis Adresse und Token verfügbar sind

        const geocodeUrl = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(
            props.address
        )}.json?access_token=${mapboxConfig}`;

        fetch(geocodeUrl)
            .then((response) => response.json())
            .then((data) => {
                if (data.features && data.features.length > 0) {
                    const [longitude, latitude] = data.features[0].geometry.coordinates;

                    // Karte initialisieren oder neu setzen
                    if (mapRef.current) {
                        mapRef.current.remove(); // Vorherige Karte entfernen
                    }

                    if (mapContainerRef.current) {
                        mapRef.current = new mapboxgl.Map({
                            container: mapContainerRef.current,
                            style: "mapbox://styles/mapbox/streets-v11", // Karte im "Streets"-Stil
                            center: [longitude, latitude], // Zentrum setzen
                            zoom: 15, // Zoom-Level direkt auf 15 setzen
                        });

                        // Marker hinzufügen
                        new mapboxgl.Marker()
                            .setLngLat([longitude, latitude])
                            .addTo(mapRef.current);
                    }
                } else {
                    setGeocodeError("Address could not be found.");
                }
            })
            .catch((error) => {
                setGeocodeError("Error geocoding address.");
                console.error("Error geocoding address:", error);
            });

        // Bereinigen der Karte, wenn die Adresse geändert wird oder das Component unmontiert wird
        return () => {
            if (mapRef.current) {
                mapRef.current.remove();
            }
        };
    }, [props.address, mapboxConfig]); // `mapboxConfig` als Abhängigkeit, da es jetzt vom API-Aufruf kommt

    return (
        <>
            <h3>MapBox</h3>
            {geocodeError && <div>{geocodeError}</div>} {/* Zeige Fehlernachricht an, falls es ein Problem gab */}
            <div id="map-container" ref={mapContainerRef} style={{ width: "100%", height: "600px" }} />
        </>
    );
}
