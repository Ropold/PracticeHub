import {Navigate, Outlet} from "react-router-dom";

type ProtectedRouteProps = {
    user: string | undefined;
}

export default function ProtectedRoute(props: ProtectedRouteProps){
    const isAuthenticated = props.user !== "anonymousUser" && props.user !== null;

    return(
        isAuthenticated ? <Outlet /> : <Navigate to={"/"} />
    )
}