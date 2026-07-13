import { LuMapPin, LuUsersRound,LuMegaphone } from "react-icons/lu";
export default function dashboard(){
    const cardStatus = [
        {title: "Infraestruturas", icon: <LuMapPin size={22}/>,value: 2},
        {title:"Utilizadores", icon: <LuUsersRound size={22}/>,value: 4},
        {title: "Conexões Ativas", icon: <LuMapPin size={22}/>,value:7},
        {title:"Entregas totais", icon: <LuMegaphone size={22}/>,value: 8}
    ];

    const dashboardInfra =[
        {icon:<LuMapPin size={22}/>, nome: "FC-UAN Campos", conexao: 25, capacidade: 56, pontos:6},
        {icon:<LuMapPin size={22}/>, nome: "Shopping Belas", conexao: 25, capacidade: 56, pontos: 4 },
        {icon:<LuMapPin size={22}/>, nome: "Talatona", conexao: 25, capacidade: 56, pontos: 9 }
    ]
    return(
        <div className="flex flex-col w-full  mt-28 gap-6">
            <div className="flex flex-col p-2 space-y-1 font-semibold ">
                <h2 className="text-xl md:text-4xl">Bem Vindo, Gestor</h2>
                <p className="text-gray-600 text-sm">Visão geral da rede AnunciosLoc em tempo real</p>    
            </div>

            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 max-w-5xl rounded-2xl ">
            {cardStatus.map((item, index)=>(
                <div key={index} className={`flex flex-col justify-center p-4 rounded-lg shadow ${item.title === "Infraestruturas"? 'bg-[#F8960D]  text-white shadow-mb shadow-amber-500':'bg-white text-gray-600 border border-gray-100'}`}>
                 
                    <div className="flex  items-start justify-between gap-3">
                        <span className="font-semibold text-xl">{item.title}</span>
                        <button className={` shrink-0  p-3 rounded-lg   ${item.value === 2 ? 'bg-white/30 text-white':'bg-[#F8960D]/20 text-[#F8960D] font-semibold'}`}>{item.icon}</button>      
                    </div>
                    <div className="flex flex-col space-y-1">
                         <h2 className={`text-3xl text-black font-semibold ${item.title === "Infraestruturas" ? 'text-white': 'text-black'}`}>{item.value}</h2>
                    </div>
                </div>
              
            ))}
            </div>
            <div className="grid grid-cols-1 gap-2 max-w-5xl w-full">

                <div className="flex flex-col p-2 rounded-lg shadow border border-gray-200 space-y-2  ">
                    <h2 className="font-semibold text-2xl">Infraestruturas registradas</h2>
                    {
                        dashboardInfra.map((item, index)=>(
                            <div key={index} className="flex items-center justify-between p-2 rounded-lg shadow border border-gray-100 gap-2">
                                <div className="flex items-center space-x-2">
                                    <button className=" p-2 text-[#F8960D]  bg-[#F8960D]/20  rounded-lg">{item.icon}</button>
                                    <div className="flex flex-col font-semibold">
                                        <h2 className="font-xl ">{item.nome}</h2>
                                        <p className="text-xs text-gray-600 font-gray-600">{item.conexao}/{item.capacidade} conexões</p>
                                    </div>
                                </div>

                                <button className="rounded-xl shadow px-2 py-1 text-[#F8960D] border border-gray-100 font-semibold text-xs">{item.pontos} <span className="">pts</span></button>
                            </div>
                        ))
                    }
                </div>
                
            </div>
        </div>
    )
}