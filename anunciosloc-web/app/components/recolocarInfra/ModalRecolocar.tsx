// app/components/recolocarInfra/ModalRecolocar.tsx
import { infraestruturaService } from '@/services/infraestruturaService';
import toast from 'react-hot-toast';
import { useState, useEffect } from 'react';

export function ModalRecolocar({
    isOpen,
    onClose,
    register,
    errors,
    isSubmitting,
    infra,
    onRecolocarSuccess,
    setValue // ← ADICIONE setValue
}: any) {
    const [recolocando, setRecolocando] = useState(false);

    // Quando o modal abrir e tiver infra, preenche os campos
    useEffect(() => {
        if (isOpen && infra) {
            console.log('Preenchendo formulário com dados de:', infra);
            
            // Preenche os campos com os valores atuais
            setValue('latitude', infra.latitude?.toString() || '');
            setValue('longitude', infra.longitude?.toString() || '');
            setValue('raio', infra.raio?.toString() || '');
        }
    }, [isOpen, infra, setValue]);

    if (!isOpen) return null;

    const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        console.log('=== INICIANDO RECOLOCAÇÃO ===');
        
        const formData = new FormData(e.currentTarget);
        const data = {
            latitude: parseFloat(formData.get('latitude') as string) || 0,
            longitude: parseFloat(formData.get('longitude') as string) || 0,
            raio: parseFloat(formData.get('raio') as string) || 0,
        };

        console.log('Novos dados do formulário:', data);

        if (data.latitude < -90 || data.latitude > 90) {
            toast.error('Latitude deve estar entre -90 e 90');
            return;
        }

        if (data.longitude < -180 || data.longitude > 180) {
            toast.error('Longitude deve estar entre -180 e 180');
            return;
        }

        if (data.raio < 1) {
            toast.error('Raio deve ser pelo menos 1 metro');
            return;
        }

        try {
            setRecolocando(true);

            if (!infra || !infra.id) {
                toast.error('ID da infraestrutura não encontrado');
                return;
            }

            const response = await infraestruturaService.recolocarInfraestrutura(
                infra.id,
                {
                    latitude: data.latitude,
                    longitude: data.longitude,
                    raio: data.raio,
                }
            );

            console.log('Resposta da API:', response);

            if (response?.id || response?.mensagem || response?.message) {
                toast.success(response.mensagem || response.message || 'Infraestrutura recolocada com sucesso!');
                onClose();
                if (onRecolocarSuccess) {
                    onRecolocarSuccess();
                }
            } else {
                toast.error(response?.mensagem || response?.message || 'Erro ao recolocar');
            }
        } catch (error: any) {
            console.error('Erro detalhado:', error);
            let errorMessage = 'Erro ao recolocar infraestrutura.';
            if (error.response?.status === 401) {
                errorMessage = 'Sessão expirada. Faça login novamente.';
            } else if (error.response?.data?.mensagem) {
                errorMessage = error.response.data.mensagem;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }
            toast.error(errorMessage);
        } finally {
            setRecolocando(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Recolocar Infraestrutura</h2>
                    <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded transition">
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                {infra && (
                    <div className="text-sm text-gray-500 mb-4">
                        <p>Recolocando: <span className="font-semibold text-gray-700">{infra.infraestrutura}</span></p>
                        <p className="text-xs text-gray-400 mt-1">ID: {infra.id}</p>
                        <p className="text-xs text-gray-400">Coordenadas atuais: [{infra.coordenada?.latitude || 'N/A'}, {infra.coordenada?.longitude || 'N/A'}, {infra.coordenada?.raio || 'N/A'}]</p>
                    </div>
                )}

                <form onSubmit={handleSubmitForm} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Latitude</label>
                        <input
                            {...register('latitude')}
                            name="latitude"
                            type="number"
                            step="any"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="-8.8147"
                            disabled={recolocando || isSubmitting}
                        />
                        {errors.latitude && <p className="text-xs text-red-500 mt-1">{errors.latitude.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Longitude</label>
                        <input
                            {...register('longitude')}
                            name="longitude"
                            type="number"
                            step="any"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="13.2302"
                            disabled={recolocando || isSubmitting}
                        />
                        {errors.longitude && <p className="text-xs text-red-500 mt-1">{errors.longitude.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Raio</label>
                        <input
                            {...register('raio')}
                            name="raio"
                            type="number"
                            step="0.01"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="20.5"
                            disabled={recolocando || isSubmitting}
                        />
                        {errors.raio && <p className="text-xs text-red-500 mt-1">{errors.raio.message}</p>}
                    </div>

                    <div className="flex gap-3 pt-4 border-t border-gray-200">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-4 py-2 rounded-lg border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 transition"
                            disabled={recolocando}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={recolocando || isSubmitting}
                            className="flex-1 px-4 py-2 rounded-lg bg-green-500 text-white font-semibold hover:bg-green-600 transition disabled:opacity-50"
                        >
                            {recolocando ? 'Recolocando...' : 'Recolocar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}