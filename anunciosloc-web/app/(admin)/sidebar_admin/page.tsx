'use client'
import { useState } from 'react';
import { LuMenu, LuX } from 'react-icons/lu';
import Link from 'next/link';
import { LuLayoutDashboard, LuMapPin, LuUsersRound,LuMegaphone, LuChartColumn, LuAward, LuLogOut } from "react-icons/lu";
import { BsBroadcast } from "react-icons/bs";

export default function SidebarAdmin(){
    const menuItems = [
        {icon: <LuLayoutDashboard size={20}/>, label:'Dashboard', href:"/dashboard"},
        {icon:<LuMapPin size={20}/>, label:'Infraestruturas', href:"/infraestruturas"},
        {icon:<LuUsersRound size={20}/>, label:'Utilizadores', href:"/utilizadores"},
        {icon:<LuChartColumn size={20}/>, label:'Estatísticas', href:"/estatisticas"}
    ]

    const userName = "João Silva";
    const [menuOpen, setMenuOpen] =useState(false);

    return(
        <div className="">
            <header className=" fixed top-0 left-0 md:left-64 right-0 h-20 flex items-center justify-between bg-white border-b border-zinc-200 px-6 z-40">
                 <button onClick={()=>setMenuOpen(true)} className="md:hidden cursor-pointer" > <LuMenu size={24}/></button>
                <div className="hidden md:flex items-center gap-2">
                    <div className="bg-gray-200 border border-zinc-200 h-6 rounded-lg"></div>
                    <span className="text-zinc-600 sm:block">AnunciosLoc / <span className=" md:text-zinc-900  font-semibold">Painel do Gestor</span></span>
                </div>

                <div className="flex gap-4 items-center">

                    <button className="p-2 rounded-lg border border-zinc-100 flex items-center justify-center gap-2 text-zinc-600 cursor-pointer hover:bg-amber-500 hover:text-zinc-900">
                        <LuLogOut size={20}/>
                        <span className="font-medium text-sm">Sair</span>
                    </button>
                </div>

            </header>
            {menuOpen && ( <div onClick={() => setMenuOpen(false)}  className="fixed inset-0 bg-black/50 z-40 md:hidden "/>)}

            
           <aside className={`fixed flex flex-col top-0 left-0 h-screen w-64 bg-white border-r border-zinc-200 z-50 transition-transform duration-300
           ${menuOpen? 'translate-x-0': '-translate-x-full' } md:translate-x-0`}>

            <div className="flex justify-end md:hidden p-2"><button onClick={()=>setMenuOpen(false)} className="p-2"><LuX size={24}/></button>
            </div>

                <div className="flex items-center p-4 gap-2 border-b border-zinc-100">
                    <div className="flex items-center justify-center rounded-lg shadow bg-amber-500 p-2">
                        <BsBroadcast size={30} className='text-white'/>
                    </div>
                    <div className="flex flex-col ">
                        <p className="font-semibold text-xl">AnunciosLoc</p>
                        <p className="text-zinc-600 text-sm">GESTOR DE REDE</p>
                    </div>
                </div>

                <div className="flex-grow space-y-1">
                    <h2 className="font-semibold px-2 py-2 text-xl ">Gestão</h2>
                    <nav className="space-y-1">
                        {
                            menuItems.map((item, index)=>(
                                <Link target='' key={index} onClick={()=>setMenuOpen(false)} href={item.href} className='flex items-center gap-3 rounded-xl text-zinc-800 px-2 py-2 hover:bg-amber-200 transition-colors group'>
                                    <span className="text-zinc-800 group-hover:text-zinc-400 transition-colors">{item.icon}</span>
                                    <span className="text-zinc-800">{item.label}</span>
                                </Link>
                        
                            ))
                        }
                    </nav>
                </div>

                <div className="flex justify-center border-t border-zinc-100 p-4 ">
                    <div className="flex flex-col bg-amber-200 p-2 rounded-xl space-y-1 w-full">
                        <span className="text-zinc-600 text-sm">Sistema baseado em localização</span>
                    </div>
                </div>

            </aside>
        </div>
    )
}