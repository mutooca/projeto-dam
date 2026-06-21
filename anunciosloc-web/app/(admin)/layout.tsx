
import SidebarAdmin from "./sidebar_admin/page"
export default function LayoutAdmin({children} : Readonly<{children : React.ReactNode}>){
    return(
        <div className="flex min-h-screen ">
            <SidebarAdmin/>
            <main className="flex-1 px-4 md:ml-64 min-w-0">
                {children}
            </main>
        </div>
    )

}