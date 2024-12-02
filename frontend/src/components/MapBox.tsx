
import mapboxgl from "mapbox-gl";

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_ACCESS_TOKEN;

type MapBoxProps = {
    address: string;
};

export default function MapBox(props: Readonly<MapBoxProps>) {
    return (
        <>
            <h3>MapBox</h3>
            <p>Address: {props.address}</p>

        </>
    );
}