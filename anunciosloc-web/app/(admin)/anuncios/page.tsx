import {LuMegaphone} from "react-icons/lu";
import { LuMapPin } from "react-icons/lu";

export default function Anuncios(){
    const anunciosPostados=[
        {nomeUser:"Joana Paula", dataPublicacao:"26/05/2025", horaPublicacao:"15:20:25", tituloAnuncio:"procura-se boleia para Talatona",
            iconInfra:<LuMapPin />, iconEntrega:<LuMapPin/>,  entreg:"16 entregas", ptsGerados:"24 pts gerados", nomeInfraestrutura:"FCN-UAN Campos"
        },
         {nomeUser:"Ana Laura", dataPublicacao:"26/05/2025", horaPublicacao:"15:20:25", tituloAnuncio:"procura-se boleia para Talatona",
          iconInfra:<LuMapPin />, iconEntrega:<LuMapPin />, entreg:"16 entregas", ptsGerados:"24 pts gerados", nomeInfraestrutura:"FCN-UAN Campos"
        },
         {nomeUser:"Mara Dalva", dataPublicacao:"26/05/2025", horaPublicacao:"15:20:25", tituloAnuncio:"procura-se boleia para Talatona",
           iconInfra:<LuMapPin />, iconEntrega:<LuMapPin/>,  entreg:"16 entregas", ptsGerados:"24 pts gerados", nomeInfraestrutura:"FCN-UAN Campos"
        }

    ]
    return(
        <div className="flex flex-col mt-28 max-w-5xl w-full gap-6">
            <div className="flex flex-col max-w-5xl w-full">
                <h2 className="text-xl md:text-4xl font-semibold">Anúncios da Rede</h2>
                <p className="text-gray-600 font-semibold">Mensagens postadas pelos clientes móveis e entregues por Infraestrutura</p>
            </div>
            <div className="flex flex-col gap-4 p-4 max-w-5xl rounded-lg shadow border border-gray-100">
                <div className="flex items-center gap-2">
                    <LuMegaphone size={26} className="text-[#F8960D] "/>
                    <h2 className="text-sm md:text-xl font-semibold">Stream de publicações</h2>
                </div>
                {
                    anunciosPostados.map((item, index)=>(
                        <div key={index} className="p-4 rounded-lg shadow border border-gray-100 hover:border-[#F8960D]/50  cursor-pointer">
                            <div className="flex flex-col space-y-1 md:flex-row md:items-center md:justify-between">
                                <div className="flex items-center gap-2">
                                    <button className="rounded-2xl p-2 bg-[#F8960D] font-bold text-sm text-white">{item.nomeUser.split(' ').map(n=>n[0]).join('').toUpperCase()}</button>
                                    <div className="flex flex-col">
                                        <span className="font-semibold text-sm">{item.nomeUser} <span className="font-normal text-gray-600">{item.dataPublicacao}, {item.horaPublicacao}</span></span>
                                        <h3 className="text-sm font-semibold">{item.tituloAnuncio}</h3>
                                    </div>
                                </div>
                                <button className="flex items-center px-4 py-1 gap-2 rounded-2xl border text-[#F8960D] border-[#F8960D] text-xs">{item.iconInfra} {item.nomeInfraestrutura}</button>
                            </div>
                            <div className="ml-8 flex items-center  text-xs font-semibold text-gray-600 ">
                                <button className="flex items-center gap-1">{item.iconEntrega} {item.entreg}.+</button>
                                <span className="">{item.ptsGerados}</span>
                            </div>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}