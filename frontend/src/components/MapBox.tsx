import { useRef, useEffect, useState } from "react";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css"; // Mapbox CSS importieren
import "./styles/mapbox.css"; // Importiere die externe CSS-Datei für die Karte

type MapBoxProps = {
    address: string;
};

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN;

export default function MapBox(props: Readonly<MapBoxProps>) {
    const mapRef = useRef<mapboxgl.Map | null>(null); // Referenz für die Karte
    const mapContainerRef = useRef<HTMLDivElement | null>(null); // Referenz für den Map Container
    const [geocodeError, setGeocodeError] = useState<string | null>(null);
    const [center, setCenter] = useState<[number, number] | null>(null); // Initialer Mittelpunkt
    const [zoom, setZoom] = useState<number>(12); // Initialer Zoom

    useEffect(() => {
        // Geocoding für die Adresse
        if (!props.address) return;

        const geocodeUrl = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(
            props.address
        )}.json?access_token=${mapboxgl.accessToken}`;

        fetch(geocodeUrl)
            .then((response) => response.json())
            .then((data) => {
                if (data.features && data.features.length > 0) {
                    const [longitude, latitude] = data.features[0].geometry.coordinates;
                    setCenter([longitude, latitude]);
                    setZoom(12); // Anpassen des Zooms bei Erfolg

                    // Alte Karte entfernen, wenn sie existiert
                    if (mapRef.current) {
                        mapRef.current.remove();
                    }

                    // Neue Karte initialisieren, wenn der Container verfügbar ist
                    if (mapContainerRef.current) {
                        mapRef.current = new mapboxgl.Map({
                            container: mapContainerRef.current,
                            style: "mapbox://styles/mapbox/streets-v11", // Karte im "Streets"-Stil
                            center: [longitude, latitude], // Zentrum setzen
                            zoom: 12, // Zoom-Level
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
    }, [props.address]); // Der Effekt wird nur ausgelöst, wenn sich die Adresse ändert

    return (
        <>
            <h3>MapBox</h3>
            <p>Address: {props.address}</p>

            {geocodeError && <div>{geocodeError}</div>} {/* Zeige Fehlernachricht an, falls es ein Problem gab */}

            <div id="map-container" ref={mapContainerRef} />
        </>
    );
}
