'use client'
import { useState,useEffect } from "react"
interface saldoUtilizador{
    id:number;
    nome: string;
    saldo:number;
}

export default function SaldoAtuais(){
    const [saldoUtilizadores, setSaldoUtilizadores] =useState<saldoUtilizador[]>([]);
    useEffect(()=>{
        const saldos:saldoUtilizador[]=[
            { id: 1, nome: "João Silva", saldo: 11 },
            { id: 2, nome: "Maria Cardoso", saldo: 71 },
            { id: 3, nome: "Pedro Mendes", saldo: 0 },
            { id: 4, nome: "Ana Bumba", saldo: 90 },
            { id: 5, nome: "Carlos Neto", saldo: 0 },
            { id: 6, nome: "Sofia Manuel", saldo: 2 },
            { id: 7, nome: "Bruno Domingos", saldo: 29 },
            { id: 8, nome: "Lúcia Pereira", saldo: 0 }
        ];
        setSaldoUtilizadores(saldos);
    }, [])
    return(
        <div className="flex flex-col gap-4 p-4 shadow rounded-lg border border-gray-200 max-w-5xl mb-4">
            <h2 className="font-semibold text-xl">Saldos atuais</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-2">
                {saldoUtilizadores.map(user=>(
                    <div key={user.id} className="rounded-xl p-4 border border-gray-200">
                        <h2 className="font-medium text-lg">{user.nome}</h2>
                        <div className="mt-2">
                            <span className="text-3xl font-bold text-amber-500">{user.saldo}</span>
                            <span className="ml-1 text-gray-600">Pts</span>
                        </div>
                    </div>
                ))}

            </div>
        </div>
    )
}