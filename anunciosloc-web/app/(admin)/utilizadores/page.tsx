'use client'
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { HiMiniMagnifyingGlass } from "react-icons/hi2";
import { LuMapPin, LuEllipsisVertical } from "react-icons/lu";
import { LuTrash2 } from "react-icons/lu";
import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const buscaSchema = z.object({
    nome: z.string().optional(),
});

type buscaData = z.infer<typeof buscaSchema>;

// Interface para os dados do utilizador vindo da API
interface UtilizadorAPI {
    email: string;
    nome: string;
    saldo: number;
    ultimoPost: string;
    diasInativo: number;
    status: string;
    ultimaLocalizacao: string;
    totalAnuncios: number;
    totalEntregas: number;
}

// Interface para exibição na tabela
interface UtilizadorExibicao {
    nome: string;
    email: string;
    saldo: number;
    ultimoPost: string;
    diasInativo: number;
    status: string;
    ultimaLocalizacao: string;
    totalAnuncios: number;
    totalEntregas: number;
}

export default function Utilizadores() {
    const [utilizadores, setUtilizadores] = useState<UtilizadorExibicao[]>([]);
    const [utilizadoresFiltrados, setUtilizadoresFiltrados] = useState<UtilizadorExibicao[]>([]);
    const [loading, setLoading] = useState(true);

    // Busca os utilizadores da API
    useEffect(() => {
        buscarUtilizadores();
    }, []);

    async function buscarUtilizadores() {
        setLoading(true);
        try {
            const response = await axios.get<UtilizadorAPI[]>(
                `${API_BASE_URL}/admin/utilizadores`,
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                }
            );

            console.log('Dados da API:', response.data);

            // Mapeia os dados da API para o formato de exibição
            const utilizadoresMapeados: UtilizadorExibicao[] = response.data.map((item) => ({
                nome: item.nome,
                email: item.email,
                saldo: item.saldo || 0,
                ultimoPost: item.ultimoPost || '',
                diasInativo: item.diasInativo || 0,
                status: item.status || 'INATIVO',
                ultimaLocalizacao: item.ultimaLocalizacao || 'N/A',
                totalAnuncios: item.totalAnuncios || 0,
                totalEntregas: item.totalEntregas || 0,
            }));

            setUtilizadores(utilizadoresMapeados);
            setUtilizadoresFiltrados(utilizadoresMapeados);
        } catch (error) {
            console.error('Erro ao buscar utilizadores:', error);
            toast.error('Erro ao carregar lista de utilizadores');
        } finally {
            setLoading(false);
        }
    }

    // Calcula os estados dos utilizadores
    const userActive = [
        { estado: "Activo", valor: utilizadores.filter(u => u.status === 'ATIVO').length },
        { estado: "Inactivos > 7 dias", valor: utilizadores.filter(u => u.diasInativo > 7).length },
    ];

    const contasRegistadas = utilizadores.length;
    const titulos = ["Utilizadores", "Saldo", "Ultima Publicação", "Ultima Localização", "Post/Entrega", "Estado"];

    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<buscaData>({
        resolver: zodResolver(buscaSchema)
    });

    async function handleBuscar(data: buscaData) {
        if (!data.nome) {
            setUtilizadoresFiltrados(utilizadores);
            return;
        }
        const filtrados = utilizadores.filter(item =>
            item.nome.toLowerCase().includes(data.nome!.toLowerCase()) ||
            item.email.toLowerCase().includes(data.nome!.toLowerCase())
        );
        setUtilizadoresFiltrados(filtrados);
    }

    // Função para formatar a data
    function formatarData(dataISO: string) {
        if (!dataISO) return 'N/A';
        const data = new Date(dataISO);
        const agora = new Date();
        const diffMs = agora.getTime() - data.getTime();
        const diffDias = Math.floor(diffMs / (1000 * 60 * 60 * 24));

        if (diffDias === 0) return 'Hoje';
        if (diffDias === 1) return 'Ontem';
        if (diffDias < 7) return `há ${diffDias}d`;
        if (diffDias < 30) return `há ${Math.floor(diffDias / 7)}s`;
        return data.toLocaleDateString('pt-AO');
    }

    // Função para obter a cor do status
    function getStatusColor(status: string) {
        if (status === 'ATIVO') return 'bg-green-300 text-green-800';
        return 'bg-red-400 text-red-900';
    }

    // Função para obter a primeira letra do nome
    function getIniciais(nome: string) {
        return nome.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
    }

    if (loading) {
        return (
            <div className="flex flex-col mt-28 max-w-5xl w-full gap-6 items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-amber-500"></div>
                <p className="text-gray-500">Carregando utilizadores...</p>
            </div>
        );
    }

    return (
        <div className="flex flex-col mt-28 max-w-5xl w-full gap-6">
            <section className="flex flex-col">
                <h3 className="text-xl md:text-4xl font-semibold">Utilizadores Móveis.</h3>
                <span className="text-sm text-gray-500 font-semibold">Veja quem publica, onde está, e gira saldos e estados.</span>
            </section>

            <section className="grid grid-cols-1 md:grid-cols-2 max-w-2xl gap-2 w-full">
                {userActive.map((item, index) => (
                    <div key={index} className="flex flex-col p-4 rounded-lg shadow bg-white border-2 border-gray-100">
                        <p className="text-gray-500 font-semibold">{item.estado}</p>
                        <span className={`text-black font-semibold text-xl ${item.estado === 'Inactivos > 7 dias' ? 'text-red-500' : 'text-green-600'}`}>
                            {item.valor}
                        </span>
                    </div>
                ))}
            </section>

            <section className="flex flex-col p-4 max-w-5xl rounded-lg shadow border border-gray-100 gap-2">
                <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
                    <p className="sm:text-sm md:text-xl font-semibold">Contas registadas ({contasRegistadas})</p>
                    <form onSubmit={handleSubmit(handleBuscar)} className="">
                        <div className="relative">
                            <HiMiniMagnifyingGlass className='absolute top-1/2 left-3 -translate-y-1/2 text-gray-400 font-semibold' size={14} />
                            <input
                                {...register('nome')}
                                disabled={isSubmitting}
                                type="text"
                                className="w-full md:w-64 font-semibold pl-8 px-8 text-gray-500 h-9 rounded-lg shadow border border-gray-100 text-sm outline-amber-500"
                                placeholder="Pesquisar nome ou e-mail..."
                                onChange={(e) => {
                                    register('nome').onChange(e);
                                    if (!e.target.value) {
                                        setUtilizadoresFiltrados(utilizadores);
                                    }
                                }}
                            />
                        </div>
                        {errors.nome && <p className="text-xs text-red-500">{errors.nome.message}</p>}
                    </form>
                </div>

                <div className="overflow-x-auto">
                    <div className="min-w-225">
                        <div className="flex flex-col mt-6">
                            <div className="grid grid-cols-6 gap-4 p-2 text-gray-500 font-semibold">
                                {titulos.map((item) => (
                                    <h2 key={item}>{item}</h2>
                                ))}
                            </div>

                            <div className="flex flex-col">
                                {utilizadoresFiltrados.length === 0 ? (
                                    <div className="text-center py-8 text-gray-500">
                                        Nenhum utilizador encontrado
                                    </div>
                                ) : (
                                    utilizadoresFiltrados.map((item, index) => (
                                        <div key={index} className="grid grid-cols-6 gap-4 justify-between border-t border-gray-100 p-4">
                                            <div className="flex items-center gap-1">
                                                <div className="flex rounded-full p-2 bg-amber-500 text-sm text-white min-w-8 min-h-8 items-center justify-center">
                                                    {getIniciais(item.nome)}
                                                </div>
                                                <div className="flex flex-col self-center">
                                                    <span className="text-sm">{item.nome}</span>
                                                    <span className="text-gray-600 text-xs">{item.email}</span>
                                                </div>
                                            </div>

                                            <p className="text-sm self-center font-bold">{item.saldo}</p>

                                            <p className="text-red-500 text-sm self-center">{formatarData(item.ultimoPost)}</p>

                                            <div className="flex items-center p-2 gap-6 justify-self-start self-start">
                                                <LuMapPin className='text-amber-500' size={14} />
                                                <span className="font-semibold text-gray-500 text-sm">{item.ultimaLocalizacao}</span>
                                            </div>

                                            <span className="text-sm self-center">
                                                {item.totalAnuncios}.<span className="text-gray-500 text-sm">{item.totalEntregas}</span>
                                            </span>

                                            <div className="flex gap-10 items-center">
                                                <button className={`self-center justify-self-start rounded-lg px-2 py-1 text-sm cursor-pointer ${getStatusColor(item.status)}`}>
                                                    {item.status === 'ATIVO' ? 'Ativo' : 'Inativo'}
                                                </button>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}