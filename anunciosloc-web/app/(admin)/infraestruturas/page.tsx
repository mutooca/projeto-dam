'use client'
import { useForm, useFieldArray } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { HiMiniMagnifyingGlass } from "react-icons/hi2";
import { LuSettings2, LuTrash2, LuMaximize2, LuMove } from "react-icons/lu";
import { useState, useEffect } from 'react';
import toast from "react-hot-toast";

// Import dos modais
import { ModalRegistrarInfra } from '@/app/components/registarInfra/ModalRegistrarInfra';
import { ModalRecolocar } from '@/app/components/recolocarInfra/ModalRecolocar';
import { ModalRedimensionar } from '@/app/components/redimensionarInfra/ModalRedimensionar';
import { infraestruturaService, InfraestruturaAdminDTO } from '@/services/infraestruturaService';

// Schemas
const buscaSchema = z.object({
    nome: z.string().optional(),
});

const restricaoSchema = z.object({
    tipo_restricao: z.string().min(1, "Tipo de restrição é obrigatório"),
    valor: z.string().min(1, "Valor da restrição é obrigatório"),
    descricao: z.string().optional(),
});

const infraestruturaSchema = z.object({
    nome: z.string().min(1, "Selecione uma infraestrutura"),
    tipo_coordenadas: z.array(z.string()).min(1, "Selecione pelo menos um tipo de coordenada."),
    latitude: z.string().optional(),
    longitude: z.string().optional(),
    raio: z.string().optional(),
    ssid: z.string().optional(),
    nome_wifi: z.string().optional(),
    premio_entrega: z.string().min(1, "Prêmio de entrega é obrigatório"),
    url: z.string().url("URL inválida").optional().or(z.literal('')),
    capacidade: z.string().optional(),
    restricoes: z.array(restricaoSchema).optional(),
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

// Interface para exibição na tabela
interface InfraestruturaExibicao {
    id: string;
    infraestrutura: string;
    tipo: string;
    coordenada: {
        latitude: string;
        longitude: string;
        raio: string;
    };
    ocupacao: string;
    premio: string;
    url: string;
    capacidade: number;
    bonusEntrega: number;
    custoPost: number;
    raio: number;
    latitude: number;
    longitude: number;
    conexoesAtuais: number;
    ativa: boolean;
    online: boolean;
}

export default function Infraestruturas() {
    const [infraestruturas, setInfraestruturas] = useState<InfraestruturaExibicao[]>([]);
    const [infraestruturasFiltradas, setInfraestruturasFiltradas] = useState<InfraestruturaExibicao[]>([]);
    const [loading, setLoading] = useState(true);

    // Estados dos modais
    const [modalNovoAberto, setModalNovoAberto] = useState(false);
    const [modalRedimensionarAberto, setModalRedimensionarAberto] = useState(false);
    const [modalRecolocarAberto, setModalRecolocarAberto] = useState(false);
    const [infraSelecionada, setInfraSelecionada] = useState<InfraestruturaExibicao | null>(null);

    const totalRedes = infraestruturasFiltradas.length;
    const titulosInfraestruturas = ["Infraestrutura", "Tipo", "Coordenada", "Ocupação", "Prêmio", "Ações"];

    // Busca as infraestruturas da API
    useEffect(() => {
        buscarInfraestruturas();
    }, []);

    async function buscarInfraestruturas() {
        setLoading(true);
        try {
            const dados = await infraestruturaService.buscarTodas();
            console.log('Dados da API:', dados);
            
            const infraestruturasMapeadas: InfraestruturaExibicao[] = dados.map((item: InfraestruturaAdminDTO) => ({
                id: item.id,
                infraestrutura: item.nome,
                tipo: "GPS",
                coordenada: {
                    latitude: item.latitude !== undefined && item.latitude !== null ? item.latitude.toString() : 'N/A',
                    longitude: item.longitude !== undefined && item.longitude !== null ? item.longitude.toString() : 'N/A',
                    raio: item.raio !== undefined && item.raio !== null ? item.raio.toString() : 'N/A',
                },
                ocupacao: `${item.conexoesAtuais || 0}/${item.capacidade || 0}`,
                premio: `${item.bonusEntrega || 0} pts`,
                url: item.url,
                capacidade: item.capacidade || 0,
                bonusEntrega: item.bonusEntrega || 0,
                custoPost: item.custoPost || 0,
                raio: item.raio || 0,
                latitude: item.latitude || 0,
                longitude: item.longitude || 0,
                conexoesAtuais: item.conexoesAtuais || 0,
                ativa: item.ativa || false,
                online: item.online || false,
            }));
            
            setInfraestruturas(infraestruturasMapeadas);
            setInfraestruturasFiltradas(infraestruturasMapeadas);
        } catch (error) {
            console.error('Erro ao buscar infraestruturas:', error);
            toast.error('Erro ao carregar lista de infraestruturas');
        } finally {
            setLoading(false);
        }
    }

    // Form de busca
    const { register: registerBusca, handleSubmit: handleSubmitBusca, formState: { errors: errorsBusca, isSubmitting: isSubmittingBusca } } = useForm<BuscaData>({
        resolver: zodResolver(buscaSchema)
    });

    // Form de registro
    const {
        register: registerInfra,
        handleSubmit: handleSubmitInfra,
        formState: { errors: errorsInfra, isSubmitting: isSubmittingInfra },
        watch,
        control,
        reset: resetInfra,
        setValue: setValueInfra
    } = useForm<InfraestruturaData>({
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
    const { register: registerRedim, formState: { errors: errorsRedim, isSubmitting: isSubmittingRedim }, reset: resetRedim, setValue: setValueRedim } = useForm<RedimensionarData>({
        resolver: zodResolver(redimensionarSchema)
    });

    // Form de recolocar
    const { register: registerRecol, formState: { errors: errorsRecol, isSubmitting: isSubmittingRecol }, reset: resetRecol, setValue: setValueRecol } = useForm<RecolocarData>({
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
            await buscarInfraestruturas();
            toast.success('Infraestrutura registrada com sucesso!');
            resetInfra();
            setModalNovoAberto(false);
        } catch (error) {
            toast.error('Erro ao registrar infraestrutura');
        }
    }

    // Callback para quando o redimensionamento é concluído
    const handleRedimensionarSuccess = () => {
        buscarInfraestruturas();
    };

    // Callback para quando a recolocação é concluída
    const handleRecolocarSuccess = () => {
        buscarInfraestruturas();
    };

    function handleRemoverInfra(nome: string) {
        const confirmar = window.confirm(`Tem certeza que deseja remover a infraestrutura "${nome}"?`);
        if (confirmar) {
            toast.success('Infraestrutura removida com sucesso!');
            buscarInfraestruturas();
        }
    }

    function abrirRedimensionar(infra: InfraestruturaExibicao) {
        setInfraSelecionada(infra);
        setModalRedimensionarAberto(true);
    }

    function abrirRecolocar(infra: InfraestruturaExibicao) {
        setInfraSelecionada(infra);
        setModalRecolocarAberto(true);
    }

    const handleRegistrarSuccess = () => {
        buscarInfraestruturas();
    };

    if (loading) {
        return (
            <div className="mt-28 max-w-5xl w-full gap-6 flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-amber-500"></div>
            </div>
        );
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
                        <div className="grid grid-cols-6 gap-4 p-2 mt-2 text-gray-600 font-semibold">
                            {titulosInfraestruturas.map((titulo) => (
                                <h2 key={titulo}>{titulo}</h2>
                            ))}
                        </div>

                        <div className="flex flex-col">
                            {infraestruturasFiltradas.map((item, index) => (
                                <div key={item.id || index} className="grid grid-cols-6 gap-4 border-t border-gray-100 p-2 text-sm font-semibold items-center">
                                    <h2 className="truncate">
                                        {item.infraestrutura}
                                        {!item.online && <span className="ml-2 text-xs text-red-500">(Offline)</span>}
                                    </h2>
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
                setValue={setValueInfra}
                reset={resetInfra}
                onRegistrarSuccess={handleRegistrarSuccess}
            />

            <ModalRedimensionar
                isOpen={modalRedimensionarAberto}
                onClose={() => {
                    setModalRedimensionarAberto(false);
                    setInfraSelecionada(null);
                    resetRedim();
                }}
                register={registerRedim}
                errors={errorsRedim}
                isSubmitting={isSubmittingRedim}
                infra={infraSelecionada}
                onRedimensionarSuccess={handleRedimensionarSuccess}
                setValue={setValueRedim}
            />

            <ModalRecolocar
                isOpen={modalRecolocarAberto}
                onClose={() => {
                    setModalRecolocarAberto(false);
                    setInfraSelecionada(null);
                    resetRecol();
                }}
                register={registerRecol}
                errors={errorsRecol}
                isSubmitting={isSubmittingRecol}
                infra={infraSelecionada}
                onRecolocarSuccess={handleRecolocarSuccess}
                setValue={setValueRecol}
            />
        </div>
    );
}