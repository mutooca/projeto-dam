// app/components/redimensionarInfra/ModalRedimensionar.tsx
import { infraestruturaService } from '@/services/infraestruturaService';
import toast from 'react-hot-toast';
import { useState, useEffect } from 'react';

export function ModalRedimensionar({
    isOpen,
    onClose,
    register,
    errors,
    isSubmitting,
    infra,
    onRedimensionarSuccess,
    setValue // ← ADICIONE setValue para preencher o formulário
}: any) {
    const [redimensionando, setRedimensionando] = useState(false);

    // Quando o modal abrir e tiver infra, preenche os campos
    useEffect(() => {
        if (isOpen && infra) {
            console.log('Preenchendo formulário com dados de:', infra);
            
            // Preenche os campos com os valores atuais
            setValue('capacidade', infra.capacidade?.toString() || '');
            setValue('bonus_entrega', infra.bonusEntrega?.toString() || '');
            setValue('custo_post', infra.custoPost?.toString() || '');
            setValue('raio', infra.raio?.toString() || '');
        }
    }, [isOpen, infra, setValue]);

    if (!isOpen) return null;

    const handleSubmitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        console.log('=== INICIANDO REDIMENSIONAMENTO ===');
        
        const formData = new FormData(e.currentTarget);
        const data = {
            capacidade: parseInt(formData.get('capacidade') as string) || 0,
            bonusEntrega: parseInt(formData.get('bonus_entrega') as string) || 0,
            custoPost: parseInt(formData.get('custo_post') as string) || 0,
            raio: parseFloat(formData.get('raio') as string) || 0,
        };

        console.log('Novos dados do formulário:', data);

        // Validações
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

            if (!infra || !infra.id) {
                toast.error('ID da infraestrutura não encontrado');
                return;
            }

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

            if (response?.id || response?.mensagem || response?.message) {
                toast.success(response.mensagem || response.message || 'Infraestrutura redimensionada com sucesso!');
                onClose();
                if (onRedimensionarSuccess) {
                    onRedimensionarSuccess();
                }
            } else {
                toast.error(response?.mensagem || response?.message || 'Erro ao redimensionar');
            }
        } catch (error: any) {
            console.error('Erro detalhado:', error);
            let errorMessage = 'Erro ao redimensionar infraestrutura.';
            if (error.response?.status === 401) {
                errorMessage = 'Sessão expirada. Faça login novamente.';
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
                    <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded transition">
                        <span className="text-2xl">&times;</span>
                    </button>
                </div>

                {infra && (
                    <div className="text-sm text-gray-500 mb-4">
                        <p>Redimensionando: <span className="font-semibold text-gray-700">{infra.infraestrutura}</span></p>
                        <p className="text-xs text-gray-400 mt-1">ID: {infra.id}</p>
                        <p className="text-xs text-gray-400">Valores atuais: Capacidade {infra.capacidade}, Bónus {infra.bonusEntrega}pts, Raio {infra.raio}m</p>
                    </div>
                )}

                <form onSubmit={handleSubmitForm} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Capacidade</label>
                        <input
                            {...register('capacidade')}
                            name="capacidade"
                            type="number"
                            className="w-full px-4 py-2 rounded-lg border border-gray-300 focus:outline-amber-500"
                            placeholder="100"
                            disabled={redimensionando || isSubmitting}
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
                            disabled={redimensionando || isSubmitting}
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
                            disabled={redimensionando || isSubmitting}
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
                            disabled={redimensionando || isSubmitting}
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
                            disabled={redimensionando || isSubmitting}
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