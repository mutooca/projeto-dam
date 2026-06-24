'use client'
import {useForm} from 'react-hook-form';
import {z} from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import {HiMiniMagnifyingGlass} from "react-icons/hi2";
import { LuMapPin, LuEllipsisVertical} from "react-icons/lu";
import { LuTrash2 } from "react-icons/lu";



const buscaShema = z.object({
    nome:z.string().min(2, "O nome deve conter no minimo 2 caracteres."),
})

type buscaData = z.infer<typeof buscaShema>;

export default function Utilizadores(){

    const userActive=[
        {estado: "Activo", valor: 5},
       {estado: "Inactivos > 7 dias", valor: 3},
    ];

    const tabelaUser =[
        {nome: "Mauro Sousa", email:"sousamauro@gmail.com", saldo: 47, ultimaPublicacao:"há 16d",
            icon:<LuMapPin className='text-amber-500' size={14}/>, ultimaLocalizacao:"Largo da Independencia",
            post: 46, entrega: 100, estado: "Inativo", btnAtividade:<LuEllipsisVertical  size={16}/>
        },

          {nome: "Mara Dalva", email:"dalvamara@gmail.com", saldo: 49, ultimaPublicacao:"há 2d",
            icon:<LuMapPin className='text-amber-500' size={14}/>, ultimaLocalizacao:"Belas Choping",
            post: 100, entrega: 200, estado: "Ativo"
        },

           {nome: "Wilson Pop", email:"wilsonpop@gmail.com", saldo: 10, ultimaPublicacao:"há 8d",
            icon:<LuMapPin className='text-amber-500' size={14}/>, ultimaLocalizacao:"Belas Choping",
            post: 10, entrega: 20, estado: "Inativo"
        }, 

          {nome: "Matondo Lucas", email:"matondolucas@gmail.com", saldo: 10, ultimaPublicacao:"há 4d",
            icon:<LuMapPin className='text-amber-500' size={14}/>, ultimaLocalizacao:"Marginal de Luanda",
            post: 20, entrega: 40, estado: "Ativo"
        }, 

    ];
    

    const titulos=["Utilizadores", "Saldo", "Ultima Publicação", "Ultima Localização", "Post/Entrega","Estado"];

    const contasRegistadas = 8;

      const { register, handleSubmit, formState: { errors, isSubmitting} } = useForm<buscaData>({
    resolver: zodResolver(buscaShema)});

    async function handleBuscar(data:buscaData){
        console.log(data);
    }

    return(
        <div className="flex flex-col mt-28 max-w-5xl w-full gap-6 ">
            
            <section className="flex flex-col ">
                <h3 className="text-xl md:text-4xl font-semibold">Utilizadores Móveis.</h3>
                <span className="text-sm text-gray-500 font-semibold">Veja quem publica, onde está, e gira saldos e estados.</span>
            </section>

            
                <section className="grid grid-cols-1 md:grid-cols-2 max-w-2xl gap-2 w-full">
                        {
                            userActive.map((item, index)=>(
                                <div key={index} className="flex flex-col p-4 rounded-lg shadow bg-white  border-2 border-gray-100">
                                    <p className="text-gray-500 font-semibold">{item.estado}</p>
                                    <span className={`text-black font-semibold text-xl ${item.estado ==='Inactivos > 7 dias' ? 'text-red-500': 'text-green-600' }`}>{item.valor}</span>
                                </div>
                            ))
                        }
                </section>

            <section className="flex flex-col p-4  max-w-5xl rounded-lg shadow border border-gray-100 gap-2">
                    <div className="flex flex-col gap-6 md:flex-row md:items-cencer md:justify-between">
                        <p className="sm:text-sm md:text-xl font-semibold">Contas registadas {(contasRegistadas)}</p>
                        <form onSubmit={handleSubmit(handleBuscar)} className="">
                            <div className="relative">
                                <HiMiniMagnifyingGlass className='absolute top-1/2 left-3 -translate-y-1/2 text-gray-400 font-semibold' size={14} />
                                <input {...register('nome')} disabled={isSubmitting} type="text" className="w-full md:w-64 font-semibold pl-8 px-8 text-gray-500 h-9 rounded-lg shadow border border-gray-100 text-sm outline-amber-500" placeholder="Pesquisar nome ou e-mail..."/>
                            </div>
                            {errors.nome &&  <p className="text-xs text-red-500">{errors.nome.message}</p>} 

                        </form>
                        
                    </div>

                      <div className="overflow-x-auto">
                        <div className="min-w-225">

                        <div className="flex flex-col mt-6">

                            <div className="grid grid-cols-6 gap-4 p-2 text-gray-500 font-semibold">
                                    {titulos.map(( item)=>(
                                            <h2 key={item}>{item}</h2>
                                    ))}
                            </div>
                            
                            <div className="flex flex-col ">
                                {tabelaUser.map((item,index)=>(
                                    <div key={index} className="grid grid-cols-6 gap-4 justify-between border-t border-gray-100 p-4">
                                        <div className="flex items-center gap-1">
                                            <div className="flex rounded-full p-2 bg-amber-500 text-sm text-white">{item.nome.split(' ').map(n=>n[0]).join('').toUpperCase()}</div>
                                            <div className="flex flex-col self-center">
                                                <span className=" text-sm">{item.nome}</span>
                                                <span className="text-gray-600 text-xs">{item.email}</span>
                                            </div>
                                        </div>
                                        
                                            <p className="text-sm self-center font-bold">{item.saldo}</p>
                                    
                                        <p className="text-red-500 text-sm self-center">{item.ultimaPublicacao}</p>
                                        <div className="flex items-center p-2 gap-6 justify-self-start self-start">
                                            {item.icon}
                                            <span className="font-semibold text-gray-500 text-sm">{item.ultimaLocalizacao}</span>
                                        </div>

                                        <span className="text-sm self-center">{item.post}.<span className="text-gray-500 text-sm">{item.entrega}</span></span>
                                        <div className="flex gap-10 items-center">
                                            <button className={` self-center justify-self-start rounded-lg px-2 py-1 text-sm cursor-pointer ${item.estado === 'Ativo' ? 'bg-green-300 text-green-800': 'bg-red-400 text-red-900'}`}>{item.estado}</button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                     </div>
                </div>
            </section>
        </div>
    )
}