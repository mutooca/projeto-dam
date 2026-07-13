'use client'
import { useForm, useFieldArray } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { HiMiniMagnifyingGlass } from "react-icons/hi2";
import { LuSettings2, LuTrash2, LuMaximize2, LuMove } from "react-icons/lu";
import { useState } from 'react';
import toast from "react-hot-toast";

// Import dos modais
import { ModalRegistrarInfra } from '@/app/components/registarInfra/ModalRegistrarInfra';
import { ModalRecolocar } from '@/app/components/recolocarInfra/ModalRecolocar';
import { ModalRedimensionar } from '@/app/components/redimensionarInfra/ModalRedimensionar';

// Schemas (mesmos que antes)
const buscaSchema = z.object({
    nome: z.string().optional(),
});

const restricaoSchema = z.object({
    tipo_restricao: z.string().min(1, "Tipo de restrição é obrigatório"),
    valor: z.string().min(1, "Valor da restrição é obrigatório"),
    descricao: z.string().optional(),
});

const infraestruturaSchema = z.object({
    nome: z.string().min(2, "O nome deve conter no mínimo 2 caracteres."),
    tipo_coordenadas: z.array(z.string()).min(1, "Selecione pelo menos um tipo de coordenada."),
    latitude: z.string().optional(),
    longitude: z.string().optional(),
    raio: z.string().optional(),
    ssid: z.string().optional(),
    nome_wifi: z.string().optional(),
    premio_entrega: z.string().min(1, "Prêmio de entrega é obrigatório"),
    restricoes: z.array(restricaoSchema).min(1, "Adicione pelo menos uma restrição"),
});

const redimensionarSchema = z.object({
    capacidade: z.string().min(1, "Capacidade é obrigatória"),
    bonus_entrega: z.string().min(1, "Bônus de entrega é obrigatório"),
    custo_post: z.string().min(1, "Custo por post é obrigatório"),
    raio: z.string().min(1, "Raio é obrigatório"),
});

const recolocarSchema = z.object({
    latitude: z.string().min(1, "Latitude é obrigatória"),
    longitude: z.string().min(1, "Longitude é obrigatória"),
    raio: z.string().min(1, "Raio é obrigatório"),
});

type BuscaData = z.infer<typeof buscaSchema>;
type InfraestruturaData = z.infer<typeof infraestruturaSchema>;
type RedimensionarData = z.infer<typeof redimensionarSchema>;
type RecolocarData = z.infer<typeof recolocarSchema>;

interface Infraestrutura {
    id?: string;
    infraestrutura: string;
    tipo: string;
    coordenada: {
        latitude: string;
        longitude: string;
        raio: string;
    };
    ocupacao: string;
    premio: string;
    regras: number;
    restricoes?: any[];
    ssid?: string;
    nome_wifi?: string;
}

export default function Infraestruturas() {
    const [infraestruturas, setInfraestruturas] = useState<Infraestrutura[]>([
        {
            infraestrutura: "Largo da Independência",
            tipo: "GPS",
            coordenada: { latitude: "-8.8147", longitude: "13.2302", raio: "20m" },
            ocupacao: "47/20",
            premio: "5pts",
            regras: 1,
            restricoes: [{ tipo_restricao: "post", valor: "Excluir redes televisivas", descricao: "Proibido anunciar redes televisivas" }]
        },
        {
            infraestrutura: "Belas",
            tipo: "GPS",
            coordenada: { latitude: "-8.9500", longitude: "13.1800", raio: "15m" },
            ocupacao: "30/15",
            premio: "10pts",
            regras: 2
        },
        {
            infraestrutura: "Mutamba",
            tipo: "WiFi",
            coordenada: { latitude: "-8.8300", longitude: "13.2400", raio: "25m" },
            ocupacao: "50/25",
            premio: "8pts",
            regras: 1,
            ssid: "Mutamba_WiFi",
            nome_wifi: "Rede Mutamba"
        }
    ]);

    const [infraestruturasFiltradas, setInfraestruturasFiltradas] = useState(infraestruturas);

    // Estados dos modais
    const [modalNovoAberto, setModalNovoAberto] = useState(false);
    const [modalRedimensionarAberto, setModalRedimensionarAberto] = useState(false);
    const [modalRecolocarAberto, setModalRecolocarAberto] = useState(false);
    const [infraSelecionada, setInfraSelecionada] = useState<Infraestrutura | null>(null);

    const totalRedes = infraestruturasFiltradas.length;
    const titulosInfraestruturas = ["Infraestrutura", "Tipo", "Coordenada", "Ocupação", "Prêmio", "Regras", "Ações"];

    // Form de busca
    const { register: registerBusca, handleSubmit: handleSubmitBusca, formState: { errors: errorsBusca, isSubmitting: isSubmittingBusca } } = useForm<BuscaData>({
        resolver: zodResolver(buscaSchema)
    });

    // Form de registro
    const { register: registerInfra, handleSubmit: handleSubmitInfra, formState: { errors: errorsInfra, isSubmitting: isSubmittingInfra }, watch, control, reset: resetInfra } = useForm<InfraestruturaData>({
        resolver: zodResolver(infraestruturaSchema),
        defaultValues: {
            tipo_coordenadas: [],
            restricoes: [{ tipo_restricao: '', valor: '', descricao: '' }]
        }
    });
    const { fields, append, remove } = useFieldArray({
        control,
        name: "restricoes"
    });

    const tipoCoordenadasWatch = watch('tipo_coordenadas', []);
    const isGPS = tipoCoordenadasWatch.includes('GPS');
    const isWiFi = tipoCoordenadasWatch.includes('WiFi');

    // Form de redimensionar
    const { register: registerRedim, handleSubmit: handleSubmitRedim, formState: { errors: errorsRedim, isSubmitting: isSubmittingRedim }, reset: resetRedim } = useForm<RedimensionarData>({
        resolver: zodResolver(redimensionarSchema)
    });

    // Form de recolocar
    const { register: registerRecol, handleSubmit: handleSubmitRecol, formState: { errors: errorsRecol, isSubmitting: isSubmittingRecol }, reset: resetRecol } = useForm<RecolocarData>({
        resolver: zodResolver(recolocarSchema)
    });

    // Funções de manipulação
    async function handleBuscarInfra(data: BuscaData) {
        if (!data.nome) {
            setInfraestruturasFiltradas(infraestruturas);
            return;
        }
        const filtradas = infraestruturas.filter(item =>
            item.infraestrutura.toLowerCase().includes(data.nome!.toLowerCase())
        );
        setInfraestruturasFiltradas(filtradas);
    }

    async function handleRegistrarInfra(data: InfraestruturaData) {
        try {
            const novaInfra: Infraestrutura = {
                infraestrutura: data.nome,
                tipo: data.tipo_coordenadas.join(' + '),
                coordenada: {
                    latitude: data.latitude || '',
                    longitude: data.longitude || '',
                    raio: data.raio || ''
                },
                ocupacao: "0/0",
                premio: `${data.premio_entrega}pts`,
                regras: data.restricoes.length,
                restricoes: data.restricoes,
                ssid: data.ssid,
                nome_wifi: data.nome_wifi
            };

            setInfraestruturas(prev => [...prev, novaInfra]);
            setInfraestruturasFiltradas(prev => [...prev, novaInfra]);
            toast.success('Infraestrutura registrada com sucesso!');
            resetInfra();
            setModalNovoAberto(false);
        } catch (error) {
            toast.error('Erro ao registrar infraestrutura');
        }
    }

    async function handleRedimensionar(data: RedimensionarData) {
        try {
            toast.success('Infraestrutura redimensionada com sucesso!');
            resetRedim();
            setModalRedimensionarAberto(false);
            setInfraSelecionada(null);
        } catch (error) {
            toast.error('Erro ao redimensionar');
        }
    }

    async function handleRecolocar(data: RecolocarData) {
        try {
            if (infraSelecionada) {
                const infraAtualizada = {
                    ...infraSelecionada,
                    coordenada: {
                        ...infraSelecionada.coordenada,
                        latitude: data.latitude,
                        longitude: data.longitude,
                        raio: data.raio
                    }
                };
                setInfraestruturas(prev =>
                    prev.map(item => item.infraestrutura === infraSelecionada.infraestrutura ? infraAtualizada : item)
                );
                setInfraestruturasFiltradas(prev =>
                    prev.map(item => item.infraestrutura === infraSelecionada.infraestrutura ? infraAtualizada : item)
                );
                toast.success('Infraestrutura recolocada com sucesso!');
                resetRecol();
                setModalRecolocarAberto(false);
                setInfraSelecionada(null);
            }
        } catch (error) {
            toast.error('Erro ao recolocar infraestrutura');
        }
    }

    function handleRemoverInfra(nome: string) {
        const confirmar = window.confirm(`Tem certeza que deseja remover a infraestrutura "${nome}"?`);
        if (confirmar) {
            setInfraestruturas(prev => prev.filter(item => item.infraestrutura !== nome));
            setInfraestruturasFiltradas(prev => prev.filter(item => item.infraestrutura !== nome));
            toast.success('Infraestrutura removida com sucesso!');
        }
    }

    function abrirRedimensionar(infra: Infraestrutura) {
        setInfraSelecionada(infra);
        setModalRedimensionarAberto(true);
    }

    function abrirRecolocar(infra: Infraestrutura) {
        setInfraSelecionada(infra);
        setModalRecolocarAberto(true);
    }

    return (
        <div className="mt-28 max-w-5xl w-full gap-6">
            {/* Cabeçalho */}
            <section className="flex flex-col space-y-1 md:flex-row md:items-center md:justify-between p-2 md:max-w-5xl border-amber-300">
                <div className="space-y-1">
                    <h2 className="text-2xl md:text-4xl font-semibold">Infraestruturas</h2>
                    <p className="text-gray-500 text-sm font-semibold">Registar, redimensionar e recolocar locais da rede AnunciosLoc.</p>
                </div>
                <button
                    onClick={() => setModalNovoAberto(true)}
                    className="text-sm sm:p-2 md:w-auto px-4 py-2 rounded-lg text-white font-semibold bg-amber-500 shadow cursor-pointer hover:bg-amber-600 transition"
                >
                    + Nova Infraestrutura
                </button>
            </section>

            {/* Tabela */}
            <section className="max-w-5xl p-4 rounded-lg shadow border border-gray-100 mt-6">
                <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                    <div className="flex space-x-2 items-center">
                        <LuSettings2 size={20} className='text-[#F8960D]' />
                        <h2 className="text-base md:text-xl font-semibold">Redes ({totalRedes})</h2>
                    </div>

                    <form onSubmit={handleSubmitBusca(handleBuscarInfra)} className="">
                        <div className="relative">
                            <HiMiniMagnifyingGlass className='absolute top-1/2 left-3 -translate-y-1/2 text-gray-400 font-semibold' size={14} />
                            <input
                                {...registerBusca('nome')}
                                disabled={isSubmittingBusca}
                                type="text"
                                className="w-full md:w-64 font-semibold pl-8 px-8 text-gray-500 h-9 rounded-lg shadow border border-gray-100 text-sm outline-amber-500"
                                placeholder="Pesquisar Infraestrutura..."
                                onChange={(e) => {
                                    registerBusca('nome').onChange(e);
                                    if (!e.target.value) {
                                        setInfraestruturasFiltradas(infraestruturas);
                                    }
                                }}
                            />
                        </div>
                        {errorsBusca.nome && <p className="text-xs text-red-500">{errorsBusca.nome.message}</p>}
                    </form>
                </div>

                <div className="overflow-x-auto">
                    <div className="min-w-225">
                        <div className="grid grid-cols-7 gap-4 p-2 mt-2 text-gray-600 font-semibold">
                            {titulosInfraestruturas.map((titulo) => (
                                <h2 key={titulo}>{titulo}</h2>
                            ))}
                        </div>

                        <div className="flex flex-col">
                            {infraestruturasFiltradas.map((item, index) => (
                                <div key={index} className="grid grid-cols-7 gap-4 border-t border-gray-100 p-2 text-sm font-semibold items-center">
                                    <h2 className="truncate">{item.infraestrutura}</h2>
                                    <button className="justify-self-start self-center rounded-lg p-1 shadow border border-gray-100 text-xs">
                                        {item.tipo}
                                    </button>
                                    <span className="text-xs truncate">
                                        [{item.coordenada.latitude}, {item.coordenada.longitude}, {item.coordenada.raio}]
                                    </span>
                                    <span>{item.ocupacao}</span>
                                    <button className="justify-self-start self-center rounded-lg p-1 shadow bg-amber-100 border border-gray-100 text-amber-500 text-xs">
                                        {item.premio}
                                    </button>
                                    <span>{item.regras}</span>

                                    <div className="flex items-center gap-2">
                                        <button
                                            onClick={() => abrirRedimensionar(item)}
                                            className="cursor-pointer p-1 hover:bg-gray-100 rounded transition"
                                            title="Redimensionar"
                                        >
                                            <LuMaximize2 size={16} className="text-blue-500" />
                                        </button>
                                        <button
                                            onClick={() => abrirRecolocar(item)}
                                            className="cursor-pointer p-1 hover:bg-gray-100 rounded transition"
                                            title="Recolocar"
                                        >
                                            <LuMove size={16} className="text-green-500" />
                                        </button>
                                        <button
                                            onClick={() => handleRemoverInfra(item.infraestrutura)}
                                            className="cursor-pointer p-1 hover:bg-gray-100 rounded transition"
                                            title="Remover"
                                        >
                                            <LuTrash2 size={16} className="text-red-500" />
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </section>

            {/* Modais */}
            <ModalRegistrarInfra
                isOpen={modalNovoAberto}
                onClose={() => {
                    setModalNovoAberto(false);
                    resetInfra();
                }}
                onSubmit={handleSubmitInfra(handleRegistrarInfra)}
                register={registerInfra}
                errors={errorsInfra}
                isSubmitting={isSubmittingInfra}
                watch={watch}
                control={control}
                fields={fields}
                append={append}
                remove={remove}
                isGPS={isGPS}
                isWiFi={isWiFi}
            />

            <ModalRedimensionar
                isOpen={modalRedimensionarAberto}
                onClose={() => {
                    setModalRedimensionarAberto(false);
                    setInfraSelecionada(null);
                    resetRedim();
                }}
                onSubmit={handleSubmitRedim(handleRedimensionar)}
                register={registerRedim}
                errors={errorsRedim}
                isSubmitting={isSubmittingRedim}
                infra={infraSelecionada}
            />

            <ModalRecolocar
                isOpen={modalRecolocarAberto}
                onClose={() => {
                    setModalRecolocarAberto(false);
                    setInfraSelecionada(null);
                    resetRecol();
                }}
                onSubmit={handleSubmitRecol(handleRecolocar)}
                register={registerRecol}
                errors={errorsRecol}
                isSubmitting={isSubmittingRecol}
                infra={infraSelecionada}
            />
        </div>
    );
}