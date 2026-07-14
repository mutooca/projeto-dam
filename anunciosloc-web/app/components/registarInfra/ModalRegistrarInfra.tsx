// ModalRegistrarInfra.tsx - Versão corrigida
import { useEffect, useState } from 'react';
import { infraestruturaService, InfraestruturaNaoRegistada, RegistarInfraestruturaRequest } from '@/services/infraestruturaService';
import toast from 'react-hot-toast';

export function ModalRegistrarInfra({
    isOpen,
    onClose,
    register,
    errors,
    isSubmitting,
    watch,
    control,
    fields,
    append,
    remove,
    isGPS,
    isWiFi,
    setValue,
    reset,
    onRegistrarSuccess
}: any) {
    const [infraestruturasDisponiveis, setInfraestruturasDisponiveis] = useState<InfraestruturaNaoRegistada[]>([]);
    const [loading, setLoading] = useState(false);
    const [selectedNome, setSelectedNome] = useState('');
    const [registrando, setRegistrando] = useState(false);

    // Busca as infraestruturas não registadas quando o modal abre
    useEffect(() => {
        if (isOpen) {
            buscarInfraestruturas();
        }
    }, [isOpen]);

    async function buscarInfraestruturas() {
        setLoading(true);
        try {
            const data = await infraestruturaService.buscarNaoRegistadas();
            setInfraestruturasDisponiveis(data);
            console.log('Infraestruturas disponíveis:', data);
        } catch (error) {
            console.error('Erro ao buscar infraestruturas:', error);
            toast.error('Erro ao carregar lista de infraestruturas');
        } finally {
            setLoading(false);
        }
    }

    // Quando o nome é selecionado, preenche a URL automaticamente
    const handleNomeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const nomeSelecionado = e.target.value;
        setSelectedNome(nomeSelecionado);
        
        const infraSelecionada = infraestruturasDisponiveis.find(
            infra => infra.nome === nomeSelecionado
        );
        
        if (infraSelecionada) {
            setValue('url', infraSelecionada.url);
            console.log('URL preenchida:', infraSelecionada.url);
        } else {
            setValue('url', '');
        }
    };

    // Handler personalizado para o submit
    const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        console.log('=== INICIANDO REGISTRO ===');
        
        const formData = watch();
        console.log('Dados do formulário (raw):', formData);

        // Validação básica
        if (!formData.nome) {
            toast.error('Selecione uma infraestrutura');
            return;
        }

        if (!formData.capacidade) {
            toast.error('Informe a capacidade');
            return;
        }

        // Verifica se tem bonusEntrega OU premio_entrega
        const bonusValue = formData.bonusEntrega || formData.premio_entrega;
        if (!bonusValue) {
            toast.error('Informe o bônus de entrega');
            return;
        }

        try {
            setRegistrando(true);

            // Prepara os dados para a API
            const requestData: RegistarInfraestruturaRequest = {
                nome: formData.nome,
                url: formData.url || '',
                capacidade: parseInt(formData.capacidade) || 0,
                bonusEntrega: parseInt(bonusValue) || 0,
            };

            // Adiciona campos opcionais
            if (formData.latitude && formData.latitude !== '') {
                requestData.latitude = parseFloat(formData.latitude);
            }
            if (formData.longitude && formData.longitude !== '') {
                requestData.longitude = parseFloat(formData.longitude);
            }
            if (formData.raio && formData.raio !== '') {
                requestData.raio = parseFloat(formData.raio);
            }
            if (formData.custoPost && formData.custoPost !== '') {
                requestData.custoPost = parseInt(formData.custoPost);
            }
            if (formData.restricoes && formData.restricoes !== '') {
                requestData.restricoes = formData.restricoes;
            }
            if (formData.ssid && formData.ssid !== '') {
                requestData.ssid = formData.ssid;
            }
            if (formData.nome_wifi && formData.nome_wifi !== '') {
                requestData.nome_wifi = formData.nome_wifi;
            }

            console.log('Dados preparados para envio:', JSON.stringify(requestData, null, 2));

            const response = await infraestruturaService.registarInfraestrutura(requestData);

            console.log('Resposta da API:', response);

            if (response.mensagem || response.id) {
                toast.success(response.mensagem || 'Infraestrutura registada com sucesso!');
                reset();
                setSelectedNome('');
                onClose();
                if (onRegistrarSuccess) {
                    onRegistrarSuccess();
                }
            } else {
                toast.error(response.mensagem || 'Erro ao registar infraestrutura');
            }
        } catch (error: any) {
            console.error('Erro detalhado ao registar:', error);
            
            let errorMessage = 'Erro ao registar infraestrutura. Tenta novamente.';
            
            if (error.message) {
                errorMessage = error.message;
            } else if (error.response?.data?.mensagem) {
                errorMessage = error.response.data.mensagem;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }
            
            toast.error(errorMessage);
        } finally {
            setRegistrando(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Registar Infraestrutura</h2>
                    <button 
                        onClick={onClose} 
                        className="p-1 hover:bg-gray-100 rounded transition"
                    >
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                <form onSubmit={handleSubmitForm} className="space-y-4">
                    {/* Nome - Select */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">
                            Nome
                            {loading && <span className="ml-2 text-xs text-gray-400">Carregando...</span>}
                        </label>
                        <select
                            {...register('nome')}
                            onChange={handleNomeChange}
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500 focus:border-amber-500"
                            disabled={loading || registrando}
                        >
                            <option value="">Selecione uma infraestrutura</option>
                            {infraestruturasDisponiveis.map((infra) => (
                                <option key={infra.nome} value={infra.nome}>
                                    {infra.nome} {!infra.online && "(Offline)"}
                                </option>
                            ))}
                        </select>
                        {errors.nome && <p className="text-xs text-red-500 mt-1">{errors.nome.message}</p>}
                    </div>

                    {/* URL */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">URL</label>
                        <input
                            {...register('url')}
                            type="url"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 bg-gray-50 focus:outline-amber-500 focus:border-amber-500"
                            placeholder="URL será preenchida automaticamente"
                            readOnly
                        />
                        {errors.url && <p className="text-xs text-red-500 mt-1">{errors.url.message}</p>}
                    </div>

                    {/* Capacidade - SEMPRE VISÍVEL */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Capacidade</label>
                        <input
                            {...register('capacidade')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500 focus:border-amber-500"
                            placeholder="100"
                        />
                        {errors.capacidade && <p className="text-xs text-red-500 mt-1">{errors.capacidade.message}</p>}
                    </div>

                    {/* Bônus de Entrega */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Bónus de Entrega (pts)</label>
                        <input
                            {...register('bonusEntrega')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="5"
                        />
                        {errors.bonusEntrega && <p className="text-xs text-red-500 mt-1">{errors.bonusEntrega.message}</p>}
                    </div>

                    {/* Custo por Post */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Custo por Post</label>
                        <input
                            {...register('custoPost')}
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="10"
                        />
                        {errors.custoPost && <p className="text-xs text-red-500 mt-1">{errors.custoPost.message}</p>}
                    </div>

                    {/* Restrições */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Restrições</label>
                        <textarea
                            {...register('restricoes')}
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500 focus:border-amber-500"
                            placeholder="Ex: Proibido anunciar redes televisivas"
                            rows={3}
                        />
                        {errors.restricoes && <p className="text-xs text-red-500 mt-1">{errors.restricoes.message}</p>}
                    </div>

                    {/* Tipo de Coordenadas - Checkboxes */}
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Tipo de coordenadas</label>
                        <div className="flex gap-6">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    value="GPS"
                                    {...register('tipo_coordenadas')}
                                    className="w-4 h-4 accent-amber-500"
                                    disabled={registrando}
                                />
                                <span className="text-sm font-medium">GPS</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    value="WiFi"
                                    {...register('tipo_coordenadas')}
                                    className="w-4 h-4 accent-amber-500"
                                    disabled={registrando}
                                />
                                <span className="text-sm font-medium">WiFi</span>
                            </label>
                        </div>
                        {errors.tipo_coordenadas && <p className="text-xs text-red-500 mt-1">{errors.tipo_coordenadas.message}</p>}
                    </div>

                    {/* Campos GPS - VISÍVEIS QUANDO GPS ESTÁ SELECIONADO */}
                    {isGPS && (
                        <div className="bg-gray-50 p-4 rounded-lg space-y-3 border border-gray-200">
                            <h3 className="font-semibold text-sm text-gray-700">Coordenadas GPS</h3>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Latitude</label>
                                    <input
                                        {...register('latitude')}
                                        type="number"
                                        step="any"
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="-8.8147"
                                    />
                                    {errors.latitude && <p className="text-xs text-red-500 mt-1">{errors.latitude.message}</p>}
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Longitude</label>
                                    <input
                                        {...register('longitude')}
                                        type="number"
                                        step="any"
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="13.2302"
                                    />
                                    {errors.longitude && <p className="text-xs text-red-500 mt-1">{errors.longitude.message}</p>}
                                </div>
                                <div className="col-span-2">
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Raio</label>
                                    <input
                                        {...register('raio')}
                                        type="number"
                                        step="any"
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="20"
                                    />
                                    {errors.raio && <p className="text-xs text-red-500 mt-1">{errors.raio.message}</p>}
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Campos WiFi - VISÍVEIS QUANDO WIFI ESTÁ SELECIONADO */}
                    {isWiFi && (
                        <div className="bg-gray-50 p-4 rounded-lg space-y-3 border border-gray-200">
                            <h3 className="font-semibold text-sm text-gray-700">Dados WiFi</h3>
                            <div className="grid grid-cols-2 gap-3">
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">SSID</label>
                                    <input
                                        {...register('ssid')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="SSID da rede"
                                    />
                                    {errors.ssid && <p className="text-xs text-red-500 mt-1">{errors.ssid.message}</p>}
                                </div>
                                <div>
                                    <label className="block text-xs font-medium text-gray-600 mb-1">Nome</label>
                                    <input
                                        {...register('nome_wifi')}
                                        className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                                        placeholder="Nome da rede"
                                    />
                                    {errors.nome_wifi && <p className="text-xs text-red-500 mt-1">{errors.nome_wifi.message}</p>}
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Botões */}
                    <div className="flex gap-3 pt-4 border-t border-gray-200">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-4 py-2 rounded-lg border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 transition"
                            disabled={registrando}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={registrando || loading}
                            className="flex-1 px-4 py-2 rounded-lg bg-amber-500 text-white font-semibold hover:bg-amber-600 transition disabled:opacity-50"
                        >
                            {registrando ? 'Registrando...' : 'Registar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}