'use client'
import { useEffect, useState } from 'react';
import { ResponsiveContainer, Tooltip, Legend, PieChart, Pie, Cell} from 'recharts';

interface cobertura{
    name:string,
    value:number
}

export default function GraficoCobertura(){

    const [cobertura, setCobertura] = useState<cobertura[]>([]);

    useEffect(()=>{
        setCobertura([
             {
                name: 'GPS',
                value: 15
            },
            {
                name: 'WiFi',
                value: 25
            }
        ])
    }, [])
    return(
        <div className="min-w-0 shadow rounded-2xl border border-gray-100 bg-white p-6">
            <h2 className="font-semibold text-xl mb-4">Tipo de cobertura</h2>
            <div className="w-full h-[350px] ">
                <ResponsiveContainer  width="99%" height={350}>
                    <PieChart>
                        <Pie data={cobertura}
                        dataKey='value'
                        nameKey='name'
                        innerRadius={50}
                        outerRadius={100}>

                        <Cell fill="#F97316" />
                        <Cell fill="#FDBA74" />
                        </Pie>
                        <Tooltip/>
                        <Legend/>
                    </PieChart>
                    
                </ResponsiveContainer>
            </div>
            
        </div>
    )
}