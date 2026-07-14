// app/components/redimensionarInfra/ModalRedimensionar.tsx
import { infraestruturaService } from '@/services/infraestruturaService';
import toast from 'react-hot-toast';
import { useState } from 'react';

// Modal de Redimensionar
export function ModalRedimensionar({
    isOpen,
    onClose,
    onSubmit,
    register,
    errors,
    isSubmitting,
    infra,
    onRedimensionarSuccess // Callback para atualizar a lista
}: any) {
    const [redimensionando, setRedimensionando] = useState(false);

    if (!isOpen) return null;

    // Handler personalizado para o submit
    const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        console.log('=== INICIANDO REDIMENSIONAMENTO ===');
        
        // Pega os dados do formulário
        const formData = new FormData(e.currentTarget);
        const data = {
            capacidade: parseInt(formData.get('capacidade') as string) || 0,
            bonusEntrega: parseInt(formData.get('bonus_entrega') as string) || 0,
            custoPost: parseInt(formData.get('custo_post') as string) || 0,
            raio: parseFloat(formData.get('raio') as string) || 0,
        };

        console.log('Dados do formulário:', data);

        // Validação básica
        if (data.capacidade < 1) {
            toast.error('Capacidade deve ser pelo menos 1');
            return;
        }

        if (data.bonusEntrega < 0) {
            toast.error('Bónus de entrega não pode ser negativo');
            return;
        }

        if (data.custoPost < 0) {
            toast.error('Custo de post não pode ser negativo');
            return;
        }

        if (data.raio < 1) {
            toast.error('Raio deve ser pelo menos 1 metro');
            return;
        }

        try {
            setRedimensionando(true);

            // Verifica se tem o ID da infraestrutura
            if (!infra || !infra.id) {
                toast.error('ID da infraestrutura não encontrado');
                return;
            }

            console.log('ID da infraestrutura:', infra.id);

            // Chama a API para redimensionar
            const response = await infraestruturaService.redimensionarInfraestrutura(
                infra.id,
                {
                    capacidade: data.capacidade,
                    bonusEntrega: data.bonusEntrega,
                    custoPost: data.custoPost,
                    raio: data.raio,
                }
            );

            console.log('Resposta da API:', response);

            if (response.success || response.mensagem || response.message) {
                toast.success(response.mensagem || response.message || 'Infraestrutura redimensionada com sucesso!');
                
                // Fecha o modal
                onClose();
                
                // Callback para atualizar a lista no componente pai
                if (onRedimensionarSuccess) {
                    onRedimensionarSuccess();
                }
            } else {
                toast.error(response.mensagem || response.message || 'Erro ao redimensionar infraestrutura');
            }
        } catch (error: any) {
            console.error('Erro detalhado ao redimensionar:', error);
            
            let errorMessage = 'Erro ao redimensionar infraestrutura. Tenta novamente.';
            
            if (error.message) {
                errorMessage = error.message;
            } else if (error.response?.data?.mensagem) {
                errorMessage = error.response.data.mensagem;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }
            
            toast.error(errorMessage);
        } finally {
            setRedimensionando(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold">Redimensionar</h2>
                    <button 
                        onClick={onClose} 
                        className="p-1 hover:bg-gray-100 rounded transition"
                    >
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                {infra && (
                    <p className="text-sm text-gray-500 mb-4">
                        Redimensionando: <span className="font-semibold text-gray-700">{infra.infraestrutura}</span>
                    </p>
                )}

                <form onSubmit={onSubmit || handleSubmitForm} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Capacidade</label>
                        <input
                            {...register('capacidade')}
                            name="capacidade"
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="100"
                            disabled={redimensionando}
                        />
                        {errors.capacidade && <p className="text-xs text-red-500 mt-1">{errors.capacidade.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Bônus Entrega</label>
                        <input
                            {...register('bonus_entrega')}
                            name="bonus_entrega"
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="10"
                            disabled={redimensionando}
                        />
                        {errors.bonus_entrega && <p className="text-xs text-red-500 mt-1">{errors.bonus_entrega.message}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Custo por Post</label>
                        <input
                            {...register('custo_post')}
                            name="custo_post"
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="5"
                            disabled={redimensionando}
                        />
                        {errors.custo_post && <p className="text-xs text-red-500 mt-1">{errors.custo_post.message}</p>}
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
                            disabled={redimensionando}
                        />
                        {errors.raio && <p className="text-xs text-red-500 mt-1">{errors.raio.message}</p>}
                    </div>

                    <div className="flex gap-3 pt-4 border-t border-gray-200">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-4 py-2 rounded-lg border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 transition"
                            disabled={redimensionando}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting || redimensionando}
                            className="flex-1 px-4 py-2 rounded-lg bg-blue-500 text-white font-semibold hover:bg-blue-600 transition disabled:opacity-50"
                        >
                            {redimensionando ? 'Redimensionando...' : 'Redimensionar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}