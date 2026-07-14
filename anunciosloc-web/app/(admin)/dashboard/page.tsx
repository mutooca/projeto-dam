// app/dashboard/page.tsx
'use client'
import { LuMapPin, LuUsersRound, LuMegaphone, LuRadio, LuPackage, LuShoppingBag } from "react-icons/lu";
import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { infraestruturaService, DashboardData } from '@/services/infraestruturaService';

interface InfraestruturaDashboard {
    id: string;
    nome: string;
    conexoesAtuais: number;
    capacidade: number;
    bonusEntrega: number;
    online: boolean;
    latitude?: number;
    longitude?: number;
    raio?: number;
}

export default function Dashboard() {
    const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
    const [infraestruturas, setInfraestruturas] = useState<InfraestruturaDashboard[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        carregarDashboard();
    }, []);

    async function carregarDashboard() {
        setLoading(true);
        try {
            // Carrega os dados do dashboard
            const dados = await infraestruturaService.buscarDashboard();
            setDashboardData(dados);
            console.log('Dados do Dashboard:', dados);

            // Carrega a lista de infraestruturas para exibir abaixo
            const infraDados = await infraestruturaService.buscarTodas();
            const infraMapeadas: InfraestruturaDashboard[] = infraDados.map((item: any) => ({
                id: item.id,
                nome: item.nome,
                conexoesAtuais: item.conexoesAtuais || 0,
                capacidade: item.capacidade || 0,
                bonusEntrega: item.bonusEntrega || 0,
                online: item.online || false,
                latitude: item.latitude,
                longitude: item.longitude,
                raio: item.raio,
            }));
            setInfraestruturas(infraMapeadas);
            console.log('Infraestruturas:', infraMapeadas);

        } catch (error) {
            console.error('Erro ao carregar dashboard:', error);
            toast.error('Erro ao carregar dados do dashboard');
        } finally {
            setLoading(false);
        }
    }

    // Dados dos cards com valores da API
    const cardStatus = [
        { 
            title: "Infraestruturas", 
            icon: <LuMapPin size={22}/>, 
            value: dashboardData?.totalInfraestruturas || 0,
            bgColor: '#F8960D',
            textColor: 'white'
        },
        { 
            title: "Utilizadores", 
            icon: <LuUsersRound size={22}/>, 
            value: dashboardData?.totalUtilizadores || 0,
            bgColor: 'white',
            textColor: 'text-black'
        },
        { 
            title: "Conexões Ativas", 
            icon: <LuRadio size={22}/>, 
            value: dashboardData?.totalConexoes || 0,
            bgColor: 'white',
            textColor: 'text-black'
        },
        { 
            title: "Total Anúncios", 
            icon: <LuMegaphone size={22}/>, 
            value: dashboardData?.totalAnuncios || 0,
            bgColor: 'white',
            textColor: 'text-black'
        },
        { 
            title: "Entregas Totais", 
            icon: <LuPackage size={22}/>, 
            value: dashboardData?.totalEntregas || 0,
            bgColor: 'white',
            textColor: 'text-black'
        },
        { 
            title: "Locais", 
            icon: <LuShoppingBag size={22}/>, 
            value: dashboardData?.totalLocais || 0,
            bgColor: 'white',
            textColor: 'text-black'
        },
    ];

    // Função para determinar a cor do ícone do card
    const getIconBgColor = (title: string) => {
        if (title === "Infraestruturas") {
            return 'bg-white/30 text-white';
        }
        return 'bg-[#F8960D]/20 text-[#F8960D]';
    };

    if (loading) {
        return (
            <div className="flex flex-col w-full mt-28 gap-6 items-center justify-center h-96">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-amber-500"></div>
                <p className="text-gray-500">Carregando dashboard...</p>
            </div>
        );
    }

    return (
        <div className="flex flex-col w-full mt-28 gap-6">
            {/* Cabeçalho */}
            <div className="flex flex-col p-2 space-y-1 font-semibold">
                <h2 className="text-xl md:text-4xl">Bem Vindo, Gestor</h2>
                <p className="text-gray-600 text-sm">
                    Visão geral da rede AnunciosLoc em tempo real
                    {dashboardData?.ultimaAtualizacao && (
                        <span className="ml-2 text-xs text-gray-400">
                            (Atualizado: {new Date(dashboardData.ultimaAtualizacao).toLocaleString('pt-AO')})
                        </span>
                    )}
                </p>
            </div>

            {/* Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 max-w-6xl rounded-2xl">
                {cardStatus.map((item, index) => (
                    <div 
                        key={index} 
                        className={`flex flex-col justify-center p-4 rounded-lg shadow ${
                            item.title === "Infraestruturas" 
                                ? 'bg-[#F8960D] text-white shadow-mb shadow-amber-500' 
                                : 'bg-white text-gray-600 border border-gray-100'
                        }`}
                    >
                        <div className="flex items-start justify-between gap-3">
                            <span className="font-semibold text-xl">{item.title}</span>
                            <button className={`shrink-0 p-3 rounded-lg ${getIconBgColor(item.title)}`}>
                                {item.icon}
                            </button>
                        </div>
                        <div className="flex flex-col space-y-1">
                            <h2 className={`text-3xl font-semibold ${
                                item.title === "Infraestruturas" ? 'text-white' : 'text-black'
                            }`}>
                                {item.value}
                            </h2>
                        </div>
                    </div>
                ))}
            </div>

            {/* Lista de Infraestruturas Registradas */}
            <div className="grid grid-cols-1 gap-2 max-w-6xl w-full">
                <div className="flex flex-col p-2 rounded-lg shadow border border-gray-200 space-y-2">
                    <div className="flex justify-between items-center">
                        <h2 className="font-semibold text-2xl">Infraestruturas registradas</h2>
                        <span className="text-sm text-gray-500">
                            {infraestruturas.length} no total
                        </span>
                    </div>

                    {infraestruturas.length === 0 ? (
                        <div className="text-center py-8 text-gray-500">
                            Nenhuma infraestrutura registrada
                        </div>
                    ) : (
                        infraestruturas.map((item, index) => (
                            <div 
                                key={item.id || index} 
                                className="flex items-center justify-between p-2 rounded-lg shadow border border-gray-100 gap-2"
                            >
                                <div className="flex items-center space-x-2">
                                    <button className="p-2 text-[#F8960D] bg-[#F8960D]/20 rounded-lg">
                                        <LuMapPin size={22} />
                                    </button>
                                    <div className="flex flex-col font-semibold">
                                        <h2 className="text-sm md:text-base">
                                            {item.nome}
                                            {!item.online && (
                                                <span className="ml-2 text-xs text-red-500">(Offline)</span>
                                            )}
                                        </h2>
                                        <p className="text-xs text-gray-600">
                                            {item.conexoesAtuais}/{item.capacidade} conexões
                                        </p>
                                    </div>
                                </div>

                                <button className="rounded-xl shadow px-3 py-1.5 text-[#F8960D] border border-gray-100 font-semibold text-xs">
                                    {item.bonusEntrega} <span className="">pts</span>
                                </button>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}