import {Navigate, Outlet} from "react-router-dom";

type ProtectedRouteProps = {
    user: string | undefined;
}

export default function ProtectedRoute(props: ProtectedRouteProps){
    const isAuthenicated = props.user !== "anonymousUser" && props.user !== null;

    return(
        isAuthenicated ? <Outlet /> : <Navigate to={"/"} />
    )
}