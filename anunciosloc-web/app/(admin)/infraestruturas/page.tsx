'use client'
import {useForm} from 'react-hook-form';
import {z} from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import {HiMiniMagnifyingGlass} from "react-icons/hi2";
import { LuSettings2 , LuTrash2} from "react-icons/lu";
import { ImPencil } from "react-icons/im";



const nomeShema = z.object({
    nome:z.string().min(2, "O nome deve conter no minimo 2 caracteres."),
})

type nomeData = z.infer<typeof nomeShema>;

export default function Infraestruturas(){
   const  totalRedes = 4;   
   const coordenada = {latitude:"192", longitude:"124", raio:"10m"}

   const titulosInfraestruturas =["Infraestrutura", "Tipo", "Coordenada", "Ocupacao", "Premio", "Regras", "Açcões"];

   const tabelasInfraestruturas =[
    {infraestrutura:"Largo", tipo:"GPS", coordenada:coordenada, ocupacao:"47/20", premio:"5pts", regras:1, accaoEditar:"",  accaoDetetar:""},
    {infraestrutura:"Belas" , tipo:"GPS", coordenada:coordenada, ocupacao:"47/20", premio:"5pts", regras:1, accaoEditar:"",  accaoDetetar:""},
    {infraestrutura:"Mutamba", tipo:"wifi", coordenada:coordenada, ocupacao:"47/20", premio:"5pts", regras:1, accaoEditar:"",  accaoDetetar:""}

   ]
       const { register, handleSubmit, formState: { errors, isSubmitting} }=useForm<nomeData>({
       resolver: zodResolver(nomeShema)});
       async function handleBuscarInfra(data:nomeData){
           console.log(data);
       }
    return(
        <div className=" mt-28 max-w-5xl w-full gap-6 ">
            
            <section className="flex flex-col space-y-1 md:flex-row md:items-center md:justify-between p-2 md:max-w-5xl border-amber-300">
                <div className="space-y-1">
                    <h2 className="text-2xl md:text-4xl font-semibold">Infraestruturas</h2>
                    <p className="text-gray-500 text-sm font-semibold">Registar, redimencionar e recolocar locais da rede AnunciosLoc.</p>
                </div>
                <button className=" text-sm sm:p-2 md:w-auto px-4 py-2 rounded-lg text-white font-semibold bg-amber-500 shadow cursor-pointer">+ Nova Infraestrutura</button>
            </section>
            <section className="max-w-5xl p-4 rounded-lg shadow border border-gray-100 mt-6">
                <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                    <div className="flex space-x-2 items-center ">
                        <LuSettings2 size={20} className='text-[#F8960D] '/>
                        <h2 className="text-base md:text-xl font-semibold">Redes  ({totalRedes})</h2>
                    </div>
                    
                    <form onSubmit={handleSubmit(handleBuscarInfra)} className="">
                        <div className="relative">
                            <HiMiniMagnifyingGlass className='absolute top-1/2 left-3 -translate-y-1/2 text-gray-400 font-semibold' size={14} />
                            <input {...register('nome')} disabled={isSubmitting} type="text" className=" w-full md:w-64 font-semibold pl-8 px-8 text-gray-500 h-9 rounded-lg shadow border border-gray-100 text-sm outline-amber-500" placeholder="Listar Infraestrutura..."/>
                        </div>
                         {errors.nome &&  <p className="text-xs text-red-500">{errors.nome.message}</p>} 
                    
                    </form>
                </div>
            


                <div className="overflow-x-auto">
                    <div className="min-w-225">
                      <div className="grid grid-cols-7 gap-4 p-2 mt-2 text-gray-600 font-semibold">
                            {
                                titulosInfraestruturas.map((titulo)=>(
                                    <h2 key={titulo}>{titulo}</h2>
                                ))
                            }
                        </div>

                    <div className="flex flex-col ">
                        {tabelasInfraestruturas.map((item, index)=>(
                            <div key={index} className="grid grid-cols-7 gap-4 border-t border-gray-100 p-2 text-sm font-semibold">
                                
                                <h2 className="">{item.infraestrutura}</h2>
                                <button className="justify-self-start self-center rounded-lg p-1 shadow border border-gray-100 ">{item.tipo}</button>
                                <span className="">[{item.coordenada.latitude}, {item.coordenada.longitude}, {item.coordenada.raio}]</span>
                                <span className="">{item.ocupacao}</span>
                                <button className="justify-self-start self-center rounded-lg p-1 shadow bg-amber-100 border border-gray-100 text-amber-500 ">{item.premio}</button>
                                <span >{item.regras}</span>

                                <div className="flex flex-col space-y-2 p-2">
                                   <button className="cursor-pointer"><ImPencil size={16}/></button> 
                                   <button className="cursor-pointer"><LuTrash2 size={16} className='text-red-500'/></button> 
                                </div>

                            </div>
                             ))}
                         </div>
                    </div>
                </div>
            </section>
        </div>
      
    )
}