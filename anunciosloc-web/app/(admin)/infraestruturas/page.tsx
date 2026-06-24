// app/(admin)/infraestruturas/page.tsx
'use client'

import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import { HiMiniMagnifyingGlass } from "react-icons/hi2";
import { LuSettings2, LuTrash2 } from "react-icons/lu";
import { ImPencil } from "react-icons/im";
import { getCookie } from 'cookies-next';

import ModalInfraestrutura, {
    Infraestrutura
} from "@/app/components/infraestruturas/ModalInfraestrutura";
import { 
    buscarTodasInfraestruturas, 
    buscarInfraestruturasDisponiveis,
    InfraestruturaResponse 
} from "@/services/infraestrutura-service";

export default function Infraestruturas() {

    const [modalOpen, setModalOpen] = useState(false);
    const [infraEditar, setInfraEditar] = useState<Infraestrutura | null>(null);
    const [infraestruturas, setInfraestruturas] = useState<Infraestrutura[]>([]);
    const [carregando, setCarregando] = useState(true);

    // Carregar infraestruturas ao iniciar
    useEffect(() => {
        carregarInfraestruturas();
    }, []);

    async function carregarInfraestruturas() {
        try {
            setCarregando(true);
            const data = await buscarTodasInfraestruturas();
            
            // Mapear para o formato da UI - CORRIGIDO
            const infraMapeadas: Infraestrutura[] = data.map((item: InfraestruturaResponse) => ({
                id: item.id,
                nome: item.nome,
                gps: item.gps || false,
                wifi: item.wifi || false,
                latitude: item.latitude,
                longitude: item.longitude,
                raio: item.raio,
                capacidade: item.capacidade,
                premio: item.premio,
                regras: item.regras || '',
                ssids: item.ssids || '',
            }));
            
            setInfraestruturas(infraMapeadas);
        } catch (error: any) {
            console.error('Erro ao carregar infraestruturas:', error);
            toast.error(error.message || 'Erro ao carregar infraestruturas');
            // Fallback para dados mock em caso de erro
            setInfraestruturas([
                {
                    id: 1,
                    nome: "Mercado do 30",
                    gps: true,
                    wifi: false,
                    latitude: "-8.8147",
                    longitude: "13.2302",
                    raio: "20",
                    capacidade: 150,
                    premio: 3,
                    regras: ""
                }
            ]);
        } finally {
            setCarregando(false);
        }
    }

    function salvarInfraestrutura(data: Infraestrutura) {
        if (infraEditar) {
            setInfraestruturas(prev =>
                prev.map(item =>
                    item.id === data.id ? data : item
                )
            );
            toast.success('Infraestrutura atualizada!');
            return;
        }

        // Nova infraestrutura - adiciona à lista
        setInfraestruturas(prev => [...prev, data]);
    }

    function eliminarInfraestrutura(id: number) {
        // TODO: Chamar API de eliminação quando disponível
        setInfraestruturas(prev =>
            prev.filter(item => item.id !== id)
        );
        toast.success("Infraestrutura removida");
    }

    const totalRedes = infraestruturas.length;

    return (
        <>
            <div className="mt-28 max-w-5xl w-full">

                <section className="flex flex-col space-y-1 md:flex-row md:items-center md:justify-between p-2 md:max-w-5xl border-amber-300">

                    <div className="space-y-1">
                        <h1 className="text-4xl font-semibold">
                            Infraestruturas
                        </h1>

                        <p className="text-gray-500">
                            Gestão das infraestruturas da rede
                        </p>
                    </div>

                    <button
                        onClick={() => {
                            setInfraEditar(null);
                            setModalOpen(true);
                        }}
                        className="text-sm sm:p-2 md:w-auto px-4 py-2 rounded-lg text-white font-semibold bg-amber-500 shadow cursor-pointer"
                    >
                        + Nova Infraestrutura
                    </button>

                </section>

                <section className="border border-gray-100 rounded-xl shadow p-4 mt-6">

                    {carregando ? (
                        <div className="text-center py-8">
                            <p className="text-gray-500">A carregar infraestruturas...</p>
                        </div>
                    ) : infraestruturas.length === 0 ? (
                        <div className="text-center py-8">
                            <p className="text-gray-500">Nenhuma infraestrutura registada.</p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto mt-6">
                            <table className="w-full min-w-[900px]">

                                <thead>
                                    <tr className="border-b border-gray-200 text-sm text-gray-500 bg-gray-50">
                                        <th className="text-left py-4 px-3 font-semibold">Infraestrutura</th>
                                        <th className="text-left py-4 px-3 font-semibold">Tipo</th>
                                        <th className="text-left py-4 px-3 font-semibold">Coordenadas</th>
                                        <th className="text-left py-4 px-3 font-semibold">Capacidade</th>
                                        <th className="text-left py-4 px-3 font-semibold">Prémio</th>
                                        <th className="text-left py-4 px-3 font-semibold">Regras</th>
                                        <th className="text-left py-4 px-3 font-semibold">Ações</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {infraestruturas.map(item => (
                                        <tr
                                            key={item.id}
                                            className="border-b border-gray-100 hover:bg-amber-50 transition-colors"
                                        >
                                            <td className="py-4 px-3 font-semibold text-gray-800">
                                                {item.nome}
                                            </td>

                                            <td className="py-4 px-3">
                                                <div className="flex gap-2 flex-wrap">
                                                    {item.gps && (
                                                        <span className="px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-700">
                                                            GPS
                                                        </span>
                                                    )}
                                                    {item.wifi && (
                                                        <span className="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-700">
                                                            WiFi
                                                        </span>
                                                    )}
                                                </div>
                                            </td>

                                            <td className="py-4 px-3 text-sm text-gray-600">
                                                {item.gps
                                                    ? `[${item.latitude}, ${item.longitude}, ${item.raio}m]`
                                                    : "--"}
                                            </td>

                                            <td className="py-4 px-3 font-medium text-gray-700">
                                                {item.capacidade}
                                            </td>

                                            <td className="py-4 px-3">
                                                <span className="bg-amber-100 text-amber-700 px-3 py-1 rounded-full text-sm font-semibold">
                                                    {item.premio} pts
                                                </span>
                                            </td>

                                            <td className="py-4 px-3 text-gray-600">
                                                {item.regras || "—"}
                                            </td>

                                            <td className="py-4 px-3">
                                                <div className="flex gap-3">
                                                    <button
                                                        onClick={() => {
                                                            setInfraEditar(item);
                                                            setModalOpen(true);
                                                        }}
                                                        className="w-9 h-9 rounded-lg border border-gray-200 hover:bg-amber-100 transition flex items-center justify-center"
                                                    >
                                                        <ImPencil size={15} className="text-gray-700" />
                                                    </button>

                                                    <button
                                                        onClick={() => eliminarInfraestrutura(item.id)}
                                                        className="w-9 h-9 rounded-lg border border-red-200 hover:bg-red-50 transition flex items-center justify-center"
                                                    >
                                                        <LuTrash2 size={16} className="text-red-500" />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>

                            </table>
                        </div>
                    )}

                </section>

            </div>

            <ModalInfraestrutura
                open={modalOpen}
                onClose={() => {
                    setModalOpen(false);
                    setInfraEditar(null);
                }}
                infraestrutura={infraEditar}
                onSave={salvarInfraestrutura}
            />
        </>
    );
}