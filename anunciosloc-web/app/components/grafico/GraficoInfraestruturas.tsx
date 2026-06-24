'use client'
import { useState, useEffect } from 'react';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Legend
} from 'recharts';

 interface estatisticaInfraestruturas{
        infraestrutura:string,
        anuncios:number,
        entregas:number
    }


export default function GraficoInfraestuturas(){

    const[estatisticas, setEstatisticas] = useState<estatisticaInfraestruturas[]>([]);

    useEffect(()=>{
        setEstatisticas(
            [
               {
                infraestrutura: 'Mercado do 30',
                anuncios: 4,
                entregas: 80
            },
            {
                infraestrutura: 'FCN',
                anuncios: 5,
                entregas: 160
            },
            {
                infraestrutura: 'Shopping Belas',
                anuncios: 3,
                entregas: 95
            },
            {
                infraestrutura:'Zap',
                anuncios:8,
                entregas:180       }

        ]
        )
    }, []);

    return(
        <div className="lg:col-span-2 min-w-0 border border-gray-100 shadow p-6 rounded-2xl">
            <h2 className="font-semibold text-xl mb-4"> Anúncios e entregas por infraestrutura</h2>

            <div className="w-full  h-[350px] ">
                <ResponsiveContainer  width="99%" height={350}>
                    <BarChart data={estatisticas}>
                        <XAxis dataKey='infraestrutura'/>
                        <YAxis/>
                        <Tooltip/>
                        <Legend/>

                        <Bar dataKey='anuncios'
                        fill="#F97316"/>

                        <Bar dataKey='entregas'
                         fill="#FDBA74"/>
                    </BarChart>

                </ResponsiveContainer>
            </div>

        </div>
    )
}