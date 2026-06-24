// app/components/infraestruturas/ModalInfraestrutura.tsx
'use client';

import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { getCookie } from 'cookies-next';
import { 
    buscarInfraestruturasDisponiveis, 
    registarInfraestrutura,
    InfraestruturaDisponivel,
    InfraestruturaResponse 
} from '@/services/infraestrutura-service';

export interface Infraestrutura {
    id: number;
    nome: string;
    gps: boolean;
    wifi: boolean;
    latitude: string;
    longitude: string;
    raio: string;
    capacidade: number;
    premio: number;
    regras?: string;
    ssids?: string;
}

interface ModalInfraestruturaProps {
    open: boolean;
    onClose: () => void;
    infraestrutura: Infraestrutura | null;
    onSave: (data: Infraestrutura) => void;
}

export default function ModalInfraestrutura({
    open,
    onClose,
    infraestrutura,
    onSave
}: ModalInfraestruturaProps) {

    const [carregando, setCarregando] = useState(false);
    const [infraestruturasDisponiveis, setInfraestruturasDisponiveis] = useState<InfraestruturaDisponivel[]>([]);
    const [carregandoLista, setCarregandoLista] = useState(false);
    
    // Estado do formulário
    const [formData, setFormData] = useState({
        nome: '',
        latitude: '',
        longitude: '',
        raio: '',
        capacidade: '',
        bonusEntrega: '',
        custoPost: '',
    });

    // Buscar infraestruturas disponíveis quando o modal abre
    useEffect(() => {
        if (open && !infraestrutura) { // Só para novo registo
            carregarInfraestruturasDisponiveis();
        }
        if (open && infraestrutura) { // Para edição
            setFormData({
                nome: infraestrutura.nome || '',
                latitude: infraestrutura.latitude || '',
                longitude: infraestrutura.longitude || '',
                raio: infraestrutura.raio || '',
                capacidade: infraestrutura.capacidade?.toString() || '',
                bonusEntrega: infraestrutura.premio?.toString() || '',
                custoPost: '',
            });
        }
    }, [open, infraestrutura]);

    async function carregarInfraestruturasDisponiveis() {
        try {
            setCarregandoLista(true);
            const data = await buscarInfraestruturasDisponiveis();
            // Filtrar apenas as disponíveis (não registadas)
            const disponiveis = data.filter(item => !item.registadoNaBd);
            setInfraestruturasDisponiveis(disponiveis);
            
            if (disponiveis.length === 0) {
                toast.error('Nenhuma infraestrutura disponível para registar');
            }
        } catch (error: any) {
            console.error('Erro ao carregar infraestruturas:', error);
            toast.error(error.message || 'Erro ao carregar infraestruturas disponíveis');
        } finally {
            setCarregandoLista(false);
        }
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setCarregando(true);

        try {
            const email = getCookie('userEmail') as string;
            
            if (!email) {
                throw new Error('E-mail do gestor não encontrado');
            }

            // Se for edição (infraestrutura existe), chamar a API de atualização
            // Mas como a API só tem registar, vamos criar nova
            const data = {
                emailGestor: email,
                nome: formData.nome,
                latitude: parseFloat(formData.latitude),
                longitude: parseFloat(formData.longitude),
                raio: parseFloat(formData.raio),
                capacidade: parseInt(formData.capacidade),
                bonusEntrega: parseInt(formData.bonusEntrega),
                custoPost: parseInt(formData.custoPost) || 0,
            };

            // Validar campos obrigatórios
            if (!data.nome || !data.latitude || !data.longitude || !data.raio || !data.capacidade) {
                toast.error('Preencha todos os campos obrigatórios');
                setCarregando(false);
                return;
            }

            // Chamar API para registar
            const response = await registarInfraestrutura(data);
            
            // Converter resposta para o formato da UI
            const novaInfraestrutura: Infraestrutura = {
                id: response.id,
                nome: response.nome,
                gps: response.gps || false,
                wifi: response.wifi || false,
                latitude: response.latitude,
                longitude: response.longitude,
                raio: response.raio,
                capacidade: response.capacidade,
                premio: response.premio,
                regras: response.regras || '',
                ssids: response.ssids || '',
            };

            toast.success('Infraestrutura registada com sucesso!');
            onSave(novaInfraestrutura);
            onClose();
            resetForm();

        } catch (error: any) {
            console.error('Erro ao registar infraestrutura:', error);
            toast.error(error.message || 'Erro ao registar infraestrutura');
        } finally {
            setCarregando(false);
        }
    }

    function resetForm() {
        setFormData({
            nome: '',
            latitude: '',
            longitude: '',
            raio: '',
            capacidade: '',
            bonusEntrega: '',
            custoPost: '',
        });
        setInfraestruturasDisponiveis([]);
    }

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
            <div className="bg-white rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6">
                
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-semibold">
                        {infraestrutura ? 'Editar Infraestrutura' : 'Nova Infraestrutura'}
                    </h2>
                    <button
                        onClick={onClose}
                        className="text-gray-500 hover:text-gray-700 text-2xl"
                    >
                        ×
                    </button>
                </div>

                {!infraestrutura && carregandoLista ? (
                    <div className="text-center py-8">
                        <p className="text-gray-500">A carregar infraestruturas disponíveis...</p>
                    </div>
                ) : !infraestrutura && infraestruturasDisponiveis.length === 0 ? (
                    <div className="text-center py-8">
                        <p className="text-gray-500">Nenhuma infraestrutura disponível para registar.</p>
                        <p className="text-sm text-gray-400 mt-2">
                            Todas as infraestruturas UDDI já estão registadas.
                        </p>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit} className="space-y-4">
                        
                        {/* Nome - Dropdown para novo, texto para edição */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Nome da Infraestrutura *
                            </label>
                            {infraestrutura ? (
                                <input
                                    type="text"
                                    value={formData.nome}
                                    disabled
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-600"
                                />
                            ) : (
                                <select
                                    value={formData.nome}
                                    onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                                    required
                                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                                >
                                    <option value="">Selecione uma infraestrutura</option>
                                    {infraestruturasDisponiveis.map((item) => (
                                        <option key={item.nome} value={item.nome}>
                                            {item.nome}
                                        </option>
                                    ))}
                                </select>
                            )}
                        </div>

                        {/* Latitude */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Latitude *
                            </label>
                            <input
                                type="number"
                                step="0.0001"
                                value={formData.latitude}
                                onChange={(e) => setFormData({ ...formData, latitude: e.target.value })}
                                required
                                placeholder="-8.8368"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Longitude */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Longitude *
                            </label>
                            <input
                                type="number"
                                step="0.0001"
                                value={formData.longitude}
                                onChange={(e) => setFormData({ ...formData, longitude: e.target.value })}
                                required
                                placeholder="13.2343"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Raio */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Raio (metros) *
                            </label>
                            <input
                                type="number"
                                step="1"
                                value={formData.raio}
                                onChange={(e) => setFormData({ ...formData, raio: e.target.value })}
                                required
                                placeholder="500"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Capacidade */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Capacidade *
                            </label>
                            <input
                                type="number"
                                step="1"
                                value={formData.capacidade}
                                onChange={(e) => setFormData({ ...formData, capacidade: e.target.value })}
                                required
                                placeholder="100"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Bónus de Entrega */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Bónus de Entrega (pontos) *
                            </label>
                            <input
                                type="number"
                                step="1"
                                value={formData.bonusEntrega}
                                onChange={(e) => setFormData({ ...formData, bonusEntrega: e.target.value })}
                                required
                                placeholder="2"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Custo de Post */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Custo de Post
                            </label>
                            <input
                                type="number"
                                step="1"
                                value={formData.custoPost}
                                onChange={(e) => setFormData({ ...formData, custoPost: e.target.value })}
                                placeholder="1"
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                            />
                        </div>

                        {/* Botões */}
                        <div className="flex gap-3 justify-end pt-4">
                            <button
                                type="button"
                                onClick={onClose}
                                className="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                disabled={carregando}
                                className="px-6 py-2 bg-amber-500 text-white font-semibold rounded-lg hover:bg-amber-600 transition disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {carregando 
                                    ? 'A registar...' 
                                    : infraestrutura 
                                        ? 'Atualizar' 
                                        : 'Registar'
                                }
                            </button>
                        </div>

                    </form>
                )}

            </div>
        </div>
    );
}