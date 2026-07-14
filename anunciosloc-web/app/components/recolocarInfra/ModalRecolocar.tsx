// app/components/recolocarInfra/ModalRecolocar.tsx
import { infraestruturaService } from '@/services/infraestruturaService';
import toast from 'react-hot-toast';
import { useState } from 'react';

// Modal de Recolocar
export function ModalRecolocar({
    isOpen,
    onClose,
    onSubmit,
    register,
    errors,
    isSubmitting,
    infra,
    onRecolocarSuccess // Callback para atualizar a lista
}: any) {
    const [recolocando, setRecolocando] = useState(false);

    if (!isOpen) return null;

    // Handler personalizado para o submit
    const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        console.log('=== INICIANDO RECOLOCAÇÃO ===');
        
        // Pega os dados do formulário
        const formData = new FormData(e.currentTarget);
        const data = {
            latitude: parseFloat(formData.get('latitude') as string) || 0,
            longitude: parseFloat(formData.get('longitude') as string) || 0,
            raio: parseFloat(formData.get('raio') as string) || 0,
        };

        console.log('Dados do formulário:', data);

        // Validação básica
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

            // Verifica se tem o ID da infraestrutura
            if (!infra || !infra.id) {
                toast.error('ID da infraestrutura não encontrado');
                return;
            }

            console.log('ID da infraestrutura:', infra.id);

            // Chama a API para recolocar
            const response = await infraestruturaService.recolocarInfraestrutura(
                infra.id,
                {
                    latitude: data.latitude,
                    longitude: data.longitude,
                    raio: data.raio,
                }
            );

            console.log('Resposta da API:', response);

            if (response.success || response.mensagem || response.message) {
                toast.success(response.mensagem || response.message || 'Infraestrutura recolocada com sucesso!');
                
                // Fecha o modal
                onClose();
                
                // Callback para atualizar a lista no componente pai
                if (onRecolocarSuccess) {
                    onRecolocarSuccess();
                }
            } else {
                toast.error(response.mensagem || response.message || 'Erro ao recolocar infraestrutura');
            }
        } catch (error: any) {
            console.error('Erro detalhado ao recolocar:', error);
            
            let errorMessage = 'Erro ao recolocar infraestrutura. Tenta novamente.';
            
            if (error.message) {
                errorMessage = error.message;
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
                    <button 
                        onClick={onClose} 
                        className="p-1 hover:bg-gray-100 rounded transition"
                    >
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                {infra && (
                    <p className="text-sm text-gray-500 mb-4">
                        Recolocando: <span className="font-semibold text-gray-700">{infra.infraestrutura}</span>
                    </p>
                )}

                <form onSubmit={onSubmit || handleSubmitForm} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Latitude</label>
                        <input
                            {...register('latitude')}
                            name="latitude"
                            type="number"
                            step="any"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="-8.8147"
                            disabled={recolocando}
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
                            disabled={recolocando}
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
                            disabled={recolocando}
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
                            disabled={isSubmitting || recolocando}
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